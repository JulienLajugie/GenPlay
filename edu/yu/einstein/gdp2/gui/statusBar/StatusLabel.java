/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;


/**
 * Label on the status bar that shows a description of the current Operation 
 * and the time elapsed since the operation started
 * @author Julien Lajugie
 * @version 0.1
 */
final class StatusLabel extends JLabel {

	private static final long serialVersionUID = 404304422248672368L; // generated ID
	private TimeCounter 			timeCounterThread;	// thread showing the time elapsed in the progress bar
	private String 					description;		// description of the current operation
	private long 					timeElapsed = 0;	// time elapsed since the beginning of the operation
	private final SimpleDateFormat 	dateFormat;			// date format for the time elapsed
	private int 					step = 0;			// step of the current operation
	private int						stepCount = 0;		// total number of steps for the operation
	
	
	/**
	 * Thread displaying the time elapsed in the progress bar
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class TimeCounter extends Thread {
		@Override
		public synchronized void run() {
			long startTime = System.currentTimeMillis();
			Thread thisThread = Thread.currentThread();
			while (timeCounterThread == thisThread) {
				try {
					timeElapsed = System.currentTimeMillis() - startTime;
					updateText();
					sleep(1000);		
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	
	/**
	 * Creates an instance of {@link StatusLabel}
	 */
	StatusLabel() {
		super();
		// set the date format
		dateFormat = new SimpleDateFormat("mm:ss");
		// the correct elapsed time it has to be adjusted to UTC so that 
		// it compensates for the timezone and daylight saving time differences
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	
	/**
	 * Updates the text of the label
	 */
	private synchronized void updateText() {		
		String timeString = new String(dateFormat.format(new Date(timeElapsed)));
		if (stepCount == 1) {
			// we don't show the step information if the operation is done in 1 step
			setText(description + "  -  " + timeString);
		} else {
			setText(description + "  (" + step + " / " + stepCount + ")  -  " + timeString);
		}
	}
	
	
	/**
	 * Sets the description 
	 * @param description
	 */
	void setDescription(String description) {
		this.description = description;
		updateText();
	}
	
	
	/**
	 * @return the description printed on the label without the time
	 */
	String getDescription() {
		return description;
	}
	
	
	/**
	 * Sets the step
	 * @param step
	 */
	void setStep(int step) {
		this.step = step;
		updateText();
	}
	
	
	/**
	 * @return the step
	 */
	int getStep() {
		return step;
	}
	

	/**
	 * Sets the step count
	 * @param stepCount
	 */
	void setStepCount(int stepCount) {
		this.stepCount = stepCount;
		updateText();
	}
	
	
	/**
	 * @return the stepCount
	 */
	int getStepCount() {
		return stepCount;
	}
	
	
	/**
	 * Starts the time counter
	 */
	void startCounter() {
		timeCounterThread = new TimeCounter();
		timeCounterThread.start();
	}
	
	
	/**
	 * Stops the time counter
	 */
	void stopCounter() {
		timeCounterThread = null;
		updateText();
	}
}
