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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.mainFrame;


/**
 * This class implements the runnable interface. The run method creates the MainFrame singleton if necessary 
 * and then show the mainframe screen
 * @author Julien Lajugie
 * @version 0.1
 */
public class MainFrameThread extends Thread {
	
	/**
	 * Creates an instance of {@link MainFrameThread} and set the name of the thread 
	 */
	public MainFrameThread() {
		super("MainFrame Thread");
	}
	
	
	/**
	 * This method computes the init method of the project screen object
	 */
	public void run() {
		MainFrame mainFrame = MainFrame.getInstance();
		mainFrame.setVisible(true);
    }
}
