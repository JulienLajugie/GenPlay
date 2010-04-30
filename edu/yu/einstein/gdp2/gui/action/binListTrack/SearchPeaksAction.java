/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOSearchPeaks;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Searches the peaks of a track.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SearchPeaksAction extends TrackListAction {

	private static final long serialVersionUID = 1524662321569310278L;  // generated ID
	private static final String 	ACTION_NAME = "Search Peaks";		// action name
	private static final String 	DESCRIPTION = 
		"Search the peaks of the selected track";						// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "searchpeaks";


	/**
	 * Creates an instance of {@link SearchPeaksAction}
	 * @param trackList a {@link TrackList}
	 */
	public SearchPeaksAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Searches the peaks of a track
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final Number sizeMovingSD = GenomeWidthChooser.getMovingStdDevWidth(getRootPane(), binList.getBinSize());
			if(sizeMovingSD != null) {
				final Number nbSDAccepted = NumberOptionPane.getValue(getRootPane(), "Threshold", "Select only peak with a local SD x time higher than the global one", new DecimalFormat("0.0"), 0, 1000, 1).intValue(); 
				if(nbSDAccepted != null) {
					final BinListOperation<BinList> operation = new BLOSearchPeaks(binList, sizeMovingSD.intValue(), nbSDAccepted.intValue());
					// thread for the action
					new ActionWorker<BinList>(trackList, "Searching Peaks") {
						@Override
						protected BinList doAction() throws Exception {
							return operation.compute();
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							selectedTrack.setBinList(actionResult, operation.getDescription());						
						}
					}.execute();
				}
			}
		}		
	}
}