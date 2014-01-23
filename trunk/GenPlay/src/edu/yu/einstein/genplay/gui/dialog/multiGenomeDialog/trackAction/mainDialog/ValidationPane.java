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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
class ValidationPane extends JPanel implements ActionListener  {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -5372729762581187391L;

	private final MultiGenomeTrackActionDialog dialog;
	private final JButton jbOk;
	private final JButton jbCancel;


	/**
	 * Constructor of {@link ValidationPane}
	 * @param panel a panel {@link ExportVCFPane} or
	 */
	protected ValidationPane (MultiGenomeTrackActionDialog dialog) {
		this.dialog = dialog;

		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Validation");
		setBorder(titledBorder);

		// Creates the ok button
		jbOk = new JButton("Ok");
		jbOk.addActionListener(this);

		// Creates the cancel button
		jbCancel = new JButton("Cancel");
		jbCancel.addActionListener(this);

		// Set the button size
		jbOk.setPreferredSize(jbCancel.getPreferredSize());

		// Creates the panel
		add(jbOk);
		add(jbCancel);

		dialog.getRootPane().setDefaultButton(jbOk);
	}


	/**
	 * Sets the text of the "OK" button
	 * @param text text to set
	 */
	protected void setValidationButtonText (String text) {
		jbOk.setText(text);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton button = (JButton) e.getSource();
			if (button.equals(jbOk)) {
				dialog.approveDialog();
			} else if (button.equals(jbCancel)) {
				dialog.cancelDialog();
			}
		}
	}

}
