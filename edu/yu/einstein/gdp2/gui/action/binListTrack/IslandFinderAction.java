/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.FilterType;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOIslandFinder;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;
import yu.einstein.gdp2.util.Utils;


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
	public static final String ACTION_KEY = "IslandFinderAction";


	/**
	 * Creates an instance of {@link IndexationPerChromosomeAction}
	 */
	public IslandFinderAction() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Indexes the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			final Number read_count_limit = NumberOptionPane.getValue(getRootPane(), "Minimum read count limit", "Windows with read count below this value will be ignored.", new DecimalFormat("0.0"), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0);
			if (read_count_limit != null){
				final Number gap = NumberOptionPane.getValue(getRootPane(), "Gap", "Minimum number of windows for separate two islands", new DecimalFormat("0.0"), 0.0, Double.POSITIVE_INFINITY, 0);
				if (gap != null){
					final IslandResultType resultIslandResultType = Utils.chooseIslandResultType(getRootPane());
					if (resultIslandResultType != null) {
						//TODO BinList binList = selectedTrack.getBinList();
						final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
						if (resultTrack != null) {
							final int index = resultTrack.getTrackNumber() - 1;
							final BinListOperation<BinList> operation = new BLOIslandFinder(selectedTrack.getBinList(), read_count_limit.doubleValue(), gap.intValue(), resultIslandResultType);
							// thread for the action
							new ActionWorker<BinList>(getTrackList(), "Searching Islands") {
								@Override
								protected BinList doAction() throws Exception {
									return operation.compute();
								}
								@Override
								protected void doAtTheEnd(BinList actionResult) {
									Track newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
									getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "peaks of " + selectedTrack.getName(), selectedTrack.getStripes());						
								}
							}.execute();
						}
					}
				}
			}
		}
	}
}
