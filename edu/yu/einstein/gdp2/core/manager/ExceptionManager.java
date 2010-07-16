/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.manager;

import java.util.concurrent.CancellationException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
import yu.einstein.gdp2.exception.valueOutOfRangeException.ValueOutOfRangeException;

/**
 * Provides a common strategy to handle the exceptions
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ExceptionManager {


	/**
	 * Handles the exception
	 * @param jc a component
	 * @param e an exception
	 * @param message error message to display.
	 */
	public static void handleException(JComponent jc, Exception e, String message) {
		showAppropriateMessage(jc, e, message);
	}


	private static void showAppropriateMessage(JComponent jc, Exception e, String message) {
		boolean exceptionHandled = false;
		boolean hasCause = true;
		Throwable exception = e;

		while (!exceptionHandled && hasCause) {
			// no message for the interrupted or cancel exceptions 
			if ((exception instanceof InterruptedException) || (exception instanceof CancellationException)) { 
				exceptionHandled = true;	
			} else if (exception instanceof InvalidFileTypeException) {				
				// case where the user tries to load an invalid file type
				JOptionPane.showMessageDialog(jc.getRootPane(), exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				exceptionHandled = true;
			} else if (exception instanceof ValueOutOfRangeException) {
				// case where the value of a binlist is out of the range of the data precision
				JOptionPane.showMessageDialog(jc.getRootPane(), exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				exceptionHandled = true;
			} else if (exception instanceof BinListDifferentWindowSizeException) {
				// case when the user tries to do an operation on 2 binlists with different binsize
				JOptionPane.showMessageDialog(jc.getRootPane(), exception.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(jc.getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
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
