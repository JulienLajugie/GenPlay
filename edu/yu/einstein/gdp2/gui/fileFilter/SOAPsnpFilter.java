/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A {@link FileFilter} for the SOAPsnp files.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SOAPsnpFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -8560834231830725642L; // generated ID
	public static final String[] EXTENSIONS = {"SNP"};
	public static final String DESCRIPTION = "SOAP SNP files (*.snp)";
	
	
	/**
	 * Creates an instance of {@link SOAPsnpFilter}
	 */
	public SOAPsnpFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
