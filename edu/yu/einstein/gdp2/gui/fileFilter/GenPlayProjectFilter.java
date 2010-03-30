/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A GenPlay project {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GenPlayProjectFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 3191118665245397752L; // generated ID
	public static final String[] EXTENSIONS = {"gen"};
	public static final String DESCRIPTION = "GenPlay Project Files (*.gen)";


	/**
	 * Creates an instance of {@link GenPlayProjectFilter}
	 */
	public GenPlayProjectFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
