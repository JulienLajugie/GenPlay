/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOTransfragGeneList;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.util.Utils;

/**
 * Defines regions separated by gaps of a specified length and computes the average/sum/max of these regions 
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BLATransfragGeneList extends TrackListActionOperationWorker<GeneList> {

	private static final long serialVersionUID = 7605588140747306023L;

	private static final String 	ACTION_NAME = "Transfrag GeneList";			// action name
	private static final String 	DESCRIPTION = 
		"Define regions separated by gaps of a specified length " +
		"and compute the average/sum/max of these regions";						// tooltip	
	private BinListTrack 			selectedTrack;						// selected track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLATransfrag";


	/**
	 * Creates an instance of {@link BLATransfragGeneList}
	 */
	public BLATransfragGeneList() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<GeneList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
			Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "<html>Select a length for the gap between two island<br><center>in number of window</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 1);
			if (gap != null) {
				ScoreCalculationMethod operationType = Utils.chooseScoreCalculation(getRootPane());
				if (operationType != null) {
					Operation<GeneList> operation = new BLOTransfragGeneList(binList, gap.intValue(), operationType);
					return operation;						
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(GeneList actionResult) {
		if (actionResult != null) {
			int selectedIndex = getTrackList().getSelectedTrackIndex();
			GeneListTrack glt = new GeneListTrack(getTrackList().getGenomeWindow(), selectedIndex + 1, actionResult);
			getTrackList().setTrack(selectedIndex, glt, selectedTrack.getPreferredHeight(), selectedTrack.getName(), selectedTrack.getStripes());
		}
	}
}