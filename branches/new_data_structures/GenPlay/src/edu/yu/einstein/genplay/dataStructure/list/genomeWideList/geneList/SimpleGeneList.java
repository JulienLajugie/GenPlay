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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.AbstractListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Simple implementation of the {@link GeneList} interface.
 * @author Julien Lajugie
 */
public final class SimpleGeneList extends AbstractListView<ListView<Gene>> implements GeneList {

	/** Generated serial ID */
	private static final long serialVersionUID = 2409860942228135092L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** {@link GenomicDataArrayList} containing the Genes */
	private final ListView<Gene>[] data;

	/** URL to a gene database that can be used to search information about the genes of this list */
	private final String geneDBURL;

	/** Type of the scores of the genes and exons of this list (RPKM, max, sum) */
	private final GeneScoreType geneScoreType;

	/** Object that searches genes and handle funtion such as find next, find previous */
	private transient GeneSearcher geneSearcher;


	/**
	 * Creates an instance of {@link SimpleGeneList}
	 * @param data list of genes organized by chromosome
	 * @param geneScoreType type of the scores of the genes and exons (RPKM, max, sum)
	 * @param geneDBURL URL of the gene database
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public SimpleGeneList(List<ListView<Gene>> data, GeneScoreType geneScoreType, String geneDBURL) throws InterruptedException, ExecutionException {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.data = new GeneListView[projectChromosome.size()];
		for (int i = 0; i < projectChromosome.size(); i++){
			if (i < data.size()) {
				this.data[i] = data.get(i);
			}
		}
		this.geneDBURL = geneDBURL;
		this.geneScoreType = geneScoreType;
		geneSearcher = new GeneSearcher(this);
	}


	@Override
	public ListView<Gene> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int index = projectChromosome.getIndex(chromosome);
		return get(index);
	}


	@Override
	public Gene get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return get(chromosome).get(index);
	}


	@Override
	public ListView<Gene> get(int chromosomeIndex) {
		return data[chromosomeIndex];
	}


	@Override
	public Gene get(int chromosomeIndex, int elementIndex) {
		return get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public String getGeneDBURL() {
		return geneDBURL;
	}


	@Override
	public GeneScoreType getGeneScoreType() {
		return geneScoreType;
	}


	@Override
	public GeneSearcher getGeneSearcher() {
		return geneSearcher;
	}


	@Override
	public boolean isEmpty() {
		return data.length == 0;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		// read final fields
		in.defaultReadObject();
		// set the gene searcher
		geneSearcher = new GeneSearcher(this);
	}


	@Override
	public int size() {
		return data.length;
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return get(chromosome).size();
	}


	@Override
	public int size(int index) {
		return get(index).size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
		out.defaultWriteObject();
	}
}
