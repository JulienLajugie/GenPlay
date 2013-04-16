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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMRecord;
import edu.yu.einstein.genplay.core.SAM.SAMContent;
import edu.yu.einstein.genplay.core.SAM.SAMFile;
import edu.yu.einstein.genplay.core.SAM.SAMProcessor;
import edu.yu.einstein.genplay.core.SAM.SAMRead;
import edu.yu.einstein.genplay.core.SAM.SAMSegment;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.action.layer.BAMLayer.BALoad;
import edu.yu.einstein.genplay.gui.event.genomeWindowLoader.GenomeWindowLoaderEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowLoader.GenomeWindowLoaderListener;
import edu.yu.einstein.genplay.gui.event.genomeWindowLoader.GenomeWindowLoaderSettings;
import edu.yu.einstein.genplay.gui.track.ScrollingManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.colors.Colors;
import edu.yu.einstein.genplay.util.colors.LayerColors;


/**
 * Layer displaying a {@link SAMFile}
 * @author Nicolas Fourel
 */
public class BAMLayer extends AbstractLayer<SAMFile> implements SAMProcessor, ColoredLayer, GenomeWindowLoaderListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 3779631846077486596L; // generated ID
	private static final int READ_HEIGHT = 15;					// The height of a read.
	private final ProjectWindow 				projectWindow;	// The project window.
	private final GenomeWindowLoaderSettings 	settings;		// The settings about how to generate extended genome window.

	private Color					color;				// color of the layer.
	private final SAMContent 		samContent;			// The SAM/BAM content manager.
	private SAMRead 				readUnderMouse;		// The read under the mouse pointer.
	private boolean					isLocked;			// True if the display has to be locked.

	private final Map<SAMRead, Integer> 	readHistory;		// Stores the read previously added and their level
	private List<List<SAMRead>> 	currentReads;				// The list of reads to be displayed organized by level.
	private List<Graphics> 			graphics;					// The list of graphics to use organized by level.
	private GenomeWindow 			genomeWindow;				// The current genome window.
	private GenomeWindow 			extendedGenomeWindow;		// The current genome window.

	private Graphics currentGraphic;		// Current graphic for display (since the displayed is delayed).
	private int currentHeight;				// Current height of the track (since the displayed is delayed).
	private int bpLimit;


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
		isLocked = true;
		settings = new GenomeWindowLoaderSettings();
		settings.set(0.2, 0.2, 2000, GenomeWindowLoaderSettings.NO_LIMIT, 2000, GenomeWindowLoaderSettings.NO_LIMIT);
		projectWindow.addGenomeWindowLoaderListener(this);
		samContent = new SAMContent(getData());
		samContent.addProcessor(this);
		readHistory = new HashMap<SAMRead, Integer>();
		extendedGenomeWindow = projectWindow.getGenomeWindow();
		computeBPLimit();
		getTrack().repaint();
	}


	private void computeBPLimit () {
		if (projectWindow.getXRatio() > 1) {
			bpLimit = 1;
		} else {
			bpLimit = projectWindow.screenToGenomeWidth(1);
		}
		System.out.println("BAMLayer.computeBPLimit() " + bpLimit);
	}


	@Override
	public void draw(Graphics g, int width, int height) {
		if (isVisible()) {
			genomeWindow = projectWindow.getGenomeWindow();
			if (genomeWindow.getSize() > 20000) {
				isLocked = true;
				String text = "Cannot be displayed at the current zoom.";
				g.setColor(Colors.BLACK);
				g.drawString(text, 10, 15);
			} else {
				isLocked = false;
				currentGraphic = g;
				currentHeight = height;
				if (samContent.isLoading()) {
					String text = "Data is loading.";
					g.setColor(Colors.BLACK);
					g.drawString(text, 10, 15);
				}
				g.setColor(color);
				samContent.retrieveReads(genomeWindow.getStart(), genomeWindow.getStop());
				//samContent.retrieveAllReads();
			}
		}
	}


	@Override
	public void handleReads(List<SAMRead> reads) {
		createCurrentLists(currentGraphic, reads);
		drawReadList(currentReads, currentHeight);
		purgeHistory();
	}


	/**
	 * Generates the list of reads to display by level
	 * @param g		a {@link Graphics}
	 * @param reads a list of {@link SAMRead}
	 */
	private void createCurrentLists (Graphics g, List<SAMRead> reads) {
		graphics = new ArrayList<Graphics>();
		currentReads = new ArrayList<List<SAMRead>>();
		List<SAMRead> newReads = new ArrayList<SAMRead>();			// The temporary list will store all reads not previously displayed: the new ones!
		int levelMax = 0;							// Keeps the highest level number.

		// Try to insert first all the reads previously added
		for (SAMRead read: reads) {
			if (read.isValid()) {
				int levelIndex = -1;
				Integer indexFound = readHistory.get(read);
				if (indexFound != null) {							// If the read has been found, it means it was displayed earlier
					levelIndex = indexFound;						// We get its level index
					addToList(currentReads, levelIndex, -1, read);	// and add it to the right level
				} else {											// If not
					newReads.add(read);								// it's a new read.
				}
				levelMax = Math.max(levelMax, levelIndex);			// Get the highest level number.
			}
		}

		// Insert all the new reads
		for (SAMRead read: newReads) {
			int[] currentIndexes = getCurrentIndex(read);			// Get the level and list index.
			int levelIndex = currentIndexes[0];
			int listIndex = currentIndexes[1];
			addToList(currentReads, levelIndex, listIndex, read);	// Add the read to the right position
			levelMax = Math.max(levelMax, levelIndex);				// Get the highest level number.
			readHistory.put(read, levelIndex);						// The read is now in the history for future display.
		}

		// Generate the graphic list to display enoug level
		while (levelMax >= graphics.size()) {
			int y = getYGraphic(graphics.size());
			Graphics currentGraphic = g.create(0, y, g.getClipBounds().width, READ_HEIGHT);
			graphics.add(currentGraphic);
		}
	}


	/**
	 * Adds a read to a specific location
	 * @param list			the lists where the read has to be added
	 * @param levelIndex	the first index: the one of the level
	 * @param listIndex		the second index: the one in the list of the choosen level
	 * @param read			the read to insert
	 */
	private void addToList (List<List<SAMRead>> list, int levelIndex, int listIndex, SAMRead read) {
		if (levelIndex > -1) {
			while (levelIndex >= list.size()) {
				list.add(new ArrayList<SAMRead>());
				//list.add(new LinkedList<SAMRead>());
			}
			if (listIndex == -1) {
				list.get(levelIndex).add(read);
			} else {
				list.get(levelIndex).add(listIndex, read);
			}
		}
	}


	/**
	 * Draw a list of reads
	 * @param list a list of reads
	 * @param height the height of the track
	 */
	private void drawReadList (List<List<SAMRead>> list, int height) {
		for (int level = 0; level < list.size(); level++) {
			List<SAMRead> reads = list.get(level);
			for (SAMRead read: reads) {
				if (isLineVisible(level, height) && read.isContained(genomeWindow.getStart(), genomeWindow.getStop())) {
					drawRead(read, level);
				}
			}
		}
	}


	/**
	 * Get the indexes of the given read.
	 * First index: the level index. The level is the horizontal piece of the track where reads are displayed. This way, reads display does not overlap.
	 * Second index: the index in the level where the read has to be inserted.
	 * @param read a {@link SAMRead}
	 * @return the level and list index of the read
	 */
	private int[] getCurrentIndex (SAMRead read) {
		int[] indexes = new int[2];
		int levelIndex = -1;
		int listIndex = -1;
		int size = currentReads.size();
		if (size == 0) {
			levelIndex = 0;
		} else {
			for (int i = 0; i < size; i++) {
				if (!isLineVisible(i, currentHeight)) {
					levelIndex = -2;
					break;
				}
				List<SAMRead> currentList = currentReads.get(i);
				int tmpListIndex = getListIndex(currentList, read);
				if (tmpListIndex != -1) {
					levelIndex = i;
					listIndex = tmpListIndex;
					break;
				}
			}
			if (levelIndex == -1) {
				levelIndex = size;
				listIndex = 0;
			}
		}
		indexes[0] = levelIndex;
		indexes[1] = listIndex;
		return indexes;
	}


	/**
	 * The list index of the read
	 * @param list a list of {@link SAMRead}
	 * @param read a {@link SAMRead}
	 * @return the index in the list where the read can be added
	 */
	private int getListIndex (List<SAMRead> list, SAMRead read) {
		int index = -1;

		if (list.size() == 0) {
			index = 0;
		} else {
			int lastIndex = list.size() - 1;
			if (fitBefore(list.get(0), read)) {
				index = 0;
			} else if (fitAfter(list.get(lastIndex), read)) {
				index = list.size();
			} else if (lastIndex > 1) {
				int indexFound = getIndex(list, read.getStart(), 0, lastIndex);
				if (fitBetween(list.get(indexFound - 1), list.get(indexFound), read)) {
					index = indexFound;
				}
			}
		}
		return index;
	}


	/**
	 * Recursive function. Returns the index where the value is found
	 * or the index right after if the exact value is not found.
	 * @param value			value
	 * @param indexStart	start index (in the data array)
	 * @param indexStop		stop index (in the data array)
	 * @return the index where the start value of the window is found or the index right after if the exact value is not found
	 */
	private int getIndex (List<SAMRead> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getStart()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getStart()) {
			return getIndex(list, value, indexStart + middle + 1, indexStop);
		} else {
			return getIndex(list, value, indexStart, indexStart + middle);
		}
	}


	/**
	 * @param read1
	 * @param read2
	 * @return true if the second read fits before the first one, false otherwise.
	 */
	private boolean fitBefore (SAMRead read1, SAMRead read2) {
		return ((read1.getStart() - read2.getFullAlignementStop()) > bpLimit);
	}


	/**
	 * @param read1
	 * @param read2
	 * @return true if the second read fits after the first one, false otherwise.
	 */
	private boolean fitAfter (SAMRead read1, SAMRead read2) {
		return ((read2.getStart() - read1.getFullAlignementStop()) > bpLimit);
	}


	/**
	 * @param read1
	 * @param read2
	 * @param read3
	 * @return true if the third read can fit between the first and second one.
	 */
	private boolean fitBetween (SAMRead read1, SAMRead read2, SAMRead read3) {
		return (fitBefore(read2, read3) && fitAfter(read1, read3));
	}


	/**
	 * Draw a {@link SAMRead} on the given index level
	 * @param index the index level
	 */
	private void drawRead (SAMRead read, int index) {
		Graphics currentGraphics = graphics.get(index);
		drawRead(currentGraphics, read);
		if (read.isPaired()) {
			drawRead(currentGraphics, read.getPair());
			if (!read.overlap()) {
				drawPairLine(currentGraphics, read);
			}
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
	private boolean isLineVisible (int index, int height) {
		if (getYGraphic(index) < height) {
			return true;
		}
		return false;
	}


	/**
	 * Get rid of the reads that are out of the window.
	 */
	private void purgeHistory () {
		List<SAMRead> reads = new ArrayList<SAMRead>(readHistory.keySet());
		for (SAMRead read: reads) {
			if (!read.isContained(extendedGenomeWindow.getStart(), extendedGenomeWindow.getStop())) {
				readHistory.remove(read);
			}
		}
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
		if (!isLocked) {
			SAMRead read = getReadUnderMouse(e);
			if (!ScrollingManager.getInstance().isScrollingEnabled()) {
				if ((readUnderMouse != null) && (read == null)) {
					getTrack().repaint();
				}
			}
			readUnderMouse = read;
			processReadUnderMouse();
		}
	}


	/**
	 * @param e a {@link MouseEvent}
	 * @return the {@link SAMRead} under the mouse, null otherwise
	 */
	private SAMRead getReadUnderMouse (MouseEvent e) {
		SAMRead read = null;
		int index = getYIndex(e.getY());
		if ((currentReads != null) && (index < currentReads.size())) {
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

	@Override
	public void genomeWindowLoaderChanged(GenomeWindowLoaderEvent evt) {
		computeBPLimit();
		if (!isLocked) {
			extendedGenomeWindow = evt.getNewExtendedGenomeWindow();
			BALoad action = new BALoad(this, samContent, extendedGenomeWindow);
			//BALoad action = new BALoad(this, samContent, genomeWindow);
			action.actionPerformed(null);
		}
	}

	@Override
	public GenomeWindowLoaderSettings getGenomeWindowLoaderSettings() {
		return settings;
	}

}