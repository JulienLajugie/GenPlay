/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import yu.einstein.gdp2.util.Utils;


/**
 * A GFF {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GFFFilter extends FileFilter {

	public static final String EXTENSION = "gff";
	public static final String EXTENSION2 = "gtf";
	public static final String EXTENSION3 = "gff3";
	public static final String DESCRIPTION = "GFF Files (*.gff; *.gtf; *.gff3)";
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = Utils.getExtension(f);
		if ((extension != null) && ((extension.equalsIgnoreCase(EXTENSION))
				|| (extension.equalsIgnoreCase(EXTENSION2))
				|| (extension.equalsIgnoreCase(EXTENSION3)))) {
			return true;
		} else {
			return false;
		}
	}

	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
}