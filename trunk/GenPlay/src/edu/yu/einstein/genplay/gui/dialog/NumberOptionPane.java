/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.NumberFormats;

/**
 * An implementation of an input option pane for number.
 * The format, the maximum and the minimum value of the input value can be specified.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class NumberOptionPane extends JDialog {

	private static final long 			serialVersionUID = 8778240527196019885L;	// Generated serial number
	private static JLabel 				jl;											// Label of the option pane
	private static JFormattedTextField 	jftfValue;									// Text field for the input
	private static JButton 				jbOk; 										// Button OK
	private static JButton 				jbCancel;									// Button Cancel
	private static Number 				validValue;									// Valid number to return
	private static double 				minValidValue;								// Max value of the input
	private static double 				maxValidValue;								// SCWLAMin value of the input
	private static String 				title;										// Title of the dialog
	private static String 				label;										// Text of the JLabel jl
	private static boolean 				validated;									// True if OK has been pressed


	/**
	 * Displays a dialog, and returns a Number.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param title Title of the dialog.
	 * @param label Text of the inside label of the dialog.
	 * @param min Minimum allowed value for the input value.
	 * @param max Maximum allowed value for the input value.
	 * @param defaultValue Default displayed value when the dialog is displayed.
	 * @return A number if OK has been pressed, otherwise null.
	 */
	public static Number getValue(Component parent, String title, String label, double min, double max, double defaultValue) {
		NumberOptionPane NOP = new NumberOptionPane(parent, title, label, defaultValue, min, max);
		NOP.setVisible(true);
		if(validated) {
			return validValue;
		} else {
			return null;
		}
	}


	/**
	 * Displays a dialog asking for a window size and returns a Number.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param defaultValue Default displayed value when the dialog is displayed.
	 * @return A number if OK has been pressed, otherwise null.
	 */
	public static Integer getValueWindow(Component parent, int defaultValue) {
		NumberOptionPane NOP = new NumberOptionPane(parent, "Window size:", null, defaultValue, 1, 1000000);
		NOP.setVisible(true);
		if (validated) {
			return validValue.intValue();
		} else {
			return null;
		}
	}


	/**
	 * Private constructor. Used internally to create a NumberOptionPane dialog.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param aTitle Title of the dialog
	 * @param aLabel Text of the inside label of the dialog
	 * @param defaultValue Default displayed value when the dialog is displayed
	 * @param min Minimum allowed value for the input value
	 * @param max Maximum allowed value for the input value
	 */
	private NumberOptionPane(Component parent, String aTitle, String aLabel, double defaultValue, double min, double max) {
		super();
		setModalityType(ModalityType.APPLICATION_MODAL);
		title = aTitle;
		label = aLabel;
		validValue = defaultValue;
		minValidValue = min;
		maxValidValue = max;
		validated = false;
		initComponent();
		setTitle(title);
		setIconImages(Images.getApplicationImages());
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}


	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		if(label != null) {
			jl = new JLabel(label);
		}

		jftfValue = new JFormattedTextField(NumberFormats.getScoreFormat());
		jftfValue.setValue(validValue);
		jftfValue.setColumns(8);
		jftfValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				jftfValuePropertyChange();
			}
		});
		jftfValue.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					validated = true;
					dispose();
				}
			}
		});

		jbOk = new JButton("Ok");
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();
			}
		});

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		if(label != null) {
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			c.gridwidth = 2;
			c.weightx = 0.1;
			c.weighty = 0.1;
			add(jl, c);
		}

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jftfValue, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
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
		dispose();
	}


	/**
	 * Closes the dialog. Sets validated to true so the main function can return the two selected curves.
	 */
	private void jbOkActionPerformed() {
		validated = true;
		dispose();
	}


	/**
	 * Called when the value of the fieldText jftfValue changes.
	 * Check if the new value is between min and max.
	 */
	private void jftfValuePropertyChange() {
		double currentValue = ((Number)(jftfValue.getValue())).doubleValue();

		if((currentValue < minValidValue) || (currentValue > maxValidValue)) {
			JOptionPane.showMessageDialog(this, "The input value must be between " + NumberFormat.getInstance().format(minValidValue) + " and " + NumberFormat.getInstance().format(maxValidValue) + "", "Incorrect value.", JOptionPane.WARNING_MESSAGE);
			jftfValue.setValue(validValue);
		} else {
			validValue = currentValue;
		}
	}
}
