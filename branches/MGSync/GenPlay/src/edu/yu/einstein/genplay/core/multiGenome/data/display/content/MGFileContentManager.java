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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.variant.Variant;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGFileContentManager {

	private final Map<VCFFile, Map<Chromosome, MGChromosomeContent>> lists;


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
	 * Loads {@link Variant} for the current chromosome.
	 */
	public void updateCurrentVariants () {
		Chromosome currentChromosome = ProjectManager.getInstance().getProjectChromosome().getCurrentChromosome();
		List<VCFFile> fileList = new ArrayList<VCFFile>(lists.keySet());

		// Reset variants
		for (VCFFile file: fileList) {
			List<Chromosome> chromosomeList = new ArrayList<Chromosome>(lists.get(file).keySet());
			for (Chromosome chromosome: chromosomeList) {
				if (!chromosome.equals(currentChromosome)) {
					lists.get(file).get(currentChromosome).removeVariants();
				}
			}
		}

		// Load variants
		for (VCFFile file: fileList) {
			lists.get(file).get(currentChromosome).generateVariants();
		}
	}


	/**
	 * @return the list of {@link VCFFile}
	 */
	public List<VCFFile> getFileList () {
		return new ArrayList<VCFFile>(lists.keySet());
	}


	/**
	 * @param chromosome	a {@link Chromosome}
	 * @param chromosomeContent a {@link MGChromosomeContent}
	 * @return the {@link VCFFile} related to the given {@link MGChromosomeContent} and {@link Chromosome}, null if not found.
	 */
	public VCFFile getFile (Chromosome chromosome, MGChromosomeContent chromosomeContent) {
		for (VCFFile file: getFileList()) {
			if (lists.get(file).get(chromosome).equals(chromosomeContent)) {
				return file;
			}
		}
		return null;
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

}
