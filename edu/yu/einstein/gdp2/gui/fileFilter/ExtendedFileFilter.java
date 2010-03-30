/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import java.io.File;
import java.io.Serializable;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.util.Utils;


/**
 * Extension of the {@link FileFilter} class with a new method 
 * that returns the list of the extensions accepted by the filter
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ExtendedFileFilter extends FileFilter implements Serializable {

	private static final long serialVersionUID = -4860666388921247783L;	// generated ID
	private final String[] 	extensions;		// extensions accepted for this file type
	private final String 	description;	// description of the file type
	
	
	/**
	 * Creates an instance of {@link ExtendedFileFilter} 
	 * @param extensions extensions associated with the file filter
	 * @param description description of the file filter
	 */
	protected ExtendedFileFilter(String[] extensions, String description) {
		this.extensions = extensions;
		this.description = description;
	}
	
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = Utils.getExtension(f);
		if (extension == null) {
			return false;
		} else {
			for (String currentExtension: extensions) {
				if (currentExtension.equalsIgnoreCase(extension)) {
					return true;
				}
			}
			return false;
		}
	}

	
	@Override
	public String getDescription() {
		return description;
	}


	/**
	 * @return the list of the extensions accepted by the filter
	 */
	public String[] getExtensions() {
		return extensions;
	}
}
