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
package edu.yu.einstein.genplay.gui.track;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.yu.einstein.genplay.gui.track.layer.Layer;

/**
 * Model that manages a list of layers
 * @author Julien Lajugie
 */
public class TrackModel implements Serializable, Collection<Layer<?>> {

	private static final long serialVersionUID = -3615098708228915823L; // generated serial ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private List<ListDataListener>		dataListeners;		// list of listeners that is notified each time a change to the data model occurs
	private List<Layer<?>>				layers; 			// array of layers displayed in the track

	/**
	 * Creates an instance of {@link TrackModel}
	 */
	public TrackModel() {
		this.dataListeners = new ArrayList<ListDataListener>();
		this.layers = new ArrayList<Layer<?>>();
	}


	@Override
	public boolean add(Layer<?> layer) {
		int indexAdded = this.layers.size();
		this.layers.add(layer);
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, indexAdded, indexAdded);
		notifyListeners(event);
		return true;
	}


	@Override
	public boolean addAll(Collection<? extends Layer<?>> layers) {
		int indexFirstAdded = this.layers.size();
		int indexLastAdded = indexFirstAdded + layers.size();
		boolean listChanged = this.layers.addAll(layers);
		if (listChanged) {
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, indexFirstAdded, indexLastAdded);
			notifyListeners(event); 
		}
		return listChanged;
	}


	/**
	 * Adds a listener to the list that's notified each time a change to the data model occurs.
	 * @param dataListener
	 */
	public void addListDataListener(ListDataListener dataListener) {
		if (!this.dataListeners.contains(dataListener)) {
			this.dataListeners.add(dataListener);
		}
	}


	@Override
	public void clear() {
		int lastIndex = layers.size() - 1;
		this.layers.clear();
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0, lastIndex);
		notifyListeners(event);
	}


	@Override
	public boolean contains(Object o) {
		return layers.contains(o);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return layers.containsAll(c);
	}


	/**
	 * @return an array containing the elements managed by this {@link TrackModel}
	 */
	public Layer<?>[] getLayers() {
		Layer<?>[] layerArray = new Layer<?>[this.layers.size()];
		return this.layers.toArray(layerArray);
	}


	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index i such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element
	 * @throws ClassCastException - if the type of the specified element is incompatible with this list (optional)
	 * @throws NullPointerException - if the specified element is null and this list does not permit null elements (optional)
	 */
	public int indexOf(Object o) {
		return layers.indexOf(o);
	}


	@Override
	public boolean isEmpty() {
		return layers.isEmpty();
	}


	@Override
	public Iterator<Layer<?>> iterator() {
		return layers.iterator();
	}


	/**
	 * Notifies all the {@link ListDataListener} that the data changed
	 * @param event
	 */
	private void notifyListeners(ListDataEvent event) {
		switch (event.getType()) {
		case ListDataEvent.CONTENTS_CHANGED:
			for (ListDataListener listener: dataListeners) {
				listener.contentsChanged(event);
			}
		case ListDataEvent.INTERVAL_ADDED:
			for (ListDataListener listener: dataListeners) {
				listener.intervalAdded(event);
			}
		case ListDataEvent.INTERVAL_REMOVED:
			for (ListDataListener listener: dataListeners) {
				listener.intervalRemoved(event);
			}
		}
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
		dataListeners = (List<ListDataListener>)in.readObject();
		layers = (List<Layer<?>>)in.readObject();
	}


	@Override
	public boolean remove(Object o) {
		int indexRemoved = layers.indexOf(o);
		if (indexRemoved != -1) {
			layers.remove(o);
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, indexRemoved, indexRemoved);
			notifyListeners(event);
			return true;
		} else {
			return false;
		}
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		int sizeBeforeChanges = layers.size();
		boolean listChanged = layers.removeAll(c);
		if (listChanged) {
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sizeBeforeChanges - 1);
			notifyListeners(event);
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Removes a listener from the list that's notified each time a change to the data model occurs.
	 * @param dataListener
	 */
	public void removeListDataListener(ListDataListener dataListener)  {
		dataListeners.remove(dataListener);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		int sizeBeforeChanges = layers.size();
		boolean listChanged = layers.retainAll(c);
		if (listChanged) {
			ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, sizeBeforeChanges - 1);
			notifyListeners(event);
			return true;
		} else {
			return false;
		}
	}


	@Override
	public int size() {
		return layers.size();
	}


	@Override
	public Object[] toArray() {
		return layers.toArray();
	}


	@Override
	public <T> T[] toArray(T[] array) {
		return layers.toArray(array);
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(this.dataListeners);
		out.writeObject(this.layers);
	}
}
