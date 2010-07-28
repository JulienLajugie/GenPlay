package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOCountNonNullLength;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.ChromosomeChooser;
import yu.einstein.gdp2.gui.track.SCWListTrack;

public class SCWLACountNonNullLength extends TrackListActionOperationWorker<Long> {

	private static final long serialVersionUID = -1773399821513504625L;		// generated ID
	private static final String 	ACTION_NAME = "Count Non-Null Length";	// action name
	private static final String 	DESCRIPTION = "Sum of the length " +
			"of the non-null windows";										// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLACountNonNullLength";


	/**
	 * Creates an instance of {@link SCWLACountNonNullLength}
	 */
	public SCWLACountNonNullLength() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<Long> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			boolean[] selectedChromo = ChromosomeChooser.getSelectedChromo(getRootPane());
			if (selectedChromo != null) {
				ScoredChromosomeWindowList scwList = selectedTrack.getData();
				Operation<Long> operation = new SCWLOCountNonNullLength(scwList, selectedChromo);
				return operation;
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(Long actionResult) {
		if (actionResult != null) {
			JOptionPane.showMessageDialog(getRootPane(), "Non-Null Length: \n" + new DecimalFormat("###,###,###,###").format(actionResult) + " bp", "Non-Null Length", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
