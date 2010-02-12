/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.progressBar;


import java.awt.*;

import javax.swing.*; 
import javax.swing.border.EtchedBorder;


/**
 * Progress bar displayed during the loading of a file.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ProgressBar extends JInternalFrame {

	private static final long 	serialVersionUID = 8940806856659914685L;	// Generated serial number
	private final JProgressBar 	jpbWait;									// Progress bar
	
	/**
	 * Shows a progress bar displayed during the loading of a file.
	 * @param container {@link Container} displaying the progressbar popup
	 */
	public ProgressBar(Container container) {
		super("Operation in progress...", false, false, false, false);
		setResizable(false);
		setFrameIcon(null);
		// Create the progress bar
		jpbWait = new JProgressBar();
		jpbWait.setIndeterminate(true);
		JPanel jp = new JPanel(new GridBagLayout());
		jp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
	
		jp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;		
		jp.add(jpbWait, gbc);
			
		add(jp);
		pack();
		setVisible(true);
		setLocation((container.getWidth() - getWidth()) / 2, (container.getHeight() - getHeight()) / 2);
	}	
}

