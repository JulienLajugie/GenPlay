/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import yu.einstein.gdp2.core.list.binList.operation.OperationPool;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressEvent;
import yu.einstein.gdp2.gui.event.operationProgressEvent.OperationProgressListener;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;


/**
 * Status bar of the software with a progress bar and a memory usage display
 * @author Julien Lajugie
 * @version 0.1
 */
public class StatusBar extends JPanel implements TrackListActionListener, OperationProgressListener {

	private static final long serialVersionUID = 6145997500187047785L; // generated ID
	private final MemoryPanel 	memoryPanel;		// panel showing the memory usage
	private final ProgressBar	progressBar;		// progress bar
	private final StopButton	stopButton;			// stop button
	private final StatusLabel 	statusLabel;		// label in the middle of the bar
	private int 				step;				// operation current step
	
		
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
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridx = 3;		
		add(memoryPanel, gbc);
		
		setBorder(BorderFactory.createEtchedBorder());
		// add itself as a listener of the OperationPool 
		OperationPool.getInstance().addOperationProgressListener(this);
	}
	

	@Override
	public void actionEnds(TrackListActionEvent evt) {
		statusLabel.actionEnds(evt);
		progressBar.setProgress(100);
		progressBar.setIndeterminate(false);
	}


	@Override
	public void actionStarts(TrackListActionEvent evt) {
		statusLabel.actionStarts(evt);
		progressBar.setProgress(0);
		progressBar.setIndeterminate(true);
		step = 1;
	}


	@Override
	public void operationProgressChanged(OperationProgressEvent evt) {
		// we set the state of the progress bar anyway
		progressBar.setProgress((int) evt.getCompletion());
		if (evt.getState() == OperationProgressEvent.STARTING) {
			// when the operation starts
			stopButton.setEnabled(true);	
			statusLabel.setStep(step);
		} else if (evt.getState() == OperationProgressEvent.COMPLETE) {
			// when the operation is done but the action not necessary finished
			stopButton.setEnabled(false);
			step++;
			// set the progress bar indeterminate so if there is something to finalize
			// the progress bar is still busy
			progressBar.setIndeterminate(true);
		} else if (evt.getState() == OperationProgressEvent.ABORT) {
			// when the operation is aborted
			stopButton.setEnabled(false);
			step = 0;
			statusLabel.setStep(step);
			statusLabel.setDescription("Aborting Operation");
		}
	}
}
