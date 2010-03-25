package yu.einstein.gdp2.gui.dialog.DAS;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASServer;
import yu.einstein.gdp2.core.DAS.DASServerList;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;

public class DASDialog extends JDialog {

	private static final long serialVersionUID = 4995384388348077375L;

	private final static Dimension WINDOW_SIZE = 
		new Dimension(300, 200);
	private final static String SERVER_LIST_PATH = 
		"yu/einstein/gdp2/resource/DASServerList.xml";

	private DASServerList serverList;
	private DASConnector dasConnector;
	private List<DataSource> dataSourceList;
	private List<DASType> dasTypeList; 


	private final JLabel jlServer;
	private final JComboBox jcbServer;

	private final JLabel jlDataSource;
	private final JComboBox jcbDataSource;

	private final JLabel jlDataType;
	private final JComboBox jcbDasType;

	private final JLabel jlResultType;
	private final JRadioButton jrbGeneListResult;
	private final JRadioButton jrbSCWListResult;
	
	private final JButton jbCancel;
	private final JButton jbRetrieve;

	private ChromosomeManager chromsomeManager;


	public DASDialog(ChromosomeManager chromsomeManager) throws ParserConfigurationException, SAXException, IOException {
		super();
		this.chromsomeManager = chromsomeManager;
		ClassLoader cl = this.getClass().getClassLoader();
		URL serverListURL = cl.getResource(SERVER_LIST_PATH);
		serverList = new DASServerList(serverListURL);

		jlServer = new JLabel("Server:");
		jcbServer = new JComboBox(serverList.toArray());
		jcbServer.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedServerChanged();
			}
		});

		jlDataSource = new JLabel("Data Source:");
		jcbDataSource = new JComboBox();
		jcbDataSource.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedDataSourceChanged();				
			}
		});

		jlDataType = new JLabel("Data Type:");
		jcbDasType = new JComboBox();

		jlResultType = new JLabel("Generate:");
		jrbGeneListResult = new JRadioButton("Gene List");
		jrbSCWListResult = new JRadioButton("Fixed Window List");
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbGeneListResult);
		radioGroup.add(jrbSCWListResult);
		radioGroup.setSelected(jrbSCWListResult.getModel(), true);

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);				
			}
		});
		jbRetrieve = new JButton("Retrieve");
		jbRetrieve.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				jbRetrieveClicked();
			}
		});

		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlServer, c);

		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbServer, c);

		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataSource, c);

		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbDataSource, c);

		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataType, c);

		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbDasType, c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlResultType, c);
		
		c.gridy = 5;
		add(jrbSCWListResult, c);
		
		c.gridy = 6;
		add(jrbGeneListResult, c);
		
		c.gridy = 7;
		c.gridx = 1;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbRetrieve, c);
		
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);

		setTitle("Retrieve DAS Data");
		setPreferredSize(WINDOW_SIZE);
		setMinimumSize(WINDOW_SIZE);
		setModal(true);
		selectedServerChanged();
	}


	protected void jbRetrieveClicked() {
		DASServer selectedServer = (DASServer)jcbServer.getSelectedItem();
		DataSource selectedDataSource = (DataSource)jcbDataSource.getSelectedItem();
		DASType selectedDasType = (DASType)jcbDasType.getSelectedItem();
		if ((selectedServer == null) || (selectedDataSource == null) || (selectedDasType == null)) {
			JOptionPane.showMessageDialog(getRootPane(), "You need to fill all the fields before retrieving the data", "Error", JOptionPane.ERROR_MESSAGE);
		} else {
			try {
				dasConnector.getGeneList(chromsomeManager, selectedDataSource, selectedDasType);
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error when retrieving the data types from " + selectedDataSource);
			}
		}
	}


	protected void selectedDataSourceChanged() {
		DataSource selectedDataSource = (DataSource)jcbDataSource.getSelectedItem();
		if (selectedDataSource != null) {
			try {
				dasTypeList = dasConnector.getDASTypeList(selectedDataSource);
				jcbDasType.removeAllItems();
				for (DASType currentDataType: dasTypeList) {
					jcbDasType.addItem(currentDataType);
				}
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error when retrieving the data types from " + selectedDataSource);
			}
		}
	}


	protected void selectedServerChanged() {
		DASServer selectedServer = (DASServer)jcbServer.getSelectedItem();
		if (selectedServer != null) {
			dasConnector = new DASConnector(selectedServer.getURL());
			try {
				dataSourceList = dasConnector.getDataSourceList();
				jcbDataSource.removeAllItems();
				for (DataSource currentSource: dataSourceList) {
					jcbDataSource.addItem(currentSource);
				}
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error when retrieving the data sources from " + selectedServer);
			}
		}
	}


	public static void main(String[] args) {
		try {
			new DASDialog(ChromosomeManager.getInstance()).setVisible(true);
		} catch (Exception e) {

		}
	}
}
