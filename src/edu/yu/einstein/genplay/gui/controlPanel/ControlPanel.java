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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;



/**
 * A ControlPanel component
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ControlPanel extends JPanel {

	private static final long serialVersionUID = -8254420324898563978L; // generated ID
	private static final int INCREMENT_FACTOR = 10; 					// the length of a left or right move is the length of the   
																		// displayed chromosome window divided by this constant 
	private final TopPanel							topPanel;			// TopPanel part (multi genome button and position scroll bar)
	private final ZoomPanel 						zoomPanel;			// ZoomPanel part
	private final ChromosomePanel 					chromosomePanel;	// ChromosomePanel part
	private final GenomeWindowPanel 				genomeWindowPanel;	// GenomeWindowPanel part
	private final ProjectWindow						projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link ControlPanel}
	 */
	public ControlPanel() { 
		topPanel = new TopPanel();
		zoomPanel = new ZoomPanel();
		chromosomePanel = new ChromosomePanel();
		genomeWindowPanel = new GenomeWindowPanel();

		projectWindow = ProjectManager.getInstance().getProjectWindow();
		
		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.gridwidth = 3;
		add(topPanel, gbc);

		gbc.insets = new Insets(0, 10, 0, 10);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		add(zoomPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.gridwidth = 1;
		add(chromosomePanel, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		add(genomeWindowPanel, gbc);
	}


	/**
	 * Decrements the start and the stop positions of the {@link GenomeWindow} 
	 */
	public void moveLeft() {
		int moveGap = (int) (projectWindow.getGenomeWindow().getSize() / (double) INCREMENT_FACTOR);
		// we want to move from at least 1 nucleotide
		moveGap = Math.max(moveGap, 1);
		GenomeWindow newGenomeWindow = new GenomeWindow(projectWindow.getGenomeWindow().getChromosome(), projectWindow.getGenomeWindow().getStart() - moveGap, projectWindow.getGenomeWindow().getStop() - moveGap);
		if (newGenomeWindow.getMiddlePosition() < 0) {
			int size = newGenomeWindow.getSize();
			newGenomeWindow.setStart(-size / 2);
			newGenomeWindow.setStop(newGenomeWindow.getStart() + size);
		}
		projectWindow.setGenomeWindow(newGenomeWindow);
	}


	/**
	 * Increments the start and the stop positions of the {@link GenomeWindow}
	 */
	public void moveRight() {
		int moveGap = (int) (projectWindow.getGenomeWindow().getSize() / (double) INCREMENT_FACTOR);
		// we want to move from at least 1 nucleotide
		moveGap = Math.max(moveGap, 1);
		GenomeWindow newGenomeWindow = new GenomeWindow(projectWindow.getGenomeWindow().getChromosome(), projectWindow.getGenomeWindow().getStart() + moveGap, projectWindow.getGenomeWindow().getStop() + moveGap);
		if (newGenomeWindow.getMiddlePosition() > projectWindow.getGenomeWindow().getChromosome().getLength()) {
			int size = newGenomeWindow.getSize();
			newGenomeWindow.setStart(projectWindow.getGenomeWindow().getChromosome().getLength() - size / 2);
			newGenomeWindow.setStop(newGenomeWindow.getStart() + size);
		}
		projectWindow.setGenomeWindow(newGenomeWindow);	
	}		

	
	/**
	 * This method reinitializes the elements of the 
	 * chromosome panel with the values of the {@link ProjectChromosome}. 
	 * This method needs to be called when the chomosome manager changes.
	 */
	public void reinitChromosomePanel () {
		chromosomePanel.updateChromosomePanel();
	}
	
	
	/**
	 * Registers every control panel components to the genome window manager.
	 */
	public void registerToGenomeWindow () {
		projectWindow.addGenomeWindowListener(chromosomePanel);
		projectWindow.addGenomeWindowListener(genomeWindowPanel);
		projectWindow.addGenomeWindowListener(topPanel.getPositionScrollPanel());
		projectWindow.addGenomeWindowListener(zoomPanel);
	}
	
	
	/**
	 * Zooms in
	 */
	public void zoomIn() {
		int newZoom = ProjectManager.getInstance().getProjectZoom().getZoomIn(projectWindow.getGenomeWindow().getSize());
		zoomPanel.zoomChanged(newZoom);
	}


	/**
	 * Zooms out
	 */
	public void zoomOut() {
		int newZoom = ProjectManager.getInstance().getProjectZoom().getZoomOut(projectWindow.getGenomeWindow().getSize());
		zoomPanel.zoomChanged(newZoom);
	}
	
	
	/**
	 * Locks the control panel
	 */
	public void lock() {
		chromosomePanel.lock();
		genomeWindowPanel.lock();
	}
	
	
	/**
	 * Unlocks the control panel
	 */
	public void unlock() {
		chromosomePanel.unlock();
		genomeWindowPanel.unlock();
	}
}
