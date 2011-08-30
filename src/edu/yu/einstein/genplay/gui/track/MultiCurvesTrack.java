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
package edu.yu.einstein.genplay.gui.track;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.GenomeWindow;

/**
 * A track showing multiples {@link CurveTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class MultiCurvesTrack extends ScoredTrack<CurveTrack<?>[]> {

	private static final long serialVersionUID = -8961218330334104474L; // generated ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
	}
	
	
	/**
	 * Creates an instance of {@link MultiCurvesTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param data array of {@link CurveTrack}
	 */
	public MultiCurvesTrack(GenomeWindow displayedGenomeWindow, int trackNumber, CurveTrack<?>[] data) {
		super(displayedGenomeWindow, trackNumber, data);
	}


	@Override
	protected TrackGraphics<CurveTrack<?>[]> createsTrackGraphics(GenomeWindow displayedGenomeWindow, CurveTrack<?>[] data) {
		return new MultiCurvesTrackGraphics(displayedGenomeWindow, data);
	}
}
