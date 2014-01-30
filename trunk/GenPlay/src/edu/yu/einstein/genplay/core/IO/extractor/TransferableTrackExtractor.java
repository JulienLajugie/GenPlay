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
import java.io.ObjectInputStream;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.TransferableTrack;


/**
 * Extractor for the serialized {@link TransferableTrack}
 * @author Julien Lajugie
 */
public class TransferableTrackExtractor extends Extractor {

	/**
	 * Creates an instance of {@link TransferableTrackExtractor}
	 * @param dataFile
	 * @throws SerializationException
	 */
	public TransferableTrackExtractor(File dataFile) throws SerializationException {
		super(dataFile);
	}


	/**
	 * @return the track from the file
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws IncompatibleAssembliesException if the serialized track is not compatible with the project
	 */
	public Track extractTrack() throws IOException, ClassNotFoundException, IncompatibleAssembliesException {
		ObjectInputStream ois = null;
		try {
			FileInputStream in = new FileInputStream(getDataFile());
			ois = new ObjectInputStream(in);
			TransferableTrack transTrack = (TransferableTrack) (ois.readObject());
			if (transTrack.getAssemblyName().equals(ProjectManager.getInstance().getAssembly().getName())) {
				throw new IncompatibleAssembliesException("The assembly of the source file is not compatible with the assembly of the project");
			}
			return transTrack.getTrackToTransfer();
		} finally {
			if (ois != null) {
				ois.close();
			}
		}
	}


	@Override
	public int getFirstBasePosition() {
		return 0;
	}


	@Override
	protected String retrieveDataName(File dataFile) {
		return null;
	}


	@Override
	public void setFirstBasePosition(int firstBasePosition) {
		// do nothing
	}
}
