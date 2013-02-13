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

import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;

/**
 * Singleton used by tracks to scroll the genome window displayed when the scrolling mode is on
 * (the scrolling mode can be turned on and off using the mouse middle button)
 * @author Julien Lajugie
 */
public class ScrollingManager {

	private static ScrollingManager instance = null;
	private ScrollingThread 	scrollingThread; 					// thread executed when the scroll mode is on
	private int					scrollingIntensity;					// intensity of the scroll.


	/**
	 * Creates an instance of {@link ScrollingManager}
	 */
	private ScrollingManager() {
		this.scrollingIntensity = 0;
	}


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


	/**
	 * @return the value of the scrolling intensity
	 */
	public int getScrollingIntensity() {
		return scrollingIntensity;
	}


	/**
	 * The ScrollModeThread class is used to scroll the track horizontally
	 * when the scroll mode is on (ie when the middle button of the mouse is clicked)
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class ScrollingThread extends Thread {
		@Override
		public void run() {
			synchronized (this) {
				Thread thisThread = Thread.currentThread();
				ProjectWindow projectWindow = ProjectManager.getInstance().getProjectWindow();
				while (scrollingThread == thisThread) {
					GenomeWindow newWindow = new GenomeWindow();
					newWindow.setChromosome(projectWindow.getGenomeWindow().getChromosome());
					newWindow.setStart(projectWindow.getGenomeWindow().getStart() + scrollingIntensity);
					newWindow.setStop(projectWindow.getGenomeWindow().getStop() + scrollingIntensity);
					if (newWindow.getMiddlePosition() < 0) {
						newWindow.setStart(-projectWindow.getGenomeWindow().getSize() / 2);
						newWindow.setStop(newWindow.getStart() + projectWindow.getGenomeWindow().getSize());
					} else if (newWindow.getMiddlePosition() > newWindow.getChromosome().getLength()) {
						newWindow.setStop(newWindow.getChromosome().getLength() + (projectWindow.getGenomeWindow().getSize() / 2));
						newWindow.setStart(newWindow.getStop() - projectWindow.getGenomeWindow().getSize());
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
}
