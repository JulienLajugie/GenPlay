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
package edu.yu.einstein.genplay.gui.trackList;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.gui.action.track.TAAddLayer;
import edu.yu.einstein.genplay.gui.action.track.TAAddLayerFromDAS;
import edu.yu.einstein.genplay.gui.action.track.TACopy;
import edu.yu.einstein.genplay.gui.action.track.TACut;
import edu.yu.einstein.genplay.gui.action.track.TADelete;
import edu.yu.einstein.genplay.gui.action.track.TAInsert;
import edu.yu.einstein.genplay.gui.action.track.TAPaste;
import edu.yu.einstein.genplay.gui.action.track.TARename;
import edu.yu.einstein.genplay.gui.action.track.TASaveAsImage;
import edu.yu.einstein.genplay.gui.action.track.TASetHeight;
import edu.yu.einstein.genplay.gui.action.track.TASetVerticalLineCount;


/**
 * This class gathers all the {@link Action} available from the track list panel
 * @author Julien Lajugie
 * @version 0.1
 */
public class TrackListActionMap {

	/**
	 * List of actions available in the {@link TrackListPanel}
	 */
	private static final Action[] TRACK_LIST_ACTIONS = {
		new TAAddLayer(),
		new TAAddLayerFromDAS(),
		new TACopy(),
		new TACut(),
		new TADelete(),
		new TAInsert(),
		new TAPaste(),
		new TARename(),
		new TASaveAsImage(),
		new TASetHeight(),
		new TASetVerticalLineCount()
	};


	/**
	 * @return an {@link ActionMap} containing the actions available for the {@link TrackListPanel}
	 */
	public static ActionMap getActionMap() {
		ActionMap actionMap = new ActionMap();
		for (Action currentAction: TRACK_LIST_ACTIONS) {
			if (currentAction.getValue(Action.ACTION_COMMAND_KEY) != null) {
				actionMap.put(currentAction.getValue(Action.ACTION_COMMAND_KEY), currentAction);
			}
		}
		return actionMap;
	}


	/**
	 * @param component the {@link TrackListPanel}
	 * @return the {@link InputMap} for the actions available for the {@link TrackListPanel}
	 */
	public static ComponentInputMap getInputMap(JComponent component) {
		ComponentInputMap inputMap = new ComponentInputMap(component);
		for (Action currentAction: TRACK_LIST_ACTIONS) {
			if (currentAction.getValue(Action.ACCELERATOR_KEY) != null) {
				inputMap.put((KeyStroke)currentAction.getValue(Action.ACCELERATOR_KEY), currentAction.getValue(Action.ACTION_COMMAND_KEY));
			}
		}
		return inputMap;
	}
}
