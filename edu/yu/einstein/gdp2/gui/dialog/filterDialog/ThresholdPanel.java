/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.filterDialog;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.FilterType;


/**
 * {@link FilterPanel} for the threshold filter
 * @author Julien Lajugie
 * @version 0.1
 */
final class ThresholdPanel extends FilterPanel {

	private static final long serialVersionUID = 7419403825735753325L;	// generated ID
	private final static String NAME = FilterType.THRESHOLD.toString();	// name of the filter
	private final static String FILTER_DESCRIPTION = "Filter the values lower than X OR greater than Y,\n" +
			"where X and Y are two specified threshold values";			// description of the filter
	private final static String TEXT_MIN = "Filter values lower than";	// text of the min label
	private final static String TEXT_MAX = "Filter values greater than";// text of the max label
	private final static DecimalFormat DF = new DecimalFormat("0.0");	// decimal format for the input numbers
	private static Number 	defaultMin = Double.NEGATIVE_INFINITY;		// default/last min value
	private static Number 	defaultMax = Double.POSITIVE_INFINITY;		// default/last max value
	private static boolean 	defaultIsSaturation = false;				// default/last saturation state
	
	
	/**
	 * Creates an instance of {@link ThresholdPanel}
	 */
	ThresholdPanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, DF, defaultMin, defaultMax, defaultIsSaturation);
	}


	@Override
	protected boolean isInputValid() {
		double thresholdLow = getMinInput().doubleValue();
		double thresholdHigh = getMaxInput().doubleValue();
		if (thresholdHigh <= thresholdLow) {
			JOptionPane.showMessageDialog(getRootPane(), "The high threshold must be greater than the low one", "Error", JOptionPane.ERROR_MESSAGE, null);
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
