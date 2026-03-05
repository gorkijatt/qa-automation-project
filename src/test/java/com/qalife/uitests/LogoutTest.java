package com.qalife.uitests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * UI Test for Logout
 *
 * Registers, logs in, then clicks logout and verifies redirect to login page.
 */
public class LogoutTest {

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
    public void testLogoutRedirectsToLogin() {
        String email = "logout" + System.currentTimeMillis() + "@example.com";
        String password = "Test@123";

        // Register
        driver.get(baseUrl + "/register");
        driver.findElement(By.cssSelector("#input-name")).sendKeys("Test User");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-register")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#message")));

        // Login
        driver.get(baseUrl + "/login");
        driver.findElement(By.cssSelector("#input-email")).sendKeys(email);
        driver.findElement(By.cssSelector("#input-password")).sendKeys(password);
        driver.findElement(By.cssSelector("#btn-login")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#welcome-message")));

        // Click Logout
        driver.findElement(By.cssSelector("#btn-logout")).click();

        // Verify we're back on the login page
        wait.until(ExpectedConditions.urlContains("/login"));
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login"),
                "Expected redirect to login page but URL is: " + currentUrl);
    }
}
