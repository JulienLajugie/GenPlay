/**
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAAddConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAAverage;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLACountNonNullLength;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLADivideConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAGenerateBinList;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAIndex;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAIndexByChromosome;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAInvertConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLALog;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLALogOnAvgWithDamper;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMax;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMin;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMultiplyConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLANormalizeStandardScore;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLARepartition;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAStandardDeviation;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLASubtractConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLATwoTracks;
import yu.einstein.gdp2.gui.action.allTrack.ATASave;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link SCWListTrack}
 * @author Julien Lajugie
 * @author Chirag Gorasia
 * @version 0.1
 */
public class SCWListTrackMenu extends CurveTrackMenu {

	private static final long serialVersionUID = 3249783097749893180L; // generated ID

	private final JMenu		jmOperation;			// category operation

	private final JMenuItem	jmiSave;				// menu save
	
	private final JMenuItem jmiAddConstant;			// menu add constant
	private final JMenuItem jmiAverage;				// menu average
	private final JMenuItem	jmiCountNonNullLength;	// menu count non null length
	private final JMenuItem jmiDivideConstant;		// menu divide constant
	private final JMenuItem jmiTwoTracks;			// menu two tracks operation
	private final JMenuItem jmiGenerateBinList;		// menu generate a BinList track
	private final JMenuItem	jmiIndex;				// menu index 
	private final JMenuItem	jmiIndexPerChromosome;	// menu index per chromosome
	private final JMenuItem jmiInvertConstant;		// menu invert constant
	private final JMenuItem	jmiLog;					// menu log 
	private final JMenuItem	jmiLogWithDamper;		// menu log with damper
	private final JMenuItem	jmiMax;					// menu max
	private final JMenuItem	jmiMin;					// menu min
	private final JMenuItem jmiMultiplyConstant;	// menu multiply constant
	private final JMenuItem	jmiNormalizeStdScore;	// menu normalize standard score
	private final JMenuItem	jmiStandardDeviation;	// menu standard deviation
	private final JMenuItem jmiSubtractConstant;	// menu subtract constant
	private final JMenuItem	jmiShowRepartition;		// menu show repartition of the SCWListTrack
	

	/**
	 * Creates an instance of a {@link SCWListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SCWListTrackMenu(TrackList tl) {
		super(tl);
		jmOperation = new JMenu("Operation");

		jmiSave = new JMenuItem(actionMap.get(ATASave.ACTION_KEY));
		
		jmiAddConstant = new JMenuItem(actionMap.get(SCWLAAddConstant.ACTION_KEY));
		jmiAverage = new JMenuItem(actionMap.get(SCWLAAverage.ACTION_KEY));
		jmiCountNonNullLength = new JMenuItem(actionMap.get(SCWLACountNonNullLength.ACTION_KEY));
		jmiDivideConstant = new JMenuItem(actionMap.get(SCWLADivideConstant.ACTION_KEY));
		jmiTwoTracks = new JMenuItem(actionMap.get(SCWLATwoTracks.ACTION_KEY));
		jmiGenerateBinList = new JMenuItem(actionMap.get(SCWLAGenerateBinList.ACTION_KEY));		
		jmiIndex = new JMenuItem(actionMap.get(SCWLAIndex.ACTION_KEY));
		jmiIndexPerChromosome = new JMenuItem(actionMap.get(SCWLAIndexByChromosome.ACTION_KEY));
		jmiInvertConstant = new JMenuItem(actionMap.get(SCWLAInvertConstant.ACTION_KEY));
		jmiLog = new JMenuItem(actionMap.get(SCWLALog.ACTION_KEY));
		jmiLogWithDamper = new JMenuItem(actionMap.get(SCWLALogOnAvgWithDamper.ACTION_KEY));
		jmiMax = new JMenuItem(actionMap.get(SCWLAMax.ACTION_KEY));
		jmiMin = new JMenuItem(actionMap.get(SCWLAMin.ACTION_KEY));
		jmiMultiplyConstant = new JMenuItem(actionMap.get(SCWLAMultiplyConstant.ACTION_KEY));
		jmiNormalizeStdScore = new JMenuItem(actionMap.get(SCWLANormalizeStandardScore.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(SCWLARepartition.ACTION_KEY));
		jmiStandardDeviation = new JMenuItem(actionMap.get(SCWLAStandardDeviation.ACTION_KEY));
		jmiSubtractConstant = new JMenuItem(actionMap.get(SCWLASubtractConstant.ACTION_KEY));		

		add(jmOperation, 0);
		add(new Separator(), 1);
		jmOperation.add(jmiAddConstant);
		jmOperation.add(jmiSubtractConstant);
		jmOperation.add(jmiMultiplyConstant);
		jmOperation.add(jmiDivideConstant);
		jmOperation.add(jmiInvertConstant);
		jmOperation.addSeparator();
		jmOperation.add(jmiTwoTracks);
		jmOperation.addSeparator();
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexPerChromosome);
		jmOperation.add(jmiLog);
		jmOperation.add(jmiLogWithDamper);
		jmOperation.add(jmiNormalizeStdScore);
		jmOperation.addSeparator();
		jmOperation.add(jmiMin);
		jmOperation.add(jmiMax);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStandardDeviation);
		jmOperation.add(jmiCountNonNullLength);
		jmOperation.addSeparator();
		jmOperation.add(jmiGenerateBinList);
		jmOperation.addSeparator();
		jmOperation.add(jmiShowRepartition);
				
		add(jmiSave, 9);
	}
}
