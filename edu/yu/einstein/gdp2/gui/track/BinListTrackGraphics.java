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

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BinListOperations;
import yu.einstein.gdp2.gui.track.drawer.BinListDrawer;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.ExceptionManager;
import yu.einstein.gdp2.util.History;
import yu.einstein.gdp2.util.ZoomManager;

/**
 * A {@link TrackGraphics} part of a {@link BinListTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackGraphics extends CurveTrackGraphics implements MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1745399422702517182L;	// generated ID

	private final ChromosomeManager 		chromosomeManager;	// ChromosomeManager
	private BinList 						binList;			// Value of the displayed BinList
	transient private ByteArrayOutputStream	initialBinList;		// Value of the BinList when the track is created (the BinList is serialized and zipped)
	transient private ByteArrayOutputStream	undoBinList = null;	// BinList used to restore when undo (the BinList is serialized and zipped)
	transient private ByteArrayOutputStream	redoBinList = null;	// BinList used to restore when redo (the BinList is serialized and zipped)
	private History							history = null;		// History containing a description of the actions done

	private BinList initialSaver = null;	// used for the serialization of the initial BinList (since a ByteArrayOutputStream can't be serialized)
	private BinList undoSaver = null;		// used for the serialization of the undo BinList (since a ByteArrayOutputStream can't be serialized)
	private BinList redoSaver = null;		// used for the serialization of the redo BinList (since a ByteArrayOutputStream can't be serialized)

	/**
	 * Creates an instance of a {@link BinListTrackGraphics}
	 * @param zoomManager {@link ZoomManager}
	 * @param displayedGenomeWindow displayed {@link GenomeWindow}
	 * @param yMin minimum score
	 * @param yMax maximum score
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binList {@link BinList}
	 * @throws BinListNoDataException
	 */
	protected BinListTrackGraphics(ZoomManager zoomManager, GenomeWindow displayedGenomeWindow, ChromosomeManager chromosomeManager, BinList binList) {
		super(zoomManager, displayedGenomeWindow, BinListOperations.minScoreToDisplay(binList), BinListOperations.maxScoreToDisplay(binList));
		this.chromosomeManager = chromosomeManager;
		try {
			this.initialBinList = BinListOperations.serializeAndZip(binList);
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while loading the track");
		}
		this.binList = binList;
		this.history = new History();
	}


	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		((BinListTrackGraphics)trackGraphics).initialBinList = this.initialBinList;
		((BinListTrackGraphics)trackGraphics).binList = this.binList.deepClone();
		if (undoBinList != null) {
			((BinListTrackGraphics)trackGraphics).undoBinList = this.undoBinList;
		}
		if (redoBinList != null) {
			((BinListTrackGraphics)trackGraphics).redoBinList = this.redoBinList;
		}
		((BinListTrackGraphics)trackGraphics).history = this.history.deepClone();
	}


	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawScore(java.awt.Graphics)
	 */
	@Override
	protected void drawScore(Graphics g) {
		try {
			short currentChromosome = chromosomeManager.getIndex(genomeWindow.getChromosome());
			g.setColor(Color.red);
			int xMid = (int)genomeWindow.getMiddlePosition();
			double yMid = 0;
			if ((binList.get(currentChromosome) != null) && ((xMid / binList.getBinSize()) < binList.size(currentChromosome))) { 
				yMid = binList.get(currentChromosome, xMid / binList.getBinSize());
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
		try {
			history.add(description);
			undoBinList = BinListOperations.serializeAndZip(this.binList);
			redoBinList = null;
			firePropertyChange("binList", this.binList, binList);
			this.binList = binList;
			yMin = BinListOperations.minScoreToDisplay(binList);
			yMax = BinListOperations.maxScoreToDisplay(binList);
			repaint();
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while adding a damper");
			history.setLastAsError();
		}	
	}


	/**
	 * Resets the BinList. Copies the value of the original BinList into the current value. 
	 */
	public void resetBinList() {
		try {
			undoBinList = BinListOperations.serializeAndZip(binList);
			redoBinList = null;
			BinList newBinList = BinListOperations.unzipAndUnserialize(initialBinList); 
			firePropertyChange("binList", binList, newBinList);
			binList = newBinList;
			yMin = BinListOperations.minScoreToDisplay(binList);
			yMax = BinListOperations.maxScoreToDisplay(binList);
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
			if (undoBinList != null) {
				redoBinList = BinListOperations.serializeAndZip(binList);
				BinList newBinList = BinListOperations.unzipAndUnserialize(undoBinList); 
				firePropertyChange("binList", binList, newBinList);
				binList = newBinList;
				undoBinList = null;
				yMin = BinListOperations.minScoreToDisplay(binList);
				yMax = BinListOperations.maxScoreToDisplay(binList);
				repaint();
				history.undo();
			} 
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while reseting");
			history.setLastAsError();
		}		
	}


	/**
	 * Redoes last action.
	 */
	public void redo() {
		try {
			if (redoBinList != null) {
				undoBinList = BinListOperations.serializeAndZip(binList);				
				BinList newBinList = BinListOperations.unzipAndUnserialize(redoBinList);
				firePropertyChange("binList", binList, newBinList);
				binList = newBinList;
				redoBinList = null;
				yMin = BinListOperations.minScoreToDisplay(binList);
				yMax = BinListOperations.maxScoreToDisplay(binList);
				repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e, "Error while reseting");
			history.setLastAsError();
		}	
	}


	/**
	 * @return True if the action undo is possible.
	 */
	public boolean isUndoable() {
		return (undoBinList != null);
	}


	/**
	 * @return True if the action redo is possible.
	 */
	public boolean isRedoable() {
		return (redoBinList != null);
	}

	/**
	 * @return the history of the current track.
	 */
	public History getHistory() {
		return history;
	}


	/**
	 * Unserializes the initial, undo and redo BinList so they can 
	 * be serialized with the rest of the current instance and saved.
	 * This is because ByteArrayOutputStream can't be serialized
	 * @param out {@link ObjectOutputStream}
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		try {
			if (initialBinList != null) {
				initialSaver = BinListOperations.unzipAndUnserialize(initialBinList);
			}
			if (undoBinList != null) {
				undoSaver = BinListOperations.unzipAndUnserialize(undoBinList);
			}
			if (redoBinList != null) {
				redoSaver = BinListOperations.unzipAndUnserialize(redoBinList);
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
	 * Serializes and zips the initial, undo and redo BinList 
	 * after the unserialization of an instance.
	 * @param in {@link ObjectInputStream}
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		if (initialSaver != null) {
			initialBinList = BinListOperations.serializeAndZip(initialSaver);
			initialSaver = null;
		}
		if (undoSaver != null) {
			undoBinList = BinListOperations.serializeAndZip(undoSaver);
			undoSaver = null;
		}
		if (redoSaver != null) {
			redoBinList = BinListOperations.serializeAndZip(redoSaver);
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
