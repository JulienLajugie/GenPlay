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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.projectFrame.newProject.vcf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class manages the multi genome settings.
 * It concerns all association maps loading/saving.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SettingsHandler extends DefaultHandler {


	private List<List<Object>> 	data;	// the data
	private String[] attributeNames;	// the attribute names


	/**
	 * Constructor of {@link SettingsHandler}
	 */
	public SettingsHandler () {
		super();
		data = new ArrayList<List<Object>>();
		this.attributeNames = new String[5];
		this.attributeNames[0] = "group";
		this.attributeNames[1] = "genome";
		this.attributeNames[2] = "type";
		this.attributeNames[3] = "file";
		this.attributeNames[4] = "raw_name";
	}



	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("row")) {
			List<Object> row = new ArrayList<Object>();
			for (String attribute: attributeNames) {
				row.add(attributes.getValue(attribute));
			}
			data.add(row);
		}
	}



	/**
	 * @return the data
	 */
	public List<List<Object>> getData() {
		return data;
	}



	/**
	 * @param data the data to set
	 */
	public void setData(List<List<Object>> data) {
		this.data = data;
	}



	/**
	 * Writes the multi genome setting in a XML file
	 * @param xml a XML file
	 */
	public void write (File xml) {
		try{
			// Create file 
			FileWriter fstream = new FileWriter(xml);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<settings>\n");
			for (List<Object> list: data) {
				out.write("\t<row ");
				for (int i = 0; i < list.size(); i++) {
					out.write(attributeNames[i] + "=\"" + list.get(i).toString() + "\" ");
				}
				out.write("/>\n");
			}
			out.write("</settings>");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}


}
