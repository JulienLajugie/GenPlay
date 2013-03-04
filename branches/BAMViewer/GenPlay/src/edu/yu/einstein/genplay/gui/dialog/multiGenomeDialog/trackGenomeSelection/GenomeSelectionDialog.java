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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.enums.AlleleType;
import edu.yu.einstein.genplay.gui.dialog.genomeSelectionPanel.GenomeSelectionPanel;
import edu.yu.einstein.genplay.util.Images;


/**
 * Dialog for choosing a genome.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GenomeSelectionDialog extends JDialog {

	/**
	 * Return value when OK has been clicked.
	 */
	public static final int 	APPROVE_OPTION = 0;
	/**
	 * Return value when Cancel has been clicked.
	 */
	public static final int 	CANCEL_OPTION = 1;

	private static final 	long 		serialVersionUID = -2863825210102188370L;	// generated ID
	private static final 	int 		dialogWidth 		= 180;					// dialog width
	private final					GenomeSelectionPanel panel;
	private 				int			approved = CANCEL_OPTION;					// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not


	/**
	 * Creates an instance of a {@link GenomeSelectionDialog}
	 */
	public GenomeSelectionDialog() {
		super();

		// Init
		setTitle("Synchronization parameters");
		setIconImage(Images.getApplicationImage());
		setResizable(false);
		setVisible(false);

		//Layout
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 0;

		panel = new GenomeSelectionPanel();

		// Insert the genome label
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(panel, gbc);

		// Insert the buttons panel
		gbc.gridy = 1;
		add(getButtonPanel(), gbc);

		pack();
	}


	private JPanel getButtonPanel () {
		JPanel panel = new JPanel();

		//Dimension
		Dimension panelDim = new Dimension(dialogWidth, 40);
		panel.setSize(panelDim);
		panel.setPreferredSize(panelDim);
		panel.setMinimumSize(panelDim);
		panel.setMaximumSize(panelDim);

		Dimension buttonDim = new Dimension(50, 25);

		//Confirm button
		JButton confirm = new JButton("Ok");
		confirm.setToolTipText("Ok");
		confirm.setSize(buttonDim);
		confirm.setPreferredSize(buttonDim);
		confirm.setMinimumSize(buttonDim);
		confirm.setMaximumSize(buttonDim);
		confirm.setMargin(new Insets(0, 0, 0, 0));
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				validChoice();
			}
		});

		//Cancel button
		JButton cancel = new JButton("Cancel");
		cancel.setToolTipText("Cancel");
		cancel.setSize(buttonDim);
		cancel.setPreferredSize(buttonDim);
		cancel.setMinimumSize(buttonDim);
		cancel.setMaximumSize(buttonDim);
		cancel.setMargin(new Insets(0, 0, 0, 0));
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelChoice();
			}
		});

		getRootPane().setDefaultButton(confirm);

		FlowLayout layout = new FlowLayout();
		layout.setHgap(20);
		layout.setVgap(10);
		//layout.set
		panel.setLayout(layout);

		panel.add(confirm);
		panel.add(cancel);

		return panel;
	}


	private void cancelChoice() {
		setVisible(false);
	}


	private void validChoice() {
		approved = APPROVE_OPTION;
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


	/**
	 * @return the full name of the selected genome
	 */
	public String getGenomeName () {
		return panel.getGenomeName();
	}


	/**
	 * @return the selected allele type
	 */
	public AlleleType getAlleleType () {
		return panel.getAlleleType();
	}

}
