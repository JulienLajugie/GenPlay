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
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Starts the updater if available (only for windows plateform with AdvancedInstaller install)
 * @author Julien Lajugie
 */
public class PACheckForUpdates extends TrackListActionWorker<Boolean> {

	/** Generated ID */
	private static final long serialVersionUID = 3327682198559441537L;

	/** Name of the updater */
	private final static String UPDATER_NAME = "GenPlay Updater.exe";

	/** Tooltip */
	private static final String DESCRIPTION = "Check if there is newer versions of genplay";

	/** Mnemonic key */
	private static final int MNEMONIC = KeyEvent.VK_U;

	/** Action name */
	private static final String ACTION_NAME = "Check for Updates";

	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PACheckForUpdates.class.getName();


	/**
	 * @return true if the updater is available, false otherwise
	 */
	public static boolean isUpdaterAvailable() {
		boolean isWindowsOS = Utils.isWindowsOS();
		File updater = new File(UPDATER_NAME);
		return isWindowsOS && updater.exists();
	}


	/**
	 * Creates an instance of {@link PASaveProject}
	 */
	public PACheckForUpdates() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		putValue(ACCELERATOR_KEY, ACCELERATOR);
	}


	@Override
	protected Boolean processAction() throws Exception {
		try {
			if (isUpdaterAvailable()) {
				Runtime.getRuntime().exec("\"" + UPDATER_NAME + "\"");
				return true;
			} else {
				return false;
			}
		} catch(IOException ex) {
			// do nothing if we can't start the updater
			return false;
		}
	}
}
