import io.github.bonigarcia.wdm.WebDriverManager
import io.github.bonigarcia.wdm.managers.ChromeDriverManager
import io.github.bonigarcia.wdm.managers.FirefoxDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.safari.SafariDriver

environments {

    // You need to configure in Safari -> Develop -> Allowed Remote Automation
    safari {
        WebDriverManager.safaridriver().setup();
        driver = { new SafariDriver() }
    }

    // run via “./gradlew -Dgeb.env=chrome iT”
    chrome {
        WebDriverManager.chromedriver().setup();
        driver = { new ChromeDriver() }
    }

    // run via “./gradlew -Dgeb.env=chromeHeadless iT”
    chromeHeadless {
        WebDriverManager.chromedriver().setup();
        driver = {
            ChromeOptions o = new ChromeOptions()
            o.addArguments('headless')
            new ChromeDriver(o)
        }
    }

    // run via “./gradlew -Dgeb.env=firefoxHeadless iT”
    firefoxHeadless {
        WebDriverManager.firefoxdriver().setup();
        driver = {
            FirefoxOptions o = new FirefoxOptions()
            o.addArguments('-headless')
            new FirefoxDriver(o)
        }
    }

    // run via “./gradlew -Dgeb.env=firefox iT”
    firefox {
        WebDriverManager.firefoxdriver().setup();
        driver = { new FirefoxDriver() }
    }
}