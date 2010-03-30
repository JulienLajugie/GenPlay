/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;

/**
 * A GDP {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GdpGeneFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 4695876018073509736L; // generated ID
	public static final String[] EXTENSIONS = {"gdp"};
	public static final String DESCRIPTION = "GenPlay Gene Files (*.gdp)";

	
	/**
	 * Creates an instance of {@link GdpGeneFilter}
	 */
	public GdpGeneFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
