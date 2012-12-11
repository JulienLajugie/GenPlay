/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.old.popupMenu;

import javax.swing.JMenuItem;

import edu.yu.einstein.genplay.gui.old.action.scoredTrack.STASetYAxis;
import edu.yu.einstein.genplay.gui.old.track.ScoredTrack;
import edu.yu.einstein.genplay.gui.old.trackList.TrackList;



/**
 * Abstract class. Popup menus for a {@link ScoredTrack}
 * @author Julien Lajugie
 * @version 0.1
 */
public class ScoredTrackMenu extends TrackMenu {
	
	private static final long serialVersionUID = -1426149345513389079L; // generated ID
	private final JMenuItem		jmiSetYAxis; // menu set maximum and minimum score
	
	
	/**
	 * Creates an instance of {@link ScoredTrackMenu}
	 * @param tl {@link TrackList} of the project
	 */
	public ScoredTrackMenu(TrackList tl) {
		super(tl);		
		jmiSetYAxis= new JMenuItem(actionMap.get(STASetYAxis.ACTION_KEY));		
		addSeparator();	
		add(jmiSetYAxis);
	}
}
