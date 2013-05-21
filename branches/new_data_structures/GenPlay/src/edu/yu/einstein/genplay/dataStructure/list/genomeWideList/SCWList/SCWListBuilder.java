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
package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.SCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListView;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.bin.BinListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.dense.DenseSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.generic.GenericSCWListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SimpleSCWList.SimpleSCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Builder that creates {@link SCWList} objects.
 * @author Julien Lajugie
 */
public class SCWListBuilder {

	/** Builder used to build the data of the SCWList */
	protected final ListOfListViewBuilder<ScoredChromosomeWindow> builders;


	/**
	 * Creates an instance of {@link SCWListBuilder}
	 * @param list list used as a model for the result list.
	 * The result list will be the same type of the specified list and have similar attributes
	 * (eg: same bin size in the case of BinList)
	 * @throws CloneNotSupportedException
	 */
	public SCWListBuilder(SCWList list) throws CloneNotSupportedException {
		super();
		ListViewBuilder<ScoredChromosomeWindow> prototypeLVBuilder = createSCWLVBuilderPrototype(list);
		builders = new ListOfListViewBuilder<ScoredChromosomeWindow>(prototypeLVBuilder);
	}


	/**
	 * Creates an instance of {@link SCWListBuilder}
	 * @param builderPrototype prototype of the builder that will create the {@link ListView} objects.
	 * @throws CloneNotSupportedException
	 */
	public SCWListBuilder(SCWListViewBuilder builderPrototype) throws CloneNotSupportedException {
		super();
		builders = new ListOfListViewBuilder<ScoredChromosomeWindow>(builderPrototype);
	}


	/**
	 * Adds an element to the {@link SCWList} to be built.
	 * @param chromosome chromosome of the element to add
	 * @param start start position of the window to add
	 * @param stop stop position of the window to add
	 * @param score score of the window to add
	 * @throws InvalidChromosomeException if the chromosome is not valid
	 * @throws ObjectAlreadyBuiltException if the SCWList has already been created
	 */
	public void addElementToBuild(Chromosome chromosome, int start, int stop, float score) throws InvalidChromosomeException, ObjectAlreadyBuiltException {
		SCWListViewBuilder scwLVB = (SCWListViewBuilder) builders.getBuilder(chromosome);
		scwLVB.addElementToBuild(start, stop, score);
	}


	/**
	 * Adds an element to the {@link SCWList} to be built.
	 * @param chromosome chromosome of the element to add
	 * @param element element to add to the SCWList to be built
	 * @throws InvalidChromosomeException if the chromosome is not valid
	 * @throws ObjectAlreadyBuiltException if the SCWList has already been created
	 */
	public void addElementToBuild(Chromosome chromosome, ScoredChromosomeWindow element) throws InvalidChromosomeException, ObjectAlreadyBuiltException {
		builders.addElementToBuild(chromosome, element);
	}


	/**
	 * Creates an instance of {@link SCWList} that can either be an instance of {@link BinList} or of {@link SimpleSCWList} depending
	 * on the specified {@link ListOfListViewBuilder}
	 * @return An instance of {@link ScoredChromosomeWindow}
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws CloneNotSupportedException
	 * @throws Exception
	 */
	private final SCWList createSCWList(List<ListView<ScoredChromosomeWindow>> data) throws InvalidParameterException, InterruptedException, ExecutionException, CloneNotSupportedException  {
		if ((data == null) || data.isEmpty()) {
			return null;
		}
		if (data.get(0) instanceof BinListView) {
			return new BinList(data);
		} else {
			return new SimpleSCWList(data);
		}
	}

	/**
	 * @param list an object instance of {@link SCWList}.
	 * @return A {@link ListViewBuilder} that creates ListView objects the same kind of the ones from the specified list
	 */
	private final ListViewBuilder<ScoredChromosomeWindow> createSCWLVBuilderPrototype(SCWList list) {
		switch (list.getSCWListType()) {
		case BIN:
			BinList binList = (BinList) list;
			return new BinListViewBuilder(binList.getBinSize());
		case DENSE:
			return new DenseSCWListViewBuilder();
		case GENERIC:
			return new GenericSCWListViewBuilder();
		case MASK:
			return new MaskListViewBuilder();
		default:
			throw new InvalidParameterException("The specified list is not a valid SCWList");
		}
	}


	/**
	 * @return the list of {@link ListView} objects created by this builder
	 * Note: the {@link #getSCWList()} method can be used to retrieve a {@link SCWList}
	 * object instead.
	 */
	public List<ListView<ScoredChromosomeWindow>> getListOfListViews() {
		return builders.getGenomicList();
	}


	/**
	 * @return A SCWList containing the items added using the {@link #addElementToBuild(Chromosome, ScoredChromosomeWindow)}.<br>
	 * The type of the SCWList will be the same as the one from the list specified in the {@link #SCWListBuilder(SCWList)} constructor.
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws InvalidParameterException
	 * @throws CloneNotSupportedException
	 */
	public SCWList getSCWList() throws InvalidParameterException, InterruptedException, ExecutionException, CloneNotSupportedException  {
		SCWList list = createSCWList(builders.getGenomicList());
		return list;
	}
}
