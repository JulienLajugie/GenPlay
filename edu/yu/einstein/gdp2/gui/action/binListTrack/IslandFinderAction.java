/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Indexes the selected {@link BinListTrack} by chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public final class IslandFinderAction extends TrackListAction {

	private static final long serialVersionUID = -6770699912184797937L;
	private static final String 	ACTION_NAME = "Find Islands";// action name
	private static final String 	DESCRIPTION = 
		"Remove all noisy data points";		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "findIsland";


	/**
	 * Creates an instance of {@link IndexationPerChromosomeAction}
	 * @param trackList a {@link TrackList}
	 */
	public IslandFinderAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Indexes the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			//TODO BinList binList = selectedTrack.getBinList();
			final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
			if (resultTrack != null) {
				final int index = resultTrack.getTrackNumber() - 1;
				// thread for the action
				new ActionWorker<BinList>(trackList, "Searching Islands") {
					@Override
					protected BinList doAction() {
						// TODO
						return null;
					}
					@Override
					protected void doAtTheEnd(BinList actionResult) {
						Track newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
						trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), "peaks of " + selectedTrack.getName(), selectedTrack.getStripes());						
					}
				}.execute();
			}
		}
	}
}
