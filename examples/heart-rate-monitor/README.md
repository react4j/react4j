# Heart Rate Monitor

A Web Bluetooth dashboard built with React4j and Arez.

[Try Heart Rate Monitor](https://react4j.github.io/heart-rate-monitor/), or run it locally
from the repository root:

```sh
tools/examples.sh serve heart-rate-monitor development
```

Open [localhost:3000](http://localhost:3000). See [CONTRIBUTING.md](../../CONTRIBUTING.md)
for build setup and other commands.

Requires a browser with Web Bluetooth, a Bluetooth heart rate sensor, and a secure context
(HTTPS or localhost). If the page is blank, check the browser console for
`Bluetooth API not present`.

[Styles.java](src/main/java/react4j/hrm/Styles.java) defines the component layout, heart size
and heartbeat keyframes. The `beating()` modifier applies the animation while a heart rate is available.
