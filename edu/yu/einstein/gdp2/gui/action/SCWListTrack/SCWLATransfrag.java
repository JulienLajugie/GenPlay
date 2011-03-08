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
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOTransfrag;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOTransfragGeneList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TransfragDialog;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.util.Utils;

/**
 * Defines regions separated by gaps of a specified length and computes the average/sum/max of these regions
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWLATransfrag extends TrackListAction {

	private static final long serialVersionUID = 4913086320948928688L;
	private static final String 	ACTION_NAME = "Transfrag";			// action name
	private static final String 	DESCRIPTION = 
		"Define regions separated by gaps of a specified length " +
		"and compute the average/max/sum of these regions";				// tooltip
	private SCWListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLATransfrag";


	/**
	 * Creates an instance of {@link SCWLATransfrag}
	 */
	public SCWLATransfrag() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			ScoredChromosomeWindowList scwList = ((SCWListTrack)selectedTrack).getData();
			Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
			if (gap != null) {
				ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if (operationType != null) {						
					Operation<ScoredChromosomeWindowList> operation = new SCWLOTransfrag(scwList, gap.intValue(), operationType);
					return operation;
				}
			}
		}
		return null;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final ScoredChromosomeWindowList scwList = selectedTrack.getData();
			final TransfragDialog tfDialog = new TransfragDialog(TransfragDialog.SCWLIST_TRANSFRAG);			
			int res = tfDialog.showTransfragDialog(getRootPane());
			if (res == TransfragDialog.APPROVE_OPTION) {
				int resType = tfDialog.getResultType();
				final ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if(operationType != null) {
					try {						
						if (resType == TransfragDialog.GENERATE_GENE_LIST) {							
							new TrackListActionOperationWorker<GeneList>(){
								
								private static final long serialVersionUID = 1L;
								@Override
								public Operation<GeneList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new SCWLOTransfragGeneList(scwList, tfDialog.getGapSize(), operationType);
								}
								@Override
								protected void doAtTheEnd(GeneList actionResult) {
									if (actionResult != null) {
										int selectedIndex = getTrackList().getSelectedTrackIndex();
										GeneListTrack glt = new GeneListTrack(getTrackList().getGenomeWindow(), selectedIndex + 1, actionResult);
										getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getStripes());
									}
								}
							}.actionPerformed(null);
							
													
						} else if (resType == TransfragDialog.GENERATE_SCORED_LIST) {
							new TrackListActionOperationWorker<ScoredChromosomeWindowList>(){
								
								private static final long serialVersionUID = 1L;
								@Override
								public Operation<ScoredChromosomeWindowList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new SCWLOTransfrag(scwList, tfDialog.getGapSize(), operationType);
								}
								@Override
								protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
									if (actionResult != null) {
										selectedTrack.setData(actionResult, operation.getDescription());
									}
								}
							}.actionPerformed(null);
						}					
					} catch (Exception err) {
						ExceptionManager.handleException(getRootPane(), err, "Error generating Transfrag");
						err.printStackTrace();
					}
				}
			}
		}
	}
}
