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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
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
public class Track extends JPanel implements Serializable, GenomeWindowListener, TrackListener, TrackEventsGenerator, ListDataListener {

	private static final long 				serialVersionUID = 818958034840761257L;	// generated ID
	private static final int  				SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private final Layer<BackgroundData> 	backgroundLayer;						// background layer of the track (with the vertical and horizontal lines)
	private final Layer<ForegroundData> 	foregroundLayer;						// foreground layer of the track (with the track name and the multi genome legend)
	private final TrackScore				score;									// score of the track
	private int 							defaultHeight;							// default height of a track
	private int								number;									// number of the track
	private List<TrackListener> 			trackListeners;							// list of track listeners
	private TrackModel 						layers;									// layers of the track
	private Layer<?>						activeLayer;							// active layer of the track
	private transient HandlePanel			handlePanel;							// handle panel of the track
	private transient GraphicsPanel			graphicsPanel;							// graphics panel of the track


	/**
	 * Creates an instance of {@link Track}
	 * @param trackNumber number of the track
	 */
	public Track(int trackNumber) {
		super();

		// creates the panels
		handlePanel = new HandlePanel(trackNumber);
		handlePanel.addTrackListener(this);
		graphicsPanel = new GraphicsPanel();

		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		add(handlePanel, BorderLayout.LINE_START);
		add(graphicsPanel, BorderLayout.CENTER);

		// initializes the foreground and background drawer
		backgroundLayer = new BackgroundLayer(this);
		foregroundLayer = new ForegroundLayer(this);
		// we update the list of drawers registered to the graphics panel
		updateGraphicsPanelDrawers();

		// set the the default height of the track
		ProjectConfiguration projectConfiguration = ProjectManager.getInstance().getProjectConfiguration();
		int defaultHeight = projectConfiguration.getTrackHeight();
		setDefaultHeight(defaultHeight);
		setPreferredHeight(defaultHeight);

		setName(null);
		setNumber(trackNumber);
		score = new TrackScore(this);

		// Set the font of the project
		setFont(TrackConstants.FONT);

		// Set the border of the track
		setBorder(TrackConstants.REGULAR_BORDER);

		// create list of track listener
		trackListeners = new ArrayList<TrackListener>();

		// initializes the layer list
		layers = new TrackModel();
		layers.addListDataListener(this);

		// register itself as a genome window listener to the genome window manager
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		projectWindow.addGenomeWindowListener(this);
	}


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	/**
	 * Sets if the track is selected or not
	 * @param isSelected true if the track is selected, false otherwise
	 */
	public void setSelected(boolean isSelected) {
		// a track is selected if its handle is selected
		handlePanel.setSelected(isSelected);
	}


	/**
	 * @return true if the track is selected, false otherwise
	 */
	public boolean isSelected() {
		// a track is selected if its handle is selected
		return handlePanel.isSelected();
	}


	/**
	 * Create a deep Copy of the track
	 * @return a copy of the track
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public Track deepClone() throws IOException, ClassNotFoundException {
		// we save in a local variable and then remove the listeners
		// before cloning the track in order to avoid cloning the listeners
		TrackListener[] tlSaver = getTrackListeners();
		for (TrackListener curList: tlSaver)	{
			removeTrackListener(curList);
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

		// register itself as a genome window listener to the genome window manager
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		projectWindow.addGenomeWindowListener(this);

		return (Track) ois.readObject();
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
	 * @return the panel where the layers are drawn
	 */
	public JPanel getGraphicsPanel() {
		return graphicsPanel;
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
			return new String(TrackConstants.TRACK_NAME_PREFIX + getNumber());
		}
	}


	/**
	 * @return the number of the track
	 */
	public int getNumber() {
		return number;
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
		String defaultName = TrackConstants.TRACK_NAME_PREFIX + getNumber();
		// we don't set the name if it's the default track name
		if ((name != null) && !name.equals(defaultName)) {
			super.setName(name);
		}
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
	 * Sets the number of the track
	 * @param number number to set
	 */
	public void setNumber(int number) {
		this.number = number;
		this.handlePanel.setNumber(number);
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
			for (Drawer currentDrawer: layers) {
				drawers[i] = currentDrawer;
				i++;
			}
			drawers[i] = foregroundLayer;
			graphicsPanel.setDrawers(drawers);
		}
	}


	/**
	 * Locks the track handle
	 */
	public void lockHandle() {
		handlePanel.setEnabled(false);
	}


	/**
	 * Unlocks the track handle
	 */
	public void unlockHandle() {
		handlePanel.setEnabled(true);
	}


	@Override
	public void contentsChanged(ListDataEvent e) {
		// case where the active layer was removed
		if (!layers.contains(activeLayer)) {
			activeLayer = null;
		}
		updateGraphicsPanelDrawers();
		getScore().autorescaleScoreAxis();
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
			activeLayer = null;
		}
		updateGraphicsPanelDrawers();
		getScore().autorescaleScoreAxis();
	}
}
