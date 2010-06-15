/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOCalculationOnProjection;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.util.Utils;


/**
 * Computes the average, sum or max of the selected track on intervals defined by another track
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLACalculationOnProjection extends TrackListActionOperationWorker<BinList> {

	private static final long serialVersionUID = -3736735803307616477L;			// generated ID
	private static final String 	ACTION_NAME = "Calculation on Projection";	// action name
	private static final String 	DESCRIPTION = 
		"Compute the average, the sum or the max of the " +
		"selected track on intervals defined by another track";					// tooltip
	private BinListTrack 			selectedTrack;		// selected track
	private Track 					intervalTrack;		// track defining the intervals
	private Number 					percentage;			// percentage of the greatest values
	private ScoreCalculationMethod 	method;				// method of calculation
	private Track 					resultTrack;		// result track


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLACalculationOnProjection";


	/**
	 * Creates an instance of {@link BLACalculationOnProjection}
	 */
	public BLACalculationOnProjection() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<BinList> initializeOperation() {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			intervalTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Choose the track defining the intervals:", getTrackList().getBinListTracks());
			if(intervalTrack != null) {
				percentage = NumberOptionPane.getValue(getRootPane(), "Enter a percentage", "Perform the calculation on the x% greatest values of each interval:", new DecimalFormat("0"), 0, 100, 100);
				if (percentage != null) {
					method = Utils.chooseScoreCalculation(getRootPane());
					if (method != null) {
						resultTrack = TrackChooser.getTracks(getRootPane(), "Choose A Track", "Generate the result on track:", getTrackList().getEmptyTracks());
						if (resultTrack != null) {
							DataPrecision precision = Utils.choosePrecision(getRootPane());;
							if (precision != null) {
								BinList valueBinList = ((BinListTrack)selectedTrack).getBinList();
								BinList intervalBinList = ((BinListTrack)intervalTrack).getBinList();
								Operation<BinList> operation = new BLOCalculationOnProjection(intervalBinList, valueBinList, percentage.intValue(), method, precision);
								return operation;
							}
						}
					}
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
			newTrack.getHistory().add("Result of the " + method + " of " + selectedTrack.getName() + " calculated on the intervals defined by " + intervalTrack.getName() + " on the " + percentage + "% greatest values", Color.GRAY);
			newTrack.getHistory().add("Window Size = " + actionResult.getBinSize() + "bp, Precision = " + actionResult.getPrecision(), Color.GRAY);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), "average of " + selectedTrack.getName() + " from intervals of  " + intervalTrack.getName(), null);
		}
	}
}
