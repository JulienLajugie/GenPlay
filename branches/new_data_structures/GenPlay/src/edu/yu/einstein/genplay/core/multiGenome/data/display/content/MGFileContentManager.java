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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;

/**
 * A {@link MGFileContentManager} represents the content of all {@link VCFFile} organized byt {@link Chromosome} and {@link MGChromosomeContent}.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFileContentManager implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = 4837189232012683529L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private Map<VCFFile, Map<Chromosome, MGChromosomeContent>> lists;		// Maps to store all data from all files.


	/**
	 * Constructor of {@link MGFileContentManager}
	 * @param files list of {@link VCFFile};
	 */
	public MGFileContentManager (List<VCFFile> files) {
		lists = new HashMap<VCFFile, Map<Chromosome,MGChromosomeContent>>();
		for (VCFFile file: files) {
			lists.put(file, new HashMap<Chromosome, MGChromosomeContent>());
		}
	}


	/**
	 * Compact all lists resizing arrays for better memory usage
	 */
	public void compact () {
		List<VCFFile> fileList = new ArrayList<VCFFile>(lists.keySet());
		for (VCFFile file: fileList) {
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>(lists.get(file).keySet());
			for (Chromosome chromosome: chromosomeList) {
				lists.get(file).get(chromosome).compact();
			}
		}
	}


	/**
	 * @param file			a vcf file
	 * @param chromosome	a chromosome
	 * @return the {@link MGChromosomeContent} for the given file and chromosome
	 */
	public MGChromosomeContent getContent (VCFFile file, Chromosome chromosome) {
		if (lists.get(file) == null) {
			lists.put(file, new HashMap<Chromosome, MGChromosomeContent>());
		}
		if (lists.get(file).get(chromosome) == null) {
			lists.get(file).put(chromosome, new MGChromosomeContent(chromosome, file.getHeader().getGenomeNames()));
		}
		return lists.get(file).get(chromosome);
	}


	/**
	 * @param chromosome	a {@link Chromosome}
	 * @param chromosomeContent a {@link MGChromosomeContent}
	 * @return the {@link VCFFile} related to the given {@link MGChromosomeContent} and {@link Chromosome}, null if not found.
	 */
	public VCFFile getFile (Chromosome chromosome, MGChromosomeContent chromosomeContent) {
		for (VCFFile file: getFileList()) {
			MGChromosomeContent currentChromosomeContent = lists.get(file).get(chromosome);
			if ((currentChromosomeContent != null) && currentChromosomeContent.equals(chromosomeContent)) {
				return file;
			}
		}
		return null;
	}


	/**
	 * @return the list of {@link VCFFile}
	 */
	public List<VCFFile> getFileList () {
		return new ArrayList<VCFFile>(lists.keySet());
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
		lists = (Map<VCFFile, Map<Chromosome, MGChromosomeContent>>) in.readObject();
	}


	/**
	 * Show files content
	 */
	public void show () {
		List<VCFFile> fileList = new ArrayList<VCFFile>(lists.keySet());
		for (VCFFile file: fileList) {
			System.out.println("FILE: " + file.getFile().getName());
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>(lists.get(file).keySet());
			for (Chromosome chromosome: chromosomeList) {
				System.out.println("CHROMOSOME: " + chromosome.getName());
				lists.get(file).get(chromosome).show();
			}
		}
	}


	/**
	 * Loads {@link Variant} for the current chromosome.
	 */
	public void updateCurrentVariants () {
		Chromosome currentChromosome = ProjectManager.getInstance().getProjectWindow().getGenomeWindow().getChromosome();
		List<VCFFile> fileList = new ArrayList<VCFFile>(lists.keySet());

		// Reset variants
		for (VCFFile file: fileList) {
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>(lists.get(file).keySet());
			for (Chromosome chromosome: chromosomeList) {
				if (!chromosome.equals(currentChromosome)) {
					MGChromosomeContent content = lists.get(file).get(currentChromosome);
					if (content != null) {
						content.removeVariants();
					}
				}
			}
		}

		// Load variants
		for (VCFFile file: fileList) {
			MGChromosomeContent content = lists.get(file).get(currentChromosome);
			if (content != null) {
				content.generateVariants();
			}
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(lists);
	}

}
