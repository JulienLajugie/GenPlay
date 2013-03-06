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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.core.manager.project.ProjectZoom;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.SimpleGenomeWindow;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveFarRight;
import edu.yu.einstein.genplay.gui.action.project.PAMoveLeft;
import edu.yu.einstein.genplay.gui.action.project.PAMoveRight;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;



/**
 * The ZoomPanel part of the {@link ControlPanel}
 * @author Julien Lajugie
 * @version 0.1
 */
final class ZoomPanel extends JPanel implements MouseWheelListener, GenomeWindowListener {

	private static final long serialVersionUID = -8481919273684304592L; // generated ID
	private final JLabel 							jlZoom;				// zoom label
	private final JButton 							jbPlus;				// button '+'
	private final JButton 							jbMinus;			// button '-'
	private final JSlider 							jsZoom;				// zoom slider
	private final ProjectZoom						projectZoom;		// ZoomManager
	private final ProjectWindow						projectWindow;		// Instance of the Genome Window Manager


	/**
	 * Creates an instance of {@link ZoomPanel}
	 * @param genomeWindow a {@link SimpleGenomeWindow}
	 */
	ZoomPanel() {
		projectZoom = ProjectManager.getInstance().getProjectZoom();
		projectWindow = ProjectManager.getInstance().getProjectWindow();
		jlZoom = new JLabel("Size: " + NumberFormat.getInstance().format(projectWindow.getGenomeWindow().getSize()));
		jbMinus = new JButton("-");
		jbMinus.setMargin(new Insets(0, 3, 0, 3));
		jbMinus.setFocusPainted(false);
		jbMinus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomChanged(projectZoom.getNextZoomIn(projectWindow.getGenomeWindow().getSize()));
			}
		});

		jbPlus = new JButton("+");
		jbPlus.setMargin(new Insets(0, 3, 0, 3));
		jbPlus.setFocusPainted(false);
		jbPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				zoomChanged(projectZoom.getNextZoomOut(projectWindow.getGenomeWindow().getSize()));
			}
		});

		int	maximumZoom = projectWindow.getGenomeWindow().getChromosome().getLength() * 2;
		jsZoom = new JSlider(SwingConstants.HORIZONTAL, 0, projectZoom.getZoomIndex(maximumZoom), projectZoom.getZoomIndex(projectWindow.getGenomeWindow().getSize()));
		jsZoom.setMinorTickSpacing(1);
		jsZoom.setPaintTicks(true);
		jsZoom.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (projectZoom.getZoomIndex(projectWindow.getGenomeWindow().getSize()) != jsZoom.getValue()) {
					zoomChanged(projectZoom.getZoom(jsZoom.getValue()));
				}
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

		// Deactivate slider key listener (bother the main frame key event management)
		jsZoom.getInputMap().put(PAMoveLeft.ACCELERATOR, "none");
		jsZoom.getInputMap().put(PAMoveFarLeft.ACCELERATOR, "none");
		jsZoom.getInputMap().put(PAMoveRight.ACCELERATOR, "none");
		jsZoom.getInputMap().put(PAMoveFarRight.ACCELERATOR, "none");
	}


	/**
	 * Called when the zoom changes
	 * @param newZoom new zoom value
	 */
	void zoomChanged(int newZoom) {
		int currentZoom = projectWindow.getGenomeWindow().getSize();
		int	maximumZoom = projectWindow.getGenomeWindow().getChromosome().getLength() * 2;
		if (newZoom > maximumZoom) {
			newZoom = maximumZoom;
		}
		if (newZoom != currentZoom) {
			double halfZoom = newZoom / (double)2;
			Chromosome chromosome = projectWindow.getGenomeWindow().getChromosome();
			int start = (int)(projectWindow.getGenomeWindow().getMiddlePosition() - halfZoom);
			int stop = start + newZoom;
			SimpleGenomeWindow newGenomeWindow = new SimpleGenomeWindow(chromosome, start, stop);
			projectWindow.setGenomeWindow(newGenomeWindow);
		}
	}


	@Override
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		int currentZoom = projectWindow.getGenomeWindow().getSize();
		for (int i = 0; i < Math.abs(mwe.getWheelRotation()); i++) {
			if (mwe.getWheelRotation() > 0) {
				zoomChanged(projectZoom.getNextZoomIn(currentZoom));
			} else {
				zoomChanged(projectZoom.getNextZoomOut(currentZoom));
			}
		}
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		// we notify the gui
		int currentZoom = evt.getNewWindow().getSize();
		jlZoom.setText("Size: " + NumberFormat.getInstance().format(currentZoom));
		// if the chromosome changes we change the maximum zoom
		if (evt.getNewWindow().getChromosome() != evt.getOldWindow().getChromosome()) {
			int	oldMaximumZoom = evt.getOldWindow().getChromosome().getLength() * 2;
			int newMaximumZoom = evt.getNewWindow().getChromosome().getLength() * 2;
			// if the new zoom value is greatter than the old max zoom we change the max first
			if (currentZoom > oldMaximumZoom) {
				jsZoom.setMaximum(projectZoom.getZoomIndex(newMaximumZoom));
				jsZoom.setValue(projectZoom.getZoomIndex(currentZoom));

			} else {
				// else we change the value first because the new max could be smaller than the old value
				jsZoom.setValue(projectZoom.getZoomIndex(currentZoom));
				jsZoom.setMaximum(projectZoom.getZoomIndex(newMaximumZoom));
			}
		} else {
			jsZoom.setValue(projectZoom.getZoomIndex(currentZoom));
		}
	}

}
