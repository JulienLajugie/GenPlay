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
package edu.yu.einstein.genplay.gui.statusBar;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * This class implements a button for calling the garbage collector from the main frame.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GarbageCollectorButton extends JButton implements ActionListener {

	/** Default generated serial version ID */
	private static final long serialVersionUID = 1971269845667687839L;
	private static final int 		SIDE = 15; 		// side base of the button


	/**
	 * Constructor of {@link GarbageCollectorButton}
	 */
	protected GarbageCollectorButton () {
		setMargin(new Insets(0, 0, 0, 0));
		setText("GC");

		setToolTipText("Clean up the memory (Garbage Collector).");

		Dimension dimension = new Dimension(SIDE * 2, SIDE);
		setPreferredSize(dimension);
		setMinimumSize(dimension);

		addActionListener(this);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.gc();System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();System.gc();
		System.gc();System.gc();System.gc();System.gc();System.gc();
	}
}
