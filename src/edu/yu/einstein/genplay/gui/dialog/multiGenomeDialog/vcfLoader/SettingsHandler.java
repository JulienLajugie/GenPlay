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


	private List<VCFData> 	data;	// the data

	/**
	 * Constructor of {@link SettingsHandler}
	 */
	public SettingsHandler () {
		super();
		data = new ArrayList<VCFData>();
	}


	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equalsIgnoreCase("row")) {
			String group = attributes.getValue(getFormattedString(VCFData.GROUP_NAME));
			String genome = attributes.getValue(getFormattedString(VCFData.GENOME_NAME));
			String path = attributes.getValue(getFormattedString(VCFData.FILE_NAME));
			String raw = attributes.getValue(getFormattedString(VCFData.RAW_NAME));
			VCFData vcfData = new VCFData(group, genome, new File(path), raw);
			data.add(vcfData);
		}
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
			for (VCFData vcfData: data) {
				out.write("\t<row ");
				out.write(getFormattedString(VCFData.GROUP_NAME) + "=\"" + vcfData.getGroup() + "\" ");
				out.write(getFormattedString(VCFData.GENOME_NAME) + "=\"" + vcfData.getGenome() + "\" ");
				out.write(getFormattedString(VCFData.FILE_NAME) + "=\"" + vcfData.getFile() + "\" ");
				out.write(getFormattedString(VCFData.RAW_NAME) + "=\"" + vcfData.getRaw() + "\" ");
				out.write("/>\n");
			}
			out.write("</settings>");
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	
	private String getFormattedString (String s) {
		return s.toLowerCase().replace(' ', '_');
	}

}
