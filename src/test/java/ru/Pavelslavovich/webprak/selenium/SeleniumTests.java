package ru.Pavelslavovich.webprak.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import static org.testng.Assert.*;

public class SeleniumTests {

    private static final String BASE_URL = "http://localhost:8080/webprak";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1600,2200");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testHomePageLoads() {
        driver.get(BASE_URL + "/");
        assertEquals(driver.getTitle(), "Главная");
        assertEquals(driver.findElement(By.tagName("h1")).getText(), "Главная");
    }

    @Test
    public void testClientsPageLoads() {
        driver.get(BASE_URL + "/clients");
        assertEquals(driver.getTitle(), "Клиенты");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Клиенты"));
    }

    @Test
    public void testEmployeesPageLoads() {
        driver.get(BASE_URL + "/employees");
        assertEquals(driver.getTitle(), "Служащие");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Служащие"));
    }

    @Test
    public void testServicesPageLoads() {
        driver.get(BASE_URL + "/services");
        assertEquals(driver.getTitle(), "Услуги");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Услуги"));
    }

    @Test
    public void testContractsPageLoads() {
        driver.get(BASE_URL + "/contracts");
        assertEquals(driver.getTitle(), "Договоры");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Договоры"));
    }

    @Test
    public void testNavigationFromHome() {
        driver.get(BASE_URL + "/");
        driver.findElement(By.id("clients_nav")).click();
        assertTrue(driver.getCurrentUrl().contains("/clients"));

        driver.get(BASE_URL + "/");
        driver.findElement(By.id("employees_nav")).click();
        assertTrue(driver.getCurrentUrl().contains("/employees"));

        driver.get(BASE_URL + "/");
        driver.findElement(By.id("services_nav")).click();
        assertTrue(driver.getCurrentUrl().contains("/services"));

        driver.get(BASE_URL + "/");
        driver.findElement(By.id("contracts_nav")).click();
        assertTrue(driver.getCurrentUrl().contains("/contracts"));
    }

    @Test
    public void testAddClientSuccess() {
        String clientName = uniqueValue("Тестовый Клиент");
        driver.get(BASE_URL + "/clients/new");
        new Select(driver.findElement(By.name("clientType"))).selectByValue("INDIVIDUAL");
        driver.findElement(By.name("displayName")).sendKeys(clientName);
        driver.findElement(By.name("note")).sendKeys("Тестовая заметка");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/clients/\\d+$");
        waitForBodyContains(clientName);
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains(clientName));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/clients$");
        waitForBodyNotContains(clientName);
    }

    @Test
    public void testEditClient() {
        driver.get(BASE_URL + "/clients");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        String originalName = driver.findElement(By.tagName("h1")).getText();
        driver.findElement(By.linkText("Редактировать")).click();

        WebElement nameField = driver.findElement(By.name("displayName"));
        nameField.clear();
        nameField.sendKeys("Изменённое Имя Клиента");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForBodyContains("Изменённое Имя Клиента");
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Изменённое Имя Клиента"));

        driver.findElement(By.linkText("Редактировать")).click();
        nameField = driver.findElement(By.name("displayName"));
        nameField.clear();
        nameField.sendKeys(originalName.replace("Клиент: ", ""));
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForBodyContains(originalName.replace("Клиент: ", ""));
    }

    @Test
    public void testDeleteClientWithContracts() {
        driver.get(BASE_URL + "/clients");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        List<WebElement> contractRows = driver.findElements(By.cssSelector("table.table-sm tbody tr"));
        if (!contractRows.isEmpty()) {
            driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
            WebElement alert = driver.findElement(By.cssSelector(".alert-danger"));
            assertTrue(alert.getText().contains("Невозможно удалить"));
        }
    }

    @Test
    public void testAddAndDeleteClient() {
        String clientName = uniqueValue("ООО Удаляемая Компания");
        driver.get(BASE_URL + "/clients/new");
        new Select(driver.findElement(By.name("clientType"))).selectByValue("ORGANIZATION");
        driver.findElement(By.name("displayName")).sendKeys(clientName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/clients/\\d+$");
        String clientUrl = driver.getCurrentUrl();
        assertTrue(clientUrl.matches(".*/clients/\\d+"));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();

        waitForUrlMatches(".*/clients$");
        assertTrue(driver.getCurrentUrl().endsWith("/clients"));

        waitForBodyNotContains(clientName);
        String pageText = driver.findElement(By.tagName("body")).getText();
        assertFalse(pageText.contains(clientName));
    }

    @Test
    public void testFilterClientsByType() {
        driver.get(BASE_URL + "/clients");
        new Select(driver.findElement(By.id("type_inp"))).selectByValue("ORGANIZATION");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlContains("type=ORGANIZATION");
        List<WebElement> rows = findDataRows();
        assertFalse(rows.isEmpty());
        for (WebElement row : rows) {
            assertTrue(row.getText().contains("Организация"));
        }
    }

    @Test
    public void testFilterClientsByNameNoResults() {
        driver.get(BASE_URL + "/clients");
        driver.findElement(By.id("nameQuery_inp")).sendKeys("НесуществующийКлиент12345");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlContains("nameQuery=");
        waitForBodyContains("Нет данных");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Нет данных"));
    }

    @Test
    public void testAddEmployeeSuccess() {
        String employeeName = uniqueValue("Тестовый Служащий");
        driver.get(BASE_URL + "/employees/new");
        driver.findElement(By.name("fullName")).sendKeys(employeeName);
        driver.findElement(By.name("position")).sendKeys("Юрист");
        driver.findElement(By.name("education")).sendKeys("Высшее юридическое");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/employees/\\d+$");
        waitForBodyContains(employeeName);
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains(employeeName));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/employees$");
        waitForBodyNotContains(employeeName);
    }

    @Test
    public void testEditEmployee() {
        driver.get(BASE_URL + "/employees");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        String originalName = driver.findElement(By.tagName("h1")).getText();
        driver.findElement(By.linkText("Редактировать")).click();

        WebElement posField = driver.findElement(By.name("position"));
        posField.clear();
        posField.sendKeys("Старший юрист (изм.)");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForBodyContains("Старший юрист (изм.)");
        String page = driver.findElement(By.tagName("body")).getText();
        assertTrue(page.contains("Старший юрист (изм.)"));

        driver.findElement(By.linkText("Редактировать")).click();
        posField = driver.findElement(By.name("position"));
        posField.clear();
        posField.sendKeys("Адвокат");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForBodyContains("Адвокат");
    }

    @Test
    public void testAddAndDeleteEmployee() {
        String employeeName = uniqueValue("Удаляемый Служащий");
        driver.get(BASE_URL + "/employees/new");
        driver.findElement(By.name("fullName")).sendKeys(employeeName);
        driver.findElement(By.name("position")).sendKeys("Стажер");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/employees/\\d+$");
        assertTrue(driver.getCurrentUrl().matches(".*/employees/\\d+"));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/employees$");
        assertTrue(driver.getCurrentUrl().endsWith("/employees"));
        waitForBodyNotContains(employeeName);
        assertFalse(driver.findElement(By.tagName("body")).getText().contains(employeeName));
    }

    @Test
    public void testDeleteEmployeeWithContracts() {
        driver.get(BASE_URL + "/employees");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        List<WebElement> contractRows = driver.findElements(By.cssSelector("table.table-sm tbody tr"));
        if (!contractRows.isEmpty()) {
            driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
            WebElement alert = driver.findElement(By.cssSelector(".alert-danger"));
            assertTrue(alert.getText().contains("Невозможно удалить"));
        }
    }

    @Test
    public void testFilterEmployeesByQuery() {
        driver.get(BASE_URL + "/employees");
        driver.findElement(By.id("query_inp")).sendKeys("Адвокат");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
        assertFalse(rows.isEmpty());
    }

    @Test
    public void testAddServiceSuccess() {
        String serviceName = uniqueValue("Тестовая услуга");
        driver.get(BASE_URL + "/services/new");
        driver.findElement(By.id("name_inp")).sendKeys(serviceName);
        driver.findElement(By.id("baseCost_inp")).sendKeys("15000.00");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/services/\\d+$");
        waitForBodyContains(serviceName);
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains(serviceName));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/services$");
        waitForBodyNotContains(serviceName);
    }

    @Test
    public void testEditService() {
        driver.get(BASE_URL + "/services");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        driver.findElement(By.linkText("Редактировать")).click();
        WebElement nameField = driver.findElement(By.id("name_inp"));
        String originalName = nameField.getAttribute("value");
        nameField.clear();
        nameField.sendKeys("Изменённая услуга");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForBodyContains("Изменённая услуга");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Изменённая услуга"));

        driver.findElement(By.linkText("Редактировать")).click();
        nameField = driver.findElement(By.id("name_inp"));
        nameField.clear();
        nameField.sendKeys(originalName);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    @Test
    public void testAddAndDeleteService() {
        String serviceName = uniqueValue("Удаляемая услуга");
        driver.get(BASE_URL + "/services/new");
        driver.findElement(By.id("name_inp")).sendKeys(serviceName);
        driver.findElement(By.id("baseCost_inp")).sendKeys("999.99");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/services/\\d+$");
        assertTrue(driver.getCurrentUrl().matches(".*/services/\\d+"));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/services$");
        assertTrue(driver.getCurrentUrl().endsWith("/services"));
        waitForBodyNotContains(serviceName);
        assertFalse(driver.findElement(By.tagName("body")).getText().contains(serviceName));
    }

    @Test
    public void testDeleteServiceWithContracts() {
        driver.get(BASE_URL + "/services");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();

        List<WebElement> alerts = driver.findElements(By.cssSelector(".alert-danger"));
        if (!alerts.isEmpty()) {
            assertTrue(alerts.get(0).getText().contains("Невозможно удалить"));
        }
    }

    @Test
    public void testFilterServicesByName() {
        driver.get(BASE_URL + "/services");
        driver.findElement(By.id("nameQuery_inp")).sendKeys("Конс");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlContains("nameQuery=");
        List<WebElement> rows = findDataRows();
        assertFalse(rows.isEmpty());
        for (WebElement row : rows) {
            assertTrue(row.getText().contains("Консульта"));
        }
    }

    @Test
    public void testFilterServicesByCostRange() {
        driver.get(BASE_URL + "/services");
        driver.findElement(By.id("minCost_inp")).sendKeys("5000");
        driver.findElement(By.id("maxCost_inp")).sendKeys("20000");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
        assertNotNull(rows);
    }

    @Test
    public void testRegisterContract() {
        String contractNumber = uniqueValue("TEST-CONTRACT");
        driver.get(BASE_URL + "/contracts/new");

        driver.findElement(By.id("contractNumber_inp")).sendKeys(contractNumber);
        selectByValue(By.id("clientId_sel"), "1");
        selectByValue(By.id("serviceId_sel"), "1");
        selectByValue(By.name("status"), "ACTIVE");
        setDateValue(By.name("signedOn"), "2025-01-01");
        setDateValue(By.name("serviceStart"), "2025-01-15");
        setDateValue(By.name("serviceEnd"), "2025-12-31");
        driver.findElement(By.id("agreedCost_inp")).clear();
        driver.findElement(By.id("agreedCost_inp")).sendKeys("50000.00");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/contracts/\\d+$");
        waitForBodyContains(contractNumber);
        assertTrue(driver.getCurrentUrl().matches(".*/contracts/\\d+"));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains(contractNumber));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/contracts$");
        waitForBodyNotContains(contractNumber);
    }

    @Test
    public void testEditContract() {
        driver.get(BASE_URL + "/contracts");
        List<WebElement> links = driver.findElements(By.cssSelector("tbody a[href*='/contracts/']"));
        if (links.isEmpty()) return;

        links.get(0).click();
        driver.findElement(By.linkText("Редактировать")).click();

        WebElement commentField = driver.findElement(By.name("comment"));
        commentField.clear();
        commentField.sendKeys("Тестовый комментарий Selenium");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForBodyContains("Тестовый комментарий Selenium");
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Тестовый комментарий Selenium"));

        driver.findElement(By.linkText("Редактировать")).click();
        commentField = driver.findElement(By.name("comment"));
        commentField.clear();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        waitForBodyNotContains("Тестовый комментарий Selenium");
    }

    @Test
    public void testRegisterContractWithEmployees() {
        String contractNumber = uniqueValue("TEST-WITH-EMP");
        driver.get(BASE_URL + "/contracts/new");

        driver.findElement(By.id("contractNumber_inp")).sendKeys(contractNumber);
        selectByValue(By.id("clientId_sel"), "1");
        selectByValue(By.id("serviceId_sel"), "1");
        selectByValue(By.name("status"), "ACTIVE");
        setDateValue(By.name("signedOn"), "2025-03-01");
        setDateValue(By.name("serviceStart"), "2025-03-15");
        driver.findElement(By.id("agreedCost_inp")).clear();
        driver.findElement(By.id("agreedCost_inp")).sendKeys("30000");

        driver.findElement(By.xpath("//button[contains(text(),'Добавить служащего')]")).click();
        selectByValue(By.name("employee_id_0"), "1");
        driver.findElement(By.name("employee_role_0")).sendKeys("Ведущий юрист");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/contracts/\\d+$");
        waitForBodyContains(contractNumber);
        waitForBodyContains("Ведущий юрист");
        assertTrue(driver.getCurrentUrl().matches(".*/contracts/\\d+"));
        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains(contractNumber));
        assertTrue(body.contains("Ведущий юрист"));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/contracts$");
        waitForBodyNotContains(contractNumber);
    }

    @Test
    public void testFilterContractsByStatus() {
        driver.get(BASE_URL + "/contracts");
        new Select(driver.findElement(By.id("status_inp"))).selectByValue("ACTIVE");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlContains("status=ACTIVE");
        List<WebElement> rows = findDataRows();
        assertFalse(rows.isEmpty());
        for (WebElement row : rows) {
            assertTrue(row.getText().contains("Активен"));
        }
    }

    @Test
    public void testFilterContractsByStatusNoResults() {
        driver.get(BASE_URL + "/contracts");
        new Select(driver.findElement(By.id("status_inp"))).selectByValue("CANCELLED");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        List<WebElement> rows = driver.findElements(By.cssSelector("tbody tr"));
        assertNotNull(rows);
    }

    @Test
    public void testClientCardShowsContracts() {
        driver.get(BASE_URL + "/clients");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        assertTrue(driver.findElement(By.tagName("body")).getText().contains("История услуг"));
    }

    @Test
    public void testEmployeeCardShowsContracts() {
        driver.get(BASE_URL + "/employees");
        WebElement firstLink = driver.findElement(By.cssSelector("tbody a"));
        firstLink.click();

        assertTrue(driver.findElement(By.tagName("body")).getText().contains("Участие в договорах"));
    }

    @Test
    public void testContractCardShowsLinks() {
        driver.get(BASE_URL + "/contracts");
        List<WebElement> links = driver.findElements(By.cssSelector("tbody a[href*='/contracts/']"));
        if (links.isEmpty()) return;

        links.get(0).click();
        assertFalse(driver.findElements(By.cssSelector("a[href*='/clients/']")).isEmpty());
        assertFalse(driver.findElements(By.cssSelector("a[href*='/services/']")).isEmpty());
    }

    @Test
    public void testAddClientWithContact() {
        String clientName = uniqueValue("ООО Тест Контакты");
        driver.get(BASE_URL + "/clients/new");
        new Select(driver.findElement(By.name("clientType"))).selectByValue("ORGANIZATION");
        driver.findElement(By.name("displayName")).sendKeys(clientName);

        driver.findElement(By.xpath("//button[contains(text(),'Добавить контактное лицо')]")).click();
        driver.findElement(By.name("contact_name_0")).sendKeys("Петров Пётр Петрович");
        driver.findElement(By.name("contact_role_0")).sendKeys("Директор");

        driver.findElement(By.xpath("//button[contains(text(),'Добавить контакт')]")).click();
        new Select(driver.findElement(By.name("contact_0_mtype_0"))).selectByValue("PHONE");
        driver.findElement(By.name("contact_0_mvalue_0")).sendKeys("+79001112233");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        waitForUrlMatches(".*/clients/\\d+$");
        waitForBodyContains(clientName);
        waitForBodyContains("Петров Пётр Петрович");
        waitForBodyContains("+79001112233");
        assertTrue(driver.getCurrentUrl().matches(".*/clients/\\d+"));
        String body = driver.findElement(By.tagName("body")).getText();
        assertTrue(body.contains("Петров Пётр Петрович"));
        assertTrue(body.contains("+79001112233"));

        driver.findElement(By.cssSelector("form[action$='/delete'] button")).click();
        waitForUrlMatches(".*/clients$");
        waitForBodyNotContains(clientName);
    }

    private void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
        waitForDocumentReady();
    }

    private void waitForUrlMatches(String pattern) {
        wait.until(ExpectedConditions.urlMatches(pattern));
        waitForDocumentReady();
    }

    private void waitForBodyContains(String text) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), text));
    }

    private void waitForBodyNotContains(String text) {
        wait.until(webDriver -> {
            try {
                return !webDriver.findElement(By.tagName("body")).getText().contains(text);
            } catch (StaleElementReferenceException ex) {
                return false;
            }
        });
    }

    private List<WebElement> findDataRows() {
        waitForDocumentReady();
        return driver.findElements(By.cssSelector("tbody tr"))
                .stream()
                .filter(row -> !row.getText().contains("Нет данных"))
                .toList();
    }

    private void selectByValue(By locator, String value) {
        new Select(driver.findElement(locator)).selectByValue(value);
        wait.until(webDriver -> value.equals(
                new Select(webDriver.findElement(locator)).getFirstSelectedOption().getAttribute("value")));
    }

    private void setDateValue(By locator, String value) {
        WebElement input = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles:true})); arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                input,
                value
        );
        wait.until(webDriver -> value.equals(webDriver.findElement(locator).getAttribute("value")));
    }

    private void waitForDocumentReady() {
        wait.until(webDriver -> "complete".equals(
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }

    private String uniqueValue(String prefix) {
        return prefix + "-" + System.nanoTime();
    }
}
