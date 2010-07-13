/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLOIndexByChromosome;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.track.SCWListTrack;


/**
 * Indexes the selected {@link SCWListTrack} by chromosome
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SCWLAIndexByChromosome extends TrackListActionOperationWorker<ScoredChromosomeWindowList> {

	private static final long serialVersionUID = -2043891820249510406L; 		// generated ID
	private static final String 	ACTION_NAME = "Indexation per Chromosome";	// action name
	private static final String 	DESCRIPTION = 
		"Index separately each chromosome of the selected track";				// tooltip
	private SCWListTrack 			selectedTrack;								// selected track

	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLAIndexByChromosome";


	/**
	 * Creates an instance of {@link SCWLAIndexByChromosome}
	 */
	public SCWLAIndexByChromosome() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public Operation<ScoredChromosomeWindowList> initializeOperation() {
		selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {			
			Number indexMin = NumberOptionPane.getValue(getRootPane(), "Minimum", "New minimum score:", new DecimalFormat("0.0"), -1000000, 1000000, 0);
			if (indexMin != null) {
				Number indexMax = NumberOptionPane.getValue(getRootPane(), "Maximum", "New maximum score:", new DecimalFormat("0.0"), -1000000, 1000000, 100);
				if(indexMax != null) {
					ScoredChromosomeWindowList scwList = selectedTrack.getData();
					Operation<ScoredChromosomeWindowList> operation = new SCWLOIndexByChromosome(scwList, indexMin.doubleValue(), indexMax.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ScoredChromosomeWindowList actionResult) {
		if (actionResult != null) {
			selectedTrack.setData(actionResult, operation.getDescription());
		}
	}		
}