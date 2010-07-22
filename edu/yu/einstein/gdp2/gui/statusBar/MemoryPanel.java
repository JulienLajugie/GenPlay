/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;


/**
 * Panel showing the memory usage of the application
 * @author Julien Lajugie
 * @version 0.1
 */
public final class MemoryPanel extends JProgressBar {

	private static final long serialVersionUID = 5175483247820204875L; 	// generated ID
	private static final int 		HEIGHT = 15; 						// height of the progress bar
	private static final Runtime 	RUN_TIME = Runtime.getRuntime();	// application runtime
	private static final long 		B_TO_MB_FACTOR = 1048576;			// number of bytes in a megabyte


	/**
	 * Thread checking the memory usage and updating the label at regular interval
	 * @author Julien Lajugie
	 * @version 0.1
	 */
	private class MemoryListener extends Thread {
		@Override
		public void run() {
			while (true) {
				long maxMemory =  RUN_TIME.maxMemory() / B_TO_MB_FACTOR;
				long usedMemory = (RUN_TIME.totalMemory() - RUN_TIME.freeMemory()) / B_TO_MB_FACTOR;
				setMemory(maxMemory, usedMemory);
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
	MemoryPanel() {
		super(0, 100);				
		setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
		setMinimumSize(new Dimension(getPreferredSize().width, HEIGHT));
		setStringPainted(true);
		new MemoryListener().start();
	}
	
	
	/**
	 * Changes the color of the string painted on the memory panel
	 */
	void customizeUI() {
		final Color stringColor = UIManager.getColor("Label.foreground");
		BasicProgressBarUI ui = new BasicProgressBarUI() {
		    protected Color getSelectionBackground() {
		        return stringColor; // string color over the background
		    }
		    protected Color getSelectionForeground() {
		        return stringColor;	// string color over the foreground
		    }
		};
		setUI(ui);
	}
	
	
	@Override
	public void updateUI() {
		super.updateUI();
		customizeUI();
	}
	
	
	/**
	 * Sets the memory string, the color of the progress bar and the value of the progress 
	 * @param maxMemory memory available
	 * @param usedMemory used memory
	 */
	private synchronized void setMemory(long maxMemory, long usedMemory) {
		NumberFormat nf = new DecimalFormat("###,###,###");
		int usedOnMax = (int)(usedMemory / (double)maxMemory * 100);
		setValue(usedOnMax);
		Color foregroundColor = memoryToColor(usedOnMax);
		setForeground(foregroundColor);
		setString(" " + nf.format(usedMemory) + " MB / " + nf.format(maxMemory) + " MB (" + usedOnMax + "%)");
	}
	
	
	/**
	 * Associates a color to a memory usage. 
	 * Green = low usage / Yellow = medium usage / Red = high usage 
	 * @param memoryUsage a memory usage
	 * @return a Color
	 */
	private Color memoryToColor(int memoryUsage) {
		int red = 255;
		int green = 255;
		int blue = 0;
		if (memoryUsage < 50) {
			red = (int) (memoryUsage / 50d * 255);
		} else {
			green = (int) (255 - (memoryUsage - 50) / 50d * 255);			
		}
		return new Color(red, green, blue);
	}
}
