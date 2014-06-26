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
package edu.yu.einstein.genplay.gui.action.layer.SCWLayer;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.dialog.SCWListStatsDialog.SCWListStatsDialog;
import edu.yu.einstein.genplay.gui.track.layer.Layer;


/**
 * Shows stats about the data of the selected layer
 * @author Julien Lajugie
 */
public final class SCWLAShowStatistics extends TrackListAction {

	private static final long serialVersionUID = -3864460354387970028L;	// generated ID
	private static final String 	ACTION_NAME = "Show Statistics";	// action name
	private static final String 	DESCRIPTION =
			"Show statistics about the selected layer" + HELP_TOOLTIP_SUFFIX; // tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Show_Statistics";


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLAShowStatistics.class.getName();


	/**
	 * Creates an instance of {@link SCWLAShowStatistics}
	 */
	public SCWLAShowStatistics() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	public void trackListActionPerformed(ActionEvent e) {
		Layer<?> selectedLayer = (Layer<?>) getValue("Layer");
		if ((selectedLayer != null) && (selectedLayer.getData() instanceof SCWList)) {
			SCWList scwList = (SCWList) selectedLayer.getData();
			SCWListStatsDialog.showDialog(getRootPane(), scwList.getStatistics());
		}
	}
}
