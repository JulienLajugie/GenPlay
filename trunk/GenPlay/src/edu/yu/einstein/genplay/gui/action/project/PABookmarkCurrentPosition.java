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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.gwBookmark.GWBookmark;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * Shows the about dialog window
 * @author Julien Lajugie
 */
public final class PABookmarkCurrentPosition extends AbstractAction {

	private static final long serialVersionUID = 4583132419889562511L;	// generated ID
	private static final String 	DESCRIPTION =
			"Bookmark the current position on the genome"; 				// tooltip
	private static final int 		MNEMONIC = KeyEvent.VK_B; 			// mnemonic key
	private static final String 	ACTION_NAME = "Add to Bookmark";	// action name

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PABookmarkCurrentPosition.class.getName();

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	private static int regionNumber = 1;	// the number of the region to bookmark


	/**
	 * Creates an instance of {@link PABookmarkCurrentPosition}
	 */
	public PABookmarkCurrentPosition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	/**
	 * Add the bookmark
	 */
	@Override
	public void actionPerformed(ActionEvent evt) {
		GenomeWindow genomeWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();

		String defaultDescription = "Region " + regionNumber;
		String bookmarkDescription = (String) JOptionPane.showInputDialog(MainFrame.getInstance().getRootPane(),
				"Please enter a name for the bookmark",
				"Enter Bookmark Name",
				JOptionPane.QUESTION_MESSAGE,
				null,
				null,
				defaultDescription);
		if (bookmarkDescription != null) {
			GWBookmark bookmark;
			if (ProjectManager.getInstance().isMultiGenomeProject()) {
				bookmark = new GWBookmark(bookmarkDescription, genomeWindow, MGDisplaySettings.SELECTED_GENOME);
			} else {
				bookmark = new GWBookmark(bookmarkDescription, genomeWindow);
			}
			regionNumber++;
			ProjectManager.getInstance().getProjectBookmarks().add(bookmark);
		}
	}
}
