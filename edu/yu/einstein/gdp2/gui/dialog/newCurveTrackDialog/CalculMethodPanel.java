/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;


/**
 * Panel for the score calculation method of a {@link NewCurveTrackDialog} 
 * @author Julien Lajugie
 * @version 0.1
 */
class CalculMethodPanel extends JPanel {

	private static final long serialVersionUID = -2863825210102188370L;	// generated ID
	private static final int 				PANEL_WIDTH = 150;	// width of the panel
	private final JComboBox 				jcbCalculMetod; 	// combo box for the score calculation method
	private static ScoreCalculationMethod 	defaultMethod = 
		ScoreCalculationMethod.AVERAGE;							// default method of calculation
	
	
	/**
	 * Creates an instance of a {@link CalculMethodPanel}
	 */
	CalculMethodPanel() {
		super();
		jcbCalculMetod = new JComboBox(ScoreCalculationMethod.values());
		jcbCalculMetod.setSelectedItem(defaultMethod);
		add(jcbCalculMetod);
		setBorder(BorderFactory.createTitledBorder("Score Calculation"));
		setPreferredSize(new Dimension(PANEL_WIDTH, getPreferredSize().height));
	}
	
	
	/**
	 * @return the selected score calculation method
	 */
	ScoreCalculationMethod getScoreCalculationMethod() {
		return (ScoreCalculationMethod) jcbCalculMetod.getSelectedItem();
	}
	
	
	/**
	 * Saves the selected method of calculation as default
	 */
	void saveDefault() {
		defaultMethod = getScoreCalculationMethod();
	}
}
