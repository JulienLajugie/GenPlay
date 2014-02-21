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
package edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.MGProperties.filterTable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TableHeaderPanel extends JPanel {

	/** Generated serial version ID */
	private static final long serialVersionUID = 4840646540885352669L;

	private final JLabel[] labels;


	/**
	 * Constructor of {@link TableHeaderPanel}
	 * @param names	list of names
	 */
	public TableHeaderPanel (String[] names) {
		((FlowLayout)getLayout()).setHgap(0);
		((FlowLayout)getLayout()).setVgap(0);

		labels = new JLabel[names.length];
		for (int i = 0; i < names.length; i++) {
			labels[i] = new JLabel("<html><i>" + names[i] + "</i></html>");
			add(labels[i]);
		}

		resetToDefaultWidth();
	}


	/**
	 * Reset the header width to the default one.
	 * It means each column will have its minimum width given by the length of its name.
	 */
	public void resetToDefaultWidth () {
		// Initializes font metrics
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());

		// Initializes column width using column names
		for (int i = 0; i < labels.length; i++) {
			int width = fm.stringWidth(labels[i].getText().toString()) + 10;
			Dimension labelDimension = new Dimension(width, fm.getHeight());
			labels[i].setPreferredSize(labelDimension);
		}

		repaint();
	}


	/**
	 * Updates the header columns size using the given width array
	 * @param widths an array of width
	 */
	public void updateHeaderWidths (int[] widths) {
		// Initializes font metrics
		FontMetrics fm = MainFrame.getInstance().getFontMetrics(MainFrame.getInstance().getFont());

		// Initializes column width using column names
		for (int i = 0; i < labels.length; i++) {
			Dimension labelDimension = new Dimension(widths[i], fm.getHeight());
			labels[i].setPreferredSize(labelDimension);
		}

		repaint();
	}

}
