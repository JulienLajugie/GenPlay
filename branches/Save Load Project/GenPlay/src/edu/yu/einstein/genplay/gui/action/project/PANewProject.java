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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.event.KeyEvent;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.launcher.Launcher;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * Loads a project from a file
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PANewProject extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = "New project"; 		// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_N; 			// mnemonic key
	private static final String 	ACTION_NAME = "New Project";		// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PANewProject";


	/**
	 * Creates an instance of {@link PANewProject}
	 */
	public PANewProject() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}
	

	@Override
	protected Boolean processAction() throws Exception {
		int result = JOptionPane.showConfirmDialog(getRootPane(), "This operation will erase all unsaved data. Do you want to continue?", "New Project", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (result == JOptionPane.YES_OPTION) {
			System.out.println("YES_OPTION");
			return true;
		} else {
			System.out.println("NO_OPTION");
			return null;
		}
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (actionResult) {
			MainFrame.getInstance().dispose();
			Launcher.main(new String[0]);
		}
	}
	
}
