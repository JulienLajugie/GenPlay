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

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTransfrag;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTransfragGeneList;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.TransfragDialog;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Defines regions separated by gaps of a specified length and computes the average/sum/max of these regions
 * @author Chirag Gorasia
 */
public class SCWLATransfrag extends TrackListAction {

	private static final long serialVersionUID = 4913086320948928688L;
	private static final String 		ACTION_NAME = "Transfrag";			// action name
	private static final String 		DESCRIPTION =
			"Define regions separated by gaps of a specified length " +
					"and compute the average/max/sum of these regions";		// tooltip
	private AbstractSCWLayer<SCWList>	selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = SCWLATransfrag.class.getName();


	/**
	 * Creates an instance of {@link SCWLATransfrag}
	 */
	public SCWLATransfrag() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@SuppressWarnings("unchecked")
	@Override
	public void trackListActionPerformed(ActionEvent e) {
		selectedLayer = (AbstractSCWLayer<SCWList>) getValue("Layer");
		if (selectedLayer != null) {
			final SCWList scwList = selectedLayer.getData();
			final TransfragDialog tfDialog = new TransfragDialog(TransfragDialog.SCWLIST_TRANSFRAG);
			int res = tfDialog.showTransfragDialog(getRootPane());
			if (res == TransfragDialog.APPROVE_OPTION) {
				int resType = tfDialog.getResultType();
				final ScoreOperation operationType = Utils.chooseScoreCalculation(getRootPane());
				if(operationType != null) {
					try {
						if (resType == TransfragDialog.GENERATE_GENE_LIST) {
							new TrackListActionOperationWorker<GeneList>(){

								private static final long serialVersionUID = 1L;
								@Override
								protected void doAtTheEnd(GeneList actionResult) {
									if (actionResult != null) {
										Track selectedTrack = selectedLayer.getTrack();
										GeneLayer gl = new GeneLayer(selectedTrack, actionResult, "Transfrags from " + selectedTrack.getName());
										selectedTrack.getLayers().add(gl);
										selectedTrack.setActiveLayer(gl);
										selectedTrack.getLayers().remove(selectedTrack);
									}
								}
								@Override
								public Operation<GeneList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new SCWLOTransfragGeneList(scwList, tfDialog.getGapSize(), operationType);
								}
							}.actionPerformed(null);


						} else if (resType == TransfragDialog.GENERATE_SCORED_LIST) {
							new TrackListActionOperationWorker<SCWList>(){
								private static final long serialVersionUID = 1L;
								@Override
								protected void doAtTheEnd(SCWList actionResult) {
									if (actionResult != null) {
										selectedLayer.setData(actionResult, operation.getDescription());
									}
								}
								@Override
								public Operation<SCWList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new SCWLOTransfrag(scwList, tfDialog.getGapSize(), operationType);
								}
							}.actionPerformed(null);
						}
					} catch (Exception err) {
						ExceptionManager.getInstance().caughtException(Thread.currentThread(), err, "Error generating Transfrag");
					}
				}
			}
		}
	}
}
