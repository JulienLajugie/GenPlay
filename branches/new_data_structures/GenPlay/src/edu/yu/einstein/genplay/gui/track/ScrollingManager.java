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

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * Singleton used by tracks to scroll the genome window displayed when the scrolling mode is on
 * (the scrolling mode can be turned on and off using the mouse middle button)
 * @author Julien Lajugie
 */
public class ScrollingManager {

	/**
	 * The ScrollModeThread class is used to scroll the track horizontally
	 * when the scroll mode is on (ie when the middle button of the mouse is clicked)
	 * @author Julien Lajugie
	 */
	private class ScrollingThread extends Thread {
		@Override
		public void run() {
			synchronized (this) {
				Thread thisThread = Thread.currentThread();
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				while (scrollingThread == thisThread) {
					Chromosome chromo = projectWindow.getGenomeWindow().getChromosome();
					int start = projectWindow.getGenomeWindow().getStart() + scrollingIntensity;
					int stop = projectWindow.getGenomeWindow().getStop() + scrollingIntensity;
					SimpleGenomeWindow newWindow = new SimpleGenomeWindow(chromo, start, stop);
					if (newWindow.getMiddlePosition() < ProjectChromosomes.FIRST_BASE_POSITION) {
						start = ProjectChromosomes.FIRST_BASE_POSITION - (projectWindow.getGenomeWindow().getSize() / 2);
						stop = start + projectWindow.getGenomeWindow().getSize();
						newWindow = new SimpleGenomeWindow(chromo, start, stop);
					} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
						stop = newWindow.getChromosome().getLength() + (projectWindow.getGenomeWindow().getSize() / 2);
						start = stop - projectWindow.getGenomeWindow().getSize();
						newWindow = new SimpleGenomeWindow(chromo, start, stop);
					}
					projectWindow.setGenomeWindow(newWindow);
					yield();
					try {
						if ((scrollingIntensity == 1) || (scrollingIntensity == -1)) {
							sleep(100);
						} else {
							sleep(10);
						}
					} catch (InterruptedException e) {
						ExceptionManager.getInstance().caughtException(e);
					}
				}
			}
		}
	}

	private static ScrollingManager instance = null;

	/**
	 * @return the instance of the {@link ScrollingManager} singleton
	 */
	public synchronized static ScrollingManager getInstance() {
		if (instance == null) {
			// we synchronize to make sure that there is no 2 instances created
			synchronized(ScrollingManager.class) {
				if (instance == null) {
					instance = new ScrollingManager();
				}
			}
		}
		return instance;
	}

	private ScrollingThread 	scrollingThread; 					// thread executed when the scroll mode is on
	private int					scrollingIntensity;					// intensity of the scroll.


	/**
	 * Creates an instance of {@link ScrollingManager}
	 */
	private ScrollingManager() {
		scrollingIntensity = 0;
	}


	/**
	 * @return the value of the scrolling intensity
	 */
	public int getScrollingIntensity() {
		return scrollingIntensity;
	}


	/**
	 * @return true if the scrolling mode is on, false if it's off
	 */
	public boolean isScrollingEnabled() {
		return scrollingThread != null;
	}


	/**
	 * @return true if the scrolling is enable and going left
	 */
	public boolean isScrollingLeft() {
		return isScrollingEnabled() && (getScrollingIntensity() < 0);
	}


	/**
	 * @return true if the scrolling is enable and going right
	 */
	public boolean isScrollingRight() {
		return isScrollingEnabled() && (getScrollingIntensity() > 0);
	}


	/**
	 * Turns on or off the track scrolling mode
	 * @param enabled true if the scrolling mode should be enabled, false otherwise
	 */
	public void setScrollingEnabled(boolean enabled) {
		if (enabled) {
			scrollingThread = new ScrollingThread();
			scrollingThread.start();
		} else {
			scrollingThread = null;
		}
	}


	/**
	 * Set the intensity of the scrolling.
	 * Positive and negative intensities scroll the windows in opposite directions
	 * @param distance horizontal distance between the cursor and the middle of the track.
	 * The distance should be negative if the cursor is located left of the middle of the track
	 */
	public void setScrollingIntensity(int distance) {
		ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
		double scrollingIntensityTmp = projectWindow.screenToGenomeWidth(distance);
		if (scrollingIntensityTmp < 0) {
			scrollingIntensity = (int) (scrollingIntensityTmp / 10d) + 1;
		} else {
			scrollingIntensity = (int) (scrollingIntensityTmp / 10d) - 1;
		}
	}
}
