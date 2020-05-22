package sample.base;

import static org.testng.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import sample.base.ExtentManager;
import sample.base.LoadProperties;
import sample.utility.testrail.APIClient;
import sample.utility.testrail.APIException;


/**
 * Class with generic methods to be reused by other projects
 * 
 */
public class BaseTest {
	private static LoadProperties prop = new LoadProperties();
	public static final String DEFAULT_URL = prop.getValue("default.url");
	public static final String DEFAULT_ENVIRONMENT = prop.getValue("default.environment");
	public static final String DEFAULT_BROWSER = prop.getValue("default.browser");
	public static final String SCREENSHOT_SUCCESS = prop.getValue("take.screenshot.success");
	public static final String SCREENSHOT_FAIL = prop.getValue("take.screenshot.fail");
	public static final String IEXPLORER = "IEXPLORER";
	public static final String FIREFOX = "FIREFOX";
	public static final String CHROME = "CHROME";
	public static final long WAIT_TIME = Long.parseLong(prop.getValue("wait.time"));
	public static final String SIM = "s";
	public static final String NAO = "n";
	public static final String RESULTS_PATH = prop.getValue("results.path");
	public static final String SCREENSHOTS_WIDTH = prop.getValue("screenshots.width");
	public static final String CHROME_PATH = prop.getValue("path.chrome");
	public static final String CHROME_WEB_DRIVER = prop.getValue("web.driver.chrome");
	public static final String FIREFOX_PATH = prop.getValue("path.firefox");
	public static final String FIREFOX_WEB_DRIVER = prop.getValue("web.driver.firefox");
	public static final String IE_PATH = prop.getValue("path.ie");
	public static final String IE_WEB_DRIVER = prop.getValue("web.driver.ie");
	public static final String SAFARI_PATH = prop.getValue("path.safari");
	public static final String SAFARI_WEB_DRIVER = prop.getValue("web.driver.safari");
	public static String currentTestSuite = "Suite"; 
	public static String currentTest = "Test"; 
	public static String currentTestClass = "Class";
	public static String currentTestMethod = "Method";
	public static String currentBaseUrl = DEFAULT_URL;
	public static String currentBrowser = DEFAULT_BROWSER;
	public static String currentEnvironment = DEFAULT_ENVIRONMENT;
	public static ExtentManager report = new ExtentManager();
	public static StringBuffer verificationErrors;
	protected static WebDriver driver;
	
	
	public static final String TEST_RUN_SUITEID = prop.getValue("test.rail.suite.id");
	public static final String TEST_PROJECT_ID = prop.getValue("test.rail.project.id");
	public static final String TEST_RUNNAME_PREFIX = prop.getValue("test.rail.runname.prefix");
	public static final int TEST_CASE_FAILED_STATUS = 5;
	public static final int TEST_CASE_PASSED_STATUS = 1;
	public static final int TEST_CASE_BLOCKED_STATUS = 2;
	public static final int TEST_CASE_RETESTED_STATUS = 4;
	public static final int TEST_CASE_NA_STATUS = 6;
	public static final String TEST_RAIL_URL = prop.getValue("test.rail.url");
	public static final String TEST_RAIL_USER = prop.getValue("test.rail.username");
	public static final String TEST_RAIL_PASSWORD = prop.getValue("test.rail.password");
	public static long TEST_REAIL_RUN_ID = 0;
	
	//if the properties there some string the test post Result to RestRail, it will be TRUE
	public static Boolean postResultsToTestRail = false;
	public static Boolean debugInfo = true;
	
	
	@Parameters({ "environment" })
	@BeforeSuite
    public void setUpBeforeSuite(String environment) throws IOException, APIException {
		currentEnvironment = environment != null ? environment : DEFAULT_ENVIRONMENT;
		//if want to post Results to Test Rails que the properties should be set with any string
		if(!prop.getValue("test.rail.post.result").isEmpty()) {
			addTestRunInTestRail();
		}
    }
	
	@Parameters({ "url", "browser" })
    @BeforeTest//(alwaysRun = true)
    public void setUpBeforeTest(String url, String browser) {
		System.out.println("URL = " + url + " ++++++++++++++++++++++++++++++ ");
		System.out.println("Browser = " + DEFAULT_BROWSER + " ++++++++++++++++++++++++++++++ ");
		currentBaseUrl = url != "" ? url : DEFAULT_URL;
		currentBrowser = browser != "" ? browser : DEFAULT_BROWSER;        
		openBrowser(currentBrowser);
		driver.manage().timeouts().implicitlyWait(WAIT_TIME, TimeUnit.SECONDS);
		
		//if want to post Results to Test Rails que the properties should be set with any string
		if(!prop.getValue("test.rail.post.result").isEmpty()) {
			postResultsToTestRail = true;
		}
		
		System.out.println("POST Results = " + postResultsToTestRail);
		System.out.println("POST Results2 = " + prop.getValue("test.rail.post.result")); 
    }
 
    @AfterTest(alwaysRun = true)
    public void tearDownAfterTest() {
    	driver.quit();
    }
    
