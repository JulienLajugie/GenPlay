/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A BinListAsBedGraphWriter {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -7589647687815666483L; // generated ID
	public static final String[] EXTENSIONS = {"bed"};
	public static final String DESCRIPTION = "Bed Files (*.bed)";
	
	
	/**
	 * Creates an instance of {@link BedFilter}
	 */
	public BedFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
