package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SettingsHandler extends DefaultHandler {


	private List<List<Object>> 	data;
	private String[] attributeNames;


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
