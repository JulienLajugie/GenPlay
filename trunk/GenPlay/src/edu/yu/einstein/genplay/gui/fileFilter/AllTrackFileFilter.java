/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.fileFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileFilter;


/**
 * A BAM {@link FileFilter}
 * @author Julien Lajugie
 */
public class AllTrackFileFilter extends ExtendedFileFilter {

	/** Generated serial ID */
	private static final long serialVersionUID = 8472257739111079051L;

	/** File type description */
	public static final String DESCRIPTION = "Track Data";

	/** Valid extensions */
	public static String[] extensions;

	static {
		ExtendedFileFilter[] trackFilters = {
				new BAMFilter(),
				new BedFilter(),
				new BedGraphFilter(),
				new ElandExtendedFilter(),
				new GenPlayTrackFilter(),
				new GFFFilter(),
				new GTFFilter(),
				new PairFilter(),
				new PSLFilter(),
				new SAMFilter(),
				new TwoBitFilter(),
				new WiggleFilter()
		};
		List<String> extensionList = new ArrayList<String>();
		for (ExtendedFileFilter filter: trackFilters) {
			extensionList.addAll(Arrays.asList(filter.getExtensions()));
		}
		extensions = extensionList.toArray(new String[0]);
	}


	/**
	 * Creates an instance of {@link AllTrackFileFilter}
	 */
	public AllTrackFileFilter() {
		super(extensions, DESCRIPTION);
	}
}
