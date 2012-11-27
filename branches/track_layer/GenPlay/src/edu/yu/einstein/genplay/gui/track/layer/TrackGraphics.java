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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.Serializable;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Graphics part of a track.
 * A track is composed of a {@link TrackHandle} and a {@link TrackGraphics}
 * @author Julien Lajugie
 */
public class TrackGraphics extends JPanel implements Serializable, ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long 	serialVersionUID = -1133983320644609161L;	// generated ID
	private final LayeredTrack 	track; 										// track showing this track graphics
	transient private int 		mouseStartDragX = -1;						// position of the mouse when start dragging


	/**
	 * Creates an instance of {@link TrackGraphics}
	 * @param track
	 */
	public TrackGraphics(LayeredTrack track) {
		super();
		this.track = track;
		setFont(new Font(LayeredTrackConstants.FONT_NAME, Font.PLAIN, LayeredTrackConstants.FONT_SIZE));
		setBackground(Colors.TRACK_BACKGROUND);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
	}


	@Override
	public void componentHidden(ComponentEvent e) {}


	@Override
	public void componentMoved(ComponentEvent e) {}


	@Override
	public void componentResized(ComponentEvent e) {
		// update the trackWidth and the X factor if needed
		if (getWidth() != LayeredTrack.getGraphicsWidth()) {
			LayeredTrack.setGraphicsWidth(getWidth());
			LayeredTrack.updateXFactor();
		}
		repaint();
	}


	@Override
	public void componentShown(ComponentEvent e) {}


	@Override
	public void mouseClicked(MouseEvent e) {
		LayeredTrackScrollingManager scrollingManager = LayeredTrackScrollingManager.getInstance();
		// double click on left button -> we center the track to the position where the double click occured
		if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2) && (!scrollingManager.isScrollingEnabled())) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// Compute the distance from the cursor to the center of the screen
			double distance = projectWindow.twoScreenPosToGenomeWidth(getWidth(), getWidth() / 2, e.getX());
			distance = Math.floor(distance);
			GenomeWindow newWindow = new GenomeWindow();
			newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
			newWindow.setStart(projectWindow.getGenomeWindow().getStart()+ (int) distance);
			newWindow.setStop(projectWindow.getGenomeWindow().getStop() + (int) distance);
			if (((newWindow.getMiddlePosition()) >= 0) && (newWindow.getMiddlePosition() <= newWindow.getChromosome().getLength())) {
				projectWindow.setGenomeWindow(newWindow);
			}
		}
		// wheel click -> we toggle the scrolling mode
		if (e.getButton() == MouseEvent.BUTTON2) {
			scrollingManager.setScrollingEnabled(!scrollingManager.isScrollingEnabled());
			if (scrollingManager.isScrollingEnabled()) {
				scrollingManager.setScrollingIntensity(e.getX());
			}
			updateCursor();
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// mouse dragged -> we scroll the track
		if (e.getModifiers() == MouseEvent.BUTTON1) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			double distance = projectWindow.twoScreenPosToGenomeWidth(getWidth(), e.getX(), mouseStartDragX);
			if ((distance > 1) || (distance < -1)) {
				GenomeWindow newWindow = new GenomeWindow();
				newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
				newWindow.setStart(projectWindow.getGenomeWindow().getStart()+ (int) distance);
				newWindow.setStop(projectWindow.getGenomeWindow().getStop() + (int) distance);
				if (newWindow.getMiddlePosition() < 0) {
					newWindow.setStart(-projectWindow.getGenomeWindow().getSize() / 2);
					newWindow.setStop(newWindow.getStart() + projectWindow.getGenomeWindow().getSize());
				} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
					newWindow.setStop(newWindow.getChromosome().getLength() + (projectWindow.getGenomeWindow().getSize() / 2));
					newWindow.setStart(newWindow.getStop() - projectWindow.getGenomeWindow().getSize());
				}
				projectWindow.setGenomeWindow(newWindow);
				mouseStartDragX = e.getX();
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// set the cursor when the mouse enter a track
		updateCursor();
	}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mouseMoved(MouseEvent e) {
		// if the scrolling mode is enable we update the intensity of the scrolling 
		// depending on how far the mouse is from the center of the track 
		LayeredTrackScrollingManager scrollingManager = LayeredTrackScrollingManager.getInstance();
		if (scrollingManager.isScrollingEnabled()) {
			scrollingManager.setScrollingIntensity(e.getX());
			updateCursor();
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// we memorize the position of the mouse in case the user drag the track
		if (e.getModifiers() == MouseEvent.BUTTON1) {
			mouseStartDragX = e.getX();
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Update the {@link GenomeWindow} when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// mouse wheel rotated -> zoom in or out
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		ProjectZoom projectZoom = ProjectManager.getInstance().getProjectZoom();
		int newZoom = 0;
		int weelRotation = Math.abs(e.getWheelRotation());
		boolean isZoomIn = e.getWheelRotation() > 0;
		for (int i = 0; i < weelRotation; i++) {
			if (isZoomIn) {
				newZoom = projectZoom.getNextZoomIn(projectWindow.getGenomeWindow().getSize());
			} else {
				newZoom = projectZoom.getNextZoomOut(projectWindow.getGenomeWindow().getSize());
			}
			newZoom = Math.min(projectWindow.getGenomeWindow().getChromosome().getLength() * 2, newZoom);
		}
		GenomeWindow newWindow = new GenomeWindow();
		newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
		newWindow.setStart((int)(projectWindow.getGenomeWindow().getMiddlePosition() - (newZoom / 2)));
		newWindow.setStop(newWindow.getStart() + newZoom);
		projectWindow.setGenomeWindow(newWindow);
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// tell the track to repaint the layers
		track.drawLayers(g);
	}


	/**
	 * Sets the cursor of the track.
	 * The cursor is different from its default value when the track is being scrolled left or right
	 */
	private void updateCursor() {
		LayeredTrackScrollingManager scrollingManager = LayeredTrackScrollingManager.getInstance();
		if (scrollingManager.isScrollingLeft()) {
			setCursor(LayeredTrackConstants.SCROLL_LEFT_CURSOR);
		} else if (scrollingManager.isScrollingRight()) {
			setCursor(LayeredTrackConstants.SCROLL_RIGHT_CURSOR);
		} else {
			setCursor(LayeredTrackConstants.DEFAULT_CURSOR);
		}
	}
}
