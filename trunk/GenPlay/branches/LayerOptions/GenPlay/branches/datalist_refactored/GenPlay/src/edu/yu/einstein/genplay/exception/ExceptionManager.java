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
import java.util.concurrent.CancellationException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.exception.valueOutOfRangeException.ValueOutOfRangeException;


/**
 * Provides a common strategy to handle the exceptions
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExceptionManager {


	/**
	 * Handles the exception
	 * @param component a component
	 * @param e an exception
	 * @param message error message to display.
	 */
	public static void handleException(Component component, Exception e, String message) {
		showAppropriateMessage(component, e, message);
	}


	/**
	 * Shows a message associated to the specified exception
	 * @param component a component
	 * @param e exception
	 * @param message default message to print 
	 */
	private static void showAppropriateMessage(Component component, Exception e, String message) {
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
	public static void showStack(JComponent jc, Exception e) {
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
}
