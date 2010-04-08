/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Panel showing the memory usage of the application
 * @author Julien Lajugie
 * @version 0.1
 */
public class MemoryPanel extends JPanel {

	private static final long serialVersionUID = 5175483247820204875L; 	// generated ID
	private static final Runtime RUN_TIME = Runtime.getRuntime();		// application runtime
	private static final long B_TO_MB_FACTOR = 1048576;					// number of bytes in a megabyte
	private final JLabel jlMemory;										// label showing the memory usage


	/**
	 * Thread checking the memory usage and updating the label at regular interval
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class MemoryListener extends Thread {
		@Override
		public void run() {
			NumberFormat nf = new DecimalFormat("###,###,###");
			while (true) {
				long maxMemory =  RUN_TIME.maxMemory() / B_TO_MB_FACTOR;
				long usedMemory = (RUN_TIME.totalMemory() - RUN_TIME.freeMemory()) / B_TO_MB_FACTOR;
				int usedOnMax = (int)(usedMemory / (double)maxMemory * 100);
				jlMemory.setText(" " + nf.format(usedMemory) + " MB / " + nf.format(maxMemory) + " MB (" + usedOnMax + "%)");
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};


	/**
	 * Creates an instance of {@link MemoryPanel}
	 */
	public MemoryPanel() {
		super();				
		jlMemory = new JLabel();
		jlMemory.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.black));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;		
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(jlMemory, gbc);
		new MemoryListener().start();
	}
}
