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
package edu.yu.einstein.genplay.gui.action.layer.geneLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.geneList.GLOExtractIntervals;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.ExtractGeneIntervalsDialog;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;


/**
 * Extract intervals defined relative to genes
 * @author Julien Lajugie
 */
public final class GLAExtractInterval  extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Extract Intervals"; // action name
	private static final String 	DESCRIPTION = "Extract intervals " +
			"defined relative to genes" + HELP_TOOLTIP_SUFFIX;			// tooltip
	private static final String		HELP_URL = "http://genplay.einstein.yu.edu/wiki/index.php/Documentation#Extract_Intervals";
	private GeneLayer 			selectedLayer;							// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = GLAExtractInterval.class.getName();


	/**
	 * Creates an instance of {@link GLAExtractInterval}
	 */
	public GLAExtractInterval() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(HELP_URL_KEY, HELP_URL);
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			selectedLayer.setData(actionResult, operation.getDescription());
		}
	}


	@Override
	public Operation<GeneList> initializeOperation() {
		selectedLayer = (GeneLayer) getValue("Layer");
		if (selectedLayer != null) {
			GeneList geneList = selectedLayer.getData();
			ExtractGeneIntervalsDialog dialog = new ExtractGeneIntervalsDialog();
			if (dialog.showDialog(getRootPane()) == ExtractGeneIntervalsDialog.APPROVE_OPTION) {
				operation = new GLOExtractIntervals(geneList, dialog.getStartDistance(), dialog.getStartFrom(), dialog.getStopDistance(), dialog.getStopFrom());
				return operation;
			}
		}
		return null;
	}
}
