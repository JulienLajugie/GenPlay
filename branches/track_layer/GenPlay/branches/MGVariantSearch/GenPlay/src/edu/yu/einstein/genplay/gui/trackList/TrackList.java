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
package edu.yu.einstein.genplay.gui.trackList;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.filter.MGFilter;
import edu.yu.einstein.genplay.core.multiGenome.filter.VCFFilter;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAAddConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAAverage;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLACountNonNullLength;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAWindowCount;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLADivideConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAFilter;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAIndex;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAInvertConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLALog;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLALogOnAvgWithDamper;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAMax;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAMin;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLANormalize;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLARepartition;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAStandardDeviation;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLASubtractConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLASumScore;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLATransfrag;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLATwoTracks;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAUniqueScore;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFilterRatio;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFilterThreshold;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFindNext;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFindPrevious;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLARemoveSNPsNotInGenes;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAConvert;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACopy;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACut;
import edu.yu.einstein.genplay.gui.action.allTrack.ATADelete;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAInsert;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAPaste;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAPasteSpecial;
import edu.yu.einstein.genplay.gui.action.allTrack.ATARename;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASave;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASaveAsImage;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASetHeight;
import edu.yu.einstein.genplay.gui.action.allTrack.ATASetVerticalLineCount;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAAddConstant;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAAverage;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAChangeBinSize;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAChangeDataPrecision;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLACompress;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAConcatenate;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLACorrelate;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLACountNonNullBins;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLADensity;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLADivideConstant;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAFilter;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAFindPeaks;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAGauss;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIndex;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIntervalsScoring;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAInvertConstant;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLALoessRegression;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLALog;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLALogOnAvgWithDamper;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAMax;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAMin;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAMovingAverage;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAMultiplyConstant;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLANormalize;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLANormalizeStandardScore;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLARepartition;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAStandardDeviation;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLASubtractConstant;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLASumScore;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLATransfrag;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLATwoTracks;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAUniqueScore;
import edu.yu.einstein.genplay.gui.action.curveTrack.CTAAppearance;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETAGenerateMultiCurvesTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadBinListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadFromDAS;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadGeneListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadNucleotideListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadRepeatFamilyListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadSCWListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadSNPListTrack;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAAverageScore;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLACountAllGenes;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLACountNonNullGenes;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLADistanceCalculator;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAExtractExons;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAExtractInterval;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAFilter;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAFilterStrand;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAGeneRenamer;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAScoreExons;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAScoreRepartitionAroundStart;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLASearchGene;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLASumScore;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAUniqueScore;
import edu.yu.einstein.genplay.gui.action.maskTrack.MTAApplyMask;
import edu.yu.einstein.genplay.gui.action.maskTrack.MTAInvertMask;
import edu.yu.einstein.genplay.gui.action.maskTrack.MTALoadMask;
import edu.yu.einstein.genplay.gui.action.maskTrack.MTARemoveMask;
import edu.yu.einstein.genplay.gui.action.maskTrack.MTASaveMask;
import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGAVCFStatistics;
import edu.yu.einstein.genplay.gui.action.multiGenome.convert.MGASCWLConvert;
import edu.yu.einstein.genplay.gui.action.multiGenome.export.MGAGlobalVCFExport;
import edu.yu.einstein.genplay.gui.action.multiGenome.update.MGAVCFApplyGenotype;
import edu.yu.einstein.genplay.gui.action.scoredTrack.STASetYAxis;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAHistory;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTARedo;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAReset;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAUndo;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.variants.VariantData;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenu;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenuFactory;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.VersionedTrack;
import edu.yu.einstein.genplay.gui.track.drawer.multiGenome.MultiGenomeDrawer;
import edu.yu.einstein.genplay.gui.track.pasteSettings.PasteSettings;



