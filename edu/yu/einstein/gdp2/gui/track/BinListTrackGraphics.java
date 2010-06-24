/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMaxScoreToDisplay;
import yu.einstein.gdp2.core.list.binList.operation.BLOMinScoreToDisplay;
import yu.einstein.gdp2.core.list.binList.operation.BLOSerializeAndZip;
import yu.einstein.gdp2.core.list.binList.operation.BLOUnzipAndUnserialize;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.gui.track.drawer.BinListDrawer;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;
import yu.einstein.gdp2.util.History;

/**
 * A {@link TrackGraphics} part of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackGraphics extends CurveTrackGraphics implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1745399422702517182L;	// generated ID

	private BinList 								binList;			// Value of the displayed BinList
	private History									history = null;		// History containing a description of the actions done
	transient private ByteArrayOutputStream			initialBinList;		// Value of the BinList when the track is created (the BinList is serialized and zipped)
	transient private List<ByteArrayOutputStream>	undoBinLists = null;	// BinList used to restore when undo (the BinList is serialized and zipped)
	transient private List<ByteArrayOutputStream>	redoBinLists = null;	// BinList used to restore when redo (the BinList is serialized and zipped)
	private BinList 								initialSaver = null;	// used for the serialization of the initial BinList (since a ByteArrayOutputStream can't be serialized)
	private List<BinList> 							undoSaver = null;		// used for the serialization of the undo BinList (since a ByteArrayOutputStream can't be serialized)
	private List<BinList> 							redoSaver = null;		// used for the serialization of the redo BinList (since a ByteArrayOutputStream can't be serialized)

	
	/**
	 * Creates an instance of a {@link BinListTrackGraphics}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param yMin minimum score
	 * @param yMax maximum score
	 * @param binList {@link BinList}
	 * @throws BinListNoDataException
	 */
	protected BinListTrackGraphics(GenomeWindow displayedGenomeWindow, BinList binList) {
		super(displayedGenomeWindow, new BLOMinScoreToDisplay(binList).compute(), new BLOMaxScoreToDisplay(binList).compute());
		this.binList = binList;
		this.history = new History();
		undoBinLists = new LinkedList<ByteArrayOutputStream>();
		redoBinLists = new LinkedList<ByteArrayOutputStream>();
	}


	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		BinListTrackGraphics bltg = ((BinListTrackGraphics)trackGraphics);
		bltg.initialBinList = this.initialBinList;
		bltg.binList = this.binList.deepClone();
		if (undoBinLists != null) {
			bltg.undoBinLists = new ArrayList<ByteArrayOutputStream>(this.undoBinLists);
		}
		if (redoBinLists != null) {
			bltg.redoBinLists = new ArrayList<ByteArrayOutputStream>(this.redoBinLists);
		}
		bltg.history = this.history.deepClone();
	}


	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawScore(java.awt.Graphics)
	 */
	@Override
	protected void drawScore(Graphics g) {
		try {
			short currentChromosome = ChromosomeManager.getInstance().getIndex(genomeWindow.getChromosome());
			g.setColor(Color.red);
			int xMid = (int)genomeWindow.getMiddlePosition();
			double yMid = 0;
			if ((binList.get(currentChromosome) != null) && ((xMid / binList.getBinSize()) < binList.size(currentChromosome))) { 
				//yMid = binList.get(currentChromosome, xMid / binList.getBinSize());
				yMid = binList.getScore(xMid);
			}
			g.drawString("y=" + SCORE_FORMAT.format(yMid), getWidth() / 2 + 3, getHeight() - 2);
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while drawing the coordinates");
		}
	}


	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#yFactorChanged()
	 */
	@Override
	protected void yFactorChanged() {
		repaint();
	}


	/**
	 * Returns the BinList of the track.
	 * @return A binList.
	 */
	public BinList getBinList() {
		return binList;
	}


	/**
	 * Sets a BinList that will be used as input data for the track.   
	 * @param binList a BinList
	 * @param description description of binList
	 */
	public void setBinList(BinList binList, String description) {
		if (binList != null) {
			try {
				int undoCount = ConfigurationManager.getInstance().getUndoCount();
				history.add(description);
				// if it's the first operation
				if (initialBinList == null) {
					initialBinList = new BLOSerializeAndZip(this.binList).compute();
					// the first element of the undo is the initial binlist
					if (undoCount > 0) {
						undoBinLists.add(initialBinList);
					}
				} else {
					// if the undoBinLists is full (ie: more elements than undo count in the config manager)
					if (undoBinLists.size() >= undoCount) {
						undoBinLists.remove(0);
					}
					undoBinLists.add(new BLOSerializeAndZip(this.binList).compute());
				}
				redoBinLists.clear();
				firePropertyChange("binList", this.binList, binList);
				this.binList = binList;
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e, "Error while updating the track");
				history.setLastAsError();
			}	
		}
	}


	/**
	 * Resets the BinList. Copies the value of the original BinList into the current value. 
	 */
	public void resetBinList() {
		try {
			int undoCount = ConfigurationManager.getInstance().getUndoCount();
			if (undoBinLists.size() >= undoCount) {
				undoBinLists.remove(0);
			}
			undoBinLists.add(new BLOSerializeAndZip(this.binList).compute());
			redoBinLists.clear();
			BinList newBinList = new BLOUnzipAndUnserialize(initialBinList).compute();; 
			initialBinList = null;
			firePropertyChange("binList", binList, newBinList);
			binList = newBinList;
			yMin = new BLOMinScoreToDisplay(binList).compute();
			yMax = new BLOMaxScoreToDisplay(binList).compute();
			repaint();
			history.reset();
		}
		catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while reseting");
			history.setLastAsError();
		}
	}


	/**
	 * Undoes last action. 
	 */
	public void undo() {
		try {
			if ((undoBinLists != null) && (!undoBinLists.isEmpty())) {
				redoBinLists.add(new BLOSerializeAndZip(this.binList).compute());
				// we unserialize the last BinList in of the undo lists and we remove it from the undo list
				int lastIndex = undoBinLists.size() - 1;
				ByteArrayOutputStream baos = undoBinLists.get(lastIndex);
				if (initialBinList == null) {
					initialBinList = new BLOSerializeAndZip(this.binList).compute();
				} 
				if (initialBinList == baos) {
					initialBinList = null;
				}
				BinList newBinList = new BLOUnzipAndUnserialize(baos).compute();
				firePropertyChange("binList", binList, newBinList);
				binList = newBinList;
				undoBinLists.remove(lastIndex);
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
				history.undo();
			} 
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while undoing");
			history.setLastAsError();
		}		
	}


	/**
	 * Redoes last action.
	 */
	public void redo() {
		try {
			if ((redoBinLists != null) && (!redoBinLists.isEmpty())) {
				int lastIndex = redoBinLists.size() - 1;
				ByteArrayOutputStream baos = redoBinLists.get(lastIndex);
				if (initialBinList == null) {
					initialBinList = new BLOSerializeAndZip(this.binList).compute();
				} 
				if (initialBinList == baos) {
					initialBinList = null;
				}
				BinList newBinList = new BLOUnzipAndUnserialize(baos).compute();;
				firePropertyChange("binList", binList, newBinList);
				binList = newBinList;
				redoBinLists.remove(lastIndex);
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while redoing");
			history.setLastAsError();
		}	
	}


	/**
	 * @return true if the action undo is possible
	 */
	public boolean isUndoable() {
		return ((undoBinLists != null) && (!undoBinLists.isEmpty()));
	}


	/**
	 * @return true if the action redo is possible
	 */
	public boolean isRedoable() {
		return ((redoBinLists != null) && (!redoBinLists.isEmpty()));
	}

	
	/**
	 * @return true if the track can be reseted
	 */
	public boolean isResetable() {
		return initialBinList != null;
	}
	
	
	/**
	 * @return the history of the current track.
	 */
	public History getHistory() {
		return history;
	}


	/**
	 * Unserializes the initial, undo and redo BinLists so they can 
	 * be serialized with the rest of the current instance and saved.
	 * This is because ByteArrayOutputStream can't be serialized
	 * @param out {@link ObjectOutputStream}
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		try {
			// unserialize the initial BinList
			if (initialBinList != null) {
				initialSaver = new BLOUnzipAndUnserialize(initialBinList).compute();
			}
			// unserialize the undo BinLists
			if (undoBinLists != null) {
				undoSaver = new ArrayList<BinList>();
				for (ByteArrayOutputStream currentList: undoBinLists) {
					undoSaver.add(new BLOUnzipAndUnserialize(currentList).compute());
				}
			}
			// unserialize the redo BinLists
			if (redoBinLists != null) {
				redoSaver = new ArrayList<BinList>();
				for (ByteArrayOutputStream currentList: redoBinLists) {
					redoSaver.add(new BLOUnzipAndUnserialize(currentList).compute());
				}
			}
			out.defaultWriteObject();
			initialSaver = null;
			undoSaver = null;
			redoSaver = null;
		} catch (ClassNotFoundException e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while saving a BinListTrack");
		}
	}


	/**
	 * Serializes and zips the initial, undo and redo BinLists 
	 * after the unserialization of an instance.
	 * @param in {@link ObjectInputStream}
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int undoCount = ConfigurationManager.getInstance().getUndoCount();
		in.defaultReadObject();
		if (initialSaver != null) {
			initialBinList = new BLOSerializeAndZip(initialSaver).compute();
			initialSaver = null;
		}
		if ((undoSaver != null) && (!undoSaver.isEmpty())) {
			// if the undo saver list is longer than the authorized count of undo
			// we remove the first elements of the undo saver
			while (undoCount - undoSaver.size() < 0) {
				undoSaver.remove(0);
			}
			for (BinList currentList: undoSaver) {
				undoBinLists.add(new BLOSerializeAndZip(currentList).compute());
			}
			undoSaver = null;
		}
		if (redoSaver != null) {
			// if the redo saver list is longer than the authorized count of undo
			// we remove the first elements of the redo saver
			while (undoCount - redoSaver.size() < 0) {
				redoSaver.remove(0);
			}
			for (BinList currentList: redoSaver) {
				redoBinLists.add(new BLOSerializeAndZip(currentList).compute());
			}
			redoSaver = null;
		}
	}


	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new BinListDrawer(g, getWidth(), getHeight(), genomeWindow, yMin, yMax, trackColor, typeOfGraph, binList);
		cd.draw();		
	}


	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight, GenomeWindow genomeWindow, double scoreMin, double scoreMax) {
		return new BinListDrawer(g, trackWidth, trackHeight, genomeWindow, scoreMin, scoreMax, trackColor, typeOfGraph, binList);
	}
}
