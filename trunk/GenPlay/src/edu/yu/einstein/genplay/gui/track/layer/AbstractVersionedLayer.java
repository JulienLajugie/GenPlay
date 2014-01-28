/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.gui.track.layer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import edu.yu.einstein.genplay.core.manager.URRManager;
import edu.yu.einstein.genplay.core.manager.application.ConfigurationManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.util.History;

/**
 * An abstract class that exists as convenience for creating class implementing the {@link VersionedLayer} interface.
 * @param <T> type of the data displayed in the layer
 * @author Julien Lajugie
 */
public abstract class AbstractVersionedLayer<T extends Serializable> extends AbstractLayer<T> implements Cloneable, Serializable, Layer<T>, VersionedLayer<T> {

	private static final long serialVersionUID = -132567654281687511L; // generated serial version ID
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version

	private History 		history;		// object managing the history of the layer
	private URRManager<T> 	urrManager;		// object managing the undo / redo / reset actions of the layer


	/**
	 * Creates an instance of {@link AbstractVersionedLayer}.
	 * Default constructor
	 */
	public AbstractVersionedLayer() {
		this(null, null, null);
	}


	/**
	 * Creates an instance of {@link AbstractVersionedLayer} with the same properties as the specified {@link AbstractVersionedLayer}
	 * @param abstractLayer
	 */
	protected AbstractVersionedLayer(AbstractVersionedLayer<T> astractVersionedLayer) {
		super(astractVersionedLayer);
		this.history = astractVersionedLayer.history.deepClone();
		this.urrManager = astractVersionedLayer.urrManager.deepClone();
	}


	/**
	 * Creates an instance of {@link AbstractVersionedLayer}
	 * @param track track displaying the layer
	 * @param data data displayed by the layer
	 */
	public AbstractVersionedLayer(Track track, T data) {
		this(track, data, null);
	}


	/**
	 * Creates an instance of {@link AbstractVersionedLayer}
	 * @param track track displaying the layer
	 * @param data data displayed by the layer
	 * @param name name of the layer
	 */
	public AbstractVersionedLayer(Track track, T data, String name) {
		super(track, data, name);
		this.history = new History();
		this.urrManager = new URRManager<T>(ConfigurationManager.getInstance().getUndoCount(), data);
	}


	@Override
	public abstract AbstractVersionedLayer<T> clone();


	@Override
	public void deactivateReset() {
		urrManager.deactivateReset();
	}


	@Override
	public History getHistory() {
		return history;
	}


	@Override
	public boolean isRedoable() {
		return urrManager.isRedoable();
	}


	@Override
	public boolean isResetable() {
		return urrManager.isResetable();
	}


	@Override
	public boolean isUndoable() {
		return urrManager.isUndoable();
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
		history = (History) in.readObject();
		urrManager = (URRManager<T>) in.readObject();
	}


	@Override
	public void redo() {
		try {
			if (isRedoable()) {
				super.setData(urrManager.redo());
				getTrack().repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while redoing the " + getName() + " layer");
			history.setLastAsError();
		}
	}


	@Override
	public void reset() {
		try {
			if (isResetable()) {
				super.setData(urrManager.reset());
				getTrack().repaint();
				history.reset();
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while reseting the " + getName() + " layer");
			history.setLastAsError();
		}
	}


	@Override
	public <U extends T> void setData(U data) {
		if (data != null) {
			try {
				urrManager.set(data);
				super.setData(data);
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while updating the track");
			}
		}
	}


	/**
	 * Sets the data showed in the track
	 * @param data the data showed in the track
	 * @param description description of the data
	 */
	@Override
	public <U extends T> void setData(U data, String description) {
		if (data != null) {
			try {
				history.add(description);
				urrManager.set(data);
				super.setData(data);
			} catch (Exception e) {
				ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while updating the track");
				history.setLastAsError();
			}
		}
	}


	@Override
	public void setUndoCount(int undoCount) {
		urrManager.setLength(undoCount);
	}


	@Override
	public void undo() {
		try {
			if (isUndoable()) {
				super.setData(urrManager.undo());
				getTrack().repaint();
				history.undo();
			}
		} catch (Exception e) {
			ExceptionManager.getInstance().caughtException(Thread.currentThread(), e, "Error while undoing the " + getName() + " layer");
			history.setLastAsError();
		}
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(history);
		out.writeObject(urrManager);
	}
}
