/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A BedGraph {@link FileFilter} with the 0 value lines printed
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedGraphWith0Filter extends ExtendedFileFilter {

	private static final long serialVersionUID = -3121884315076204776L; // generated ID
	public static final String[] EXTENSIONS = {"bgr"};
	public static final String DESCRIPTION = "BedGraph Files With Zero Value Lines (*.bgr)";
	
	
	/**
	 * Creates an instance of {@link BedGraphWith0Filter}
	 */
	public BedGraphWith0Filter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
