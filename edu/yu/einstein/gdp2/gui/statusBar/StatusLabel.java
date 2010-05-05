/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;

import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;


/**
 * Label on the status bar that shows a description of the current Operation 
 * and the time elapsed since the operation started
 * @author Julien Lajugie
 * @version 0.1
 */
public class StatusLabel extends JLabel implements TrackListActionListener {

	private static final long serialVersionUID = 404304422248672368L; // generated ID
	private TimeCounter 			timeCounterThread;	// thread showing the time elapsed in the progress bar
	private String 					description;		// description of the current operation
	private long 					timeElapsed = 0;	// time elapsed since the beginning of the operation
	private final SimpleDateFormat 	dateFormat;			// date format for the time elapsed

	
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
	public StatusLabel() {
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
		setText(description + "  -  " + timeString);
	}
	
	
	/**
	 * Sets the description 
	 * @param description
	 */
	public synchronized void setDescription(String description) {
		this.description = description;
		updateText();
	}
	
	
	/**
	 * @return the description printed on the label without the time
	 */
	public String getDescription() {
		return description;
	}
	
	
	@Override
	public void actionEnds(TrackListActionEvent evt) {
		setDescription(evt.getActionDescription());
		// stop the thread updating the time elapsed
		timeCounterThread = null;
	}

	
	@Override
	public void actionStarts(TrackListActionEvent evt) {
		setDescription(evt.getActionDescription());
		// start the thread updating the time elapsed
		timeCounterThread = new TimeCounter();
		timeCounterThread.start();
	}
}
