/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.old.popupMenu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAAddConstant;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAAverage;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLACountNonNullLength;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLADivideConstant;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAFilter;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAIndex;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAInvertConstant;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLALog;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLALogOnAvgWithDamper;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAMax;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAMin;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLANormalize;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLARepartition;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAStandardDeviation;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLASubtractConstant;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLASumScore;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLATransfrag;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLATwoTracks;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAUniqueScore;
import edu.yu.einstein.genplay.gui.old.action.SCWListTrack.SCWLAWindowCount;
import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASave;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



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
	private final JMenuItem	jmiCountWindows;		// menu count non null length
	private final JMenuItem jmiDivideConstant;		// menu divide constant
	private final JMenuItem jmiFilter;				// menu filter
	private final JMenuItem	jmiIndex;				// menu index
	private final JMenuItem	jmiIndexPerChromosome;	// menu index per chromosome
	private final JMenuItem jmiInvertConstant;		// menu invert constant
	private final JMenuItem	jmiLog;					// menu log
	private final JMenuItem	jmiLogWithDamper;		// menu log with damper
	private final JMenuItem	jmiMax;					// menu max
	private final JMenuItem	jmiMin;					// menu min
	private final JMenuItem jmiMultiplyConstant;	// menu multiply constant
	private final JMenuItem	jmiNormalize;			// menu normalize
	private final JMenuItem	jmiNormalizeStdScore;	// menu normalize standard score
	private final JMenuItem	jmiShowRepartition;		// menu show repartition of the SCWListTrack
	private final JMenuItem	jmiStandardDeviation;	// menu standard deviation
	private final JMenuItem jmiSubtractConstant;	// menu subtract constant
	private final JMenuItem jmiSumScore;			// menu sum score
	private final JMenuItem	jmiTransfrag;			// menu transfrag of the SCWListTrack
	private final JMenuItem jmiTwoTracks;			// menu two tracks operation
	private final JMenuItem jmiUniqueScore;			// menu unique value


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
		jmiCountWindows = new JMenuItem(actionMap.get(SCWLAWindowCount.ACTION_KEY));
		jmiDivideConstant = new JMenuItem(actionMap.get(SCWLADivideConstant.ACTION_KEY));
		jmiFilter  = new JMenuItem(actionMap.get(SCWLAFilter.ACTION_KEY));
		jmiIndex = new JMenuItem(actionMap.get(SCWLAIndex.ACTION_KEY));
		jmiIndexPerChromosome = new JMenuItem(actionMap.get(SCWLAIndexByChromosome.ACTION_KEY));
		jmiInvertConstant = new JMenuItem(actionMap.get(SCWLAInvertConstant.ACTION_KEY));
		jmiLog = new JMenuItem(actionMap.get(SCWLALog.ACTION_KEY));
		jmiLogWithDamper = new JMenuItem(actionMap.get(SCWLALogOnAvgWithDamper.ACTION_KEY));
		jmiMax = new JMenuItem(actionMap.get(SCWLAMax.ACTION_KEY));
		jmiMin = new JMenuItem(actionMap.get(SCWLAMin.ACTION_KEY));
		jmiMultiplyConstant = new JMenuItem(actionMap.get(SCWLAMultiplyConstant.ACTION_KEY));
		jmiNormalize = new JMenuItem(actionMap.get(SCWLANormalize.ACTION_KEY));
		jmiNormalizeStdScore = new JMenuItem(actionMap.get(SCWLANormalizeStandardScore.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(SCWLARepartition.ACTION_KEY));
		jmiStandardDeviation = new JMenuItem(actionMap.get(SCWLAStandardDeviation.ACTION_KEY));
		jmiSubtractConstant = new JMenuItem(actionMap.get(SCWLASubtractConstant.ACTION_KEY));
		jmiSumScore= new JMenuItem(actionMap.get(SCWLASumScore.ACTION_KEY));
		jmiTransfrag = new JMenuItem(actionMap.get(SCWLATransfrag.ACTION_KEY));
		jmiTwoTracks = new JMenuItem(actionMap.get(SCWLATwoTracks.ACTION_KEY));
		jmiUniqueScore = new JMenuItem(actionMap.get(SCWLAUniqueScore.ACTION_KEY));

		add(jmOperation, 0);
		add(new Separator(), 1);
		jmOperation.add(jmiAddConstant);
		jmOperation.add(jmiSubtractConstant);
		jmOperation.add(jmiMultiplyConstant);
		jmOperation.add(jmiDivideConstant);
		jmOperation.add(jmiInvertConstant);
		jmOperation.add(jmiUniqueScore);
		jmOperation.addSeparator();
		jmOperation.add(jmiTwoTracks);
		jmOperation.addSeparator();
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexPerChromosome);
		jmOperation.add(jmiLog);
		jmOperation.add(jmiLogWithDamper);
		jmOperation.add(jmiNormalize);
		jmOperation.add(jmiNormalizeStdScore);
		jmOperation.addSeparator();
		jmOperation.add(jmiMin);
		jmOperation.add(jmiMax);
		jmOperation.add(jmiCountNonNullLength);
		jmOperation.add(jmiSumScore);
		jmOperation.add(jmiAverage);
		jmOperation.add(jmiStandardDeviation);
		jmOperation.add(jmiCountWindows);
		jmOperation.addSeparator();
		jmOperation.add(jmiFilter);
		jmOperation.add(jmiTransfrag);
		jmOperation.addSeparator();
		jmOperation.add(jmiShowRepartition);
		add(jmiSave, 10);
	}
}
