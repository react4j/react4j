import base64, json, socket, subprocess, time, urllib.request
from pathlib import Path
root = Path.cwd()
metadata = root / "bazel-bin/examples/native-utilities/src/test/js/react4j/nativeutilities/browser_tests.gen.json"
config = json.loads(metadata.read_text())
files = config["webTestFiles"][0]["namedFiles"]
runfiles = metadata.parent / "browser_tests.sh.runfiles"
with socket.socket() as sock:
    sock.bind(("127.0.0.1", 0))
    port = sock.getsockname()[1]
process = subprocess.Popen([str((runfiles / files["CHROMEDRIVER"]).resolve()), "--port=" + str(port)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
def request(method, path, body=None):
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request("http://127.0.0.1:" + str(port) + path, data=data, method=method, headers={"Content-Type":"application/json"})
    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            value = json.load(response)["value"]
    except urllib.error.HTTPError as error:
        raise RuntimeError(error.read().decode()) from error
    assert not isinstance(value, dict) or "error" not in value, value
    return value
session = None
try:
    for i in range(100):
        try:
            request("GET", "/status")
            break
        except OSError:
            time.sleep(.05)
    capabilities = {"browserName":"chrome", "goog:chromeOptions":{"binary":str((runfiles / files["CHROMIUM"]).resolve()), "args":["--headless=new"]}}
    session = request("POST", "/session", {"capabilities":{"alwaysMatch":capabilities}})["sessionId"]
    def command(path, body):
        return request("POST", "/session/" + session + path, body)
    def execute(script):
        return command("/execute/sync", {"script":script, "args":[]})
    command("/url", {"url":"http://127.0.0.1:3000/"})
    for i in range(100):
        if execute("return !!document.querySelector('#save')"):
            break
        time.sleep(.05)
    command("/goog/cdp/execute", {"cmd":"Emulation.setEmulatedMedia", "params":{"features":[{"name":"prefers-reduced-motion", "value":"reduce"}]}})
    assert execute("return matchMedia('(prefers-reduced-motion: reduce)').matches")
    execute("document.querySelector('#rules-trigger').click()")
    result = execute("let p=document.querySelector('#rules-panel'); return {duration:getComputedStyle(p).transitionDuration, animations:p.getAnimations().length, open:p.matches(':popover-open')}")
    assert result == {"duration":"0s", "animations":0, "open":True}, result
    print("Reduced motion: actual media preference enabled; open panel has 0s transition and zero animations")
    command("/goog/cdp/execute", {"cmd":"Emulation.setEmulatedMedia", "params":{"features":[]}})
    execute("document.querySelector('#rules-close').click()")
    out = root / "reports/screenshots"
    out.mkdir(parents=True, exist_ok=True)
    for width, name in [(1200,"native-utilities-wide"),(375,"native-utilities-narrow")]:
        command("/goog/cdp/execute", {"cmd":"Emulation.setDeviceMetricsOverride", "params":{"width":width,"height":900,"deviceScaleFactor":1,"mobile":False}})
        time.sleep(.1)
        assert execute("return document.documentElement.scrollWidth <= innerWidth"), "Horizontal overflow at " + str(width)
        image = request("GET", "/session/" + session + "/screenshot")
        (out / (name + ".png")).write_bytes(base64.b64decode(image))
    print("Wide 1200px and narrow 375px screenshots captured; no horizontal overflow")
finally:
    if session:
        request("DELETE", "/session/" + session)
    process.terminate()
    process.wait(timeout=10)
