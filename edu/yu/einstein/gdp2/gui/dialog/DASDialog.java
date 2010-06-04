/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.DAS.DASConnector;
import yu.einstein.gdp2.core.DAS.DASServer;
import yu.einstein.gdp2.core.DAS.DASServerList;
import yu.einstein.gdp2.core.DAS.DASType;
import yu.einstein.gdp2.core.DAS.DataSource;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;


/**
 * A dialog box for choosing data to download from a DAS server
 * @author Julien Lajugie
 * @version 0.1
 */
public class DASDialog extends JDialog {

	private static final long serialVersionUID = 4995384388348077375L;	// generated ID
	private final static Dimension WINDOW_SIZE = 
		new Dimension(600, 300);						// size of the window
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
	/**
	 * Generate the Genome Wide List
	 */
	public static final int GENERATE_GENOMEWIDE_LIST = 0;
	/**
	 * Generate the current value Option
	 */
	public static final int GENERATE_CURRENT_LIST = 1;
	/**
	 * Generate the user specified Option
	 */
	public static final int GENERATE_USER_SPECIFIED_LIST = 2;
	private DecimalFormat numFormat;
	private NumberFormatter num;
	
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
	private final JLabel		jlGenomeRange;
	private final JRadioButton	jrbGenomeWide;		// radio button for the genome wide range
	private final JRadioButton	jrbUserSpecifiedRange;			// radio button user specified range
	private final JFormattedTextField	jtfUserStart;		// text filed for user specified start value
	private final JFormattedTextField	jtfUserEnd;			// text filed for user specified stop value
	private final JComboBox		jcbChromozomeNumber;	// combo box to select choromosome number
	private final JRadioButton	jrbCurrentRange;	// radio button for current range