	/**
	 * Opens an instance of the browser, given in the parameter.
	 * <p>
	 * Possible browser values are: CHROME, FIREFOX, IEXPLORER
	 * <p>
	 * For example: if you want all the tests to run on "Chrome", set the
	 * browser parameter to CHROME.
	 * 
	 * @param browser
	 *            the browser that the tests will run
	 */
	public void openBrowser(String browser) {
		if (browser.equalsIgnoreCase(CHROME)) {
			openChromeBrowser();
		} else if (browser.equalsIgnoreCase(IEXPLORER)) {
			openIEBrowser();
		} else {
			openFirefoxBrowser();
		}
	}

	/**
	 * Locates the Google Chrome path and Driver path,
	 * <strong>"path.chrome"</strong> and <strong>"web.driver.chrome"</strong>,
	 * defined in the properties file, and creates an instance of the browser.
	 */
	public void openChromeBrowser() {
		String chromePath = prop.getValue("path.chrome");
		String chromeDriverPath = prop.getValue("web.driver.chrome");

		System.setProperty(chromeDriverPath, chromePath);
		driver = new ChromeDriver();
	}

	/**
	 * Locates the Internet Explorer path and Driver path,
	 * <strong>"path.ie"</strong> and <strong>"web.driver.ie"</strong>, defined
	 * in the properties file, and creates an instance of the browser.
	 */
	public void openIEBrowser() {
		String iEPath = prop.getValue("path.ie");
		String iEDriverPath = prop.getValue("web.driver.ie");

		System.setProperty(iEDriverPath, iEPath);
		driver = new InternetExplorerDriver();
	}

	/**
	 * Locates the Firefox path and Driver path, <strong>"path.firefox"</strong>
	 * and <strong>"web.driver.firefox"</strong>, defined in the properties
	 * file, and creates an instance of the browser.
	 */
	public void openFirefoxBrowser() {
		// works from Firefox 45 onwards
		String firefoxPath = prop.getValue("path.firefox");
		String firefoxDriverPath = prop.getValue("web.driver.firefox");

		System.setProperty(firefoxDriverPath, firefoxPath);
		driver = new FirefoxDriver();

		// FirefoxProfile profile = new FirefoxProfile();
		// profile.setAcceptUntrustedCertificates(true);
		// driver = new FirefoxDriver(profile);

		// FirefoxProfile profile = new FirefoxProfile();
		// profile.setAssumeUntrustedCertificateIssuer(false);
		// driver = new FirefoxDriver(profile);

		// DesiredCapabilities capabilities = new DesiredCapabilities();
		// capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		// driver = new FirefoxDriver(capabilities);
	}

	/**
	 * Locates the Safari path and Driver path, <strong>"path.safari"</strong>
	 * and <strong>"web.driver.safari"</strong>, defined in the properties file,
	 * and creates an instance of the browser.
	 */
	public void openSafariBrowser() {
		String safariPath = prop.getValue("path.safari");
		String safariDriverPath = prop.getValue("web.driver.safari");

		System.setProperty(safariDriverPath, safariPath);
		driver = new SafariDriver();
	}

	/**
	 * Calls {@code capturePage(String folderName, String subFolderName, String
	 * imageMainName, String imageAditionalName)} method, passing the folder,
	 * sub-folder and file name that are retrieved from the current test suite.
	 * OBS.: No additional name will be added to the screenshot name.
	 * 
	 * @return link the link to the screenshot
	 */
	public static String capturePage() {
		return capturePage(currentTestSuite, currentTestClass, currentTestMethod, null);
	}

	/**
	 * Calls {@code capturePage(String folderName, String subFolderName, String
	 * imageMainName, String imageAditionalName)} method, passing the folder,
	 * sub-folder and file name that are retrieved from the current test suite,
	 * and adds the imageAditionalName to the image name
	 * 
	 * @param imageAditionalName
	 *            the additional name that can be added to the image main name
	 * @return link the link to the screenshot
	 */
	public static String capturePage(String imageAditionalName) {
		return capturePage(currentTestSuite, currentTestClass, currentTestMethod, imageAditionalName);
	}

	/**
	 * Captures a screenshot of the current visible page.
	 * <p>
	 * The screenshots will be saved in a folder/sub-folder, that will be
	 * created inside the test results directory.
	 * <p>
	 * The path/link to the saved image will be added to the tests results
	 * Report Log
	 * 
	 * @param folderName
	 *            the folder name where the sub-folder and image will be saved
	 * @param subFolderName
	 *            the sub-folder name where the image will be saved
	 * @param imageMainName
	 *            the desired name for the image
	 * @param imageAditionalName
	 *            the additional name that can be added to the image main name
	 *            (optional - can be null)
	 * @return link the link to the screenshot
	 */
	public static String capturePage(String folderName, String subFolderName, String imageMainName,
			String imageAditionalName) {
		File screenshot = null;
		ScreenImage img;
		String currentImageName;
		
		driver.manage().window().maximize();

		if (imageAditionalName.equals(null)) {
			currentImageName = imageMainName + " (" + getCurrentDate() + ").png";
		} else {
			currentImageName = imageMainName + "-" + imageAditionalName + " (" + getCurrentDate() + ").png";
		}
		
		img = report.createScreenImage(folderName, subFolderName, currentImageName);

		try {
			// Takes a screenshot and creates the file inside the directory
			if (currentBrowser.equals(CHROME) || currentBrowser.equals(FIREFOX)) {
				Screenshot screenshotChrome = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(400))
						.takeScreenshot(driver);
				BufferedImage image = screenshotChrome.getImage();

				screenshot = new File(img.getPath() + img.getName());
				screenshot.getParentFile().mkdirs();

				ImageIO.write(image, "PNG", screenshot);

			} else {
				screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(screenshot, new File(img.getPath() + img.getName()));
			}

			report.logScreenshotToTestRunner(img.getLink());

		} catch (Exception e) {
			report.logToTestRunner("Error capturing screenshot. " + e.getMessage());
			img.setLink("#");
		}
		
