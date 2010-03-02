/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.event.ActionEvent;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.BinListOperations;
import yu.einstein.gdp2.gui.action.TrackListAction;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.worker.actionWorker.ActionWorker;


/**
 * Gausses the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GaussAction extends TrackListAction {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Gauss";				// action name
	private static final String 	DESCRIPTION = 
		"Apply a gaussian filter on the selected track";		 		// tooltip

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "gauss";


	/**
	 * Creates an instance of {@link GaussAction}
	 * @param trackList a {@link TrackList}
	 */
	public GaussAction(TrackList trackList) {
		super(trackList);
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	/**
	 * Gausses the selected {@link BinListTrack}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final BinListTrack selectedTrack = (BinListTrack) trackList.getSelectedTrack();
		if (selectedTrack != null) {
			final BinList binList = selectedTrack.getBinList();
			final int windowSize = binList.getBinSize();
			if(windowSize > 0) {
				final Integer sigma = GenomeWidthChooser.getSigma(getRootPane(), windowSize);
				if(sigma != null) {
					// thread for the action
					new ActionWorker<BinList>(trackList) {
						@Override
						protected BinList doAction() {
							return BinListOperations.gauss(binList, sigma, binList.getPrecision());
						}
						@Override
						protected void doAtTheEnd(BinList actionResult) {
							String description = "gauss track, sigma = " + sigma + "bp";
							selectedTrack.setBinList(actionResult, description);							
						}
					}.execute();
				}
			}
		}		
	}
}
