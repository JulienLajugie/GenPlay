/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.trackList;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.chromosomeWindowList.ChromosomeWindowList;
import yu.einstein.gdp2.gui.event.GenomeWindowEvent;
import yu.einstein.gdp2.gui.event.GenomeWindowListener;
import yu.einstein.gdp2.gui.event.GenomeWindowModifier;
import yu.einstein.gdp2.gui.popupMenu.TrackMenu;
import yu.einstein.gdp2.gui.popupMenu.TrackMenuFactory;
import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.EmptyTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.action.SCWList.GenerateBinListAction;
import yu.einstein.gdp2.gui.trackList.action.binList.AdditionConstantAction;
import yu.einstein.gdp2.gui.trackList.action.binList.AdditionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.AverageAction;
import yu.einstein.gdp2.gui.trackList.action.binList.BinCountAction;
import yu.einstein.gdp2.gui.trackList.action.binList.CalculationOnProjectionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ChangeBinSizeAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ChangePrecisionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ConcatenateAction;
import yu.einstein.gdp2.gui.trackList.action.binList.CorrelationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.DensityAction;
import yu.einstein.gdp2.gui.trackList.action.binList.DensityFilterAction;
import yu.einstein.gdp2.gui.trackList.action.binList.DivisionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.DivisionConstantAction;
import yu.einstein.gdp2.gui.trackList.action.binList.GaussAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IndexAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IndexByChromosomeAction;
import yu.einstein.gdp2.gui.trackList.action.binList.IslandFinderAction;
import yu.einstein.gdp2.gui.trackList.action.binList.Log2Action;
import yu.einstein.gdp2.gui.trackList.action.binList.Log2WithDamperAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MaximumAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MinimumAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MultiplicationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.MultiplicationConstantAction;
import yu.einstein.gdp2.gui.trackList.action.binList.NormalizeAction;
import yu.einstein.gdp2.gui.trackList.action.binList.RedoAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ResetAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SaturationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ScoreCountAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SearchPeaksAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ShowHistoryAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ShowRepartitionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.StandardDeviationAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SubtractionAction;
import yu.einstein.gdp2.gui.trackList.action.binList.SubtractionConstantAction;
import yu.einstein.gdp2.gui.trackList.action.binList.ThresholdFilterAction;
import yu.einstein.gdp2.gui.trackList.action.binList.UndoAction;
import yu.einstein.gdp2.gui.trackList.action.curve.AppearanceAction;
import yu.einstein.gdp2.gui.trackList.action.curve.SetYAxisAction;
import yu.einstein.gdp2.gui.trackList.action.emptyTrack.LoadBinListTrackAction;
import yu.einstein.gdp2.gui.trackList.action.emptyTrack.LoadGeneListTrackAction;
import yu.einstein.gdp2.gui.trackList.action.emptyTrack.LoadRepeatFamilyListTrackAction;
import yu.einstein.gdp2.gui.trackList.action.emptyTrack.LoadSCWListTrackAction;
import yu.einstein.gdp2.gui.trackList.action.geneList.ExtractIntervalAction;
import yu.einstein.gdp2.gui.trackList.action.geneList.SearchGeneAction;
import yu.einstein.gdp2.gui.trackList.action.general.CopyAction;
import yu.einstein.gdp2.gui.trackList.action.general.CutAction;
import yu.einstein.gdp2.gui.trackList.action.general.DeleteAction;
import yu.einstein.gdp2.gui.trackList.action.general.LoadStripesAction;
import yu.einstein.gdp2.gui.trackList.action.general.PasteAction;
import yu.einstein.gdp2.gui.trackList.action.general.RemoveStripesAction;
import yu.einstein.gdp2.gui.trackList.action.general.RenameAction;
import yu.einstein.gdp2.gui.trackList.action.general.SaveAsImageAction;
import yu.einstein.gdp2.gui.trackList.action.general.SaveTrackAction;
import yu.einstein.gdp2.gui.trackList.action.general.SetHeightAction;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ConfigurationManager;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.ZoomManager;


