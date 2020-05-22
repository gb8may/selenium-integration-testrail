package sample.base;

import org.testng.ISuite;
import org.testng.ISuiteListener;

public class ReportSuiteListener implements ISuiteListener{

	public void onStart(ISuite suite) {
		BaseTest.currentTestSuite = suite.getName();
		BaseTest.report.createExtent(BaseTest.currentTestSuite != null ? BaseTest.removeSpecialCharacters(BaseTest.currentTestSuite) : "Suite");
		BaseTest.report.logHTMLToTestRunner("<table>");
	}

	public void onFinish(ISuite suite) {
		BaseTest.report.logHTMLToTestRunner("</table>");
		BaseTest.report.closeExtent();
	}
}