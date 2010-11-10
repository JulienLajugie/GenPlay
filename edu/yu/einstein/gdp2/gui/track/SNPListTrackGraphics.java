/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.track;

import java.awt.Graphics;

import yu.einstein.gdp2.core.GenomeWindow;
import yu.einstein.gdp2.core.SNPList.SNPList;


/**
 *  A {@link TrackGraphics} part of a track showing SNPS
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPListTrackGraphics extends TrackGraphics<SNPList> {

	private static final long serialVersionUID = -5740813392910733205L; // generated ID

	
	/**
	 * Creates an instance of {@link SNPListTrackGraphics}
	 * @param displayedGenomeWindow
	 * @param data
	 */
	protected SNPListTrackGraphics(GenomeWindow displayedGenomeWindow, SNPList data) {
		super(displayedGenomeWindow, data);
	}

	
	@Override
	protected void drawTrack(Graphics g) {
		// TODO Auto-generated method stub
		
	}
}