/**
 * A scroll panel containing many tracks.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackList extends JScrollPane implements PropertyChangeListener, GenomeWindowListener, GenomeWindowModifier {

	private static final long serialVersionUID = 7304431979443474040L; // generated ID
	private final JPanel 		jpTrackList;					// panel with the tracks
	private final ConfigurationManager configurationManager;	// ConfigurationManager
	private final ZoomManager 	zoomManager;					// ZoomManager
	private final ChromosomeManager chromosomeManager;			// ChromosomeManager
	private final ArrayList<GenomeWindowListener> listenerList;	// list of GenomeWindowListener
	private GenomeWindow 		displayedGenomeWindow;			// displayed GenomeWindow
	private Track[] 			trackList;						// array of tracks
	private Track				selectedTrack = null;			// track selected
	private Track				copiedTrack = null; 			// list of the tracks in the clipboard
	private int 				draggedTrackIndex = -1;			// index of the dragged track, -1 if none
	private int 				draggedOverTrackIndex = -1; 	// index of the track rolled over by the dragged track, -1 if none


	/**
	 * Creates an instance of {@link TrackList}
	 * @param configurationManager a {@link ConfigurationManager}
	 * @param zoomManager a {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 */
	public TrackList(ConfigurationManager configurationManager, ChromosomeManager chromosomeManager, ZoomManager zoomManager, GenomeWindow displayedGenomeWindow) {
		super();
		this.configurationManager = configurationManager;
		this.chromosomeManager = chromosomeManager;
		this.zoomManager = zoomManager;
		this.displayedGenomeWindow = displayedGenomeWindow;
		this.listenerList = new ArrayList<GenomeWindowListener>();
		getVerticalScrollBar().setUnitIncrement(15);
		getVerticalScrollBar().setBlockIncrement(40);
		jpTrackList = new JPanel();
		jpTrackList.setLayout(new BoxLayout(jpTrackList, BoxLayout.PAGE_AXIS));		
		int trackCount = configurationManager.getTrackCount();
		int preferredHeight = configurationManager.getTrackHeight();
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			trackList[i] = new EmptyTrack(zoomManager, displayedGenomeWindow, i + 1);
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
		getActionMap().put(CopyAction.ACTION_KEY, new CopyAction(this));
		getActionMap().put(CutAction.ACTION_KEY, new CutAction(this));
		getActionMap().put(DeleteAction.ACTION_KEY, new DeleteAction(this));
		getActionMap().put(LoadStripesAction.ACTION_KEY, new LoadStripesAction(this));
		getActionMap().put(PasteAction.ACTION_KEY, new PasteAction(this));
		getActionMap().put(RemoveStripesAction.ACTION_KEY, new RemoveStripesAction(this));
		getActionMap().put(RenameAction.ACTION_KEY, new RenameAction(this));
		getActionMap().put(SaveAsImageAction.ACTION_KEY, new SaveAsImageAction(this));
		getActionMap().put(SetHeightAction.ACTION_KEY, new SetHeightAction(this));
		// add empty list actions
		getActionMap().put(LoadBinListTrackAction.ACTION_KEY, new LoadBinListTrackAction(this));
		getActionMap().put(LoadGeneListTrackAction.ACTION_KEY, new LoadGeneListTrackAction(this));
		getActionMap().put(LoadRepeatFamilyListTrackAction.ACTION_KEY, new LoadRepeatFamilyListTrackAction(this));
		getActionMap().put(LoadSCWListTrackAction.ACTION_KEY, new LoadSCWListTrackAction(this));
		// add gene list actions
		getActionMap().put(SearchGeneAction.ACTION_KEY, new SearchGeneAction(this));
		getActionMap().put(ExtractIntervalAction.ACTION_KEY, new ExtractIntervalAction(this));
		// add curve track actions
		getActionMap().put(AppearanceAction.ACTION_KEY, new AppearanceAction(this));
		getActionMap().put(SetYAxisAction.ACTION_KEY, new SetYAxisAction(this));
		// add SCWList actions
		getActionMap().put(GenerateBinListAction.ACTION_KEY, new GenerateBinListAction(this));
		// add binlist actions
		getActionMap().put(AdditionConstantAction.ACTION_KEY, new AdditionConstantAction(this));
		getActionMap().put(AdditionAction.ACTION_KEY, new AdditionAction(this));
		getActionMap().put(AdditionConstantAction.ACTION_KEY, new AdditionConstantAction(this));
		getActionMap().put(AverageAction.ACTION_KEY, new AverageAction(this));
		getActionMap().put(BinCountAction.ACTION_KEY, new BinCountAction(this));		
		getActionMap().put(CalculationOnProjectionAction.ACTION_KEY, new CalculationOnProjectionAction(this));
		getActionMap().put(ChangeBinSizeAction.ACTION_KEY, new ChangeBinSizeAction(this));
		getActionMap().put(ChangePrecisionAction.ACTION_KEY, new ChangePrecisionAction(this));
		getActionMap().put(ConcatenateAction.ACTION_KEY, new ConcatenateAction(this));
		getActionMap().put(CorrelationAction.ACTION_KEY, new CorrelationAction(this));
		getActionMap().put(DensityAction.ACTION_KEY, new DensityAction(this));
		getActionMap().put(DensityFilterAction.ACTION_KEY, new DensityFilterAction(this));
		getActionMap().put(DivisionAction.ACTION_KEY, new DivisionAction(this));
		getActionMap().put(DivisionConstantAction.ACTION_KEY, new DivisionConstantAction(this));
		getActionMap().put(GaussAction.ACTION_KEY, new GaussAction(this));
		getActionMap().put(IndexAction.ACTION_KEY, new IndexAction(this));
		getActionMap().put(IndexByChromosomeAction.ACTION_KEY, new IndexByChromosomeAction(this));
		getActionMap().put(IslandFinderAction.ACTION_KEY, new IslandFinderAction(this));
		getActionMap().put(Log2Action.ACTION_KEY, new Log2Action(this));
		getActionMap().put(Log2WithDamperAction.ACTION_KEY, new Log2WithDamperAction(this));
		getActionMap().put(MaximumAction.ACTION_KEY, new MaximumAction(this));
		getActionMap().put(MinimumAction.ACTION_KEY, new MinimumAction(this));
		getActionMap().put(MultiplicationAction.ACTION_KEY, new MultiplicationAction(this));
		getActionMap().put(MultiplicationConstantAction.ACTION_KEY, new MultiplicationConstantAction(this));
		getActionMap().put(NormalizeAction.ACTION_KEY, new NormalizeAction(this));
		getActionMap().put(RedoAction.ACTION_KEY, new RedoAction(this));
		getActionMap().put(ResetAction.ACTION_KEY, new ResetAction(this));
		getActionMap().put(ScoreCountAction.ACTION_KEY, new ScoreCountAction(this));
		getActionMap().put(SaturationAction.ACTION_KEY, new SaturationAction(this));
		getActionMap().put(SaveTrackAction.ACTION_KEY, new SaveTrackAction(this));
		getActionMap().put(SearchPeaksAction.ACTION_KEY, new SearchPeaksAction(this));
		getActionMap().put(ShowHistoryAction.ACTION_KEY, new ShowHistoryAction(this));
		getActionMap().put(ShowRepartitionAction.ACTION_KEY, new ShowRepartitionAction(this));
		getActionMap().put(StandardDeviationAction.ACTION_KEY, new StandardDeviationAction(this));
		getActionMap().put(SubtractionAction.ACTION_KEY, new SubtractionAction(this));
		getActionMap().put(SubtractionConstantAction.ACTION_KEY, new SubtractionConstantAction(this));
		getActionMap().put(ThresholdFilterAction.ACTION_KEY, new ThresholdFilterAction(this));
		getActionMap().put(UndoAction.ACTION_KEY, new UndoAction(this));
	}


	/**
	 * Adds key listener to the {@link InputMap}
	 */
	private void addKeyToInputMap() {
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CopyAction.ACCELERATOR, CopyAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(CutAction.ACCELERATOR, CutAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(PasteAction.ACCELERATOR, PasteAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(DeleteAction.ACCELERATOR, DeleteAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(RenameAction.ACCELERATOR, RenameAction.ACTION_KEY);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(SearchGeneAction.ACCELERATOR, SearchGeneAction.ACTION_KEY);
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(UndoAction.ACCELERATOR, UndoAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(RedoAction.ACCELERATOR, RedoAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ResetAction.ACCELERATOR, ResetAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ShowHistoryAction.ACCELERATOR, ShowHistoryAction.ACTION_KEY);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(SaveTrackAction.ACCELERATOR, SaveTrackAction.ACTION_KEY);		
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		firePropertyChange(arg0.getPropertyName(), arg0.getOldValue(), arg0.getNewValue());
		if (arg0.getPropertyName() == "trackRightClicked") {
			setScrollMode(false);
			selectedTrack = (Track)arg0.getSource();
			TrackMenu tm = TrackMenuFactory.getTrackMenu(this);
			tm.show(this, getMousePosition().x, getMousePosition().y);
		} else if (arg0.getPropertyName() == "trackDragged") {
			if (draggedTrackIndex == -1) {
				draggedTrackIndex = ((Track)arg0.getSource()).getTrackNumber() - 1;
			}
			dragTrack();
		} else if (arg0.getPropertyName() == "trackDraggedReleased") {
			releaseTrack();
		} else if (arg0.getPropertyName() == "scrollMode") {
			setScrollMode((Boolean)arg0.getNewValue());
		} else if (arg0.getPropertyName() == "selected") {
			if ((Boolean)arg0.getNewValue() == true) {
				for (Track currentTrack : trackList) {
					if (currentTrack != arg0.getSource()) {
						currentTrack.setSelected(false);
					}
				}
				selectedTrack = (Track)arg0.getSource();
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
	public void setTrack(int index, Track track, int preferredHeight, String name, ChromosomeWindowList stripes) {
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
		for (Track currentTrack: trackList) {
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
				Track trackTmp = trackList[draggedTrackIndex];
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
				trackList[i].addGenomeWindowListener(this);
				trackList[i].addPropertyChangeListener(this);
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
			((Track)jpTrackList.getComponent(i)).setPreferredHeight(preferredHeight);
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
		Track[] trackTmp = trackList;
		trackList = new Track[trackCount];
		for (int i = 0; i < trackCount; i++) {
			if (i < trackTmp.length) {
				trackList[i] = trackTmp[i];
			} else {
				trackList[i] = new EmptyTrack(zoomManager, displayedGenomeWindow, i + 1);
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
			for (Track track : trackList) {
				track.setGenomeWindow(displayedGenomeWindow);
			}
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, displayedGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
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
	public Track getSelectedTrack() {
		return selectedTrack;
	}


	/**
	 * @return an array containing all the {@link EmptyTrack}
	 */
	public Track[] getEmptyTracks() {
		int count = 0;
		for (Track currentTrack: trackList) {
			if (currentTrack instanceof EmptyTrack) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track[] result = new Track[count];
		int i = 0;
		for (Track currentTrack: trackList) {
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
	public Track[] getBinListTracks() {
		int count = 0;
		for (Track currentTrack: trackList) {
			if (currentTrack instanceof BinListTrack) {
				count++;
			}
		}
		if (count == 0) {
			return null;
		}
		Track[] result = new Track[count];
		int i = 0;
		for (Track currentTrack: trackList) {
			if (currentTrack instanceof BinListTrack) {
				result[i] = currentTrack;
				i++;
			}
		}
		return result;
	}


	/**
	 * @return the {@link ChromosomeManager}
	 */
	public ChromosomeManager getChromosomeManager() {
		return chromosomeManager;
	}


	/**
	 * @return the {@link ConfigurationManager}
	 */
	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}


	/**
	 * @return the {@link ZoomManager}
	 */
	public ZoomManager getZoomManager() {
		return zoomManager;
	}


	/**
	 * Copies the selected track.
	 */
	public void copyTrack() {
		if (selectedTrack != null) {
			try {
				copiedTrack = selectedTrack.copy();
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
				Track emptyTrack = new EmptyTrack(zoomManager, displayedGenomeWindow, trackList.length);
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
				Track newTrack = copiedTrack.copy();
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
			trackList[trackList.length - 1] = new EmptyTrack(zoomManager, displayedGenomeWindow, trackList.length);
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
		for (Track currentTrack: trackList) {
			currentTrack.lockHandle();
		}
	}


	/**
	 * Unlocks the handles of all the tracks
	 */
	public void unlockTracksHandles() {
		for (Track currentTrack: trackList) {
			currentTrack.unlockHandle();
		}
	}
		
	
	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);		
	}
	
	
	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		setGenomeWindow(evt.getNewWindow());	
	}
}
