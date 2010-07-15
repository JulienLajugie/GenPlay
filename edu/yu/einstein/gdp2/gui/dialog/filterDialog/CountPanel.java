/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.filterDialog;

import java.text.DecimalFormat;

import yu.einstein.gdp2.core.enums.FilterType;


/**
 * {@link FilterPanel} for the count filters
 * @author Julien Lajugie
 * @version 0.1
 */
final class CountPanel extends FilterPanel {

	private static final long serialVersionUID = -6666939773683020846L;				// generated ID
	private final static String NAME = FilterType.COUNT.toString();					// name of the filter
	private final static String FILTER_DESCRIPTION = "Filter the X lowest values and the Y greatest values,\n" +
			"where X and Y are two specified integers";								// description of the filter
	private final static String TEXT_MIN = "Number of lowest values to filter";		// text of the min label
	private final static String TEXT_MAX = "Number of greatest values to filter";	// text of the max label
	private final static DecimalFormat 	DF = new DecimalFormat("0");				// decimal format for the input numbers
	private static Number 	defaultMin = 0;											// default/last min value
	private static Number 	defaultMax = 0;											// default/last max value
	private static boolean 	defaultIsSaturation = false;							// default/last saturation state
	
	
	/**
	 * Creates an instance of {@link CountPanel}
	 */
	CountPanel() {
		super(NAME, FILTER_DESCRIPTION, TEXT_MIN, TEXT_MAX, DF, defaultMin, defaultMax, defaultIsSaturation);
	}

	
	@Override
	boolean isInputValid() {
		return true;
	}


	@Override
	boolean isSaturable() {
		return true;
	}


	@Override
	void saveIsSaturation() {
		defaultIsSaturation = isSaturation();
	}


	@Override
	void saveMax() {
		defaultMax = getMaxInput();		
	}


	@Override
	void saveMin() {
		defaultMin = getMinInput();		
	}
}
