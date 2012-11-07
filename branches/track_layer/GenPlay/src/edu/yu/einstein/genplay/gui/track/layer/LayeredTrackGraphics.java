package edu.yu.einstein.genplay.gui.track.layer;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;

public class LayeredTrackGraphics extends JPanel {
	
	private final LayeredTrack track;
	
	
	public LayeredTrackGraphics(LayeredTrack track) {
		this.track = track;
	}


	/**
	 * Sets the variable xFactor
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		updateXFactor();
		// we draw the list of layers
		List<TrackLayer<?>> layers = track.getLayersToPaint();
		for (TrackLayer<?> currentLayer: layers) {
			currentLayer.draw();
		}
	}


	/**
	 * e
	 */
	private void updateXFactor() {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double newXFactor = projectWindow.getXFactor(getWidth());
		if (newXFactor != projectWindow.getXFactor()) {
			projectWindow.setXFactor(newXFactor);
			// if the x factor changed we should update the intensity of the scrolling mode
			int intensity = computeScrollIntensity(getMousePosition().x);
			LayeredTrackScrollingManager.getInstance().setScrollingIntensity(intensity);;
		}
	}




	/**
	 * @param mouseXPosition X position of the mouse on the track
	 * @return an intensity for the track scrolling. 
	 * The intensity depends on the distance between the mouse cursor and the horizontal center of the track graphics
	 */
	private int computeScrollIntensity(int mouseXPosition) {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		int width = getWidth();
		double res = projectWindow.twoScreenPosToGenomeWidth(width, mouseXPosition, width / 2);
		if (res > 0) {
			return (int) (res / 10d) + 1;
		} else {
			return (int) (res / 10d) - 1;
		}
	}


	/**
	 * Save the track graphics as a PNG image.
	 * @param file output file
	 */
	public void saveAsImage(File file) {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {
			ImageIO.write(image, "PNG", file);
		}catch(Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving the tracks as an image");
		}
	}
}
