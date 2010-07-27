/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;


import java.awt.Color;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationTwoTrackMethod;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOTwoTracks;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOTwoTracks;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Adds a constant to the scores of the selected {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLATwoTracks extends TrackListActionOperationWorker<DisplayableListOfLists<?, ?>> {

	private static final long 				serialVersionUID = 4027173438789911860L; 				// generated ID
	private static final String 			ACTION_NAME = "Two Tracks Operation";			// action name
	private static final String 			DESCRIPTION = "Run operation on two trakcs";	// tooltip
	private Track<?> 						selectedTrack;									// selected track
	private Track<?> 						otherTrack = null;								// other track
	private Track<?>						resultTrack = null;								// result track
	private ScoreCalculationTwoTrackMethod 	scm;

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLATwoTracks";


	/**
	 * Creates an instance of {@link BLATwoTracks}
	 */
	public BLATwoTracks() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<DisplayableListOfLists<?, ?>> initializeOperation() {
		selectedTrack = getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose a track to add to the selected track:", getTrackList().getCurveTracks());
			if (otherTrack != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					this.scm = Utils.chooseScoreCalculationTwoTrackMethod(getRootPane());
					if (scm != null) {
						if (isSCWList()) {
							operation = new SCWLOTwoTracks(	(DisplayableListOfLists<?, ?>)selectedTrack.getData(),
															(DisplayableListOfLists<?, ?>)otherTrack.getData(),
															this.scm);
						} else {
							DataPrecision precision = Utils.choosePrecision(getRootPane());
							if (precision != null) {
								operation = new BLOTwoTracks(	((BinListTrack)selectedTrack).getData(),
																((BinListTrack)otherTrack).getData(),
																precision,
																scm);
							}
						}
						return operation;
					}
				}
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(DisplayableListOfLists<?, ?> actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			CurveTrack<?> newTrack;
			if (isSCWList()) {
				newTrack = new SCWListTrack(getTrackList().getGenomeWindow(), index + 1, (ScoredChromosomeWindowList)actionResult);
			} else {
				newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, (BinList)actionResult);
			}
			// add info to the history
			newTrack.getHistory().add("Operation on two tracks", Color.GRAY);
			newTrack.getHistory().add("Operation: " + this.scm.toString(), Color.GRAY);
			newTrack.getHistory().add("First track: " + this.selectedTrack.getName(), Color.GRAY);
			newTrack.getHistory().add("Second track: " + this.otherTrack.getName(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " & " + otherTrack.getName(), null);
		}
	}
	
	private boolean isSCWList () {
		if (selectedTrack.getData() instanceof BinList & otherTrack.getData() instanceof BinList) {
			if (((BinList)selectedTrack.getData()).getBinSize() == ((BinList)otherTrack.getData()).getBinSize()) {
				return false;
			}
		}
		return true;
	}
}
