package sample.pages;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import sample.base.BaseTest;
import sample.utility.testrail.APIException;

public class SimplePage extends BaseTest {
	private WebDriverWait wait;
	
	public void goToPage() {
		wait = new WebDriverWait(driver, BaseTest.WAIT_TIME);
		System.out.println(" Driver = " + driver);
		System.out.println("Passei Aqui: " + BaseTest.currentBaseUrl + " ++++++++++++++++++++++++++++++ ");
		driver.get(BaseTest.currentBaseUrl);
	}
	
	public void login() throws InterruptedException, AssertionError, IOException  {
		System.out.println("Teste de página");
	}
}
