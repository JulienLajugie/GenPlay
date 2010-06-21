/**
 * @author Julien Lajugie
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
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAInvertConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMax;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMin;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMultiplyConstant;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAStandardDeviation;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLASubtractConstant;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * A popup menu for a {@link SCWListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class SCWListTrackMenu extends CurveTrackMenu {

	private static final long serialVersionUID = 3249783097749893180L; // generated ID

	private final JMenu		jmOperation;			// category operation

	private final JMenuItem jmiAddConstant;			// menu add constant
	private final JMenuItem jmiAverage;				// menu average
	private final JMenuItem	jmiCountNonNullLength;	// menu count non null length
	private final JMenuItem jmiDivideConstant;		// menu divide constant
	private final JMenuItem jmiGenerateBinList;		// menu generate a BinList track
	private final JMenuItem jmiInvertConstant;		// menu invert constant
	private final JMenuItem	jmiMax;					// menu max
	private final JMenuItem	jmiMin;					// menu min
	private final JMenuItem jmiMultiplyConstant;	// menu multiply constant
	private final JMenuItem	jmiStandardDeviation;	// menu standard deviation
	private final JMenuItem jmiSubtractConstant;	// menu subtract constant
	

	/**
	 * Creates an instance of a {@link SCWListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SCWListTrackMenu(TrackList tl) {
		super(tl);
		jmOperation = new JMenu("Operation");

		jmiAddConstant = new JMenuItem(actionMap.get(SCWLAAddConstant.ACTION_KEY));
		jmiAverage = new JMenuItem(actionMap.get(SCWLAAverage.ACTION_KEY));
		jmiCountNonNullLength = new JMenuItem(actionMap.get(SCWLACountNonNullLength.ACTION_KEY));
		jmiDivideConstant = new JMenuItem(actionMap.get(SCWLADivideConstant.ACTION_KEY));
		jmiGenerateBinList = new JMenuItem(actionMap.get(SCWLAGenerateBinList.ACTION_KEY));		
		jmiInvertConstant = new JMenuItem(actionMap.get(SCWLAInvertConstant.ACTION_KEY));
		jmiMax = new JMenuItem(actionMap.get(SCWLAMax.ACTION_KEY));
		jmiMin = new JMenuItem(actionMap.get(SCWLAMin.ACTION_KEY));
		jmiMultiplyConstant = new JMenuItem(actionMap.get(SCWLAMultiplyConstant.ACTION_KEY));
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
		jmOperation.add(jmiMin);
		jmOperation.add(jmiMax);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStandardDeviation);
		jmOperation.add(jmiCountNonNullLength);
		jmOperation.addSeparator();
		jmOperation.add(jmiGenerateBinList);
	}
}
