/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


/**
 * Status bar of the software with a progress bar and a memory usage display
 * @author Julien Lajugie
 * @version 0.1
 */
public final class StatusBar extends JPanel {

	private static final long serialVersionUID = 6145997500187047785L; // generated ID
	private final MemoryPanel 	memoryPanel;		// panel showing the memory usage
	private final ProgressBar	progressBar;		// progress bar
	private final StopButton	stopButton;			// stop button
	private final StatusLabel 	statusLabel;		// label in the middle of the bar
	
		
	/**
	 * Creates an instance of {@link StatusBar}
	 */
	public StatusBar() {
		// we create the subcomponents
		progressBar = new ProgressBar();
		stopButton = new StopButton();
		statusLabel = new StatusLabel();		
		memoryPanel = new MemoryPanel();
		
		// we add the subcomponents to the status bar
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();		
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(5, 10, 5, 3);
		gbc.weightx = 0;
		gbc.weighty = 1;
		add(progressBar, gbc);

		gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 1;
		add(stopButton, gbc);		
		
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridx = 2;
		add(statusLabel, gbc);

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 3, 1, 1);
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 3;		
		add(memoryPanel, gbc);
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
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
		progressBar.setIndeterminate(true);
		// initialize the status label
		statusLabel.setDescription(actionDescription);
		statusLabel.setStep(1);
		statusLabel.setStepCount(stepCount);
		statusLabel.startCounter();		
		// initialize the stop button
		stopButton.setStoppable(stoppable);
	}
	
	
	/**
	 * Notifies the status bar that the action is done
	 * @param resultStatus {@link String} describing the result of the action 
	 */
	public void actionStop(String resultStatus) {
		// stop the progress bar
		progressBar.setIndeterminate(false);
		progressBar.setProgress(100);
		// stop the status label
		statusLabel.stopCounter();
		statusLabel.setDescription(resultStatus);
		statusLabel.setStepCount(1); // set the step count to 1 so the step is not displayed anymore
		// disable the stop button
		stopButton.setStoppable(null);
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
}
