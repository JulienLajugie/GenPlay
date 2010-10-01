/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import yu.einstein.gdp2.core.enums.Strand;

/**
 * Strand selection panel of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class StrandSelectionPanel extends JPanel {

	private static final long serialVersionUID = -2426572515664231706L;	//generated ID
	private final JRadioButton	jrPlus;							// 5' Strand radio button
	private final JRadioButton	jrMinus;						// 3' Strand radio button 
	private final JRadioButton	jrBoth;							// both strands radio button
	private final ButtonGroup	radioGroup;						// group for the raio buttons
	private static boolean		jrPlusDefaultState = false;	 	// default selection state of the 5' button 
	private static boolean		jrMinusDefaultState = false;	// default selection state of the 3' button
	private static boolean		jrBothDefaultState = true;		// default selection state of the both button


	/**
	 * Creates an instance of {@link StrandSelectionPanel}
	 */
	StrandSelectionPanel() {
		jrPlus = new JRadioButton("5' Strand");
		jrMinus = new JRadioButton("3' Strand");
		jrBoth = new JRadioButton("Both Strands");

		radioGroup = new ButtonGroup();
		radioGroup.add(jrPlus);
		radioGroup.add(jrMinus);
		radioGroup.add(jrBoth);

		jrPlus.setSelected(jrPlusDefaultState);
		jrMinus.setSelected(jrMinusDefaultState);
		jrBoth.setSelected(jrBothDefaultState);

		// add the components
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		add(jrPlus);
		add(jrMinus);
		add(jrBoth);
		
		setBorder(BorderFactory.createTitledBorder("Selected Strand"));
	}


	/**
	 * @return the Strand to extract. Null if both
	 */
	Strand getStrandToExtract() {
		if (jrPlus.isSelected()) {
			return Strand.FIVE;
		} else if (jrMinus.isSelected()) {
			return Strand.THREE;
		} else {
			return null;
		}
	}


	/**
	 * Saves the selected state of the radio buttons
	 */
	void saveDefault() {
		jrPlusDefaultState = jrPlus.isSelected();
		jrMinusDefaultState = jrMinus.isSelected();
		jrBothDefaultState = jrBoth.isSelected();
	}
}
