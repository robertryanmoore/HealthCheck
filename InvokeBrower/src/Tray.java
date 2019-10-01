import static org.quartz.JobBuilder.newJob;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;


public class Tray {

	public static void main(String args[]) throws SchedulerException {
		tray();
		timing();
	}

	public static void tray() {
		TrayIcon trayIcon = null;

		if (SystemTray.isSupported()) {

			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();

			
			
			// load an image
			Image image = Toolkit.getDefaultToolkit()
					.getImage("logo.gif");
			

			// create a action listener to listen for default action executed on
			// the tray icon
			ActionListener listener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Chrome healthCheck = new Chrome();
					healthCheck.healthCheck();
				}
			};

			ActionListener exitAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			};

			// create a popup menu
			PopupMenu popup = new PopupMenu();

			// create menu item for the default action
			MenuItem defaultItem = new MenuItem("Run health check");
			defaultItem.addActionListener(listener);
			popup.add(defaultItem);

			// exit option
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(exitAction);
			popup.add(exitItem);

			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Health Check", popup);
			trayIcon.setImageAutoSize(true);
			// set the TrayIcon properties
			trayIcon.addActionListener(listener);
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}

		}

	}

	public static void timing() throws SchedulerException{
		//Timing
				SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();

				Scheduler sched = schedFact.getScheduler();

				sched.start();

				// define the job and tie it to our HelloJob class
				JobDetail job = newJob(Chrome.class).withIdentity("myJob", "group1").build();
				
				//set up triggers 
				CronTrigger morningTrigger = TriggerBuilder.newTrigger()
						  .withIdentity("morningTrigger", "group1")
						  .withSchedule(CronScheduleBuilder.cronSchedule("0 45 8 ? * MON-FRI"))
						  .forJob("myJob", "group1")
						  .build();
				
				CronTrigger middayTrigger = TriggerBuilder.newTrigger()
						  .withIdentity("middayTrigger", "group1")
						  .withSchedule(CronScheduleBuilder.cronSchedule("0 0 12 ? * MON-FRI"))
						  .forJob("myJob", "group1")
						  .build();
				
				CronTrigger afternoonTrigger = TriggerBuilder.newTrigger()
						  .withIdentity("afternoonTrigger", "group1")
						  .withSchedule(CronScheduleBuilder.cronSchedule("0 20 15 ? * MON-THU"))
						  .forJob("myJob", "group1")
						  .build();
				
				CronTrigger fridayTrigger = TriggerBuilder.newTrigger()
						  .withIdentity("fridayTrigger", "group1")
						  .withSchedule(CronScheduleBuilder.cronSchedule("0 50 14 ? * FRI"))
						  .forJob("myJob", "group1")
						  .build();
												
				// Tell quartz to schedule the job using our trigger
				sched.addJob(job, true,true);
			    sched.scheduleJob(morningTrigger);
			    sched.scheduleJob(middayTrigger);
			    sched.scheduleJob(afternoonTrigger);
			    sched.scheduleJob(fridayTrigger);
			    
	}
}
