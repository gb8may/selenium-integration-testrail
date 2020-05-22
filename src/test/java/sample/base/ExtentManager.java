package sample.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
 
public class ExtentManager {
	
	private ExtentReports extent;
	private ExtentTest test;
	private ExtentHtmlReporter htmlReporter;
	private String separator = java.io.File.separator;
	private String reportFolder;
	private String reportPath;
	private String reportName;
	private String screenshotsFolder;
	private List<ScreenImage> imagesList;
	
	public ExtentManager() {
		reportFolder = "Report_" + BaseTest.getCurrentDate();
		System.out.println(" report = " + BaseTest.RESULTS_PATH + separator + reportFolder);
		reportPath = BaseTest.RESULTS_PATH + separator + reportFolder;
		screenshotsFolder = "screenshots";
		imagesList = new ArrayList<ScreenImage>();
	}
	
	protected void createExtent(String reportName){
		this.reportName = reportName + ".html";			
		extent = new ExtentReports();
		extent.setSystemInfo("Environment", BaseTest.currentEnvironment);
		extent.attachReporter(getHtmlReporter());
	}
 
	private ExtentHtmlReporter getHtmlReporter() {
		File reportFile = new File(reportPath + separator + reportName);
		reportFile.getParentFile().mkdirs();
		htmlReporter = new ExtentHtmlReporter(reportFile);
        htmlReporter.loadXMLConfig("./config/extent-config.xml");
        htmlReporter.config().setReportName("Test Results - " + BaseTest.currentTestSuite);
        
        return htmlReporter;
	}
	
	protected void closeExtent() {
//		extent.close();	
		customizeHTML(reportPath, reportName);
	}
	
