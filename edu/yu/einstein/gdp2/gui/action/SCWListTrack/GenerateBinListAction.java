/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutionException;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;
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


	/**
	 * Creates an instance of {@link GenerateBinListAction}
	 */
	public GenerateBinListAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Creates a {@link BinListTrack} from a {@link SCWListTrack} 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		final SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final Number binSize = NumberOptionPane.getValue(getRootPane(), "Fixed Window Size", "Enter window size", new DecimalFormat("#"), 0, Integer.MAX_VALUE, 1000);
			if (binSize != null) {
				final ScoreCalculationMethod scoreCalculation = Utils.chooseScoreCalculation(getRootPane());
				if (scoreCalculation != null) {
					final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
					if (resultTrack != null) {
						final DataPrecision precision = Utils.choosePrecision(getRootPane());
						if (precision != null) {
							final int index = resultTrack.getTrackNumber() - 1;
							// thread for the action
							new ActionWorker<BinList>(getTrackList(), "Generating Fixed Window Track") {
								@Override
								protected BinList doAction() throws IllegalArgumentException, InterruptedException, ExecutionException {
									return selectedTrack.getData().generateBinList(binSize.intValue(), precision, scoreCalculation);
								}
								@Override
								protected void doAtTheEnd(BinList actionResult) {
									Track newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
									getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName(), selectedTrack.getStripes());
								}
							}.execute();							
						}
					}
				}						
			}	
		}
	}
}