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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;


/**
 * The PositionScrollPanel part of the {@link ControlPanel} 
 * @author Julien Lajugie
 * @version 0.1
 */
final class PositionScrollPanel extends JPanel implements AdjustmentListener, MouseWheelListener, GenomeWindowListener {


	private static final long serialVersionUID = 2266293237606451568L; 	// Generated ID
	private static final int TRACKS_SCROLL_WIDTH = 17;					// Width of the scroll bar
	private final JScrollBar 		jsbPosition;		// scroll bar to modify the position
	private final ProjectWindow		projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link PositionScrollPanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	PositionScrollPanel() {
		this.projectWindow = ProjectManager.getInstance().getProjectWindow();
		int currentPosition = (int)projectWindow.getGenomeWindow().getMiddlePosition();
		int currentSize = projectWindow.getGenomeWindow().getSize();
		Chromosome currentChromosome = projectWindow.getGenomeWindow().getChromosome();		
		jsbPosition = new JScrollBar(JScrollBar.HORIZONTAL, currentPosition, currentSize, 0, currentChromosome.getLength() + currentSize);
		jsbPosition.setBlockIncrement(currentSize / 10);
		jsbPosition.setUnitIncrement(currentSize / 10);
		jsbPosition.addAdjustmentListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 0, 0, TRACKS_SCROLL_WIDTH);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(jsbPosition, gbc);

		addMouseWheelListener(this);
	}


	/**
	 * Sets the greatest attainable position
	 */
	private void setMaximumPosition() {
		int extent = projectWindow.getGenomeWindow().getSize();
		int newMaximum = projectWindow.getGenomeWindow().getChromosome().getLength() + extent; 
		jsbPosition.setMaximum(newMaximum);
	}


	/**
	 * Sets the value of the increment when the scroll bar is clicked
	 */
	private void setIncrement() {
		int increment = projectWindow.getGenomeWindow().getSize() / 10;
		jsbPosition.setBlockIncrement(increment);
		jsbPosition.setUnitIncrement(increment);	
	}


	/**
	 * Sets the extent parameter of the scroll bar.
	 */
	private void setExtent() {
		int newExtent = projectWindow.getGenomeWindow().getSize();
		int maximumPosition = projectWindow.getGenomeWindow().getChromosome().getLength(); 
		jsbPosition.setValue((int)projectWindow.getGenomeWindow().getMiddlePosition());
		if (newExtent > jsbPosition.getVisibleAmount()) {
			jsbPosition.setMaximum(maximumPosition + newExtent);
			jsbPosition.setVisibleAmount(newExtent);
		} else {
			jsbPosition.setVisibleAmount(newExtent);
			jsbPosition.setMaximum(maximumPosition + newExtent);
		}
	}


	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		int halfSize = projectWindow.getGenomeWindow().getSize() / 2;
		Chromosome chromosome = projectWindow.getGenomeWindow().getChromosome();
		int start = (jsbPosition.getValue() - halfSize);
		int stop = start + projectWindow.getGenomeWindow().getSize();
		GenomeWindow newGenomeWindow = new GenomeWindow(chromosome, start, stop);
		projectWindow.setGenomeWindow(newGenomeWindow);
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		double newPosition = (mwe.getWheelRotation() * jsbPosition.getBlockIncrement()) + jsbPosition.getValue();
		// newPosition must be >= 0
		newPosition = Math.max(0, newPosition);
		// newPosition must be <= than the max position of jsbPosition
		newPosition = Math.min(projectWindow.getGenomeWindow().getChromosome().getLength(), newPosition);
		int halfSize = projectWindow.getGenomeWindow().getSize() / 2;
		Chromosome chromosome = projectWindow.getGenomeWindow().getChromosome();
		int start = (int)(newPosition - halfSize);
		int stop = start + projectWindow.getGenomeWindow().getSize();
		GenomeWindow newGenomeWindow = new GenomeWindow(chromosome, start, stop);
		projectWindow.setGenomeWindow(newGenomeWindow);
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// we notify the gui
		if (evt.getNewWindow().getSize() != evt.getOldWindow().getSize()) {
			setIncrement();
			setExtent();
		}
		if (evt.chromosomeChanged()) {
			setMaximumPosition();
		}
		if ((int)evt.getNewWindow().getMiddlePosition() != (int)evt.getOldWindow().getMiddlePosition()) {
			jsbPosition.setValue((int)evt.getNewWindow().getMiddlePosition());
		}
	}

}
