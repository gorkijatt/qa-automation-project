package com.qalife.uitests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * UI Tests for Login Page
 *
 * First registers a user, then tests login with correct and wrong passwords.
 */
public class LoginTest {

    WebDriver driver;
    WebDriverWait wait;
    String baseUrl = "https://qalife-production.up.railway.app";

    @BeforeMethod
    public void openBrowser() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
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
    public void testSuccessfulLogin() {
        String email = "login" + System.currentTimeMillis() + "@example.com";
        String password = "Test@123";

        // Step 1: Register a new user first
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("Test User");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-register")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message")));

        // Step 2: Go to login page and login
        driver.get(baseUrl + "/login");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-login")).click();

        // Step 3: Verify we land on the dashboard with a welcome message
        WebElement welcomeMessage = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-message"))
        );
        Assert.assertTrue(welcomeMessage.getText().toLowerCase().contains("hello"),
                "Expected welcome message with 'Hello' but got: " + welcomeMessage.getText());
    }

    @Test
    public void testLoginWithWrongPassword() {
        String email = "wrongpass" + System.currentTimeMillis() + "@example.com";

        // Register a user
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("Test User");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys("Test@123");
        driver.findElement(By.cssSelector("#btn-register")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message")));

        // Try to login with wrong password
        driver.get(baseUrl + "/login");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys("WrongPass@999");
        driver.findElement(By.cssSelector("#btn-login")).click();

        // Should show error message
        WebElement messageElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message"))
        );
        String message = messageElement.getText().toLowerCase();
        Assert.assertTrue(message.contains("invalid") || message.contains("wrong"),
                "Expected error message but got: " + message);
    }
}
