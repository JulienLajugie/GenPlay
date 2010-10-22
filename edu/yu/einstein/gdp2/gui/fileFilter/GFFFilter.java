/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A GFF {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GFFFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = 1615779972078858623L;	// generated ID
	public static final String[] EXTENSIONS = {"gff", "gff3"};
	public static final String DESCRIPTION = "GFF Files (*.gff; *.gff3)";
	
	
	/**
	 * Creates an instance of {@link GFFFilter}
	 */
	public GFFFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}	
}
