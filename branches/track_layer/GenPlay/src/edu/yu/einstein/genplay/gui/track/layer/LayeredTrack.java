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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.gui.track.layer.background.TrackBackgroundData;
import edu.yu.einstein.genplay.gui.track.layer.background.TrackBackgroundLayer;
import edu.yu.einstein.genplay.gui.track.layer.foreground.TrackForegroundData;
import edu.yu.einstein.genplay.gui.track.layer.foreground.TrackForegroundLayer;

/**
 * Track is a component showing the data in GenPlay.
 * The panel can be retrieve with the getPanel method.
 * A track contains two elements: the track handle and the graphics panel showing the data
 * @author Julien Lajugie
 */
public class LayeredTrack implements Serializable, GenomeWindowListener, TrackListener, TrackEventsGenerator {

	private static final long 						serialVersionUID = 818958034840761257L;	// generated ID
	private static final int  						SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private static int 								graphicsWidth;							// with of the track graphics (static because all track should have the same width in a project)
	private final TrackLayer<TrackBackgroundData> 	backgroundLayer;						// background layer of the track (with the vertical and horizontal lines)
	private final TrackLayer<TrackForegroundData> 	foregroundLayer;						// foreground layer of the track (with the track name and the multi genome legend)
	private final LayeredTrackScore					score;									// score of the track
	private	final FontMetrics						fontMetrics; 							// FontMetrics to get the size of a string	
	private int										height;									// height of the track
	private int 									defaultHeight;							// default height of a track
	private int										number;									// number of the track
	private String									name;									// name of the track
	private List<TrackListener> 					trackListeners;							// list of track listeners
	private List<TrackLayer<?>> 					layers;									// layers of the track
	private TrackLayer<?>							activeLayer;							// active layer of the track
	private transient TrackHandle					handlePanel;							// handle panel of the track
	private transient TrackGraphics					graphicsPanel;							// graphics panel of the track
	private transient JPanel						trackPanel;								// panel of the track containing the handle and graphics panel


	/**
	 * @return the width of the graphics of the tracks.
	 * This method is static because all tracks have the same length
	 */
	public static int getGraphicsWidth() {
		return graphicsWidth;
	}


	/**
	 * Sets the graphics width and update the project X factor if needed
	 * @param graphicsWidth
	 */
	protected static void setGraphicsWidth(int graphicsWidth) {
		LayeredTrack.graphicsWidth = graphicsWidth;
	}


