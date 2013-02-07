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
package edu.yu.einstein.genplay.core.list.geneList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.gene.Gene;
import edu.yu.einstein.genplay.core.list.GenomicDataArrayList;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;


/**
 * A list of {@link Gene}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneList extends GenomicDataArrayList<Gene> implements Serializable, GenomicDataList<Gene> {

	/** Generated serial ID */
	private static final long serialVersionUID = -1567605708127718216L;

	/** Saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** URL to a gene database that can be used to search information about the genes of this list */
	private String geneDBURL;

	/** Object that searches genes and handle funtion such as find next, find previous */
	private final GeneSearcher	geneSearcher;


	/**
	 * Creates an instance of {@link GeneList}
	 */
	protected GeneList() {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			add(new ArrayList<Gene>());
		}
		geneSearcher = new GeneSearcher(this);
	}


	/**
	 * Performs a deep clone of the current GeneList
	 * @return a new GeneList that is a deep copy of this one
	 */
	public GeneList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((GeneList)ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * @return the URL of the gene database that contains information about the genes of this list
	 */
	public String getGeneDBURL() {
		return geneDBURL;
	}


	/**
	 * @return the {@link GeneSearcher} object that handles gene searches
	 */
	public GeneSearcher getGeneSearcher() {
		return geneSearcher;
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		geneDBURL = (String) in.readObject();
	}


	/**
	 * Sets the URL of the gene database that contains information about the genes of this list.
	 * @param geneDBURL URL of the database containing information about the genes of this list
	 */
	public void setGeneDBURL(String geneDBURL) {
		this.geneDBURL = geneDBURL;
	}


	/**
	 * For each chromosome, sorts the genes by position.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	public void sort() throws InterruptedException, ExecutionException {
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
		out.writeObject(geneDBURL);
	}
}
