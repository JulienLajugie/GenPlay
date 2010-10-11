/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.newCurveTrackDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.NumberFormatter;

import yu.einstein.gdp2.core.enums.Strand;

/**
 * Strand selection panel of a {@link NewCurveTrackDialog}
 * @author Julien Lajugie
 * @version 0.1
 */
class StrandSelectionPanel extends JPanel {

	private static final long serialVersionUID = -2426572515664231706L;	//generated ID
	private static final DecimalFormat DF = new DecimalFormat("###,###,###"); // decimal format
	private final JRadioButton			jrPlus;							// 5' Strand radio button
	private final JRadioButton			jrMinus;						// 3' Strand radio button 
	private final JRadioButton			jrBoth;							// both strands radio button
	private final ButtonGroup			radioGroup;						// group for the raio buttons
	private final JFormattedTextField 	jftfShift;						// text field shift
	private final JLabel				jlShift1;						// first label shift
	private final JLabel				jlShift2;						// second label shift
	private static boolean				jrPlusDefaultState = false;	 	// default selection state of the 5' button 
	private static boolean				jrMinusDefaultState = false;	// default selection state of the 3' button
	private static boolean				jrBothDefaultState = true;		// default selection state of the both button
	private static int					shiftDefaultValue = 0;			// default shift value 

	
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

		jlShift1 = new JLabel("Shift:  ");
		
		jftfShift = new JFormattedTextField(DF);
		((NumberFormatter) jftfShift.getFormatter()).setMinimum(0);
		jftfShift.setColumns(5);
		jftfShift.setValue(shiftDefaultValue);
		
		jlShift2 = new JLabel("bp");
				
		// add the components
		setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth = 3;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrPlus, c);
		
		c = new GridBagConstraints();
		c.gridwidth = 3;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrMinus, c);
		
		c = new GridBagConstraints();
		c.gridwidth = 3;		
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(jrBoth, c);
		
		c = new GridBagConstraints();
		c.gridy = 3;
		add(jlShift1, c);
		
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		add(jftfShift, c);
		
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 3;
		add(jlShift2, c);
		setBorder(BorderFactory.createTitledBorder("Selected Strand"));
	}


	/**
	 * @return the shift value
	 */
	int getShiftValue() {
		Number shiftNumber = ((Number) jftfShift.getValue());
		if (shiftNumber != null) {
			return shiftNumber.intValue();
		}
		return 0;
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
		shiftDefaultValue = getShiftValue();
	}
}
