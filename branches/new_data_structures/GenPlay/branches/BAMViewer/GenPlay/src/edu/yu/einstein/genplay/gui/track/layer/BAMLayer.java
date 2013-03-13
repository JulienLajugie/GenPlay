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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMRecord;
import edu.yu.einstein.genplay.core.SAM.SAMContent;
import edu.yu.einstein.genplay.core.SAM.SAMFile;
import edu.yu.einstein.genplay.core.SAM.SAMRead;
import edu.yu.einstein.genplay.core.SAM.SAMSegment;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.LayerColors;


/**
 * Layer displaying a {@link SAMFile}
 * @author Nicolas Fourel
 */
public class BAMLayer extends AbstractLayer<SAMFile> implements ColoredLayer, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private static final int READ_HEIGHT = 15;
	private final ProjectWindow projectWindow;
	private Color		color;		// color of the layer
	private SAMContent samContent;
	private List<SAMRead> reads;
	private SAMRead readUnderMouse;

	private List<List<SAMRead>> currentReads;
	private List<Graphics> graphics;
	private GenomeWindow genomeWindow;


	/**
	 * Creates an instance of a {@link BAMLayer}
	 * @param track track containing the layer
	 * @param data data of the layer
	 * @param name name of the layer
	 */
	public BAMLayer(Track track, SAMFile data, String name) {
		super(track, data, name);
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		color = LayerColors.getLayerColor();
		initializeContent();
	}


	/**
	 * Initialize the {@link SAMContent} and {@link SAMRead} to display
	 */
	private void initializeContent () {
		samContent = new SAMContent(getData());
		Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		samContent.load(chromosome, 0, chromosome.getLength());
		getTrack().repaint();
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			currentReads = new ArrayList<List<SAMRead>>();
			graphics = new ArrayList<Graphics>();
			genomeWindow = projectWindow.getGenomeWindow();
			reads = samContent.getReads(genomeWindow.getStart(), genomeWindow.getStop());
			for (SAMRead read: reads) {
				int currentIndex = getCurrentIndex(read);
				setCurrentInformation(currentIndex, g, read);
				if (isReadVisible(currentIndex, height)) {
					drawRead(currentIndex);
				}
			}
		}
	}


	/**
	 * @param read a {@link SAMRead}
	 * @return the current index level of the read
	 */
	private int getCurrentIndex (SAMRead read) {
		int index = -1;
		int size = currentReads.size();
		if (size == 0) {
			index = 0;
		} else {
			for (int i = 0; i < size; i++) {
				List<SAMRead> currentList = currentReads.get(i);
				int lastIndex = Math.max(0, currentList.size() - 1);
				SAMRead currentRead = currentList.get(lastIndex);
				if (read.getStart() >= currentRead.getFullAlignementStop()) {
					index = i;
					break;
				}
			}
			if (index == -1) {
				index = size;
			}
		}
		return index;
	}


	/**
	 * Update the current information, the list of {@link SAMRead} and the the {@link Graphics}
	 * @param index the index level
	 * @param g the graphics
	 * @param read the {@link SAMRead}
	 */
	private void setCurrentInformation (int index, Graphics g, SAMRead read) {
		int y = getYGraphic(index);
		Graphics currentGraphic = g.create(0, y, g.getClipBounds().width, READ_HEIGHT);
		if (index >= currentReads.size()) {
			currentReads.add(new ArrayList<SAMRead>());
			graphics.add(currentGraphic);
		}
		currentReads.get(index).add(read);
	}


	/**
	 * Draw a {@link SAMRead} on the given index level
	 * @param index the index level
	 */
	private void drawRead (int index) {
		List<SAMRead> currentList = currentReads.get(index);
		int lastIndex = Math.max(0, currentList.size() - 1);
		SAMRead read = currentList.get(lastIndex);
		Graphics currentGraphics = graphics.get(index);
		drawRead(currentGraphics, read);
		if (read.isPaired()) {
			drawRead(currentGraphics, read.getPair());
			drawPairLine(currentGraphics, read);
		}
	}


	/**
	 * Draw a {@link SAMRead}
	 * @param graphic the graphics
	 * @param read the {@link SAMRead}
	 */
	private void drawRead (Graphics graphic, SAMRead read) {
		// we want to make sure that x is > 0
		int x = Math.max(0, projectWindow.genomeToScreenPosition(read.getStart()));
		// we want to make sure that window width is not larger than the screen width
		int widthWindow = Math.min(graphic.getClipBounds().width, projectWindow.genomeToScreenPosition(read.getStop()) - x);
		// we want to make sure that the window width is > 0
		widthWindow = Math.max(1, widthWindow);

		graphic.fillRect(x, 0, widthWindow, READ_HEIGHT);
	}


	/**
	 * Draw the line between two {@link SAMRead}
	 * @param graphic the graphics
	 * @param read the {@link SAMRead}
	 */
	private void drawPairLine (Graphics graphic, SAMRead read) {
		int y = READ_HEIGHT / 2;
		int x1 = projectWindow.genomeToScreenPosition(read.getStop());
		int x2 = projectWindow.genomeToScreenPosition(read.getPair().getStart());
		graphic.drawLine(x1, y, x2, y);
	}


	/**
	 * @param index an index level
	 * @return the Y position for the given level
	 */
	private int getYGraphic (int index) {
		return (index * READ_HEIGHT) + 2 + index;
	}


	/**
	 * @param y a Y position on the track
	 * @return the index level for the given Y
	 */
	private int getYIndex (int y) {
		return (y - 2) / (READ_HEIGHT + 1);
	}


	/**
	 * @param index an index level
	 * @param height the height of the track
	 * @return true if {@link SAMRead} for the given index level is visible, false otherwise
	 */
	private boolean isReadVisible (int index, int height) {
		if (getYGraphic(index) < height) {
			return true;
		}
		return false;
	}


	@Override
	public LayerType getType() {
		return LayerType.BAM_LAYER;
	}


	@Override
	public Color getColor() {
		return color;
	}


	@Override
	public void setColor(Color color) {
		this.color = color;
	}


	@Override
	public void mouseDragged(MouseEvent e) {}


	@Override
	public void mouseMoved(MouseEvent e) {
		SAMRead read = getReadUnderMouse(e);
		if (!ScrollingManager.getInstance().isScrollingEnabled()) {
			if ((readUnderMouse != null) && (read == null)) {
				getTrack().repaint();
			}
		}
		readUnderMouse = read;
		processReadUnderMouse();
	}


	/**
	 * @param e a {@link MouseEvent}
	 * @return the {@link SAMRead} under the mouse, null otherwise
	 */
	private SAMRead getReadUnderMouse (MouseEvent e) {
		SAMRead read = null;
		int index = getYIndex(e.getY());
		if (index < currentReads.size()) {
			int genomePosition = projectWindow.screenToGenomePosition(e.getX());
			List<SAMRead> currentList = currentReads.get(index);
			for (SAMRead currentRead: currentList) {
				if (currentRead.contains(genomePosition)) {
					read = currentRead;
					break;
				}
			}
		}
		return read;
	}


	/**
	 * Process an action for the current read under the mouse.
	 */
	private void processReadUnderMouse () {
		if (readUnderMouse != null) {
			Chromosome chromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
			SAMRecord record = samContent.getRecord(chromosome, readUnderMouse.getStart(), readUnderMouse.getStop());
			if (record != null) {
				String toolTipText = SAMSegment.getHTMLDescription(record);
				getTrack().getGraphicsPanel().setToolTipText(toolTipText);
			}
		}
	}


	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!ScrollingManager.getInstance().isScrollingEnabled() && (readUnderMouse != null)) {
			readUnderMouse = null;
			getTrack().repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
