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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.yu.einstein.genplay.core.list.chromosomeWindowList.ChromosomeWindowList;
import edu.yu.einstein.genplay.core.manager.ExceptionManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAAddConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAAverage;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLACountNonNullLength;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLADivideConstant;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAFilter;
import edu.yu.einstein.genplay.gui.action.SCWListTrack.SCWLAGenerateBinList;
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
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFilterRatio;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFilterThreshold;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFindNext;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLAFindPrevious;
import edu.yu.einstein.genplay.gui.action.SNPListTrack.SLARemoveSNPsNotInGenes;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACopy;
import edu.yu.einstein.genplay.gui.action.allTrack.ATACut;
import edu.yu.einstein.genplay.gui.action.allTrack.ATADelete;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAInsert;
import edu.yu.einstein.genplay.gui.action.allTrack.ATALoadStripes;
import edu.yu.einstein.genplay.gui.action.allTrack.ATAPaste;
import edu.yu.einstein.genplay.gui.action.allTrack.ATARemoveStripes;
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
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAGenerateSCWList;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIndex;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIndexByChromosome;
import edu.yu.einstein.genplay.gui.action.binListTrack.BLAIntervalsSummarization;
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
import edu.yu.einstein.genplay.gui.action.curveTrack.CTAAppearance;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETAGenerateMultiCurvesTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadBinListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadFromDAS;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadGeneListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadNucleotideListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadRepeatFamilyListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadSCWListTrack;
import edu.yu.einstein.genplay.gui.action.emptyTrack.ETALoadSNPListTrack;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLADistanceCalculator;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAExtractExons;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAExtractInterval;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAFilter;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAFilterStrand;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAGeneRenamer;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAScoreExons;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLAScoreRepartitionAroundStart;
import edu.yu.einstein.genplay.gui.action.geneListTrack.GLASearchGene;
import edu.yu.einstein.genplay.gui.action.scoredTrack.STASetYAxis;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAHistory;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTARedo;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAReset;
import edu.yu.einstein.genplay.gui.action.versionedTrack.VTAUndo;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenu;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenuFactory;
import edu.yu.einstein.genplay.gui.track.BinListTrack;
import edu.yu.einstein.genplay.gui.track.CurveTrack;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.track.Track;



