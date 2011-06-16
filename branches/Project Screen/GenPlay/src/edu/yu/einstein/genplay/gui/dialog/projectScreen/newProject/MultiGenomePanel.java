package edu.yu.einstein.genplay.gui.dialog.projectScreen.newProject;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	
	private MultiGenomeInformationPanel informationPanel;
	private VCFLoader vcfLoader;
	private JFileChooser fc;
	
	protected MultiGenomePanel () {
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
		
		//Edit button
		JButton editVCFFile = new JButton("Edit");
		editVCFFile.setToolTipText("Edit multi genome information");
		editVCFFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				vcfLoader.setVisible(true);
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
		vcfLoader.setData(xmlParser.getData());
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
