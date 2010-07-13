/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.filterDialog;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.FilterType;


/**
 * {@link FilterPanel} for the count filters
 * @author Julien Lajugie
 * @version 0.1
 */
final class PercentagePanel extends FilterPanel {

	private static final long serialVersionUID = 931310064958672339L;					// generated ID
	private final static String NAME = FilterType.PERCENTAGE.toString();				// name of the filter
	private final static String FILTER_DESCRIPTION = "Filter the X% lowest values and the Y% greatest values,\n" +
			"X and Y are two decimals and X + Y <= 100";								// description of the filter
	private final static String TEXT_MIN = "Percentage of lowest values to filter";		// text of the min label
	private final static String TEXT_MAX = "Percentage of greatest values to filter";	// text of the max label
	private final static DecimalFormat DF = new DecimalFormat("0%");					// decimal format for the input numbers
	private static Number 	defaultMin = 0.01;											// default/last min value
	private static Number 	defaultMax = 0.01;											// default/last max value
	private static boolean 	defaultIsSaturation = false;								// default/last saturation state
	
	
	/**
	 * Creates an instance of {@link PercentagePanel}
	 */
	PercentagePanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, DF, defaultMin, defaultMax, defaultIsSaturation);
	}

	
	@Override
	protected boolean isInputValid() {
		double percentageLow = getMinInput().doubleValue();
		double percentageHigh = getMaxInput().doubleValue();
		if (percentageHigh + percentageLow > 1) {
			JOptionPane.showMessageDialog(getRootPane(), "The sum of the two percentages must be smaller than 100", "Error", JOptionPane.ERROR_MESSAGE, null);
			return false;
		} else {
			return true;
		}
	}


	@Override
	protected void saveIsSaturation() {
		defaultIsSaturation = isSaturation();
	}


	@Override
	protected void saveMax() {
		defaultMax = getMaxInput();		
	}


	@Override
	protected void saveMin() {
		defaultMin = getMinInput();		
	}
}
