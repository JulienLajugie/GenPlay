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
package edu.yu.einstein.genplay.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOTransfrag;
import edu.yu.einstein.genplay.core.list.binList.operation.BLOTransfragGeneList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.action.TrackListActionOperationWorker;
import edu.yu.einstein.genplay.gui.dialog.TransfragDialog;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;
import edu.yu.einstein.genplay.util.Utils;



/**
 * Defines regions separated by gaps of a specified length and computes the average of these regions
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLATransfrag extends TrackListAction {

	private static final long serialVersionUID = 8388717083206483317L;	// generated ID	
	private static final String 	ACTION_NAME = "Transfrag";			// action name
	private static final String 	DESCRIPTION = 
		"Define regions separated by gaps of a specified length " +
		"and compute the average/max/sum of these regions";						// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLATransfrag";


	/**
	 * Creates an instance of {@link BLATransfrag}
	 */
	public BLATransfrag() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getData();
			final TransfragDialog tfDialog = new TransfragDialog(TransfragDialog.BINLIST_TRANSFRAG);			
			int res = tfDialog.showTransfragDialog(getRootPane());
			if (res == TransfragDialog.APPROVE_OPTION) {
				int resType = tfDialog.getResultType();
				final ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if(operationType != null) {
					try {						
						if (resType == TransfragDialog.GENERATE_GENE_LIST) {							
							new TrackListActionOperationWorker<GeneList>(){
								private static final long serialVersionUID = -182674743663404937L;
								@Override
								public Operation<GeneList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new BLOTransfragGeneList(binList, tfDialog.getGapSize(), operationType);
								}
								@Override
								protected void doAtTheEnd(GeneList actionResult) {
									if (actionResult != null) {
										int selectedIndex = getTrackList().getSelectedTrackIndex();
										GeneListTrack glt = new GeneListTrack(selectedIndex + 1, actionResult);
										getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getMask(), selectedTrack.getStripesList(), selectedTrack.getFiltersList());
									}
								}
							}.actionPerformed(null);
							
													
						} else if (resType == TransfragDialog.GENERATE_SCORED_LIST) {
							new TrackListActionOperationWorker<BinList>(){
								private static final long serialVersionUID = -182674743663404937L;
								@Override
								public Operation<BinList> initializeOperation()
										throws Exception {
									// case where the result type is a GeneList
									return new BLOTransfrag(binList, tfDialog.getGapSize(), operationType);
								}
								@Override
								protected void doAtTheEnd(BinList actionResult) {
									if (actionResult != null) {
										selectedTrack.setData(actionResult, operation.getDescription());
									}
								}
							}.actionPerformed(null);
						}					
					} catch (Exception e) {
						ExceptionManager.handleException(getRootPane(), e, "Error generating Transfrag");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	
//	public Operation<BinList> initializeOperation() {
//	selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
//	if (selectedTrack != null) {
//		BinList binList = selectedTrack.getData();
//		Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
//		if (gap != null) {
//			ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
//			if(operationType != null) {
//				Operation<BinList> operation = new BLOTransfrag(binList, gap.intValue(), operationType);
//				return operation;						
//			}
//		}
//	}
//	return null;
//}
}
