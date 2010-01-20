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
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import yu.einstein.gdp2.util.ConfigurationManager;


/**
 * Panel of the {@link OptionDialog} that allows to save / restore the config file
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SaveRestoreOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 32933937591821971L; // generated ID

	private final JLabel 	jlSave;		// Label save
	private final JButton	jbSave;		// Button save
	private final JLabel 	jlRestore;	// Label restore
	private final JButton	jbRestore;	// Button restore
	
	
	/**
	 * Creates an instance of {@link SaveRestoreOptionPanel}
	 * @param configMmanager {@link ConfigurationManager}
	 */
	public SaveRestoreOptionPanel(ConfigurationManager configMmanager) {
		super("Save / Restore Default", configMmanager);
		
		jlSave = new JLabel("Save configuration:");
		jbSave = new JButton("Save");
		jbSave.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent evt) {
				try {
					cm.writeConfigurationFile();
					JOptionPane.showMessageDialog(getRootPane(), "The configuration has been saved", "Configuration saved", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(getRootPane(), "Error while saving the configuration", "Error", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}				
			}
		});
		
		jlRestore = new JLabel("Restore default configuration:");
		jbRestore = new JButton("Restore");
		jbRestore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cm.restoreDefault();
				firePropertyChange("reset", false, true);
			}
		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlSave, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbSave, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(10, 0, 0, 20);
		add(jlRestore, c);
		
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(10, 20, 0, 0);
		add(jbRestore, c);		
	}
}
