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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.export;

import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportValidationPane extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = -7340132842303233253L;

	protected static final String EXPORT_TEXT = "Export";
	protected static final String CANCEL_TEXT = "Cancel";

	private final JButton export;	// The export button
	private final JButton cancel;	// The cancel button


	/**
	 * Constructor of {@link ExportValidationPane}
	 */
	protected ExportValidationPane (ActionListener al) {
		// Create the field set effect
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Validation");
		setBorder(titledBorder);

		// Create the buttons
		export = new JButton(EXPORT_TEXT);
		cancel = new JButton(CANCEL_TEXT);

		// Add the action listener
		if (al != null) {
			export.addActionListener(al);
			cancel.addActionListener(al);
		}

		// Add components
		add(export);
		add(cancel);
	}


	/**
	 * @param button	a button
	 * @return true if the button is one the button of this panel
	 */
	protected boolean contains (JButton button) {
		if (button.equals(export) || button.equals(cancel)) {
			return true;
		}
		return false;
	}

}
