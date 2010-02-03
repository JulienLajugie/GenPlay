/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;
import yu.einstein.gdp2.gui.trackList.action.binList.AddConstantAction;
import yu.einstein.gdp2.gui.trackList.action.binList.AdditionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.AverageAction;
import yu.einstein.gdp2.gui.trackList.action.binList.BinCountAction;
import yu.einstein.gdp2.gui.trackList.action.binList.CalculationOnProjectionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.CorrelationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.DivideAction;
import yu.einstein.gdp2.gui.trackList.action.binList.GaussAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IndexAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IndexByChromosomeAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IslandFinderAction;
import yu.einstein.gdp2.gui.trackList.action.binList.Log2Action;
import yu.einstein.gdp2.gui.trackList.action.binList.Log2WithDamperAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MaximumAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MinimumAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MultiplyByAction;
import yu.einstein.gdp2.gui.trackList.action.binList.NormalizeAction;
import yu.einstein.gdp2.gui.trackList.action.binList.RedoAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ResetAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ScoreCountAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SearchPeaksAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ShowHistoryAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ShowRepartitionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.StandardDeviationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SubtractAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ThresholdFilterAction;
import yu.einstein.gdp2.gui.trackList.action.binList.UndoAction;
import yu.einstein.gdp2.gui.trackList.action.general.SaveTrackAction;


