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

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ExportPane extends JPanel {

	/** Generated default serial version ID */
	private static final long serialVersionUID = -3500561776326311918L;


	/**
	 * Constructor of {@link ExportPane}
	 */
	protected ExportPane (ExportLegendPane legendPanel, JPanel panel, ExportValidationPane validationPane) {
		// Layout
		BorderLayout layout = new BorderLayout(0, 10);
		setLayout(layout);

		// Add components
		add(legendPanel, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
		if (validationPane != null) {
			add(validationPane, BorderLayout.SOUTH);
		}
	}


}
