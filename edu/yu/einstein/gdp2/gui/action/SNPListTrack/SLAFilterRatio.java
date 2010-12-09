/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SNPListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.SNPList.operation.SLOFilterRatio;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.TwoNumbersOptionPane;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Removes the SNPs with a ratio (first base count) on (second base count)
 * greater or smaller than a specified value
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLAFilterRatio extends TrackListActionOperationWorker<SNPList> {

	private static final long serialVersionUID = 2056103615608319188L;	// generated ID
	private static final String 	ACTION_NAME = "Ratio Threshold";	// action name
	private static final String 	DESCRIPTION = 
		"Removes the SNPs with a ratio (first base count) on (second base count)" +
		"greater or smaller than a specified value";					// tooltip
	private Track<?> 				selectedTrack;						// selected track
	
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SLAFilterRatio";


	/**
	 * Creates an instance of {@link SLAFilterRatio}
	 */
	public SLAFilterRatio() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	
	@Override
	public Operation<SNPList> initializeOperation() throws Exception {
		selectedTrack = getTrackList().getSelectedTrack();
		if ((selectedTrack != null) && (selectedTrack.getData() instanceof SNPList)) {
			SNPList inputList = (SNPList) selectedTrack.getData();
			Number[] thresholds = TwoNumbersOptionPane.getValue(getRootPane(), "Ratio", "Remove SNPs with a ratio (1st base count) / (2nd base count) smaller than", 
					"Or greater than", new DecimalFormat("###,###.###"), 0, Double.POSITIVE_INFINITY, 0, Double.POSITIVE_INFINITY);
			if (thresholds != null) {
				Operation<SNPList> operation = new SLOFilterRatio(inputList, thresholds[0].doubleValue(), thresholds[1].doubleValue());
				return operation;
			}
		}
		return null;
	}

	
	@Override
	protected void doAtTheEnd(SNPList actionResult) {
		if (actionResult != null) {
			int index = selectedTrack.getTrackNumber() - 1;
			Track<?> newTrack = new SNPListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult);
			getTrackList().setTrack(index, newTrack, ConfigurationManager.getInstance().getTrackHeight(), selectedTrack.getName() + " filtered", selectedTrack.getStripes());
		}		
	}

}
