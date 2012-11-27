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
public abstract class AbstractLayer<T extends Serializable> implements Layer<T> {

	private static final long 	serialVersionUID = 5294712647479393706L;// generated ID
	private static final int  	SAVED_FORMAT_VERSION_NUMBER = 1;		// saved format version
	private T 					data;									// data displayed in the layer
	private Track 		track;									// track in which the layer is displayed
	private boolean 			isHidden;								// true if the layer needs to be hidden

	
	/**
	 * Creates an instance of {@link AbstractLayer}.
	 * Default constructor
	 */
	public AbstractLayer() {
		setData(null);
		setTrack(null);
		setHidden(false);
	}


	/**
	 * Creates an instance of {@link AbstractLayer}
	 * @param data data displayed by the layer
	 * @param track track displaying the layer
	 */
	public AbstractLayer(T data, Track track) {
		setData(data);
		setTrack(track);
		setHidden(false);
	}


	@Override
	public Layer<?> deepCopy() throws IOException {
		// we clone the object
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bais);
		return (Layer<?>) ois;
	}


	@Override
	public T getData() {
		return data;
	}


	@Override
	public Track getTrack() {
		return track;
	}


	@Override
	public boolean isHidden() {
		return isHidden;
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
		setData((T)in.readObject());
		setTrack((Track)in.readObject());
		setHidden(in.readBoolean());
	}


	@Override
	public void setData(T data) {
		this.data = data;
	}


	@Override
	public void setHidden(boolean isHidden) {
		this.isHidden = isHidden;
	}


	@Override
	public void setTrack(Track track) {
		this.track = track;
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(getData());
		out.writeObject(getTrack());
		out.writeBoolean(isHidden());
	}
}
