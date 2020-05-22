package sample.base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import sample.utility.testrail.APIClient;
import sample.utility.testrail.APIException;
import sample.utility.testrail.TestRail;

public class ReportTestListener extends TestListenerAdapter {
	
	BaseTest basetest = new BaseTest();

	@Override
	public void onStart(ITestContext context) {
		BaseTest.currentTest = context.getName();
		BaseTest.report.logHTMLToTestRunner("<tr><td style=\"text-align:center;\"><h5>" + BaseTest.currentTest + "</h5></td></tr><tr><td>");
	}
	
	@Override
	public void onFinish(ITestContext context) {
		BaseTest.report.logHTMLToTestRunner("</div></td></tr>");
	}

	@Override
	public void onTestStart(ITestResult result) {
		BaseTest.report.startTest(result);
		BaseTest.verificationErrors = new StringBuffer();
		BaseTest.report.logToTestRunner("Test started.");
		
		// Start here log
		String timeStamp = new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(Calendar.getInstance().getTime());
        ITestNGMethod m = result.getMethod();

        // get test method name
        String methodName = m.getMethodName();
        // get test browser name
        String browserName = m.getXmlTest().getParameter("browser");

        System.out.println("");
        System.out.println("===============================================");

        if (browserName == null)
        {
            System.out.println("Test started - " + methodName + " at " + timeStamp);
        }
        else
        {
            System.out.println("Test started - " + methodName + " on browser " + browserName + " at " + timeStamp);
        }

        System.out.println("===============================================");
        System.out.println("");
		
	}

	@Override
	public void onTestSuccess(ITestResult result) {		
		System.out.println("[TEST-INFO] SUCCESS");

		if (BaseTest.SCREENSHOT_SUCCESS.equals(BaseTest.SIM)) {
			String link = BaseTest.capturePage("TEST_SUCCESS"); // Take screenshot
			BaseTest.report.addScreenCaptureFromPath(link);
		}
		
		System.out.println("");
        System.out.println("===============================================");

        // if you have chosen to record test results to TestRail
        if (BaseTest.postResultsToTestRail)
        {
            ITestNGMethod m = result.getMethod();
            // get the method name for the test
            String methodName = m.getMethodName();
            // get the browser name for the test
            String browserName = m.getXmlTest().getParameter("browser");
            String environment = m.getXmlTest().getParameter("environment");

            // get the TestRail Test Run Id from the Base Test
            long testRailTestRunId = BaseTest.TEST_REAIL_RUN_ID;

            // get the TestRail Test Case id from the Test annonation description value
            int[] testCaseIds = returnTestCaseId(result);
            // if the Test Case Id and Test Run Id have both been set then try to add the test result to TestRail
            if (testCaseIds != null && testRailTestRunId != 0)
            {
                // the comment that will be added to the Test Run result
                Object commentFromTest = result.getTestContext().getAttribute("Comment");
                if (BaseTest.debugInfo)
                    System.out.println("Comment attribute = " + commentFromTest);

                String additionalComment;
                if (commentFromTest == null)
                {
                    additionalComment = "";
                }
                else
                {
                    additionalComment = "\n\nAdditional info - " + commentFromTest.toString();
                }

                String testRailComment = "Automated Test - PASS; \n\nTest Method name = " + methodName + "\nBrowser name = " + browserName + "\nEnvironment = " + environment +  additionalComment;

                try
                {
                    // add the result to TestRail
                    for (int testCaseId : testCaseIds)
                    {
                        String testCaseIdStr = String.valueOf(testCaseId);
                        basetest.addTestCaseResultInTestRail(testRailTestRunId, testCaseIdStr, BaseTest.TEST_CASE_PASSED_STATUS, testRailComment);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                // if the Test Case Id or Test Run Id have not been set correctly record a message to the log
                System.out.println("Test results are not being recorded to TestRail for test " + methodName + " on browser " + browserName);
            }
        }
        System.out.println("===============================================");
    

		
		
		
		BaseTest.report.finishTest(result);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("[TEST-INFO] FAILURE");
		BaseTest.report.logToTestRunner("<span style=\"color:red;\">Failure: " + result.getThrowable().getMessage() + "</span>");

		if (BaseTest.SCREENSHOT_SUCCESS.equals(BaseTest.SIM)) {
			String link = BaseTest.capturePage("TEST_FAILURE"); // Take a screenshot
			BaseTest.report.addScreenCaptureFromPath(link);
		}
		
		
		//Log library start here
		System.out.println("");
        System.out.println("===============================================");
        // store the failure exception
        String errorMessage = result.getThrowable().toString();

        // if you have chosen to record test results to TestRail
        if (BaseTest.postResultsToTestRail)
        {
            ITestNGMethod m = result.getMethod();
            // store the test method name
            String methodName = m.getMethodName();

            // store the test browser name
            String browserName = m.getXmlTest().getParameter("browser");
            String environment = m.getXmlTest().getParameter("environment");

            // get the TestRail Test Run Id from the
            long testRailTestRunId = 0;
            try {
				testRailTestRunId = BaseTest.addTestRunInTestRail();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (APIException e1) {
				e1.printStackTrace();
			}
            // get the TestRail Test Case id from the Test annonation description value
            int[] testCaseIds = returnTestCaseId(result);
            System.out.println("Case IDS = " + testCaseIds);
            // if the Test Case Id and Test Run Id have both been set then try to add the test result to TestRail
            if (testCaseIds != null && testRailTestRunId != 0)
            {
                // the comment that will be added to the Test Run result
                Object commentFromTest = result.getTestContext().getAttribute("Comment");
                if (BaseTest.debugInfo)
                    System.out.println("Comment attribute = " + commentFromTest);

                String additionalComment;
                if (commentFromTest == null)
                {
                    additionalComment = "";
                }
                else
                {
                    additionalComment = "\nAdditional info - " + commentFromTest.toString();
                }
                String testRailComment = "Automated Test - FAIL\n\nTest Method name = " + methodName + "\nBrowser name = " + browserName + "\nEnvironment = " + environment + "\n\nFailure Exception = " + errorMessage + additionalComment;

                try
                {
                    // add the result to TestRail
                    for (int testCaseId : testCaseIds)
                    {
                        System.out.println(testCaseId);
                        String testCaseIdStr = String.valueOf(testCaseId);
                        basetest.addTestCaseResultInTestRail(testRailTestRunId, testCaseIdStr, BaseTest.TEST_CASE_FAILED_STATUS, testRailComment);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                // if the Test Case Id or Test Run Id have not been set correctly record a message to the log
                System.out.println("Test results are not being recorded to TestRail for test " + methodName + " on browser " + browserName);
            }
        }
        System.out.println("===============================================");
		
		
		BaseTest.report.finishTest(result);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		BaseTest.report.startTest(result);
		System.out.println("[TEST-INFO] SKIPPED");
		BaseTest.report.finishTest(result);
	}
	
	public int[] returnTestCaseId(ITestResult result)
    {
        ITestNGMethod testNGMethod = result.getMethod();
        Method method = testNGMethod.getConstructorOrMethod().getMethod();
        TestRail testRailAnnotation = method.getAnnotation(TestRail.class);
        int[] testCaseIds;
        try
        {
            testCaseIds = testRailAnnotation.testCaseId();
        }
        catch (Exception ex)
        {
            return null;
        }
        return testCaseIds;
    }

}
