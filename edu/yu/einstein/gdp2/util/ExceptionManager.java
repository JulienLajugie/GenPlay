/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.util;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

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
		JOptionPane.showMessageDialog(jc.getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
		showStack(jc, e);
		e.printStackTrace();
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
