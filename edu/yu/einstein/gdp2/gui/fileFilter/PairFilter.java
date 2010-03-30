/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;

/**
 * A Pair {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PairFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -2669663045535948437L; // generated ID
	public static final String[] EXTENSIONS = {"pair"};
	public static final String DESCRIPTION = "Pair Files (*.pair)";
	
	
	/**
	 * Creates an instance of {@link PairFilter}
	 */
	public PairFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
	
}
