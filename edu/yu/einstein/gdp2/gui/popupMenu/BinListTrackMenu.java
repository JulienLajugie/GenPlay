/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import yu.einstein.gdp2.gui.action.allTrack.ATASave;
import yu.einstein.gdp2.gui.action.binListTrack.BLAAdd;
import yu.einstein.gdp2.gui.action.binListTrack.BLAAddConstant;
import yu.einstein.gdp2.gui.action.binListTrack.BLAAverage;
import yu.einstein.gdp2.gui.action.binListTrack.BLACountNonNullBins;
import yu.einstein.gdp2.gui.action.binListTrack.BLACalculationOnProjection;
import yu.einstein.gdp2.gui.action.binListTrack.BLAChangeBinSize;
import yu.einstein.gdp2.gui.action.binListTrack.BLAChangeDataPrecision;
import yu.einstein.gdp2.gui.action.binListTrack.BLACompress;
import yu.einstein.gdp2.gui.action.binListTrack.BLAConcatenate;
import yu.einstein.gdp2.gui.action.binListTrack.BLACorrelate;
import yu.einstein.gdp2.gui.action.binListTrack.BLADensity;
import yu.einstein.gdp2.gui.action.binListTrack.BLADivide;
import yu.einstein.gdp2.gui.action.binListTrack.BLADivideConstant;
import yu.einstein.gdp2.gui.action.binListTrack.BLAFilter;
import yu.einstein.gdp2.gui.action.binListTrack.BLAGauss;
import yu.einstein.gdp2.gui.action.binListTrack.BLAIndex;
import yu.einstein.gdp2.gui.action.binListTrack.BLAIndexByChromosome;
import yu.einstein.gdp2.gui.action.binListTrack.BLAFindIslands;
import yu.einstein.gdp2.gui.action.binListTrack.BLAInvertConstant;
import yu.einstein.gdp2.gui.action.binListTrack.BLALog2;
import yu.einstein.gdp2.gui.action.binListTrack.BLALog2OnAvgWithDamper;
import yu.einstein.gdp2.gui.action.binListTrack.BLAMax;
import yu.einstein.gdp2.gui.action.binListTrack.BLAMin;
import yu.einstein.gdp2.gui.action.binListTrack.BLAMultiply;
import yu.einstein.gdp2.gui.action.binListTrack.BLAMultiplyConstant;
import yu.einstein.gdp2.gui.action.binListTrack.BLANormalize;
import yu.einstein.gdp2.gui.action.binListTrack.BLANormalizeStandardScore;
import yu.einstein.gdp2.gui.action.binListTrack.BLASaturate;
import yu.einstein.gdp2.gui.action.binListTrack.BLASumScore;
import yu.einstein.gdp2.gui.action.binListTrack.BLASearchPeaks;
import yu.einstein.gdp2.gui.action.binListTrack.BLARepartition;
import yu.einstein.gdp2.gui.action.binListTrack.BLAStandardDeviation;
import yu.einstein.gdp2.gui.action.binListTrack.BLASubtract;
import yu.einstein.gdp2.gui.action.binListTrack.BLASubtractConstant;
import yu.einstein.gdp2.gui.action.binListTrack.BLATransfrag;
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
	
	private final JCheckBoxMenuItem jcbmiCompression;	// check box menu compression
	
	private final JMenuItem		jmiAddConstant;			// menu add constant to BinListTrack
	private final JMenuItem		jmiSubtractConstant;	// menu subtract constant from BinListTrack
	private final JMenuItem		jmiMultiplyConstant;	// menu multiply BinListTrack by constant
	private final JMenuItem		jmiDivideConstant;		// menu divide BinListTrack by constant
	private final JMenuItem		jmiInvertConstant;		// menu invert BinListTrack
	
	private final JMenuItem		jmiAdd;					// menu add BinListTrack to another one
	private final JMenuItem		jmiSubtract;			// menu subtract another BinListTrack from selected one
	private final JMenuItem		jmiMultiply;			// menu multiply BinListTrack by another one
	private final JMenuItem		jmiDivide;				// menu divide BinListTrack by another one
	
	private final JMenuItem		jmiGauss;				// menu gauss BinListTrack
	private final JMenuItem		jmiIndex;				// menu index BinListTrack
	private final JMenuItem		jmiIndexPerChromosome;	// menu index BinListTrack per chromosome
	private final JMenuItem		jmiLog2;				// menu log2 BinListTrack
	private final JMenuItem		jmiLog2WithDamper;		// menu log2 BinListTrack with damper
	private final JMenuItem		jmiNormalize;			// menu normalize BinListTrack
	private final JMenuItem		jmiNormalizeStdScore;	// menu normalize standard score BinListTrack
	
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
		
		jmiSave = new JMenuItem(actionMap.get(ATASave.ACTION_KEY));

		jcbmiCompression = new JCheckBoxMenuItem(actionMap.get(BLACompress.ACTION_KEY));
		
		jmiAddConstant = new JMenuItem(actionMap.get(BLAAddConstant.ACTION_KEY));
		jmiSubtractConstant = new JMenuItem(actionMap.get(BLASubtractConstant.ACTION_KEY));
		jmiMultiplyConstant = new JMenuItem(actionMap.get(BLAMultiplyConstant.ACTION_KEY));
		jmiDivideConstant = new JMenuItem(actionMap.get(BLADivideConstant.ACTION_KEY));
		jmiInvertConstant = new JMenuItem(actionMap.get(BLAInvertConstant.ACTION_KEY));
		
		jmiAdd = new JMenuItem(actionMap.get(BLAAdd.ACTION_KEY));
		jmiSubtract = new JMenuItem(actionMap.get(BLASubtract.ACTION_KEY));
		jmiMultiply = new JMenuItem(actionMap.get(BLAMultiply.ACTION_KEY));
		jmiDivide = new JMenuItem(actionMap.get(BLADivide.ACTION_KEY));
		
		jmiGauss = new JMenuItem(actionMap.get(BLAGauss.ACTION_KEY));
		jmiIndex = new JMenuItem(actionMap.get(BLAIndex.ACTION_KEY));
		jmiIndexPerChromosome = new JMenuItem(actionMap.get(BLAIndexByChromosome.ACTION_KEY));
		jmiLog2 = new JMenuItem(actionMap.get(BLALog2.ACTION_KEY));
		jmiLog2WithDamper = new JMenuItem(actionMap.get(BLALog2OnAvgWithDamper.ACTION_KEY));
		jmiNormalize = new JMenuItem(actionMap.get(BLANormalize.ACTION_KEY));
		jmiNormalizeStdScore = new JMenuItem(actionMap.get(BLANormalizeStandardScore.ACTION_KEY));
		
		jmiMinimum = new JMenuItem(actionMap.get(BLAMin.ACTION_KEY));
		jmiMaximum = new JMenuItem(actionMap.get(BLAMax.ACTION_KEY));
		jmiBinCount = new JMenuItem(actionMap.get(BLACountNonNullBins.ACTION_KEY));
		jmiScoreCount = new JMenuItem(actionMap.get(BLASumScore.ACTION_KEY));
		jmiAverage = new JMenuItem(actionMap.get(BLAAverage.ACTION_KEY));
		jmiStdDev = new JMenuItem(actionMap.get(BLAStandardDeviation.ACTION_KEY));
		jmiCorrelation = new JMenuItem(actionMap.get(BLACorrelate.ACTION_KEY));		
		
		jmiFilter = new JMenuItem(actionMap.get(BLAFilter.ACTION_KEY));
		jmiSaturation = new JMenuItem(actionMap.get(BLASaturate.ACTION_KEY));
		jmiSearchPeaks = new JMenuItem(actionMap.get(BLASearchPeaks.ACTION_KEY));
		jmiIslandFinder = new JMenuItem(actionMap.get(BLAFindIslands.ACTION_KEY));
		jmiTransfrag =  new JMenuItem(actionMap.get(BLATransfrag.ACTION_KEY));
		
		jmiChangeBinSize = new JMenuItem(actionMap.get(BLAChangeBinSize.ACTION_KEY));
		jmiChangePrecision = new JMenuItem(actionMap.get(BLAChangeDataPrecision.ACTION_KEY));
		
		jmiDensity = new JMenuItem(actionMap.get(BLADensity.ACTION_KEY));
		jmiCalculOnProjection = new JMenuItem(actionMap.get(BLACalculationOnProjection.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(BLARepartition.ACTION_KEY));
		jmiConcatenate = new JMenuItem(actionMap.get(BLAConcatenate.ACTION_KEY));
		
		addSeparator();
		add(jcbmiCompression);
		
		jmOperation.add(jmiAddConstant);
		jmOperation.add(jmiSubtractConstant);
		jmOperation.add(jmiMultiplyConstant);
		jmOperation.add(jmiDivideConstant);
		jmOperation.add(jmiInvertConstant);
		jmOperation.addSeparator();
		jmOperation.add(jmiAdd);
		jmOperation.add(jmiSubtract);
		jmOperation.add(jmiMultiply);
		jmOperation.add(jmiDivide);
		jmOperation.addSeparator();
		jmOperation.add(jmiGauss);
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexPerChromosome);
		jmOperation.add(jmiLog2);
		jmOperation.add(jmiLog2WithDamper);
		jmOperation.add(jmiNormalize);
		jmOperation.add(jmiNormalizeStdScore);
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
	}
	
	
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		super.popupMenuWillBecomeVisible(arg0);
		// check the compression checkbox if the selected list is checked
		BinListTrack blt = (BinListTrack) trackList.getSelectedTrack();
		if (blt != null) {
			jcbmiCompression.setState(blt.getData().isCompressed());
		}
		jmOperation.setEnabled(!jcbmiCompression.getState());
		jmiSave.setEnabled(!jcbmiCompression.getState());
	}
}
