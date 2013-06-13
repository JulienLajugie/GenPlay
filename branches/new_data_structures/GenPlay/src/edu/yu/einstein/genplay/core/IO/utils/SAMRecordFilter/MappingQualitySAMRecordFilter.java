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
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.IO.utils.SAMRecordFilter;

import java.security.InvalidParameterException;

import net.sf.samtools.SAMRecord;

/**
 * Filters out {@link SAMRecord} with a quality value smaller than
 * @author Julien Lajugie
 */
public class MappingQualitySAMRecordFilter implements SAMRecordFilter {

	/** Maximum valid quality value of a SAM read */
	private final int MAX_QUALITY_VALUE = 255;
	/** Minimum valid quality value of a SAM read */
	private final int MIN_QUALITY_VALUE = 0;
	/** Will reject all records with a quality value smaller than this threshold */
	private final int qualityThreshold;


	/**
	 * Creates an instance of {@link MappingQualitySAMRecordFilter}
	 * @param qualityThreshold this filter will reject all records with a quality value smaller than this threshold
	 */
	public MappingQualitySAMRecordFilter(int qualityThreshold) {
		if ((qualityThreshold < MIN_QUALITY_VALUE) || (qualityThreshold > MAX_QUALITY_VALUE)) {
			throw new InvalidParameterException("Quality value must be between " + MIN_QUALITY_VALUE + " and " + MAX_QUALITY_VALUE);
		}
		this.qualityThreshold = qualityThreshold;
	}


	@Override
	public SAMRecord applyFilter(SAMRecord samRecord) {
		int recordQuality = samRecord.getMappingQuality();
		return recordQuality < qualityThreshold ? null : samRecord;
	}
}
