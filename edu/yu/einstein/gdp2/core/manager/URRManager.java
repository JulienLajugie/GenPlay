/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A generic class to provides tool to handle the Undo / Redo / Reset actions
 * 
 * @author Julien Lajugie
 * @version 0.1
 * @param <T>
 *            type of the object to restore with undo / redo / reset
 */
public class URRManager<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -7259155655274729887L; // generated
																		// ID
	/**
	 */
	private int length; // number of action that can be undone / redone
	/**
	 */
	private T currentObject; // current object
	/**
	 */
	private T initialObjectSaver; // initial object to restore with the reset
									// action
	/**
	 */
	private transient ByteArrayOutputStream initialObject; // the initial object
															// in it's
															// compressed form.
															// Transient because
															// a BAOS can't be
															// serialized
	/**
	 */
	private transient List<ByteArrayOutputStream> undoList; // a list of object
															// to restore with
															// the undo action
															// in a compressed
															// form.
	/**
	 */
	private transient List<ByteArrayOutputStream> redoList; // a list of object
															// to restore with
															// the redo action
															// in a compressed
															// form.
	/**
	 */
	private List<T> undoListSaver; // the list of undo in an uncompressed form.
									// Used only for the serialization
	/**
	 */
	private List<T> redoListSaver; // the list of redo in an uncompressed form.
									// Used only for the serialization

	/**
	 * Creates an instance of {@link URRManager}
	 * 
	 * @param length
	 *            number i
	 * @param initialObject
	 */
	public URRManager(int length, T initialObject) {
		this.length = length;
		this.currentObject = initialObject;
		undoList = new LinkedList<ByteArrayOutputStream>();
		redoList = new LinkedList<ByteArrayOutputStream>();
	}

	public void set(T newObject) throws IOException {
		if (newObject != null) {
			ByteArrayOutputStream oldBaos = null;
			// if it's the first operation
			if (initialObject == null) {
				oldBaos = serializeAndZip(currentObject);
				initialObject = oldBaos;
			}
			// if we accept the undo operation
			if (length > 0) {
				// if the undoBinLists is full (ie: more elements than undo
				// count in the config manager)
				if (undoList.size() >= length) {
					undoList.remove(0);
				}
				if (oldBaos == null) {
					oldBaos = serializeAndZip(currentObject);
				}
				undoList.add(oldBaos);
			}
			currentObject = newObject;
			redoList.clear();
		}
	}

	public T undo() throws IOException, ClassNotFoundException {
		if (this.isUndoable()) {
			ByteArrayOutputStream oldBaos = serializeAndZip(currentObject);
			redoList.add(oldBaos);
			// we unserialize the last BinList in of the undo lists and we
			// remove it from the undo list
			int lastIndex = undoList.size() - 1;
			ByteArrayOutputStream newBaos = undoList.get(lastIndex);
			if (initialObject == null) {
				initialObject = oldBaos;
			} else if (initialObject.equals(newBaos)) {
				initialObject = null;
			}
			currentObject = unzipAndUnserialize(newBaos);
			undoList.remove(lastIndex);
			return currentObject;
		} else {
			return null;
		}
	}

	public T redo() throws IOException, ClassNotFoundException {
		if (this.isRedoable()) {
			ByteArrayOutputStream oldBaos = serializeAndZip(currentObject);
			undoList.add(oldBaos);
			int lastIndex = redoList.size() - 1;
			ByteArrayOutputStream newBaos = redoList.get(lastIndex);
			if (initialObject == null) {
				initialObject = oldBaos;
			} else if (initialObject.equals(newBaos)) {
				initialObject = null;
			}
			currentObject = unzipAndUnserialize(newBaos);
			redoList.remove(lastIndex);
			return currentObject;
		} else {
			return null;
		}
	}

	public T reset() throws IOException, ClassNotFoundException {
		set(unzipAndUnserialize(initialObject));
		return currentObject;
	}

	public boolean isUndoable() {
		return (undoList != null) && (!undoList.isEmpty());
	}

	public boolean isRedoable() {
		return (redoList != null) && (!redoList.isEmpty());
	}

	public boolean isResetable() {
		return initialObject != null;
	}

	private ByteArrayOutputStream serializeAndZip(T inputObject)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gz = new GZIPOutputStream(baos);
		ObjectOutputStream oos = new ObjectOutputStream(gz);
		oos.writeObject(inputObject);
		oos.flush();
		oos.close();
		gz.flush();
		gz.close();
		return baos;
	}

	private T unzipAndUnserialize(ByteArrayOutputStream baos)
			throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		GZIPInputStream gz = new GZIPInputStream(bais);
		ObjectInputStream ois = new ObjectInputStream(gz);
		T outputObject = (T) ois.readObject();
		ois.close();
		gz.close();
		return outputObject;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		try {
			// unserialize the initial BinList
			if (this.isResetable()) {
				initialObjectSaver = unzipAndUnserialize(initialObject);
			}
			// unserialize the undo BinLists
			if (this.isUndoable()) {
				undoListSaver = new ArrayList<T>();
				for (ByteArrayOutputStream currentUndo : undoList) {
					undoListSaver.add(unzipAndUnserialize(currentUndo));
				}
			}
			// unserialize the redo BinLists
			if (this.isRedoable()) {
				redoListSaver = new ArrayList<T>();
				for (ByteArrayOutputStream currentRedo : redoList) {
					redoListSaver.add(unzipAndUnserialize(currentRedo));
				}
			}
			// write the savers
			out.defaultWriteObject();
			// delete the savers
			initialObjectSaver = null;
			undoListSaver = null;
			redoListSaver = null;
		} catch (ClassNotFoundException e) {
			throw new IOException();
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		undoList = new LinkedList<ByteArrayOutputStream>();
		redoList = new LinkedList<ByteArrayOutputStream>();
		if (initialObjectSaver != null) {
			initialObject = serializeAndZip(initialObjectSaver);
			initialObjectSaver = null;
		}
		if (undoListSaver != null) {
			// if the undo saver list is longer than the authorized count of
			// undo
			// we remove the first elements of the undo saver
			while (length - undoListSaver.size() < 0) {
				undoListSaver.remove(0);
			}
			for (T currentUndo : undoListSaver) {
				undoList.add(serializeAndZip(currentUndo));
			}
			undoListSaver = null;
		}
		if (redoListSaver != null) {
			// if the redo saver list is longer than the authorized count of
			// undo
			// we remove the first elements of the redo saver
			while (length - redoListSaver.size() < 0) {
				redoListSaver.remove(0);
			}
			for (T currentRedo : redoListSaver) {
				redoList.add(serializeAndZip(currentRedo));
			}
			redoListSaver = null;
		}
	}

	public URRManager<T> deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos
					.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (URRManager<T>) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// /**
	// * Unserializes the initial, undo and redo BinLists so they can
	// * be serialized with the rest of the current instance and saved.
	// * This is because ByteArrayOutputStream can't be serialized
	// * @param out {@link ObjectOutputStream}
	// * @throws IOException
	// */

	// /**
	// * Serializes and zips the initial, undo and redo BinLists
	// * after the unserialization of an instance.
	// * @param in {@link ObjectInputStream}
	// * @throws IOException
	// * @throws ClassNotFoundException
	// */

	// transient private ByteArrayOutputStream initialBinList; // Value of the
	// BinList when the track is created (the BinList is serialized and zipped)
	// transient private List<ByteArrayOutputStream> undoBinLists = null; //
	// BinList used to restore when undo (the BinList is serialized and zipped)
	// transient private List<ByteArrayOutputStream> redoBinLists = null; //
	// BinList used to restore when redo (the BinList is serialized and zipped)
	// private BinList initialSaver = null; // used for the serialization of the
	// initial BinList (since a ByteArrayOutputStream can't be serialized)
	// private List<BinList> undoSaver = null; // used for the serialization of
	// the undo BinList (since a ByteArrayOutputStream can't be serialized)
	// private List<BinList> redoSaver = null; // used for the serialization of
	// the redo BinList (since a ByteArrayOutputStream can't be serialized)

}
