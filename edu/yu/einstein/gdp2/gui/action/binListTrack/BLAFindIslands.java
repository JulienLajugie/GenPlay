/**
 * @author Alexander Golec
 * @version 0.1
 */
package yu.einstein.gdp2.gui.action.binListTrack;

import javax.swing.ActionMap;
import yu.einstein.gdp2.core.enums.IslandResultType;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOFindIslands;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.operation.Operation;
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
		"Remove all noisy data points";									// tooltip
	private BinListTrack 			selectedTrack;						// selected track
	private Track					resultTrack;						// result track
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
				int sucess = 0;	//allow to count the number of tasks achieved
				bloIsland.initOutputBinList(this.resultType.length);
				for (int i=0; i < this.resultType.length; i++) {
					if (this.resultType[i] != null) {
						bloIsland.getIsland().setResultType(this.resultType[i]);	// at this point, the resultType setting is the last to set
						bloIsland.setOutputBinList(bloIsland.getIsland().findIsland(), i);	// we store the calculated bin list on the output binlist array of bloIsland object
						sucess++;
					}
				}
				if (numResult() == sucess) {
					return bloIsland;	// if all tasks were calculated, the bloIsland can be returned
				}
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
						Track newTrack = new BinListTrack(getTrackList().getGenomeWindow(), index + 1, actionResult[i]);
						getTrackList().setTrack(index, 
												newTrack, 
												ConfigurationManager.getInstance().getTrackHeight(), 
												"peaks of " +
												selectedTrack.getName() + 
												"(" + this.resultType[i] + 
												"; L:" + bloIsland.getIsland().getReadCountLimit() +
												"; g:" + bloIsland.getIsland().getGap() +
												"; cut-off:" + bloIsland.getIsland().getCutOff() +
												")", 
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
	
	/**
	 * Count the number of valid result type.
	 * The array size will be always 2 but some fields can be null and do not been counted.
	 * @return	number of valid result type
	 */
	private int numResult() {
		int cpt = 0;
		for (int i=0; i < this.resultType.length; i++) {
			if (this.resultType[i] != null) {
				cpt++;
			}
		}
		return cpt;
	}
	
}
