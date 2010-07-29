/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;


/**
 * Panel of a {@link NewCurveTrackDialog} with an input box for the bin size
 * @author Julien Lajugie
 * @version 0.1
 */
class BinSizePanel extends JPanel {

	private static final long serialVersionUID = -7359118518250220846L;	// generated ID
	private static final int 	MAX_BINSIZE = Integer.MAX_VALUE;		// maximum bin size
	private static final int 	SPINNER_STEP = 100; 					// step of the spinner
	private final JSpinner 		jsBinSize; 								// spinner for the binsize input
	private static int 			defaultBinSize = 1000; 					// default binsize
	
	
	/**
	 * Creates an instance of {@link BinSizePanel}
	 */
	BinSizePanel() {
		super();
		SpinnerNumberModel snm = new SpinnerNumberModel(defaultBinSize, 1, MAX_BINSIZE, SPINNER_STEP);
		jsBinSize = new JSpinner(snm);
		add(jsBinSize);
		setBorder(BorderFactory.createTitledBorder("Window Size"));
	}

	
	/**
	 * @return the selected binsize
	 */
	int getBinSize() {
		return (Integer) jsBinSize.getValue();
	}
	
	
	/**
	 * Saves the selected bin size as default
	 */
	void saveDefault() {
		defaultBinSize = getBinSize();
	}
}
