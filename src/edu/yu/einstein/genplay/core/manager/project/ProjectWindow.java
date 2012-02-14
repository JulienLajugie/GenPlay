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
package edu.yu.einstein.genplay.core.manager.project;

import java.util.ArrayList;
import java.util.List;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;

/**
 * This class manages the genome window and the X factor.
 * Every class that is involved by one or both attributes must registered into this class AND implements the {@link GenomeWindowListener}.
 * The genome windows refers to the current chromosome and the genomic width of the tracks.
 * The X factor is a ratio between the width of a track and the genomic width.
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public class ProjectWindow implements GenomeWindowEventsGenerator {

	private GenomeWindow				genomeWindow;			// the genome window displayed by the track
	private double						xFactor;				// factor between the genomic width and the screen width
	private List<GenomeWindowListener> 	gwListenerList;			// list of GenomeWindowListener


	/**
	 * Constructor of {@link ProjectWindow}
	 */
	protected ProjectWindow () {
		this.gwListenerList = new ArrayList<GenomeWindowListener>();
		genomeWindow = null;
	}

	/**
	 * @return the genomeWindow
	 */
	public GenomeWindow getGenomeWindow() {
		return genomeWindow;
	}


	/**
	 * This method initializes the genome window.
	 * It performs this action only if the genome window is null and no methods set it up to null.
	 * It means that the content of this method is properly ran only once.
	 * The genome window is initialized using the current chromosome. 
	 */
	public void initialize () {
		if (genomeWindow == null) {
			Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
			genomeWindow = new GenomeWindow(currentChromosome, 0, currentChromosome.getLength());
		}
	}


	/**
	 * @param newGenomeWindow new displayed {@link GenomeWindow}
	 */
	public void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(genomeWindow)) {
			GenomeWindow oldGenomeWindow = genomeWindow;
			genomeWindow = newGenomeWindow;
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, genomeWindow);
			if (evt.chromosomeChanged()) {
				ProjectManager.getInstance().getProjectChromosome().setCurrentChromosome(genomeWindow.getChromosome());
			}
			for (GenomeWindowListener currentListener: gwListenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}
	
	
	/**
	 * This methods makes the X factor calculation using a width.
	 * This width must be the width of a track.
	 * @param trackWidth track width
	 * @return	the X factor
	 */
	public double getXFactor (int trackWidth) {
		double newXFactor = (double)trackWidth / (double)(genomeWindow.getStop() - genomeWindow.getStart());
		return newXFactor;
	}


	/**
	 * @return the xFactor
	 */
	public double getXFactor() {
		return xFactor;
	}


	/**
	 * @param xFactor the xFactor to set
	 */
	public void setXFactor(double xFactor) {
		this.xFactor = xFactor;
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		gwListenerList.add(genomeWindowListener);

	}


	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[gwListenerList.size()];
		return gwListenerList.toArray(genomeWindowListeners);
	}


	@Override
	public void removeGenomeWindowListener(
			GenomeWindowListener genomeWindowListener) {
		gwListenerList.remove(genomeWindowListener);
	}


	/**
	 * @param genomePosition a position on the genome
	 * @return the absolute position on the screen (can be > than the screen width)
	 */
	public int genomePosToScreenXPos(int genomePosition) {
		return (int)Math.round((double)(genomePosition - genomeWindow.getStart()) * xFactor);
	}


	/**
	 * @param genomePositionStart start position on the genome
	 * @param genomePositionStop stop position on the genome
	 * @return the width on the screen between this two positions
	 */
	public int twoGenomePosToScreenWidth(int genomePositionStart, int genomePositionStop) {
		double x1 = ((double)(genomePositionStart - genomeWindow.getStart())) * xFactor;
		double x2 = ((double)(genomePositionStop - genomeWindow.getStart())) * xFactor;
		double distance = Math.abs(x1 - x2);
		return (int) Math.ceil(distance);
	}


	/**
	 * @param x position on the screen
	 * @return position on the genome 
	 */
	public double screenXPosToGenomePos(int x) {
		double distance = twoScreenPosToGenomeWidth(0, x);
		double genomePosition = genomeWindow.getStart() + Math.floor(distance);
		return genomePosition;
	}


	/**
	 * @param x1 position 1 on the screen
	 * @param x2 position 2 on the screen
	 * @return the distance in base pair between the screen position x1 and x2 
	 */
	public double twoScreenPosToGenomeWidth(int x1, int x2) {
		int width = MainFrame.getInstance().getTrackList().getTrackList()[0].getWidth();
		double distance = ((double)(x2 - x1) / (double)width * (double)(genomeWindow.getStop() - genomeWindow.getStart()));
		return distance;
	}
}
