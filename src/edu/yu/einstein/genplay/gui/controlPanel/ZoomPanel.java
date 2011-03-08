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
package edu.yu.einstein.genplay.gui.controlPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.manager.ZoomManager;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEventsGenerator;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;



/**
 * The ZoomPanel part of the {@link ControlPanel} 
 * @author Julien Lajugie
 * @version 0.1
 */
final class ZoomPanel extends JPanel implements MouseWheelListener, GenomeWindowEventsGenerator {

	private static final long serialVersionUID = -8481919273684304592L; // generated ID
	private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("###,###,###");	// Format of the zoom string
	private final JLabel 							jlZoom;				// zoom lable
	private final JButton 							jbPlus;				// button '+'
	private final JButton 							jbMinus;			// button '-'
	private final JSlider 							jsZoom;				// zoom slider
	private final ZoomManager						zoomManager;		// ZoomManager 
	private final ArrayList<GenomeWindowListener> 	listenerList;		// list of GenomeWindowListener
	private GenomeWindow 							currentGenomeWindow;// current GenomeWindow
	
	
	/**
	 * Creates an instance of {@link ZoomPanel}
	 * @param genomeWindow a {@link GenomeWindow}
	 */
	ZoomPanel(GenomeWindow genomeWindow) {
		this.zoomManager = ZoomManager.getInstance();
		this.currentGenomeWindow = genomeWindow;
		this.listenerList = new ArrayList<GenomeWindowListener>();
		jlZoom = new JLabel("Size: " + SIZE_FORMAT.format(currentGenomeWindow.getSize()));
		jbMinus = new JButton("-");
		jbMinus.setMargin(new Insets(0, 3, 0, 3));
		jbMinus.setFocusPainted(false);
		jbMinus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomChanged(zoomManager.getZoomIn(currentGenomeWindow.getSize()));
			}
		});

		jbPlus = new JButton("+");
		jbPlus.setMargin(new Insets(0, 3, 0, 3));
		jbPlus.setFocusPainted(false);
		jbPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomChanged(zoomManager.getZoomOut(currentGenomeWindow.getSize()));
			}
		});
		
		int	maximumZoom = currentGenomeWindow.getChromosome().getLength() * 2;
		jsZoom = new JSlider(JSlider.HORIZONTAL, 0, zoomManager.getZoomIndex(maximumZoom), zoomManager.getZoomIndex(currentGenomeWindow.getSize()));
		jsZoom.setMinorTickSpacing(1);
		jsZoom.setPaintTicks(true);
		jsZoom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (zoomManager.getZoomIndex(currentGenomeWindow.getSize()) != jsZoom.getValue())
					zoomChanged(zoomManager.getZoom(jsZoom.getValue()));
			}
		});
		
		// Add the components
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jlZoom, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(jbMinus, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.weighty = 0;
		add(jsZoom, gbc);

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		add(jbPlus, gbc);

		addMouseWheelListener(this);
	}

	
	/**
	 * Sets the current {@link GenomeWindow}
	 * @param newGenomeWindow new {@link GenomeWindow}
	 */
	void setGenomeWindow(GenomeWindow newGenomeWindow) {
		if (!newGenomeWindow.equals(currentGenomeWindow)) {
			GenomeWindow oldGenomeWindow = currentGenomeWindow;
			currentGenomeWindow = newGenomeWindow;
			// we notify the gui
			int currentZoom = currentGenomeWindow.getSize();
			jlZoom.setText("Size: " + SIZE_FORMAT.format(currentZoom));			
			// if the chromosome changes we change the maximum zoom
			if (currentGenomeWindow.getChromosome() != oldGenomeWindow.getChromosome()) {
				int	oldMaximumZoom = oldGenomeWindow.getChromosome().getLength() * 2;
				int newMaximumZoom = currentGenomeWindow.getChromosome().getLength() * 2;
				// if the new zoom value is greatter than the old max zoom we change the max first 
				if (currentZoom > oldMaximumZoom) {
					jsZoom.setMaximum(zoomManager.getZoomIndex(newMaximumZoom));
					jsZoom.setValue(zoomManager.getZoomIndex(currentZoom));
					
				} else {
					// else we change the value first because the new max could be smaller than the old value
					jsZoom.setValue(zoomManager.getZoomIndex(currentZoom));
					jsZoom.setMaximum(zoomManager.getZoomIndex(newMaximumZoom));
				}	
			} else {
				jsZoom.setValue(zoomManager.getZoomIndex(currentZoom));
			}
			// we notify the listeners
			GenomeWindowEvent evt = new GenomeWindowEvent(this, oldGenomeWindow, currentGenomeWindow);
			for (GenomeWindowListener currentListener: listenerList) {
				currentListener.genomeWindowChanged(evt);
			}
		}
	}
	
		
	/**
	 * Called when the zoom changes
	 * @param newZoom new zoom value
	 */
	void zoomChanged(int newZoom) {
		int currentZoom = currentGenomeWindow.getSize();
		int	maximumZoom = currentGenomeWindow.getChromosome().getLength() * 2;
		if (newZoom > maximumZoom) {
			newZoom = maximumZoom;
		}
		if (newZoom != currentZoom) {
			double halfZoom = newZoom / (double)2;
			Chromosome chromosome = currentGenomeWindow.getChromosome();
			int start = (int)(currentGenomeWindow.getMiddlePosition() - halfZoom);
			int stop = start + newZoom;
			GenomeWindow newGenomeWindow = new GenomeWindow(chromosome, start, stop);
			setGenomeWindow(newGenomeWindow);
		}
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int currentZoom = currentGenomeWindow.getSize();
		for (int i = 0; i < Math.abs(mwe.getWheelRotation()); i++) {
			if (mwe.getWheelRotation() > 0) {
				zoomChanged(zoomManager.getZoomIn(currentZoom));
			} else {
				zoomChanged(zoomManager.getZoomOut(currentZoom));
			}
		}
	}


	@Override
	public void addGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.add(genomeWindowListener);		
	}
	
	
	@Override
	public GenomeWindowListener[] getGenomeWindowListeners() {
		GenomeWindowListener[] genomeWindowListeners = new GenomeWindowListener[listenerList.size()];
		return listenerList.toArray(genomeWindowListeners);
	}
	
	
	@Override
	public void removeGenomeWindowListener(GenomeWindowListener genomeWindowListener) {
		listenerList.remove(genomeWindowListener);		
	}
}
