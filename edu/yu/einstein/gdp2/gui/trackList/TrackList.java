/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.exception.InvalidFileTypeException;
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
import yu.einstein.gdp2.gui.action.allTrack.ATACopy;
import yu.einstein.gdp2.gui.action.allTrack.ATACut;
import yu.einstein.gdp2.gui.action.allTrack.ATADelete;
import yu.einstein.gdp2.gui.action.allTrack.ATALoadStripes;
import yu.einstein.gdp2.gui.action.allTrack.ATAPaste;
import yu.einstein.gdp2.gui.action.allTrack.ATARemoveStripes;
import yu.einstein.gdp2.gui.action.allTrack.ATARename;
import yu.einstein.gdp2.gui.action.allTrack.ATASaveAsImage;
import yu.einstein.gdp2.gui.action.allTrack.ATASave;
import yu.einstein.gdp2.gui.action.allTrack.ATASetHeight;
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
import yu.einstein.gdp2.gui.action.binListTrack.BLALog;
import yu.einstein.gdp2.gui.action.binListTrack.BLALogOnAvgWithDamper;
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
import yu.einstein.gdp2.gui.action.curveTrack.CTAHistory;
import yu.einstein.gdp2.gui.action.curveTrack.CTARedo;
import yu.einstein.gdp2.gui.action.curveTrack.CTAReset;
import yu.einstein.gdp2.gui.action.curveTrack.CTAUndo;
import yu.einstein.gdp2.gui.action.curveTrack.CTAAppearance;
import yu.einstein.gdp2.gui.action.emptyTrack.ETAGenerateMultiCurvesTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadBinListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadFromDAS;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadGeneListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadNucleotideListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadRepeatFamilyListTrack;
import yu.einstein.gdp2.gui.action.emptyTrack.ETALoadSCWListTrack;
import yu.einstein.gdp2.gui.action.geneListTrack.GLAExtractInterval;
import yu.einstein.gdp2.gui.action.geneListTrack.GLASearchGene;
import yu.einstein.gdp2.gui.action.scoredTrack.STASetYAxis;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import yu.einstein.gdp2.gui.event.genomeWindowEvent.GenomeWindowListener;
import yu.einstein.gdp2.gui.popupMenu.TrackMenu;
import yu.einstein.gdp2.gui.popupMenu.TrackMenuFactory;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.CurveTrack;
import yu.einstein.gdp2.gui.track.EmptyTrack;
import yu.einstein.gdp2.gui.track.Track;


