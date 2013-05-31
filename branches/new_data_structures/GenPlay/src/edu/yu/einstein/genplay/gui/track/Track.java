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
package edu.yu.einstein.genplay.gui.track;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectConfiguration;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.background.BackgroundData;
import edu.yu.einstein.genplay.gui.track.layer.background.BackgroundLayer;
import edu.yu.einstein.genplay.gui.track.layer.foreground.ForegroundData;
import edu.yu.einstein.genplay.gui.track.layer.foreground.ForegroundLayer;

/**
 * Track component showing the data in GenPlay.
 * A track contains two subcomponents: the track handle and the graphics panel showing the data
 * @author Julien Lajugie
 */
public final class Track extends JPanel implements Serializable, GenomeWindowListener, TrackListener, TrackEventsGenerator, ListDataListener {

	private static final long 				serialVersionUID = 818958034840761257L;	// generated ID
	private static final int  				SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private transient List<TrackListener>	trackListeners;							// list of track listeners
	private transient int 					defaultHeight;							// default height of a track
	private int								trackNumber;							// number of the track
	private HandlePanel						handlePanel;							// handle panel of the track
	private GraphicsPanel					graphicsPanel;							// graphics panel of the track
	private BackgroundLayer					backgroundLayer;						// background layer of the track (with the vertical and horizontal lines)
	private ForegroundLayer 				foregroundLayer;						// foreground layer of the track (with the track name and the multi genome legend)
	private TrackModel 						layers;									// layers of the track
	private Layer<?>						activeLayer;							// active layer of the track
	private TrackScore						score;									// score of the track


	/**
	 * Creates an instance of {@link Track}
	 * @param trackNumber number of the track
	 */
	public Track(int trackNumber) {
		super();

		setMinimumSize(new Dimension(getMinimumSize().width, TrackConstants.MINIMUM_HEIGHT));
		setMaximumSize(new Dimension(getMaximumSize().width, TrackConstants.MAXIMUM_HEIGHT));

		// create the panels
		handlePanel = new HandlePanel(trackNumber);
		graphicsPanel = new GraphicsPanel();

		// add the panels
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(handlePanel, BorderLayout.LINE_START);
		add(graphicsPanel, BorderLayout.CENTER);

		// sets the track number
		setNumber(trackNumber);

		// initializes the foreground and background drawer
		backgroundLayer = new BackgroundLayer(this);
		foregroundLayer = new ForegroundLayer(this);

		// we update the list of drawers registered to the graphics panel
		updateGraphicsPanelDrawers();

		// initialize the track
		init();
	}


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	@Override
	public void contentsChanged(ListDataEvent e) {
		// case where the active layer was removed
		if (!layers.contains(activeLayer)) {
			setActiveLayer(null);
		}
		if ((activeLayer == null) && !layers.isEmpty()) {
			setActiveLayer(layers.getLayers()[0]);
		}
		updateGraphicsPanelDrawers();
		getScore().autorescaleScoreAxis();
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// repaint the layers if the genome window changed
		graphicsPanel.repaint();
	}


	/**
	 * @return the active track layer
	 */
	public Layer<?> getActiveLayer() {
		return activeLayer;
	}


	/**
	 * @return the background layer of the track
	 */
	public Layer<BackgroundData> getBackgroundLayer() {
		return backgroundLayer;
	}


	/**
	 * @return the default height of the track
	 */
	public int getDefaultHeight() {
		return defaultHeight;
	}



	/**
	 * @return the foreground layer of the track
	 */
	public Layer<ForegroundData> getForegroundLayer() {
		return foregroundLayer;
	}


	/**
	 * @return the panel where the layers are drawn
	 */
	public GraphicsPanel getGraphicsPanel() {
		return graphicsPanel;
	}


	/**
	 * @return an image of the track (without its handle)
	 */
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(graphicsPanel.getWidth(), graphicsPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.createGraphics();
		graphicsPanel.paint(g);
		return image;
	}


	/**
	 * @return the {@link TrackModel} managing the list of layers of the track
	 */
	public TrackModel getLayers() {
		return layers;
	}


