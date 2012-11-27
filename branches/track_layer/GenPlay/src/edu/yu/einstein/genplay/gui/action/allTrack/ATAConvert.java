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
package edu.yu.einstein.genplay.gui.action.allTrack;

import javax.swing.ActionMap;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.core.converter.ConverterFactory;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.enums.TrackType;
import edu.yu.einstein.genplay.core.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.core.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.core.list.binList.BinList;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.ConvertDialog;
import edu.yu.einstein.genplay.gui.old.track.BinListTrack;
import edu.yu.einstein.genplay.gui.old.track.EmptyTrack;
import edu.yu.einstein.genplay.gui.old.track.GeneListTrack;
import edu.yu.einstein.genplay.gui.old.track.SCWListTrack;
import edu.yu.einstein.genplay.gui.old.track.Track;
import edu.yu.einstein.genplay.util.colors.TrackColor;



/**
 * Converts the selected {@link Track} into another {@link Track}
 * @author Nicolas Fourel
 * @version 0.1
 */
public final class ATAConvert extends TrackListActionWorker<ChromosomeListOfLists<?>> {

	private static final long serialVersionUID = 4027173438789911860L; 	// generated ID
	private static final String 	ACTION_NAME = "Convert Track";// action name
	private static final String 	DESCRIPTION = "Convert the current track into another kind of track.";			// tooltip
	private Track<?> 				selectedTrack;					// The selected track.
	private Track<?>				resultTrack;					// The result track.
	private Converter				converter;						// The track converter.

	private ChromosomeListOfLists<?> data;
	private ChromosomeListOfLists<?> mask;
	private TrackType trackType;
	private String trackName;
	private int binSize;
	private DataPrecision precision;
	private ScoreCalculationMethod method;


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "ATAConvert";


	/**
	 * Creates an instance of {@link ATAConvert}
	 */
	public ATAConvert() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);

		resultTrack = null;
		data = null;
		mask = null;
		trackType = null;
		trackName = "";
		binSize = 0;
		precision = null;
		method = null;
	}


	@Override
	public ChromosomeListOfLists<?> processAction() {
		selectedTrack = getTrackList().getSelectedTrack();
		ConvertDialog dialog = new ConvertDialog(selectedTrack);
		if (dialog.showDialog(getRootPane()) == ConvertDialog.APPROVE_OPTION) {
			trackType = dialog.getOutputTrackType();
			if (trackType == TrackType.BIN) {
				binSize = dialog.getBinSize();
				precision = dialog.getDataPrecision();
				method = dialog.getScoreCalculationMethod();
			}
			resultTrack = dialog.getOutputTrack();
			trackName = dialog.getOutputTrackName();
			data = (ChromosomeListOfLists<?>) selectedTrack.getData();
			mask = selectedTrack.getMask();

			if (data != null) {
				converter = ConverterFactory.getConverter(data, null, trackType, binSize, precision, method);
			} else {
				if (mask != null) {
					converter = ConverterFactory.getConverter(null, mask, trackType, binSize, precision, method);
				}
			}
			if (converter != null) {
				try {
					converter.convert();
					return converter.getList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("No converter found");
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(ChromosomeListOfLists<?> actionResult) {
		if (actionResult != null) {
			int index = resultTrack.getTrackNumber() - 1;
			Track<?> newTrack = null;
			ScoredChromosomeWindowList mask = null;
			if (trackType == TrackType.GENE) {
				newTrack = new GeneListTrack(index, (GeneList) actionResult);
			} else if (trackType == TrackType.BIN) {
				newTrack = new BinListTrack(index + 1, (BinList) actionResult);
				((BinListTrack) newTrack).setTrackColor(TrackColor.getTrackColor());
			} else if (trackType == TrackType.SCW) {
				newTrack = new SCWListTrack(index + 1, (ScoredChromosomeWindowList)actionResult);
				((SCWListTrack) newTrack).setTrackColor(TrackColor.getTrackColor());
			} else if (trackType == TrackType.MASK) {
				newTrack = new EmptyTrack(index + 1);
				mask = (ScoredChromosomeWindowList)actionResult;
			}
			if (newTrack != null) {
				getTrackList().setTrack(index, newTrack, ProjectManager.getInstance().getProjectConfiguration().getTrackHeight(), trackName, mask, null, null);
			} else {
				System.err.println("The track could not be converted");
			}
		}
	}

}
