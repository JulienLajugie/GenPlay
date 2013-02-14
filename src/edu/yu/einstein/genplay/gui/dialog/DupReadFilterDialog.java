/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.dialog;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.util.Images;

/**
 * A dialog box for choosing the number of duplicates to be filtered out of a read file
 * @author Chirag Gorasia
 * @version 0.1
 */
public class DupReadFilterDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -106512525852031330L;

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private final JRadioButton jrbLE;											// JRadioButton for less than equal to
	private final JRadioButton jrbEE;											// JRadioButton for exactly equal
	private final JRadioButton jrbBetween;									// JRadioButton for a range
	private final JRadioButton jrbMT;											// JRadioButton for more than
	private int optionSelected = 1;

	private final JTextField jtLE;											// JTextField for less than equal to
	private final JTextField jtEE;											// JTextField for exactly equal
	private final JTextField jtBetweenMin;									// JTextField 1 for a min value in the range
	private final JTextField jtBetweenMax;									// JTextField 1 for a max value in the range
	private final JTextField jtMT;											// JTextField for more than

	private final JLabel jlTo;												// JLabel for And

	private final JButton jbOK;												// JButton OK
	private final JButton jbCancel;											// JButton Cancel

	private int maxDupCount;
	private int minDupCount;

	private int	approved = CANCEL_OPTION;

	/**
	 * Creates an instance of the Duplicate Reads Filter Dialog Box
	 */
	public DupReadFilterDialog() {
		super();

		jrbLE = new JRadioButton("Less than or equal to");
		jrbLE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				radioButtonChanged();
			}
		});

		jrbEE = new JRadioButton("Exactly equal to");
		jrbEE.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				radioButtonChanged();
			}
		});

		jrbBetween = new JRadioButton("Between");
		jrbBetween.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				radioButtonChanged();
			}
		});

		jrbMT = new JRadioButton("More than");
		jrbMT.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				radioButtonChanged();
			}
		});

		jtLE = new JTextField(10);
		jtEE = new JTextField(10);
		jtBetweenMin = new JTextField(10);
		jtBetweenMax = new JTextField(10);
		jtMT = new JTextField(10);

		jlTo = new JLabel(" and ");

		jbOK = new JButton("OK");
		jbOK.setDefaultCapable(true);
		jbOK.addActionListener(this);

		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(this);

		// we want the size of the two buttons to be equal
		jbOK.setPreferredSize(jbCancel.getPreferredSize());

		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(jrbLE);
		radioGroup.add(jrbEE);
		radioGroup.add(jrbBetween);
		radioGroup.add(jrbMT);
		radioGroup.setSelected(jrbLE.getModel(), true);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jrbLE, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtLE, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jrbEE, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtEE, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jrbBetween, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtBetweenMin, gbc);

		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jlTo, gbc);

		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtBetweenMax, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jrbMT, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jtMT, gbc);

		gbc.gridx = 3;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.LINE_END;
		add(jbOK, gbc);

		gbc.gridx = 4;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(jbCancel, gbc);

		pack();
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("Duplicate Reads Filter");
		setIconImage(Images.getApplicationImage());
		setVisible(false);
		jbOK.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOK);
	}

	/**
	 * Action to be performed when the radio button is changed
	 */
	protected void radioButtonChanged() {
		if (jrbLE.isSelected()) {
			optionSelected = 1;
			jtLE.setEnabled(true);
			jtEE.setEnabled(false);
			jtBetweenMin.setEnabled(false);
			jtBetweenMax.setEnabled(false);
			jtMT.setEnabled(false);
		}
		if (jrbEE.isSelected()) {
			optionSelected = 2;
			jtLE.setEnabled(false);
			jtEE.setEnabled(true);
			jtBetweenMin.setEnabled(false);
			jtBetweenMax.setEnabled(false);
			jtMT.setEnabled(false);
		}
		if (jrbBetween.isSelected()) {
			optionSelected = 3;
			jtLE.setEnabled(false);
			jtEE.setEnabled(false);
			jtBetweenMin.setEnabled(true);
			jtBetweenMax.setEnabled(true);
			jtMT.setEnabled(false);
		}
		if (jrbMT.isSelected()) {
			optionSelected = 4;
			jtLE.setEnabled(false);
			jtEE.setEnabled(false);
			jtBetweenMin.setEnabled(false);
			jtBetweenMax.setEnabled(false);
			jtMT.setEnabled(true);
		}
	}

	/**
	 * Method to return the option selected
	 * @return optionSelected
	 */
	public int getOptionSelected() {
		return optionSelected;
	}

	/**
	 * Method to return the min duplicate count
	 * @return minDupCount
	 */
	public int getMinDupCount() {
		return minDupCount;
	}

	/**
	 * Method to return the max duplicate count
	 * @return maxDupCount
	 */
	public int getMaxDupCount() {
		return maxDupCount;
	}

	/**
	 * Action to be performed when OK is clicked
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbOK) {
			approved = APPROVE_OPTION;

			if (optionSelected == 1) {
				try {
					maxDupCount = Integer.parseInt(jtLE.getText());
				} catch (NumberFormatException err) {
					JOptionPane.showMessageDialog(getRootPane(), "Please enter a valid integer value");
				}
			}
			if (optionSelected == 2) {
				try {
					minDupCount = maxDupCount = Integer.parseInt(jtEE.getText());
				} catch (NumberFormatException err) {
					JOptionPane.showMessageDialog(getRootPane(), "Please enter a valid integer value");
				}
			}
			if (optionSelected == 3) {
				try {
					minDupCount = Integer.parseInt(jtBetweenMin.getText());
					maxDupCount = Integer.parseInt(jtBetweenMax.getText());
				} catch (NumberFormatException err) {
					JOptionPane.showMessageDialog(getRootPane(), "Please enter a valid integer value");
				}
			}
			if (optionSelected == 4) {
				try {
					minDupCount = Integer.parseInt(jtMT.getText());
				} catch (NumberFormatException err) {
					JOptionPane.showMessageDialog(getRootPane(), "Please enter a valid integer value");
				}
			}
		}
		setVisible(false);
	}

	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}

	//	public static void main(String args[]) {
	//		DupReadFilterDialog df = new DupReadFilterDialog();
	//		df.showDialog(null);
	//	}
}
