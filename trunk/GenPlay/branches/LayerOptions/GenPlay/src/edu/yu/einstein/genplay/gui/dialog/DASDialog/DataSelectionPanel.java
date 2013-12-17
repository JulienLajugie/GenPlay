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
package edu.yu.einstein.genplay.gui.dialog.DASDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.yu.einstein.genplay.core.DAS.DASConnector;
import edu.yu.einstein.genplay.core.DAS.DASServer;
import edu.yu.einstein.genplay.core.DAS.DASServerList;
import edu.yu.einstein.genplay.core.DAS.DASType;
import edu.yu.einstein.genplay.core.DAS.DataSource;
import edu.yu.einstein.genplay.exception.ExceptionManager;


/**
 * Panel of a DAS dialog allowing the select the data to retrieve
 * @author Julien Lajugie
 */
public class DataSelectionPanel extends JPanel {

	private static final long serialVersionUID = 9076119842012637763L; // generated ID
	private final static String SERVER_LIST_PATH =
			"edu/yu/einstein/genplay/resource/DASServerList.xml";	// config file path
	private final JLabel 		jlServer;						// label server
	private final JComboBox 	jcbServer;						// combo box server
	private final JLabel 		jlDataSource;					// label data source
	private final JComboBox 	jcbDataSource;					// combo box data source
	private final JLabel 		jlDataType;						// label data type
	private final JComboBox 	jcbDasType;						// combo box data type
	private DASConnector 		selectedDasConnector = null;	// the DASConnector of the selected server
	private DASType 			selectedDasType = null;			// the selected DASType
	private DataSource 			selectedDataSource = null;		// the selected DataSource


	/**
	 * Creates an instance of {@link DataSelectionPanel}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DataSelectionPanel() throws ParserConfigurationException, SAXException, IOException {
		super();

		ClassLoader cl = this.getClass().getClassLoader();
		URL serverListURL = cl.getResource(SERVER_LIST_PATH);
		DASServerList dasServerList = new DASServerList(serverListURL);

		jlServer = new JLabel("Server:");
		jcbServer = new JComboBox(dasServerList.toArray());
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
		jcbDasType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedDasTypeChanged();
			}
		});

		// we add the components
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlServer, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbServer, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataSource, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbDataSource, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataType, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbDasType, c);

		setBorder(BorderFactory.createTitledBorder("Select Data"));
		selectedServerChanged();
	}


	/**
	 * Method called when the selected DASType changes
	 */
	private void selectedDasTypeChanged() {
		selectedDasType = (DASType) jcbDasType.getSelectedItem();
	}


	/**
	 * Method called when the selected DataSource changes
	 */
	private void selectedDataSourceChanged() {
		selectedDataSource = (DataSource) jcbDataSource.getSelectedItem();
		if (selectedDataSource != null) {
			try {
				List<DASType> dasTypeList = selectedDasConnector.getDASTypeList(selectedDataSource);
				jcbDasType.removeAllItems();
				for (DASType currentDataType: dasTypeList) {
					jcbDasType.addItem(currentDataType);
				}
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error when retrieving the data types from " + selectedDataSource);
			}
		}
	}


	/**
	 * Method called when the selected DAS server changes
	 */
	private void selectedServerChanged() {
		DASServer selectedServer = (DASServer) jcbServer.getSelectedItem();
		if (selectedServer != null) {
			selectedDasConnector = new DASConnector(selectedServer.getURL());
			try {
				List<DataSource> dataSourceList = selectedDasConnector.getDataSourceList();
				jcbDataSource.removeAllItems();
				for (DataSource currentSource: dataSourceList) {
					jcbDataSource.addItem(currentSource);
				}
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error when retrieving the data sources from " + selectedServer);
			}
		}
	}


	/**
	 * @return the selected {@link DASConnector}
	 */
	public final DASConnector getSelectedDasConnector() {
		return selectedDasConnector;
	}


	/**
	 * @return the selected {@link DASType}
	 */
	public final DASType getSelectedDasType() {
		return selectedDasType;
	}


	/**
	 * @return the selected {@link DataSource}
	 */
	public final DataSource getSelectedDataSource() {
		return selectedDataSource;
	}
}
