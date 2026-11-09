package org.example.p2;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ejercicios de la guia selenium + testng sobre saucedemo.
 */
public class GuiaTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.setBinary("/usr/bin/chromium");
        options.addArguments("--start-maximized");

        boolean hasDisplay = System.getenv("DISPLAY") != null || System.getenv("WAYLAND_DISPLAY") != null;
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", Boolean.toString(!hasDisplay)));
        if (headless) {
            options.addArguments("--headless=new");
        }

        driver = new ChromeDriver(options);
        driver.get("https://www.saucedemo.com/");
    }

    //prueba 1: localizar un elemento por id y verificar que sea visible.
    @Test(priority = 1)
    public void loginButtonIsVisible() {
        WebElement loginButton = driver.findElement(By.id("login-button"));

        Assert.assertTrue(loginButton.isDisplayed(), "el boton login deberia estar visible.");
    }

    //prueba 2: ingresar texto con sendkeys en los campos de inicio de sesion.
    @Test(priority = 2, dependsOnMethods = "loginButtonIsVisible")
    public void inputFieldsAcceptText() {
        String username = "standard_user";
        String password = "secret_sauce";

        WebElement usernameInput = driver.findElement(By.id("user-name"));
        WebElement passwordInput = driver.findElement(By.id("password"));
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);

        Assert.assertEquals(usernameInput.getAttribute("value"), username, "el usuario no se escribio.");
        Assert.assertEquals(passwordInput.getAttribute("value"), password, "la contrasena no se escribio.");
    }

    //prueba 3: iniciar sesion y validar el texto principal de la pagina.
    @Test(priority = 3, dependsOnMethods = "inputFieldsAcceptText")
    public void loginShowsProductsTitle() {
        driver.findElement(By.id("login-button")).click();

        String pageTitle = driver.findElement(By.className("title")).getText();
        Assert.assertEquals(pageTitle, "Products", "el titulo despues del login no es el esperado.");
    }

    //prueba 4: agregar un producto, abrir el carrito y validar su nombre.
    @Test(priority = 4, dependsOnMethods = "loginShowsProductsTitle")
    public void addBackpackToCart() {
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        
        driver.findElement(By.className("shopping_cart_link")).click();
        String productName = driver.findElement(By.className("inventory_item_name")).getText();

        Assert.assertEquals(productName, "Sauce Labs Backpack", "el producto del carrito no es el esperado.");
        System.out.println("producto agregado al carrito: " + productName);
    }

    //prueba 5: extraer y validar un atributo del enlace del carrito.
    @Test(priority = 5, dependsOnMethods = "addBackpackToCart")
    public void cartLinkHasExpectedDataTestAttribute() {
        WebElement cartLink = driver.findElement(By.className("shopping_cart_link"));

        String dataTest = cartLink.getAttribute("data-test");
        Assert.assertEquals(dataTest, "shopping-cart-link", "el atributo data-test no es el esperado.");
    }

    //prueba 6: esperar de forma explicita hasta que el indicador del carrito sea visible.
    @Test(priority = 6, dependsOnMethods = "cartLinkHasExpectedDataTestAttribute")
    public void cartBadgeBecomesVisible() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement cartBadge = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge"))
        );

        Assert.assertTrue(cartBadge.isDisplayed(), "el indicador del carrito deberia ser visible.");
    }

    //prueba 7: desplazar la pagina hasta el pie usando javascriptexecutor.
    @Test(priority = 7, dependsOnMethods = "cartBadgeBecomesVisible")
    public void scrollToFooter() {
        driver.findElement(By.id("continue-shopping")).click();
        WebElement footer = driver.findElement(By.tagName("footer"));
        JavascriptExecutor javascript = (JavascriptExecutor) driver;
        javascript.executeScript("arguments[0].scrollIntoView({block: 'center'});", footer);

        Number scrollPosition = (Number) javascript.executeScript("return window.scrollY;");
        Assert.assertTrue(scrollPosition.longValue() > 0, "la pagina deberia haberse desplazado.");
    }

    //prueba 8: abrir el menu lateral y cerrar sesion.
    @Test(priority = 8, dependsOnMethods = "scrollToFooter")
    public void logoutFromSideMenu() {
        driver.findElement(By.id("react-burger-menu-btn")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement logoutLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))
        );
        logoutLink.click();
    }

    //prueba 9: validar la url a la que redirige el cierre de sesion.
    @Test(priority = 9, dependsOnMethods = "logoutFromSideMenu")
    public void logoutRedirectsToLoginPage() {
        String currentUrl = driver.getCurrentUrl();

        Assert.assertEquals(currentUrl, "https://www.saucedemo.com/", "la url despues de logout no es la esperada.");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("se cerro el navegador.");
        }
    }
}
