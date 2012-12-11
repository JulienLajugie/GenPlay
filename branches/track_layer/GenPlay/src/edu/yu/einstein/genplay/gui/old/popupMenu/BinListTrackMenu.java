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

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.PopupMenuEvent;

import edu.yu.einstein.genplay.gui.old.action.allTrack.ATASave;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAAddConstant;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAAverage;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAChangeBinSize;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAChangeDataPrecision;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLACompress;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAConcatenate;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLACorrelate;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLACountNonNullBins;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLADensity;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLADivideConstant;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAFilter;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAFindPeaks;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAGauss;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAIndex;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAIntervalsScoring;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAInvertConstant;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLALoessRegression;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLALog;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLALogOnAvgWithDamper;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAMax;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAMin;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAMovingAverage;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLANormalize;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLARepartition;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAStandardDeviation;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLASubtractConstant;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLASumScore;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLATransfrag;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLATwoTracks;
import edu.yu.einstein.genplay.gui.old.action.binListTrack.BLAUniqueScore;
import edu.yu.einstein.genplay.gui.old.track.BinListTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



/**
 * A popup menu for a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackMenu extends CurveTrackMenu {

	private static final long serialVersionUID = -1453741322870299413L; // generated ID

	private final JMenu			jmOperation;					// category operation

	private final JMenuItem		jmiSave;						// menu save BinListTrack

	private final JCheckBoxMenuItem jcbmiCompression;			// check box menu compression

	private final JMenuItem		jmiAddConstant;					// menu add constant to BinListTrack
	private final JMenuItem		jmiSubtractConstant;			// menu subtract constant from BinListTrack
	private final JMenuItem		jmiMultiplyConstant;			// menu multiply BinListTrack by constant
	private final JMenuItem		jmiDivideConstant;				// menu divide BinListTrack by constant
	private final JMenuItem		jmiInvertConstant;				// menu invert BinListTrack
	private final JMenuItem		jmiUniqueScore;					// menu unique value

	private final JMenuItem		jmiTwoTracks;					// menu operates BinListTrack by another one

	private final JMenuItem		jmiMovingAverage;				// menu moving average on BinListTrack
	private final JMenuItem		jmiGauss;						// menu gauss BinListTrack
	private final JMenuItem		jmiLoessRegression;				// menu Loess regression
	private final JMenuItem		jmiIndex;						// menu index BinListTrack
	private final JMenuItem		jmiIndexPerChromosome;			// menu index BinListTrack per chromosome
	private final JMenuItem		jmiLog;							// menu log BinListTrack
	private final JMenuItem		jmiLogWithDamper;				// menu log BinListTrack with damper
	private final JMenuItem		jmiNormalize;					// menu normalize BinListTrack
	private final JMenuItem		jmiNormalizeStdScore;			// menu normalize standard score BinListTrack

	private final JMenuItem		jmiMinimum;						// menu minimum of the BinListTrack
	private final JMenuItem		jmiMaximum;						// menu maximum of the BinListTrack
	private final JMenuItem		jmiBinCount;					// menu bin count
	private final JMenuItem		jmiScoreCount;					// menu score count
	private final JMenuItem		jmiAverage;						// menu average
	private final JMenuItem		jmiStdDev;						// menu standard deviation
	private final JMenuItem		jmiCorrelation;					// menu correlation BinListTrack with another one

	private final JMenuItem		jmiFilter;						// menu filter
	private final JMenuItem		jmiSearchPeaks;					// menu search peaks
	private final JMenuItem		jmiTransfrag;					// menu transfrag for BinLists

	private final JMenuItem		jmiChangeBinSize;				// menu change bin size
	private final JMenuItem		jmiChangePrecision;				// menu change data precision

	private final JMenuItem		jmiDensity;						// menu density of none null windows
	private final JMenuItem		jmiIntervalsSummarization;		// menu Intervals Summarization
	private final JMenuItem		jmiShowRepartition;				// menu show repartition of the BinListTrack
	private final JMenuItem		jmiConcatenate;					// menu concatenate


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
		jmiUniqueScore = new JMenuItem(actionMap.get(BLAUniqueScore.ACTION_KEY));

		jmiTwoTracks = new JMenuItem(actionMap.get(BLATwoTracks.ACTION_KEY));

		jmiMovingAverage = new JMenuItem(actionMap.get(BLAMovingAverage.ACTION_KEY));
		jmiGauss = new JMenuItem(actionMap.get(BLAGauss.ACTION_KEY));
		jmiLoessRegression = new JMenuItem(actionMap.get(BLALoessRegression.ACTION_KEY));

		jmiIndex = new JMenuItem(actionMap.get(BLAIndex.ACTION_KEY));
		jmiIndexPerChromosome = new JMenuItem(actionMap.get(BLAIndexByChromosome.ACTION_KEY));
		jmiLog = new JMenuItem(actionMap.get(BLALog.ACTION_KEY));
		jmiLogWithDamper = new JMenuItem(actionMap.get(BLALogOnAvgWithDamper.ACTION_KEY));
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
		jmiSearchPeaks = new JMenuItem(actionMap.get(BLAFindPeaks.ACTION_KEY));
		jmiTransfrag =  new JMenuItem(actionMap.get(BLATransfrag.ACTION_KEY));

		jmiChangeBinSize = new JMenuItem(actionMap.get(BLAChangeBinSize.ACTION_KEY));
		jmiChangePrecision = new JMenuItem(actionMap.get(BLAChangeDataPrecision.ACTION_KEY));

		jmiDensity = new JMenuItem(actionMap.get(BLADensity.ACTION_KEY));
		jmiIntervalsSummarization = new JMenuItem(actionMap.get(BLAIntervalsScoring.ACTION_KEY));
		jmiShowRepartition = new JMenuItem(actionMap.get(BLARepartition.ACTION_KEY));
		jmiConcatenate = new JMenuItem(actionMap.get(BLAConcatenate.ACTION_KEY));

		addSeparator();
		add(jcbmiCompression);

		jmOperation.add(jmiAddConstant);
		jmOperation.add(jmiSubtractConstant);
		jmOperation.add(jmiMultiplyConstant);
		jmOperation.add(jmiDivideConstant);
		jmOperation.add(jmiInvertConstant);
		jmOperation.add(jmiUniqueScore);
		jmOperation.addSeparator();
		jmOperation.add(jmiTwoTracks);
		jmOperation.addSeparator();
		jmOperation.add(jmiMovingAverage);
		jmOperation.add(jmiGauss);
		jmOperation.add(jmiLoessRegression);
		jmOperation.addSeparator();
		jmOperation.add(jmiIndex);
		jmOperation.add(jmiIndexPerChromosome);
		jmOperation.add(jmiLog);
		jmOperation.add(jmiLogWithDamper);
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
		jmOperation.add(jmiSearchPeaks);
		jmOperation.add(jmiTransfrag);
		//jmOperation.add(jmiTransfragGeneList);
		jmOperation.addSeparator();
		jmOperation.add(jmiChangeBinSize);
		jmOperation.add(jmiChangePrecision);
		jmOperation.addSeparator();
		jmOperation.add(jmiDensity);
		jmOperation.add(jmiShowRepartition);
		jmOperation.add(jmiConcatenate);
		jmOperation.add(jmiIntervalsSummarization);

		add(jmOperation, 0);
		add(new Separator(), 1);

		add(jmiSave, 10);
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
