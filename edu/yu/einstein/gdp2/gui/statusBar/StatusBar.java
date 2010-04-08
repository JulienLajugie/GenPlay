/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionEvent;
import yu.einstein.gdp2.gui.event.trackListActionEvent.TrackListActionListener;


/**
 * Status bar of the software with a progress bar and a memory usage display
 * @author Julien Lajugie
 * @version 0.1
 */
public class StatusBar extends JPanel implements TrackListActionListener{

	private static final long serialVersionUID = 6145997500187047785L; // generated ID
	private static final int 	PANEL_HEIGHT = 10; 	// height of the status bar
	private final MemoryPanel 	memoryPanel;		// panel showing the memory usage
	private final JProgressBar 	jpbProgress;		// progress bar
	private final JLabel 		jlAction;			// label in the middle of the bar
	private TimeCounter 		timeCounterThread;	// thread showing the time elapsed in the progress bar
	
	
	/**
	 * Thread displaying the time elapsed in the progress bar
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class TimeCounter extends Thread {
		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			jpbProgress.setStringPainted(true);
			jpbProgress.setFont(new Font("ARIAL", Font.PLAIN, 9));
			Thread thisThread = Thread.currentThread();
			while (timeCounterThread == thisThread) {
				try {
					long currentTime = System.currentTimeMillis();
					String timeString = new String(Long.toString((currentTime - startTime) / 1000) + "s");
					jpbProgress.setString(timeString);
					sleep(1000);					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			jpbProgress.setStringPainted(false);
		}
	};
	
	
	/**
	 * Creates an instance of {@link StatusBar}
	 */
	public StatusBar() {		
		jpbProgress = new JProgressBar();
		jpbProgress.setBackground(Color.white);
		jpbProgress.setMinimumSize(jpbProgress.getPreferredSize());
		jpbProgress.setSize(jpbProgress.getPreferredSize());
		jlAction = new JLabel();
		
		memoryPanel = new MemoryPanel();
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(5, 10, 5, 0);
		add(jpbProgress, gbc);

		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridx = 1;
		gbc.insets = new Insets(0, 0, 0, 0);
		add(jlAction, gbc);

		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridx = 2;		
		add(memoryPanel, gbc);
		
		setPreferredSize(new Dimension(getPreferredSize().width, PANEL_HEIGHT));
		setBorder(BorderFactory.createEtchedBorder());
	}
	

	@Override
	public void actionEnds(TrackListActionEvent evt) {
		jpbProgress.setIndeterminate(false);
		jlAction.setText(evt.getActionDescription());	
		timeCounterThread = null;
	}


	@Override
	public void actionStarts(TrackListActionEvent evt) {
		jpbProgress.setIndeterminate(true);
		jlAction.setText(evt.getActionDescription());
		timeCounterThread = new TimeCounter();
		timeCounterThread.start();
	}
}
