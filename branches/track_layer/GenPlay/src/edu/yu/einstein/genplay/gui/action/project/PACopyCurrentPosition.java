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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;


/**
 * Copy the current position of the main frame to the clipboard.
 * The current position is the red one on the middle top of the frame (the ruler).
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class PACopyCurrentPosition extends AbstractAction {

	private static final long serialVersionUID = -416430771224618219L; // generated ID


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK);


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PACopyCurrentPosition";


	/**
	 * Creates an instance of {@link PACopyCurrentPosition}
	 */
	public PACopyCurrentPosition() {
		super();
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Copy the current position of the main frame to the clipboard.
	 * The current position is the red one on the middle top of the frame (the ruler).
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int position = (int)ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getMiddlePosition();
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection("" + position), null);
	}
}
