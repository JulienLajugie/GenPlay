/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * Extension of the {@link FileFilter} class with a new method 
 * that returns the list of the extensions accepted by the filter
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class ExtendedFileFilter extends FileFilter {

	/**
	 * @return the list of the extensions accepted by the filter
	 */
	public abstract String[] getExtensions();
}
