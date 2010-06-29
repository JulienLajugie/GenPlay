/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JDialog;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLORepartition;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.MultiTrackChooser;
import yu.einstein.gdp2.gui.dialog.NumberOptionPane;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotData;
import yu.einstein.gdp2.gui.scatterPlot.ScatterPlotPanel;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Generates an array containing the repartition of the score values of the selected {@link BinListTrack}
 * @author Chirag Gorasia
 * @version 0.1
 */
public final class BLARepartition extends TrackListActionOperationWorker<double [][][]> {

	private static final long serialVersionUID = -7166030548181210580L; // generated ID
	private static final String 	ACTION_NAME = "Show Repartition";	// action name
	private static final String 	DESCRIPTION = 
		"Generate a csv file showing the repartition of the scores of the selected track";	// tooltip
	private Track[] selectedTracks;
	private List<ScatterPlotData> scatPlotData;
	private ScatterPlotPanel scatPlotPanel;

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLARepartition";


	/**
	 * Creates an instance of {@link BLARepartition}
	 */
	public BLARepartition() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		scatPlotData = new ArrayList<ScatterPlotData>();
	}


	@Override
	public Operation<double [][][]> initializeOperation() {
		BinListTrack selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			Number scoreBin = NumberOptionPane.getValue(getRootPane(), "Size", "Enter the size of the bin of score:", new DecimalFormat("0.0"), 0, 1000, 1);
			if (scoreBin != null) {	
				selectedTracks = MultiTrackChooser.getSelectedTracks(getRootPane(), getTrackList().getBinListTracks());
				if ((selectedTracks != null)) {
					BinList[] binListArray = new BinList[selectedTracks.length];
					for (int i = 0; i < selectedTracks.length; i++) {
						binListArray[i] = ((BinListTrack)selectedTracks[i]).getData();						
					}					
					Operation<double[][][]> operation = new BLORepartition(binListArray, scoreBin.doubleValue());
					return operation;
				}
			}
		}
		return null;
	}
	
	
	@Override
	protected void doAtTheEnd(double[][][] actionResult) {
		for (int k = 0; k < actionResult.length; k++) {
			scatPlotData.add(new ScatterPlotData(actionResult[k], selectedTracks[k].getName()));
		}
		scatPlotPanel = new ScatterPlotPanel(scatPlotData);
		//scatPlotMenu = new ScatterPlotRightClickedMenu();
		JDialog jf = new JDialog();
		jf.setTitle("Scatter Plot");
		jf.setContentPane(scatPlotPanel);
		//jf.add(scatPlotMenu);
		jf.setPreferredSize(new Dimension(900, 700));
		jf.setMinimumSize(new Dimension(500, 500));
		jf.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - jf.getWidth())/2, (Toolkit.getDefaultToolkit().getScreenSize().height - jf.getHeight())/2);
		jf.pack();
		jf.setVisible(true);
	}
}
