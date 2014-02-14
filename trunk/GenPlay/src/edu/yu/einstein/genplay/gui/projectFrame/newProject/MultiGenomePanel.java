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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.SettingsHandler;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.VCFData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.VCFLoaderDialog;
import edu.yu.einstein.genplay.gui.fileFilter.XMLFilter;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class shows information and buttons about the multi genome
 * @author Nicolas Fourel
 */
class MultiGenomePanel extends JPanel {

	private static final long serialVersionUID = -1295541774864815129L;

	private final MultiGenomeInformationPanel 	informationPanel;		// multi genome information panel
	private final JButton 						exportXML;				// button to export the multi-genome settings as data
	private VCFLoaderDialog						vcfLoaderDialog;		// the VCF loader
	private List<VCFData> 						data;					// data
	private Map<String, List<VCFFile>> 			genomeFileAssociation;
	private final JFileChooser 					fc;						// file chooser


	/**
	 * Constructor of {@link MultiGenomePanel}
	 */
	protected MultiGenomePanel () {
		//Create a file chooser
		fc = new JFileChooser();
		Utils.setFileChooserSelectedDirectory(fc);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new XMLFilter());

		informationPanel = new MultiGenomeInformationPanel();

		vcfLoaderDialog = new VCFLoaderDialog();
		data = new ArrayList<VCFData>();

