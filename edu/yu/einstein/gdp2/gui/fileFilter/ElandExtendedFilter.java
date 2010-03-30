/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A Eland Extended {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class ElandExtendedFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -8024581610634301136L; // generated ID
	public static final String[] EXTENSIONS = {"elx"};
	public static final String DESCRIPTION = "Eland Extended Files (*.elx)";
	
	
	/**
	 * Creates an instance of {@link ElandExtendedFilter}
	 */
	public ElandExtendedFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
