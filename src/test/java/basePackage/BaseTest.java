package basePackage;

import java.io.File;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import excelUtilities.ExcelUtilities;
import exceptions.FileDoesNotExistsException;

@Listeners(APIReportingUtility.class)
public class BaseTest {
	String testScriptName;
	private int passedTests = 0;
	private int failedTests = 0;
	private int skipedTests = 0;
	public ExcelUtilities excelUtil = new ExcelUtilities();


	public BaseTest() {

	}

	/**
	 * Get Class Name
	 * @return
	 */
	public String getPkgName() {
		String packageName = this.getClass().getPackage().getName().trim();
		System.out.println("Qualified Test Package name : " + packageName);
		return packageName;
	}

	@BeforeSuite(alwaysRun=true)
	public void beforeSuite() throws Throwable {
		//String testPkgName = getPkgName();			
		//TestNg class's part
//		String  path = System.getProperty("user.dir") + "\\testData\\restAssuredTestDriver.xlsx";
//		validateInputFile(path);
		System.setProperty("driverFilePath", ".xlsx");
		System.setProperty("url","https://petstore.swagger.io");
		System.setProperty("AppName Details","Petstore");
		System.setProperty("Environment Details","Test");
		String applicationTitle = "Rest Assured API";
		System.setProperty("Application Title", applicationTitle +" Automation");
		System.setProperty("Report Name",applicationTitle+" AutomationTest-Report");
	}

	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext testcontext) throws Throwable {
		System.out.println("Before Class");
	}

	@BeforeMethod
	public void beforMethod() {
		System.out.println("Before Method");
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod(ITestResult result) {
		testScriptName = result.getMethod().getMethodName();
		System.setProperty("testScriptName", testScriptName);
		try {
			if (result.getStatus() == ITestResult.SUCCESS) {
				++passedTests;
			} else if (result.getStatus() == ITestResult.FAILURE) {
				++failedTests;
			} else if (result.getStatus() == ITestResult.SKIP) {
				++skipedTests;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass(alwaysRun = true)
	public void afterClass() throws Throwable {
		int totalTestCases = excelUtil.getTotalTestCases(passedTests,failedTests,skipedTests);
		double passPercentage = excelUtil.calculatePercentage(passedTests,totalTestCases);
		System.out.println("Pass Percentage : "+passPercentage+" %");
	}

	@AfterSuite(alwaysRun=true)
	public void afterSuite() {
		System.out.println("After Suite");
	}
	
	@SuppressWarnings("unused")
	private void validateInputFile(String path) throws FileDoesNotExistsException {
		File f = new File(path);
		if(!f.exists()) {
			throw new FileDoesNotExistsException("File dmdaTestDriver.xlsx not found under path: "+f.getParentFile());
		}else {
			if (f.length()==0) {
				throw new FileDoesNotExistsException("File dmdaTestDriver.xlsx found under path: "+f.getParentFile() + " is empty");
			}
		}
	}
}
