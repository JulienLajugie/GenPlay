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

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;

import edu.yu.einstein.genplay.core.GenomeWindow;
import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.manager.project.ProjectWindow;
import edu.yu.einstein.genplay.gui.action.multiGenome.properties.MGAProperties;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.colors.Colors;

/**
 * This panel gathers two elements:
 * - the panel that contains the button for the multi genome properties dialog
 * - the scroll bar
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TopPanel extends JPanel implements AdjustmentListener, MouseWheelListener, GenomeWindowListener {

	/** Generated default version ID */
	private static final long serialVersionUID = 2637751583693743095L;

	private static final int HANDLE_WIDTH 			= 50;	// Width of the track handle
	private static final int TRACKS_SCROLL_WIDTH 	= 17;	// Width of the scroll bar
	private final 	JScrollBar 		jsbPosition;			// scroll bar to modify the position
	private final 	ProjectWindow	projectWindow;			// Instance of the Genome Window Manager
	private 		JButton 		jbMultiGenome;			// button for the multi genome properties dialog
	private 		JPopupMenu		popupMenu;				// popup-menu


	/**
	 * Constructor of {@link TopPanel}
	 */
	TopPanel () {
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
		setLayout(layout);

		this.projectWindow = ProjectManager.getInstance().getProjectWindow();
		int currentPosition = (int)projectWindow.getGenomeWindow().getMiddlePosition();
		int currentSize = projectWindow.getGenomeWindow().getSize();
		Chromosome currentChromosome = projectWindow.getGenomeWindow().getChromosome();
		jsbPosition = new JScrollBar(Adjustable.HORIZONTAL, currentPosition, currentSize, 0, currentChromosome.getLength() + currentSize);
		jsbPosition.setBlockIncrement(currentSize / 10);
		jsbPosition.setUnitIncrement(currentSize / 10);
		jsbPosition.addAdjustmentListener(this);

		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.weighty = 1.0;
		gbc.gridwidth = 1;

		// We add the button only if it is a multi genome project
		if (ProjectManager.getInstance().isMultiGenomeProject()) {
			// Initializes the button
			initializesMultiGenomeButton();

			// add the button
			add(jbMultiGenome, gbc);

			// update constraints for the scroll bar
			gbc.gridx++;
			gbc.insets = new Insets(0, 0, 0, 0);
		} else {
			gbc.insets = new Insets(0, HANDLE_WIDTH, 0, 0);
		}

		gbc.weightx = 1;
		add(jsbPosition, gbc);

		addMouseWheelListener(this);
	}


	/**
	 * Initializes the multi genome button
	 */
	private void initializesMultiGenomeButton () {
		// creates the button
		jbMultiGenome = new JButton(new ImageIcon(Images.getDNAImage()));

		// creates the button popup menu
		popupMenu = new MGButtonPopupMenu();

		// sets some attributes
		Dimension buttonDimension = new Dimension(HANDLE_WIDTH, TRACKS_SCROLL_WIDTH - 1);
		jbMultiGenome.setBackground(Colors.WHITE);
		jbMultiGenome.setMargin(new Insets(0, 0, 0, 0));
		jbMultiGenome.setFocusPainted(false);
		jbMultiGenome.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, Colors.LIGHT_GREY));
		jbMultiGenome.setPreferredSize(buttonDimension);
		jbMultiGenome.setToolTipText("Show the Multi Genome Properties Dialog");

		// defines the action listener
		jbMultiGenome.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MGAProperties action = new MGAProperties();
				action.actionPerformed(null);
			}
		});

		// defines the mouse listener
		jbMultiGenome.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				jbMultiGenome.setBackground(Colors.GREY);
				super.mouseEntered(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				jbMultiGenome.setBackground(Colors.WHITE);

				super.mouseExited(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(),
							e.getX(), e.getY());
				}
			}
		});
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
