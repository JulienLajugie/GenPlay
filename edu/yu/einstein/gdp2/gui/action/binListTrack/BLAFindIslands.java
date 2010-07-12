/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import java.awt.Color;

import javax.swing.ActionMap;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindIslands;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.exception.InvalidFactorialParameterException;
import yu.einstein.gdp2.exception.InvalidLambdaPoissonParameterException;
import yu.einstein.gdp2.gui.action.TrackListActionOperationWorker;
import yu.einstein.gdp2.gui.dialog.IslandDialog;
import yu.einstein.gdp2.gui.dialog.TrackChooser;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * Indexes the selected {@link BinListTrack} by chromosome
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class BLAFindIslands extends TrackListActionOperationWorker<BinList[]> {

	private static final long serialVersionUID = -3178294348003123920L;	// generated ID
	private static final String 	ACTION_NAME = "Find Islands";		// action name
	private static final String 	DESCRIPTION = 
		"Find island on the track";									// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	private Track<?>				resultTrack;						// result track
	private IslandResultType[]		resultType;							// needs to know which kind of result must be realised
	private BLOFindIslands			bloIsland;							// necessary to manage the result type array
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "BLAFindIslands";


	/**
	 * Creates an instance of {@link BLAIndexByChromosome}
	 */
	public BLAFindIslands() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}

	
	@Override
	public Operation<BinList[]> initializeOperation() throws Exception {
		selectedTrack = (BinListTrack) getTrackList().getSelectedTrack();
		if (selectedTrack != null) {
			bloIsland = new BLOFindIslands(selectedTrack.getData());
			IslandDialog dialog = new IslandDialog (bloIsland.getIsland(), this);
			if (dialog.showTrackConfiguration(getRootPane()) == IslandDialog.APPROVE_OPTION) {	// result type array is initialized on the showTrackConfiguration method
				bloIsland.setList(this.resultType);
				return bloIsland;
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(BinList[] actionResult) {
		if (actionResult != null) {
			int index;
			for (int i=0; i < actionResult.length; i++) {	// we have to treat all actions result
				if (actionResult[i] != null){
					resultTrack = TrackChooser.getTracks(getRootPane(),
							"Choose A Track", 
							"Generate the " + this.resultType[i].toString() + " result on track:", 
							getTrackList().getEmptyTracks());	// purposes tracks
					if (resultTrack != null) {
						index = resultTrack.getTrackNumber() - 1;
						BinListTrack newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult[i]);
						newTrack.getHistory().add("Window Size = " + actionResult[i].getBinSize() + "bp, Precision = " + actionResult[i].getPrecision(), Color.GRAY);
						newTrack.getHistory().add("Island Finder - Information", Color.BLACK);
						newTrack.getHistory().add("Average: " + bloIsland.getIsland().getLambda(), Color.GRAY);
						try {
							newTrack.getHistory().add("P-Value: " + bloIsland.getIsland().findPValue(bloIsland.getIsland().getWindowLimitValue()), Color.GRAY);
						} catch (InvalidLambdaPoissonParameterException e) {
							e.printStackTrace();
						} catch (InvalidFactorialParameterException e) {
							e.printStackTrace();
						}
						newTrack.getHistory().add("Island Finder - Input parameters", Color.BLACK);
						newTrack.getHistory().add("Window value: " + bloIsland.getIsland().getWindowLimitValue(), Color.GRAY);
						newTrack.getHistory().add("Gap: " + bloIsland.getIsland().getGap(), Color.GRAY);
						newTrack.getHistory().add("Island score: " + bloIsland.getIsland().getIslandLimitScore(), Color.GRAY);
						newTrack.getHistory().add("Island length: " + bloIsland.getIsland().getMinIslandLength(), Color.GRAY);
						newTrack.getHistory().add("Island Finder - Output parameters", Color.BLACK);
						newTrack.getHistory().add("Result type: " + bloIsland.getIsland().getResultType(), Color.GRAY);
						getTrackList().setTrack(index, 
												newTrack, 
												ConfigurationManager.getInstance().getTrackHeight(), 
												"peaks of " +
												selectedTrack.getName() + 
												" (Island Finder process)", 
												selectedTrack.getStripes());
					}
				}
			}
		}
	}


	/**
	 * Set the result type array
	 * @param resultType	result type array input 
	 */
	public void setResultType(IslandResultType[] resultType) {
		this.resultType = resultType;
	}
	
}
