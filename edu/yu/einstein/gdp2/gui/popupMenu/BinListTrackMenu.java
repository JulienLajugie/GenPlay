/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import yu.einstein.gdp2.gui.action.allTrack.SaveTrackAction;
import yu.einstein.gdp2.gui.action.binListTrack.AdditionAction;
import yu.einstein.gdp2.gui.action.binListTrack.AdditionConstantAction;
import yu.einstein.gdp2.gui.action.binListTrack.AverageAction;
import yu.einstein.gdp2.gui.action.binListTrack.BinCountAction;
import yu.einstein.gdp2.gui.action.binListTrack.CalculationOnProjectionAction;
import yu.einstein.gdp2.gui.action.binListTrack.ChangeBinSizeAction;
import yu.einstein.gdp2.gui.action.binListTrack.ChangePrecisionAction;
import yu.einstein.gdp2.gui.action.binListTrack.CompressionAction;
import yu.einstein.gdp2.gui.action.binListTrack.ConcatenateAction;
import yu.einstein.gdp2.gui.action.binListTrack.CorrelationAction;
import yu.einstein.gdp2.gui.action.binListTrack.DensityAction;
import yu.einstein.gdp2.gui.action.binListTrack.DivisionAction;
import yu.einstein.gdp2.gui.action.binListTrack.DivisionConstantAction;
import yu.einstein.gdp2.gui.action.binListTrack.FilterAction;
import yu.einstein.gdp2.gui.action.binListTrack.GaussAction;
import yu.einstein.gdp2.gui.action.binListTrack.IndexationAction;
import yu.einstein.gdp2.gui.action.binListTrack.IndexationPerChromosomeAction;
import yu.einstein.gdp2.gui.action.binListTrack.IslandFinderAction;
import yu.einstein.gdp2.gui.action.binListTrack.Log2Action;
import yu.einstein.gdp2.gui.action.binListTrack.Log2WithDamperAction;
import yu.einstein.gdp2.gui.action.binListTrack.MaximumAction;
import yu.einstein.gdp2.gui.action.binListTrack.MinimumAction;
import yu.einstein.gdp2.gui.action.binListTrack.MultiplicationAction;
import yu.einstein.gdp2.gui.action.binListTrack.MultiplicationConstantAction;
import yu.einstein.gdp2.gui.action.binListTrack.NormalizationAction;
import yu.einstein.gdp2.gui.action.binListTrack.RedoAction;
import yu.einstein.gdp2.gui.action.binListTrack.ResetAction;
import yu.einstein.gdp2.gui.action.binListTrack.SaturationAction;
import yu.einstein.gdp2.gui.action.binListTrack.ScoreCountAction;
import yu.einstein.gdp2.gui.action.binListTrack.SearchPeaksAction;
import yu.einstein.gdp2.gui.action.binListTrack.ShowHistoryAction;
import yu.einstein.gdp2.gui.action.binListTrack.ShowRepartitionAction;
import yu.einstein.gdp2.gui.action.binListTrack.StandardDeviationAction;
import yu.einstein.gdp2.gui.action.binListTrack.SubtractionAction;
import yu.einstein.gdp2.gui.action.binListTrack.SubtractionConstantAction;
import yu.einstein.gdp2.gui.action.binListTrack.TransfragAction;
import yu.einstein.gdp2.gui.action.binListTrack.UndoAction;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


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

	private final JCheckBoxMenuItem jcbmiCompression;	// check box menu compression
	
	private final JMenuItem		jmiAdditionConstant;	// menu add constant to BinListTrack
	private final JMenuItem		jmiSubtractionConstant;	// menu subtract constant from BinListTrack
	private final JMenuItem		jmiMultiplicationConstant;// menu multiply BinListTrack by constant
	private final JMenuItem		jmiDivisionConstant;	// menu divide BinListTrack by constant
	
	private final JMenuItem		jmiAddition;			// menu add BinListTrack to another one
	private final JMenuItem		jmiSubtraction;			// menu subtract another BinListTrack from selected one
	private final JMenuItem		jmiMultiplication;		// menu multiply BinListTrack by another one
	private final JMenuItem		jmiDivision;			// menu divide BinListTrack by another one
	
	private final JMenuItem		jmiGauss;				// menu gauss BinListTrack
	private final JMenuItem		jmiIndex;				// menu index BinListTrack
	private final JMenuItem		jmiIndexPerChromosome;	// menu index BinListTrack per chromosome
	private final JMenuItem		jmiLog2;				// menu log2 BinListTrack
	private final JMenuItem		jmiLog2WithDamper;		// menu log2 BinListTrack with damper
	private final JMenuItem		jmiNormalize;			// menu normalize BinListTrack
	
	private final JMenuItem		jmiMinimum;				// menu minimum of the BinListTrack
	private final JMenuItem		jmiMaximum;				// menu maximum of the BinListTrack
	private final JMenuItem		jmiBinCount;			// menu bin count
	private final JMenuItem		jmiScoreCount;			// menu score count
	private final JMenuItem		jmiAverage;				// menu average
	private final JMenuItem		jmiStdDev;				// menu standard deviation
	private final JMenuItem		jmiCorrelation;			// menu correlation BinListTrack with another one
	
	private final JMenuItem		jmiFilter;				// menu filter
	private final JMenuItem		jmiSaturation;			// menu saturate BinListTrack
	private final JMenuItem		jmiSearchPeaks;			// menu search peaks 
	private final JMenuItem		jmiIslandFinder;		// menu find islands
	private final JMenuItem		jmiTransfrag;			// menu transfrag
	
	private final JMenuItem		jmiChangeBinSize;		// menu change bin size
	private final JMenuItem		jmiChangePrecision;		// menu change data precision
	
	private final JMenuItem		jmiDensity;				// menu density of none null windows
	private final JMenuItem		jmiCalculOnProjection;	// menu calculation on intervals
	private final JMenuItem		jmiShowRepartition;		// menu show repartition of the BinListTrack
	private final JMenuItem		jmiConcatenate;			// menu concatenate
	
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

		jcbmiCompression = new JCheckBoxMenuItem(actionMap.get(CompressionAction.ACTION_KEY));
		
		jmiAdditionConstant = new JMenuItem(actionMap.get(AdditionConstantAction.ACTION_KEY));
		jmiSubtractionConstant = new JMenuItem(actionMap.get(SubtractionConstantAction.ACTION_KEY));
		jmiMultiplicationConstant = new JMenuItem(actionMap.get(MultiplicationConstantAction.ACTION_KEY));
		jmiDivisionConstant = new JMenuItem(actionMap.get(DivisionConstantAction.ACTION_KEY));
		
		jmiAddition = new JMenuItem(actionMap.get(AdditionAction.ACTION_KEY));
		jmiSubtraction = new JMenuItem(actionMap.get(SubtractionAction.ACTION_KEY));
		jmiMultiplication = new JMenuItem(actionMap.get(MultiplicationAction.ACTION_KEY));
		jmiDivision = new JMenuItem(actionMap.get(DivisionAction.ACTION_KEY));
		
		jmiGauss = new JMenuItem(actionMap.get(GaussAction.ACTION_KEY));
		jmiIndex = new JMenuItem(actionMap.get(IndexationAction.ACTION_KEY));
		jmiIndexPerChromosome = new JMenuItem(actionMap.get(IndexationPerChromosomeAction.ACTION_KEY));
		jmiLog2 = new JMenuItem(actionMap.get(Log2Action.ACTION_KEY));
		jmiLog2WithDamper = new JMenuItem(actionMap.get(Log2WithDamperAction.ACTION_KEY));
		jmiNormalize = new JMenuItem(actionMap.get(NormalizationAction.ACTION_KEY));
		
		jmiMinimum = new JMenuItem(actionMap.get(MinimumAction.ACTION_KEY));
		jmiMaximum = new JMenuItem(actionMap.get(MaximumAction.ACTION_KEY));
		jmiBinCount = new JMenuItem(actionMap.get(BinCountAction.ACTION_KEY));
		jmiScoreCount = new JMenuItem(actionMap.get(ScoreCountAction.ACTION_KEY));
		jmiAverage = new JMenuItem(actionMap.get(AverageAction.ACTION_KEY));
		jmiStdDev = new JMenuItem(actionMap.get(StandardDeviationAction.ACTION_KEY));
		jmiCorrelation = new JMenuItem(actionMap.get(CorrelationAction.ACTION_KEY));		
		
		jmiFilter = new JMenuItem(actionMap.get(FilterAction.ACTION_KEY));
		jmiSaturation = new JMenuItem(actionMap.get(SaturationAction.ACTION_KEY));
		jmiSearchPeaks = new JMenuItem(actionMap.get(SearchPeaksAction.ACTION_KEY));
		jmiIslandFinder = new JMenuItem(actionMap.get(IslandFinderAction.ACTION_KEY));
		jmiTransfrag =  new JMenuItem(actionMap.get(TransfragAction.ACTION_KEY));
		
		jmiChangeBinSize = new JMenuItem(actionMap.get(ChangeBinSizeAction.ACTION_KEY));
		jmiChangePrecision = new JMenuItem(actionMap.get(ChangePrecisionAction.ACTION_KEY));
		
		jmiDensity = new JMenuItem(actionMap.get(DensityAction.ACTION_KEY));
		jmiCalculOnProjection = new JMenuItem(actionMap.get(CalculationOnProjectionAction.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(ShowRepartitionAction.ACTION_KEY));
		jmiConcatenate = new JMenuItem(actionMap.get(ConcatenateAction.ACTION_KEY));
		
		add(jmiUndo);
		add(jmiRedo);
		add(jmiReset);
		add(jmiShowHistory);
		addSeparator();
		add(jcbmiCompression);
		
		jmOperation.add(jmiAdditionConstant);
		jmOperation.add(jmiSubtractionConstant);
		jmOperation.add(jmiMultiplicationConstant);
		jmOperation.add(jmiDivisionConstant);
		jmOperation.addSeparator();
		jmOperation.add(jmiAddition);
		jmOperation.add(jmiSubtraction);
		jmOperation.add(jmiMultiplication);
		jmOperation.add(jmiDivision);
		jmOperation.addSeparator();
		jmOperation.add(jmiGauss);
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexPerChromosome);
		jmOperation.add(jmiLog2);
		jmOperation.add(jmiLog2WithDamper);
		jmOperation.add(jmiNormalize);
		jmOperation.addSeparator();
		jmOperation.add(jmiMinimum);
		jmOperation.add(jmiMaximum);
		jmOperation.add(jmiBinCount);
		jmOperation.add(jmiScoreCount);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStdDev);
		jmOperation.add(jmiCorrelation);
		jmOperation.addSeparator();
		jmOperation.add(jmiFilter);
		jmOperation.add(jmiSaturation);
		jmOperation.add(jmiSearchPeaks);
		jmOperation.add(jmiIslandFinder);
		jmOperation.add(jmiTransfrag);
		jmOperation.addSeparator();
		jmOperation.add(jmiChangeBinSize);
		jmOperation.add(jmiChangePrecision);
		jmOperation.addSeparator();
		jmOperation.add(jmiDensity);
		jmOperation.add(jmiShowRepartition);
		jmOperation.add(jmiConcatenate);
		jmOperation.add(jmiCalculOnProjection);
		
		add(jmOperation, 0);
		add(new Separator(), 1);

		add(jmiSave, 9);
		
		jmiUndo.setEnabled(((BinListTrack)trackList.getSelectedTrack()).isUndoable());
		jmiRedo.setEnabled(((BinListTrack)trackList.getSelectedTrack()).isRedoable());
	}
	
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		super.popupMenuWillBecomeVisible(arg0);
		// check the compression checkbox if the selected list is checked
		BinListTrack blt = (BinListTrack) trackList.getSelectedTrack();
		if (blt != null) {
			jcbmiCompression.setState(blt.getBinList().isCompressed());
		}
		jmOperation.setEnabled(!jcbmiCompression.getState());
	}
}
