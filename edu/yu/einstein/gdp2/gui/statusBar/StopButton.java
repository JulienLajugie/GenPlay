/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.statusBar;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


/**
 * Stop button of the status bar. The button stops the current operation in the thread pool when clicked
 * @author Julien Lajugie
 * @version 0.1
 */
final class StopButton extends JButton implements ActionListener {
	
	private static final long serialVersionUID = 8260242568878040712L; 		// generated ID	
	private static final Color ENABLED_COLOR = Color.red;					// color of the button when enabled
	private static final Color DISABLED_COLOR = new Color(200, 175, 175);	// color of the button when disabled
	private Stoppable stoppable = null;	// stoppable to stop when the button is clicked
	
	
	/**
	 * Creates an instance of a {@link StopButton}
	 */
	StopButton() {
		setBackground(Color.red);
		setMargin(new Insets(4, 4, 4, 4));
		setFocusPainted(false);
		setEnabled(false);
		addActionListener(this);
	}

	
	/**
	 * Sets the stoppable to stop when the button is clicked.
	 * Disables the button if the stoppable is null 
	 * @param stoppable a {@link Stoppable}
	 */
	void setStoppable(Stoppable stoppable) {
		this.stoppable = stoppable;
		setEnabled(stoppable != null);		
	}
	
	
	/**
	 * Changes the color of the button when the enabled state changes
	 */
	@Override
	public void setEnabled(boolean b) {
		if (b) {
			setBackground(ENABLED_COLOR);
		} else {
			setBackground(DISABLED_COLOR);
		}
		super.setEnabled(b);		
	}


	/**
	 * Stops the stoppable when the button is clicked if the stoppable is not null.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (stoppable != null) {
			stoppable.stop();
		}
	}
}
