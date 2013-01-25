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
package edu.yu.einstein.genplay.gui.action.layer.binlayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.layer.BinLayer;



/**
 * Compresses / uncompresses the data of the selected {@link BinLayer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class BLACompress extends TrackListActionWorker<BinList> {

	private static final long serialVersionUID = 5156554955152029111L;	// generated ID
	private static final String 	ACTION_NAME = "Compress";			// action name
	private static final String 	DESCRIPTION =
			"Compress the data of the selected layer";						// tooltip

	private BinLayer	 			selectedLayer;						// selected layer


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BLACompress.class.getName();


	/**
	 * Creates an instance of {@link BLACompress}
	 */
	public BLACompress() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected void doAtTheEnd(BinList actionResult) {
		if (actionResult != null) {
			String description = new String();
			if (actionResult.isCompressed()) {
				description = "Compressed Mode On";
			} else {
				description = "Compressed Mode Off";
			}
			selectedLayer.setData(actionResult,  description);
		}
	}


	@Override
	protected BinList processAction() throws Exception {
		selectedLayer = (BinLayer) getValue("Layer");
		if (selectedLayer != null) {
			String actionDescription = new String();
			if (selectedLayer.getData().isCompressed()) {
				actionDescription = "Uncompressing Data";
			} else {
				actionDescription = "Compressing Data";
			}
			BinList binList = selectedLayer.getData();
			notifyActionStart(actionDescription, 1, false);
			if (binList.isCompressed()) {
				binList.uncompress();
			} else {
				binList.compress();
			}
			return binList;
		}
		return null;
	}
}
