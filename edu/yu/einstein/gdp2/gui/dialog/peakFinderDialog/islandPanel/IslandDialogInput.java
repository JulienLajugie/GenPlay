/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.peakFinderDialog.islandPanel;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

import yu.einstein.gdp2.core.list.binList.operation.peakFinder.IslandFinder;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;
import yu.einstein.gdp2.exception.InvalidLambdaPoissonParameterException;

/**
 * This panel shows input parameters for the island finder frame settings
 * 
 * @author Nicolas
 * @version 0.1
 */
final class IslandDialogInput extends IslandDialogFieldset{
	
	private static final long serialVersionUID = -1616307602412859645L;
	
	private final IslandDialogInformation	dialogInformation;
	
	//Constant values
	private static final int NAME_WIDTH =  (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.27);
	private static final int INEQUALITY_WIDTH =  (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.05);
	private static final int MIN_VALUE_WIDTH =  (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.2);
	private static final int UNIT_WIDTH =  (int)Math.round(IslandDialogFieldset.FIELDSET_WIDTH * 0.3);
	private static final String ICON_ACTUALIZE_PATH = "yu/einstein/gdp2/resource/actualize15x15.png";
	private static final String ICON_SUPERIOR_PATH = "yu/einstein/gdp2/resource/superior8x6.png";
	private static final String ICON_INFERIOR_PATH = "yu/einstein/gdp2/resource/inferior8x6.png";
	
	//Storage values
	protected static Double 	windowMinValueStore;
	protected static Integer 	gapStore;
	protected static Double 	IslandMinScoreStore;
	protected static Integer 	IslandMinLengthStore;
	
	//Graphics elements
	private final JLabel 				jlWindowMinValueName;			//for the window limit value
	private final JFormattedTextField 	jftfWindowMinValueValue;
	private final JLabel 				jlWindowMinValueUnit;
	private final JButton				jbConvertToPValue;
	private final ImageIcon				iActualize;
	
	private final JLabel 				jlGapName;						//for the gap
	private final JFormattedTextField 	jftfGapValue;
	private final JLabel 				jlGapUnit;
	
	private final JLabel 				jlIslandMinScoreName;			//for the island limit score
	private final JFormattedTextField 	jftfIslandMinScoreValue;
	private final JLabel 				jlIslandMinScoreUnit;
	
	private final JLabel 				jlIslandMinLengthName;			//for the minimum island length
	private final JFormattedTextField 	jftfIslandMinLengthValue;
	private final JLabel 				jlIslandMinLengthUnit;
	
	private final JLabel				jlSuperior01;
	private final JLabel				jlSuperior02;
	private final JLabel				jlSuperior03;
	private final JLabel				jlInferior;
	