	/**
	 * Update the project xFactor if needed. 
	 * The xFactor is the ratio between the track width on the screen and the number of genomic position to display
	 */
	protected static void updateXFactor() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double newXFactor = projectWindow.getXFactor(getGraphicsWidth());
		if (newXFactor != projectWindow.getXFactor()) {
			projectWindow.setXFactor(newXFactor);
		}
	}


	/**
	 * Creates an instance of {@link LayeredTrack}
	 * @param trackNumber number of the track
	 */
	public LayeredTrack(int trackNumber) {
		// creates the panels
		handlePanel = new TrackHandle(trackNumber);
		handlePanel.addTrackListener(this);
		graphicsPanel = new TrackGraphics(this);
		trackPanel = createTrackPanel();

		// initializes the foreground and background drawer
		backgroundLayer = new TrackBackgroundLayer(this);
		foregroundLayer = new TrackForegroundLayer(this);

		// set the the default height of the track
		setDefaultHeight(LayeredTrackConstants.TRACK_HEIGHT);
		setHeight(LayeredTrackConstants.TRACK_HEIGHT);

		setName(null);
		setNumber(trackNumber);
		score = new LayeredTrackScore(this);

		// Set the font of the project
		fontMetrics = trackPanel.getFontMetrics(new Font(LayeredTrackConstants.FONT_NAME, Font.PLAIN, LayeredTrackConstants.FONT_SIZE));

		// create list of track listener
		trackListeners = new ArrayList<TrackListener>();

		// initializes the layer list
		setLayers(new ArrayList<TrackLayer<?>>());

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
	 * @return the track panel
	 */
	private JPanel createTrackPanel() {
		JPanel trackPanelt = new JPanel();
		BorderLayout layout = new BorderLayout();
		trackPanelt.setLayout(layout);
		trackPanelt.add(handlePanel, BorderLayout.LINE_START);
		trackPanelt.add(graphicsPanel, BorderLayout.CENTER);
		trackPanelt.setBorder(LayeredTrackConstants.REGULAR_BORDER);
		return trackPanelt;
	}


	/**
	 * Create a deep Copy of the track
	 * @return a copy of the track
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public LayeredTrack deepClone() throws IOException, ClassNotFoundException {
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

		return (LayeredTrack) ois.readObject();
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// repaint the layers if the genome window changed
		drawLayers(graphicsPanel.getGraphics());
	}


	/**
	 * @return the active track layer
	 */
	public TrackLayer<?> getActiveLayer() {
		return activeLayer;
	}



	/**
	 * @return the background layer of the track
	 */
	public TrackLayer<TrackBackgroundData> getBackgroundLayer() {
		return backgroundLayer;
	}


	/**
	 * @return the default height of the track
	 */
	public int getDefaultHeight() {
		return defaultHeight;
	}


	/**
	 * @return the font metrics of the track
	 */
	public FontMetrics getFontMetrics() {
		return fontMetrics;
	}


	/**
	 * @return the foreground layer of the track
	 */
	public TrackLayer<TrackForegroundData> getForegroundLayer() {
		return foregroundLayer;
	}


	/**
	 * @return the height of the track
	 */
	public int getHeight() {
		return height;
	}


	/**
	 * @return all the layers of the track
	 */
	public List<TrackLayer<?>> getLayers() {
		return layers;
	}


	/**
	 * @return the name of the track
	 */
	public String getName() {
		if (name != null) {
			return name;
		} else {
			return new String("Track #" + getNumber());
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
	public LayeredTrackScore getScore() {
		return score;
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	/**
	 * @return the panel containing the track
	 */
	public JPanel getPanel() {
		return trackPanel;
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


	/**
	 * Draws the track
	 */
	protected void drawLayers(Graphics g) {
		// draw the track background
		backgroundLayer.drawLayer(g);
		// draw the list of layers
		List<TrackLayer<?>> layers = getLayers();
		for (TrackLayer<?> currentLayer: layers) {
			currentLayer.drawLayer(g);
		}
		// draw the foreground
		foregroundLayer.drawLayer(g);
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}


	/**
	 * Saves the track graphics as a PNG image
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(graphicsPanel.getWidth(), graphicsPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		graphicsPanel.paint(g);
		try {
			ImageIO.write(image, "PNG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(graphicsPanel.getRootPane(), e, "Error while saving the tracks as an image");
		}
	}


	/**
	 * Sets the active layer of the track
	 * @param activeLayer the active layer to set
	 */
	public void setActiveLayer(TrackLayer<?> activeLayer) {
		this.activeLayer = activeLayer;
		getScore().updateCurrentScore();
	}


	/**
	 * Sets the default height of the track
	 * @param defaultHeight default height to set
	 */
	public void setDefaultHeight(int defaultHeight) {
		this.defaultHeight = defaultHeight;
	}


	/**
	 * Sets the height of the track
	 * @param height height to set
	 */
	public void setHeight(int height) {
		this.height = height;
		// update the dimension of the track panel
		Dimension trackDimension = new Dimension(trackPanel.getPreferredSize().width, height);
		trackPanel.setPreferredSize(trackDimension);
		trackPanel.revalidate();
	}


	/**
	 * Sets the list of track layers 
	 * @param layers a list of track layers
	 */
	public void setLayers(List<TrackLayer<?>> layers) {
		this.layers = layers;
		getScore().updateCurrentScore();
	}


	/**
	 * Renames the track
	 * @param name name to set
	 */
	public void setName(String name) {
		this.name = name;
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
			setHeight(handlePanel.getNewHeight());
		} else if (evt.getEventType() == TrackEventType.SIZE_SET_TO_DEFAULT) { // size set to default event
			setHeight(getDefaultHeight());
		} else { // other event
			// we relay the other events to the element that contains this track
			notifyTrackListeners(evt.getEventType());
		}
	}
}
