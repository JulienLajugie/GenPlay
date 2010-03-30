/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A {@link FileFilter} for the 2bit files.
 * @author Julien Lajugie
 * @version 0.1
 */
public class TwoBitFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 6699798831056164420L; // generated ID
	public static final String[] EXTENSIONS = {"2bit"};
	public static final String DESCRIPTION = "DNA sequence 2bit files (*.2bit)";


	/**
	 * Creates an instance of {@link TwoBitFilter}
	 */	
	public TwoBitFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
