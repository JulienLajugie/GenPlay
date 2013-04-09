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
package edu.yu.einstein.genplay.dataStructure.list.listView;

import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;


/**
 * Builds instances of {@link ListView}. The goal is to assure that {@link ListView}
 * objects are immutable.<br>
 * This can be done as follow: <br>
 * 1) Set the visibility of constructors of class implementing the {@link ListView}
 * interface to package.<br>
 * 2) Create a builder class in the same package implementing this interface.<br>
 * This interface extends the {@link Cloneable} interface so object implementing this
 * interface can be created following the method specified by the Prototype design pattern.
 * @param <T> type of the elements of the {@link ListView}
 * @author Julien Lajugie
 */
public interface ListViewBuilder<T> extends Cloneable {


	/**
	 * Adds an element to the {@link ListView} that will be built.
	 * To assure that {@link ListView} objects are immutable, this method will throw an exception
	 * if called after the getListView() has been called.
	 * @param element element to add
	 * @throws ObjectAlreadyBuiltException if this method is called after the build method was called
	 */
	public void addElementToBuild(T element) throws ObjectAlreadyBuiltException;


	/**
	 * @return a new instance of {@link ListViewBuilder} that is a clone of the current prototype instance.
	 * Potential elements to build added to prototype instance won't be present in the clone.
	 * @throws CloneNotSupportedException
	 */
	public ListViewBuilder<T> clone() throws CloneNotSupportedException;


	/**
	 * Builds a {@link ListView} with the elements previously added and returns it.
	 * No more elements can be added to the {@link ListView} once this method is called.
	 * @return a {@link ListView}
	 */
	public ListView<T> getListView();
}
