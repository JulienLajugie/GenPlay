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
import yu.einstein.gdp2.core.list.binList.BinListOperations;
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

	private BinList initialSaver = null;
	private BinList undoSaver = null;
	private BinList redoSaver = null;

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
		super(zoomManager, displayedGenomeWindow, BinListOperations.minDisplayedScore(binList), BinListOperations.maxDisplayedScore(binList));
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
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawBarGraphics(java.awt.Graphics)
	 */
	@Override
	protected void drawBarGraphics(Graphics g) {
		double[] data = binList.getFittedData(genomeWindow, xFactor);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			// Compute the reverse color
			Color reverseCurveColor = Color.gray;
			if (!trackColor.equals(Color.black)) {
				reverseCurveColor = new Color(trackColor.getRGB() ^ 0xffffff);
			}
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			// Compute the Y = 0 position 
			int screenY0 = scoreToScreenPos(0);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * xFactor);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					int screenYPosition = scoreToScreenPos(currentIntensity);
					int rectHeight = screenYPosition - screenY0;
					if (currentIntensity > 0) {
						g.setColor(trackColor);
						g.fillRect(screenXPosition, screenYPosition, screenWindowWidth, -rectHeight);
					} else {
						g.setColor(reverseCurveColor);
						g.fillRect(screenXPosition, screenY0, screenWindowWidth, rectHeight);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}
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
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawCurveGraphics(java.awt.Graphics)
	 */
	@Override
	protected void drawCurveGraphics(Graphics g) {
		double[] data = binList.getFittedData(genomeWindow, xFactor);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			g.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * xFactor);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				int nextIndex = (currentGenomePosition + windowData) / windowData;
				if ((currentGenomePosition >= 0) && (nextIndex < data.length)){
					double currentIntensity = data[currentIndex];
					double nextIntensity = data[nextIndex];
					int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX2Position = screenX1Position + screenWindowWidth;
					int screenY1Position = scoreToScreenPos(currentIntensity);
					int screenY2Position = scoreToScreenPos(nextIntensity);
					if ((currentIntensity == 0) && (nextIntensity != 0)) {
						g.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);
					} else if ((currentIntensity != 0) && (nextIntensity == 0)) {
						g.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY1Position);
						g.drawLine(screenX2Position, screenY1Position, screenX2Position, screenY2Position);					
					} else if ((currentIntensity != 0) && (nextIntensity != 0)) {
						g.drawLine(screenX1Position, screenY1Position, screenX2Position, screenY2Position);
					}
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}
		}
	}


	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawDenseGraphics(java.awt.Graphics)
	 */
	@Override
	protected void drawDenseGraphics(Graphics g) {
		double[] data = binList.getFittedData(genomeWindow, xFactor);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.ceil(windowData * xFactor);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenXPosition = genomePosToScreenPos(currentGenomePosition);
					g.setColor(scoreToColor(currentIntensity, yMin, yMax));
					g.fillRect(screenXPosition, 0, screenWindowWidth, getHeight());
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;			
			}
		}
	}


	/* (non-Javadoc)
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawPointGraphics(java.awt.Graphics)
	 */
	@Override
	protected void drawPointGraphics(Graphics g) {
		double[] data = binList.getFittedData(genomeWindow, xFactor);
		int windowData = binList.getFittedBinSize();
		if (data != null) {
			int currentMinX = genomeWindow.getStart();
			int currentMaxX = genomeWindow.getStop();
			g.setColor(trackColor);
			// First position
			int firstGenomePosition = (currentMinX / windowData) * windowData;
			int currentGenomePosition = firstGenomePosition;		
			int i = 0;
			int screenWindowWidth = (int)Math.round(windowData * xFactor);
			while (currentGenomePosition < currentMaxX) {
				int currentIndex = currentGenomePosition / windowData;
				if ((currentGenomePosition >= 0) && (currentIndex < data.length)){
					double currentIntensity = data[currentIndex];
					int screenX1Position = genomePosToScreenPos(currentGenomePosition);
					int screenX2Position = screenX1Position + screenWindowWidth;
					int screenYPosition = scoreToScreenPos(currentIntensity);				
					g.drawLine(screenX1Position, screenYPosition, screenX2Position, screenYPosition);
				}
				i++;
				currentGenomePosition = firstGenomePosition + i * windowData;	
			}	
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
			this.binList = binList;
			yMin = BinListOperations.minDisplayedScore(binList);
			yMax = BinListOperations.maxDisplayedScore(binList);
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
			binList = BinListOperations.unzipAndUnserialize(initialBinList);
			yMin = BinListOperations.minDisplayedScore(binList);
			yMax = BinListOperations.maxDisplayedScore(binList);
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
				binList = BinListOperations.unzipAndUnserialize(undoBinList);
				undoBinList = null;
				yMin = BinListOperations.minDisplayedScore(binList);
				yMax = BinListOperations.maxDisplayedScore(binList);
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
				binList = BinListOperations.unzipAndUnserialize(redoBinList);
				redoBinList = null;
				yMin = BinListOperations.minDisplayedScore(binList);
				yMax = BinListOperations.maxDisplayedScore(binList);
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
}
