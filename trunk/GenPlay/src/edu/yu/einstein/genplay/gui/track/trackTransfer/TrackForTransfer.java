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
package edu.yu.einstein.genplay.gui.track.trackTransfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.MultiGenomeProject;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.exceptions.IncompatibleAssembliesException;
import edu.yu.einstein.genplay.gui.track.Track;

/**
 * This is a wrapper around {@link Track}. It adds information such as the assembly name and the genome name
 * so when the track is transfered, the compatibility between the transfered track and the target project can
 * be checked.
 * @author Julien Lajugie
 */
public final class TrackForTransfer implements Serializable {

	/** generated serial ID */
	private static final long serialVersionUID = 4675091809754723764L;

	/** saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version

	private Track 			trackToTransfer;	// track to transfer
	private String 			assemblyName;		// assembly of the track to transfer
	private List<String>	genomeNames;		// name of the genomes in the case of a multi-genome project


	/**
	 * Creates an instance of {@link TrackForTransfer}
	 * @param trackToTransfer the track that needs to be transfered
	 */
	public TrackForTransfer(Track trackToTransfer) {
		this.trackToTransfer = trackToTransfer;
		ProjectManager pm = ProjectManager.getInstance();
		assemblyName = pm.getAssembly().getName();
		if (pm.isMultiGenomeProject()) {
			genomeNames = pm.getMultiGenomeProject().getGenomeNames();
		} else {
			genomeNames = null;
		}
	}


	/**
	 * @return the track to transfer
	 */
	public Track getTrackToTransfer() {
		return trackToTransfer;
	}


	/**
	 * @return true if the transferable track is compatible with the current project
	 * (ie: same assembly, same multi-genome profile)
	 */
	private boolean isTransferableTrackCompatibleWithCurrentProject() {
		ProjectManager pm = ProjectManager.getInstance();
		if ((pm.getAssembly() != null) && (!assemblyName.equals(pm.getAssembly().getName()))) {
			return false;
		}

		if (pm.isMultiGenomeProject()) {
			MultiGenomeProject mgp = pm.getMultiGenomeProject();
			if ((genomeNames == null) && (mgp.getGenomeNames() != null)) {
				return false;
			}
			if (genomeNames.size() != mgp.getGenomeNames().size()) {
				return false;
			}
			for (int i = 0; i < genomeNames.size(); i++) {
				if (!genomeNames.get(i).equals(mgp.getGenomeNames().get(i))) {
					return false;
				}
			}
		} else {
			if (genomeNames != null) {
				return false;
			}
		}
		return true;
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt(); // read the saved format
		trackToTransfer = (Track) in.readObject();
		assemblyName = (String) in.readObject();
		genomeNames = (List<String>) in.readObject();
		// check if the unserialized track is compatible with the current project
		if (!isTransferableTrackCompatibleWithCurrentProject()) {
			throw new IncompatibleAssembliesException("The track cannot be pasted because the source "
					+ "and the target assemblies are not compatible.");
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(trackToTransfer);
		out.writeObject(assemblyName);
		out.writeObject(genomeNames);
	}
}
