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
package edu.yu.einstein.genplay.core.DAS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class allows writing the DAS Server List
 * to a file. 
 * @author Chirag Gorasia
 * @version 0.1
 */
public class DASServerListWriter {

	/**
	 * @param tableData of type Object[][]
	 * @param fileName of type String
	 * @throws IOException 
	 */
	public void write(Object[][] tableData, String fileName) throws IOException {
		BufferedWriter writer = null;
		//System.out.println("File Path: " + fileName);
		try {
			// try to create a output file
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			// print the title of the graph
			writer.write("<DASLIST>");
			writer.newLine();
			for (int i = 0; i < tableData.length; i++) {
				writer.write("<SERVER href=" + "\"" + tableData[i][1] + "\" name=" + "\"" + tableData[i][0] + "\"/>");
				writer.newLine();
			}
			writer.write("</DASLIST>");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}

