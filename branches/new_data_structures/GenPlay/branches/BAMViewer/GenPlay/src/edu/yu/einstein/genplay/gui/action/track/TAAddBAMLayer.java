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
package edu.yu.einstein.genplay.gui.action.track;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.SAM.SAMFile;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListAction;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.layer.BAMLayer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Loads a layer from data retrieve from a DAS server
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class TAAddBAMLayer extends TrackListAction {

	private static final long serialVersionUID = 5229478480046927796L;
	private static final String ACTION_NAME = "Add BAM Layer"; 										// action name
	private static final String DESCRIPTION = "Add a layer to display BAM files"; 		// tooltip


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "TAAddBAMLayer";


	/**
	 * Creates an instance of {@link TAAddBAMLayer}
	 */
	public TAAddBAMLayer() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		final Track selectedTrack = getTrackListPanel().getSelectedTrack();
		if (selectedTrack != null) {
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			File selectedFile = Utils.chooseFileToLoad(getRootPane(), "Load BAM Layer", defaultDirectory, Utils.getReadableSAMFileFilters(), true);
			if (selectedFile != null) {
				SAMFile samFile = new SAMFile(selectedFile);
				BAMLayer bamLayer = new BAMLayer(selectedTrack, samFile, selectedFile.getName());
				selectedTrack.getLayers().add(bamLayer);
				selectedTrack.setActiveLayer(bamLayer);
			}
		}
	}

}