		report.addImageToList(img); //add image to the image list

		return img.getLink();
	}
	
	public static void failTestIfVerifyFails() throws AssertionError {		
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			throw new AssertionError(verificationErrorString);
		}
	}
	
	public static void attachError(AssertionError e) {
		verificationErrors.append("<br>" + e);
		report.logError(e.getMessage());
	}
	
	public static void attachErrorIO(Exception e) {
		verificationErrors.append("<br>" + e);
		report.logError(e.getMessage());
	}

	/**
	 * Waits until the given <strong>Text</strong> is present on the page.
	 * 
	 * @param type
	 * 			 the type of parameter that method should be to look. </br>
	 * 			<strong>id, name, class, css, xpath</strong>
	 * @param string
	 *            the text that will be waited to load
	 * @param elementId
	 *            the element id where the text should be present
	 */
	public void waitUntilTextPresentByType(String type, String string, String elementId) {
		WebDriverWait wait = new WebDriverWait(driver, 10000);

		if(type == "id"){
			report.logInfo("Searching By ID text: " + string);
			System.out.println("Searching By ID text: " + string);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id(elementId), string));
			System.out.println("Found element by id: " + string);
		} else if(type == "name") {
			report.logInfo("Searching By NAME text: " + string);
			System.out.println("Searching By NAME text: " + string);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.name(elementId), string));
			System.out.println("Found element by name: " + string);
		} else if(type == "class") {
			report.logInfo("Searching By CLASS text: " + string);
			System.out.println("Searching By CLASS text: " + string);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className(elementId), string));
			System.out.println("Found element by class: " + string);
		} else if(type == "css") {
			report.logInfo("Searching By CSS text: " + string);
			System.out.println("Searching By CSS text: " + string);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(elementId), string));
			System.out.println("Found element by css: " + string);
		} else {
			report.logInfo("Searching By XPATH text: " + string);
			System.out.println("Searching By XPATH text: " + string);
			wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath(elementId), string));
			System.out.println("Found element by xpath: " + string);
		}
	}

	/**
	 * Assert if each "text" (string) passed through the parameter <strong>is
	 * Present</strong> on the page.
	 * <p>
	 * You can pass as many strings as you like, separated by comma. The test
	 * will fail if at least one element isn't found on the Page.
	 * <p>
	 * E.g.: {@code assertTextPresent("String 1", "String 2", "String 3");}
	 * 
	 * @param strings
	 *            the texts that will be validated
	 * @throws AssertionError
	 *             if at least one string isn't found
	 */
	public void assertTextPresent(String... strings) throws AssertionError, IOException {
		for (String string : strings) {
			System.out.println("driver = " + driver);
			assertTrue(driver.getPageSource().contains(string), "The Text '" + string + "' isn't present on the page!");
		}
	}
	
	public void verifyTextPresent(String... strings) throws IOException, APIException {
		for (String string : strings) {
			try {
				assertTextPresent(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Assert if a "text" (string) passed through the parameter <strong>is Not
	 * Present</strong> on the page.
	 * <p>
	 * You can pass as many strings as you like, separated by comma. The test
	 * will fail if at least one element <strong>is</strong> found on the page.
	 * <p>
	 * E.g.: {@code assertTextNotPresent("String 1", "String 2", "String 3");}
	 * 
	 * @param strings
	 *            the texts that will be validated
	 * @throws Exception
	 *             if at least one string is found
	 */
	public void assertTextNotPresent(String... strings) throws AssertionError {
		for (String string : strings) {
			assertFalse(driver.getPageSource().contains(string), "The Text '" + string + "' is present in the page!");
		}
	}
	
	public void verifyTextNotPresent(String... strings) {
		for (String string : strings) {
			try {
				assertTextNotPresent(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if an element, searched by <strong>tag name</strong>, is
	 * present on the page.
	 * <p>
	 * You can pass as many strings as you like, separated by comma. The test
	 * will fail if at least one element <strong>isn't</strong> found on the
	 * page.
	 * <p>
	 * E.g.: {@code isElementPresentByName("name1", "name2", "name3")};
	 * <p>
	 * OBS.: Selenium does not validate HTML tags inside frame. For example, if
	 * the page that will be validated with this method has a tag "name" inside
	 * a frame, the test will fail.
	 * 
	 * @param strings
	 *            the tag names that you want validate
	 * @throws AssertionError
	 *             if at least one tag name isn't found
	 */
	public void assertElementPresentByName(String... strings) throws AssertionError {
		for (String string : strings) {
			assertTrue(driver.findElements(By.name(string)).size() > 0, "The Element with name '" + string + "' is not present on page!");
		}
	}
	
	public void verifyElementPresentByName(String... strings) {
		for (String string : strings) {
			try {
				assertElementPresentByName(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if an element, searched by <strong>tag id</strong>, is present
	 * on the page.
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>isn't</strong> found on the
	 * page.
	 * <p>
	 * E.g.: {@code isElementPresentById("id1", "id2", "id3");}
	 * <p>
	 * OBS.: Selenium does not validate HTML tags inside frame. For example, if
	 * the page that will be validated with this method has a tag "id" inside a
	 * frame, the test will fail.
	 * 
	 * @param strings
	 *            the id that you want to validate
	 * @throws AssertionError
	 *             if at least one element isn't found
	 */
	public void assertElementPresentById(String... strings) throws AssertionError {
		for (String string : strings) {
			assertTrue(driver.findElements(By.id(string)).size() > 0, "The Element with name '" + string + "' is not present on page!");
		}
	}
	
	public void verifyElementPresentById(String... strings) {
		for (String string : strings) {
			try {
				assertElementPresentById(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if an element, searched by <strong>class name</strong>, is
	 * present on the page.
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>isn't</strong> found on the
	 * page.
	 * <p>
	 * E.g.: {@code isElementPresentByClass("class1", "class2", "class3");}
	 * 
	 * @param strings
	 *            the id that you want to validate
	 * @throws AssertionError
	 *             if at least one element isn't found
	 */
	public void assertElementPresentByClass(String... strings) throws AssertionError {
		for (String string : strings) {
			assertTrue(driver.findElements(By.className(string)).size() > 0, "The Element with name '" + string + "' is not present on page!");
		}
	}
	
	public void verifyElementPresentByClass(String... strings) {
		for (String string : strings) {
			try {
				assertElementPresentByClass(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if an element or an element group is visible, passing a class
	 * name.
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>isn't</strong> displayed on the
	 * page.
	 * <p>
	 * E.g.:
	 * {@code isTextPresentAndVisibleByClass("class1", "class2", "class3");}
	 * 
	 * @param strings
	 *            the class name that you want to validate
	 * @throws AssertionError
	 *             if at least one element isn't visible
	 */
	public void assertTextPresentAndVisibleByClass(String... strings) throws AssertionError {
		for (String string : strings) {
			assertTrue(driver.findElement(By.className(string)).isDisplayed(), "The text with attribute name '" + string + "' is not visible on the page!");
		}
	}
	
	public void verifyTextPresentAndVisibleByClass(String... strings) {
		for (String string : strings) {
			try {
				assertTextPresentAndVisibleByClass(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if an element or an element group is not visible, passing a
	 * class name.
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>is</strong> displayed on the
	 * page.
	 * <p>
	 * E.g.:
	 * {@code isTextPresentAndNotVisibleByClass("class1", "class2", "class3");}
	 * 
	 * @param strings
	 *            the class name that you want to validate
	 * @throws AssertionError
	 *             if at least one element is visible
	 */
	public void assertTextPresentAndNotVisibleByClass(String... strings) throws AssertionError {
		for (String string : strings) {
			assertFalse(driver.findElement(By.className(string)).isDisplayed(), "The text with attribute name '" + string + "' is visible on the page!");
		}
	}
	
	public void verifyTextPresentAndNotVisibleByClass(String... strings) {
		for (String string : strings) {
			try {
				assertTextPresentAndNotVisibleByClass(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * TODO: add method description here
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>is</strong> enabled on the
	 * page.
	 * 
	 * @param strings
	 *            the element name that you want to validate
	 * @throws AssertionError
	 *             if at least one element is enabled
	 */
	public void assertTextPresentAndDisabledByName(String... strings) throws AssertionError {
		for (String string : strings) {
			assertTrue(driver.findElement(By.name(string)).isEnabled(), "The text with attribute name '" + string + "' is not Enabled on page!");
		}
	}
	
	public void verifyTextPresentAndDisabledByName(String... strings) {
		for (String string : strings) {
			try {
				assertTextPresentAndDisabledByName(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * TODO: add method description here
	 * <p>
	 * You can pass as many elements as you like, separated by comma. The test
	 * will fail if at least one element <strong>is</strong> disabled on the
	 * page.
	 * 
	 * @param strings
	 *            the element name that you want to validate
	 * @throws AssertionError
	 *             if at least one element is disabled
	 */
	public void assertTextPresentAndNotDisabledByName(String... strings) throws AssertionError {
		for (String string : strings) {
			assertFalse(driver.findElement(By.name(string)).isEnabled(), "The text with attribute name '" + string + "' is not Enabled on page!");
		}
	}
	
	public void verifyTextPresentAndNotDisabledByName(String... strings) {
		for (String string : strings) {
			try {
				assertTextPresentAndNotDisabledByName(string);
			} catch(AssertionError e){
				attachError(e);
			}
		}
	}

	/**
	 * Validates if the given option {@code stringValue} is selected in the
	 * element {@code selectName}.
	 * <p>
	 * If the value passed by parameter is not the same as on the page, the test
	 * will fail.
	 * <p>
	 * E.g.: {@code optionIsSelected("SelectTagName", "optionValue);}
	 * 
	 * @param selectName
	 *            the name of the selector
	 * @param stringValue
	 *            the value that should be selected
	 * @throws AssertionError
	 *             if the current selected option is different from stringValue
	 */
	public void assertOptionIsSelected(String selectName, String stringValue) throws AssertionError {
		String optionSelected = driver.findElement(By.name(selectName)).getText();
		assertTrue(optionSelected.contentEquals(stringValue), "The option with value '" + stringValue + "' is not selected on dropdown!");
	}
	
	public void verifyOptionIsSelected(String selectName, String stringValue) {
		try {
			assertOptionIsSelected(selectName, stringValue);
		} catch(AssertionError e){
			attachError(e);
		}
	}

	/**
	 * Compares the options inside a dropdown ({@code List<WebElement>}) in the
	 * page with the options inside an expected given list.
	 * <p>
	 * If the options are different, the assert will fail.
	 * <p>
	 * E.g.: {@code compareListOption(webElement, arrayOptions, true, true);}
	 * 
	 * @param dropdownOptions
	 *            dropdown ({@code List<WebElement>}) in the page
	 * @param listOptionsExpected
	 *            list with the expected options, that will be compared to the
	 *            options in the dropdown
	 * @param compareOrder
	 *            indicates if the order has to be considered or not
	 * @param caseSensitive
	 *            indicates if the comparison has to be case sensitive or not
	 * @throws AssertionError
	 *             if the dropdown is different from the expected list
	 */
	public void assertListsAreEqual(List<WebElement> dropdownOptions, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) throws AssertionError {
		int sizeActual = dropdownOptions.size();
		int sizeExpected = listOptionsExpected.length;

		assertTrue((sizeActual == sizeExpected), "List size is different. List size: " + sizeActual + ". Expected size: " + sizeExpected);

		if (!caseSensitive) {
			for (int i = 0; i < sizeExpected; i++) {
				listOptionsExpected[i] = listOptionsExpected[i].toLowerCase();
			}
		}

		// Se a ordem dos itens importar
		if (compareOrder) {
			String[] listActual = new String[sizeActual];

			for (int i = 0; i < sizeActual; i++) {
				if (!caseSensitive) {
					listActual[i] = dropdownOptions.get(i).getText().toLowerCase();
				} else {
					listActual[i] = dropdownOptions.get(i).getText();
				}
			}
			
			assertEquals(listActual, listOptionsExpected); //TODO: confirmar se funciona
			//assertArrayEquals("Lists options are different", listOptionsExpected, listActual);
		}
		// Se a ordem dos itens não importar
		else {
			String opcaoAtual = "";
			ArrayList<String> arrayActual = new ArrayList<String>();

			for (int i = 0; i < sizeActual; i++) {
				if (!caseSensitive) {
					arrayActual.add(i, dropdownOptions.get(i).getText().toLowerCase());
				} else {
					arrayActual.add(i, dropdownOptions.get(i).getText());
				}

			}

			// Inicia comparação item a item da lista, para saber se são iguais,
			// independente da ordem
			for (int i = 0; i < sizeExpected; i++) {
				for (int j = 0; j < sizeActual; j++) {
					opcaoAtual = arrayActual.get(j);
					if (opcaoAtual.equals(listOptionsExpected[i])) {
						arrayActual.remove(j);
						sizeActual--;
						j = 0;
						break;
					}
				}
			}

			// Se a lista contém um ou mais itens diferentes do esperado
			if (sizeActual != 0) {
				String itensDiferentes = "";
				for (int i = 0; i < sizeActual; i++)
					itensDiferentes += arrayActual.get(i) + ". ";
				assertFalse(true, "The list has different options. Items that are different: " + itensDiferentes);
			}
		}
	}
	
	public void verifyListsAreEqual(List<WebElement> dropdownOptions, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) {
		try {
			assertListsAreEqual(dropdownOptions, listOptionsExpected, compareOrder, caseSensitive);
		} catch(AssertionError e){
			attachError(e);
		}
		
	}

	/**
	 * Compares the options inside a dropdown (by the dropdown Name) in the page
	 * to the options inside an expected given list. If the options are
	 * different, the assert will fail.
	 * <p>
	 * E.g.:
	 * {@code compareListOption("dropdownName", arrayOptions, true, true);}
	 * 
	 * @param dropdownName
	 *            the dropdown name in the page
	 * @param listOptionsExpected
	 *            the list with the expected options, that will be compared to
	 *            the options in the dropdown
	 * @param compareOrder
	 *            indicates if the order has to be considered or not
	 * @param caseSensitive
	 *            indicates if the comparison has to be case sensitive or not
	 * @throws AssertionError
	 *             if the dropdown is different from the expected list
	 */
	public void assertListsAreEqualByName(String dropdownName, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) throws AssertionError {
		Select dropdown = new Select(driver.findElement(By.name(dropdownName)));
		List<WebElement> listOptions = dropdown.getOptions();

		assertListsAreEqual(listOptions, listOptionsExpected, compareOrder, caseSensitive);
	}
	
	public void verifyListsAreEqualByName(String dropdownName, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) {
		try {
			assertListsAreEqualByName(dropdownName, listOptionsExpected, compareOrder, caseSensitive);
		} catch(AssertionError e){
			attachError(e);
		}
	}

	/**
	 * Compares the options inside a dropdown (by the dropdown Name) in the page
	 * to the options inside an expected given list. If the options are
	 * different, the assert will fail.
	 * <p>
	 * E.g.: {@code compareListOption("dropdownID", arrayOptions, true, false);}
	 * 
	 * @param dropdownID
	 *            dropdown ID in the page
	 * @param listOptionsExpected
	 *            list with the expected options, that will be compared to the
	 *            options in the dropdown
	 * @param compareOrder
	 *            indicates if the order has to be considered or not
	 * @param caseSensitive
	 *            indicates if the comparison has to be case sensitive or not
	 * @throws AssertionError
	 *             if the dropdown is different from the expected list
	 */
	public void assertListsAreEqualByID(String dropdownID, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) throws AssertionError {
		Select dropdown = new Select(driver.findElement(By.id(dropdownID)));
		List<WebElement> listOptions = dropdown.getOptions();
		
		assertListsAreEqual(listOptions, listOptionsExpected, compareOrder, caseSensitive);
	}
	
	public void verifyListsAreEqualByID(String dropdownID, String[] listOptionsExpected, boolean compareOrder,
			boolean caseSensitive) {
		try {
			assertListsAreEqualByID(dropdownID, listOptionsExpected, compareOrder, caseSensitive);
		} catch(AssertionError e){
			attachError(e);
		}
	}

	/**
	 * Compares two strings. The test will fail if the strings are different.
	 * <p>
	 * E.g.: This method can be used to compare if email and confirm email are
	 * the same, if password and confirm password are the same.
	 * 
	 * @param string1
	 *            the string to be compared
	 * @param string2
	 *            the string to be compared
	 * @throws AssertionError
	 *             if string1 is different from string2
	 */
	public void assertStringsAreEqual(String string1, String string2) throws AssertionError {
		assertTrue(string1.equals(string2), "String '" + string1 + "' is different from '" + string2 + "'.");
	}
	
	public void verifyStringsAreEqual(String string1, String string2) {
		try {
			assertStringsAreEqual(string1, string2);
		} catch(AssertionError e){
			attachError(e);
		}
	}
	
	/**
	 * Compares two strings. The test will fail if the strings are equal.
	 * <p>
	 * E.g.: This method can be used to compare if email and second email are
	 * the different.
	 * 
	 * @param string1
	 *            the string to be compared
	 * @param string2
	 *            the string to be compared
	 * @throws AssertionError
	 *             if string1 is equals to string2
	 */
	public void assertStringsAreDifferent(String string1, String string2) throws AssertionError {
		assertFalse(string1.equals(string2), "String '" + string1 + "' is the same as '" + string2 + "'.");
	}
	
	public void verifyStringsAreDifferent(String string1, String string2) {
		try {
			assertStringsAreDifferent(string1, string2);
		} catch(AssertionError e){
			attachError(e);
		}
	}

	/**
	 * Returns a random option from a given dropdown.
	 * <p>
	 * It will return "" if dropdown is empty.
	 * 
	 * @param dropdown
	 *            the dropdown to be used
	 * @return a string with a random option from the given dropdown
	 */
	public String getAnyDropdownOption(List<WebElement> dropdown) {
		List<WebElement> dropdownOptions = dropdown;
		String opcao = "";
		Random rand = new Random();
		int randomNum = 0;
		int size = dropdownOptions.size();

		if (size == 1) {
			opcao = dropdownOptions.get(0).getText();
		} else if (size > 1) {
			randomNum = rand.nextInt((size - 2) + 1) + 1;
			opcao = dropdownOptions.get(randomNum).getText();
		}

		return opcao;
	}

	/**
	 * Waits for the value inside an element to change. For example: if an
	 * element current value is "test", and within 10 seconds it changes to
	 * "finish", the test will succeed. Otherwise the test will fail.
	 * 
	 * @param element
	 *            the WebElement to be watched
	 * @param currentValue
	 *            the element current value
	 * @throws Exception
	 *             if after 10 seconds the element value remains the same
	 */
	public void waitForElementToChange(final WebElement element, final String currentValue) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, 10);

		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				String value = element.getAttribute("value");
				System.out.println("VALUE: " + value);

				if (!value.equals(currentValue)) {
					return true;
				}

				return false;
			}
		});
	}

	/**
	 * Verifies if an alert is present or not.
	 * 
	 * @return true if the alert is present, and false if it's not present.
	 */
	public boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	/**
	 * Closes (accept or dismiss) an alert, and returns its text.
	 * 
	 * @param acceptNextAlert
	 *            indicates if the alert should be accepted or dismissed (true
	 *            or false)
	 * @return a string containing the alert message
	 */
	public String closeAlertAndGetItsText(boolean acceptNextAlert) {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}
	
	public void deleteCookies(){
		if(driver.manage().getCookies() != null)
			driver.manage().deleteAllCookies();
    }
    
    public void deleteCookieByName(String cookieName){
    	driver.manage().deleteCookieNamed(cookieName);
    }

	/**
	 * Returns the current date. The date is returned in this format: yyyy-MM-dd
	 * HH.mm.ss
	 * 
	 * @return a string with the current date
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
		Date date = new Date();

		return dateFormat.format(date);
	}

	/**
	 * Returns the current date in the given format.
	 * 
	 * @param format
	 *            the format that the date should be returned (e.g.: yyyy-MM-dd
	 *            HH.mm.ss)
	 * @return a string with the current date
	 */
	public static String getCurrentDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();

		return dateFormat.format(date);
	}
	
	/**
	 * Returns the String without some special characters: 
	 * {@code \ / " * ? : < > @ # ! % & { }}
	 * 
	 * @param string
	 *            the string that will have the characters replaced
	 * @return a string without the special characters
	 */
	public static String removeSpecialCharacters(String string) {
		return string.replaceAll("[\"\\*/?:<>@#!%&{}]","");
	}
	
	
	/**
	 * Returns the boolean result for the element present. <br>
	 * For to verify if element exist in HTML can be used a parameter to type of verification.<br><br>
	 * <i>Example:</i> Parameter Type equals "id" the method verify for ID.<br>
	 * The possibilities are <strong> "id", "class", "name", "xpath", "link".</strong> 
	 * 
	 * @param type -> type of verification
	 * @param strings -> <i>className, ids, name or xpath</i>.
	 * @return
	 */
	public boolean isElementPresentByType(String type, String... strings){
		boolean present = true;
		int ispresent;
		for (String string : strings) {
			if(type == "id"){
				ispresent = driver.findElements(By.id(string)).size();
			} else if(type == "class"){
				ispresent = driver.findElements(By.className(string)).size();
			} else if( type == "name") {
				ispresent = driver.findElements(By.name(string)).size();
			} else if(type == "link"){
				ispresent = driver.findElements(By.linkText(string)).size();
			} if(type == "css"){
				ispresent = driver.findElements(By.cssSelector(string)).size();
			} else {
				ispresent = driver.findElements(By.xpath(string)).size();
			}
			
			if(ispresent != 0){
				present =  true;
			} else {
				present = false;
			}
		}
		return present;
	}
	
	
	/**
	 * Method that move mouse for some object on the view by type verification.
	 * 
	 * @param type
	 * 			<i>type of verification</i> 
	 * @param element
	 * 			<i>attribute of type like ids, classNames, link or xpath.</i>
	 */
	public void moveMouseFor(String type, String element) {
		Actions myMouse = new Actions(driver);
		WebElement e;
		
		if(type == "id"){
			e = driver.findElement(By.id(element));
			myMouse.moveToElement(e).build().perform();
		} else if(type == "class"){
			e = driver.findElement(By.className(element));
			myMouse.moveToElement(e).build().perform();
		} else if( type == "name") {
			e = driver.findElement(By.name(element));
			myMouse.moveToElement(e).build().perform();
		} else if(type == "link"){
			e = driver.findElement(By.linkText(element));
			myMouse.moveToElement(e).build().perform();
		}else {
			e = driver.findElement(By.xpath(element));
			myMouse.moveToElement(e).build().perform();
		}
		
	}
	
	/** Assert if each "text" (string) passed through the parameter <strong>is
	 * Present</strong> on the page.
	 * <p>
	 * The test will fail if the text isn't found on the Element.<br>
	 * You should have pass a type like <i>"id", "name", "class", "link" or "xpath".</i>
	 * <p>
	 * E.g.: {@code assertTextPresent("link", "element", "String");}
	 * 
	 * @param strings
	 *            the text that will be validated
	 * @throws AssertionError
	 *             if at least one string isn't found
	 */
	public void assertTextPresentByType(String type, String element, String string) throws AssertionError {
		if(type == "id"){
			assertEquals(driver.findElement(By.id(element)).getText(), string,  "The Text '" + string + "' isn't present on the page!");
		}else if(type == "name"){
			assertEquals(driver.findElement(By.name(element)).getText(), string,  "The Text '" + string + "' isn't present on the page!");
		}else if(type == "class"){
			assertEquals(driver.findElement(By.className(element)).getText(), string,  "The Text '" + string + "' isn't present on the page!");
		}else if(type == "link"){
			assertEquals(driver.findElement(By.linkText(element)).getText(), string,  "The Text '" + string + "' isn't present on the page!");
		}else {
			assertEquals(driver.findElement(By.xpath(element)).getText(), string,  "The Text '" + string + "' isn't present on the page!");
		}
		
	}
	
	public void verifyTextPresentByType(String type, String element, String string) {
		try {
			assertTextPresentByType(type, element, string);
		} catch(AssertionError e){
			attachError(e);
		}
	}
	
	public boolean isTextPresentArray(String[] strings) {
		String erros = "";
		boolean flag = false;
		for (String string : strings) {
			flag = driver.getPageSource().contains(string);
			if(flag == false) {
				erros = erros + string + "; ";
				
			}
		}
		if(!erros.equals("")) {
			report.logInfo("Textos não encontrados: " + erros);
			return false;
		}
		return true;
	}
	
	public void waitForSomethingByType(String type, String waitSomething) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10000);
		if(type == "id") {
			report.logInfo("Searching by ID: " + waitSomething);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(waitSomething)));
			report.logInfo("Found ID: " + waitSomething);
			System.out.println("Found id: " + waitSomething);
		} else if(type == "xpath") {
			report.logInfo("Searching by XPATH: " + waitSomething);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(waitSomething)));
			report.logInfo("Found XPATH: " + waitSomething);
			System.out.println("Found xpath: " + waitSomething);
		} else if(type == "class") {
			report.logInfo("Searching by CLASS: " + waitSomething);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.className(waitSomething)));
			report.logInfo("Found CLASS: " + waitSomething);
			System.out.println("Found class: " + waitSomething);
		} else if(type == "css") {
			report.logInfo("Searching by CSS: " + waitSomething);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(waitSomething)));
			report.logInfo("Found CSS: " + waitSomething);
			System.out.println("Found css: " + waitSomething);
		} else if(type == "name") {
			report.logInfo("Searching by NAME: " + waitSomething);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(waitSomething)));
			Thread.sleep(3000);
			report.logInfo("Found NAME: " + waitSomething);
			System.out.println("Found name: " + waitSomething);
		} else {
			report.logError("The Type informed doesn't exist!");
		}
	}
	
	public void waitForSomethingAttibute(WebElement element, String attribute, String value) throws InterruptedException{
		WebDriverWait wait = new WebDriverWait(driver, 10000);
		System.out.println("element, attribute, value = "+ element + " "+  attribute + " "+ value);
		wait.until(ExpectedConditions.attributeContains(element, attribute, value));
	}
	
	public void waitForSomethingClickableByType(String type, String waitSomething) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, 10000);
		if(type == "id") {
			report.logInfo("Searching by ID: " + waitSomething);
			wait.until(ExpectedConditions.elementToBeClickable(By.id(waitSomething)));
			report.logInfo("Found ID: " + waitSomething);
			System.out.println("Found id: " + waitSomething);
		} else if(type == "xpath") {
			report.logInfo("Searching by XPATH: " + waitSomething);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(waitSomething)));
			report.logInfo("Found XPATH: " + waitSomething);
			System.out.println("Found xpath: " + waitSomething);
		} else if(type == "class") {
			report.logInfo("Searching by CLASS: " + waitSomething);
			wait.until(ExpectedConditions.elementToBeClickable(By.className(waitSomething)));
			report.logInfo("Found CLASS: " + waitSomething);
			System.out.println("Found class: " + waitSomething);
		} else if(type == "css") {
			report.logInfo("Searching by CSS: " + waitSomething);
			wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(waitSomething)));
			report.logInfo("Found CSS: " + waitSomething);
			System.out.println("Found css: " + waitSomething);
		} else if(type == "name") {
			report.logInfo("Searching by NAME: " + waitSomething);
			wait.until(ExpectedConditions.elementToBeClickable(By.name(waitSomething)));
			Thread.sleep(3000);
			report.logInfo("Found NAME: " + waitSomething);
			System.out.println("Found name: " + waitSomething);
		} else {
			report.logError("The Type informed doesn't exist!");
		}
	}
	
	/**
     * Create testrun as per test suite in testrail
     */
    public static long addTestRunInTestRail() throws IOException, APIException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy-HH:mm:ss");
        Date date = new Date();
        String runIdName = TEST_RUNNAME_PREFIX+ " - " + formatter.format(date);
        APIClient client = setTestRailClient();
        Map data = new HashMap();
        data.put("name", runIdName);
        data.put("description", "This is the execution of all testcases which is available in iDevOps");
        data.put("suite_id", Integer.valueOf(TEST_RUN_SUITEID));
        data.put("include_all", "1");
        
        System.out.println("addTestRunInTestRail PREFIX = " + TEST_RUNNAME_PREFIX);
        System.out.println("addTestRunInTestRail SUITEID = " + TEST_RUN_SUITEID);
        System.out.println("addTestRunInTestRail TEST_PROJECT_ID = " + TEST_PROJECT_ID);
        
        client.sendPost("add_run/"+TEST_PROJECT_ID, data);
        // Return runId which is generated of above
        long testRunId = getTestRunId(client, runIdName);
        TEST_REAIL_RUN_ID = testRunId;
        return testRunId;
    }

    /**
     * Use testrail creadential required for TestRail API integration
     * @param mapDataSet to do
     * @return to do
     */
    public static APIClient setTestRailClient() {
        APIClient client = new APIClient(TEST_RAIL_URL);
        client.setUser(TEST_RAIL_USER);
        client.setPassword(TEST_RAIL_PASSWORD);
        
        System.out.println("Authentication: " + TEST_RAIL_URL + " User: " + TEST_RAIL_USER + " Password: " + TEST_RAIL_PASSWORD);
        return client;
    }

    /**
     * Get runId which is generated as per specific name
     * @param client to do
     * @param runIdName to do
     * @returnto do
     * @throws MalformedURLException to do
     * @throws IOException to do
     * @throws APIException to do
     */
    public static long getTestRunId(APIClient client, String runIdName) throws MalformedURLException, IOException, APIException {
        long runId = 0;
        JSONArray runids = (JSONArray) client.sendGet("get_runs/"+TEST_PROJECT_ID);
        for (int i = 0; i < runids.size(); i++) {
            Map runidObj = (Map) runids.get(i);
            if (runidObj.get("name").equals(runIdName))
                runId = (Long) runidObj.get("id");
            break;
        }
        
        System.out.println(" RUNID = " + runId);
        return runId;
    }
    
    /**
	 * Upload testcase result in testrail
	 * @throws SQLException 
	 */
	public void addTestCaseResultInTestRail(long testRailRunId, String testrailCaseId, int status, String error) throws IOException, APIException {
		APIClient client = setTestRailClient();
        Map data = new HashMap();
        data.put("status_id", status);
        data.put("comment", error);
        System.out.println(" TestRailRUNID = " + testRailRunId + " TestCASEID = " + testrailCaseId);
        client.sendPost("add_result_for_case/"+testRailRunId+"/"+testrailCaseId, data);
    }
	
	public static JSONObject getCase(int p_caseId) throws Exception{
		APIClient client = setTestRailClient();
        JSONObject c = (JSONObject) client.sendGet("get_case/" + p_caseId);

        return c;
    }
	
}
