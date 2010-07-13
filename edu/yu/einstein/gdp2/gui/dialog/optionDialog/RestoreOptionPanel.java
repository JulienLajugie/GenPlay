/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.optionDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * Panel of the {@link OptionDialog} that allows to restore the config file
 * @author Julien Lajugie
 * @version 0.1
 */
final class RestoreOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 32933937591821971L; // generated ID
	private final JLabel jlRestore; 	// Label restore
	private final JButton jbRestore; 	// Button restore

	
	/**
	 * Creates an instance of {@link RestoreOptionPanel}
	 */
	RestoreOptionPanel() {
		super("Restore Default");
		jlRestore = new JLabel("Restore default configuration:");
		jbRestore = new JButton("Restore");
		jbRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				configurationManager.restoreDefault();
				firePropertyChange("reset", false, true);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlRestore, c);

		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbRestore, c);
	}
}