		//Edit button
		JButton editVCFFile = new JButton("Select VCF");
		editVCFFile.setToolTipText("Edit multi genome information");
		editVCFFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vcfLoaderDialog == null) {
					vcfLoaderDialog = new VCFLoaderDialog();
				}
				vcfLoaderDialog.setData(getData());
				if (vcfLoaderDialog.showDialog(ProjectFrame.getInstance().getRootPane()) == VCFLoaderDialog.APPROVE_OPTION) {
					setData(vcfLoaderDialog.getData());
					if (vcfLoaderDialog.areValidSettings()) {

						initializesGenomeFileAssociation();
						updatesStatistics();
					}
				}
			}
		});

		//Import button
		JButton importXML = new JButton("Import Config");
		importXML.setToolTipText("Import information from xml");
		importXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				importXML();
			}
		});

		//Export button
		exportXML = new JButton("Export Config");
		exportXML.setToolTipText("Export information to xml");
		exportXML.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportXML();
			}
		});
		exportXML.setEnabled(false);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridwidth = 2;
		add(informationPanel, gbc);

		gbc.insets = new Insets(10, 0, 0, 0);
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridy = 1;
		add(editVCFFile, gbc);

		gbc.gridy = 2;
		gbc.gridwidth = 1;
		add(importXML, gbc);

		gbc.gridx = 1;
		add(exportXML, gbc);

		setOpaque(false);

		setVarTableVisible(false);
	}


	/**
	 * Adds data to the current list.
	 * Case of importing data.
	 * (importing do not erase current settings but add new ones!)
	 * @param newData
	 */
	private void addData (List<VCFData> newData) {
		if (data == null) {
			data = new ArrayList<VCFData>();
		}
		for (VCFData vcfData: newData) {
			data.add(vcfData);
		}
	}


	/**
	 * Closes the XML stream
	 * @param xml
	 */
	private void closeXML (FileInputStream xml) {
		if (xml != null) {
			try {
				xml.close();
			} catch (IOException e) {
				ExceptionManager.getInstance().caughtException(e);
			}
		}
	}


	/**
	 * Exports a XML file settings
	 */
	private void exportXML () {
		if (data.size() > 0) {
			int returnVal = fc.showSaveDialog(getRootPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				file = Utils.addExtension(file, XMLFilter.EXTENSIONS[0]);
				SettingsHandler xmlParser = new SettingsHandler(file);
				xmlParser.setData(data);
				xmlParser.write();
			} else if (returnVal == JFileChooser.ERROR_OPTION) {
				JOptionPane.showMessageDialog(getRootPane(), "Please select a valid XML file", "Invalid XML selection", JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(getRootPane(), "No setting has been found", "Settings export process", JOptionPane.WARNING_MESSAGE);
		}
	}


	/**
	 * @return the data object
	 */
	private List<VCFData> getData () {
		List<VCFData> newData = new ArrayList<VCFData>();
		if (data == null) {
			data = new ArrayList<VCFData>();
		}
		for (VCFData vcfData: data) {
			newData.add(vcfData);
		}
		return newData;
	}


	/**
	 * @return the mapping between genome full names and their readers.
	 */
	protected Map<String, List<VCFFile>> getGenomeFileAssociation ()  {
		return genomeFileAssociation;
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
			FileInputStream xml = null;
			SAXParser parser;
			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(true);
			SettingsHandler xmlParser = new SettingsHandler(xmlFile);
			try {
				xml = new FileInputStream(xmlFile);
				parser = parserFactory.newSAXParser();
				parser.parse(xml, xmlParser);
				closeXML(xml);
			} catch (ParserConfigurationException e1) {
				closeXML(xml);
				ExceptionManager.getInstance().caughtException(e1);
			} catch (SAXException e1) {
				closeXML(xml);
				ExceptionManager.getInstance().caughtException(e1);
			} catch (FileNotFoundException e) {
				closeXML(xml);
				ExceptionManager.getInstance().caughtException(e);
			} catch (IOException e) {
				closeXML(xml);
				ExceptionManager.getInstance().caughtException(e);
			}
			closeXML(xml);
			// Manager initialization
			addData(xmlParser.getData());

			vcfLoaderDialog.setData(data);
			initializesGenomeFileAssociation();
			updatesStatistics();
		}
	}


	/**
	 * Initializes the genome/file map association.
	 * Updates also the statistical information and refreshes the panel.
	 */
	private void initializesGenomeFileAssociation () {
		genomeFileAssociation = new HashMap<String, List<VCFFile>>();
		List<VCFFile> readerList = new ArrayList<VCFFile>();

		for (VCFData vcfData: data) {
			String fullName = FormattedMultiGenomeName.getFullFormattedGenomeName(vcfData.getGroup(), vcfData.getNickname(), vcfData.getRaw());
			if (!genomeFileAssociation.containsKey(fullName)) {
				genomeFileAssociation.put(fullName, new ArrayList<VCFFile>());
			}
			VCFFile vcfFile = vcfLoaderDialog.getVCFFile(vcfData.getFile());
			genomeFileAssociation.get(fullName).add(vcfFile);
			readerList.add(vcfFile);
		}
	}


	/**
	 * @return true if the multi genome project is valid
	 */
	protected boolean isValidMultigenomeProject () {
		return vcfLoaderDialog.areValidSettings();
	}


	/**
	 * Sets the data object.
	 * After VCF loader dialog validation.
	 * @param newData	new data
	 */
	private void setData (List<VCFData> newData) {
		data = new ArrayList<VCFData>();
		for (VCFData vcfData: newData) {
			data.add(vcfData);
		}
	}


	/**
	 * Displays or hides the var panel
	 * @param visible set to true to show the var table
	 */
	void setVarTableVisible(boolean visible) {
		if (visible) {
			setBorder(BorderFactory.createTitledBorder("Genomes Selection"));
		} else {
			setBorder(null);
		}
		for (Component c: getComponents()) {
			c.setVisible(visible);
		}
	}


	/**
	 * Generates statistics about:
	 * - group #
	 * - genome #
	 * - VCF file #
	 * Updates the information panel to display them
	 */
	private void updatesStatistics () {
		List<String> groupList = new ArrayList<String>();
		List<String> genomeList = new ArrayList<String>();
		Map<String, Integer> fileGenome = new HashMap<String, Integer>();

		if (genomeFileAssociation.keySet().isEmpty()) {
			exportXML.setEnabled(false);
		} else {
			exportXML.setEnabled(true);
			for (String fullGenomeName: genomeFileAssociation.keySet()) {
				String groupName = FormattedMultiGenomeName.getGroupName(fullGenomeName);
				if (!groupList.contains(groupName)) {
					groupList.add(groupName);
				}

				String rawName = FormattedMultiGenomeName.getRawName(fullGenomeName);
				if (!genomeList.contains(rawName)) {
					genomeList.add(rawName);
				}

				for (VCFFile reader: genomeFileAssociation.get(fullGenomeName)) {
					String path = reader.getFile().getPath();
					if (!fileGenome.containsKey(path)) {
						fileGenome.put(path, 0);
					}
					Integer cpt = fileGenome.get(path) + 1;
					fileGenome.put(path, cpt);
				}
			}

			MultiGenomeInformationPanel.GROUP_NUMBER = groupList.size();
			MultiGenomeInformationPanel.GENOME_NUMBER = genomeList.size();
			MultiGenomeInformationPanel.FILE_NUMBER = fileGenome.size();
			MultiGenomeInformationPanel.refreshInformation();
		}
	}
}
