/**
 * Contains the GUI files of the Genomic Data Processor.
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * An implementation of an input option pane for number.
 * The format, the maximum and the minimum value of the input value can be specified.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TwoNumbersOptionPane extends JDialog {

	private static final long 			serialVersionUID = 8778240527196019885L;	// Generated serial number
	private static JLabel 				jl1;										// first label of the option pane
	private static JFormattedTextField 	jftfValue1;									// first text field for the input
	private static JLabel 				jl2;										// second label of the option pane
	private static JFormattedTextField 	jftfValue2;									// second text field for the input
	private static JButton 				jbOk; 										// Button OK
	private static JButton 				jbCancel;									// Button Cancel
	private static Number 				validValue1;								// first valid number to return
	private static Number 				validValue2;								// second valid number to return
	private static double 				minValidValue;								// Max value of the input
	private static double 				maxValidValue;								// Min value of the input
	private static DecimalFormat 		decimalFormat;								// Format of the input
	private static String 				title;										// Title of the dialog
	private static String 				label1;										// Text of the first JLabel
	private static String 				label2;										// Text of the second JLabel
	private static boolean 				validated;									// True if OK has been pressed

	
	/**
	 * Private constructor. Used internally to create a NumberOptionPane dialog. 
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @param aTitle Title of the dialog
	 * @param aLabel1 Text of the first inside label of the dialog
	 * @param aLabel2 Text of the second inside label of the dialog
	 * @param df DecimalFormat of the input value
	 * @param defaultValue1 first default displayed value when the dialog is displayed
	 * @param defaultValue2 second default displayed value when the dialog is displayed
	 * @param min Minimum allowed value for the input value
	 * @param max Maximum allowed value for the input value
	 */
	private TwoNumbersOptionPane(Component parent, String aTitle, String aLabel1, String aLabel2, DecimalFormat df, double defaultValue1, double defaultValue2, double min, double max) {
		super();
		setModal(true);
		title = aTitle;
		label1 = aLabel1;
		label2 = aLabel2;
		decimalFormat = df;
		validValue1 = defaultValue1;
		validValue2 = defaultValue2;
		minValidValue = min;
		maxValidValue = max;
		validated = false;
		initComponent();	
		setTitle(title);
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	
	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		if(label1 != null) {
			jl1 = new JLabel(label1);
		}
		if(label2 != null) {
			jl2 = new JLabel(label2);
		}
		
		
		jftfValue1 = new JFormattedTextField(decimalFormat);
		jftfValue1.setValue(validValue1);
		jftfValue1.setColumns(8);
		jftfValue1.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftf1ValuePropertyChange();
			}
		});
		jftfValue1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					validated = true;
					dispose();
				}
			}
		});

		
		jftfValue2 = new JFormattedTextField(decimalFormat);
		jftfValue2.setValue(validValue2);
		jftfValue2.setColumns(8);
		jftfValue2.addPropertyChangeListener(new PropertyChangeListener() {				
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftf2ValuePropertyChange();
			}
		});
		jftfValue2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					validated = true;
					dispose();
				}
			}
		});
		
		jbOk = new JButton("Ok");
		jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();				
			}
		});
		
		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();				
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		if(label1 != null) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.weightx = 0.1;
			c.weighty = 0.1;
			add(jl1, c);
		}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jftfValue1, c);

		
		if(label2 != null) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 2;
			c.gridwidth = 2;
			c.weightx = 0.1;
			c.weighty = 0.1;
			add(jl2, c);
		}
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jftfValue2, c);
		
		
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(jbOk, c);

		c.gridx = 1;
		add(jbCancel, c);		
	}

	
	/**
	 * Closes the dialog. No action are performed.
	 */
	private void jbCancelActionPerformed() {
		this.dispose();
	}

	
	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected curves.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		this.dispose();		
	}

	
	/**
	 * Called when the value of the fieldText jftfValue1 changes.
	 * Check if the new value is between min and max.
	 */
	private void jftf1ValuePropertyChange() {
		double currentValue = ((Number)(jftfValue1.getValue())).doubleValue();

		if((currentValue < minValidValue) || (currentValue > maxValidValue)) {
			JOptionPane.showMessageDialog(this, "The input value must be between " + decimalFormat.format(minValidValue) + " and " + decimalFormat.format(maxValidValue) + "", "Incorrect value.", JOptionPane.WARNING_MESSAGE);
			jftfValue1.setValue(validValue1);
		} else {
			validValue1 = currentValue;
		}
	}

	
	/**
	 * Called when the value of the fieldText jftfValue2 changes.
	 * Check if the new value is between min and max.
	 */
	private void jftf2ValuePropertyChange() {
		double currentValue = ((Number)(jftfValue2.getValue())).doubleValue();

		if((currentValue < minValidValue) || (currentValue > maxValidValue)) {
			JOptionPane.showMessageDialog(this, "The input value must be between " + decimalFormat.format(minValidValue) + " and " + decimalFormat.format(maxValidValue) + "", "Incorrect value.", JOptionPane.WARNING_MESSAGE);
			jftfValue2.setValue(validValue2);
		} else {
			validValue2 = currentValue;
		}
	}
	
	
	/**
	 * Displays a GdpCurveChooser dialog, and returns a Number.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details 
	 * @param title Title of the dialog.
	 * @param label1 Text of the first inside label of the dialog.
	 * @param label2 Text of the second inside label of the dialog.
	 * @param df DecimalFormat of the input value.
	 * @param min Minimum allowed value for the input value. 
	 * @param max Maximum allowed value for the input value.
	 * @param defaultValue1 first default displayed value when the dialog is displayed.
	 * @param defaultValue2 second default displayed value when the dialog is displayed.
	 * @return A number if OK has been pressed, otherwise null.
	 */
	public static Number[] getValue(Component parent, String title, String label1, String label2, DecimalFormat df, double min, double max, double defaultValue1, double defaultValue2) {
		TwoNumbersOptionPane NOP = new TwoNumbersOptionPane(parent, title, label1, label2, df, defaultValue1, defaultValue2, min, max);
		NOP.setVisible(true);	
		if(validated) {
			Number[] result = {validValue1, validValue2};
			return result;
		} else {
			return null;
		}
	}
}
