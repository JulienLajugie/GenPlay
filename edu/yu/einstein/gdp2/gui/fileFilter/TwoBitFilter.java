/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.util.Utils;


/**
 * A {@link FileFilter} for the 2bit files.
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitFilter extends ExtendedFileFilter {

	public static final String EXTENSION = "2bit";
	public static final String DESCRIPTION = "DNA sequence 2bit files (*.2bit)";

	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = Utils.getExtension(f);
		if ((extension != null) && (extension.equalsIgnoreCase(EXTENSION))) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public String[] getExtensions() {
		String[] extensions = {EXTENSION};
		return extensions;
	}
}
