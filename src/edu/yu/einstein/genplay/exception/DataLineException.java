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

import java.awt.FontMetrics;
import java.io.File;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;



/**
 * The InvalidDataLineException class is thrown when an extractor can't extract a line. 
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class DataLineException extends Exception {

	private static final long serialVersionUID = 7000180996789501289L;	// generated ID
	
	/** Comment for an invalid number of parameters in a line */
	public static final String INVALID_PARAMETER_NUMBER 	= "Invalid number of parameters.";
	/** Comment for an invalid format number in a line */
	public static final String INVALID_FORMAT_NUMBER 		= "The error may occured because of an invalid format number.";
	
	/** Comment when a data has been skipped */
	public static final String SKIP_PROCESS = "The data has been skipped.";
	/** Comment when a stop position has been modified to fit the chromosome length */
	public static final String SHRINK_STOP_PROCESS = "The stop position has been shrinked to fit the chromosome length.";
	
	private final 	String 	description;	// Description of the error
	private final 	String 	process;	// Description of the error
	private 		File 	file;			// File where the error happened
	private			String	line;			// Original line where the error happened
	private 		int 	lineNumber;		// Line of the file where the error happened
	
	
	/**
	 * Creates an instance of {@link DataLineException}
	 * @param description description of the error
	 */
	public DataLineException(String description) {
		super();
		this.description = description;
		this.process = SKIP_PROCESS;
	}
	
	
	/**
	 * Creates an instance of {@link DataLineException}
	 * @param description 	description of the error
	 * @param process 		defines how the data has been processed
	 */
	public DataLineException(String description, String process) {
		super();
		this.description = description;
		this.process = process;
	}

	
	@Override
	public String getMessage() {
		String message = "";
		String defaultIndentPattern = "Description: ";
		
		message += "Error in the file " + file.getPath() + " at the line " + lineNumber + "\n";
		message += "Treatment: " + process + "\n";
		message += "Description: ";
		String[] array = description.split("\n");
		for (int i = 0; i < array.length; i++) {
			switch (i) {
			case 0:
				break;
			default:
				message += getIndent(defaultIndentPattern);
				break;
			}
			message += array[i] + "\n";
		}
		message += getAdjustedString(defaultIndentPattern, "Line:") + line + "\n";

		return message;
	}
	
	
	/**
	 * Creates a white space as long as the pattern.
	 * @param pattern	the pattern
	 * @return			the white space
	 */
	private String getIndent (String pattern) {
		String indent = "";
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		int patternLength = fm.stringWidth(pattern);
		int indentLength = fm.stringWidth(indent);
		while (indentLength < patternLength) {
			indent += " ";
			indentLength = fm.stringWidth(indent);
		}
		return indent;
	}
	
	
	/**
	 * Adds to a text a white space in order to create a final text as long as the pattern.
	 * @param pattern	the pattern
	 * @param suffix	the text
	 * @return			the text with white spaces
	 */
	private String getAdjustedString (String pattern, String text) {
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());
		int patternLength = fm.stringWidth(pattern);
		int textLength = fm.stringWidth(text);
		while (textLength < patternLength) {
			text += " ";
			textLength = fm.stringWidth(text);
		}
		return text;
	}
	

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}


	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}


	/**
	 * @param lineNumber the lineNumber to set
	 */
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @return the line
	 */
	public String getLine() {
		return line;
	}


	/**
	 * @param line the line to set
	 */
	public void setLine(String line) {
		this.line = line;
	}
	
}
