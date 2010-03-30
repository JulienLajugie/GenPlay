/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;

/**
 * A PSL {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public class PSLFilter extends ExtendedFileFilter {
	
	private static final long serialVersionUID = 1615779972078858623L;	// generated ID
	public static final String[] EXTENSIONS = {"psl"};
	public static final String DESCRIPTION = "PSL files (*.psl)";
	
	
	/**
	 * Creates an instance of {@link PSLFilter}
	 */
	public PSLFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}	
}
