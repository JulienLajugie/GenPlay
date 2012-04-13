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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFReader;
import edu.yu.einstein.genplay.core.multiGenome.display.MGMultiGenomeForDisplay;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGGenome;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGOffset;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSNPSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.gui.action.project.multiGenome.PAMultiGenomeSynchronizing;
import edu.yu.einstein.genplay.gui.action.project.multiGenome.PAMultiGenomeSNP;


/**
 * The multi genome data structure can be seen in 3 main parts:
 * - {@link MGMultiGenome} : Manages offsets between genomes and the meta genome. It is all about the synchronization of the positions.
 * - {@link MGMultiGenomeForDisplay} : Manages the variant information for their display.
 * - {@link MGSynchronizer} & {@link MGSNPSynchronizer}: the synchronizers, they perform the synchronization operations.
 * 
 * This class also contains the map between the genome names and their VCF file readers.
 * Information about a genome can be stored in one or several VCF files, no matter the type (Indels, SV, SNPs).
 * Genomes separated in different files MUST HAVE THE SAME NAME IN EVERY FILE!
 * 
 * The genomes names list is required quiet often. That list is made from the map between the genome names and their reader.
 * Once created, the list is stored in order to be use later without creating it again and again.
 * 
 * ALL GENOME NAMES ARE STORED IN THIS DATA STRUCTURE AS "FULL GENOME NAME" (with group/genome/raw name).
 * See {@link FormattedMultiGenomeName} for more details.
 * 
 * THE WHOLE SYNCHRONIZATION PROCESS IS HANDLED BY {@link PAMultiGenomeSynchronizing} AND {@link PAMultiGenomeSNP}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeProject implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -6096336417566795182L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;					// saved format version
	
	private		List<String>					genomeNames;					// The genome names list.
	private 	Map<String, List<VCFReader>> 	genomeFileAssociation;			// The map between genome names and their readers.

	private 	MGMultiGenome 					multiGenome;					// The genome synchronization data structure.
	private 	MGMultiGenomeForDisplay 		multiGenomeForDisplay;			// The genome display data structure.

	private		MGSynchronizer					multiGenomeSynchronizer;		// The synchronizer for Indels and Structural Variant variations.
	private		MGSNPSynchronizer				multiGenomeSynchronizerForSNP; 	// The synchronizer for SNPs variations.


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(genomeNames);
		out.writeObject(genomeFileAssociation);
		out.writeObject(multiGenome);
		out.writeObject(multiGenomeForDisplay);
		out.writeObject(multiGenomeSynchronizer);
		out.writeObject(multiGenomeSynchronizerForSNP);
	}
	

	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		genomeNames = (List<String>) in.readObject();
		genomeFileAssociation = (Map<String, List<VCFReader>>) in.readObject();
		multiGenome = (MGMultiGenome) in.readObject();
		multiGenomeForDisplay = (MGMultiGenomeForDisplay) in.readObject();
		multiGenomeSynchronizer = (MGSynchronizer) in.readObject();
		multiGenomeSynchronizerForSNP = (MGSNPSynchronizer) in.readObject();
	}


	/**
	 * Set the current {@link MultiGenomeProject} using another instance of {@link MultiGenomeProject}
	 * Used for the unserialization.
	 * @param project the instance of {@link MultiGenomeProject} to use
	 */
	protected void setMultiGenomeProject (MultiGenomeProject project) {
			this.genomeNames = project.getGenomeNames();
			this.genomeFileAssociation = project.getGenomeFileAssociation();
			this.multiGenome = project.getMultiGenome();
			this.multiGenomeForDisplay = project.getMultiGenomeForDisplay();
			this.multiGenomeSynchronizer = project.getMultiGenomeSynchronizer();
			this.multiGenomeSynchronizerForSNP = project.getMultiGenomeSynchronizerForSNP();
	}


	/**
	 * Constructor of {@link MultiGenomeProject}
	 */
	public MultiGenomeProject () {}


	/**
	 * Initializes synchronizer attributes.
	 * @param genomeFileAssociation	the genome file association
	 */
	public void initializeSynchronization (Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
		this.genomeNames = new ArrayList<String>(this.genomeFileAssociation.keySet());
		Collections.sort(genomeNames);

		for (String genomeName: genomeNames) {
			List<VCFReader> readers = genomeFileAssociation.get(genomeName);
			for (VCFReader reader: readers) {
				reader.addGenomeName(genomeName);
			}
		}

		this.multiGenome = new MGMultiGenome(genomeNames);
		this.multiGenomeSynchronizer = new MGSynchronizer(this);
		this.multiGenomeSynchronizerForSNP = new MGSNPSynchronizer();
		initializesDisplayInformation();
		initializeFileDependancy();
	}
	
	
	/**
	 * This method notice the file manager of the dependant files.
	 */
	private void initializeFileDependancy () {
		List<VCFReader> readers = getAllReaders();
		String[] paths = new String[readers.size()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = readers.get(i).getFile().getPath();
		}
		ProjectFiles.getInstance().setCurrentFiles(paths);
	}


	/**
	 * @param genomeFileAssociation the genomeFileAssociation to set
	 */
	public void setGenomeFileAssociation(Map<String, List<VCFReader>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
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
	 * Update the chromosome list using the new chromosome length
	 */
	public void updateChromosomeList () {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		List<Chromosome> currentChromosomeList = projectChromosome.getChromosomeList();
		List<Chromosome> newChromosomeList = new ArrayList<Chromosome>();
		ChromosomeListOfLists<MGOffset> offsetList = multiGenome.getReferenceGenome().getAllele().getOffsetList();

		for (Chromosome current: currentChromosomeList) {
			String name = current.getName();
			int lastOffsetIndex = offsetList.get(current).size() - 1;
			int length = current.getLength();
			if (lastOffsetIndex > -1) {
				length += offsetList.get(current, lastOffsetIndex).getValue();
			}
			newChromosomeList.add(new Chromosome(name, length));
		}

		projectChromosome.updateChromosomeLength(newChromosomeList);
	}


	/**
	 * Initializes the genome information for display purpose
	 */
	private void initializesDisplayInformation () {
		List<MGGenome> genomeList = multiGenome.getGenomeInformation();
		multiGenomeForDisplay = new MGMultiGenomeForDisplay(genomeList);
	}


	/**
	 * Retrieves all the VCF readers
	 * @return the full list of VCF reader
	 */
	public List<VCFReader> getAllReaders () {
		List<VCFReader> readerList = new ArrayList<VCFReader>();

		for (List<VCFReader> currentReaderList: genomeFileAssociation.values()) {
			for (VCFReader currentReader: currentReaderList) {
				if (!readerList.contains(currentReader)) {
					readerList.add(currentReader);
				}
			}
		}

		return readerList;
	}


	/**
	 * Retrieves the VCF reader according to a genome name and a variant type
	 * @param genomeName	the full genome name
	 * @param type			the variant type
	 * @return				the list of VCF reader for the given genome and variant type
	 */
	public List<VCFReader> getReaders (String genomeName, VariantType type) {
		List<VCFReader> readerList = new ArrayList<VCFReader>();
		List<VCFReader> currentList = genomeFileAssociation.get(genomeName);

		for (VCFReader currentReader: currentList) {
			List<VariantType> typeList = currentReader.getVariantTypes(genomeName);
			if (typeList != null && typeList.contains(type)) {
				readerList.add(currentReader);
			}
		}

		return readerList;
	}


	/**
	 * Get a vcf reader object with a vcf file name.
	 * @param fileName 	the name of the vcf file
	 * @return			the reader
	 */
	public VCFReader getReadersFromName (String fileName) {
		List<VCFReader> list = getAllReaders();
		for (VCFReader reader: list) {
			if (reader.getFile().getName().equals(fileName)) {
				return reader;
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Getters & Setters


	/**
	 * @return the multiGenomeSynchronizer
	 */
	public MGSynchronizer getMultiGenomeSynchronizer() {
		return multiGenomeSynchronizer;
	}


	/**
	 * @return the multiGenomeSynchronizerForSNP
	 */
	public MGSNPSynchronizer getMultiGenomeSynchronizerForSNP() {
		return multiGenomeSynchronizerForSNP;
	}


	/**
	 * @return the multiGenome
	 */
	public MGMultiGenome getMultiGenome() {
		return multiGenome;
	}


	/**
	 * @return the multiGenomeForDisplay
	 */
	public MGMultiGenomeForDisplay getMultiGenomeForDisplay() {
		return multiGenomeForDisplay;
	}


	/**
	 * @return the genomeFileAssociation
	 */
	public Map<String, List<VCFReader>> getGenomeFileAssociation() {
		return genomeFileAssociation;
	}


	/**
	 * Show the information of the {@link MultiGenomeProject}
	 */
	public void show () {
		System.out.println("POSITION");
		multiGenome.show();
		System.out.println("DISPLAY");
		multiGenomeForDisplay.show();
	}

}
