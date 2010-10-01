package yu.einstein.gdp2.gui.action.geneListTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ActionMap;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.list.geneList.GeneListMiddlePositionComparator;
import yu.einstein.gdp2.core.list.geneList.GeneListStopPositionComparator;
import yu.einstein.gdp2.core.list.geneList.operation.GLODistanceCalculator;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.DistanceCalculatorDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotData;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPane;
import yu.einstein.gdp2.gui.track.GeneListTrack;

public class GLADistanceCalculator extends TrackListActionOperationWorker<long[][]>{

	private static final long serialVersionUID = 1401297625985870348L;
	private static final String 	ACTION_NAME = "Distance Calculation";		// action name
	private static final String 	DESCRIPTION = 
		"Compute the number of base pairs at a specific distance between " +
		"the selected track and another track";							// tooltip
	private GeneListTrack 			selectedTrack;						// 1st selected track  	
	private GeneListTrack 			otherTrack;							// 2nd selected track
	private DistanceCalculatorDialog dcd;	
	private List<ScatterPlotData> 	scatPlotData;
	private String					graphName;
	
	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "GLADistanceCalculator";


	/**
	 * Creates an instance of {@link BLADistanceCalculator}
	 */
	public GLADistanceCalculator() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		scatPlotData = new ArrayList<ScatterPlotData>();
	}

	@Override
	public Operation<long[][]> initializeOperation() throws Exception {
		selectedTrack = (GeneListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			otherTrack = (GeneListTrack) TrackChooser.getTracks(getRootPane(), "Choose A Track", "Calculate the distance with:", getTrackList().getGeneListTracks());
			if (otherTrack != null) {
				GeneList geneList1 = new GeneList(selectedTrack.getData());
				GeneList geneList2 = new GeneList(otherTrack.getData());
				graphName = "distance between " + selectedTrack.getName().substring(0, 10) + " and " + otherTrack.getName().substring(0, 10);
				dcd = new DistanceCalculatorDialog();
				if (dcd.showDialog(getRootPane()) == DistanceCalculatorDialog.APPROVE_OPTION) {
					if (dcd.getSelectionFlag() == 3 || dcd.getSelectionFlag() == 6 || dcd.getSelectionFlag() == 9 || dcd.getSelectionFlag() == 12 || dcd.getSelectionFlag() == 15 || dcd.getSelectionFlag() == 18) {
						GeneListStopPositionComparator comp = new GeneListStopPositionComparator();
						for (List<Gene> currentList: geneList2) {
							if (currentList != null) {
								Collections.sort(currentList,comp);
							}
						}
					} else if (dcd.getSelectionFlag() == 2 || dcd.getSelectionFlag() == 5 || dcd.getSelectionFlag() == 8 || dcd.getSelectionFlag() == 11 || dcd.getSelectionFlag() == 13 || dcd.getSelectionFlag() == 15) {
						GeneListMiddlePositionComparator comp = new GeneListMiddlePositionComparator();
						for (List<Gene> currentList: geneList2) {
							if (currentList != null) {
								Collections.sort(currentList,comp);
							}
						}
					}
					Operation<long[][]> operation = new GLODistanceCalculator(geneList1, geneList2, dcd.getSelectionFlag());
					return operation;
				}
			}
		}
		return null;
	}
	
	@Override
	protected void doAtTheEnd(long[][] actionResult) {
		if (actionResult != null) {
			Map<Long,Integer> counter = new HashMap<Long, Integer>();
			for (int i = 0; i < actionResult.length; i++) {
				for (int j = 0; j < actionResult[i].length; j++) {
					if (counter.containsKey(actionResult[i][j]) != true) {
						counter.put((Long) actionResult[i][j], 1);
					} else {
						int newValue = counter.get(actionResult[i][j]) + 1;
						counter.put((Long) actionResult[i][j], newValue);
					}
				}
			}
			
			Map<Long,Integer> sortedCounter = new TreeMap<Long, Integer>(counter);
			Iterator<Long> iter = sortedCounter.keySet().iterator();
			int i = 0;
			double[][] plotData = new double[sortedCounter.size()][2];
			while (iter.hasNext()) {
				long key = iter.next();
				plotData[i][0] = key;
				plotData[i][1] = sortedCounter.get(key);
				i++;
			}
			scatPlotData.add(new ScatterPlotData(plotData, graphName, ScatterPlotData.generateRandomColor()));
			ScatterPlotPane.showDialog(getRootPane(), "distande", "Count", scatPlotData);
		}
	}
}