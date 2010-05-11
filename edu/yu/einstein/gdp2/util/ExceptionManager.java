/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

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
	
	
	private static void showAppropriateMessage(JComponent jc, Exception e, String message) {
		// no message for the interrupted exception 
		if (!(e instanceof InterruptedException) && !(e.getCause() instanceof InterruptedException)) {			
			if (e instanceof InvalidFileTypeException) {				
				// case where the user tries to load an invalid file type
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} else if (e.getCause() instanceof InvalidFileTypeException) {
				// case where the user tries to load an invalid file type
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} else if (e instanceof ValueOutOfRangeException) {
				// case where the value of a binlist is out of the range of the data precision
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} else if (e.getCause() instanceof ValueOutOfRangeException) {
				// case where the value of a binlist is out of the range of the data precision
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);			
			} else if (e instanceof BinListDifferentWindowSizeException) {
				// case when the user tries to do an operation on 2 binlists with different binsize
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} else if (e.getCause() instanceof BinListDifferentWindowSizeException) {
				// case when the user tries to do an operation on 2 binlists with different binsize
				JOptionPane.showMessageDialog(jc.getRootPane(), e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} else {
				e.printStackTrace();
				JOptionPane.showMessageDialog(jc.getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
