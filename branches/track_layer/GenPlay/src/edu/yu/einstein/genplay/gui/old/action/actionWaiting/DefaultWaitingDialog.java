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
package edu.yu.einstein.genplay.gui.old.action.actionWaiting;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JLabel;

import edu.yu.einstein.genplay.util.Images;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class DefaultWaitingDialog extends JDialog {


	/** Generated default serial version ID */
	private static final long serialVersionUID = -1771683185592808089L;

	private List<String> lines;


	/**
	 * Constructor of {@link DefaultWaitingDialog}
	 */
	public DefaultWaitingDialog () {
		lines = new ArrayList<String>();
		lines.add("This action may take few minutes...");
		lines.add("Please wait until the dialog closes automatically.");

		initialize();
	}


	/**
	 * @param lines the lines to set
	 */
	public void setLines(List<String> lines) {
		this.lines = lines;
		initialize();
	}


	/**
	 * Initialize the dialog
	 */
	private void initialize () {
		// Get the number of line
		int lineNumber = lines.size();

		// Get the longest line width
		FontMetrics fm = getFontMetrics(getFont());
		int maxWidth = 0;
		for (String line: lines) {
			int width = fm.stringWidth(line);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}

		// Get/Set dimension
		int width = (int) (maxWidth * 1.2);
		int height = (fm.getHeight() * lineNumber) + 30;
		Dimension size = new Dimension(width, height);
		setSize(size);
		setPreferredSize(size);

		// Set the layout and add the labels
		GridLayout layout = new GridLayout(lineNumber, 1);
		setLayout(layout);
		for (String line: lines) {
			add(new JLabel(line));
		}

		// Dialog settings
		setTitle("Please wait");
		setIconImage(Images.getApplicationImage());
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(false);
		pack();
	}

}
