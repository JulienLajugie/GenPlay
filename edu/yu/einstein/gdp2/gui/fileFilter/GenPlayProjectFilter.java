/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.util.Utils;


/**
 * A GenPlay project {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GenPlayProjectFilter extends ExtendedFileFilter {

	public static final String EXTENSION = "gen";
	public static final String DESCRIPTION = "GenPlay Project Files (*.gen)";

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