	/**
	 * @return the name of the track
	 */
	@Override
	public String getName() {
		if (super.getName() != null) {
			return super.getName();
		} else {
			return new String(TrackConstants.NAME_PREFIX + getNumber());
		}
	}


	/**
	 * @return the number of the track
	 */
	public int getNumber() {
		return trackNumber;
	}


	/**
	 * @return the object that manages the score of the track
	 */
	public TrackScore getScore() {
		return score;
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	/**
	 * Initialize the track
	 * @param trackNumber number of the track
	 */
	private void init() {
		// register itself to the handle so the track can be notified when there is an action on the handle
		handlePanel.addTrackListener(this);

		// set the the default height of the track
		ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
		int defaultHeight = projectConfiguration.getTrackHeight();
		setDefaultHeight(defaultHeight);
		setPreferredHeight(defaultHeight);

		setName(null);

		// set the track score
		score = new TrackScore(this);

		// set the border of the track
		setBorder(TrackConstants.REGULAR_BORDER);

		// create list of track listener
		trackListeners = new ArrayList<TrackListener>();

		// initializes the layer list
		layers = new TrackModel();
		layers.addListDataListener(this);
	}


	@Override
	public void intervalAdded(ListDataEvent e) {
		updateGraphicsPanelDrawers();
		getScore().autorescaleScoreAxis();
	}


	@Override
	public void intervalRemoved(ListDataEvent e) {
		// case where the active layer was removed
		if (!layers.contains(activeLayer)) {
			setActiveLayer(null);
		}
		updateGraphicsPanelDrawers();
		getScore().autorescaleScoreAxis();
	}


	/**
	 * @return true if the track is selected, false otherwise
	 */
	public boolean isSelected() {
		// a track is selected if its handle is selected
		return handlePanel.isSelected();
	}


	/**
	 * Locks the track handle
	 */
	public void lockHandle() {
		handlePanel.setEnabled(false);
	}


	/**
	 * Notifies all the track listeners that the track has changed
	 * @param trackEventType track event type
	 */
	public void notifyTrackListeners(TrackEventType trackEventType) {
		if (trackListeners != null) {
			TrackEvent trackEvent = new TrackEvent(this, trackEventType);
			for (TrackListener listener: trackListeners) {
				listener.trackChanged(trackEvent);
			}
		}
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		trackNumber = in.readInt();
		handlePanel = (HandlePanel) in.readObject();
		graphicsPanel = (GraphicsPanel) in.readObject();
		// initialize the track
		init();
		// set the background and foreground layers
		backgroundLayer = (BackgroundLayer) in.readObject();
		backgroundLayer.setTrack(this);
		foregroundLayer = (ForegroundLayer) in.readObject();
		foregroundLayer.setTrack(this);
		// initialize the track
		// add the tracks
		int layerCount = in.readInt();
		if (layerCount > 0) {
			layers = (TrackModel) in.readObject();
			// since the track field of layers is transient we have to set it
			for (Layer<?> currentLayer: layers) {
				currentLayer.setTrack(this);
			}
			setActiveLayer((Layer<?>) in.readObject());
		}
		score = (TrackScore) in.readObject();
		updateGraphicsPanelDrawers();
		//ProjectManager.getInstance().getProjectWindow().setTrackWidth(graphicsPanel.getWidth());
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}


	/**
	 * Sets the active layer of the track if the specified layer is one of the layers of the track.
	 * Sets the active layer to null if the specified parameter is null.
	 * Does nothing if the specified layer is not null and is not one of the layers of the track
	 * @param activeLayer the active layer to set
	 */
	public void setActiveLayer(Layer<?> activeLayer) {
		Layer<?> oldLayer = getActiveLayer();
		if (activeLayer == null) {
			this.activeLayer = null;
		} else if (layers.contains(activeLayer)) {
			this.activeLayer = activeLayer;
		}
		// update the mouse listeners of the graphics panel
		if (oldLayer != activeLayer) {
			if (oldLayer != null) {
				if (oldLayer instanceof MouseMotionListener) {
					graphicsPanel.removeMouseMotionListener((MouseMotionListener)oldLayer);
				}
				if (oldLayer instanceof MouseListener) {
					graphicsPanel.removeMouseListener((MouseListener)oldLayer);
				}
				if (oldLayer instanceof MouseWheelListener) {
					graphicsPanel.removeMouseWheelListener((MouseWheelListener)oldLayer);
				}
			}
			if (activeLayer != null) {
				if (activeLayer instanceof MouseMotionListener) {
					graphicsPanel.addMouseMotionListener((MouseMotionListener)activeLayer);
				}
				if (activeLayer instanceof MouseListener) {
					graphicsPanel.addMouseListener((MouseListener)activeLayer);
				}
				if (activeLayer instanceof MouseWheelListener) {
					graphicsPanel.addMouseWheelListener((MouseWheelListener)activeLayer);
				}
			}
		}
	}


	/**
	 * Sets the default height of the track
	 * @param defaultHeight default height to set
	 */
	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}