	/**
	 * Receives the generated HTML report, and customizes it as needed.
	 * 
	 */
	private void customizeHTML(String reportPath, String reportName) {
		File folder = new File(reportPath);
		String directoryName = folder.toString();

		if (new File(directoryName).exists()) {
			try {
				// ajusta encoding do arquivo
				File file = new File(folder + separator + reportName);
				Document document = Jsoup.parse(file, "UTF-8");
				
				//se necessário, adicionar customização aqui
				
				FileUtils.writeStringToFile(file, document.outerHtml(), "UTF-8");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public ScreenImage createScreenImage(String folderName, String subFolderName, String imageName) {
		String currentImageName, currentImagePath, currentImageLink;
		ScreenImage currentImage;
		
		currentImageName = imageName;
		currentImagePath = reportPath + separator + screenshotsFolder + 
				separator + folderName + separator + subFolderName + separator;
		currentImageLink = screenshotsFolder + separator + folderName + separator + subFolderName + separator + currentImageName;
		currentImage = new ScreenImage(imagesList.size(), currentImagePath, currentImageName, currentImageLink, BaseTest.currentTestSuite, BaseTest.currentTestClass, BaseTest.currentTestMethod);
		
		return currentImage;
	}

	public List<ScreenImage> getImagesList() {
		return imagesList;
	}

	protected void setImagesList(List<ScreenImage> screenImages) {
		this.imagesList = screenImages;
	}
	
	public void addImageToList(ScreenImage img) {
		imagesList.add(img);
	}

	public String getReportFolder() {
		return reportFolder;
	}

	public String getReportPath() {
		return reportPath;
	}

	public String getReportName() {
		return reportName;
	}

	public String getScreenshotsFolder() {
		return screenshotsFolder;
	}

	protected void setReportFolder(String reportFolder) {
		this.reportFolder = reportFolder;
	}

	protected void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	protected void setReportName(String reportName) {
		this.reportName = reportName;
	}

	protected void setScreenshotsFolder(String screenshotsFolder) {
		this.screenshotsFolder = screenshotsFolder;
	}

	public void flush() {
		extent.flush();
	}
	
	/**
	 * Adds the given description to the Extent Report Test and TestRunner
	 * Report Log, with an info indicator
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logInfo(String description) {
		try {
			test.info(description);
			logToTestRunner("<span style=\"color:#2196F3\">Info: </span> " + description);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Adds the given description to the Extent Report Test and TestRunner
	 * Report Log, with an error indicator
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logError(String description) {
		try {
			test.error(description);
			logToTestRunner("<span style=\"color:#F44336\">Error: </span> " + description);
		} catch (Exception e) {
		}
	}
	
	/**
	 * Adds the given description to the Extent Report Test and TestRunner
	 * Report Log, with a warning indicator
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logWarning(String description) {
		try {
			test.warning(description);
			logToTestRunner("<span style=\"color:#ff9800\">Warning: </span> " + description);
		} catch (Exception e) {
		}
	}

	/**
	 * Adds the given description only to the TestRunner Report Log
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logToTestRunner(String description) {
		try {
			extent.setTestRunnerOutput("<p style=\"margin-left: 15px;\">[" + BaseTest.getCurrentDate("MMM dd, yyyy HH:mm:ss")
					+ "] " + description + "</p>");
		} catch (Exception e) {
		}
	}

	/**
	 * Adds the given description only to the TestRunner Report Log
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logScreenshotToTestRunner(String screenshotURL) {
		try {
			logToTestRunner("Screenshot:");
			extent.setTestRunnerOutput("<p style=\"text-align:center;\"><a href=\"" + screenshotURL
					+ "\" target=\"_blank\">" + "<img src=\"" + screenshotURL + "\" width=\"" + BaseTest.SCREENSHOTS_WIDTH
					+ "\" style=\"border: 1px solid #ccc;\" /></a></p>");
		} catch (Exception e) {
		}
	}
	
	/**
	 * Adds the given description only to the TestRunner Report Log
	 * 
	 * @param description
	 *            the description to be added in the Report Log
	 */
	public void logHTMLToTestRunner(String description) {
		try {
			extent.setTestRunnerOutput(description);
		} catch (Exception e) {
		}
	}
	
	public void addScreenCaptureFromPath(String link) {
		try {
			test.addScreenCaptureFromPath(link);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void assignCategory(String categoryName) {
		test.assignCategory(categoryName);
	}
	
	public void startTest(ITestResult result) {
		BaseTest.currentTestClass = result.getTestClass().getRealClass().getSimpleName();
		BaseTest.currentTestMethod = result.getName().toString().trim();
		String testClassMethod = BaseTest.currentTestClass + "/" + BaseTest.currentTestMethod;
		String testMethodDescription = result.getMethod().getDescription();

		System.out.println("[TEST-INFO] Current method: " + testClassMethod);
		test = extent.createTest(testClassMethod, testMethodDescription);
		//test.assignCategory(BaseTest.currentTest);
		test.assignCategory(BaseTest.currentTestClass);
		 
		/*String[] categories = result.getMethod().getGroups();
		if(categories.length > 0){			
			for (int i = 0; i < categories.length; i++) {
				assignCategory(categories[i]);
			}
		}*/

		// Classe e método de teste em execução
		logHTMLToTestRunner("<pre>" + testClassMethod + ": " + testMethodDescription + "</pre>");
	}
	
	public void finishTest(ITestResult result){
		String cssClass, status;
		
		if(result.getStatus() == ITestResult.SUCCESS) {
			cssClass = "label green accent-4 white-text";
			test.pass("Success");
			status = "Pass";
		}
		else if(result.getStatus() == ITestResult.FAILURE) {
			cssClass = "label red lighten-1 white-text";
			test.fail(result.getThrowable());
			status = "Fail";
		}
		else if(result.getStatus() == ITestResult.SKIP) {
			cssClass = "label yellow darken-2 white-text";
			test.skip(result.getThrowable());
			status = "Skip";
		}
		else if(result.getStatus() == ITestResult.SUCCESS_PERCENTAGE_FAILURE) {
			cssClass = "label green accent-4 white-text";
			test.pass("Sucess with Percentage Failure");
			status = "Pass (with Percentage Failure)";
		}
		else {
			cssClass = "label yellow darken-2 white-text";
			status = "Other";
		}
		
		logToTestRunner("Test result: <span class=\"" + cssClass + "\">" + status + "</span>");
		extent.flush();
	}
	
}

