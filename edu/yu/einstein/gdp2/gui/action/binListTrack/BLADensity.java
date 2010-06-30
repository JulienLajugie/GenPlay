/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLODensity;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Computes the densities of none null bins of the selected {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLADensity extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = 8669677084318132021L;	// generated ID
	private static final String 	ACTION_NAME = "Density";			// action name
	private static final String 	DESCRIPTION = 
		"Computes the densities of none null bins of the selected track";// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	private Track<?> 				resultTrack;						// result track
	private Number 					halfWidth;							// half width
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLADensity";


	/**
	 * Creates an instance of {@link BLADensity}
	 */
	public BLADensity() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			BinList binList = selectedTrack.getData();
			halfWidth = NumberOptionPane.getValue(getRootPane(), "Enter Value", "<html>Enter the half width<br><center>(in number of bins)</center></html>", new DecimalFormat("0"), 1, Integer.MAX_VALUE, 5);
			if(halfWidth != null) {
				resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
				if (resultTrack != null) {
					Operation<BinList> operation = new BLODensity(binList, halfWidth.intValue());
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			BinListTrack newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			// add info to the history
			newTrack.getHistory().add("Result of the density calculation of " + selectedTrack.getName() + ", Half Width = " + halfWidth);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "Density of " + selectedTrack.getName(), null);
		}		
	}
}