/**
 * A scroll panel containing many tracks.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackList extends JScrollPane implements Serializable, TrackListener, TrackEventsGenerator {

	private static final long serialVersionUID = 7304431979443474040L; 	// generated ID
	private final JPanel 				jpTrackList;					// panel with the tracks
	private final ProjectConfiguration 	projectConfiguration;			// ConfigurationManager
	private final List<TrackListener> 	trackListeners;					// list of track listeners
	private Track<?>[] 					trackList;						// array of tracks
	private Track<?>					selectedTrack = null;			// track selected
	private Track<?>					copiedTrack = null; 			// list of the tracks in the clipboard
	private int 						draggedTrackIndex = -1;			// index of the dragged track, -1 if none
	private int 						draggedOverTrackIndex = -1; 	// index of the track rolled over by the dragged track, -1 if none


	/**
	 * Creates an instance of {@link TrackList}
	 */
	public TrackList() {
		super();
		this.trackListeners = new ArrayList<TrackListener>();
		this.projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
		getVerticalScrollBar().setUnitIncrement(15);
		getVerticalScrollBar().setBlockIncrement(40);
		jpTrackList = new JPanel();
		jpTrackList.setLayout(new BoxLayout(jpTrackList, BoxLayout.PAGE_AXIS));
		int trackCount = projectConfiguration.getTrackCount();
		int preferredHeight = projectConfiguration.getTrackHeight();
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			trackList[i] = new EmptyTrack(i + 1);
			trackList[i].setPreferredHeight(preferredHeight);
			trackList[i].addTrackListener(this);
			jpTrackList.add(trackList[i]);
		}
		for (int i = 0; i < trackCount; i++) {
			jpTrackList.add(trackList[i]);
		}
		setViewportView(jpTrackList);
		addActionsToActionMap();
		addKeyToInputMap();
	}


	/**
	 * Resets the {@link TrackList} by filling it with empty tracks
	 */
	public void resetTrackList() {
		int trackCount = projectConfiguration.getTrackCount();
		int preferredHeight = projectConfiguration.getTrackHeight();
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			trackList[i] = new EmptyTrack(i + 1);
			trackList[i].setPreferredHeight(preferredHeight);
			trackList[i].addTrackListener(this);
		}
		System.gc();
		rebuildPanel();
	}


	/**
	 * Adds the action to the {@link ActionMap}
	 */
	private void addActionsToActionMap() {
		// add general actions
		getActionMap().put(ATAConvert.ACTION_KEY, new ATAConvert());
		getActionMap().put(ATACopy.ACTION_KEY, new ATACopy());
		getActionMap().put(ATACut.ACTION_KEY, new ATACut());
		getActionMap().put(ATADelete.ACTION_KEY, new ATADelete());
		getActionMap().put(ATAInsert.ACTION_KEY, new ATAInsert());
		getActionMap().put(MTALoadMask.ACTION_KEY, new MTALoadMask());
		getActionMap().put(MTASaveMask.ACTION_KEY, new MTASaveMask());
		getActionMap().put(ATAPaste.ACTION_KEY, new ATAPaste());
		getActionMap().put(ATAPasteSpecial.ACTION_KEY, new ATAPasteSpecial());
		getActionMap().put(MTARemoveMask.ACTION_KEY, new MTARemoveMask());
		getActionMap().put(MTAInvertMask.ACTION_KEY, new MTAInvertMask());
		getActionMap().put(MTAApplyMask.ACTION_KEY, new MTAApplyMask());
		getActionMap().put(ATARename.ACTION_KEY, new ATARename());
		getActionMap().put(ATASave.ACTION_KEY, new ATASave());
		getActionMap().put(ATASaveAsImage.ACTION_KEY, new ATASaveAsImage());
		getActionMap().put(ATASetHeight.ACTION_KEY, new ATASetHeight());
		getActionMap().put(ATASetVerticalLineCount.ACTION_KEY, new ATASetVerticalLineCount());
		// add empty list actions
		getActionMap().put(ETALoadBinListTrack.ACTION_KEY, new ETALoadBinListTrack());
		getActionMap().put(ETALoadFromDAS.ACTION_KEY, new ETALoadFromDAS());
		getActionMap().put(ETALoadGeneListTrack.ACTION_KEY, new ETALoadGeneListTrack());
		getActionMap().put(ETAGenerateMultiCurvesTrack.ACTION_KEY, new ETAGenerateMultiCurvesTrack());
		getActionMap().put(ETALoadNucleotideListTrack.ACTION_KEY, new ETALoadNucleotideListTrack());
		getActionMap().put(ETALoadRepeatFamilyListTrack.ACTION_KEY, new ETALoadRepeatFamilyListTrack());
		getActionMap().put(ETALoadSCWListTrack.ACTION_KEY, new ETALoadSCWListTrack());
		getActionMap().put(ETALoadSNPListTrack.ACTION_KEY, new ETALoadSNPListTrack());
		// add gene list actions
		getActionMap().put(GLAAverageScore.ACTION_KEY, new GLAAverageScore());
		getActionMap().put(GLACountNonNullGenes.ACTION_KEY, new GLACountNonNullGenes());
		getActionMap().put(GLACountAllGenes.ACTION_KEY, new GLACountAllGenes());
		getActionMap().put(GLADistanceCalculator.ACTION_KEY, new GLADistanceCalculator());
		getActionMap().put(GLAExtractExons.ACTION_KEY, new GLAExtractExons());
		getActionMap().put(GLAExtractInterval.ACTION_KEY, new GLAExtractInterval());
		getActionMap().put(GLAFilter.ACTION_KEY, new GLAFilter());
		getActionMap().put(GLAFilterStrand.ACTION_KEY, new GLAFilterStrand());
		getActionMap().put(GLAGeneRenamer.ACTION_KEY, new GLAGeneRenamer());
		getActionMap().put(GLAScoreExons.ACTION_KEY, new GLAScoreExons());
		getActionMap().put(GLAScoreRepartitionAroundStart.ACTION_KEY, new GLAScoreRepartitionAroundStart());
		getActionMap().put(GLASearchGene.ACTION_KEY, new GLASearchGene());
		getActionMap().put(GLASumScore.ACTION_KEY, new GLASumScore());
		getActionMap().put(GLAUniqueScore.ACTION_KEY, new GLAUniqueScore());
		// add curve track actions
		getActionMap().put(CTAAppearance.ACTION_KEY, new CTAAppearance());
		getActionMap().put(VTAHistory.ACTION_KEY, new VTAHistory());
		getActionMap().put(VTARedo.ACTION_KEY, new VTARedo());
		getActionMap().put(VTAReset.ACTION_KEY, new VTAReset());
		getActionMap().put(VTAUndo.ACTION_KEY, new VTAUndo());
		// add scored track actions
		getActionMap().put(STASetYAxis.ACTION_KEY, new STASetYAxis());
		// add SCWList actions
		getActionMap().put(SCWLAAddConstant.ACTION_KEY, new SCWLAAddConstant());
		getActionMap().put(SCWLAAverage.ACTION_KEY, new SCWLAAverage());
		getActionMap().put(SCWLACountNonNullLength.ACTION_KEY, new SCWLACountNonNullLength());
		getActionMap().put(SCWLAWindowCount.ACTION_KEY, new SCWLAWindowCount());
		getActionMap().put(SCWLADivideConstant.ACTION_KEY, new SCWLADivideConstant());
		getActionMap().put(SCWLAFilter.ACTION_KEY, new SCWLAFilter());
		getActionMap().put(SCWLAIndex.ACTION_KEY, new SCWLAIndex());
		getActionMap().put(SCWLAIndexByChromosome.ACTION_KEY, new SCWLAIndexByChromosome());
		getActionMap().put(SCWLAInvertConstant.ACTION_KEY, new SCWLAInvertConstant());
		getActionMap().put(SCWLALog.ACTION_KEY, new SCWLALog());
		getActionMap().put(SCWLALogOnAvgWithDamper.ACTION_KEY, new SCWLALogOnAvgWithDamper());
		getActionMap().put(SCWLAMax.ACTION_KEY, new SCWLAMax());
		getActionMap().put(SCWLAMin.ACTION_KEY, new SCWLAMin());
		getActionMap().put(SCWLAMultiplyConstant.ACTION_KEY, new SCWLAMultiplyConstant());
		getActionMap().put(SCWLANormalize.ACTION_KEY, new SCWLANormalize());
		getActionMap().put(SCWLANormalizeStandardScore.ACTION_KEY, new SCWLANormalizeStandardScore());
		getActionMap().put(SCWLARepartition.ACTION_KEY, new SCWLARepartition());
		getActionMap().put(SCWLAStandardDeviation.ACTION_KEY, new SCWLAStandardDeviation());
		getActionMap().put(SCWLASubtractConstant.ACTION_KEY, new SCWLASubtractConstant());
		getActionMap().put(SCWLASumScore.ACTION_KEY, new SCWLASumScore());
		getActionMap().put(SCWLATransfrag.ACTION_KEY, new SCWLATransfrag());
		getActionMap().put(SCWLATwoTracks.ACTION_KEY, new SCWLATwoTracks());
		getActionMap().put(SCWLAUniqueScore.ACTION_KEY, new SCWLAUniqueScore());
		// add binlist actions
		getActionMap().put(BLAAddConstant.ACTION_KEY, new BLAAddConstant());
		getActionMap().put(BLAAverage.ACTION_KEY, new BLAAverage());
		getActionMap().put(BLACountNonNullBins.ACTION_KEY, new BLACountNonNullBins());
		getActionMap().put(BLAIntervalsScoring.ACTION_KEY, new BLAIntervalsScoring());
		getActionMap().put(BLAChangeBinSize.ACTION_KEY, new BLAChangeBinSize());
		getActionMap().put(BLAChangeDataPrecision.ACTION_KEY, new BLAChangeDataPrecision());
		getActionMap().put(BLACompress.ACTION_KEY, new BLACompress());
		getActionMap().put(BLAConcatenate.ACTION_KEY, new BLAConcatenate());
		getActionMap().put(BLACorrelate.ACTION_KEY, new BLACorrelate());
		getActionMap().put(BLADensity.ACTION_KEY, new BLADensity());
		getActionMap().put(BLADivideConstant.ACTION_KEY, new BLADivideConstant());
		getActionMap().put(BLAFilter.ACTION_KEY, new BLAFilter());
		getActionMap().put(BLAGauss.ACTION_KEY, new BLAGauss());
		getActionMap().put(BLAIndex.ACTION_KEY, new BLAIndex());
		getActionMap().put(BLAIndexByChromosome.ACTION_KEY, new BLAIndexByChromosome());
		getActionMap().put(BLAInvertConstant.ACTION_KEY, new BLAInvertConstant());
		getActionMap().put(BLALoessRegression.ACTION_KEY, new BLALoessRegression());
		getActionMap().put(BLALog.ACTION_KEY, new BLALog());
		getActionMap().put(BLALogOnAvgWithDamper.ACTION_KEY, new BLALogOnAvgWithDamper());
		getActionMap().put(BLAMax.ACTION_KEY, new BLAMax());
		getActionMap().put(BLAMin.ACTION_KEY, new BLAMin());
		getActionMap().put(BLAMultiplyConstant.ACTION_KEY, new BLAMultiplyConstant());
		getActionMap().put(BLAMovingAverage.ACTION_KEY, new BLAMovingAverage());
		getActionMap().put(BLANormalize.ACTION_KEY, new BLANormalize());
		getActionMap().put(BLANormalizeStandardScore.ACTION_KEY, new BLANormalizeStandardScore());
		getActionMap().put(BLASumScore.ACTION_KEY, new BLASumScore());
		getActionMap().put(BLAFindPeaks.ACTION_KEY, new BLAFindPeaks());
		getActionMap().put(BLARepartition.ACTION_KEY, new BLARepartition());
		getActionMap().put(BLAStandardDeviation.ACTION_KEY, new BLAStandardDeviation());
		getActionMap().put(BLASubtractConstant.ACTION_KEY, new BLASubtractConstant());
		getActionMap().put(BLATransfrag.ACTION_KEY, new BLATransfrag());
		getActionMap().put(BLATwoTracks.ACTION_KEY, new BLATwoTracks());
		getActionMap().put(BLAUniqueScore.ACTION_KEY, new BLAUniqueScore());
		// SNP tracks
		getActionMap().put(SLAFilterRatio.ACTION_KEY, new SLAFilterRatio());
		getActionMap().put(SLAFilterThreshold.ACTION_KEY, new SLAFilterThreshold());
		getActionMap().put(SLAFindNext.ACTION_KEY, new SLAFindNext());
		getActionMap().put(SLAFindPrevious.ACTION_KEY, new SLAFindPrevious());
		getActionMap().put(SLARemoveSNPsNotInGenes.ACTION_KEY, new SLARemoveSNPsNotInGenes());

		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			getActionMap().put(MGAVCFStatistics.ACTION_KEY, new MGAVCFStatistics());
			getActionMap().put(MGAGlobalVCFExport.ACTION_KEY, new MGAGlobalVCFExport());
			//getActionMap().put(MGABedExport.ACTION_KEY, new MGABedExport());
			getActionMap().put(MGASCWLConvert.ACTION_KEY, new MGASCWLConvert());
			getActionMap().put(MGAVCFApplyGenotype.ACTION_KEY, new MGAVCFApplyGenotype());
		}
	}


	/**
	 * Adds key listener to the {@link InputMap}
	 */
	private void addKeyToInputMap() {
		// all tracks
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATACopy.ACCELERATOR, ATACopy.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATACut.ACCELERATOR, ATACut.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATADelete.ACCELERATOR, ATADelete.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATAInsert.ACCELERATOR, ATAInsert.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATAPaste.ACCELERATOR, ATAPaste.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATAPasteSpecial.ACCELERATOR, ATAPasteSpecial.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATARename.ACCELERATOR, ATARename.ACTION_KEY);
		// curve tracks
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(VTAHistory.ACCELERATOR, VTAHistory.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(VTARedo.ACCELERATOR, VTARedo.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(VTAReset.ACCELERATOR, VTAReset.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(VTAUndo.ACCELERATOR, VTAUndo.ACTION_KEY);
		// gene tracks
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(GLASearchGene.ACCELERATOR, GLASearchGene.ACTION_KEY);
		// SNP tracks
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(SLAFindNext.ACCELERATOR, SLAFindNext.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(SLAFindPrevious.ACCELERATOR, SLAFindPrevious.ACTION_KEY);
	}


	@Override
	public void trackChanged(TrackEvent evt) {
		notifyTrackListeners(evt);
		if (evt.getEventType() == TrackEventType.RIGHT_CLICKED) {
			setScrollMode(false);
			selectedTrack = (Track<?>)evt.getSource();
			TrackMenu tm = TrackMenuFactory.getTrackMenu(this);
			Point mousePoint = getMousePosition();
			if (mousePoint != null) {
				tm.show(this, mousePoint.x, mousePoint.y);
			}
		} else if (evt.getEventType() == TrackEventType.DRAGGED) {
			if (draggedTrackIndex == -1) {
				draggedTrackIndex = ((Track<?>)evt.getSource()).getTrackNumber() - 1;
			}
			dragTrack();
		} else if (evt.getEventType() == TrackEventType.RELEASED) {
			releaseTrack();
		} else if (evt.getEventType() == TrackEventType.SCROLL_MODE_TURNED_ON) {
			setScrollMode(true);
		} else if (evt.getEventType() == TrackEventType.SCROLL_MODE_TURNED_OFF) {
			setScrollMode(false);
		} else if (evt.getEventType() == TrackEventType.SELECTED) {
			for (Track<?> currentTrack : trackList) {
				if (currentTrack != evt.getSource()) {
					currentTrack.setSelected(false);
				}
			}
			selectedTrack = (Track<?>)evt.getSource();
		} else if (evt.getEventType() == TrackEventType.UNSELECTED) {
			selectedTrack = null;
		}
	}


	/**
	 * Sets a specified {@link Track} at the specified index of the list of tracks
	 * @param index index index where to set
	 * @param track {@link Track} to set
	 * @param preferredHeight preferred height of the track
	 * @param name name of the track (can be null)
	 * @param mask {@link ChromosomeWindowList} (can be null)
	 * @param stripesList {@link VariantData} (can be null)
	 * @param filtersList {@link VCFFilter} (can be null)
	 */
	public void setTrack(int index, Track<?> track, int preferredHeight, String name, ScoredChromosomeWindowList mask, List<VariantData> stripesList, List<MGFilter> filtersList) {
		track.setPreferredHeight(preferredHeight);
		track.setName(name);
		track.setMask(mask);
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			MGDisplaySettings.getInstance().replaceTrack(trackList[index], track);
			track.updateMultiGenomeInformation(stripesList, filtersList);
		}

		trackList[index] = track;
		trackList[index].setTrackNumber(index + 1);
		trackList[index].addTrackListener(this);

		trackList[index].registerToEventGenerators();

		jpTrackList.remove(index);
		jpTrackList.add(trackList[index], index);
		jpTrackList.revalidate();
		setViewportView(jpTrackList);
	}


	/**
	 * Switches the scroll mode on / off
	 * @param b
	 */
	public void setScrollMode(boolean b) {
		for (Track<?> currentTrack: trackList) {
			currentTrack.setScrollMode(b);
		}
	}


	/**
	 * Called when a track is dragged in the list
	 */
	public void dragTrack() {
		lockTrackHandles();
		for (int i = 0; i < trackList.length; i++) {
			if (getMousePosition() != null) {
				int screenTrackTop = trackList[i].getY() - getVerticalScrollBar().getValue();
				int screenTrackBottom = (trackList[i].getY() + trackList[i].getHeight()) - getVerticalScrollBar().getValue();
				if ((getMousePosition().y > screenTrackTop) && ( getMousePosition().y < screenTrackBottom)
						&& (draggedOverTrackIndex != i)) {
					if (draggedOverTrackIndex != -1) {
						trackList[draggedOverTrackIndex].setBorder(Track.REGULAR_BORDER);
					}
					draggedOverTrackIndex = i;
					if (draggedOverTrackIndex == draggedTrackIndex) {
						trackList[draggedOverTrackIndex].setBorder(Track.DRAG_START_BORDER);
					} else if (draggedOverTrackIndex < draggedTrackIndex) {
						trackList[draggedOverTrackIndex].setBorder(Track.DRAG_UP_BORDER);
					} else {
						trackList[draggedOverTrackIndex].setBorder(Track.DRAG_DOWN_BORDER);
					}
				}
			}
		}
	}


	/**
	 * Called when a track is released after being dragged in the list
	 */
	public void releaseTrack() {
		if ((draggedTrackIndex != -1) && (draggedOverTrackIndex != -1)) {
			trackList[draggedOverTrackIndex].setBorder(Track.REGULAR_BORDER);
			if (getMousePosition() != null) {
				Track<?> trackTmp = trackList[draggedTrackIndex];
				if (draggedTrackIndex < draggedOverTrackIndex) {
					for (int i = draggedTrackIndex; i < draggedOverTrackIndex; i++) {
						trackList[i] = trackList[i + 1];

					}
				} else if (draggedTrackIndex > draggedOverTrackIndex) {
					for (int i = draggedTrackIndex - 1; i >= draggedOverTrackIndex; i--) {
						trackList[i + 1] = trackList[i];
					}
				}
				trackList[draggedOverTrackIndex] = trackTmp;
			}
		}
		rebuildPanel();
		draggedTrackIndex = -1;
		draggedOverTrackIndex = -1;
		unlockTracksHandles();
	}


	/**
	 * Rebuilds the list based on the data of the array of track
	 */
	public void rebuildPanel() {
		jpTrackList.removeAll();
		for(int i = 0; i < trackList.length; i++) {
			trackList[i].setTrackNumber(i + 1);
			jpTrackList.add(trackList[i]);
			if (trackList[i].getTrackListeners().length == 0) {
				trackList[i].addTrackListener(this);
			}
		}
		jpTrackList.revalidate();
		setViewportView(jpTrackList);
	}


	/**
	 * Sets the height of each {@link Track} according to the value specified in the {@link ProjectConfiguration}
	 */
	public void trackHeightChanged() {
		int preferredHeight = projectConfiguration.getTrackHeight();
		for(int i = 0; i < trackList.length; i++) {
			trackList[i].setPreferredHeight(preferredHeight);
			((Track<?>)jpTrackList.getComponent(i)).setPreferredHeight(preferredHeight);
		}
		revalidate();
	}


	/**
	 * Changes the number of {@link Track} in the {@link TrackList} according to
	 * the value specified in the {@link ProjectConfiguration}
	 */
	public void trackCountChanged() {
		int trackCount = projectConfiguration.getTrackCount();
		int preferredHeight = projectConfiguration.getTrackHeight();
		Track<?>[] trackTmp = trackList;
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			if (i < trackTmp.length) {
				trackList[i] = trackTmp[i];
			} else {
				trackList[i] = new EmptyTrack(i + 1);
				trackList[i].setPreferredHeight(preferredHeight);
			}
		}
		rebuildPanel();
	}


	/**
	 * @return the index of the track where the mouse is on top during a right click. -1 if none
	 */
	public int getSelectedTrackIndex() {
		for (int i = 0; i < trackList.length; i++) {
			if (trackList[i] == selectedTrack) {
				return i;
			}
		}
		return -1;
	}


	/**
	 * @return the selected track
	 */
	public Track<?> getSelectedTrack() {
		return selectedTrack;
	}


	/**
	 * Checks if a track is compressed or not.
	 * Only {@link BinListTrack} can be compressed.
	 * @param track a track
	 * @return		true if the track is compressed, false otherwise.
	 */
	private boolean isCompressedTrack (Track<?> track) {
		if (track instanceof BinListTrack) {
			if (((BinListTrack) track).getData().isCompressed()) {
				return true;
			}
		}
		return false;
	}


	/**
	 * @return an array containing all the {@link EmptyTrack}
	 */
	public Track<?>[] getEmptyTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof EmptyTrack) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof EmptyTrack) {
				result[i] = currentTrack;
				i++;
			}
		}
		return result;
	}


	/**
	 * @return an array containing all the {@link VersionedTrack}
	 */
	public Track<?>[] getVersionnedTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof VersionedTrack) {
				if (!isCompressedTrack(currentTrack)) {
					count++;
				}
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof VersionedTrack) {
				if (!isCompressedTrack(currentTrack)) {
					result[i] = currentTrack;
					i++;
				}
			}
		}
		return result;
	}


	/**
	 * @return an array containing all the {@link BinListTrack}
	 */
	public Track<?>[] getBinListTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof BinListTrack) {
				if (!((BinListTrack) currentTrack).getData().isCompressed()) {
					count++;
				}
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof BinListTrack) {
				if (!isCompressedTrack(currentTrack)) {
					result[i] = currentTrack;
					i++;
				}
			}
		}
		return result;
	}


	/**
	 * @author Chirag Gorasia
	 * @return an array containing all the {@link SCWListTrack}
	 */
	public Track<?>[] getSCWListTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof SCWListTrack) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof SCWListTrack) {
				result[i] = currentTrack;
				i++;
			}
		}
		return result;
	}


	/**
	 * @author Chirag Gorasia
	 * @return an array containing all the {@link GeneListTrack}
	 */
	public Track<?>[] getGeneListTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof GeneListTrack) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof GeneListTrack) {
				result[i] = currentTrack;
				i++;
			}
		}
		return result;
	}


	/**
	 * @return an array containing all the {@link CurveTrack}
	 */
	public Track<?>[] getCurveTracks() {
		int count = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof CurveTrack<?>) {
				if (!isCompressedTrack(currentTrack)) {
					count++;
				}
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof CurveTrack<?>) {
				if (!isCompressedTrack(currentTrack)) {
					result[i] = currentTrack;
					i++;
				}
			}
		}
		return result;
	}


	/**
	 * Copies the selected track.
	 */
	public void copyTrack() {
		if (selectedTrack != null) {
			try {
				//selectedTrack.disableStripeListSerialization();
				copiedTrack = selectedTrack.deepClone();
				selectedTrack.copyMultiGenomeInformation();
				//selectedTrack.enableStripeListSerialization();
				// we need to clone the selected track because the user may copy the
				// then modify the track and finally paste the track.  If we don't do the deep clone
				// all the modification made after the cloning will be copied (and we don't want that)
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while copying the track");
			}
		}
	}


	/**
	 * Cuts the selected track.
	 */
	public void cutTrack() {
		if (selectedTrack != null) {
			try {
				copiedTrack = selectedTrack;
				copiedTrack.copyMultiGenomeInformation();
				int selectedTrackIndex = getSelectedTrackIndex();
				Track<?> emptyTrack = new EmptyTrack(trackList.length);
				setTrack(selectedTrackIndex, emptyTrack, projectConfiguration.getTrackHeight(), null, null, null, null);
				selectedTrack = null;
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while cutting the track");
			}
		}
	}


	/**
	 * @return true if there is a track to paste
	 */
	public boolean isPasteEnable() {
		return (copiedTrack != null);
	}


	/**
	 * @return true if there is a mask to remove
	 */
	public boolean isMaskRemovable() {
		return (getSelectedTrack().getMask() != null);
	}


	/**
	 * @return true if the mask can be used, false otherwise
	 */
	public boolean isMaskApplicable () {
		if ((getSelectedTrack().getData() != null) &&
				(getSelectedTrack().getMask() != null)) {
			if ((getSelectedTrack().getData() instanceof BinList) ||
					(getSelectedTrack().getData() instanceof ScoredChromosomeWindowList) ) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Pastes the selected track
	 */
	public void pasteCopiedTrack() {
		if ((selectedTrack != null) && (copiedTrack != null)) {
			try {
				// Get a copy of the copied track
				Track<?> newTrack = copiedTrack.deepClone();

				// Manage the name
				String name = "Copy of " + copiedTrack.getName();

				// Manage the mask
				ScoredChromosomeWindowList mask = copiedTrack.getMask();

				// Get the selected track index
				int selectedTrackIndex = getSelectedTrackIndex();

				// Manage the multi genome information
				List<VariantData> stripesList = null;
				List<MGFilter> filtersList = null;
				if (ProjectManager.getInstance().isMultiGenomeProject()) {
					MGDisplaySettings.getInstance().deleteTrack(trackList[selectedTrackIndex]);		// Get rid of previous MG information
					MGDisplaySettings.getInstance().pasteTemporaryTrack(newTrack);					// Create the new MG information from the copied ones
					stripesList = MGDisplaySettings.getInstance().getVariantSettings().getVariantsForTrack(newTrack);		// Set the new stripe list
					filtersList = MGDisplaySettings.getInstance().getFilterSettings().getMGFiltersForTrack(newTrack);	// Set the new filter list
				}

				// Set up the new track
				setTrack(selectedTrackIndex, newTrack, copiedTrack.getPreferredHeight(), name, mask, stripesList, filtersList);

				// Unlink the selected track
				selectedTrack = null;
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while pasting the track");
			}
		}
	}


	/**
	 * Pastes the selected track
	 * @param name the new name of the track
	 */
	public void pasteSpecialCopiedTrack(String name) {
		if ((selectedTrack != null) && (copiedTrack != null)) {
			try {
				// Get a copy of the copied track
				Track<?> newTrack = copiedTrack.deepClone();

				// Retrieve the right the mask
				ScoredChromosomeWindowList mask = null;
				if (PasteSettings.PASTE_MASK == PasteSettings.YES_OPTION) {
					mask = copiedTrack.getMask();
				} else {
					mask = selectedTrack.getMask();
				}

				// Retrieve the right multi genome information
				List<VariantData> stripesList = null;
				List<MGFilter> filtersList = null;
				if (PasteSettings.PASTE_MG == PasteSettings.YES_OPTION) {
					if (ProjectManager.getInstance().isMultiGenomeProject()) {
						MGDisplaySettings.getInstance().deleteTrack(selectedTrack);											// Get rid of previous MG information on the selected track
						Track<?> currentTrack = null;
						if (PasteSettings.PASTE_DATA == PasteSettings.YES_OPTION) {											// If the data will be pasted, the whole track will change and the reference track is the new track
							currentTrack = newTrack;
						} else {																							// If the data won't be pasted, we only update the selected track!
							currentTrack = selectedTrack;
						}
						MGDisplaySettings.getInstance().pasteTemporaryTrack(currentTrack);										// Create the new MG information from the copied ones
						stripesList = MGDisplaySettings.getInstance().getVariantSettings().getVariantsForTrack(currentTrack);		// Set the new stripes list
						filtersList = MGDisplaySettings.getInstance().getFilterSettings().getMGFiltersForTrack(currentTrack);	// Set the new filters list
					}
				} else {
					if (ProjectManager.getInstance().isMultiGenomeProject()) {
						stripesList = selectedTrack.getStripesList();
						filtersList = selectedTrack.getFiltersList();

						MGDisplaySettings.getInstance().replaceTrack(selectedTrack, newTrack);
					}
				}

				// Set up the new track
				if (PasteSettings.PASTE_DATA == PasteSettings.YES_OPTION) {
					int selectedTrackIndex = getSelectedTrackIndex();
					setTrack(selectedTrackIndex, newTrack, copiedTrack.getPreferredHeight(), name, mask, stripesList, filtersList);
				} else {
					if ((PasteSettings.PASTE_NAME == PasteSettings.YES_OPTION) || (PasteSettings.REDEFINE_NAME == PasteSettings.YES_OPTION)) {
						selectedTrack.setName(name);
					}

					if (PasteSettings.PASTE_MASK == PasteSettings.YES_OPTION) {
						selectedTrack.setMask(mask);
					}
					if (PasteSettings.PASTE_MG == PasteSettings.YES_OPTION) {
						if (ProjectManager.getInstance().isMultiGenomeProject()) {
							selectedTrack.updateMultiGenomeInformation(stripesList, filtersList);
						}
					}
				}

				// Unlink the selected track
				selectedTrack = null;
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while pasting the track");
			}
		}
	}


	/**
	 * Deletes the selected track
	 */
	public void deleteTrack() {
		if (selectedTrack != null) {
			MGDisplaySettings.getInstance().deleteTrack(selectedTrack);
			selectedTrack.delete();
			int selectedTrackIndex = getSelectedTrackIndex();
			for (int i = selectedTrackIndex + 1; i < trackList.length; i++) {
				trackList[i - 1] = trackList[i];
			}
			trackList[trackList.length - 1] = new EmptyTrack(trackList.length);
			trackList[trackList.length - 1].addTrackListener(this);

			selectedTrack = null;
			rebuildPanel();
		}
	}


	/**
	 * Locks the handles of all the tracks
	 */
	public void lockTrackHandles() {
		for (Track<?> currentTrack: trackList) {
			if (currentTrack != null) {
				currentTrack.lockHandle();
			}
		}
	}


	/**
	 * Unlocks the handles of all the tracks
	 */
	public void unlockTracksHandles() {
		for (Track<?> currentTrack: trackList) {
			if (currentTrack != null) {
				currentTrack.unlockHandle();
			}
		}
	}


	/**
	 * Unlocks the track handles when an action ends
	 */
	public void actionEnds() {
		// unlock the track handles
		unlockTracksHandles();
		// enable the action map
		setEnabled(true);
	}


	/**
	 * Locks the track handles when an action starts
	 */
	public void actionStarts() {
		// lock the track handles
		lockTrackHandles();
		// disable the action map
		setEnabled(false);
	}


	/**
	 * Changes the undo count of the undoable tracks
	 */
	public void undoCountChanged() {
		int undoCount = ProjectManager.getInstance().getProjectConfiguration().getUndoCount();
		if (getVersionnedTracks() != null) {
			for (Track<?> currentTrack: getVersionnedTracks()) {
				((VersionedTrack) currentTrack).setUndoCount(undoCount);
			}
		}
	}


	/**
	 * Change the reset track function of the versionned tracks.
	 */
	public void resetTrackChanged() {
		boolean hasToBeDisabled = !ProjectManager.getInstance().getProjectConfiguration().isResetTrack();
		if (hasToBeDisabled) {
			if (getVersionnedTracks() != null) {
				for (Track<?> currentTrack: getVersionnedTracks()) {
					((VersionedTrack) currentTrack).deactivateReset();
				}
			}
		}
	}


	/**
	 * Changes the legend display of the tracks
	 */
	public void legendChanged() {
		for (Track<?> currentTrack: trackList) {
			currentTrack.legendChanged();
			currentTrack.repaint();
		}
	}


	/**
	 * Inserts a blank track right above the specified index
	 * @param trackIndex index where to insert the blank track
	 */
	public void insertTrack(int trackIndex) {
		for (int i = trackList.length - 2; i >= trackIndex; i--) {
			trackList[i + 1] = trackList[i];
		}
		trackList[trackIndex] = new EmptyTrack(trackList.length);
		trackList[trackIndex].addTrackListener(this);
		selectedTrack = null;
		rebuildPanel();
	}


	/**
	 * @param trackList the new track list
	 */
	public void setTrackList (Track<?>[] trackList) {
		this.trackList = trackList;
		rebuildPanel();
	}


	/**
	 * @return the trackList
	 */
	public Track<?>[] getTrackList() {
		return trackList;
	}


	/**
	 * @return the jpTrackList
	 */
	public JPanel getJpTrackList() {
		return jpTrackList;
	}


	/**
	 * @param multiGenomeDrawer
	 * @return the number of the track according to a {@link MultiGenomeDrawer}
	 */
	public int getTrackNumberFromMGGenomeDrawer (MultiGenomeDrawer multiGenomeDrawer) {
		for (int i = 0; i < trackList.length; i++) {
			if (trackList[i].getMultiGenomeDrawer().equals(multiGenomeDrawer)) {
				return i + 1;
			}
		}
		return -1;
	}


	/**
	 * @param multiGenomeDrawer
	 * @return the the track according to a {@link MultiGenomeDrawer}
	 */
	public Track<?> getTrackFromMGGenomeDrawer (MultiGenomeDrawer multiGenomeDrawer) {
		int trackIndex = getTrackNumberFromMGGenomeDrawer(multiGenomeDrawer);
		if (trackIndex != -1) {
			return trackList[trackIndex];
		}
		return null;
	}


	/**
	 * @return the copiedTrack
	 */
	public Track<?> getCopiedTrack() {
		return copiedTrack;
	}


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}


	/**
	 * Notifies all the track listeners that a track has changed
	 * @param evt track event
	 */
	public void notifyTrackListeners(TrackEvent evt) {
		for (TrackListener listener: trackListeners) {
			listener.trackChanged(evt);
		}
	}
}
