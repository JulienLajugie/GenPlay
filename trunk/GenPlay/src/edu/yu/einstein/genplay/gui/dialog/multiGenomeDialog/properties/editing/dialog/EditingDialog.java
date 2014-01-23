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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.managers.EditingDialogManagerInterface;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.dialog.panels.EditingPanel;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.Utils;

/**
 * @author Nicolas Fourel
 * @version 0.1
 * @param <K>
 */
public class EditingDialog<K> extends JDialog {

	/** Generated serial version ID */
	private static final long serialVersionUID = -977707723378348966L;

	/** Return value when OK has been clicked. */
	public static final 	int 			APPROVE_OPTION 		= 0;
	/** Return value when Cancel has been clicked. */
	public static final 	int 			CANCEL_OPTION 		= 1;

	private int				approved 			= CANCEL_OPTION;	// equals APPROVE_OPTION if user clicked OK, CANCEL_OPTION if not

	List<EditingPanel<?>> editingPanelList;


	/**
	 * Constructor of {@link EditingDialog}
	 * @param editingManager manager for editing the dialog
	 */
	public EditingDialog (EditingDialogManagerInterface<K> editingManager) {
		editingPanelList = editingManager.getEditingPanelList();

		// Dialog layout
		BorderLayout layout = new BorderLayout();
		setLayout(layout);

		// Add the panels
		add(getPanel(editingPanelList), BorderLayout.CENTER);
		add(getValidationPanel(), BorderLayout.SOUTH);

		// Dialog settings
		setTitle("Multi-Genome Project Properties - Editing Dialog");
		setIconImage(Images.getApplicationImage());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setVisible(false);
		pack();
	}


	/**
	 * Shows the component.
	 * @param parent the parent component of the dialog, can be null;
	 * @return APPROVE_OPTION is OK is clicked. CANCEL_OPTION otherwise.
	 */
	public int showDialog(Component parent) {
		// Sets dialog display options
		setLocationRelativeTo(parent);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setVisible(true);

		return approved;
	}


	/**
	 * Create the panel containing all the editing panels
	 * @param 	panelList the list of editing panels
	 * @return	the panel containing all the editing panels
	 */
	private JPanel getPanel (List<EditingPanel<?>> panelList) {
		JPanel panel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		panel.setLayout(layout);

		for (EditingPanel<?> editingPanel: panelList) {
			panel.add(editingPanel);
		}

		return panel;
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
				if (approveSelection()) {
					approved = APPROVE_OPTION;
					setVisible(false);
				}
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

		// Creates the panel
		JPanel panel = new JPanel();
		panel.add(jbOk);
		panel.add(jbCancel);

		getRootPane().setDefaultButton(jbOk);

		// Returns the panel
		return panel;
	}


	/**
	 * @return true is the current selection is valid
	 */
	private boolean approveSelection () {
		String errors = "";
		for (EditingPanel<?> panel: editingPanelList) {
			errors += panel.getErrors();
		}

		if (errors.isEmpty()) {
			return true;
		} else {
			showErrorDialog(errors);
			return false;
		}
	}


	/**
	 * Shows an error message
	 * @param errors error message
	 */
	private void showErrorDialog (String errors) {
		String message = "Some errors have been found, please check out the following points:\n";
		//String[] errorsArray = errors.split("\n");
		String[] errorsArray = Utils.split(errors, '\n');
		for (int i = 0; i < errorsArray.length; i++) {
			if (!errorsArray[i].isEmpty()) {
				message += i + 1 + ": " + errorsArray[i];
				if (i < (errorsArray.length - 1)) {
					message += "\n";
				}
			}
		}
		JOptionPane.showMessageDialog(this, message, "Settings are not valid", JOptionPane.ERROR_MESSAGE);
	}

}
