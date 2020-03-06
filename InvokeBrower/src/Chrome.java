import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Chrome implements Job{
	
	private boolean popups = true;
	
	private boolean findTechError(WebDriver driver, List<String> message){
				
		if (driver.getPageSource().contains("<strong>More details about the error</strong>")) {
			
			int errorStart = driver.getPageSource().lastIndexOf("ml15 noMarginBottom")+21;
			String errorReference = driver.getPageSource().substring(errorStart, errorStart+8); 
			
			Object[] options = {"OK", "Copy"};
			
			//show JOptionPane with options of "Ok" and Copy
			int result = joptionsPaneBulder("Tech erro: "+errorReference, "Tech Error", options, popups);
			
			
			// copy text to clipboard
			if (result == 1) {
				StringSelection stringSelection = new StringSelection(errorReference);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
			
			message.add("tech error: "+errorReference);
			
			Logger logger = new Logger();
			logger.log(message);
			
			driver.close();
			return true;
			
		}else{
			return false;
		}
		
	}
	
	public void healthCheck(){
		Logger logger = new Logger();
		List<String> message = new ArrayList<String>();

		// Tell the program where to find the Chrome driver
		System.setProperty("webdriver.chrome.driver", "../chromedriver.exe");		
		
		WebDriver driver = new ChromeDriver(new ChromeOptions().addArguments("--start-maximized"));
		
		// load up the site
		driver.get(
				"url"  //url removed for security
				);

		// Login
		try {
			// inject the username and password
			driver.findElement(By.id("username")).sendKeys("username"); //test account username removed for securirty
			driver.findElement(By.id("LoginPassword")).sendKeys("Password"); //password removed for secuirty

			driver.findElement(By.id("login")).click(); // click the login button
														
		} catch (Exception e) {
			message.add("Unable to login");
			joptionPaneBulder(message.get(message.size()-1), "", popups);
			logger.log(message);
			driver.close();
			return;
		}
		
		message.add("Login successful");
		
		// check for tech error after log in
		if(findTechError(driver, message)){
			return;
		}

		// try to click on myport
		try {
			// goes to myport
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",driver.findElement(By.cssSelector("a[href='removed for security']")));

		} catch (Exception e) {
			message.add("Unable to click through to MyPortfolio");
			joptionPaneBulder(message.get(message.size()-1), "", popups);
			logger.log(message);
			driver.close();
			return;
		}
		
		if(findTechError(driver, message)){
			return;
		}
		message.add("My Portfolio loaded successfully");

		// log out
		
		try {
			driver.findElement(By.linkText("LOGOUT")).click();
		} catch (Exception e) {

			message.add("Unable to click on logout");
			joptionPaneBulder(message.get(message.size()-1), "", popups);
			logger.log(message);
			driver.close();
			return;
		}

		boolean pageChanged = false;
		
		//waits for the next page to load
		while (pageChanged == false) {

			if (!driver.getPageSource().contains("Your Car & Home Insurance")) {
				pageChanged = true;
			}
		}
			
		if (driver.getPageSource().contains("You have been <strong>logged out</strong>")) {
			
			message.add("IAM QA health check completed successfully"); 
			joptionPaneBulder(message.get(message.size()-1), "", popups);
		} else {
		
			message.add("Error logging out");
			joptionPaneBulder(message.get(message.size()-1), "Error", popups);
			System.out.println(message.get(message.size()-1));
		}

		logger.log(message);
		driver.close();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		healthCheck();
		
	}
	
	private void joptionPaneBulder(String message, String title,boolean popup){
		
		JOptionPane pane = new JOptionPane();
		pane.setMessage(message);
		pane.setMessageType(0);
		JDialog dialog = pane.createDialog(title);
		dialog.setAlwaysOnTop(true);  
		dialog.setVisible(true);
		
	}
	
	private int joptionsPaneBulder(String message, String title, Object options[],boolean popup){
		if(popup){
		JOptionPane pane = new JOptionPane();
		pane.setMessage(message);
		pane.setMessageType(0);
		JDialog dialog = pane.createDialog(title);
		dialog.setAlwaysOnTop(true);  
		return JOptionPane.showOptionDialog(dialog, message, title, 0, 1, null,options, 0);
		}else{
			return 2;
		}
	}
	
}
	
