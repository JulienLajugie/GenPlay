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
package edu.yu.einstein.genplay.core.manager.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.display.MGMultiGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenome {

	private		List<String>					genomeNames;				// genome names list
	private 	Map<String, List<VCFReader>> 	genomeFileAssociation;		// mapping between genome names and their reader.

	private 	MGMultiGenome 					multiGenome;
	private 	MGMultiGenomeForDisplay 		multiGenomeForDisplay;

	private		MGSynchronizer					multiGenomeSynchronizer;


	/**
	 * Constructor of {@link MultiGenome}
	 */
	public MultiGenome () {
		
	}


	/**
	 * Initializes synchronizer attributes.
	 * @param genomeFileAssociation	the genome file association
	 */
	public void initializeSynchronization (Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
		this.genomeNames = new ArrayList<String>(this.genomeFileAssociation.keySet());
		Collections.sort(genomeNames);
		this.multiGenome = new MGMultiGenome(genomeNames);
		this.multiGenomeSynchronizer = new MGSynchronizer(this);
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Genome names methods


	/**
	 * @return the genomeNames
	 */
	public List<String> getGenomeNames() {
		return genomeNames;
	}


	/**
	 * Retrieves all the genome raw names of the project
	 * @return the full list of genome raw name
	 */
	public List<String> getAllGenomeRawNames () {
		List<String> genomeRawNames = new ArrayList<String>();
		for (String genomeName: genomeNames) {
			genomeRawNames.add(FormattedMultiGenomeName.getRawName(genomeName));
		}
		return genomeRawNames;
	}


	/**
	 * Creates an array with all genome names association.
	 * Used for display.
	 * @return	genome names association array
	 */
	public Object[] getFormattedGenomeArray () {
		String[] names = new String[genomeNames.size() + 1];
		names[0] = ProjectManager.getInstance().getAssembly().getDisplayName();
		int index = 1;
		for (String name: genomeNames) {
			names[index] = name;
			index++;
		}
		return names;
	}
	

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Others


	/**
	 * Retrieves all the VCF readers
	 * @return the full list of VCF reader
	 */
	public List<VCFReader> getAllReaders () {
		List<VCFReader> readerList = new ArrayList<VCFReader>();
		List<File> readerPath = new ArrayList<File>();

		for (List<VCFReader> currentReaderList: genomeFileAssociation.values()) {
			for (VCFReader currentReader: currentReaderList) {
				if (!readerPath.contains(currentReader.getFile())) {
					readerList.add(currentReader);
					readerPath.add(currentReader.getFile());
				}
			}
		}

		return readerList;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Getters & Setters
	
	
	/**
	 * @return the multiGenomeSynchronizer
	 */
	public MGSynchronizer getMultiGenomeSynchronizer() {
		return multiGenomeSynchronizer;
	}
	
	
	/**
	 * @return the multiGenome
	 */
	public MGMultiGenome getMultiGenome() {
		return multiGenome;
	}


	/**
	 * @return the genomeFileAssociation
	 */
	public Map<String, List<VCFReader>> getGenomeFileAssociation() {
		return genomeFileAssociation;
	}


	/**
	 * @param genomeFileAssociation the genomeFileAssociation to set
	 */
	public void setGenomeFileAssociation(
			Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
	}
	
}
