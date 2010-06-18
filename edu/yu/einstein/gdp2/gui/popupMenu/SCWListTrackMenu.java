/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAAverage;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLACountNonNullLength;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAGenerateBinList;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMax;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAMin;
import yu.einstein.gdp2.gui.action.SCWListTrack.SCWLAStandardDeviation;
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

	private final JMenuItem jmiAverage;				// menu average
	private final JMenuItem	jmiCountNonNullLength;	// menu count non null length
	private final JMenuItem jmiGenerateBinList;		// menu generate a BinList track
	private final JMenuItem	jmiMax;					// menu max
	private final JMenuItem	jmiMin;					// menu min
	private final JMenuItem	jmiStandardDeviation;	// menu standard deviation


	/**
	 * Creates an instance of a {@link SCWListTrackMenu}
	 * @param tl {@link TrackList}
	 */
	public SCWListTrackMenu(TrackList tl) {
		super(tl);
		jmOperation = new JMenu("Operation");

		jmiAverage = new JMenuItem(actionMap.get(SCWLAAverage.ACTION_KEY));
		jmiCountNonNullLength  = new JMenuItem(actionMap.get(SCWLACountNonNullLength.ACTION_KEY));
		jmiGenerateBinList = new JMenuItem(actionMap.get(SCWLAGenerateBinList.ACTION_KEY));		
		jmiMax = new JMenuItem(actionMap.get(SCWLAMax.ACTION_KEY));
		jmiMin = new JMenuItem(actionMap.get(SCWLAMin.ACTION_KEY));
		jmiStandardDeviation = new JMenuItem(actionMap.get(SCWLAStandardDeviation.ACTION_KEY));		

		add(jmOperation, 0);
		add(new Separator(), 1);
		jmOperation.add(jmiGenerateBinList);
		jmOperation.addSeparator();
		jmOperation.add(jmiMin);
		jmOperation.add(jmiMax);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStandardDeviation);
		jmOperation.add(jmiCountNonNullLength);
	}
}
