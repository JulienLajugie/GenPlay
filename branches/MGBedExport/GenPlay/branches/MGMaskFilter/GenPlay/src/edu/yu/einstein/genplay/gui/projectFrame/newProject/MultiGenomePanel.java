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
package edu.yu.einstein.genplay.gui.projectFrame.newProject;

import java.awt.Dimension;
import java.awt.FlowLayout;
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.SettingsHandler;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.VCFData;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.vcfLoader.VCFLoaderDialog;
import edu.yu.einstein.genplay.gui.fileFilter.XMLFilter;
import edu.yu.einstein.genplay.gui.projectFrame.ProjectFrame;
import edu.yu.einstein.genplay.util.Utils;

/**
 * This class shows information and buttons about the multi genome
 * @author Nicolas Fourel
 * @version 0.1
 */
class MultiGenomePanel extends JPanel {

	private static final long serialVersionUID = -1295541774864815129L;

	private MultiGenomeInformationPanel informationPanel;	// multi genome information panel 
	private VCFLoaderDialog				vcfLoaderDialog;	// the VCF loader
	private List<VCFData> 				data;				// data
	private Map<String, List<VCFFile>> genomeFileAssociation;

	private JFileChooser 				fc;					// file chooser


	/**
	 * Constructor of {@link MultiGenomePanel}
	 */
	protected MultiGenomePanel () {

		setVisible(false);

		//Create a file chooser
		fc = new JFileChooser();
		fc.setCurrentDirectory(new File(ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory()));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new XMLFilter());

		//Size
		Dimension dim = ProjectFrame.VCF_DIM;
		setSize(dim);
		setPreferredSize(dim);
		setMinimumSize(dim);
		setMaximumSize(dim);

		setBackground(ProjectFrame.VCF_COLOR);

		//Layout
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER);
		flow.setVgap(20);
		setLayout(flow);


		informationPanel = new MultiGenomeInformationPanel();

		vcfLoaderDialog = new VCFLoaderDialog();
		data = new ArrayList<VCFData>();

		//Edit button
		JButton editVCFFile = new JButton("Edit");
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
						//vcfLoaderDialog.closeDialog();
					}
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
			SettingsHandler xmlParser = new SettingsHandler(xmlFile);
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
			vcfLoaderDialog.setData(data);
			initializesGenomeFileAssociation();
			updatesStatistics();
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
		//showsAssociation();
	}


	/*private void showsAssociation () {
		String info = "-----------------\n";
		for (String genome: genomeFileAssociation.keySet()) {
			info += genome + ": ";
			for (VCFFile reader: genomeFileAssociation.get(genome)) {
				info += reader.hashCode() + " " + reader.getFile().getName() + ";";
			}
			info += "\n";
		}
		System.out.println(info);
	}*/


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


	/**
	 * @return the mapping between genome full names and their readers.
	 */
	protected Map<String, List<VCFFile>> getGenomeFileAssociation ()  {
		return genomeFileAssociation;
	}


	/**
	 * @return true if the multi genome project is valid
	 */
	protected boolean isValidMultigenomeProject () {
		return vcfLoaderDialog.areValidSettings();
	}

}
