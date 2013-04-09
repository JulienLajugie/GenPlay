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
import javax.swing.SwingUtilities;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * Panel that defines mouse listeners that modifies the genomic position.
 * A list of drawers can be registered an instance of this class in order
 * to draw on the {@link Graphics} context of this panel.
 * The first elements of the list of drawers draw first.
 * @author Julien Lajugie
 */
public class GraphicsPanel extends JPanel implements Serializable, ComponentListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long 	serialVersionUID = -1133983320644609161L;	// generated ID
	private transient int 		mouseStartDragX = -1;						// position of the mouse when start dragging
	private Drawer[]			drawers;									// drawers that can draw on this panel


	/**
	 * Creates an instance of {@link GraphicsPanel}
	 */
	public GraphicsPanel() {
		this(null);
	}


	/**
	 * Creates an instance of {@link GraphicsPanel}
	 * @param drawers drawers that can drawn on this panel
	 */
	public GraphicsPanel(Drawer[] drawers) {
		super();
		this.drawers = drawers;
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
		// tells the project window manager that the track width changed
		ProjectManager.getInstance().getProjectWindow().setTrackWidth(getWidth());
		repaint();
	}


	@Override
	public void componentShown(ComponentEvent e) {}


	/**
	 * @return the drawers registered to this panel
	 */
	public Drawer[] getDrawer() {
		return drawers;
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		ScrollingManager scrollingManager = ScrollingManager.getInstance();
		// double click on left button -> we center the track to the position where the double click occured
		if (!scrollingManager.isScrollingEnabled() && SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 2)) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// distance between the cursor and the middle of the track
			int screenWidth = e.getX() - (getWidth() / 2);
			// compute the corresponding genomic distance
			int genomeWidth = projectWindow.screenToGenomeWidth(screenWidth);
			Chromosome chromo = projectWindow.getGenomeWindow().getChromosome();
			int start = projectWindow.getGenomeWindow().getStart()+ genomeWidth;
			int stop = projectWindow.getGenomeWindow().getStop() + genomeWidth;
			SimpleGenomeWindow newWindow = new SimpleGenomeWindow(chromo, start, stop);
			if (((newWindow.getMiddlePosition()) >= 0) && (newWindow.getMiddlePosition() <= newWindow.getChromosome().getLength())) {
				projectWindow.setGenomeWindow(newWindow);
			}
		} else if (SwingUtilities.isMiddleMouseButton(e)) { // wheel click -> we toggle the scrolling mode
			scrollingManager.setScrollingEnabled(!scrollingManager.isScrollingEnabled());
			if (scrollingManager.isScrollingEnabled()) {
				// width between the cursor and the middle of the track
				int screenWidth = e.getX() - (getWidth() / 2);
				scrollingManager.setScrollingIntensity(screenWidth);
			}
			updateCursor();
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// mouse dragged -> we scroll the track
		if (SwingUtilities.isLeftMouseButton(e)) {
			ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
			// distance between the current position of the cursor and the position where the dragging started
			int screenWidth = mouseStartDragX - e.getX();
			// compute the corresponding genomic distance
			double genomeWidth = projectWindow.screenToGenomeWidth(screenWidth);
			if ((genomeWidth >= 1) || (genomeWidth <= -1)) {
				Chromosome chromo = projectWindow.getGenomeWindow().getChromosome();
				int start = projectWindow.getGenomeWindow().getStart()+ (int) genomeWidth;
				int stop = projectWindow.getGenomeWindow().getStop() + (int) genomeWidth;
				SimpleGenomeWindow newWindow = new SimpleGenomeWindow(chromo, start, stop);
				if (newWindow.getMiddlePosition() < 0) {
					start = -projectWindow.getGenomeWindow().getSize() / 2;
					stop = newWindow.getStart() + projectWindow.getGenomeWindow().getSize();
					newWindow = new SimpleGenomeWindow(chromo, start, stop);
				} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
					stop = newWindow.getChromosome().getLength() + (projectWindow.getGenomeWindow().getSize() / 2);
					start = newWindow.getStop() - projectWindow.getGenomeWindow().getSize();
					newWindow = new SimpleGenomeWindow(chromo, start, stop);
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
		ScrollingManager scrollingManager = ScrollingManager.getInstance();
		if (scrollingManager.isScrollingEnabled()) {
			// width between the cursor and the middle of the track
			int screenWidth = e.getX() - (getWidth() / 2);
			scrollingManager.setScrollingIntensity(screenWidth);
			updateCursor();
		}
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// we memorize the position of the mouse in case the user drag the track
		if (SwingUtilities.isLeftMouseButton(e)) {
			mouseStartDragX = e.getX();
		}
	}


	@Override
	public void mouseReleased(MouseEvent e) {}


	/**
	 * Update the {@link SimpleGenomeWindow} when the mouse wheel is used
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// mouse wheel rotated (without modifier) -> zoom in or out
		if (e.getModifiers() == 0) {
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
			Chromosome chromo = projectWindow.getGenomeWindow().getChromosome();
			int start =  (int)(projectWindow.getGenomeWindow().getMiddlePosition() - (newZoom / 2));
			int stop = start + newZoom;
			SimpleGenomeWindow newWindow = new SimpleGenomeWindow(chromo, start, stop);
			projectWindow.setGenomeWindow(newWindow);
		}
	}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (drawers != null) {
			// tell the drawers to draw
			for (Drawer currentDrawer: drawers) {
				g.setFont(TrackConstants.FONT_DEFAULT);
				currentDrawer.draw(g, getWidth(), getHeight());
			}
		}
	}


	/**
	 * Sets the drawers that will draw on this panel
	 * @param drawers drawers to set
	 */
	public void setDrawers(Drawer[] drawers) {
		this.drawers = drawers;
	}


	/**
	 * Sets the cursor of the track.
	 * The cursor is different from its default value when the track is being scrolled left or right
	 */
	public void updateCursor() {
		ScrollingManager scrollingManager = ScrollingManager.getInstance();
		if (scrollingManager.isScrollingLeft()) {
			setCursor(TrackConstants.SCROLL_LEFT_CURSOR);
		} else if (scrollingManager.isScrollingRight()) {
			setCursor(TrackConstants.SCROLL_RIGHT_CURSOR);
		} else {
			setCursor(TrackConstants.DEFAULT_CURSOR);
		}
	}
}
