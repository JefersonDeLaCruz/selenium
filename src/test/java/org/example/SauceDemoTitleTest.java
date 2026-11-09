package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Prueba inicial: abre SauceDemo y verifica el título publicado por la página.
 */
public class SauceDemoTitleTest {
    @Test
    public void abrirNavegador() {
        ChromeOptions options = new ChromeOptions();
        // Esta es la instalación de Chromium del sistema, no Google Chrome.
        options.setBinary("/usr/bin/chromium");

        // En IntelliJ o una terminal gráfica se verá el navegador. En entornos sin
        // pantalla (por ejemplo, un servidor) Selenium usa headless automáticamente.
        boolean hasDisplay = System.getenv("DISPLAY") != null || System.getenv("WAYLAND_DISPLAY") != null;
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", Boolean.toString(!hasDisplay)));
        if (headless) {
            options.addArguments("--headless=new");
        }

        // Selenium Manager localiza y descarga el ChromeDriver compatible si falta.
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.saucedemo.com/");
            String title = driver.getTitle();
            System.out.println("Título: " + title);

            Assert.assertEquals(title, "Swag Labs", "El título de SauceDemo no es el esperado.");
        } finally {
            // finally se ejecuta incluso si falla la navegación o la aserción.
            driver.quit();
        }
    }
}
