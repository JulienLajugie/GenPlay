/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


/**
 * Creates a {@link BinListTrack} from a {@link SCWListTrack} 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GenerateBinListAction  extends TrackListAction {

	private static final long serialVersionUID = 2102571378866219218L; // generated ID
	private static final String 	ACTION_NAME = "Generate Fixed Window Track";		// action name
	private static final String 	DESCRIPTION = "Generate a fixed window track from the selected track"; // tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "generateBinList";



	public GenerateBinListAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Creates a {@link BinListTrack} from a {@link SCWListTrack} 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final SCWListTrack selectedTrack = (SCWListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final Number binSize = NumberOptionPane.getValue(getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				final ScoreCalculationMethod scoreCalculation = Utils.chooseScoreCalculation(getRootPane());
				if (scoreCalculation != null) {
					final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
					if (resultTrack != null) {
						final DataPrecision precision = Utils.choosePrecision(getRootPane());
						if (precision != null) {
							final int index = resultTrack.getTrackNumber() - 1;
							// thread for the action
							new ActionWorker<BinList>(trackList) {
								@Override
								protected BinList doAction() {
									return selectedTrack.getData().generateBinList(trackList.getChromosomeManager(), binSize.intValue(), precision, scoreCalculation);
								}
								@Override
								protected void doAtTheEnd(BinList actionResult) {
									Track newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
									trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), selectedTrack.getName(), selectedTrack.getStripes());
								}
							}.execute();							
						}
					}
				}						
			}	
		}
	}
}