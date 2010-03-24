package yu.einstein.gdp2.gui.dialog.DAS;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASServer;
import yu.einstein.gdp2.core.DAS.DASServerList;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.util.ExceptionManager;

public class DASMain extends JDialog {

	private static final long serialVersionUID = 4995384388348077375L;

	private final static String SERVER_LIST_PATH = 
		"yu/einstein/gdp2/resource/DASServerList.xml";

	private DASServerList serverList;
	private DASConnector dasConnector;
	private List<DataSource> dataSourceList;
	private List<DASType> dataTypeList; 


	private final JLabel jlServer;
	private final JComboBox jcbServer;

	private final JLabel jlDataSource;
	private final JComboBox jcbDataSource;

	private final JLabel jlDataType;
	private final JComboBox jcbDataType;

	private final JLabel jlResultType;
	private final JRadioButton jrbGeneListResult;
	private final JRadioButton jrbSCWListResult;


	public DASMain() throws ParserConfigurationException, SAXException, IOException {
		super();
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
		jcbDataType = new JComboBox();

		jlResultType = new JLabel("Generate:");
		jrbGeneListResult = new JRadioButton("Gene List");
		jrbSCWListResult = new JRadioButton("Fixed Window List");



		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlServer, c);

		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbServer, c);

		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataSource, c);

		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbDataSource, c);

		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataType, c);

		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_END;
		add(jcbDataType, c);
	}


	protected void selectedDataSourceChanged() {
		DataSource selectedDataSource = (DataSource)jcbDataSource.getSelectedItem();
		if (selectedDataSource != null) {
			try {
				dataTypeList = dasConnector.getDASTypeList(selectedDataSource);
				jcbDataType.removeAllItems();
				for (DASType currentDataType: dataTypeList) {
					jcbDataType.addItem(currentDataType);
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
			new DASMain().setVisible(true);
		} catch (Exception e) {

		}
	}
}
