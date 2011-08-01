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
package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.enums.VCFType;
import edu.yu.einstein.genplay.core.manager.ConfigurationManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.ProjectScreenManager;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf.SettingsHandler;
import edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject.vcf.VCFLoader;
import edu.yu.einstein.genplay.gui.fileFilter.XMLFilter;

/**
 * This class shows information and buttons about the multi genome
 * @author Nicolas Fourel
 * @version 0.1
 */
class MultiGenomePanel extends JPanel {

	private static final long serialVersionUID = -1295541774864815129L;

	private MultiGenomePanel 			instance;			// instance of the class
	private MultiGenomeInformationPanel informationPanel;	// multi genome information panel 
	private VCFLoader 					vcfLoader;			// VCF loader
	private List<List<Object>> 			data;				// data
	private JFileChooser 				fc;					// file chooser

	
	/**
	 * Constructor of {@link MultiGenomePanel}
	 */
	protected MultiGenomePanel () {
		instance = this;

		setVisible(false);

		//Create a file chooser
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(ConfigurationManager.getInstance().getDefaultDirectory()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new XMLFilter());

		//Size
		Dimension dim = ProjectScreenManager.getVCFDim();
		setSize(dim);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);

		setBackground(ProjectScreenManager.getVCFColor());

		//Layout
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER);
		flow.setVgap(20);
		setLayout(flow);


		informationPanel = new MultiGenomeInformationPanel();

		vcfLoader = new VCFLoader();
		data = new ArrayList<List<Object>>();

		//Edit button
		JButton editVCFFile = new JButton("Edit");
		editVCFFile.setToolTipText("Edit multi genome information");
		editVCFFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vcfLoader == null) {
					vcfLoader = new VCFLoader();
				}
				vcfLoader.setData(getData());
				if (vcfLoader.showDialog(instance) == VCFLoader.APPROVE_OPTION) {
					setData(vcfLoader.getData());
					vcfLoader.closeDialog();
				}

			}
		});


		//Import button
		JButton importXML = new JButton("Import");
		importXML.setToolTipText("Import information from xml");
		importXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				importXML();
			}
		});


		//Export button
		JButton exportXML = new JButton("Export");
		exportXML.setToolTipText("Export information to xml");
		exportXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportXML();
			}
		});


		add(informationPanel);
		add(editVCFFile);
		add(importXML);
		add(exportXML);

	}


	/**
	 * Imports a XML file settings
	 */
	private void importXML () {
		// XML File
		File xmlFile = null;

		// XML Chooser
		int returnVal = fc.showOpenDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			String xmlPath = fc.getSelectedFile().getPath();
			xmlFile = new File(xmlPath);

			// Stream & Parsers
			FileInputStream xml;
			SAXParser parser;
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			SettingsHandler xmlParser = new SettingsHandler();
			try {
				xml = new FileInputStream(xmlFile);
				parser = parserFactory.newSAXParser();
				parser.parse(xml, xmlParser);
			} catch (ParserConfigurationException e1) {
				e1.printStackTrace();
			} catch (SAXException e1) {
				e1.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Manager initialization
			addData(xmlParser.getData());
			vcfLoader.setData(data);
			vcfLoader.initStatisticsInformation();
		}
	}


	/**
	 * Exports a XML file settings
	 */
	private void exportXML () {
		int returnVal = fc.showSaveDialog(getRootPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			SettingsHandler xmlParser = new SettingsHandler();
			xmlParser.setData(vcfLoader.getData());
			xmlParser.write(file);
		} else if (returnVal == JFileChooser.ERROR_OPTION) {
			JOptionPane.showMessageDialog(getRootPane(), "Please select a valid XML file", "Invalid XML selection", JOptionPane.WARNING_MESSAGE);
		}
	}


	/**
	 * Adds data to the current list
	 * @param newData
	 */
	private void addData (List<List<Object>> newData) {
		if (data == null) {
			data = new ArrayList<List<Object>>();
		}
		for (List<Object> rowData: newData) {
			data.add(rowData);
		}
	}


	/**
	 * Sets the data object
	 * @param newData	new data
	 */
	private void setData (List<List<Object>> newData) {
		data = new ArrayList<List<Object>>();
		for (List<Object> list: newData) {
			data.add(list);
		}
	}


	/**
	 * @return the data object
	 */
	private List<List<Object>> getData () {
		List<List<Object>> newData = new ArrayList<List<Object>>();
		if (data == null) {
			data = new ArrayList<List<Object>>();
		}
		for (List<Object> list: data) {
			newData.add(list);
		}
		return newData;
	}


	/**
	 * @return the genome/group association
	 */
	protected Map<String, List<String>> getGenomeGroupAssociation () {
		return vcfLoader.getGenomeGroupAssociation();
	}


	/**
	 * @return the genome/VCF association
	 */
	protected Map<String, List<File>> getGenomeFilesAssociation () {
		return vcfLoader.getGenomeFilesAssociation();
	}


	/**
	 * @return the raw/usual genome names association
	 */
	protected Map<String, String> getGenomeNamesAssociation () {
		return vcfLoader.getGenomeNamesAssociation();
	}


	/**
	 * @return the VCF type/files association
	 */
	protected Map<VCFType, List<File>> getFilesTypeAssociation () {
		return vcfLoader.getFilesTypeAssociation();
	}


	/**
	 * @return true if the multi genome project is valid
	 */
	protected boolean isValidMultigenomeProject () {
		if (vcfLoader != null) {
			return vcfLoader.isValidMultigenomeProject();
		}
		return false;
	}

}