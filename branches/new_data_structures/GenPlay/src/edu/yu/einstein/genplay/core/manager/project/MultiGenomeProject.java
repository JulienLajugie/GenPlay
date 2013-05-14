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

import edu.yu.einstein.genplay.core.manager.ProjectFiles;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGFileContentManager;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSMultiGenome;
import edu.yu.einstein.genplay.core.multiGenome.data.synchronization.MGSOffset;
import edu.yu.einstein.genplay.core.multiGenome.operation.synchronization.MGSynchronizer;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;
import edu.yu.einstein.genplay.gui.action.multiGenome.synchronization.MGASynchronizing;


/**
 * The multi genome data structure can be seen in 3 main parts:
 * - {@link MGSMultiGenome} : Manages offsets between genomes and the meta genome. It is all about the synchronization of the positions.
 * - {@link MGFileContentManager} : Manages the variant information for their display.
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
 * THE WHOLE SYNCHRONIZATION PROCESS IS HANDLED BY {@link MGASynchronizing}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MultiGenomeProject implements Serializable {

	/** Generated serial version ID */
	private static final long serialVersionUID = -6096336417566795182L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;					// saved format version

	private		List<String>					genomeNames;					// The genome names list.
	private 	Map<String, List<VCFFile>> 		genomeFileAssociation;			// The map between genome names and their files.

	private 	MGSMultiGenome 					multiGenome;					// The genome synchronization data structure.
	private		MGSynchronizer					multiGenomeSynchronizer;		// The synchronizer for Indels and Structural Variant variations.
	private		MGFileContentManager			fileContentManager;				// The file content manager.


	/**
	 * Constructor of {@link MultiGenomeProject}
	 */
	public MultiGenomeProject () {}


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
	 * Retrieves all the VCF files
	 * @return the full list of VCF files
	 */
	public List<VCFFile> getAllVCFFiles () {
		List<VCFFile> readerList = new ArrayList<VCFFile>();

		for (List<VCFFile> currentReaderList: genomeFileAssociation.values()) {
			for (VCFFile currentReader: currentReaderList) {
				if (!readerList.contains(currentReader)) {
					readerList.add(currentReader);
				}
			}
		}

		return readerList;
	}


	/**
	 * @return the fileContentManager
	 */
	public MGFileContentManager getFileContentManager() {
		return fileContentManager;
	}


	/**
	 * Creates an array with all genome names association (including the reference genome).
	 * Used for display.
	 * @return	genome names association array
	 */
	public Object[] getFormattedGenomeArray () {
		return getFormattedGenomeArray(true, true);
	}


	/**
	 * Creates an array with all genome names association.
	 * Used for display.
	 * @param withReferenceGenome true to add the reference genome to the list
	 * @param withMetaGenome true to add the meta genome to the list
	 * @return	genome names association array
	 */
	public Object[] getFormattedGenomeArray (boolean withReferenceGenome, boolean withMetaGenome) {
		String[] names;
		List<String> preNames = new ArrayList<String>();
		int index = 0;

		if (withMetaGenome) {
			preNames.add(FormattedMultiGenomeName.META_GENOME_NAME);
		}

		if (withReferenceGenome) {
			preNames.add(ProjectManager.getInstance().getAssembly().getDisplayName());
		}

		names = new String[genomeNames.size() + preNames.size()];

		for (String preName: preNames) {
			names[index] = preName;
			index++;
		}
		for (String name: genomeNames) {
			names[index] = name;
			index++;
		}

		return names;
	}


	/**
	 * @return the genomeFileAssociation
	 */
	public Map<String, List<VCFFile>> getGenomeFileAssociation() {
		return genomeFileAssociation;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Genome names methods


	/**
	 * @return the genomeNames
	 */
	public List<String> getGenomeNames() {
		return genomeNames;
	}


	/**
	 * @return the multiGenome
	 */
	public MGSMultiGenome getMultiGenome() {
		return multiGenome;
	}


	/**
	 * @return the multiGenomeSynchronizer
	 */
	public MGSynchronizer getMultiGenomeSynchronizer() {
		return multiGenomeSynchronizer;
	}



	/**
	 * Get a vcf file object with a vcf file name.
	 * @param fileName 	the name of the vcf file
	 * @return			the reader
	 */
	public VCFFile getVCFFileFromName (String fileName) {
		List<VCFFile> list = getAllVCFFiles();
		for (VCFFile vcfFile: list) {
			if (vcfFile.getFile().getName().equals(fileName)) {
				return vcfFile;
			}
		}
		return null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Others


	/**
	 * Retrieves the VCF diles according to a genome name and a variant type
	 * @param genomeName	the full genome name
	 * @param type			the variant type
	 * @return				the list of VCF files for the given genome and variant type
	 */
	public List<VCFFile> getVCFFiles (String genomeName, VariantType type) {
		List<VCFFile> fileList = new ArrayList<VCFFile>();
		List<VCFFile> currentList = genomeFileAssociation.get(genomeName);

		for (VCFFile currentReader: currentList) {
			List<VariantType> typeList = currentReader.getVariantTypes(genomeName);
			if ((typeList != null) && typeList.contains(type)) {
				fileList.add(currentReader);
			}
		}

		return fileList;
	}


	/**
	 * This method notice the file manager of the dependant files.
	 */
	private void initializeFileDependancy () {
		List<VCFFile> vcfFiles = getAllVCFFiles();
		String[] paths = new String[vcfFiles.size()];
		for (int i = 0; i < paths.length; i++) {
			paths[i] = vcfFiles.get(i).getFile().getPath();
		}
		ProjectFiles.getInstance().setCurrentFiles(paths);
	}


	/**
	 * Initializes synchronizer attributes.
	 * @param genomeFileAssociation	the genome file association
	 */
	public void initializeSynchronization (Map<String, List<VCFFile>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
		genomeNames = new ArrayList<String>(this.genomeFileAssociation.keySet());
		Collections.sort(genomeNames);

		for (String genomeName: genomeNames) {
			List<VCFFile> vcfFiles = genomeFileAssociation.get(genomeName);
			for (VCFFile vcfFile: vcfFiles) {
				vcfFile.addGenomeName(genomeName);
			}
		}

		multiGenome = new MGSMultiGenome(genomeNames);
		multiGenomeSynchronizer = new MGSynchronizer(this);
		fileContentManager = new MGFileContentManager(getAllVCFFiles());
		initializeFileDependancy();
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
		genomeFileAssociation = (Map<String, List<VCFFile>>) in.readObject();
		multiGenome = (MGSMultiGenome) in.readObject();
		multiGenomeSynchronizer = (MGSynchronizer) in.readObject();
		fileContentManager = (MGFileContentManager) in.readObject();
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////// Getters & Setters


	/**
	 * @param genomeFileAssociation the genomeFileAssociation to set
	 */
	public void setGenomeFileAssociation(Map<String, List<VCFFile>> genomeFileAssociation) {
		this.genomeFileAssociation = genomeFileAssociation;
	}


	/**
	 * Set the current {@link MultiGenomeProject} using another instance of {@link MultiGenomeProject}
	 * Used for the unserialization.
	 * @param project the instance of {@link MultiGenomeProject} to use
	 */
	protected void setMultiGenomeProject (MultiGenomeProject project) {
		genomeNames = project.getGenomeNames();
		genomeFileAssociation = project.getGenomeFileAssociation();
		multiGenome = project.getMultiGenome();
		multiGenomeSynchronizer = project.getMultiGenomeSynchronizer();
		fileContentManager = project.getFileContentManager();
	}


	/**
	 * Show the information of the {@link MultiGenomeProject}
	 */
	public void show () {
		System.out.println("POSITION");
		multiGenome.show();
		System.out.println("CONTENT");
		fileContentManager.show();
	}


	/**
	 * Update the chromosome list using the new chromosome length
	 */
	public void updateChromosomeList () {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		List<Chromosome> currentChromosomeList = projectChromosomes.getChromosomeList();
		List<Integer> newChromosomeLengths = new ArrayList<Integer>();
		List<List<MGSOffset>> offsetList = multiGenome.getReferenceGenome().getAllele().getOffsetList();

		for (Chromosome current: currentChromosomeList) {
			int index = projectChromosomes.getIndex(current);
			int lastOffsetIndex = offsetList.get(index).size() - 1;
			int length = current.getLength();
			if (lastOffsetIndex > -1) {
				length += offsetList.get(index).get(lastOffsetIndex).getValue();
			}
			newChromosomeLengths.add(length);
		}

		projectChromosomes.updateChromosomeLengths(newChromosomeLengths);
	}


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
		out.writeObject(multiGenomeSynchronizer);
		out.writeObject(fileContentManager);
	}

}
