/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A BedGraph {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedGraphFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -1006499835810531469L; // generated ID
	public static final String[] EXTENSIONS = {"bgr"};
	public static final String DESCRIPTION = "BedGraph Files (*.bgr)";
	
	
	/**
	 * Creates an instance of {@link BedGraphFilter}
	 */
	public BedGraphFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
