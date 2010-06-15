/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.gui.popupMenu;

import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import yu.einstein.gdp2.gui.action.allTrack.ATACopy;
import yu.einstein.gdp2.gui.action.allTrack.ATACut;
import yu.einstein.gdp2.gui.action.allTrack.ATADelete;
import yu.einstein.gdp2.gui.action.allTrack.ATALoadStripes;
import yu.einstein.gdp2.gui.action.allTrack.ATAPaste;
import yu.einstein.gdp2.gui.action.allTrack.ATARemoveStripes;
import yu.einstein.gdp2.gui.action.allTrack.ATARename;
import yu.einstein.gdp2.gui.action.allTrack.ATASaveAsImage;
import yu.einstein.gdp2.gui.action.allTrack.ATASetHeight;
import yu.einstein.gdp2.gui.trackList.TrackList;


/**
 * Base class of the popup menus of a {@link TrackList}
 * @author Julien Lajugie
 * @version 0.1
 */
public abstract class TrackMenu extends JPopupMenu implements PopupMenuListener {

	private static final long serialVersionUID = -2376957246826289131L;	// generated ID

	private final JMenuItem 	jmiCopy;			// menu copy track
	private final JMenuItem 	jmiCut;				// menu cut track
	private final JMenuItem		jmiPaste;			// menu paste track
	private final JMenuItem 	jmiDelete;			// menu delete track
	private final JMenuItem 	jmiRename;			// menu rename track
	private final JMenuItem 	jmiSetHeight;		// menu set height 
	private final JMenuItem 	jmiSaveAsImage;		// menu save track as image
	private final JMenuItem 	jmiLoadStripes;		// menu load stripes
	private final JMenuItem 	jmiRemoveStripes;	// menu remove stripe

	protected final TrackList 	trackList;			// track list where the menu popped up
	protected final ActionMap	actionMap;			// map containing the actions for this menu
	
	
	/**
	 * Constructor.
	 * @param tl {@link TrackList} where the menu popped up
	 */
	public TrackMenu(TrackList tl) {
		super ("Track Menu");
		this.trackList = tl;
		this.actionMap = tl.getActionMap();
		
		jmiCopy = new JMenuItem(actionMap.get(ATACopy.ACTION_KEY));
		jmiCut = new JMenuItem(actionMap.get(ATACut.ACTION_KEY));
		jmiPaste = new JMenuItem(actionMap.get(ATAPaste.ACTION_KEY));
		jmiDelete = new JMenuItem(actionMap.get(ATADelete.ACTION_KEY));
		jmiRename = new JMenuItem(actionMap.get(ATARename.ACTION_KEY));
		jmiSetHeight = new JMenuItem(actionMap.get(ATASetHeight.ACTION_KEY));
		jmiSaveAsImage = new JMenuItem(actionMap.get(ATASaveAsImage.ACTION_KEY));
		jmiLoadStripes = new JMenuItem(actionMap.get(ATALoadStripes.ACTION_KEY));
		jmiRemoveStripes = new JMenuItem(actionMap.get(ATARemoveStripes.ACTION_KEY));		
		
		add(jmiCopy);
		add(jmiCut);
		add(jmiPaste);
		add(jmiDelete);
		add(jmiRename);
		add(jmiSetHeight);
		addSeparator();
		add(jmiSaveAsImage);
		addSeparator();
		add(jmiLoadStripes);
		add(jmiRemoveStripes);
		
		jmiPaste.setEnabled(trackList.isPasteEnable());
		jmiRemoveStripes.setEnabled(trackList.isRemoveStripesEnable());
		
		addPopupMenuListener(this);
	}

	
	@Override
	public void popupMenuCanceled(PopupMenuEvent arg0) {}

	
	/**
	 * Unlocks the handle of the tracks when a menu disappear
	 */
	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
		trackList.unlockTracksHandles();
	}

	
	/**
	 * Locks the handle of the tracks when a menu appear
	 */
	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
		trackList.lockTrackHandles();		
	}
}
