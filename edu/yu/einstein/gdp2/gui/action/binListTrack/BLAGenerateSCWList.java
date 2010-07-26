/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOGenerateSCWList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Creates a {@link SCWListTrack} from a {@link BinListTrack}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class BLAGenerateSCWList  extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Generate " +
			"Variable Window Track";									// action name
	private static final String 	DESCRIPTION = "Generate a " +
			"variable window track from the selected track"; 			// tooltip
	private BinListTrack 			selectedTrack;					// selected track
	private Track<?>				resultTrack;					// result track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAGenerateSCWList";


	/**
	 * Creates an instance of {@link BLAGenerateSCWList}
	 */
	public BLAGenerateSCWList() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}
	

	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
			if (resultTrack != null) {
				Operation<ScoredChromosomeWindowList> operation = new BLOGenerateSCWList((BinList)selectedTrack.getData());
				return operation;
			}
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			SCWListTrack newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName(), selectedTrack.getStripes());
		}
	}	
}
