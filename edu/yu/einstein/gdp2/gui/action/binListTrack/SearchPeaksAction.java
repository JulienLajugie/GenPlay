/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Creates a new track containing only the peaks of the selected one.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SearchPeaksAction extends TrackListAction {

	private static final long serialVersionUID = 1524662321569310278L;  // generated ID
	private static final String 	ACTION_NAME = "Search Peaks";		// action name
	private static final String 	DESCRIPTION = 
		"Creates a new track containing only the " +
		"peaks of the selected one";									// tooltip


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
	 * Creates a new track containing only the peaks of the selected one.
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
					final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
					if (resultTrack != null) {
						final DataPrecision precision = Utils.choosePrecision(getRootPane());
						// thread for the action
						new ActionWorker<BinList>(trackList, "Searching Peaks") {
							@Override
							protected BinList doAction() {
								return BinListOperations.searchPeaks(binList, sizeMovingSD.intValue(), nbSDAccepted.doubleValue(), precision);
							}
							@Override
							protected void doAtTheEnd(BinList actionResult) {
								int index = resultTrack.getTrackNumber() - 1;
								BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
								// add info to the history
								newTrack.getHistory().add("Result of the peak search on " + selectedTrack.getName() + ", Moving StdDev Window = " + sizeMovingSD +"bp, Threshold = " + nbSDAccepted + "Genomewide StdDev");
								newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
								trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), "peaks of " + selectedTrack.getName(), selectedTrack.getStripes());								
							}
						}.execute();
					}
				}
			}
		}		
	}
}