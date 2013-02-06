/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.exception;

import java.awt.Component;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.CancellationException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.exception.exceptions.BinListDifferentWindowSizeException;
import edu.yu.einstein.genplay.exception.exceptions.InvalidFileTypeException;
import edu.yu.einstein.genplay.exception.exceptions.valueOutOfRangeException.ValueOutOfRangeException;
import edu.yu.einstein.genplay.exception.report.ReportBuilder;


/**
 * Provides a common strategy to handle the exceptions
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ExceptionManager implements UncaughtExceptionHandler {

	/** Yes option: enable feature */
	public static final int NO = 0;
	/** No option: disable feature */
	public static final int YES = 1;

	/** Print the stack trace into the console */
	private int printStackTrace = YES;
	/** Print the report into the console */
	private int printReport = YES;

	private static	ExceptionManager	instance = null;		// unique instance of the singleton

	private final ReportBuilder report;
	private Throwable throwable;


	/**
	 * @return an instance of a {@link ExceptionManager}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static ExceptionManager getInstance() {
		if (instance == null) {
			synchronized(ExceptionManager.class) {
				if (instance == null) {
					instance = new ExceptionManager();
				}
			}
		}
		return instance;
	}


	/**
	 * Constructor of {@link ExceptionManager}
	 */
	private ExceptionManager () {
		report = new ReportBuilder();
	}


	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		handleThrowable(thread, null, throwable, null);
	}


	/**
	 * Handles the exception
	 * @param component a component
	 * @param throwable an exception
	 * @param message error message to display
	 */
	public void handleException(Component component, Throwable throwable, String message) {
		handleThrowable(null, component, throwable, message);
		showAppropriateMessage(component, throwable, message);
	}


	/**
	 * Handles the exception
	 * @param throwable an exception
	 */
	public void handleException(Throwable throwable) {
		handleThrowable(null, null, throwable, null);
	}


	/**
	 * Handle the {@link Throwable} object
	 * @param thread a thread
	 * @param component a component
	 * @param throwable an exception
	 * @param message error message to display
	 */
	private void handleThrowable (Thread thread, Component component, Throwable throwable, String message) {
		this.throwable = throwable;
		report.initializeReport(thread, component, throwable, message);
		processError();
	}


	/**
	 * Process the error workflow
	 */
	private void processError () {
		if (printReport()) {
			System.out.println(report.getReport());
		}

		if (printStackTrace()) {
			throwable.printStackTrace();
		}
	}


	/**
	 * Shows a message associated to the specified exception
	 * @param component a component
	 * @param e exception
	 * @param message default message to print
	 */
	private void showAppropriateMessage(Component component, Throwable e, String message) {
		boolean exceptionHandled = false;
		boolean hasCause = true;
		Throwable exception = e;

		while (!exceptionHandled && hasCause) {
			// no message for the interrupted or cancel exceptions
			if ((exception instanceof InterruptedException) || (exception instanceof CancellationException)) {
				exceptionHandled = true;
			} else if (exception instanceof InvalidFileTypeException) {
				// case where the user tries to load an invalid file type
				JOptionPane.showMessageDialog(component, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				exceptionHandled = true;
			} else if (exception instanceof ValueOutOfRangeException) {
				// case where the value of a binlist is out of the range of the data precision
				JOptionPane.showMessageDialog(component, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				exceptionHandled = true;
			} else if (exception instanceof BinListDifferentWindowSizeException) {
				// case when the user tries to do an operation on 2 binlists with different binsize
				JOptionPane.showMessageDialog(component, exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				exceptionHandled = true;
			}
			if (exception.getCause() != null) {
				exception = exception.getCause();
			} else {
				hasCause = false;
			}
		}
		if (!exceptionHandled) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(component, message, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Shows the error stack track in a dialog box
	 * @param jc a component
	 * @param e an exception
	 */
	public void showStack(JComponent jc, Exception e) {
		String errorMessage = "<html><pre>";
		if (e.getCause() != null) {
			errorMessage += "Caused by: " + e.getCause().toString() + "<br/>";
		}
		for (StackTraceElement currentTrace : e.getStackTrace()) {
			errorMessage += "\tat " + currentTrace.toString() + "<br/>";
		}
		errorMessage += "</pre></html>";
		JOptionPane.showMessageDialog(jc.getRootPane(), errorMessage, "Error Info", JOptionPane.ERROR_MESSAGE);
	}



	/**
	 * @param i an int
	 * @return true if i meets the YES option, false otherwise
	 */
	private boolean intToBool (int i) {
		if (i == YES) {
			return true;
		}
		return false;
	}


	/**
	 * @param b a boolean
	 * @return YES if b is true, NO otherwise
	 */
	private int boolToIn (boolean b) {
		if (b) {
			return YES;
		}
		return NO;
	}

	/**
	 * @return the printStackTrace
	 */
	public boolean printStackTrace() {
		return intToBool(printStackTrace);
	}


	/**
	 * @param printStackTrace the printStackTrace to set
	 */
	public void enablePrintStackTrace(boolean printStackTrace) {
		this.printStackTrace = boolToIn(printStackTrace);
	}


	/**
	 * @return the printReport
	 */
	public boolean printReport() {
		return intToBool(printReport);
	}


	/**
	 * @param printReport the printReport to set
	 */
	public void enablePrintReport(boolean printReport) {
		this.printReport = boolToIn(printReport);
	}

}
