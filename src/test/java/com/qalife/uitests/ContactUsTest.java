package com.qalife.uitests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * UI Test for Contact Form
 *
 * Registers a user, logs in, then fills and submits the contact form on the dashboard.
 */
public class ContactUsTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "https://qalife-production.up.railway.app";

    @BeforeMethod
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        if (System.getenv("CI") != null) {
            options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSubmitContactForm() {
        String email = "contact" + System.currentTimeMillis() + "@example.com";
        String password = "Test@123";

        // Step 1: Register
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("Test User");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-register")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message")));

        // Step 2: Login
        driver.get(baseUrl + "/login");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-login")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-message")));

        // Step 3: Fill contact form on the dashboard
        driver.findElement(By.cssSelector("#contact-name")).sendKeys("Test User");
        driver.findElement(By.cssSelector("#contact-email")).sendKeys("test@example.com");
        driver.findElement(By.cssSelector("#contact-message")).sendKeys("This is a test message");
        driver.findElement(By.cssSelector("#btn-submit-contact")).click();

        // Step 4: Verify success message
        WebElement messageElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message"))
        );
        String message = messageElement.getText().toLowerCase();
        Assert.assertTrue(message.contains("sent") || message.contains("received"),
                "Expected success message but got: " + message);
    }
}
