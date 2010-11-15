/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import yu.einstein.gdp2.gui.track.BinListTrack;
import yu.einstein.gdp2.gui.track.EmptyTrack;
import yu.einstein.gdp2.gui.track.GeneListTrack;
import yu.einstein.gdp2.gui.track.MultiCurvesTrack;
import yu.einstein.gdp2.gui.track.NucleotideListTrack;
import yu.einstein.gdp2.gui.track.RepeatFamilyListTrack;
import yu.einstein.gdp2.gui.track.SCWListTrack;
import yu.einstein.gdp2.gui.track.SNPListTrack;
import yu.einstein.gdp2.gui.track.Track;
import yu.einstein.gdp2.gui.trackList.TrackList;

/**
 * Creates an instance of a subclass of {@link TrackMenu} depending on the instance of the 
 * selected {@link Track} of a {@link TrackList} 
 * @author Julien Lajugie
 * @version 0.1
 */
public final class TrackMenuFactory {
	
	/**
	 * @param tl a {@link TrackList}
	 * @return an instance of a subclass of {@link TrackMenu} depending on the  
	 * instance of the selected {@link Track} of a {@link TrackList} 
	 */
	public static TrackMenu getTrackMenu (TrackList tl) {
		if (tl.getSelectedTrack() instanceof EmptyTrack) {
			return new EmptyTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof BinListTrack) {
			return new BinListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof RepeatFamilyListTrack) {
			return new RepeatFamilyListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof SCWListTrack) {
			return new SCWListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof GeneListTrack) {
			return new GeneListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof NucleotideListTrack) {
			return new NucleotideListTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof MultiCurvesTrack) {
			return new MultiCurvesTrackMenu(tl);
		} else if (tl.getSelectedTrack() instanceof SNPListTrack) {
			return new SNPListTrackMenu(tl);
		} else {
			return null;
		}
	}
}
