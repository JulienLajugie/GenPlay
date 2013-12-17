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
package edu.yu.einstein.genplay.gui.track.layer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.gui.track.Track;


/**
 * An abstract class that exists as convenience for creating class implementing the {@link Layer} interface.
 * @author Julien Lajugie
 * @param <T> type of the data displayed in the layer
 */
public abstract class AbstractLayer<T extends Serializable> implements Cloneable, Layer<T> {

	private static final long 	serialVersionUID = 5294712647479393706L;// generated ID
	private static final int  	SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version
	private transient Track 	track;									// track in which the layer is displayed
	private T 					data;									// data displayed in the layer
	private String				name;									// name of the layer
	private boolean 			isVisible;								// true if the layer is visible, false if hidden


	/**
	 * Creates an instance of {@link AbstractLayer}.
	 * Default constructor
	 */
	public AbstractLayer() {
		this(null, null, null, true);
	}


	/**
	 * Creates an instance of {@link AbstractLayer} with the same properties as the specified {@link AbstractLayer}
	 * @param abstractLayer
	 */
	protected AbstractLayer(AbstractLayer<T> abstractLayer) {
		this(abstractLayer.track, abstractLayer.data, abstractLayer.name, abstractLayer.isVisible);
	}


	/**
	 * Creates an instance of {@link AbstractLayer}
	 * @param track track displaying the layer
	 * @param data data displayed by the layer
	 */
	public AbstractLayer(Track track, T data) {
		this(track, data, null, true);
	}


	/**
	 * Creates an instance of {@link AbstractLayer}
	 * @param track track displaying the layer
	 * @param data data displayed by the layer
	 * @param name name of the layer
	 */
	public AbstractLayer(Track track, T data, String name) {
		this(track, data, name, true);
	}


	/**
	 * Creates an instance of {@link AbstractLayer}
	 * @param track track displaying the layer
	 * @param data data displayed by the layer
	 * @param name name of the layer
	 * @param isVisible true if the layer is visible
	 */
	public AbstractLayer(Track track, T data, String name, boolean isVisible) {
		this.data = data;
		this.track = track;
		this.name = name;
		this.isVisible = isVisible;
	}


	@Override
	public abstract AbstractLayer<T> clone();


	@Override
	public Layer<?> deepCopy() throws IOException, ClassNotFoundException {
		// we clone the object
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Layer<?>) ois.readObject();
	}


	@Override
	public T getData() {
		return data;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public Track getTrack() {
		return track;
	}


	@Override
	public boolean isVisible() {
		return isVisible;
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
		data = (T)in.readObject();
		name = (String) in.readObject();
		isVisible = in.readBoolean();
	}


	@Override
	public <U extends T> void setData(U data) {
		this.data = data;
		if (getTrack() != null) {
			getTrack().repaint();
		}
	}


	@Override
	public void setName(String name) {
		this.name = name;
	}


	@Override
	public void setTrack(Track track) {
		this.track = track;
	}


	@Override
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}


	@Override
	public String toString() {
		if (getName() != null) {
			return getName();
		} else {
			return super.toString();
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(data);
		out.writeObject(name);
		out.writeBoolean(isVisible);
	}
}
