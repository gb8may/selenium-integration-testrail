package sample.base;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import sample.base.BaseTest;
import sample.base.ScreenImage;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

public class CaptureScreenshot {
	
	/**
	 * Calls {@code capturePage(String folderName, String subFolderName, String
	 * imageMainName, String imageAditionalName)} method, passing the folder,
	 * sub-folder and file name that are retrieved from the current test suite.
	 * OBS.: No additional name will be added to the screenshot name.
	 * 
	 * @return link the link to the screenshot
	 */
	public void capturePage(WebDriver driver){
		capturePage(driver, BaseTest.currentTestSuite, BaseTest.currentTestClass, BaseTest.currentTestMethod, null);
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
	public void capturePage(WebDriver driver, String imageAditionalName){		
		capturePage(driver, BaseTest.currentTestSuite, BaseTest.currentTestClass, BaseTest.currentTestMethod, imageAditionalName);
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
	public String capturePage(WebDriver driver, String folderName, String subFolderName, String imageMainName,
			String imageAditionalName) {
		File screenshot = null;
		ScreenImage img;
		String currentImageName;
		
		//driver.manage().window().maximize();

		if (imageAditionalName.equals(null)) {
			currentImageName = imageMainName + " (" + BaseTest.getCurrentDate() + ").png";
		} else {
			currentImageName = imageMainName + "-" + imageAditionalName + " (" + BaseTest.getCurrentDate() + ").png";
		}
		
		img = BaseTest.report.createScreenImage(folderName, subFolderName, currentImageName);

		try {
			// Takes a screenshot and creates the file inside the directory
			if (BaseTest.currentBrowser.equals(BaseTest.CHROME) || BaseTest.currentBrowser.equals(BaseTest.FIREFOX)) {
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

			BaseTest.report.logScreenshotToTestRunner(img.getLink());

		} catch (Exception e) {
			BaseTest.report.logToTestRunner("Error capturing screenshot. " + e.getMessage());
			img.setLink("#");
		}
		
		BaseTest.report.addImageToList(img); //add image to the image list

		return img.getLink();
	}
	
}
