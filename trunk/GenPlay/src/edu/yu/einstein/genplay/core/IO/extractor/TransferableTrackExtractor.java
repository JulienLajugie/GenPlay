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
package edu.yu.einstein.genplay.core.IO.extractor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.track.trackTransfer.TrackForTransfer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Extract a GenPlay Transferable Track File
 * @author Julien Lajugie
 */
public class TransferableTrackExtractor extends Extractor {

	private int firstBasePosition = 1;

	public TransferableTrackExtractor(File dataFile) {
		super(dataFile);
	}


	/**
	 * @return a {@link TrackForTransfer} from the data in the file
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public TrackForTransfer extract() throws ClassNotFoundException, IOException, IncompatibleAssembliesException {
		InputStream file = null;
		ObjectInput input = null;
		try {
			file = new FileInputStream(getDataFile());
			input = new ObjectInputStream (file);
			TrackForTransfer transTrack = (TrackForTransfer) (input.readObject());
			return transTrack;
		} finally {
			if (input != null) {
				input.close();
			}
			if (file != null) {
				file.close();
			}
		}
	}


	@Override
	public int getFirstBasePosition() {
		return firstBasePosition;
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		return Utils.getFileNameWithoutExtension(dataFile);
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		this.firstBasePosition = firstBasePosition;
	}
}
