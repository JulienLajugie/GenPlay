/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;


/**
 * A dialog box used to choose a {@link ScoreCalculation}. 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ScoreCalculationChooser extends JDialog {

	private static final long serialVersionUID = -5654898399773009796L; // generated ID

	private static JComboBox 	jcbScoreCalculationOption;	// JComboBox with the different options
	private static JLabel 		jlText;						// label of the box
	private static JButton 		jbOk;						// button OK
	private static JButton 		jbCancel;					// button Cancel
	private static boolean 		validated = false;			// true if OK has been pressed
	
	
	/**
	 * Private constructor. Creates an instance of {@link ScoreCalculationChooser}
	 * @param parent The Component from which the dialog is displayed
	 */
	private ScoreCalculationChooser(Component parent) {
		super();
		setModal(true);
		setTitle("Score Calculation");
		initComponent();
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);		
		setLocationRelativeTo(parent);		
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlText = new JLabel("Choose a method for the calculation of the score");		
		
		jcbScoreCalculationOption = new JComboBox(ScoreCalculationMethod.values());		
		
		jbOk = new JButton("OK");
		jbOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				validated = true;
				dispose();
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		add(jlText, c);
		
		c.gridy = 1;
		add(jcbScoreCalculationOption, c);
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		c.gridy = 2;
		c.gridwidth = 1;
		add(jbOk, c);
		
		c.gridx = 1;
		c.gridy = 2;
		add(jbCancel, c);		
	}
	
	
	/**
	 * Shows a dialog box asking the user to choose a method for the score calculation of a {@link BinList}
	 * @param parent The Component from which the dialog is displayed
	 * @return an Integer representing the score calculation method of a {@link BinList}
	 */
	public static ScoreCalculationMethod getScoreCalculation(Component parent) {
		new ScoreCalculationChooser(parent).setVisible(true);
		if (validated) {
			return (ScoreCalculationMethod) jcbScoreCalculationOption.getSelectedItem();
		} else {
			return null;
		}
	}
}
