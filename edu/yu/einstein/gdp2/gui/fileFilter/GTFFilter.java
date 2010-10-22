/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A GTF {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public class GTFFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 3916531769336913421L; // generated ID
	public static final String[] EXTENSIONS = {"gtf"};
	public static final String DESCRIPTION = "GTF Files (*.gtf)";
	
	
	/**
	 * Creates an instance of {@link GTFFilter}
	 */
	public GTFFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}	
}
