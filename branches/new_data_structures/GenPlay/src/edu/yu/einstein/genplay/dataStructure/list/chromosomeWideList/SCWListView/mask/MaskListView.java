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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewIterator;
import edu.yu.einstein.genplay.dataStructure.list.listView.subListView.SubListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * {@link ListView} of masks {@link ScoredChromosomeWindow}. Masks always have a score of 1.
 * {@link MaskListView} objects are immutable.
 * @author Julien Lajugie
 */
public final class MaskListView implements ListView<ScoredChromosomeWindow> {

	/** Generated serial ID */
	private static final long serialVersionUID = -2065237090366294538L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** List of the start positions of the masks */
	private final List<Integer> maskStarts;

	/** List of the stop positions of the masks */
	private final List<Integer> maskStops;


	/**
	 * Creates an instance of {@link MaskListView}
	 * @param maskStarts list of the start positions of the masks
	 * @param maskStops list of the stop positions of the masks
	 */
	MaskListView(List<Integer> maskStarts, List<Integer> maskStops) {
		super();
		this.maskStarts = maskStarts;
		this.maskStops = maskStops;
	}


	@Override
	public ScoredChromosomeWindow get(int elementIndex) {
		return new SimpleScoredChromosomeWindow(maskStarts.get(elementIndex), maskStops.get(elementIndex), 1);
	}


	@Override
	public boolean isEmpty() {
		return size() == 0;
	}


	@Override
	public Iterator<ScoredChromosomeWindow> iterator() {
		return new ListViewIterator<ScoredChromosomeWindow>(this);
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// read the version number of the object
		in.readInt();
		// read the final fields
		in.defaultReadObject();
	}


	@Override
	public int size() {
		return maskStarts.size();
	}


	@Override
	public ListView<ScoredChromosomeWindow> subList(int fromIndex, int toIndex) {
		return new SubListView<ScoredChromosomeWindow>(this, fromIndex, toIndex);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		// write the format version number of the object
		out.writeInt(CLASS_VERSION_NUMBER);
		// write the final fields
		out.defaultWriteObject();
	}
}