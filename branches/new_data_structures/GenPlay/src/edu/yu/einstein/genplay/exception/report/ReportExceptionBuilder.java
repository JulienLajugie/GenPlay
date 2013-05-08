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
package edu.yu.einstein.genplay.exception.report;

import java.awt.Component;

/**
 * The {@link ReportExceptionBuilder} generates the exception report.
 * For now, the content contains information about:
 * - the thread (optional)
 * - the throwable
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReportExceptionBuilder {

	private String report;

	/**
	 * @param throwable a trhowable (exception)
	 * @return the throwable report
	 */
	private String getLowerLayerThrowableReports (Throwable throwable) {
		String report = null;
		int count = 0;
		Throwable cause = throwable.getCause();
		while (cause != null) {
			count++;
			String currenReport = getThrowableReport(cause, count);
			if (report == null) {
				report = currenReport;
			} else {
				report += "\n" + currenReport;
			}
			cause = cause.getCause();
		}
		return report;
	}


	/**
	 * @param message a message
	 * @return the message report
	 */
	private String getMessageReport (String message) {
		String report = "";

		report += ReportBuilder.getTitle("Message");
		report += ReportBuilder.getInformation("Value", message);

		return report;
	}


	/**
	 * @return the report
	 */
	public String getReport() {
		return report;
	}


	/**
	 * @param thread a thread
	 * @return the thread report
	 */
	@SuppressWarnings("unused") // Not necessary, keep it just in case we need it later.
	private String getThreadReport (Thread thread) {
		String report = "";

		report += ReportBuilder.getTitle("Thread");

		report += ReportBuilder.getInformation("Name", thread.getName());
		report += ReportBuilder.getInformation("Priority", thread.getPriority());
		report += ReportBuilder.getInformation("Priority", thread.getState());
		report += ReportBuilder.getInformation("Is alive", thread.isAlive());
		report += ReportBuilder.getInformation("Is interrupted", thread.isInterrupted());
		report += ReportBuilder.getInformation("Stack trace", null);

		StackTraceElement[] trace = thread.getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			report += (i + 1) + ": " + trace[i] + "\n";
		}

		return report;
	}


	/**
	 * @param throwable a trhowable (exception)
	 * @return the throwable report
	 */
	private String getThrowableReport (Throwable throwable, int count) {
		if (throwable != null) {
			String report = "";

			report += ReportBuilder.getTitle("Throwable (" + count + ")");
			report += ReportBuilder.getInformation("Class", throwable.getClass());
			report += ReportBuilder.getInformation("Cause", throwable.getCause());
			report += ReportBuilder.getInformation("Message", throwable.getMessage());
			report += ReportBuilder.getInformation("Localized message", throwable.getLocalizedMessage());
			report += ReportBuilder.getInformation("Stack trace", null);

			StackTraceElement[] trace = throwable.getStackTrace();
			for (int i = 0; i < trace.length; i++) {
				report += (i + 1) + ": " + trace[i];
				if (i < (trace.length - 1)) {
					report += "\n";
				}
			}

			return report;
		}
		return null;
	}


	/**
	 * Initializes the report
	 * @param component a component
	 * @param throwable an exception
	 * @param message error message to display.
	 */
	protected void initializeReport (Component component, Throwable throwable, String message) {
		report += getMessageReport(message) + "\n";
		report += getThrowableReport(throwable, 0);
		String lowerLayerThrowableReports = getLowerLayerThrowableReports(throwable);
		if (lowerLayerThrowableReports != null) {
			report += getLowerLayerThrowableReports(throwable);
		}
	}


	/**
	 * Initializes the report
	 * @param thread a thread
	 * @param throwable a throwable
	 */
	protected void initializeReport (Thread thread, Throwable throwable) {
		//report = getThreadReport(thread) + "\n";
		report = getThrowableReport(throwable, 0);
		String lowerLayerThrowableReports = getLowerLayerThrowableReports(throwable);
		if (lowerLayerThrowableReports != null) {
			report += getLowerLayerThrowableReports(throwable);
		}
	}


	/**
	 * Initializes the report
	 * @param throwable a throwable
	 */
	protected void initializeReport (Throwable throwable) {
		report = getThrowableReport(throwable, 0);
		String lowerLayerThrowableReports = getLowerLayerThrowableReports(throwable);
		if (lowerLayerThrowableReports != null) {
			report += getLowerLayerThrowableReports(throwable);
		}
	}

}
