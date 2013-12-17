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
package edu.yu.einstein.genplay.gui.statusBar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import edu.yu.einstein.genplay.util.colors.Colors;


/**
 * Status bar of the software with a progress bar and a memory usage display
 * @author Julien Lajugie
 * @version 0.1
 */
public final class StatusBar extends JPanel {

	private static final long serialVersionUID = 6145997500187047785L; // generated ID
	private final MemoryPanel 				memoryPanel;			// panel showing the memory usage
	private final ProgressBar				progressBar;			// progress bar
	private final StopButton				stopButton;				// stop button
	private final StatusLabel 				statusLabel;			// label in the middle of the bar
	private final GarbageCollectorButton	garbageCollectorButton;	// garbage collector button


	/**
	 * Creates an instance of {@link StatusBar}
	 */
	public StatusBar() {
		// we create the subcomponents
		progressBar = new ProgressBar();
		stopButton = new StopButton();
		statusLabel = new StatusLabel();
		memoryPanel = new MemoryPanel();
		garbageCollectorButton = new GarbageCollectorButton();

		// we add the subcomponents to the status bar
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.CENTER;

		gbc.insets = new Insets(5, 10, 5, 3);
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 0;
		add(progressBar, gbc);

		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridx++;
		add(stopButton, gbc);

		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx++;
		add(statusLabel, gbc);

		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx++;
		add(garbageCollectorButton, gbc);

		gbc.insets = new Insets(1, 3, 1, 1);
		gbc.gridx++;
		add(memoryPanel, gbc);
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.LIGHT_GREY));
	}


	/**
	 * Notifies the status bar the an action is starting.
	 * @param actionDescription description of the action
	 * @param stepCount number of steps needed to complete the action
	 * @param stoppable action to stop when the button is clicked
	 */
	public void actionStart(String actionDescription, int stepCount, Stoppable stoppable) {
		// initialize the progress bar
		progressBar.setProgress(0);
		progressBar.setIndeterminateAndUnpainted(true);
		// initialize the status label
		statusLabel.setDescription(actionDescription);
		statusLabel.setStep(1);
		statusLabel.setStepCount(stepCount);
		statusLabel.startCounter();
		// initialize the stop button
		stopButton.setStoppable(stoppable);
		garbageCollectorButton.setEnabled(false);
	}


	/**
	 * Notifies the status bar that the action is done
	 * @param resultStatus {@link String} describing the result of the action
	 */
	public void actionStop(String resultStatus) {
		// stop the progress bar
		progressBar.setIndeterminateAndUnpainted(false);
		progressBar.setProgress(100);
		// stop the status label
		statusLabel.stopCounter();
		statusLabel.setDescription(resultStatus);
		statusLabel.setStepCount(1); // set the step count to 1 so the step is not displayed anymore
		// disable the stop button
		stopButton.setStoppable(null);
		garbageCollectorButton.setEnabled(true);
	}


	/**
	 * Sets the progress of the action on the status bar
	 * @param step current step of the action
	 * @param progress progress of the action
	 */
	public void setProgress(int step, int progress) {
		progressBar.setProgress(progress);
		statusLabel.setStep(step);
	}


	/**
	 * Reinits the status to its default state
	 */
	public void reinit() {
		statusLabel.reinit();
		progressBar.setProgress(0);
		garbageCollectorButton.setEnabled(true);
	}


	/**
	 * Set the description label when starting a new project
	 */
	public void initDescriptionForFirstUse () {
		statusLabel.initDescriptionForFirstUse();
	}
}
