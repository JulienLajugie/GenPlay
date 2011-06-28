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

class MultiGenomePanel extends JPanel {

	private static final long serialVersionUID = -1295541774864815129L;

	private MultiGenomePanel instance;
	private MultiGenomeInformationPanel informationPanel;
	private VCFLoader vcfLoader;
	private List<List<Object>> 	data;
	private JFileChooser fc;

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


	private void importXML () {
		// XML File
		File xmlFile = null;

		// XML Chooser
		int returnVal = fc.showOpenDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			String xmlPath = fc.getSelectedFile().getPath();
			xmlFile = new File(xmlPath);
		}

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


	private void addData (List<List<Object>> newData) {
		if (data == null) {
			data = new ArrayList<List<Object>>();
		}
		for (List<Object> rowData: newData) {
			data.add(rowData);
		}
	}


	private void setData (List<List<Object>> newData) {
		data = new ArrayList<List<Object>>();
		for (List<Object> list: newData) {
			data.add(list);
		}
	}


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


	protected Map<String, List<String>> getGenomeGroupAssociation () {
		return vcfLoader.getGenomeGroupAssociation();
	}


	protected Map<String, List<File>> getGenomeFilesAssociation () {
		return vcfLoader.getGenomeFilesAssociation();
	}


	protected Map<String, String> getGenomeNamesAssociation () {
		return vcfLoader.getGenomeNamesAssociation();
	}


	protected Map<VCFType, List<File>> getFilesTypeAssociation () {
		return vcfLoader.getFilesTypeAssociation();
	}


	protected boolean isValidMultigenomeProject () {
		if (vcfLoader != null) {
			return vcfLoader.isValidMultigenomeProject();
		}
		return false;
	}

}
