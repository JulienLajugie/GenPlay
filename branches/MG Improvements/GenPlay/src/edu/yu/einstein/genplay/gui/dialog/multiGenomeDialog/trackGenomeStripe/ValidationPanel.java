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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeStripe;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * 
 * @author Nicolas Fourel
 */
class ValidationPanel extends JPanel {

	private static final long serialVersionUID = 8752558211729628953L;

	
	private MultiGenomeStripeSelectionDialog multiGenomeStripeSelectionDialog;
	
	
	protected ValidationPanel (final MultiGenomeStripeSelectionDialog multiGenomeStripeSelectionDialog) {
		this.multiGenomeStripeSelectionDialog = multiGenomeStripeSelectionDialog;
		
		//Dimension
		Dimension panelDim = new Dimension(MultiGenomeStripeSelectionDialog.getDialogWidth(), MultiGenomeStripeSelectionDialog.getValidationHeight());
		setSize(panelDim);
		setPreferredSize(panelDim);
		setMinimumSize(panelDim);
		setMaximumSize(panelDim);
		
		Dimension buttonDim = new Dimension(MultiGenomeStripeSelectionDialog.getValidationButtonSide(), 25);
		
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
				getMultiGenomeStripeSelectionDialog().validChoice();
			}
		});
		// set confirm button as the default button of the dialog
		multiGenomeStripeSelectionDialog.getRootPane().setDefaultButton(confirm);
		
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
				getMultiGenomeStripeSelectionDialog().cancelChoice();
			}
		});
		
		MainFrame.getInstance().getRootPane().setDefaultButton(confirm);
		
		FlowLayout layout = new FlowLayout();
		layout.setHgap(20);
		layout.setVgap(0);
		setLayout(layout);
		
		add(confirm);
		add(cancel);
		
	}


	/**
	 * @return the multiGenomePanel
	 */
	private MultiGenomeStripeSelectionDialog getMultiGenomeStripeSelectionDialog() {
		return multiGenomeStripeSelectionDialog;
	}
	
}
