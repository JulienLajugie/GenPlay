/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.dialog.optionDialog;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import yu.einstein.gdp2.util.ConfigurationManager;


/**
 * Right panel of an {@link OptionDialog}
 * Defines the common attributes of the different panels of the configuration frame.
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class OptionPanel extends JPanel {

	private static final long serialVersionUID = 4821469631755757767L;	// Generated serial number
	protected  final ConfigurationManager 	cm;				// ConfigurationManager


	/**
	 * Constructor. Creates an instance of {@link OptionPanel}
	 * @param name name of the category of configuration
	 * @param configMmanager a {@link ConfigurationManager}
	 */
	protected OptionPanel(String name, ConfigurationManager configMmanager) {
		super();
		setName(name);
		this.cm = configMmanager;		
	}


	/**
	 * Override of toString use for the JTree in order to set the name of a category. 
	 */
	@Override
	public String toString() {
		return getName();
	}
	
	
	/**
	 * Open a file choose and set the text field with the chosen value
	 * @param title	title of the open dialog
	 * @param currentFile Name of the current log file
	 * @param textField a {@link JTextField}
	 * @param chooseFile true to choose a file, false to choose a directory
	 */
	protected void browse(String title, File currentFile, JTextField textField, boolean chooseFile) {
		JFileChooser jfc = new JFileChooser();		
		if (chooseFile) {
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		} else {
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		jfc.setSelectedFile(currentFile.getAbsoluteFile());
		jfc.setDialogTitle(title);
		int returnVal =jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			textField.setText(jfc.getSelectedFile().toString());
		}
	}
}
