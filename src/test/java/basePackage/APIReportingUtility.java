package basePackage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;

public class APIReportingUtility extends TestListenerAdapter {

	public ExtentSparkReporter sparkReporter;
	public ExtentReports extent; 
	String htmlReportName;

	private static ThreadLocal<ExtentTest> methodTest = new ThreadLocal<ExtentTest>();
	private static ThreadLocal<ExtentTest> dataProviderTest = new ThreadLocal<>();

	public String currentDateFormat(String dateFormat) {
		LocalDateTime currentDateTime = LocalDateTime.now();  
		DateTimeFormatter format1 = DateTimeFormatter.ofPattern(dateFormat);  
		String formatDateTime = currentDateTime.format(format1).toString(); 
		return formatDateTime;
	}

	public void onStart(ITestContext testContext) {
		String formatDateTime = currentDateFormat("dd-MMM-yyyy");
		String timeStamp = new SimpleDateFormat("dd_MMM_yyyy-k_mm_ss a").format(new Date());// Time Stamp
		String nameOfReportRequired = System.getProperty("Report Name");//Report Name : needs to be set
		String reportName = nameOfReportRequired+" "+ timeStamp + ".html";
		String  outputFolder = "\\Reports\\"+formatDateTime+"\\";
		Path path = Paths.get(System.getProperty("user.dir") + outputFolder);

		if(!Files.exists(path)) {	
			try {
				Files.createDirectories(path);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

		htmlReportName = System.getProperty("user.dir") +outputFolder+reportName;
		sparkReporter =  new ExtentSparkReporter(htmlReportName).viewConfigurer().viewOrder().as(new ViewName[] { ViewName.TEST,ViewName.DASHBOARD, ViewName.DEVICE, ViewName.LOG,ViewName.EXCEPTION,ViewName.AUTHOR}).apply();

		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		extent.setSystemInfo("User Name", System.getProperty("user.name"));
		extent.setSystemInfo("Application",System.getProperty("AppName Details"));//Application Name : needs to be set
		extent.setSystemInfo("Environment", System.getProperty("Environment Details"));//Environment Details : needs to be set
		extent.setSystemInfo("URL", System.getProperty("url"));
		extent.setSystemInfo("OS", System.getProperty("os.name"));
		extent.setSystemInfo("OS Version", System.getProperty("os.version"));
		extent.setSystemInfo("OS Arch", System.getProperty("os.arch"));

		sparkReporter.config().setCss("css-string");
		sparkReporter.config().setDocumentTitle(System.getProperty("Application Title"));//Application Title : needs to be set
		sparkReporter.config().setReportName(System.getProperty("Report Name"));//Report Name : needs to be set
		sparkReporter.config().setTheme(Theme.DARK);  //Theme
		sparkReporter.config().setTimelineEnabled(true);
		sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss a");
	}

	public void onTestStart(ITestResult result) {    
		String methodName = result.getMethod().getMethodName();
		if (result.getParameters().length>0) {
			if (methodTest.get() != null && methodTest.get().getModel().getName().equals(methodName)) { } 
			else {
				createTest(result);
			}
			String paramName = Arrays.asList(result.getParameters()).toString();
			ExtentTest paramTest = methodTest.get().createNode(paramName);
			dataProviderTest.set(paramTest);
		} else {
			createTest(result);
		}
	}

	private void createTest(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		ExtentTest test = extent.createTest(methodName);
		methodTest.set(test);
		String[] groups = result.getMethod().getGroups();
		if (groups.length > 0) {
			Arrays.asList(groups)
			.forEach(x -> methodTest.get().assignCategory(x));
		}
	}

	private ExtentTest getTest(ITestResult result) {
		ExtentTest t = result.getParameters() != null && result.getParameters().length>0
				? dataProviderTest.get()
						: methodTest.get();
		return t;
	}

	public void onTestSuccess(ITestResult result) {	
		getTest(result).pass(MarkupHelper.createLabel(result.getName()+ " - Test Case Passed", ExtentColor.GREEN));
		getTest(result).pass("Test passed");
	}

	public void onTestFailure(ITestResult result ) {
		getTest(result).fail(MarkupHelper.createLabel(result.getName()+ " - Test Case Failed", ExtentColor.RED));
		getTest(result).fail(result.getThrowable());
	}

	public void onTestSkipped(ITestResult result) {
		getTest(result).skip(MarkupHelper.createLabel(result.getName()+" - Test Case Skipped", ExtentColor.ORANGE));
		getTest(result).skip(result.getThrowable());
	}

	public void onFinish(ITestContext textContext) {
		extent.flush();
		methodTest.remove();
		try { 
			// Preload report on default browser of the system
			Desktop.getDesktop().browse(new File(htmlReportName).toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}