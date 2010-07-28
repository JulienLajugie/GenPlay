/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.enums.DataPrecision;


/**
 * Panel for the data precision input of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class DataPrecisionPanel extends JPanel {

	private static final long serialVersionUID = -2255804422921021285L; 				// generated ID
	private final JComboBox 		jcbDataPrecision; 									// combo box for the data precision 
	private static DataPrecision 	defaultPrecision = DataPrecision.PRECISION_32BIT; 	// default data precision
	
	
	/**
	 * Creates an instance of {@link DataPrecisionPanel}
	 */
	DataPrecisionPanel() {
		super();
		jcbDataPrecision = new JComboBox(DataPrecision.values());
		jcbDataPrecision.setSelectedItem(defaultPrecision);
		add(jcbDataPrecision);
		setBorder(BorderFactory.createTitledBorder("Data Precision"));
	}
	
	
	/**
	 * @return the selected {@link DataPrecision}
	 */
	DataPrecision getDataPrecision() {
		return (DataPrecision) jcbDataPrecision.getSelectedItem();
	}
	
	
	/**
	 * Saves the selected data precision as default
	 */
	void saveDefault() {
		defaultPrecision = getDataPrecision();
	}
}
