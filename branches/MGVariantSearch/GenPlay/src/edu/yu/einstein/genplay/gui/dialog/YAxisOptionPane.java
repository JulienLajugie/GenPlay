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
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * An implementation of an input option pane for number.
 * The format, the maximum and the minimum value of the input value can be specified.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class YAxisOptionPane extends JDialog implements ChangeListener {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int CANCEL_OPTION = 1;

	private static final long 	serialVersionUID = 8778240527196019885L;	// Generated serial number
	private JLabel 				jlYMin;										// minimum y value label
	private JFormattedTextField jftfYMin;									// minimum y value text field
	private JLabel 				jlYMax;										// maximum y value label
	private JFormattedTextField jftfYMax;									// maximum y value text field
	private JLabel				jlYAutoscale;								// autoscale label
	private JCheckBox			jcbYAutoscale;								// autoscale check box
	private JButton				jbOk; 										// button OK
	private JButton				jbCancel;									// button Cancel
	private int					approved;									// approved or canceled


	/**
	 * Private constructor. Used internally to create a NumberOptionPane dialog. 
	 */
	public YAxisOptionPane() {
		super();
		this.approved = CANCEL_OPTION;
		initComponent();
		setTitle("Y Axis");
		getRootPane().setDefaultButton(jbOk);
		pack();
		setResizable(false);
	}


	/**
	 * Creates the component and all the subcomponents.
	 */
	private void initComponent() {
		jlYMin = new JLabel("Y-Axis Minimum Value:");
		jlYMax = new JLabel("Y-Axis Maximum Value:");
		jlYAutoscale = new JLabel("Y-Axis Autoscale:");

		DecimalFormat decimalFormat = new DecimalFormat("#.###");

		jftfYMin = new JFormattedTextField(decimalFormat);
		jftfYMin.setColumns(8);

		jftfYMax = new JFormattedTextField(decimalFormat);
		jftfYMax.setColumns(8);

		jcbYAutoscale = new JCheckBox();
		jcbYAutoscale.addChangeListener(this);

		jbOk = new JButton("Ok");
		//jbOk.setPreferredSize(new Dimension(75, 30));
		jbOk.setDefaultCapable(true);
		jbOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbOkActionPerformed();
			}
		});

		jbCancel = new JButton("Cancel");
		//jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jbCancelActionPerformed();
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.weightx = 0.1;
		c.weighty = 0.1;
		add(jlYMin, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jftfYMin, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0.1;
		c.weighty = 0.1;
		add(jlYMax, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jftfYMax, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 0.1;
		c.weighty = 0.1;
		add(jlYAutoscale, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.weighty = 0.5;
		add(jcbYAutoscale, c);

		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 6;
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
		approved = APPROVE_OPTION;
		this.dispose();
	}


	/**
	 * @return the Y axis minimum value
	 */
	public double getYMin() {
		return ((Number) jftfYMin.getValue()).doubleValue();
	}


	/**
	 * @return the Y axis maximum value
	 */
	public double getYMax() {
		return ((Number) jftfYMax.getValue()).doubleValue();
	}


	/**
	 * @return true if the autoscale mode is selected. False otherwise
	 */
	public boolean isYAutoscale() {
		return jcbYAutoscale.isSelected();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null; see showDialog for details
	 * @param minValue track current minimum Y value
	 * @param maxValue track current maximum Y value
	 * @param isAutoscale true if the track autoscale is set to true
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent, double minValue, double maxValue, boolean isAutoscale) {
		this.jftfYMin.setValue(minValue);
		this.jftfYMax.setValue(maxValue);
		this.jcbYAutoscale.setSelected(isAutoscale);
		setModal(true);
		setLocationRelativeTo(parent);
		setVisible(true);
		return approved;
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == jcbYAutoscale) {
			if (jcbYAutoscale.isSelected()) {
				jftfYMin.setEnabled(false);
				jftfYMax.setEnabled(false);
			} else {
				jftfYMin.setEnabled(true);
				jftfYMax.setEnabled(true);
			}
		}
	}
}
