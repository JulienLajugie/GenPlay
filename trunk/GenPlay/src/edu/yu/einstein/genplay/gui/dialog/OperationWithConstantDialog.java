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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.dataStructure.enums.OperationWithConstant;
import edu.yu.einstein.genplay.gui.action.layer.SCWLayer.SCWLAOperationWithConstant;
import edu.yu.einstein.genplay.util.NumberFormats;


/**
 * Dialog to select an operation and a constant value for a {@link SCWLAOperationWithConstant} action.
 * @author Julien Lajugie
 */
public class OperationWithConstantDialog extends JDialog {

	/** Generated serial ID */
	private static final long serialVersionUID = -6398583070690379524L;
	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int	approved = CANCEL_OPTION;				// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	private JFormattedTextField jftfConstant;			// the text field to enter the constant
	private JComboBox			jcbOperationType;		// combo box to select the type of operation
	private JCheckBox			jcbApplyToNullWindows;	// check box apply to null windows

	/**
	 * Creates an instance of {@link OperationWithConstantDialog}
	 */
	public OperationWithConstantDialog() {
		JPanel jpOperation = getOperationPanel();
		JPanel jpValidation = getValidationPanel();

		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		add(jpOperation);
		add(jpValidation);

		pack();
		setResizable(false);
	}


	/**
	 * @return true if the operation should be applied to null windows
	 */
	public boolean getApplyToNullWindows() {
		return jcbApplyToNullWindows.isSelected();
	}


	/**
	 * @return the value of the constant
	 */
	public float getConstant() {
		return ((Number) jftfConstant.getValue()).floatValue();
	}


	/**
	 * @return the selected {@link OperationWithConstant}
	 */
	public OperationWithConstant getOperation() {
		return (OperationWithConstant) jcbOperationType.getSelectedItem();
	}


	/**
	 * @return the panel to select the operation and the value of the constant
	 */
	private JPanel getOperationPanel() {
		final JLabel jlOperationDescription = new JLabel();
		JLabel jlOperation = new JLabel("Choose an operation:");
		JLabel jlConstant = new JLabel("Where constant = ");
		//JLabel jlApplyToNullWindows = new JLabel("A")
		jftfConstant = new JFormattedTextField(NumberFormats.getScoreFormat());
		jftfConstant.setColumns(8);
		jftfConstant.setValue(0);
		jcbOperationType = new JComboBox(OperationWithConstant.values());
		jcbOperationType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				jlOperationDescription.setText(((OperationWithConstant) jcbOperationType.getSelectedItem()).getDescription());
			}
		});
		jcbOperationType.setSelectedItem(OperationWithConstant.ADDITION);
		jlOperationDescription.setText(((OperationWithConstant) jcbOperationType.getSelectedItem()).getDescription());

		jcbApplyToNullWindows = new JCheckBox("Apply to null windows: ");


		// Creates the panel
		JPanel jPanel = new JPanel();
		jPanel.setBorder(BorderFactory.createTitledBorder("Operation"));

		// Layout settings
		GridBagLayout layout = new GridBagLayout();
		jPanel.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;

		gbc.gridx = 0;
		gbc.gridy = 0;
		jPanel.add(jlOperation, gbc);
		gbc.gridx = 1;
		jPanel.add(jcbOperationType, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 10, 0);
		jPanel.add(jlOperationDescription, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		jPanel.add(jlConstant, gbc);

		gbc.gridx = 1;
		jPanel.add(jftfConstant, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 0, 0);
		jPanel.add(jcbApplyToNullWindows, gbc);

		return jPanel;
	}


	/**
	 * Creates the panel that contains OK and CANCEL buttons
	 * @return the panel
	 */
	private JPanel getValidationPanel () {
		// Creates the ok button
		JButton jbOk = new JButton("Ok");
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = APPROVE_OPTION;
				setVisible(false);
			}
		});

		// Creates the cancel button
		JButton jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				approved = CANCEL_OPTION;
				setVisible(false);
			}
		});

		// we want the size of the two buttons to be equal
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * Shows the component.
	 * @param parent 	the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);
		return approved;
	}
}
