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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import edu.yu.einstein.genplay.dataStructure.enums.RNAToDNAResultType;
import edu.yu.einstein.genplay.util.Images;



/**
 * A frame to select the Output File Type for the {@link RNAPosToDNAPosOutputFileTypeDialog}
 * @author Chirag Gorasia
 * @author Julien Lajugie
 * @version 0.1
 */
public class RNAPosToDNAPosOutputFileTypeDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 8313046917432891962L;	// generated ID
	private static final int INSET = 15;	// gab between the components and the window
	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private final JComboBox jcb;			// combo box
	private final JLabel jlFileTypeOptions;	// Output File Type Option Label
	private final JButton jbOK;					// ok button
	private final JButton jbCancel;				// cancel button
	private int	approved = CANCEL_OPTION;	// true if okay has been clicked


	/**
	 * Public constructor. Creates an instance of {@link RNAPosToDNAPosOutputFileTypeDialog}
	 */
	public RNAPosToDNAPosOutputFileTypeDialog() {
		super();
		jlFileTypeOptions = new JLabel("RNA To DNA Output File Type: ");
		jcb = new JComboBox(RNAToDNAResultType.values());

		jbOK = new JButton("OK");
		jbOK.setPreferredSize(new Dimension(75, 30));
		jbOK.setDefaultCapable(true);
		jbOK.addActionListener(this);

		jbCancel = new JButton("Cancel");
		jbCancel.setPreferredSize(new Dimension(75, 30));
		jbCancel.addActionListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(INSET, INSET, INSET, 0);
		add(jlFileTypeOptions, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(INSET, 0, INSET, INSET);
		add(jcb, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.insets = new Insets(INSET, INSET, INSET, 0);
		add(jbOK, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(INSET, 0, INSET, INSET);
		add(jbCancel, gbc);

		pack();
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setTitle("RNA To DNA Output File Selector");
		setIconImages(Images.getApplicationImages());
		setVisible(false);
		jbOK.setDefaultCapable(true);
		getRootPane().setDefaultButton(jbOK);
	}


	/**
	 * @return an {@link RNAToDNAResultType} corresponding to the selected output
	 */
	public RNAToDNAResultType getSelectedOutputFileType() {
		return (RNAToDNAResultType) jcb.getSelectedItem();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbOK) {
			approved = APPROVE_OPTION;
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
}
