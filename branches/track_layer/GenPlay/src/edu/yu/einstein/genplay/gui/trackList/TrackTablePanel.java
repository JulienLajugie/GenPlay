package edu.yu.einstein.genplay.gui.trackList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.LayeredTrack;

public class TrackTablePanel extends JScrollPane implements Serializable, TrackListener, TrackEventsGenerator {

	private final JTable 				jtTrackList;					// panel with the tracks
	private final List<TrackListener> 	trackListeners;					// list of track listeners
	private LayeredTrack[]				trackList;						// array of tracks
	private LayeredTrack				selectedTrack = null;			// track selected
	private LayeredTrack				copiedTrack = null; 			// list of the tracks in the clipboard

	
	public TrackTablePanel() {
		super();
		this.trackListeners = new ArrayList<TrackListener>();
		getVerticalScrollBar().setUnitIncrement(15);
		getVerticalScrollBar().setBlockIncrement(40);
		this.jtTrackList = new JTable();
		ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
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
}
