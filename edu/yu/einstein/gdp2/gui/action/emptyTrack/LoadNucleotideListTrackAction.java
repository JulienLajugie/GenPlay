/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.nucleotideList.TwoBitSequenceList;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.track.NucleotideListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link NucleotideListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class LoadNucleotideListTrackAction extends TrackListAction {

	private static final long serialVersionUID = 5998366494409991822L;	// generated ID
	private static final String 	ACTION_NAME = "Load Sequence Track";	// action name
	private static final String 	DESCRIPTION = "Load a track showing DNA sequences";	// tooltip

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "LoadNucleotideListTrackAction";


	/**
	 * Creates an instance of {@link LoadNucleotideListTrackAction}
	 * @param trackList a {@link TrackList}
	 */
	public LoadNucleotideListTrackAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Loads a {@link NucleotideListTrack} in the {@link TrackList}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		String defaultDirectory = trackList.getConfigurationManager().getDefaultDirectory();
		final File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Sequence Track", defaultDirectory, Utils.getSequenceFileFilters());
		if (selectedFile != null) {
			new ActionWorker<TwoBitSequenceList>(trackList, "Loading Sequence Track") {
				@Override
				protected TwoBitSequenceList doAction() {
					try {
						return new TwoBitSequenceList(trackList.getChromosomeManager(), selectedFile);
					} catch (Exception e) {
						ExceptionManager.handleException(getRootPane(), e, "Error while loading the sequence track");
						return null;
					}
				}
				@Override
				protected void doAtTheEnd(TwoBitSequenceList actionResult) {
					if (actionResult != null) {
						final int selectedTrackIndex = trackList.getSelectedTrackIndex();
						final ChromosomeWindowList stripes = trackList.getSelectedTrack().getStripes();
						Track newTrack = new NucleotideListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), selectedTrackIndex + 1, actionResult);
						trackList.setTrack(selectedTrackIndex, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedFile.getName(), stripes);
					}
				}
			}.execute();
		}
	}
}
