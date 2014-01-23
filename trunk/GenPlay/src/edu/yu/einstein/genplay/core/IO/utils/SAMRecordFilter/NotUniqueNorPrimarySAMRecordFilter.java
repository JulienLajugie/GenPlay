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
package edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter;

import net.sf.samtools.SAMRecord;

/**
 * Filters out {@link SAMRecord} that are not uniquely aligned and that are not primary alignments
 * @author Julien Lajugie
 */
public class NotUniqueNorPrimarySAMRecordFilter implements SAMRecordFilter {

	@Override
	public SAMRecord applyFilter(SAMRecord samRecord) {
		Character typeBWAFlag = (Character) samRecord.getAttribute("XT");
		if (samRecord.getNotPrimaryAlignmentFlag()) {
			return ((typeBWAFlag != null) && (typeBWAFlag == 'U')) ? samRecord : null;
		} else {
			return samRecord;
		}
	}
}