/**
 * A scroll panel containing many tracks.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackList extends JScrollPane implements PropertyChangeListener, GenomeWindowListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = 7304431979443474040L; 	// generated ID
	private final JPanel 		jpTrackList;					// panel with the tracks
	private final ConfigurationManager configurationManager;	// ConfigurationManager
	private final List<GenomeWindowListener> gwListenerList;	// list of GenomeWindowListener
	private GenomeWindow 		displayedGenomeWindow;			// displayed GenomeWindow
	private Track<?>[] 			trackList;						// array of tracks
	private Track<?>				selectedTrack = null;			// track selected
	private Track<?>				copiedTrack = null; 			// list of the tracks in the clipboard
	private int 				draggedTrackIndex = -1;			// index of the dragged track, -1 if none
	private int 				draggedOverTrackIndex = -1; 	// index of the track rolled over by the dragged track, -1 if none


	/**
	 * Creates an instance of {@link TrackList}
	 */
	public TrackList(GenomeWindow displayedGenomeWindow) {
		super();
		this.configurationManager = ConfigurationManager.getInstance();
		this.displayedGenomeWindow = displayedGenomeWindow;
		this.gwListenerList = new ArrayList<GenomeWindowListener>();
		getVerticalScrollBar().setUnitIncrement(15);
		getVerticalScrollBar().setBlockIncrement(40);
		jpTrackList = new JPanel();
		jpTrackList.setLayout(new BoxLayout(jpTrackList, BoxLayout.PAGE_AXIS));		
		int trackCount = configurationManager.getTrackCount();
		int preferredHeight = configurationManager.getTrackHeight();
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			trackList[i] = new EmptyTrack(displayedGenomeWindow, i + 1);
			trackList[i].setPreferredHeight(preferredHeight);
			trackList[i].addPropertyChangeListener(this);
			trackList[i].addGenomeWindowListener(this);
			jpTrackList.add(trackList[i]);
		}
		setViewportView(jpTrackList);
		addActionsToActionMap();
		addKeyToInputMap();
	}


	/**
	 * Adds the action to the {@link ActionMap}
	 */
	private void addActionsToActionMap() {
		// add general actions
		getActionMap().put(ATACopy.ACTION_KEY, new ATACopy());
		getActionMap().put(ATACut.ACTION_KEY, new ATACut());
		getActionMap().put(ATADelete.ACTION_KEY, new ATADelete());
		getActionMap().put(ATALoadStripes.ACTION_KEY, new ATALoadStripes());
		getActionMap().put(ATAPaste.ACTION_KEY, new ATAPaste());
		getActionMap().put(ATARemoveStripes.ACTION_KEY, new ATARemoveStripes());
		getActionMap().put(ATARename.ACTION_KEY, new ATARename());
		getActionMap().put(ATASave.ACTION_KEY, new ATASave());
		getActionMap().put(ATASaveAsImage.ACTION_KEY, new ATASaveAsImage());
		getActionMap().put(ATASetHeight.ACTION_KEY, new ATASetHeight());
		// add empty list actions
		getActionMap().put(ETALoadBinListTrack.ACTION_KEY, new ETALoadBinListTrack());
		getActionMap().put(ETALoadGeneListTrack.ACTION_KEY, new ETALoadGeneListTrack());
		getActionMap().put(ETALoadNucleotideListTrack.ACTION_KEY, new ETALoadNucleotideListTrack());
		getActionMap().put(ETALoadRepeatFamilyListTrack.ACTION_KEY, new ETALoadRepeatFamilyListTrack());
		getActionMap().put(ETALoadSCWListTrack.ACTION_KEY, new ETALoadSCWListTrack());
		getActionMap().put(ETALoadFromDAS.ACTION_KEY, new ETALoadFromDAS());
		getActionMap().put(ETAGenerateMultiCurvesTrack.ACTION_KEY, new ETAGenerateMultiCurvesTrack());
		// add gene list actions
		getActionMap().put(GLASearchGene.ACTION_KEY, new GLASearchGene());
		getActionMap().put(GLAExtractInterval.ACTION_KEY, new GLAExtractInterval());
		// add curve track actions
		getActionMap().put(CTAAppearance.ACTION_KEY, new CTAAppearance());
		getActionMap().put(CTAHistory.ACTION_KEY, new CTAHistory());
		getActionMap().put(CTARedo.ACTION_KEY, new CTARedo());
		getActionMap().put(CTAReset.ACTION_KEY, new CTAReset());
		getActionMap().put(CTAUndo.ACTION_KEY, new CTAUndo());		
		// add scored track actions
		getActionMap().put(STASetYAxis.ACTION_KEY, new STASetYAxis());
		// add SCWList actions
		getActionMap().put(SCWLAAddConstant.ACTION_KEY, new SCWLAAddConstant());
		getActionMap().put(SCWLAAverage.ACTION_KEY, new SCWLAAverage());
		getActionMap().put(SCWLACountNonNullLength.ACTION_KEY, new SCWLACountNonNullLength());
		getActionMap().put(SCWLADivideConstant.ACTION_KEY, new SCWLADivideConstant());
		getActionMap().put(SCWLAGenerateBinList.ACTION_KEY, new SCWLAGenerateBinList());
		getActionMap().put(SCWLAInvertConstant.ACTION_KEY, new SCWLAInvertConstant());
		getActionMap().put(SCWLAMax.ACTION_KEY, new SCWLAMax());
		getActionMap().put(SCWLAMin.ACTION_KEY, new SCWLAMin());
		getActionMap().put(SCWLAMultiplyConstant.ACTION_KEY, new SCWLAMultiplyConstant());
		getActionMap().put(SCWLAStandardDeviation.ACTION_KEY, new SCWLAStandardDeviation());
		getActionMap().put(SCWLASubtractConstant.ACTION_KEY, new SCWLASubtractConstant());
		// add binlist actions
		getActionMap().put(BLAAddConstant.ACTION_KEY, new BLAAddConstant());
		getActionMap().put(BLAAdd.ACTION_KEY, new BLAAdd());
		getActionMap().put(BLAAddConstant.ACTION_KEY, new BLAAddConstant());
		getActionMap().put(BLAAverage.ACTION_KEY, new BLAAverage());
		getActionMap().put(BLACountNonNullBins.ACTION_KEY, new BLACountNonNullBins());		
		getActionMap().put(BLACalculationOnProjection.ACTION_KEY, new BLACalculationOnProjection());
		getActionMap().put(BLAChangeBinSize.ACTION_KEY, new BLAChangeBinSize());
		getActionMap().put(BLAChangeDataPrecision.ACTION_KEY, new BLAChangeDataPrecision());
		getActionMap().put(BLACompress.ACTION_KEY, new BLACompress());
		getActionMap().put(BLAConcatenate.ACTION_KEY, new BLAConcatenate());
		getActionMap().put(BLACorrelate.ACTION_KEY, new BLACorrelate());
		getActionMap().put(BLADensity.ACTION_KEY, new BLADensity());
		getActionMap().put(BLADivide.ACTION_KEY, new BLADivide());
		getActionMap().put(BLADivideConstant.ACTION_KEY, new BLADivideConstant());
		getActionMap().put(BLAFilter.ACTION_KEY, new BLAFilter());
		getActionMap().put(BLAGauss.ACTION_KEY, new BLAGauss());
		getActionMap().put(BLAIndex.ACTION_KEY, new BLAIndex());
		getActionMap().put(BLAIndexByChromosome.ACTION_KEY, new BLAIndexByChromosome());
		getActionMap().put(BLAInvertConstant.ACTION_KEY, new BLAInvertConstant());
		getActionMap().put(BLAFindIslands.ACTION_KEY, new BLAFindIslands());
		getActionMap().put(BLALog.ACTION_KEY, new BLALog());
		getActionMap().put(BLALogOnAvgWithDamper.ACTION_KEY, new BLALogOnAvgWithDamper());
		getActionMap().put(BLAMax.ACTION_KEY, new BLAMax());
		getActionMap().put(BLAMin.ACTION_KEY, new BLAMin());
		getActionMap().put(BLAMultiply.ACTION_KEY, new BLAMultiply());
		getActionMap().put(BLAMultiplyConstant.ACTION_KEY, new BLAMultiplyConstant());
		getActionMap().put(BLANormalize.ACTION_KEY, new BLANormalize());
		getActionMap().put(BLANormalizeStandardScore.ACTION_KEY, new BLANormalizeStandardScore());
		getActionMap().put(BLASumScore.ACTION_KEY, new BLASumScore());
		getActionMap().put(BLASaturate.ACTION_KEY, new BLASaturate());
		getActionMap().put(BLASearchPeaks.ACTION_KEY, new BLASearchPeaks());
		getActionMap().put(BLARepartition.ACTION_KEY, new BLARepartition());
		getActionMap().put(BLAStandardDeviation.ACTION_KEY, new BLAStandardDeviation());
		getActionMap().put(BLASubtract.ACTION_KEY, new BLASubtract());
		getActionMap().put(BLASubtractConstant.ACTION_KEY, new BLASubtractConstant());
		getActionMap().put(BLATransfrag.ACTION_KEY, new BLATransfrag());
	}


	/**
	 * Adds key listener to the {@link InputMap}
	 */
	private void addKeyToInputMap() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATACopy.ACCELERATOR, ATACopy.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATACut.ACCELERATOR, ATACut.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATAPaste.ACCELERATOR, ATAPaste.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATADelete.ACCELERATOR, ATADelete.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATARename.ACCELERATOR, ATARename.ACTION_KEY);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(GLASearchGene.ACCELERATOR, GLASearchGene.ACTION_KEY);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CTAUndo.ACCELERATOR, CTAUndo.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CTARedo.ACCELERATOR, CTARedo.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CTAReset.ACCELERATOR, CTAReset.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CTAHistory.ACCELERATOR, CTAHistory.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ATASave.ACCELERATOR, ATASave.ACTION_KEY);		
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
	 */
	public void setTrack(int index, Track<?> track, int preferredHeight, String name, ChromosomeWindowList stripes) {
		track.setPreferredHeight(preferredHeight);
		if (name != null) {
			track.setName(name);
		}
		if (stripes != null) {
			track.setStripes(stripes);
		}
		trackList[index] = track;
		trackList[index].setTrackNumber(index + 1);
		trackList[index].addPropertyChangeListener(this);
		trackList[index].addGenomeWindowListener(this);
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
	private void rebuildPanel() {
		jpTrackList.removeAll();
		for(int i = 0; i < trackList.length; i++) {
			trackList[i].setTrackNumber(i + 1);
			jpTrackList.add(trackList[i]);
			if (trackList[i].getPropertyChangeListeners().length == 0) {				
				trackList[i].addPropertyChangeListener(this);
			}
			if (trackList[i].getGenomeWindowListeners().length == 0) {
				trackList[i].addGenomeWindowListener(this);
			}
		}		
		jpTrackList.revalidate();
		setViewportView(jpTrackList);
	}


	/**
	 * Sets the height of each {@link Track} according to the value specified in the {@link ConfigurationManager}
	 */
	public void trackHeightChanged() {
		int preferredHeight = configurationManager.getTrackHeight();
		for(int i = 0; i < trackList.length; i++) {
			trackList[i].setPreferredHeight(preferredHeight);
			((Track<?>)jpTrackList.getComponent(i)).setPreferredHeight(preferredHeight);
		}
		revalidate();
	}


	/**
	 * Changes the number of {@link Track} in the {@link TrackList} according to 
	 * the value specified in the {@link ConfigurationManager}
	 */
	public void trackCountChanged() {
		int trackCount = configurationManager.getTrackCount();
		int preferredHeight = configurationManager.getTrackHeight();
		Track<?>[] trackTmp = trackList;
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			if (i < trackTmp.length) {
				trackList[i] = trackTmp[i];
			} else {
				trackList[i] = new EmptyTrack(displayedGenomeWindow, i + 1);
				trackList[i].setPreferredHeight(preferredHeight);
			}
		}
		rebuildPanel();
	}


	/**
	 * Sets the {@link GenomeWindow} displayed by the track
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(displayedGenomeWindow)) {
			GenomeWindow oldGenomeWindow = displayedGenomeWindow;
			displayedGenomeWindow = newGenomeWindow;
			for (Track<?> track : trackList) {
				track.setGenomeWindow(displayedGenomeWindow);
			}
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, displayedGenomeWindow);
			for (GenomeWindowListener currentListener: gwListenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}


	/**
	 * @return the displayed {@link GenomeWindow}
	 */
	public GenomeWindow getGenomeWindow() {
		return displayedGenomeWindow;
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
				Track<?> emptyTrack = new EmptyTrack(displayedGenomeWindow, trackList.length);
				setTrack(selectedTrackIndex, emptyTrack, configurationManager.getTrackHeight(), null, null);
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
				GenomeWindow currentGenomeWindow = trackList[selectedTrackIndex].getGenomeWindow();
				Track<?> newTrack = copiedTrack.deepClone();
				newTrack.setGenomeWindow(currentGenomeWindow);
				setTrack(selectedTrackIndex, newTrack, copiedTrack.getPreferredHeight(), null, null);
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
			int selectedTrackIndex = getSelectedTrackIndex();
			for (int i = selectedTrackIndex + 1; i < trackList.length; i++) {
				trackList[i - 1] = trackList[i];
			}
			trackList[trackList.length - 1] = new EmptyTrack(displayedGenomeWindow, trackList.length);
			trackList[trackList.length - 1].addPropertyChangeListener(this);
			trackList[trackList.length - 1].addGenomeWindowListener(this);
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


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.add(genomeWindowListener);		
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		setGenomeWindow(evt.getNewWindow());	
	}


	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[gwListenerList.size()];
		return gwListenerList.toArray(genomeWindowListeners);
	}


	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.remove(genomeWindowListener);		
	}


	/**
	 * Saves the current list of tracks into a file
	 */
	public void saveProject(File outputFile) {
		try {
			// remove all the references to the listener so we don't save them
			for (Track<?> currentTrack: trackList) {
				currentTrack.removePropertyChangeListener(this);
				currentTrack.removeGenomeWindowListener(this);
			}
			FileOutputStream fos = new FileOutputStream(outputFile);
			GZIPOutputStream gz = new GZIPOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(gz);
			// there is bug during the serialization with the nimbus LAF if the track list is visible 
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				setViewportView(null);
			}
			oos.writeObject(this.trackList);
			// there is bug during the serialization with the nimbus LAF if the track list is visible
			if (UIManager.getLookAndFeel().getID().equalsIgnoreCase("Nimbus")) {
				setViewportView(jpTrackList);
			}
			oos.flush();
			oos.close();
			gz.flush();
			gz.close();
			// rebuild the references to the listener
			for (Track<?> currentTrack: trackList) {
				currentTrack.addPropertyChangeListener(this);
				currentTrack.addGenomeWindowListener(this);
			}
		} catch (IOException e) {
			ExceptionManager.handleException(getRootPane(), e, "An error occurred while saving the project"); 
		}
	}


	/**
	 * Loads a list of tracks from a file
	 * @throws Exception 
	 */
	public void loadProject(File inputFile) throws Exception {
		try {
			FileInputStream fis = new FileInputStream(inputFile);
			GZIPInputStream gz = new GZIPInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(gz);
			trackList = (Track[])ois.readObject();
			rebuildPanel();
		} catch (IOException e) {
			// a IOException is likely to be caused by a invalid file type 
			throw new InvalidFileTypeException();
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
		int undoCount = ConfigurationManager.getInstance().getUndoCount();
		for (Track<?> currentTrack: getBinListTracks()) {
			((BinListTrack) currentTrack).setUndoCount(undoCount);
		}		
	}
}
