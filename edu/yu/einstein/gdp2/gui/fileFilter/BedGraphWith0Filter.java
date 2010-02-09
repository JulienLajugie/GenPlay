/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.util.Utils;


/**
 * A BedGraph {@link FileFilter} with the 0 value lines printed
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedGraphWith0Filter extends ExtendedFileFilter {

	public static final String EXTENSION = "bgr";
	public static final String DESCRIPTION = "BedGraph Files With Zero Value Lines (*.bgr)";
	
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
