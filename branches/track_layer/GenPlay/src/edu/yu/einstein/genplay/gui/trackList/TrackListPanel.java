package edu.yu.einstein.genplay.gui.trackList;

import java.awt.Point;
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
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenu;
import edu.yu.einstein.genplay.gui.popupMenu.TrackMenuFactory;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;

public class TrackListPanel extends JScrollPane implements Serializable, TrackListener, TrackEventsGenerator {

	private final static int SCROLL_BAR_UNIT_INCREMENT = 15;
	private final static int SCROLL_BAR_BLOCK_INCREMENT = 40;
	private final JPanel 				jpTrackList;					// panel with the tracks
	private final List<TrackListener> 	trackListeners;					// list of track listeners
	private TrackListModel 				model;
	private LayeredTrack				selectedTrack = null;			// track selected
	private LayeredTrack				copiedTrack = null; 			// list of the tracks in the clipboard

	
	public TrackListPanel(TrackListModel model) {
		super();
		this.model = model;
		this.trackListeners = new ArrayList<TrackListener>();
		jpTrackList = new JPanel();
		jpTrackList.setLayout(new BoxLayout(jpTrackList, BoxLayout.PAGE_AXIS));
		for (LayeredTrack currentTrack: getModel().getTracks()) {
			jpTrackList.add(currentTrack.getPanel());
			currentTrack.addTrackListener(this);
		}
		getVerticalScrollBar().setUnitIncrement(SCROLL_BAR_UNIT_INCREMENT);
		getVerticalScrollBar().setBlockIncrement(SCROLL_BAR_BLOCK_INCREMENT);
		setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setViewportView(jpTrackList);
		//addActionsToActionMap();
		//addKeyToInputMap();
	}


	/**
	 * @return the model containing the data displayed in this panel
	 */
	public TrackListModel getModel() {
		return model;
	}

	@Override
	public void trackChanged(TrackEvent evt) {
	/*	notifyTrackListeners(evt);
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
		}*/
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
