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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatFamily;

import java.io.Serializable;
import java.util.Iterator;

import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Representation of a family of repeats. A repeat family has a name and
 * a list of repeats. Repeats are {@link ChromosomeWindow} objects having a
 * start and a stop position.
 * {@link RepeatFamily} objects are immutable.
 * @author Julien Lajugie
 */
public class RepeatFamily implements Serializable, ListView<ChromosomeWindow> {

	/** generated ID */
	private static final long serialVersionUID = -7691967168795920365L;

	/** Name of the family of repeat */
	private final String name;

	/** {@link ListView} of repeats. Repeats are {@link ChromosomeWindow} objects
	 * containing the start and the stop position of the repeat */
	private final ListView<ChromosomeWindow> repeatList;


	/**
	 * Creates an instance of {@link RepeatFamily}
	 * @param name name of the family
	 * @param repeatList {@link ListView} of {@link ChromosomeWindow} with the start and stop position of the repeats
	 */
	public RepeatFamily(String name, ListView<ChromosomeWindow> repeatList) {
		this.name = name;
		this.repeatList = repeatList;
	}


	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	/**
	 * Prints the name and the repeats of the {@link RepeatFamily}
	 */
	public void print() {
		String info = "";
		info += "Family name: " + name + "\n";
		info += "Number of repeats: " + repeatList.size() + "\n";
		for (ChromosomeWindow repeat: repeatList) {
			info += "(" + repeat.getStart() + ", " + repeat.getStop() + ") ";
		}
		System.out.println(info);
	}


	@Override
	public Iterator<ChromosomeWindow> iterator() {
		return repeatList.iterator();
	}


	@Override
	public int size() {
		return repeatList.size();
	}


	@Override
	public ChromosomeWindow get(int repeatIndex) {
		return repeatList.get(repeatIndex);
	}
}
