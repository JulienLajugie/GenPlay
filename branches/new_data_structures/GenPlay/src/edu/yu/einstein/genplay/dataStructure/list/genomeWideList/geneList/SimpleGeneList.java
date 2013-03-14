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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataArrayList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;


/**
 * Simple implementation of the {@link GeneList} interface.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class SimpleGeneList implements GeneList {

	/** Generated serial ID */
	private static final long serialVersionUID = -1567605708127718216L;

	/** Saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** {@link GenomicDataArrayList} containing the Genes */
	private GenomicDataArrayList<Gene> data;

	/** URL to a gene database that can be used to search information about the genes of this list */
	private String geneDBURL;

	/** Type of the scores of the genes and exons of this list (RPKM, max, sum) */
	private GeneScoreType geneScoreType;

	/** Object that searches genes and handle funtion such as find next, find previous */
	private GeneSearcher geneSearcher;


	/**
	 * Creates an instance of {@link SimpleGeneList}
	 * @param data list of genes organized by chromosome
	 * @param geneScoreType type of the scores of the genes and exons (RPKM, max, sum)
	 * @param geneDBURL URL of the gene database
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public SimpleGeneList(List<List<Gene>> data, GeneScoreType geneScoreType, String geneDBURL) throws InterruptedException, ExecutionException {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			if (i < data.size()) {
				data.add(data.get(i));
			} else {
				// add an empty list
				data.add(new ArrayList<Gene>());
			}
		}
		this.geneScoreType = geneScoreType;
		this.geneDBURL = geneDBURL;
		geneSearcher = new GeneSearcher(this);
		sort();
	}


	@Override
	public Gene get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		return data.get(chromosome, index);
	}


	@Override
	public Gene get(int chromosomeIndex, int elementIndex) {
		return data.get(chromosomeIndex, elementIndex);
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
	public ListView<Gene> getView(Chromosome chromosome) throws InvalidChromosomeException {
		return data.getView(chromosome);
	}


	@Override
	public ListView<Gene> getView(int chromosomeIndex) {
		return data.getView(chromosomeIndex);
	}


	@Override
	public Iterator<List<Gene>> iterator() {
		return data.iterator();
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		data = (GenomicDataArrayList<Gene>) in.readObject();
		geneDBURL = (String) in.readObject();
		geneScoreType = (GeneScoreType) in.readObject();
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		return data.size(chromosome);
	}


	@Override
	public int size(int index) {
		return data.size(index);
	}


	/**
	 * Sorts the elements of the {@link GeneList} by position
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void sort() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		for (final List<Gene> currentList: this) {
			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						Collections.sort(currentList);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
		out.writeObject(geneDBURL);
		out.writeObject(geneScoreType);
		geneSearcher = new GeneSearcher(this);
	}
}