	@Override
	public void setName(String name) {
		String defaultName = TrackConstants.NAME_PREFIX + getNumber();
		// we don't set the name if it's the default track name
		if ((name != null) && !name.equals(defaultName)) {
			super.setName(name);
		}
	}


	/**
	 * Sets the number of the track
	 * @param number number to set
	 */
	public void setNumber(int number) {
		trackNumber = number;
		handlePanel.setNumber(number);
	}


	/**
	 * Sets the height of the track
	 * @param preferredHeight preferred height to set
	 */
	public void setPreferredHeight(int preferredHeight) {
		// update the dimension of the track panel
		Dimension trackDimension = new Dimension(getPreferredSize().width, preferredHeight);
		setPreferredSize(trackDimension);
		revalidate();
	}


	/**
	 * Sets if the track is selected or not
	 * @param isSelected true if the track is selected, false otherwise
	 */
	public void setSelected(boolean isSelected) {
		// a track is selected if its handle is selected
		handlePanel.setSelected(isSelected);
	}


	@Override
	public String toString() {
		if (getName() != null) {
			return getName();
		} else {
			return super.toString();
		}
	}


	@Override
	public void trackChanged(TrackEvent evt) {
		if (evt.getEventType() == TrackEventType.RESIZED) { // resize event
			setPreferredHeight(handlePanel.getNewHeight());
		} else if (evt.getEventType() == TrackEventType.SIZE_SET_TO_DEFAULT) { // size set to default event
			setPreferredHeight(getDefaultHeight());
		} else { // other event
			// we relay the other events to the element that contains this track
			notifyTrackListeners(evt.getEventType());
		}
	}


	/**
	 * Unlocks the track handle
	 */
	public void unlockHandle() {
		handlePanel.setEnabled(true);
	}


	/**
	 * Updates the list of drawers registered to the graphics panel.
	 * There are one drawer per layer including the background and foreground layers
	 * that need to be registered.
	 */
	private void updateGraphicsPanelDrawers() {
		if (layers == null) { // case where there is no other layer than the foreground and the background
			Drawer[] drawers = {backgroundLayer, foregroundLayer};
			graphicsPanel.setDrawers(drawers);
		} else { // case where there are other layers
			Drawer[] drawers = new Drawer[layers.size() + 2];
			drawers[0] = backgroundLayer;
			int i = 1;
			// we want to draw the layers in reverse order because the first layers
			// of the list should be on top
			for (int j = layers.size() - 1; j >= 0; j--) {
				Drawer currentDrawer = layers.getLayers()[j];
				drawers[i] = currentDrawer;
				i++;
			}
			drawers[i] = foregroundLayer;
			graphicsPanel.setDrawers(drawers);
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeInt(trackNumber);
		out.writeObject(handlePanel);
		out.writeObject(graphicsPanel);
		out.writeObject(backgroundLayer);
		out.writeObject(foregroundLayer);
		// write the number of layers
		if ((layers == null) || layers.isEmpty()) {
			out.writeInt(0);
		} else {
			out.writeInt(layers.size());
			out.writeObject(layers);
			// make sure the active layer is not null
			if (activeLayer == null) {
				setActiveLayer(layers.getLayers()[0]);
			}
			out.writeObject(activeLayer);
		}
		out.writeObject(score);
	}
}