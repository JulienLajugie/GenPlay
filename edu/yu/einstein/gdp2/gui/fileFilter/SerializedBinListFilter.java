/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A {@link FileFilter} for the Serialized BinList files.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SerializedBinListFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -7644293615947149616L; // generated ID
	public static final String[] EXTENSIONS = {"bin"};
	public static final String DESCRIPTION = "Serialized Fixed Window Files (*.bin)";
	
	
	/**
	 * Creates an instance of {@link SerializedBinListFilter}
	 */
	public SerializedBinListFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
