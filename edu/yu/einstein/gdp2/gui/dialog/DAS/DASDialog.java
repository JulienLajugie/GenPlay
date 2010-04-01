/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.DAS;


import java.awt.Component;
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
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASServer;
import yu.einstein.gdp2.core.DAS.DASServerList;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.util.ExceptionManager;


/**
 * A dialog box for choosing data to download from a DAS server
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASDialog extends JDialog {

	private static final long serialVersionUID = 4995384388348077375L;	// generated ID
	private final static Dimension WINDOW_SIZE = 
		new Dimension(300, 200);						// size of the window
	private final static String SERVER_LIST_PATH = 
		"yu/einstein/gdp2/resource/DASServerList.xml";	// config file path
	
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 0;
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 1;	
	/**
	 * Generate a Gene List Option
	 */
	public static final int GENERATE_GENE_LIST = 0;
	/**
	 * Generate a Scored Chromosome Window Option
	 */
	public static final int GENERATE_SCW_LIST = 1;
	
	private final JLabel 		jlServer;			// label server
	private final JComboBox 	jcbServer;			// combo box server
	private final JLabel 		jlDataSource;		// lable data source
	private final JComboBox 	jcbDataSource;		// combo box data source
	private final JLabel 		jlDataType;			// label data type
	private final JComboBox 	jcbDasType;			// combo box data type
	private final JLabel 		jlResultType;		// label result type
	private final JRadioButton 	jrbGeneListResult;	// radio button gene list 
	private final JRadioButton 	jrbSCWListResult;	// radio button SCW list
	private final JButton 		jbCancel;			// cancel button
	private final JButton 		jbOk;				// ok button

	private int				approved = CANCEL_OPTION;			// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not 
	private int 			generateType = GENERATE_GENE_LIST;	// the type of list to generate 
	private DASConnector 	selectedDasConnector = null;		// the DASConnector of the selected server
	private DASType 		selectedDasType = null;				// the selected DASType
	private DataSource 		selectedDataSource = null;			// the selected DataSource


	/**
	 * Creates an instance of {@link DASDialog}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public DASDialog() throws ParserConfigurationException, SAXException, IOException {
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

		jlResultType = new JLabel("Generate:");
		jrbGeneListResult = new JRadioButton("Gene List");
		jrbGeneListResult.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				resultTypeChanged();				
			}
		});
		jrbSCWListResult = new JRadioButton("Variable Window List");
		jrbSCWListResult.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				resultTypeChanged();				
			}
		});
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
		jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkClicked();
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
		add(jbOk, c);

		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);

		setTitle("Retrieve DAS Data");
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		getRootPane().setDefaultButton(jbOk);
		setPreferredSize(WINDOW_SIZE);
		setMinimumSize(WINDOW_SIZE);
		setModal(true);
		selectedServerChanged();
		resultTypeChanged();
	}

	
	/**
	 * @return generateType (GENERATE_GENE_LIST or GENERATE_SCW_LIST)
	 */
	public final int getGenerateType() {
		return generateType;
	}


	/**
	 * @return the selected DasConnector
	 */
	public final DASConnector getSelectedDasConnector() {
		return selectedDasConnector;
	}


	/**
	 * @return the selected DasType
	 */
	public final DASType getSelectedDasType() {
		return selectedDasType;
	}


	/**
	 * @return the selected DataSource
	 */
	public final DataSource getSelectedDataSource() {
		return selectedDataSource;
	}


	/**
	 * Method called when the selected DASType changes
	 */
	protected void selectedDasTypeChanged() {
		selectedDasType = (DASType)jcbDasType.getSelectedItem();		
	}


	/**
	 * Method called when the selected result type changes
	 */
	protected void resultTypeChanged() {
		if (jrbGeneListResult.isSelected()) {
			generateType = GENERATE_GENE_LIST;
		} else if (jrbSCWListResult.isSelected()) {
			generateType = GENERATE_SCW_LIST;
		}
	}


	/**
	 * Method called when the button okay is clicked
	 */
	protected void jbOkClicked() {
		approved = APPROVE_OPTION;
		setVisible(false);
	}


	/**
	 * Method called when the selected DataSource changes
	 */
	protected void selectedDataSourceChanged() {
		selectedDataSource = (DataSource)jcbDataSource.getSelectedItem();
		if (selectedDataSource != null) {
			try {
				List<DASType> dasTypeList = selectedDasConnector.getDASTypeList(selectedDataSource);
				jcbDasType.removeAllItems();
				for (DASType currentDataType: dasTypeList) {
					jcbDasType.addItem(currentDataType);
				}
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error when retrieving the data types from " + selectedDataSource);
			}
		}
	}


	/**
	 * Method called when the selected DAS server changes
	 */
	protected void selectedServerChanged() {
		DASServer selectedServer = (DASServer)jcbServer.getSelectedItem();
		if (selectedServer != null) {
			selectedDasConnector = new DASConnector(selectedServer.getURL());
			try {
				List<DataSource> dataSourceList = selectedDasConnector.getDataSourceList();
				jcbDataSource.removeAllItems();
				for (DataSource currentSource: dataSourceList) {
					jcbDataSource.addItem(currentSource);
				}
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error when retrieving the data sources from " + selectedServer);
			}
		}
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @return GENERATE_OPTION if Generate is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDASDialog(Component parent) {
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}
}
