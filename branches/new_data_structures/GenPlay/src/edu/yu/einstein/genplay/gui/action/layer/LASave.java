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
package edu.yu.einstein.genplay.gui.action.layer;

import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.IO.writer.Writer;
import edu.yu.einstein.genplay.core.IO.writer.WriterFactory;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.GenomicDataList;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackGenomeSelection.GenomeSelectionDialog;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.statusBar.Stoppable;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.LayerType;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Saves the selected {@link Layer}
 * @author Julien Lajugie
 * @version 0.1
 */
public class LASave extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 212223991804272305L;	// generated ID
	private static final String 	ACTION_NAME = "Save As";			// action name
	private static final String 	DESCRIPTION =
			"Save the selected Layer";
	private Writer 					writer;								// object that writes the data

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = LASave.class.getName();


	/**
	 * Creates an instance of {@link LASave}
	 */
	public LASave() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected Void processAction() throws Exception {
		if (getTrackListPanel().getSelectedTrack() != null) {
			Layer<?> selectedLayer = (Layer<?>) getValue("Layer");
			String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
			JFileChooser jfc = new JFileChooser(defaultDirectory);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setDialogTitle("Save Layer");
			FileFilter[] filters = null;
			if (selectedLayer.getType() == LayerType.BIN_LAYER) {
				filters = Utils.getWritableBinListFileFilters();
			} else if (selectedLayer.getType() == LayerType.GENE_LAYER) {
				filters = Utils.getWritableGeneFileFilters();
			} else if ((selectedLayer.getType() == LayerType.SCW_LAYER) || (selectedLayer.getType() == LayerType.MASK_LAYER)) {
				filters = Utils.getWritableSCWFileFilter();
			} else {
				// case where we don't know how to save the type of the selected track
				return null;
			}
			for (FileFilter currentFilter: filters) {
				jfc.addChoosableFileFilter(currentFilter);
			}
			jfc.setAcceptAllFileFilterUsed(false);
			jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
			jfc.setSelectedFile(new File(selectedLayer.getName()));
			int returnVal = jfc.showSaveDialog(getRootPane());
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
				File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
				if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
					boolean isValid = true;
					GenomicDataList<?> data = (GenomicDataList<?>) selectedLayer.getData();
					String name = selectedLayer.getName();
					writer = WriterFactory.getWriter(selectedFile, data, name, selectedFilter);
					if (ProjectManager.getInstance().isMultiGenomeProject()) {
						GenomeSelectionDialog dialog = new GenomeSelectionDialog();
						if (dialog.showDialog(getRootPane()) == GenomeSelectionDialog.APPROVE_OPTION) {
							writer.setMultiGenomeCoordinateSystem(dialog.getGenomeName(), dialog.getAlleleType());
						} else {
							isValid = false;
						}
					}
					if (isValid) {
						notifyActionStart("Saving Layer " + name, 1, writer instanceof Stoppable);
						writer.write();
					}
				}
			}
		}
		return null;
	}


	@Override
	public void stop() {
		if ((writer != null) && (writer instanceof Stoppable)) {
			((Stoppable)writer).stop();
		}
		super.stop();
	}
}
