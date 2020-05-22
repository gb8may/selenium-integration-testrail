package sample.base;

import org.testng.IExecutionListener;

import sample.base.BaseTest;
import sample.base.ExtentManager;

public class ReportExecutionListener implements IExecutionListener{

	public void onExecutionStart() {
		BaseTest.report = new ExtentManager();
	}

	public void onExecutionFinish() {
		// TODO Auto-generated method stub
	}

}
