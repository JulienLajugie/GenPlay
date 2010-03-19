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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import yu.einstein.gdp2.util.ConfigurationManager;


/**
 * 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ConfigFileOptionPanel extends OptionPanel {

	private static final long serialVersionUID = 4936841930455874582L; // generated ID

	private final JLabel 		jlZoomFile;		// Label zoom file
	private final JTextField 	jtfZoomFile;	// TextField zoom file 
	private final JButton 		jbZoomBrowse;	// Button browse zoom file
	private final JLabel 		jlChromoFile;	// Label chromosome file
	private final JTextField 	jtfChromoFile;	// TextField chromosome file 
	private final JButton 		jbChromoBrowse;	// Button browse chromosome file
	private final JLabel		jlRestart;		// label telling the user to restart the application
	
	
	/**
	 * Creates an instance of {@link ConfigFileOptionPanel}
	 * @param configMmanager a {@link ConfigurationManager}
	 */
	public ConfigFileOptionPanel(ConfigurationManager configMmanager) {
		super("Configuration Files", configMmanager);
		
		jlZoomFile = new JLabel("Zoom configuration file: ");
		if (cm.getZoomFile() == null) {
			jtfZoomFile = new JTextField();
		} else {
			jtfZoomFile = new JTextField(new File(cm.getZoomFile()).getAbsolutePath());
		}
		jtfZoomFile.setColumns(30);
		jtfZoomFile.setEditable(false);
		jtfZoomFile.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				cm.setZoomFile(jtfZoomFile.getText());
			}
		});
		
		jbZoomBrowse = new JButton("Browse");
		jbZoomBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse("Zoom File:", new File(cm.getZoomFile()), jtfZoomFile, true);				
			}
		});
		
		jlChromoFile = new JLabel("Chromosome configuration file: ");		
		if (cm.getChromosomeFile() == null) {
			jtfChromoFile = new JTextField();
		} else {
			jtfChromoFile = new JTextField(new File(cm.getChromosomeFile()).getAbsolutePath());
		}
		jtfChromoFile.setColumns(30);
		jtfChromoFile.setEditable(false);
		jtfChromoFile.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				cm.setChromosomeFile(jtfChromoFile.getText());
			}
		});
		
		jbChromoBrowse = new JButton("Browse");
		jbChromoBrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse("Chromosome File:", new File(cm.getChromosomeFile()), jtfChromoFile, true);				
			}
		});
		
		jlRestart = new JLabel("Restart the application to take these modifications into account");
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlZoomFile, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfZoomFile, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;		
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbZoomBrowse, c);
		
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(10, 0, 0, 0);
		add(jlChromoFile, c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 0, 0);
		add(jtfChromoFile, c);
		
		c.gridx = 1;
		c.gridy = 5;
		c.gridwidth = 1;		
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		add(jbChromoBrowse, c);

		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 2;
		c.insets = new Insets(50, 0, 0, 0);
		c.anchor = GridBagConstraints.CENTER;
		add(jlRestart, c);		
	}
}
