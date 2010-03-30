/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;

/**
 * A Wiggle {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class WiggleFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 6441730682478032544L; // generated ID
	public static final String[] EXTENSIONS = {"wig"};
	public static final String DESCRIPTION = "Wiggle Files (*.wig)";
	
	
	/**
	 * Creates an instance of {@link WiggleFilter}
	 */
	public WiggleFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
