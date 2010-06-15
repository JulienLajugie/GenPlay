/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOGauss;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.GenomeWidthChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;


/**
 * Gausses the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BLAGauss extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -4566157311251154991L; // generated ID
	private static final String 	ACTION_NAME = "Gauss";				// action name
	private static final String 	DESCRIPTION = 
		"Apply a gaussian filter on the selected track";		 		// tooltip
	private BinListTrack 			selectedTrack;						// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAGauss";


	/**
	 * Creates an instance of {@link BLAGauss}
	 */
	public BLAGauss() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public BinListOperation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getBinList();
			int windowSize = binList.getBinSize();
			if(windowSize > 0) {
				Integer sigma = GenomeWidthChooser.getSigma(getRootPane(), windowSize);
				if(sigma != null) {
					BinListOperation<BinList> operation = new BLOGauss(binList, sigma);
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			selectedTrack.setBinList(actionResult, operation.getDescription());
		}		
	}
}
