package edu.yu.einstein.genplay.gui.trackList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellRenderer;

import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;

public class TrackTablePanel extends JScrollPane implements Serializable, TrackListener, TrackEventsGenerator {

	private final static int SCROLL_BAR_UNIT_INCREMENT = 15;
	private final static int SCROLL_BAR_BLOCK_INCREMENT = 40;
	private final TrackTable			jtTrackTable;					// JTable with the tracks
	private final List<TrackListener> 	trackListeners;					// list of track listeners
	private LayeredTrack[]				trackList;						// array of tracks
	private LayeredTrack				selectedTrack = null;			// track selected
	private LayeredTrack				copiedTrack = null; 			// list of the tracks in the clipboard

	
	public TrackTablePanel() {
		super();
		this.trackListeners = new ArrayList<TrackListener>();
		ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
		int trackCount = projectConfiguration.getTrackCount();
		int preferredHeight = projectConfiguration.getTrackHeight();
		trackList = new LayeredTrack[trackCount];
		for (int i = 0; i < trackCount; i++) {
			trackList[i] = new LayeredTrack(i + 1);
			trackList[i].setPreferredHeight(preferredHeight);
			trackList[i].addTrackListener(this);
		}
		TrackListModel trackTableModel = new TrackListModel(trackList);
		jtTrackTable = new TrackTable(trackTableModel);
		
		//jtTrackTable.setModel(trackTableModel);
		getVerticalScrollBar().setUnitIncrement(SCROLL_BAR_UNIT_INCREMENT);
		getVerticalScrollBar().setBlockIncrement(SCROLL_BAR_BLOCK_INCREMENT);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setViewportView(jtTrackTable);
		//addActionsToActionMap();
		//addKeyToInputMap();
	}


	@Override
	public void trackChanged(TrackEvent evt) {
		// TODO Auto-generated method stub
		
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
