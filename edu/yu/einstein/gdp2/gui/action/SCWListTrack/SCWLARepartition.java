/**
 * @author Chirag Gorasia
 * @version 0.1
 */

package yu.einstein.gdp2.gui.action.SCWListTrack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.core.list.SCWList.ScoredChromosomeWindowList;
import yu.einstein.gdp2.core.list.SCWList.operation.SCWLORepartition;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotData;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.Track;

/**
 * Generates an array containing the repartition of the score values of the selected {@link SCWListTrack}
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class SCWLARepartition extends TrackListActionOperationWorker<double [][][]>{
	
	private static final long serialVersionUID = -6665806475919318742L;
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a plot showing the repartition of the scores of the selected track";	// tooltip
	private Track<?>[] 				selectedTracks;
	private List<ScatterPlotData> 	scatPlotData;
	private int graphIndicator;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "SCWLARepartition";


	/**
	 * Creates an instance of {@link SCWLARepartition}
	 */
	public SCWLARepartition() {
		super();
		//System.out.println("In SCWLARepartition Constructor");
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		scatPlotData = new ArrayList<ScatterPlotData>();
	}


	/**
	 * @param graphIndicator the graphIndicator to set
	 */
	public void setGraphIndicator(int graphIndicator) {
		this.graphIndicator = graphIndicator;
	}


	/**
	 * @return the graphIndicator
	 */
	public int getGraphIndicator() {
		return graphIndicator;
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		SCWListTrack selectedTrack = (SCWListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Object[] graphTypes = {"Score vs Window Count", "Score vs Base Pair Count"};
			String selectedValue = (String) JOptionPane.showInputDialog(null, "Select the operation", "Graph Operation", JOptionPane.PLAIN_MESSAGE, null, graphTypes, graphTypes[0]);
			if (selectedValue != null) {
				if (selectedValue.toString().equals(graphTypes[0])) {
					// graph of score vs window count
					setGraphIndicator(1);
				} else {
					setGraphIndicator(2);
				}
				
				Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0##"), 0, 1000, 1);
				if (scoreBin != null) {	
					selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getSCWListTracks());
					if ((selectedTracks != null)) {
						ScoredChromosomeWindowList[] scwListArray = new ScoredChromosomeWindowList[selectedTracks.length];
						for (int i = 0; i < selectedTracks.length; i++) {
							scwListArray[i] = ((SCWListTrack)selectedTracks[i]).getData();						
						}	
						if (scwListArray.length > 0) {
							Operation<double[][][]> operation = new SCWLORepartition(scwListArray, scoreBin.doubleValue(), getGraphIndicator());
							return operation;
						}
					}
				}				
			}
		}
		return null;
	}

	@Override
	protected void doAtTheEnd(double[][][] actionResult) {
		if (actionResult != null && selectedTracks.length != 0) {
			for (int k = 0; k < actionResult.length; k++) {
				scatPlotData.add(new ScatterPlotData(actionResult[k], selectedTracks[k].getName()));
			}
			ScatterPlotPanel.setxAxisName("Score");
			ScatterPlotPanel.setyAxisName("Count");
			ScatterPlotPanel.showDialog(getRootPane(), scatPlotData);
		}
	}
}