/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Computes the densities of none null bins of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class DensityAction extends TrackListAction {

	private static final long serialVersionUID = 8669677084318132021L;	// generated ID
	private static final String 	ACTION_NAME = "Density";			// action name
	private static final String 	DESCRIPTION = 
		"Computes the densities of none null bins of the selected track";// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "DensityAction";


	/**
	 * Creates an instance of {@link DensityAction}
	 * @param trackList a {@link TrackList}
	 */
	public DensityAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Computes the densities of none null bins of the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final Number halfWidth = NumberOptionPane.getValue(getRootPane(), "Enter Value", "<html>Enter the half width<br><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 5);
			if(halfWidth != null) {
				final Track resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", trackList.getEmptyTracks());
				if (resultTrack != null) {
					// thread for the action
					new ActionWorker<BinList>(trackList) {
						@Override
						protected BinList doAction() {
							return BinListOperations.density(binList, halfWidth.intValue());
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							if (actionResult != null) {
								int index = resultTrack.getTrackNumber() - 1;
								BinListTrack newTrack = new BinListTrack(trackList.getZoomManager(), trackList.getGenomeWindow(), index + 1, trackList.getChromosomeManager(), actionResult);
								// add info to the history
								newTrack.getHistory().add("Result of the density calculation of " + selectedTrack.getName() + ", Half Width = " + halfWidth);
								newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
								trackList.setTrack(index, newTrack, trackList.getConfigurationManager().getTrackHeight(), "Density of " + selectedTrack.getName(), null);
							}
						}
					}.execute();
				}
			}
		}		
	}
}
