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
package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TrackHandle;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * @author jlajugie
 *
 */
public class LayeredTrack extends JPanel implements GenomeWindowListener, TrackListener, TrackEventsGenerator {


	private static final long 			serialVersionUID = 818958034840761257L;	// generated ID
	private static final int  			SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private int 						defaultHeight;							// default height of a track
	private TrackHandle					trackHandle;							// handle of the track
	private List<TrackListener> 		trackListeners;							// list of track listeners
	private JPanel						trackGraphics;							// graphics part of the track
	private String						name;									// name of the track
	private String 						genomeName;								// genome on which the track is based (ie aligned on)
	private	FontMetrics 				fontMetrics; 							// FontMetrics to get the size of a string
	private final TrackLayer<Void>		backgroundLayer;						// background layer of the track (layer with the vertical and horizontal lines)
	private List<TrackLayer<?>> 		layers;									// layers of the track
	private final TrackLayer<Void> 		foregroundLayer;						// foreground layer of the track (layer with the track name and the multi genome legend)


	/**
	 * Creates an instance of {@link LayeredTrack}
	 * @param trackNumber number of the track
	 */
	protected LayeredTrack(int trackNumber) {
		// create handle
		trackHandle = new TrackHandle(trackNumber);

		// register itself as a track listener to the track handle
		trackHandle.addTrackListener(this);

		// create the track graphics panel
		trackGraphics = new LayeredTrackGraphics(this);

		// create list of track listener
		trackListeners = new ArrayList<TrackListener>();

		// register itself as a genome window listener to the genome window manager
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		projectWindow.addGenomeWindowListener(this);

		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(trackHandle, gbc);

		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(trackGraphics, gbc);

		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BLACK));

		// set the the default height of the track
		defaultHeight = LayeredTrackConstants.TRACK_HEIGHT;
		setPreferredHeight(defaultHeight);

		// Set the font of the project
		fontMetrics = getFontMetrics(new Font(LayeredTrackConstants.FONT_NAME, Font.PLAIN, LayeredTrackConstants.FONT_SIZE));

		// initializes the layer list
		layers = new ArrayList<TrackLayer<?>>();
	}


	/**
	 * Sets the preferred Height of the track
	 * @param newPreferredHeight new preferred height
	 */
	public void setPreferredHeight(int newPreferredHeight) {
		setPreferredSize(new Dimension(getPreferredSize().width, newPreferredHeight));
		this.defaultHeight = newPreferredHeight;
		revalidate();
	}


	/**
	 * @return the preferred height
	 */
	public int getPreferredHeight() {
		return getPreferredSize().height;
	}


	/**
	 * Create a deep Copy of the track
	 * @return a copy of the track
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Track<?> deepClone() throws IOException, ClassNotFoundException {
		// we save in a local variable and then remove the listeners
		// before cloning the track in order to avoid cloning the listeners
		TrackListener[] tlSaver = getTrackListeners();
		for (TrackListener curList: tlSaver)	{
			removeTrackListener(curList);
		}
		// we remove the listeners of the track handle as well
		TrackListener[] trackHandleTlSaver = trackHandle.getTrackListeners();
		for (TrackListener curList: trackHandleTlSaver)	{
			trackHandle.removeTrackListener(curList);
		}
		// we remove listeners from the genome window manager
		ProjectManager.getInstance().getProjectWindow().removeGenomeWindowListener(this);

		// we clone the object
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);


		// we restore the listeners
		for (TrackListener curList: tlSaver)	{
			addTrackListener(curList);
		}
		for (TrackListener curList: trackHandleTlSaver) {
			trackHandle.addTrackListener(curList);
		}

		// register itself as a genome window listener to the genome window manager
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		projectWindow.addGenomeWindowListener(this);

		return (Track<?>) ois.readObject();
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


	@Override
	public void trackChanged(TrackEvent evt) {
		if (evt.getEventType() == TrackEventType.RESIZED) {
			int newHeight = getPreferredSize().height + trackHandle.getResizeHeight();
			// we don't want the new height to be smaller than TRACK_MINIMUM_HEIGHT
			newHeight = Math.max(LayeredTrackConstants.TRACK_MINIMUM_HEIGHT, newHeight);
			setPreferredSize(new Dimension(getPreferredSize().width, newHeight));
			revalidate();
		} else if (evt.getEventType() == TrackEventType.SIZE_SET_TO_DEFAULT) {
			setPreferredSize(new Dimension(getPreferredSize().width, defaultHeight));
			revalidate();
		} else {
			// we relay the other events to the element that contains this track
			notifyTrackListeners(evt.getEventType());
		}
	}


	/**
	 * Notifies all the track listeners that the track has changed
	 * @param trackEventType track event type
	 */
	public void notifyTrackListeners(TrackEventType trackEventType) {
		TrackEvent trackEvent = new TrackEvent(this, trackEventType);
		for (TrackListener listener: trackListeners) {
			listener.trackChanged(trackEvent);
		}
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// TODO Auto-generated method stub
	}

	/**
	 * Renames the track
	 * @param name name to set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the name of the track
	 */
	@Override
	public String getName() {
		if (this.name != null) {
			return this.name;
		} else if (trackHandle != null) {
			return new String("Track #" + trackHandle.getTrackNumber());
		} else {
			return super.getName();
		}
	}


	/**
	 * Sets the list of track layers 
	 * @param layers a list of track layers
	 */
	public void setLayers(List<TrackLayer<?>> layers) {
		this.layers = layers;
	}


	/**
	 * @return all the layers of the track
	 */
	public List<TrackLayer<?>> getLayers() {
		return layers;
	}
}
