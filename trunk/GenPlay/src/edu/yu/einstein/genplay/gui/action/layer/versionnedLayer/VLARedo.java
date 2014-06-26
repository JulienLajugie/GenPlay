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
package edu.yu.einstein.genplay.gui.action.layer.versionnedLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.layer.VersionedLayer;


/**
 * Redoes the last action performed on the selected {@link VersionedLayer}
 * @author Julien Lajugie
 */
public final class VLARedo extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6836640129258678255L; 	// generated ID
	private static final String 	ACTION_NAME = "Redo";				// action name
	private static final String 	DESCRIPTION =
			"Redo the last action performed on the selected layer" + HELP_TOOLTIP_SUFFIX;		// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Using_the_Undo_.2F_Redo_.2F_Reset_Options";


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = VLARedo.class.getName();


	/**
	 * Creates an instance of {@link VLARedo}
	 */
	public VLARedo() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getValue("Layer") instanceof VersionedLayer) {
			VersionedLayer<?> selectedLayer = (VersionedLayer<?>) getValue("Layer");
			if (selectedLayer != null) {
				notifyActionStart("Redoing", 1, false);
				selectedLayer.redo();
			}
		}
		return null;
	}
}
