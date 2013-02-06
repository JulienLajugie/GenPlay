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
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReportBuilder {

	protected final static String DEFAULT_VALUE = "unknown";
	private static int REPORT_COUNT = 0;

	private final ReportHeaderBuilder headerBuilder;
	private final ReportExceptionBuilder exceptionBuilder;

	private String report;


	/**
	 * Constructor of {@link ReportBuilder}
	 */
	public ReportBuilder () {
		headerBuilder = new ReportHeaderBuilder();
		exceptionBuilder = new ReportExceptionBuilder();
	}


	/**
	 * Initializes the report
	 * @param thread a thread
	 * @param component a component
	 * @param throwable an exception
	 * @param message error message to display
	 */
	public void initializeReport (Thread thread, Component component, Throwable throwable, String message) {
		REPORT_COUNT++;
		headerBuilder.initializeReport();
		if (thread != null) {
			exceptionBuilder.initializeReport(thread, throwable);
		} else if (component != null) {
			exceptionBuilder.initializeReport(component, throwable, message);
		} else if (throwable != null) {
			exceptionBuilder.initializeReport(throwable);
		}

		report = "== REPORT #" + REPORT_COUNT + "\n";
		report += "== REPORT HEADER\n";
		report += headerBuilder.getReport();
		report += "\n\n";
		report += "== REPORT EXCEPTION CONTENT\n";
		report += exceptionBuilder.getReport();
	}


	/**
	 * @return the report
	 */
	public String getReport () {
		return report;
	}


	/**
	 * @param title a title
	 * @return the formatted title
	 */
	protected static String getTitle (String title) {
		return "" + title + "\n";
	}


	/**
	 * @param title a title
	 * @param value a value for the title
	 * @return a formatted title/value string
	 */
	protected static String getInformation (String title, Object value) {
		if (value == null) {
			return "";
		}
		return ("- " + title + ": " + value.toString() + "\n");
	}

}