/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Color;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.list.binList.BinList;

/**
 * A track containing a {@link BinList}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinListTrack extends CurveTrack<BinList> {

	private static final long serialVersionUID = -395099043710070726L; // generated ID
	private final BinList binList; // BinList used to create the track

	
	/**
	 * Creates an instance of {@link BinListTrack}
	 * @param displayedGenomeWindow the displayed {@link GenomeWindow}
	 * @param trackNumber the number of the track
	 * @param binList the {@link BinList} showed in the track
	 */
	public BinListTrack(GenomeWindow displayedGenomeWindow, int trackNumber, BinList binList) {
		this.binList = binList;
		initComponent(displayedGenomeWindow, trackNumber);
	}


	@Override
	public Track copy() {
		Track copiedTrack = new BinListTrack(trackGraphics.genomeWindow, trackHandle.getTrackNumber(), binList);
		trackGraphics.copyTo(copiedTrack.trackGraphics);
		trackGraphics.repaint();
		copiedTrack.setPreferredHeight(getPreferredSize().height);
		return copiedTrack;
	}


	@Override
	protected void initTrackGraphics(GenomeWindow displayedGenomeWindow) {
		trackGraphics = new BinListTrackGraphics(displayedGenomeWindow, binList);
	}

	
	/**
	 * Renames the track
	 * @param newName a new name for the track
	 */
	@Override
	public void setName(String newName) {
		// add the name of the track to the history
		getHistory().add("Track Name: \"" + newName + "\"",	new Color(0, 100, 0));
		super.setName(newName);
	}
}
