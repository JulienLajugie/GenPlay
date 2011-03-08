/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
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
