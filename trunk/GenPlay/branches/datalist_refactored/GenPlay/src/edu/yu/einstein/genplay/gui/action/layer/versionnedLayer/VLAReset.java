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
package edu.yu.einstein.genplay.gui.action.layer.versionnedLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.layer.VersionedLayer;


/**
 * Resets the selected {@link VersionedLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class VLAReset extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 4801183816800208961L;	// generated ID
	private static final String 	ACTION_NAME = "Reset";				// action name
	private static final String 	DESCRIPTION =
			"Reset the selected layer";									// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = VLAReset.class.getName();


	/**
	 * Creates an instance of {@link VLAReset}
	 */
	public VLAReset() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getValue("Layer") instanceof VersionedLayer) {
			VersionedLayer<?> selectedLayer = (VersionedLayer<?>) getValue("Layer");
			if (selectedLayer != null) {
				notifyActionStart("Reseting Layer", 1, false);
				selectedLayer.reset();
			}
		}
		return null;
	}
}