/**
 * A scroll panel containing many tracks.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackList extends JScrollPane implements PropertyChangeListener, Serializable {

	private static final long serialVersionUID = 7304431979443474040L; 	// generated ID
	private final JPanel 		jpTrackList;					// panel with the tracks
	private final ProjectConfiguration projectConfiguration;	// ConfigurationManager
	private Track<?>[] 			trackList;						// array of tracks
	private Track<?>			selectedTrack = null;			// track selected
	private Track<?>			copiedTrack = null; 			// list of the tracks in the clipboard
	private int 				draggedTrackIndex = -1;			// index of the dragged track, -1 if none
	private int 				draggedOverTrackIndex = -1; 	// index of the track rolled over by the dragged track, -1 if none


	/**
	 * Creates an instance of {@link TrackList}
	 */
	public TrackList() {
		super();
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
			trackList[i].addPropertyChangeListener(this);
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
			trackList[i].addPropertyChangeListener(this);
		}		
		System.gc();
		rebuildPanel();
	}
	

	/**
	 * Adds the action to the {@link ActionMap}
	 */
	private void addActionsToActionMap() {
		// add general actions
		getActionMap().put(ATACopy.ACTION_KEY, new ATACopy());
		getActionMap().put(ATACut.ACTION_KEY, new ATACut());
		getActionMap().put(ATADelete.ACTION_KEY, new ATADelete());
		getActionMap().put(ATAInsert.ACTION_KEY, new ATAInsert());
		getActionMap().put(ATALoadStripes.ACTION_KEY, new ATALoadStripes());
		getActionMap().put(ATAPaste.ACTION_KEY, new ATAPaste());
		getActionMap().put(ATARemoveStripes.ACTION_KEY, new ATARemoveStripes());
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
		getActionMap().put(GLADistanceCalculator.ACTION_KEY, new GLADistanceCalculator());
		getActionMap().put(GLAExtractExons.ACTION_KEY, new GLAExtractExons());
		getActionMap().put(GLAExtractInterval.ACTION_KEY, new GLAExtractInterval());
		getActionMap().put(GLAFilter.ACTION_KEY, new GLAFilter());
		getActionMap().put(GLAFilterStrand.ACTION_KEY, new GLAFilterStrand());
		getActionMap().put(GLAGeneRenamer.ACTION_KEY, new GLAGeneRenamer());
		getActionMap().put(GLAScoreExons.ACTION_KEY, new GLAScoreExons());
		getActionMap().put(GLAScoreRepartitionAroundStart.ACTION_KEY, new GLAScoreRepartitionAroundStart());
		getActionMap().put(GLASearchGene.ACTION_KEY, new GLASearchGene());
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
		getActionMap().put(SCWLADivideConstant.ACTION_KEY, new SCWLADivideConstant());
		getActionMap().put(SCWLAFilter.ACTION_KEY, new SCWLAFilter());
		getActionMap().put(SCWLAGenerateBinList.ACTION_KEY, new SCWLAGenerateBinList());
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
		// add binlist actions
		getActionMap().put(BLAAddConstant.ACTION_KEY, new BLAAddConstant());
		getActionMap().put(BLAAddConstant.ACTION_KEY, new BLAAddConstant());
		getActionMap().put(BLAAverage.ACTION_KEY, new BLAAverage());
		getActionMap().put(BLACountNonNullBins.ACTION_KEY, new BLACountNonNullBins());		
		getActionMap().put(BLAIntervalsSummarization.ACTION_KEY, new BLAIntervalsSummarization());
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
		getActionMap().put(BLAGenerateSCWList.ACTION_KEY, new BLAGenerateSCWList());
		// SNP tracks
		getActionMap().put(SLAFilterRatio.ACTION_KEY, new SLAFilterRatio());
		getActionMap().put(SLAFilterThreshold.ACTION_KEY, new SLAFilterThreshold());
		getActionMap().put(SLAFindNext.ACTION_KEY, new SLAFindNext());
		getActionMap().put(SLAFindPrevious.ACTION_KEY, new SLAFindPrevious());
		getActionMap().put(SLARemoveSNPsNotInGenes.ACTION_KEY, new SLARemoveSNPsNotInGenes());
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
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATARename.ACCELERATOR, ATARename.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATASave.ACCELERATOR, ATASave.ACTION_KEY);
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
	public void propertyChange(PropertyChangeEvent arg0) {
		firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
		if (arg0.getPropertyName() == "trackRightClicked") {
			setScrollMode(false);
			selectedTrack = (Track<?>)arg0.getSource();
			TrackMenu tm = TrackMenuFactory.getTrackMenu(this);
			tm.show(this, getMousePosition().x, getMousePosition().y);
		} else if (arg0.getPropertyName() == "trackDragged") {
			if (draggedTrackIndex == -1) {
				draggedTrackIndex = ((Track<?>)arg0.getSource()).getTrackNumber() - 1;
			}
			dragTrack();
		} else if (arg0.getPropertyName() == "trackDraggedReleased") {
			releaseTrack();
		} else if (arg0.getPropertyName() == "scrollMode") {
			setScrollMode((Boolean)arg0.getNewValue());
		} else if (arg0.getPropertyName() == "selected") {
			if ((Boolean)arg0.getNewValue() == true) {
				for (Track<?> currentTrack : trackList) {
					if (currentTrack != arg0.getSource()) {
						currentTrack.setSelected(false);
					}
				}
				selectedTrack = (Track<?>)arg0.getSource();
			} else {
				selectedTrack = null;
			}
		}
	}


	/**
	 * Sets a specified {@link Track} at the specified index of the list of tracks
	 * @param index index index where to set
	 * @param track {@link Track} to set
	 * @param preferredHeight preferred height of the track
	 * @param name name of the track (can be null)
	 * @param stripes {@link ChromosomeWindowList} (can be null)
	 * @param stripesList {@link StripesData} (can be null)
	 * @param filtersList {@link VCFFilter} (can be null)
	 */
	public void setTrack(int index, Track<?> track, int preferredHeight, String name, ChromosomeWindowList stripes, List<StripesData> stripesList, List<VCFFilter> filtersList) {
		track.setPreferredHeight(preferredHeight);
		if (name != null) {
			track.setName(name);
		}
		if (stripes != null) {
			track.setStripes(stripes);
		}
		if (stripesList != null && filtersList != null) {
			track.updateMultiGenomeInformation(stripesList, filtersList);
		}
		trackList[index] = track;
		trackList[index].setTrackNumber(index + 1);
		trackList[index].addPropertyChangeListener(this);
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
				int screenTrackBottom = trackList[i].getY() + trackList[i].getHeight() - getVerticalScrollBar().getValue();
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
			if (trackList[i].getPropertyChangeListeners().length == 0) {				
				trackList[i].addPropertyChangeListener(this);
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
				if (!((BinListTrack) currentTrack).getData().isCompressed()) {
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
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track<?>[] result = new Track[count];
		int i = 0;
		for (Track<?> currentTrack: trackList) {
			if (currentTrack instanceof CurveTrack<?>) {
				result[i] = currentTrack;
				i++;
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
				copiedTrack = selectedTrack.deepClone(); 
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
				int selectedTrackIndex = getSelectedTrackIndex();				
				Track<?> emptyTrack = new EmptyTrack(trackList.length);
				setTrack(selectedTrackIndex, emptyTrack, projectConfiguration.getTrackHeight(), null, null, null, null);
				selectedTrack = null;
			} catch (Exception e) {
				ExceptionManager.handleException(this, e, "Error while copying the track");
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
	 * @return true if there is stripes to remove 
	 */
	public boolean isRemoveStripesEnable() {
		return (getSelectedTrack().getStripes() != null);
	}


	/**
	 * Pastes the selected track
	 */
	public void pasteCopiedTrack() {
		if ((selectedTrack != null) && (copiedTrack != null)) {
			try {
				int selectedTrackIndex = getSelectedTrackIndex();
				Track<?> newTrack = copiedTrack.deepClone();
				/*GenomeWindow currentGenomeWindow = trackList[selectedTrackIndex].getGenomeWindow();
				newTrack.setGenomeWindow(currentGenomeWindow);*/
				setTrack(selectedTrackIndex, newTrack, copiedTrack.getPreferredHeight(), null, null, null, null);
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
			selectedTrack.delete();
			int selectedTrackIndex = getSelectedTrackIndex();
			for (int i = selectedTrackIndex + 1; i < trackList.length; i++) {
				trackList[i - 1] = trackList[i];
			}
			trackList[trackList.length - 1] = new EmptyTrack(trackList.length);
			trackList[trackList.length - 1].addPropertyChangeListener(this);
			selectedTrack = null;
			rebuildPanel();
		}
	}


	/**
	 * Locks the handles of all the tracks 
	 */
	public void lockTrackHandles() {
		for (Track<?> currentTrack: trackList) {
			currentTrack.lockHandle();
		}
	}


	/**
	 * Unlocks the handles of all the tracks
	 */
	public void unlockTracksHandles() {
		for (Track<?> currentTrack: trackList) {
			currentTrack.unlockHandle();
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
		if (getBinListTracks() != null) {
			for (Track<?> currentTrack: getBinListTracks()) {
				((BinListTrack) currentTrack).setUndoCount(undoCount);
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
		trackList[trackIndex].addPropertyChangeListener(this);
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
}
