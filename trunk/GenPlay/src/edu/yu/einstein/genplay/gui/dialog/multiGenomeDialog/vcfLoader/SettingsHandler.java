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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class manages the multi genome import/export settings.
 * It concerns all association maps loading/saving.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SettingsHandler extends DefaultHandler {

	private final static String GROUP_FIELD = "group";
	private final static String NICKNAME_FIELD = "genome";
	private final static String FILE_FIELD = "file";
	private final static String RAW_FIELD = "raw_name";

	private final File		file;	// the file
	private List<VCFData> 	data;	// the data

	/**
	 * Constructor of {@link SettingsHandler}
	 * @param file the file
	 */
	public SettingsHandler (File file) {
		super();
		this.file = file;
		data = new ArrayList<VCFData>();
	}


	/**
	 * @return the data
	 */
	public List<VCFData> getData() {
		return data;
	}



	/**
	 * @param data the data to set
	 */
	public void setData(List<VCFData> data) {
		this.data = data;
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("row")) {
			String group = attributes.getValue(GROUP_FIELD);
			String genome = attributes.getValue(NICKNAME_FIELD);
			String path = attributes.getValue(FILE_FIELD);
			String raw = attributes.getValue(RAW_FIELD);
			if ((path.length() > 2) && path.startsWith(".\\")) {
				path = file.getParent() + path.substring(1);
			}
			VCFData vcfData = new VCFData(group, genome, new File(path), raw);
			data.add(vcfData);
		}
	}


	/**
	 * Writes the multi genome setting in a XML file
	 */
	public void write () {
		try{
			// Create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<settings>\n");
			for (VCFData vcfData: data) {
				out.write("\t<row ");
				out.write(GROUP_FIELD + "=\"" + vcfData.getGroup() + "\" ");
				out.write(NICKNAME_FIELD + "=\"" + vcfData.getNickname() + "\" ");
				out.write(FILE_FIELD + "=\"" + vcfData.getFile() + "\" ");
				out.write(RAW_FIELD + "=\"" + vcfData.getRaw() + "\" ");
				out.write("/>\n");
			}
			out.write("</settings>");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}


	/*private String getFormattedString (String s) {
		return s.toLowerCase().replace(' ', '_');
	}*/

}
