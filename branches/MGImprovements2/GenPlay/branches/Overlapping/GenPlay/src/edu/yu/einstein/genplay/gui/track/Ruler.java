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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowEvent;
import edu.yu.einstein.genplay.gui.event.genomeWindowEvent.GenomeWindowListener;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEvent;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventType;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackEventsGenerator;
import edu.yu.einstein.genplay.gui.event.trackEvent.TrackListener;
import edu.yu.einstein.genplay.util.Images;
import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * A ruler
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Ruler extends JPanel implements GenomeWindowListener, TrackListener, TrackEventsGenerator {

	private static final long serialVersionUID = -5243446035761988387L; // Generated ID
	private static final int 			HANDLE_WIDTH = 50;				// Width of the track handle
	private static final int 			TRACKS_SCROLL_WIDTH = 17;		// Width of the scroll bar
	private static final int 			RULER_HEIGHT = 20;				// Height of the ruler
	private final RulerGraphics			rulerGraphics;					// Graphics part
	private final JButton 				rulerButton;					// button of the ruler
	private final List<TrackListener> 	trackListeners;					// list of track listeners

	/**
	 * Creates an instance of {@link Ruler}
	 */
	public Ruler() {
		rulerGraphics = new RulerGraphics();
		rulerGraphics.addTrackListener(this);
		rulerButton = new JButton();
		trackListeners = new ArrayList<TrackListener>();
		initButton();
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(rulerButton, gbc);
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, TRACKS_SCROLL_WIDTH);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(rulerGraphics, gbc);
		setMinimumSize(new Dimension(getPreferredSize().width, RULER_HEIGHT));
	}


	/**
	 * Initializes the button of the ruler
	 */
	private void initButton() {
		rulerButton.setBackground(Colors.WHITE);
		rulerButton.setMargin(new Insets(0, 0, 0, 0));
		rulerButton.setFocusPainted(false);
		rulerButton.setIcon(new ImageIcon(Images.getToolsImage()));
		rulerButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Colors.LIGHT_GREY));
		rulerButton.setPreferredSize(new Dimension(HANDLE_WIDTH + 1, RULER_HEIGHT));
		rulerButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				rulerButton.setBackground(Colors.GREY);
				super.mouseEntered(e);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				rulerButton.setBackground(Colors.WHITE);
				super.mouseExited(e);
			}
		});
	}


	/**
	 * Turns the scroll mode on / off
	 * @param scrollMode
	 */
	public void setScrollMode(boolean scrollMode) {
		rulerGraphics.setScrollMode(scrollMode);
	}


	/**
	 * @return the main button of the application attached to the Ruler
	 */
	public JButton getOptionButton() {
		return rulerButton;
	}


	@Override
	public void genomeWindowChanged(GenomeWindowEvent evt) {
		rulerGraphics.repaint();
	}


	/**
	 * Locks the ruler (the button)
	 */
	public void lock() {
		rulerButton.setEnabled(false);
	}


	/**
	 * Unlocks the ruler (the button)
	 */
	public void unlock() {
		rulerButton.setEnabled(true);
	}


	/**
	 * @return the current middle position
	 */
	public int getCurrentMiddlePosition () {
		return rulerGraphics.getCurrentMiddlePosition();
	}


	@Override
	public void addTrackListener(TrackListener trackListener) {
		if (!trackListeners.contains(trackListener)) {
			trackListeners.add(trackListener);
		}
	}


	@Override
	public TrackListener[] getTrackListeners() {
		TrackListener[] listeners = new TrackListener[trackListeners.size()];
		return trackListeners.toArray(listeners);
	}


	@Override
	public void removeTrackListener(TrackListener trackListener) {
		trackListeners.remove(trackListener);
	}


	/**
	 * Notifies all the track listeners that the track has changed
	 * @param trackEventType track event type
	 */
	public void notifyTrackListeners(TrackEventType trackEventType) {
		TrackEvent trackEvent = new TrackEvent(this, trackEventType);
		for (TrackListener listener: trackListeners) {
			listener.trackChanged(trackEvent);
		}
	}


	@Override
	public void trackChanged(TrackEvent arg0) {
		// we relay the other events to the element that contains this track
		notifyTrackListeners(arg0.getEventType());
	}
}
