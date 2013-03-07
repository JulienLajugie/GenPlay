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
package edu.yu.einstein.genplay.dataStructure.list.geneList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.GenomicDataArrayList;


/**
 * A list of {@link Gene}.
 * Implementation of the {@link GeneList} interface using an {@link ArrayList} based data structure
 * @author Julien Lajugie
 * @version 0.1
 */
public final class GeneArrayList extends GenomicDataArrayList<Gene> implements GeneList {

	/** Generated serial ID */
	private static final long serialVersionUID = -1567605708127718216L;

	/** Saved format version */
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0;

	/** URL to a gene database that can be used to search information about the genes of this list */
	private String geneDBURL;

	/** Type of the scores of the genes and exons of this list (RPKM, max, sum) */
	private GeneScoreType geneScoreType;

	/** Object that searches genes and handle funtion such as find next, find previous */
	private final GeneSearcher	geneSearcher;


	/**
	 * Creates an instance of {@link GeneArrayList}
	 */
	protected GeneArrayList() {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		for (int i = 0; i < projectChromosome.size(); i++){
			add(new ArrayList<Gene>());
		}
		geneSearcher = new GeneSearcher(this);
	}


	@Override
	public GeneArrayList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((GeneArrayList)ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		geneDBURL = (String) in.readObject();
		geneScoreType = (GeneScoreType) in.readObject();
	}


	@Override
	public void setGeneDBURL(String geneDBURL) {
		this.geneDBURL = geneDBURL;
	}


	@Override
	public void setGeneScoreType(GeneScoreType geneScoreType) {
		this.geneScoreType = geneScoreType;
	}


	@Override
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
		out.writeObject(geneScoreType);
	}
}
