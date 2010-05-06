/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JProgressBar;

/**
 * Progress bar of the {@link StatusBar}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ProgressBar extends JProgressBar {

	private static final long serialVersionUID = -3669001086333207235L; // generated ID
	private static final Color 	BACKGROUND_COLOR = Color.white; // color of the background of the progressbar
	private static final int 	HEIGHT = 15; 					// height of the progress bar
	
	/**
	 * Creates an instance of {@link ProgressBar}
	 */
	public ProgressBar() {
		super();
		// the progression a percentage between 0 and 100
		setMinimum(0);
		setMaximum(100);
		setMinimumSize(new Dimension(getPreferredSize().width, HEIGHT));
		setBackground(BACKGROUND_COLOR);
		setStringPainted(true);
	}


	/**
	 * Sets the level of completion showed on the {@link ProgressBar}
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		setIndeterminate(false);
		// set the progression position
		setValue(progress);
		// set the text on the progress bar
		setString(progress + "%");
	}

	/**
	 * Sets the progress bar indeterminate and don't print the string 
	 * on the progress bar if the parameter is true
	 */
	@Override
	public void setIndeterminate(boolean b) {
		super.setIndeterminate(b);
		setStringPainted(!b);
	}
}
