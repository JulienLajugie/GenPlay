/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindIslands;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Indexes the selected {@link BinListTrack} by chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAFindIslands extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -3178294348003123920L;	// generated ID
	private static final String 	ACTION_NAME = "Find Islands";		// action name
	private static final String 	DESCRIPTION = 
		"Remove all noisy data points";									// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	private Track 					resultTrack;						// result track
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAFindIslands";


	/**
	 * Creates an instance of {@link BLAIndexByChromosome}
	 */
	public BLAFindIslands() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number read_count_limit = NumberOptionPane.getValue(getRootPane(), "Minimum read count limit", "Windows with read count below this value will be ignored.", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if (read_count_limit != null){
				Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "Minimum number of windows for separate two islands", new DecimalFormat("0.0"), 0.0, Double.POSITIVE_INFINITY, 0);
				if (gap != null){
					IslandResultType resultIslandResultType = Utils.chooseIslandResultType(getRootPane());
					if (resultIslandResultType != null) {
						resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
						if (resultTrack != null) {
							BinListOperation<BinList> operation = new BLOFindIslands(selectedTrack.getBinList(), read_count_limit.doubleValue(), gap.intValue(), resultIslandResultType);
							return operation;
						}
					}
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			Track newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "peaks of " + selectedTrack.getName(), selectedTrack.getStripes());						
		}
	}
}