/**
 * A popup menu for a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackMenu extends CurveTrackMenu {

	private static final long serialVersionUID = -1453741322870299413L; // generated ID

	private final JMenu			jmOperation;			// category operation
	
	private final JMenuItem		jmiSave;				// menu save BinListTrack
	
	private final JMenuItem		jmiUndo;				// menu undo BinListTrack
	private final JMenuItem		jmiRedo;				// menu redo BinListTrack
	private final JMenuItem		jmiReset;				// menu reset BinListTrack
	private final JMenuItem		jmiShowHistory;			// menu show BinListTrack history

	private final JMenuItem		jmiGauss;				// menu gauss BinListTrack
	private final JMenuItem		jmiIndex;				// menu index BinListTrack
	private final JMenuItem		jmiIndexByChromosome;	// menu index BinListTrack by chromosome
	private final JMenuItem		jmiAddConstant;			// menu add constant to BinListTrack
	private final JMenuItem		jmiLog2;				// menu log2 BinListTrack
	private final JMenuItem		jmiLog2WithDamper;		// menu log2 BinListTrack with damper
	private final JMenuItem		jmiNormalize;			// menu normalize BinListTrack
	private final JMenuItem		jmiShowRepartition;		// menu show repartition of the BinListTrack
	
	private final JMenuItem		jmiMinimum;				// menu minimum of the BinListTrack
	private final JMenuItem		jmiMaximum;				// menu maximum of the BinListTrack
	private final JMenuItem		jmiBinCount;			// menu bin count
	private final JMenuItem		jmiScoreCount;			// menu score count
	private final JMenuItem		jmiAverage;				// menu average
	private final JMenuItem		jmiStdDev;				// menu standard deviation
	private final JMenuItem		jmiCorrelation;			// menu correlation BinListTrack with another one
	
	private final JMenuItem		jmiAdd;					// menu add BinListTrack to another one
	private final JMenuItem		jmiSubtract;			// menu subtract BinListTrack with another one
	private final JMenuItem		jmiMultiply;			// menu multiply BinListTrack by another one
	private final JMenuItem		jmiDivide;				// menu divide BinListTrack by another one
	private final JMenuItem		jmiThresholdFilter;		// menu threshold filter
	private final JMenuItem		jmiSearchPeaks;			// menu search peaks 
	private final JMenuItem		jmiIslandFinder;		// menu find islands
	private final JMenuItem		jmiCalculOnProjection;	// menu calculation on intervals
	
	
	/**
	 * Creates an instance of a {@link BinListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public BinListTrackMenu(TrackList tl) {
		super(tl);

		jmOperation = new JMenu("Operation");
		
		jmiSave = new JMenuItem(actionMap.get(SaveTrackAction.ACTION_KEY));
		jmiUndo = new JMenuItem(actionMap.get(UndoAction.ACTION_KEY));
		jmiRedo = new JMenuItem(actionMap.get(RedoAction.ACTION_KEY));
		jmiReset = new JMenuItem(actionMap.get(ResetAction.ACTION_KEY));
		jmiShowHistory = new JMenuItem(actionMap.get(ShowHistoryAction.ACTION_KEY));
		jmiGauss = new JMenuItem(actionMap.get(GaussAction.ACTION_KEY));
		jmiIndex = new JMenuItem(actionMap.get(IndexAction.ACTION_KEY));
		jmiIndexByChromosome = new JMenuItem(actionMap.get(IndexByChromosomeAction.ACTION_KEY));
		jmiAddConstant = new JMenuItem(actionMap.get(AddConstantAction.ACTION_KEY));
		jmiLog2 = new JMenuItem(actionMap.get(Log2Action.ACTION_KEY));
		jmiLog2WithDamper = new JMenuItem(actionMap.get(Log2WithDamperAction.ACTION_KEY));
		jmiNormalize = new JMenuItem(actionMap.get(NormalizeAction.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(ShowRepartitionAction.ACTION_KEY));
		jmiMinimum = new JMenuItem(actionMap.get(MinimumAction.ACTION_KEY));
		jmiMaximum = new JMenuItem(actionMap.get(MaximumAction.ACTION_KEY));
		jmiBinCount = new JMenuItem(actionMap.get(BinCountAction.ACTION_KEY));
		jmiScoreCount = new JMenuItem(actionMap.get(ScoreCountAction.ACTION_KEY));
		jmiAverage = new JMenuItem(actionMap.get(AverageAction.ACTION_KEY));
		jmiStdDev = new JMenuItem(actionMap.get(StandardDeviationAction.ACTION_KEY));
		jmiCorrelation = new JMenuItem(actionMap.get(CorrelationAction.ACTION_KEY));		
		jmiAdd = new JMenuItem(actionMap.get(AdditionAction.ACTION_KEY));
		jmiSubtract = new JMenuItem(actionMap.get(SubtractAction.ACTION_KEY));
		jmiMultiply = new JMenuItem(actionMap.get(MultiplyByAction.ACTION_KEY));
		jmiDivide = new JMenuItem(actionMap.get(DivideAction.ACTION_KEY));
		jmiThresholdFilter = new JMenuItem(actionMap.get(ThresholdFilterAction.ACTION_KEY));
		jmiSearchPeaks = new JMenuItem(actionMap.get(SearchPeaksAction.ACTION_KEY));
		jmiIslandFinder = new JMenuItem(actionMap.get(IslandFinderAction.ACTION_KEY));
		jmiCalculOnProjection = new JMenuItem(actionMap.get(CalculationOnProjectionAction.ACTION_KEY));
		
		add(jmiUndo);
		add(jmiRedo);
		add(jmiReset);
		add(jmiShowHistory);

		jmOperation.add(jmiGauss);
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexByChromosome);
		jmOperation.add(jmiAddConstant);
		jmOperation.add(jmiLog2);
		jmOperation.add(jmiLog2WithDamper);
		jmOperation.add(jmiNormalize);
		jmOperation.add(jmiShowRepartition);
		jmOperation.addSeparator();
		jmOperation.add(jmiMinimum);
		jmOperation.add(jmiMaximum);
		jmOperation.add(jmiBinCount);
		jmOperation.add(jmiScoreCount);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStdDev);
		jmOperation.add(jmiCorrelation);
		jmOperation.addSeparator();
		jmOperation.add(jmiAdd);
		jmOperation.add(jmiSubtract);
		jmOperation.add(jmiMultiply);
		jmOperation.add(jmiDivide);
		jmOperation.add(jmiThresholdFilter);
		jmOperation.add(jmiSearchPeaks);
		jmOperation.add(jmiIslandFinder);
		jmOperation.add(jmiCalculOnProjection);
		
		add(jmOperation, 0);
		add(new Separator(), 1);

		add(jmiSave, 9);
		
		jmiUndo.setEnabled(((BinListTrack)trackList.getSelectedTrack()).isUndoable());
		jmiRedo.setEnabled(((BinListTrack)trackList.getSelectedTrack()).isRedoable());
	}
}
