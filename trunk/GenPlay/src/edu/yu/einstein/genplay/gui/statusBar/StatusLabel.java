/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.statusBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JLabel;

import edu.yu.einstein.genplay.exception.ExceptionManager;


/**
 * Label on the status bar that shows a description of the current Operation
 * and the time elapsed since the operation started
 * @author Julien Lajugie
 */
final class StatusLabel extends JLabel {

	/**
	 * Thread displaying the time elapsed in the progress bar
	 * @author Julien Lajugie
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
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
	}


	private static final long serialVersionUID = 404304422248672368L; // generated ID
	private static final String INIT_TEXT = "Right click on a track number to select actions.";	// welcome text when creating a new project
	private TimeCounter 			timeCounterThread;	// thread showing the time elapsed in the progress bar
	private String 					description;		// description of the current operation
	private long 					timeElapsed = 0;	// time elapsed since the beginning of the operation
	private final SimpleDateFormat 	dateFormat;			// date format for the time elapsed
	private int 					step = 0;			// step of the current operation
	private int						stepCount = 0;		// total number of steps for the operation;


	/**
	 * Creates an instance of {@link StatusLabel}
	 */
	StatusLabel() {
		super();
		// set the date format
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		// the correct elapsed time it has to be adjusted to UTC so that
		// it compensates for the timezone and daylight saving time differences
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		setHorizontalAlignment(JLabel.CENTER);
	}


	/**
	 * @return the description printed on the label without the time
	 */
	String getDescription() {
		return description;
	}


	/**
	 * @return the step
	 */
	int getStep() {
		return step;
	}


	/**
	 * @return the stepCount
	 */
	int getStepCount() {
		return stepCount;
	}


	/**
	 * Set the description label when starting a new project
	 */
	void initDescriptionForFirstUse () {
		setText(INIT_TEXT);
	}


	/**
	 * Erases the text
	 */
	void reinit() {
		setText("");
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
	 * Sets the step
	 * @param step
	 */
	void setStep(int step) {
		this.step = step;
		updateText();
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
}