	private int				approved = CANCEL_OPTION;			// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not 
	private int 			generateType = GENERATE_GENE_LIST;	// the type of list to generate
	private int 			dataRange = GENERATE_CURRENT_LIST;	// the type of the data range to be considered
	private DASConnector 	selectedDasConnector = null;		// the DASConnector of the selected server
	private DASType 		selectedDasType = null;				// the selected DASType
	private DataSource 		selectedDataSource = null;			// the selected DataSource
	private Chromosome		selectedChromosome = null;
	private long 			userSpecifiedStart = 0;
	private long 			userSpecifiedStop = 0;
	
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
		jrbGeneListResult = new JRadioButton("Gene Track");
		jrbGeneListResult.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				resultTypeChanged();				
			}
		});
		jrbSCWListResult = new JRadioButton("Variable Window Track");
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
		
		numFormat = new DecimalFormat("###,###,###");
		num = new NumberFormatter(numFormat);
		num.setAllowsInvalid(false);
		
		jtfUserStart = new JFormattedTextField(num);
		jtfUserStart.setEditable(false); 				
		jtfUserEnd = new JFormattedTextField(num);
		jtfUserEnd.setEditable(false);
		jtfUserEnd.setEnabled(false);
		jtfUserStart.setEnabled(false);
		
		jtfUserStart.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if (jtfUserStart.getText().equals("")) {
					jtfUserStart.setText("Start");
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				if (jtfUserStart.getText().equals("Start")) {
					jtfUserStart.setText("");
				}				
			}
		});
		
		jtfUserEnd.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				if (jtfUserEnd.getText().equals("")) {
					jtfUserEnd.setText("Stop");
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				if (jtfUserEnd.getText().equals("Stop")) {
					jtfUserEnd.setText("");
				}
			}
		});
		
		jcbChromozomeNumber= new JComboBox(ChromosomeManager.getInstance().toArray());
		jcbChromozomeNumber.setEnabled(false);
		jcbChromozomeNumber.addItemListener(new ItemListener() {			
			@Override
			public void itemStateChanged(ItemEvent e) {
				selectedChromosomeChanged();
			}
		});
		
		jlGenomeRange = new JLabel("Range of the Data");
		jrbGenomeWide = new JRadioButton("Genome Wide");
		jrbGenomeWide.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();								
			}
		});
		jrbCurrentRange = new JRadioButton("Current Range");
		jrbCurrentRange.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();					
			}
		});
		jrbUserSpecifiedRange = new JRadioButton("From");
		jrbUserSpecifiedRange.addChangeListener(new ChangeListener() {			
			@Override
			public void stateChanged(ChangeEvent e) {
				dataRangeChanged();				
			}
		});
		ButtonGroup radioGroup2 = new ButtonGroup();
		radioGroup2.add(jrbGenomeWide);
		radioGroup2.add(jrbCurrentRange);
		radioGroup2.add(jrbUserSpecifiedRange);
		radioGroup2.setSelected(jrbCurrentRange.getModel(), true);
		        		
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
		c.weightx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlServer, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbServer, c);

		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataSource, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbDataSource, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlDataType, c);

		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.LINE_START;
		c.fill = GridBagConstraints.BOTH;
		add(jcbDasType, c);

		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 4;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlResultType, c);

		c.gridy = 5;
		add(jrbSCWListResult, c);

		c.gridy = 6;
		add(jrbGeneListResult, c);

		c.gridx = 0;
		c.gridy = 7;
		c.gridwidth = 3;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jlGenomeRange, c);
		
		c.gridwidth = 3;
		c.gridy = 8;
		add(jrbGenomeWide, c);

		c.gridy = 9;
		add(jrbCurrentRange, c);
		
		c.gridy = 10;
		c.gridwidth = 3;
		c.weightx = 1;
		add(jrbUserSpecifiedRange, c);
		
		c.gridy = 12;
		c.gridx = 4;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.LINE_END;
		add(jbOk, c);

		c.gridx = 5;
		c.weightx = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, c);

		JPanel content = new JPanel();
		content.setLayout(new GridLayout(1, 3));
		content.add(jtfUserStart);
		content.add(jtfUserEnd);
		content.add(jcbChromozomeNumber);
		
		c.gridy = 10;
		c.gridx = 3;
		c.gridwidth = 3;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;
		add(content, c);
		
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
	 * @return dataRange(GENERATE_GENOMEWIDE_LIST or GENERATE_CURRENT_LIST or GENERATE_USER_SPECIFIED_LIST)
	 */
	public final int getDataRange() {
		return dataRange;
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
	 * @return the selected Chromosome
	 */
	public final Chromosome getSelectedChromosome() {
		return selectedChromosome;
	}

	/**
	 * Method called when the selected DASType changes
	 */
	protected void selectedDasTypeChanged() {
		selectedDasType = (DASType)jcbDasType.getSelectedItem();		
	}
	
	/**
	 * Method called when the selected Chromozome changes
	 */
	protected void selectedChromosomeChanged() {
		selectedChromosome = (Chromosome) (jcbChromozomeNumber.getSelectedItem());	
		num.setMaximum(new Long(selectedChromosome.getLength()));
		if (((Long) jtfUserEnd.getValue()) > selectedChromosome.getLength()) {
			jtfUserEnd.setValue((long) selectedChromosome.getLength());
		}
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
	 * Method called when the selected data range changes
	 */
	protected void dataRangeChanged()
	{
		if (jrbCurrentRange.isSelected())
		{
			dataRange = GENERATE_CURRENT_LIST;
			jtfUserEnd.setEnabled(false);
			jtfUserStart.setEnabled(false);
			jcbChromozomeNumber.setEnabled(false);
		}
		else if (jrbGenomeWide.isSelected()) 
		{
			dataRange = GENERATE_GENOMEWIDE_LIST;
			jtfUserEnd.setEnabled(false);
			jtfUserStart.setEnabled(false);
			jcbChromozomeNumber.setEnabled(false);
		}
		else if(jrbUserSpecifiedRange.isSelected())
		{
			dataRange = GENERATE_USER_SPECIFIED_LIST;
			jtfUserEnd.setEnabled(true);
			jtfUserStart.setEnabled(true);
			jtfUserStart.setEditable(true);
			jtfUserEnd.setEditable(true);
			jcbChromozomeNumber.setEnabled(true);
			selectedChromosome = (Chromosome) jcbChromozomeNumber.getSelectedItem();
			num.setMinimum(new Long(0));
			num.setMaximum(new Long(selectedChromosome.getLength()));
		}
	}
	
	/**
	 * @return the selected User Specified Start Value
	 */
	public long getUserSpecifiedStart()
	{
		try
		{
			if(jtfUserStart.isEnabled())
			{
				userSpecifiedStart = (Long)jtfUserStart.getValue();
				return userSpecifiedStart;
			}
		}
		catch(NumberFormatException e)
		{
			System.err.println("Please enter a valid start value");
			e.printStackTrace();
		}
		return 0;		
	}
	
	/**
	 * @return the selected User Specified Stop Value
	 */
	public long getUserSpecifiedStop()
	{
		try
		{
			if(jtfUserEnd.isEnabled()) 
			{
				userSpecifiedStop = (Long)jtfUserEnd.getValue();
				return userSpecifiedStop;
			}
		}
		catch(NumberFormatException e)
		{
			System.err.println("Please enter a valid stop value");
			e.printStackTrace();
		}
		return 0;		
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
