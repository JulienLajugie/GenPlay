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

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.list.binList.operation.BLOMaxScoreToDisplay;
import yu.einstein.gdp2.core.list.binList.operation.BLOMinScoreToDisplay;
import yu.einstein.gdp2.core.manager.ChromosomeManager;
import yu.einstein.gdp2.core.manager.ConfigurationManager;
import yu.einstein.gdp2.core.manager.ExceptionManager;
import yu.einstein.gdp2.core.manager.URRManager;
import yu.einstein.gdp2.gui.track.drawer.BinListDrawer;
import yu.einstein.gdp2.gui.track.drawer.CurveDrawer;
import yu.einstein.gdp2.util.History;

/**
 * A {@link TrackGraphics} part of a {@link BinListTrack}
 * 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrackGraphics extends CurveTrackGraphics implements
		MouseListener, MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = 1745399422702517182L; // generated
																		// ID
	/**
	 */
	private BinList binList; // value of the displayed BinList
	/**
	 */
	private History history = null; // history containing a description of the
									// actions done
	/**
	 */
	private URRManager<BinList> urrManager; // manager that handles the undo /
											// redo / reset of the track

	/**
	 * Creates an instance of a {@link BinListTrackGraphics}
	 * 
	 * @param displayedGenomeWindow
	 *            displayed {@link GenomeWindow}
	 * @param yMin
	 *            minimum score
	 * @param yMax
	 *            maximum score
	 * @param binList
	 *            {@link BinList}
	 * @throws BinListNoDataException
	 */
	protected BinListTrackGraphics(GenomeWindow displayedGenomeWindow,
			BinList binList) {
		super(displayedGenomeWindow, new BLOMinScoreToDisplay(binList)
				.compute(), new BLOMaxScoreToDisplay(binList).compute());
		this.binList = binList;
		this.history = new History();
		urrManager = new URRManager<BinList>(ConfigurationManager.getInstance()
				.getUndoCount(), binList);
	}

	@Override
	public void copyTo(TrackGraphics trackGraphics) {
		super.copyTo(trackGraphics);
		BinListTrackGraphics bltg = ((BinListTrackGraphics) trackGraphics);
		bltg.binList = this.binList.deepClone();
		bltg.urrManager = this.urrManager.deepClone();
		bltg.history = this.history.deepClone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * yu.einstein.gdp2.gui.track.CurveTrackGraphics#drawScore(java.awt.Graphics
	 * )
	 */
	@Override
	protected void drawScore(Graphics g) {
		try {
			short currentChromosome = ChromosomeManager.getInstance().getIndex(
					genomeWindow.getChromosome());
			g.setColor(Color.red);
			int xMid = (int) genomeWindow.getMiddlePosition();
			double yMid = 0;
			if ((binList.get(currentChromosome) != null)
					&& ((xMid / binList.getBinSize()) < binList
							.size(currentChromosome))) {
				// yMid = binList.get(currentChromosome, xMid /
				// binList.getBinSize());
				yMid = binList.getScore(xMid);
			}
			g.drawString("y=" + SCORE_FORMAT.format(yMid), getWidth() / 2 + 3,
					getHeight() - 2);
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e,
					"Error while drawing the coordinates");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see yu.einstein.gdp2.gui.track.CurveTrackGraphics#yFactorChanged()
	 */
	@Override
	protected void yFactorChanged() {
		repaint();
	}

	/**
	 * Returns the BinList of the track.
	 * 
	 * @return A binList.
	 */
	public BinList getBinList() {
		return binList;
	}

	/**
	 * Sets a BinList that will be used as input data for the track.
	 * 
	 * @param binList
	 *            a BinList
	 * @param description
	 *            description of binList
	 */
	public void setBinList(BinList binList, String description) {
		if (binList != null) {
			try {
				history.add(description);
				urrManager.set(binList);
				this.binList = binList;
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
			} catch (Exception e) {
				ExceptionManager.handleException(getRootPane(), e,
						"Error while updating the track");
				history.setLastAsError();
			}
		}
	}

	/**
	 * Resets the BinList. Copies the value of the original BinList into the
	 * current value.
	 */
	public void resetBinList() {
		try {
			if (isResetable()) {
				binList = urrManager.reset();
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
				history.reset();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e,
					"Error while reseting");
			history.setLastAsError();
		}
	}

	/**
	 * Undoes last action.
	 */
	public void undo() {
		try {
			if (isUndoable()) {
				binList = urrManager.undo();
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
				history.undo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e,
					"Error while undoing");
			history.setLastAsError();
		}
	}

	/**
	 * Redoes last action.
	 */
	public void redo() {
		try {
			if (isRedoable()) {
				binList = urrManager.redo();
				yMin = new BLOMinScoreToDisplay(binList).compute();
				yMax = new BLOMaxScoreToDisplay(binList).compute();
				repaint();
				history.redo();
			}
		} catch (Exception e) {
			ExceptionManager.handleException(getRootPane(), e,
					"Error while redoing");
			history.setLastAsError();
		}
	}

	/**
	 * @return true if the action undo is possible
	 */
	public boolean isUndoable() {
		return urrManager.isUndoable();
	}

	/**
	 * @return true if the action redo is possible
	 */
	public boolean isRedoable() {
		return urrManager.isRedoable();
	}

	/**
	 * @return true if the track can be reseted
	 */
	public boolean isResetable() {
		return urrManager.isResetable();
	}

	/**
	 * @return the history of the current track.
	 */
	public History getHistory() {
		return history;
	}

	@Override
	protected void drawData(Graphics g) {
		CurveDrawer cd = new BinListDrawer(g, getWidth(), getHeight(),
				genomeWindow, yMin, yMax, trackColor, typeOfGraph, binList);
		cd.draw();
	}

	@Override
	public CurveDrawer getDrawer(Graphics g, int trackWidth, int trackHeight,
			GenomeWindow genomeWindow, double scoreMin, double scoreMax) {
		return new BinListDrawer(g, trackWidth, trackHeight, genomeWindow,
				scoreMin, scoreMax, trackColor, typeOfGraph, binList);
	}
}
