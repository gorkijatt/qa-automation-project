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
 * UI Tests for Registration Page
 *
 * Opens a real browser, fills in the form, clicks register, checks the result.
 */
public class RegisterTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "https://qalife-production.up.railway.app";

    @BeforeMethod
    public void openBrowser() {
        // Auto-download chromedriver and open Chrome
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        // If running in CI (GitHub Actions), use headless mode (no screen available)
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
    public void testSuccessfulRegistration() {
        // Go to register page
        driver.get(baseUrl + "/register");

        // Fill in the form
        String uniqueEmail = "user" + System.currentTimeMillis() + "@example.com";
        driver.findElement(By.cssSelector("#input-name")).sendKeys("John Doe");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(uniqueEmail);
        driver.findElement(By.cssSelector("#input-password")).sendKeys("Test@123");

        // Click Register button
        driver.findElement(By.cssSelector("#btn-register")).click();

        // Wait for success message and verify
        WebElement messageElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message"))
        );
        String message = messageElement.getText().toLowerCase();
        Assert.assertTrue(message.contains("registration successful") || message.contains("registered"),
                "Expected success message but got: " + message);
    }

    @Test
    public void testDuplicateEmailRegistration() {
        String email = "dup" + System.currentTimeMillis() + "@example.com";

        // Register first time
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("John Doe");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys("Test@123");
        driver.findElement(By.cssSelector("#btn-register")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message")));

        // Register second time with same email
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("John Doe");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys("Test@123");
        driver.findElement(By.cssSelector("#btn-register")).click();

        // Should show error
        WebElement messageElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message"))
        );
        String message = messageElement.getText().toLowerCase();
        Assert.assertTrue(message.contains("already") || message.contains("exists"),
                "Expected duplicate error but got: " + message);
    }
}
