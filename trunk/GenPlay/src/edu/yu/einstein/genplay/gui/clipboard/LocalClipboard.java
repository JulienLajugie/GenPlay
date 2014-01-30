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
package edu.yu.einstein.genplay.gui.clipboard;

import java.awt.datatransfer.Clipboard;

import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;


/**
 * Singleton of the GenPlay local clipboard.
 * Used if the application doesn't have the permission to access the system clipboard
 * @author Julien Lajugie
 */
public class LocalClipboard extends Clipboard {

	private static LocalClipboard instance = null;

	/**
	 * @return an instance of a {@link LocalClipboard}.
	 * Makes sure that there is only one unique instance as specified in the singleton pattern
	 */
	public static LocalClipboard getInstance() {
		if (instance == null) {
			synchronized(LocalClipboard.class) {
				if (instance == null) {
					instance = new LocalClipboard(MainFrame.APPLICATION_TITLE);
				}
			}
		}
		return instance;
	}

	private LocalClipboard(String name) {
		super(name);
	}
}