	/**
	 * Constructor for IslandDialogInput
	 * @param title		fieldset title
	 * @param island	IslandFinder object to set some information
	 * @param dialog	necessary to communicate with the p-value
	 */
	IslandDialogInput(String title, IslandFinder island, IslandDialogInformation dialog) {
		super(title, island);
		
		this.dialogInformation = dialog;
		
		//Digits Format
		NumberFormat floatFormat = NumberFormat.getInstance();
		floatFormat.setMinimumFractionDigits(2);
		floatFormat.setMaximumFractionDigits(2);
		NumberFormat integerFormat = NumberFormat.getInstance();
		integerFormat.setMaximumFractionDigits(0);
		
		//Number Format
		NumberFormatter positiveIntegerFormat = new NumberFormatter(integerFormat);
		positiveIntegerFormat.setMinimum(0);
		NumberFormatter lenghtFormat = new NumberFormatter(integerFormat);
		lenghtFormat.setMinimum(1);
		NumberFormatter positiveFloatFormat = new NumberFormatter(floatFormat);
		positiveFloatFormat.setMinimum(0.0);
		
		//Class loader
		ClassLoader cl = this.getClass().getClassLoader();
		
		//Set "window limit value" information
		this.jlWindowMinValueName = new JLabel("Window value");
		this.jftfWindowMinValueValue = new JFormattedTextField(positiveFloatFormat);
		this.jftfWindowMinValueValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		this.jftfWindowMinValueValue.setValue(getDoubleStoredValue(windowMinValueStore, 0.0));
		this.jlWindowMinValueUnit = new JLabel("window value");
		this.iActualize = new ImageIcon(Toolkit.getDefaultToolkit().getImage(cl.getResource(IslandDialogInput.ICON_ACTUALIZE_PATH)));
		this.jbConvertToPValue = new JButton(this.iActualize);
		
		//Set "gap" information
		this.jlGapName = new JLabel("Gap");
		this.jftfGapValue = new JFormattedTextField(positiveIntegerFormat);
		this.jftfGapValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		this.jftfGapValue.setValue(getIntStoredValue(gapStore, 0));
		this.jlGapUnit = new JLabel("window(s)");
		
		//Set "island limit score" information
		this.jlIslandMinScoreName = new JLabel("Island score");
		this.jftfIslandMinScoreValue = new JFormattedTextField(positiveFloatFormat);
		this.jftfIslandMinScoreValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		this.jftfIslandMinScoreValue.setValue(getDoubleStoredValue(IslandMinScoreStore, 0.0));
		this.jlIslandMinScoreUnit = new JLabel("-");
		
		//Set "minimum island length" information
		this.jlIslandMinLengthName = new JLabel("Island length");
		this.jftfIslandMinLengthValue = new JFormattedTextField(lenghtFormat);
		this.jftfIslandMinLengthValue.setHorizontalAlignment(JFormattedTextField.RIGHT);
		this.jftfIslandMinLengthValue.setValue(getIntStoredValue(IslandMinLengthStore, 1));
		this.jlIslandMinLengthUnit = new JLabel("window(s)");
		
		//Set inequality label
		ImageIcon superior = new ImageIcon(Toolkit.getDefaultToolkit().getImage(cl.getResource(IslandDialogInput.ICON_SUPERIOR_PATH)));
		ImageIcon inferior = new ImageIcon(Toolkit.getDefaultToolkit().getImage(cl.getResource(IslandDialogInput.ICON_INFERIOR_PATH)));
		this.jlSuperior01 = new JLabel(superior);
		this.jlSuperior02 = new JLabel(superior);
		this.jlSuperior03 = new JLabel(superior);
		this.jlInferior = new JLabel(inferior);
		this.jlSuperior01.setHorizontalTextPosition(SwingConstants.CENTER);
		this.jlSuperior01.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlSuperior02.setHorizontalTextPosition(SwingConstants.CENTER);
		this.jlSuperior02.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlSuperior03.setHorizontalTextPosition(SwingConstants.CENTER);
		this.jlSuperior03.setVerticalTextPosition(SwingConstants.CENTER);
		this.jlInferior.setHorizontalTextPosition(SwingConstants.CENTER);
		this.jlInferior.setVerticalTextPosition(SwingConstants.CENTER);
		
		//Dimension PreferredSize
		this.jlWindowMinValueName.setPreferredSize(new Dimension(IslandDialogInput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jftfWindowMinValueValue.setPreferredSize(new Dimension(IslandDialogInput.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlWindowMinValueUnit.setPreferredSize(new Dimension(IslandDialogInput.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jbConvertToPValue.setPreferredSize(new Dimension(IslandDialogInput.LINE_HEIGHT, IslandDialogFieldset.LINE_HEIGHT));
		this.jlGapName.setPreferredSize(new Dimension(IslandDialogInput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jftfGapValue.setPreferredSize(new Dimension(IslandDialogInput.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlGapUnit.setPreferredSize(new Dimension(IslandDialogInput.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlIslandMinScoreName.setPreferredSize(new Dimension(IslandDialogInput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jftfIslandMinScoreValue.setPreferredSize(new Dimension(IslandDialogInput.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlIslandMinScoreUnit.setPreferredSize(new Dimension(IslandDialogInput.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlIslandMinLengthName.setPreferredSize(new Dimension(IslandDialogInput.NAME_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jftfIslandMinLengthValue.setPreferredSize(new Dimension(IslandDialogInput.MIN_VALUE_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlIslandMinLengthUnit.setPreferredSize(new Dimension(IslandDialogInput.UNIT_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlSuperior01.setPreferredSize(new Dimension(IslandDialogInput.INEQUALITY_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlSuperior02.setPreferredSize(new Dimension(IslandDialogInput.INEQUALITY_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlSuperior03.setPreferredSize(new Dimension(IslandDialogInput.INEQUALITY_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		this.jlInferior.setPreferredSize(new Dimension(IslandDialogInput.INEQUALITY_WIDTH, IslandDialogFieldset.LINE_HEIGHT));
		
		//Tool Tip Text
		String sWindowLimitValue = "All values below this value will be ignored";
		String sConvertToPValue = "Convert window limit value to p-value";
		String sGap = "Number of window authorized below this value to select island";
		String sIlsandLimitScore = "All island score below this value will be ignored";
		String sMinIslandLength = "All island score composed of a number of window below this value will be ignored";
		this.jlWindowMinValueName.setToolTipText(sWindowLimitValue);
		this.jftfWindowMinValueValue.setToolTipText(sWindowLimitValue);
		this.jbConvertToPValue.setToolTipText(sConvertToPValue);
		this.jlGapName.setToolTipText(sGap);
		this.jftfGapValue.setToolTipText(sGap);
		this.jlIslandMinScoreName.setToolTipText(sIlsandLimitScore);
		this.jftfIslandMinScoreValue.setToolTipText(sIlsandLimitScore);
		this.jlIslandMinLengthName.setToolTipText(sMinIslandLength);
		this.jftfIslandMinLengthValue.setToolTipText(sMinIslandLength);
		
		//Listeners
		this.jbConvertToPValue.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					toPValue();
				} catch (InvalidLambdaPoissonParameterException e) {
					e.printStackTrace();
				} catch (InvalidFactorialParameterException e) {
					e.printStackTrace();
				}
			}
		});
		
		//Layout
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		Insets gbcInsets = new Insets (IslandDialogFieldset.LINE_TOP_INSET_HEIGHT, 0, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		Insets gbcInsetsUnit = new Insets (IslandDialogFieldset.LINE_TOP_INSET_HEIGHT, 7, IslandDialogFieldset.LINE_BOTTOM_INSET_HEIGHT, 0);
		
		// jlWindowMinValueName
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlWindowMinValueName, gbc);
		
		// jlSuperior01
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		//this.add(this.jlSuperior, gbc);
		this.add(this.jlSuperior01, gbc);
		
		// jftfWindowMinValueValue
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jftfWindowMinValueValue, gbc);
		
		// jlWindowMinValueUnit
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsetsUnit;
		this.add(this.jlWindowMinValueUnit, gbc);
		
		// jbConvertToPValue
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jbConvertToPValue, gbc);
		
		// jlGapName
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlGapName, gbc);
		
		// jlInferior
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlInferior, gbc);
		
		// jftfGapValue
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jftfGapValue, gbc);
		
		// jlGapUnit
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsetsUnit;
		this.add(this.jlGapUnit, gbc);
		
		// jlIslandMinScoreName
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlIslandMinScoreName, gbc);
		
		// jlSuperior02
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlSuperior02, gbc);
		
		// jftfIslandMinScoreValue
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jftfIslandMinScoreValue, gbc);
		
		// jlIslandMinScoreUnit
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsetsUnit;
		this.add(this.jlIslandMinScoreUnit, gbc);
		
		// jlIslandMinLengthName
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsets;
		this.add(this.jlIslandMinLengthName, gbc);
		
		// jlSuperior03
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jlSuperior03, gbc);
		
		// jftfIslandMinLengthValue
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.insets = gbcInsets;
		this.add(this.jftfIslandMinLengthValue, gbc);
		
		// jlIslandMinLengthUnit
		gbc.gridx = 3;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.WEST;
		gbc.insets = gbcInsetsUnit;
		this.add(this.jlIslandMinLengthUnit, gbc);
		
		// Dimension
		this.setRows(gbc.gridy + 1);
		
		this.setVisible(true);
	}
	
	/**
	 * Manage the conversion of read count limit to p-value.
	 * @throws InvalidFactorialParameterException 
	 * @throws InvalidLambdaPoissonParameterException 
	 */
	private void toPValue () throws InvalidLambdaPoissonParameterException, InvalidFactorialParameterException {
		Double read;
		Double pvalue;
		if (this.jftfWindowMinValueValue.getValue() != null) {
			try {
				this.jftfWindowMinValueValue.commitEdit();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			read = Double.parseDouble(this.jftfWindowMinValueValue.getValue().toString());
			pvalue = this.getIsland().findPValue(read);
			this.dialogInformation.getJlPValueValue().setText(pvalue.toString());
		}
	}
	
	/**
	 * This method check a Double value to return it if it is not null, or return the initial value
	 * 
	 * @param value		value to return
	 * @param initial	returned value if 'value' is null
	 * @return			Double
	 */
	private Double getDoubleStoredValue (Double value, Double initial) {
		if (value != null) {
			return value;
		} else {
			return initial;
		}
	}
	
	/**
	 * This method check an Integer value to return it if it is not null, or return the initial value
	 * 
	 * @param value		value to return
	 * @param initial	returned value if 'value' is null
	 * @return			Integer
	 */
	private Integer getIntStoredValue (Integer value, Integer initial) {
		if (value != null) {
			return value;
		} else {
			return initial;
		}
	}
	
	//Getters
	protected Double getWindowLimitValue () {
		return Double.parseDouble(this.jftfWindowMinValueValue.getValue().toString());
	}
	
	protected int getGap () {
		return Integer.parseInt(this.jftfGapValue.getValue().toString());
	}
	
	protected Double getIslandLimitScore () {
		return Double.parseDouble(this.jftfIslandMinScoreValue.getValue().toString());
	}
	
	protected int getMinIslandLength () {
		return Integer.parseInt(this.jftfIslandMinLengthValue.getValue().toString());
	}
}