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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;



/**
 * Zooms the {@link SimpleGenomeWindow} displayed in the application out
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAZoomOut extends AbstractAction {

	private static final long serialVersionUID = -2474270228967497967L;	// generated ID


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PAZoomOut.class.getName();


	/**
	 * Creates an instance of {@link PAZoomOut}
	 */
	public PAZoomOut() {
		super();
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Zooms the {@link SimpleGenomeWindow} displayed in the application out
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		MainFrame.getInstance().getControlPanel().zoomOut();
	}
}
