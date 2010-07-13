/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.filterDialog;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.enums.FilterType;


/**
 * {@link FilterPanel} for the band-stop filters
 * @author Julien Lajugie
 * @version 0.1
 */
final class BandStopPanel extends FilterPanel {

	private static final long serialVersionUID = -8470118628769444167L;	// generated ID
	private final static String NAME = FilterType.BANDSTOP.toString();	// name of the filter
	private final static String FILTER_DESCRIPTION = 
		"Filter values between two specified threshold values\n";		// description of the filter
	private final static String TEXT_MIN = "Filter values between";		// text of the min label
	private final static String TEXT_MAX = "And";						// text of the max label
	private final static DecimalFormat DF = new DecimalFormat("0.0");	// decimal format for the input numbers
	private static Number 	defaultMin = 0;								// default/last min value
	private static Number 	defaultMax = 100;							// default/last max value
	private static boolean 	defaultIsSaturation = false;				// default/last saturation state
	
	
	/**
	 * Creates an instance of {@link BandStopPanel}
	 */
	BandStopPanel() {
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
