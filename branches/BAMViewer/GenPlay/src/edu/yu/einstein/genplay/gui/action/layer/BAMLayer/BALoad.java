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
package edu.yu.einstein.genplay.gui.action.layer.BAMLayer;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.SAM.SAMContent;
import edu.yu.einstein.genplay.core.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.track.layer.BAMLayer;


/**
 * Loads data from a BAM file into GenPlay
 * @author Nicolas Fourel
 * @version 0.1
 */
public class BALoad extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 212223991804272305L;					// generated ID
	private static final String 	ACTION_NAME = "Load SAM/BAM data";					// action name
	private static final String 	DESCRIPTION = "Load SAM/BAM data into GenPlay";


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = BALoad.class.getName();

	private final BAMLayer layer;
	private final SAMContent samContent;
	private final GenomeWindow genomeWindow;


	/**
	 * Creates an instance of {@link BALoad}
	 * @param layer the {@link BAMLayer}
	 * @param samContent the {@link SAMContent} to load data in
	 * @param genomeWindow the {@link GenomeWindow} to know which data to load
	 */
	public BALoad(BAMLayer layer, SAMContent samContent, GenomeWindow genomeWindow) {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		this.layer = layer;
		this.samContent = samContent;
		this.genomeWindow = genomeWindow;
	}


	@Override
	protected Void processAction() throws Exception {
		samContent.load(genomeWindow.getChromosome(), genomeWindow.getStart(), genomeWindow.getStop());
		layer.getTrack().repaint();
		return null;
	}

}
