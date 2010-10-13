/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOTransfrag;
import yu.einstein.gdp2.core.list.binList.operation.BLOTransfragGeneList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TransfragDialog;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.util.Utils;


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


	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
			Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
			if (gap != null) {
				ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if(operationType != null) {
					Operation<BinList> operation = new BLOTransfrag(binList, gap.intValue(), operationType);
					return operation;						
				}
			}
		}
		return null;
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
										GeneListTrack glt = new GeneListTrack(getTrackList().getGenomeWindow(), selectedIndex + 1, actionResult);
										getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getStripes());
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
}
