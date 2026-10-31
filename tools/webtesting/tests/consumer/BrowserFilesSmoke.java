import com.google.devtools.build.runfiles.Runfiles;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

public final class BrowserFilesSmoke {
    public static void main(String[] args) throws IOException {
        var service = new ChromeDriverService.Builder()
                .usingDriverExecutable(executable("test.driver").toFile())
                .usingAnyFreePort()
                .build();
        var options = new ChromeOptions();
        options.setBinary(executable("test.browser").toFile());
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        ChromeDriver driver = null;
        try {
            driver = new ChromeDriver(service, options);
            driver.get("data:text/html,<title>Browser files</title>");
            if (!"Browser files".equals(driver.getTitle())) {
                throw new AssertionError("External consumer could not load the browser test page");
            }
        } finally {
            try {
                if (null != driver) {
                    driver.quit();
                }
            } finally {
                service.stop();
            }
        }
    }

    private static Path executable(String property) throws IOException {
        var rootpath = Objects.requireNonNull(System.getProperty(property));
        var key = rootpath.startsWith("../") ? rootpath.substring(3) : System.getenv("TEST_WORKSPACE") + "/" + rootpath;
        return Path.of(Objects.requireNonNull(Runfiles.create().rlocation(key)));
    }
}
