package sample.tests;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import sample.base.BaseTest;
import sample.pages.SimplePage;
import sample.utility.testrail.APIException;
import sample.utility.testrail.TestRail;

public class SimplePageTest extends BaseTest {
	SimplePage page;
	long testRailRunId;
	
	@BeforeClass
	public void setUP() throws IOException, APIException {
		page = new SimplePage();
		page.goToPage();
	}
	
//	// @Test
//     public void testAPI() throws Exception {
//        String testRailCase = BaseTest.getCase(1).toString();
//        System.out.println(testRailCase);
//    }
	
	@Test
	@TestRail(testCaseId = {1})
	public void test() throws IOException, APIException, InterruptedException {
			 page.login();
		
	}
	
	@Test
	@TestRail(testCaseId = {2})
	public void test2() throws IOException, APIException, InterruptedException {
//			 page.login();
		System.out.println("Teste 3 ");
		
	}
}
