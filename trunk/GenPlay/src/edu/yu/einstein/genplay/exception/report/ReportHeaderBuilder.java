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
package edu.yu.einstein.genplay.exception.report;

import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * The ReportHeaderBuilder generates the header report.
 * For now, the header contains information about:
 * - GenPlay
 * - Java
 * - Memory usage
 * - System
 * 
 * @author Nicolas Fourel
 */
class ReportHeaderBuilder {

	private String report;

	/**
	 * @return the GenPlay report
	 */
	private String getGenPlayReport () {
		String report = "";

		report += ReportBuilder.getTitle("GenPlay");

		report += ReportBuilder.getInformation("Version", MainFrame.GENPLAY_VERSION);

		String look = null;
		try {
			look = ConfigurationManager.getInstance().getLookAndFeel();
		} catch (Exception e) {}

		if (look != null) {
			report += ReportBuilder.getInformation("Look & Feel", look);
		}

		return report;
	}


	/**
	 * @return the Java report
	 */
	private String getJavaReport () {
		String report = "";

		report += ReportBuilder.getTitle("Java");

		report += ReportBuilder.getInformation("JRE version", getProperty("java.version"));
		report += ReportBuilder.getInformation("JRE vendor", getProperty("java.vendor"));

		report += ReportBuilder.getInformation("JRE specification version", getProperty("java.specification.version"));
		report += ReportBuilder.getInformation("JRE specification vendor", getProperty("java.specification.vendor"));
		report += ReportBuilder.getInformation("JRE specification name", getProperty("java.specification.name"));

		report += ReportBuilder.getInformation("JVM specification version", getProperty("java.vm.specification.version"));
		report += ReportBuilder.getInformation("JVM specification vendor", getProperty("java.vm.specification.vendor"));
		report += ReportBuilder.getInformation("JVM specification name", getProperty("java.vm.specification.name"));

		report += ReportBuilder.getInformation("JVM implementation version", getProperty("java.vm.version"));
		report += ReportBuilder.getInformation("JVM implementation vendor", getProperty("java.vm.vendor"));
		report += ReportBuilder.getInformation("JVM implementation name", getProperty("java.vm.name"));

		report += ReportBuilder.getInformation("Compiler", getProperty("java.compiler"));

		return report;
	}


	/**
	 * @return the memory report
	 */
	private String getMemoryReport () {
		String report = "";

		long byteToMBFactor = 1048576;
		Runtime runTime = Runtime.getRuntime();
		long maxMemory =  runTime.maxMemory() / byteToMBFactor;
		long usedMemory = (runTime.totalMemory() - runTime.freeMemory()) / byteToMBFactor;

		report += ReportBuilder.getTitle("Memory");

		report += ReportBuilder.getInformation("Max (MB)", maxMemory);
		report += ReportBuilder.getInformation("Used (MB)", usedMemory);

		return report;
	}


	/**
	 * @param property a system property key
	 * @return a system property
	 */
	private String getProperty (String property) {
		return System.getProperty(property, ReportBuilder.DEFAULT_VALUE);
	}


	/**
	 * @return the header
	 */
	public String getReport() {
		return report;
	}


	/**
	 * @return the system report
	 */
	private String getSystemReport () {
		String report = "";

		report += ReportBuilder.getTitle("System");

		report += ReportBuilder.getInformation("Name", getProperty("os.name"));
		report += ReportBuilder.getInformation("Architecture", getProperty("os.arch"));
		report += ReportBuilder.getInformation("Version", getProperty("os.version"));

		report += ReportBuilder.getInformation("File separator", stringToASCII(getProperty("file.separator")));
		report += ReportBuilder.getInformation("Path separator", stringToASCII(getProperty("path.separator")));
		report += ReportBuilder.getInformation("Line separator", stringToASCII(getProperty("line.separator")));

		return report;
	}


	/**
	 * Initialize the header report
	 */
	protected void initializeReport () {
		report = getGenPlayReport() + "\n";
		report += getJavaReport() + "\n";
		report += getMemoryReport() + "\n";
		report += getSystemReport();
	}


	/**
	 * @param s
	 * @return the ASCII code of the given string
	 */
	private String stringToASCII (String s) {
		if ((s == null) || (s.length() == 0)) {
			return ReportBuilder.DEFAULT_VALUE;
		}

		String result = "";
		byte[] bytes = s.getBytes();
		for (int i = 0; i < bytes.length; i++) {
			result += bytes[i];
			if (i < (bytes.length - 1)) {
				result += " ";
			}
		}
		return result;
	}

}
