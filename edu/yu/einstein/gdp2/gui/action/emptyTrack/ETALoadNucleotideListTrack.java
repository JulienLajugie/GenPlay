/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.emptyTrack;

import java.io.File;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.list.nucleotideList.TwoBitSequenceList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListActionWorker;
import yu.einstein.gdp2.gui.track.NucleotideListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.util.Utils;


/**
 * Loads a {@link NucleotideListTrack} in the {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ETALoadNucleotideListTrack extends TrackListActionWorker<TwoBitSequenceList> {

	private static final long serialVersionUID = 5998366494409991822L;	// generated ID
	private static final String 	ACTION_NAME = "Load Sequence Track";	// action name
	private static final String 	DESCRIPTION = "Load a track showing DNA sequences";	// tooltip
	private File 					selectedFile;										// selected file
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ETALoadNucleotideListTrack";


	/**
	 * Creates an instance of {@link ETALoadNucleotideListTrack}
	 */
	public ETALoadNucleotideListTrack() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected TwoBitSequenceList processAction() throws Exception {
		String defaultDirectory = ConfigurationManager.getInstance().getDefaultDirectory();
		selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load Sequence Track", defaultDirectory, Utils.getReadableSequenceFileFilters());
		if (selectedFile != null) {
			notifyActionStart("Loading Sequence File", 1, true);
			return new TwoBitSequenceList(selectedFile);
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(TwoBitSequenceList actionResult) {
		if (actionResult != null) {
			int selectedTrackIndex = getTrackList().getSelectedTrackIndex();
			ChromosomeWindowList stripes = getTrackList().getSelectedTrack().getStripes();
			Track newTrack = new NucleotideListTrack(getTrackList().getGenomeWindow(), selectedTrackIndex + 1, actionResult);
			getTrackList().setTrack(selectedTrackIndex, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedFile.getName(), stripes);
		}
	}
}
