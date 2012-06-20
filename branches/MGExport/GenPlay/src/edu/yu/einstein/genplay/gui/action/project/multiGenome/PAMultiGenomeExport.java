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
package edu.yu.einstein.genplay.gui.action.project.multiGenome;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.export.ExportEngineInterface;
import edu.yu.einstein.genplay.core.multiGenome.export.SingleFileExportEngine;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.editing.stripes.StripesData;
import edu.yu.einstein.genplay.gui.fileFilter.ExtendedFileFilter;
import edu.yu.einstein.genplay.gui.fileFilter.VCFFilter;
import edu.yu.einstein.genplay.gui.track.Track;
import edu.yu.einstein.genplay.gui.track.drawer.MultiGenomeDrawer;
import edu.yu.einstein.genplay.util.Utils;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class PAMultiGenomeExport extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION = 
		"Performs the multi genome algorithm"; 										// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Export as VCF";			// action name
	

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome Export";


	/**
	 * Creates an instance of {@link PAMultiGenomeExport}.
	 */
	public PAMultiGenomeExport() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Void processAction() throws Exception {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {
			// Get track information
			Track<?> track = getTrackList().getSelectedTrack();
			MultiGenomeDrawer genomeDrawer = track.getMultiGenomeDrawer();
			
			if (genomeDrawer.getStripesList().size() > 0) {
				
				// Get input parameters
				Map<String, List<VCFFile>> fileMap = getGenomeFileMap(genomeDrawer.getStripesList());
				Map<String, List<VariantType>> variationMap = getVariationMap(genomeDrawer.getStripesList());
				
				// Declare the export engine
				ExportEngineInterface exportEngine = null;
				
				// Initialize the engine if the export is about only one VCF file
				int fileNumber = getFileNumber(fileMap);
				if (fileNumber == 1) {
					exportEngine = new SingleFileExportEngine();
				} else if (fileNumber > 1) {
					JOptionPane.showMessageDialog(getRootPane(), "Cannot export data from more than one VCF.\nMore support coming soon.", "Export error", JOptionPane.INFORMATION_MESSAGE);
				} else {
					System.err.println("PAMultiGenomeExport.processAction(): Number of file required is " + fileNumber);
				}
				
				File file = getFile();
				
				// Runs the export process
				if (file != null && exportEngine != null) {
					// Notifies the action
					notifyActionStart(ACTION_NAME, 1, false);
					
					exportEngine.setFileMap(fileMap);
					exportEngine.setVariationMap(variationMap);
					exportEngine.setFilterList(genomeDrawer.getFiltersList());
					exportEngine.setPath(file.getPath());
					
					exportEngine.process();
				}
				
			} else {
				JOptionPane.showMessageDialog(getRootPane(), "The selected tracks does not contain any stripes,\nit cannot be exported as VCF file.", "Export error", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		return null;
	}


	@Override
	protected void doAtTheEnd(Void actionResult) {
		
	}
	
	
	/**
	 * @return a file to export the VCF
	 */
	private File getFile () {
		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		JFileChooser jfc = new JFileChooser(defaultDirectory);
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jfc.setDialogTitle("Export track as VCF");
		FileFilter[] filters = {new VCFFilter()};
		for (FileFilter currentFilter: filters) {
			jfc.addChoosableFileFilter(currentFilter);
		}
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setFileFilter(jfc.getChoosableFileFilters()[0]);
		int returnVal = jfc.showSaveDialog(getRootPane());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			ExtendedFileFilter selectedFilter = (ExtendedFileFilter)jfc.getFileFilter();
			File selectedFile = Utils.addExtension(jfc.getSelectedFile(), selectedFilter.getExtensions()[0]);
			if (!Utils.cancelBecauseFileExist(getRootPane(), selectedFile)) {
				return selectedFile;
			}
		}
		return null;
	}
	
	
	/**
	 * Retrieves a map between genome names and their list of variant type from a list of stripes.
	 * @param stripeList the list of stripes
	 * @return the map genome/file list
	 */
	private Map<String, List<VariantType>> getVariationMap (List<StripesData> stripeList) {
		Map<String, List<VariantType>> map = new HashMap<String, List<VariantType>>();
		for (StripesData stripe: stripeList) {
			String genome = stripe.getGenome();
			List<VariantType> variationList = stripe.getVariationTypeList();
			if (!map.containsKey(genome)) {
				map.put(genome, new ArrayList<VariantType>());
			}
			List<VariantType> currentList = map.get(genome);
			for (VariantType variantType: variationList) {
				if (!currentList.contains(variantType)) {
					currentList.add(variantType);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * Retrieves a map between genome names and their list of file from a list of stripes. 
	 * The selection takes into account what kind of variation a file can handle according to the genome.
	 * If insertions are required but the file contains deletion for the same genome, it won't be selected.
	 * @param stripeList the list of stripes
	 * @return the map genome/file list
	 */
	private Map<String, List<VCFFile>> getGenomeFileMap (List<StripesData> stripeList) {
		Map<String, List<VCFFile>> map = new HashMap<String, List<VCFFile>>();
		Map<String, List<VCFFile>> projectMap = ProjectManager.getInstance().getMultiGenomeProject().getGenomeFileAssociation();
		for (StripesData stripe: stripeList) {
			String genome = stripe.getGenome();
			if (!map.containsKey(genome)) {
				map.put(genome, new ArrayList<VCFFile>());
			}
			List<VCFFile> projectList = projectMap.get(genome);
			List<VCFFile> currentList = map.get(genome);
			for (VCFFile file: projectList) {
				if (!currentList.contains(file) && canManageRequirements(file, genome, stripe.getVariationTypeList())) {
					currentList.add(file);
				}
			}
		}
		return map;
	}
	
	
	/**
	 * Checks if a file contains data about the given genome for at least one of the given variant type.
	 * @param file			the VCF file
	 * @param genomeName	the genome name
	 * @param variantList	the variation list
	 * @return				true if the file contains information for that genome and those variation types, false otherwise
	 */
	private boolean canManageRequirements (VCFFile file, String genomeName, List<VariantType> variantList) {
		for (VariantType variantType: variantList) {
			if (file.canManage(genomeName, variantType)) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * @return the number of file involved into the export process
	 */
	private int getFileNumber (Map<String, List<VCFFile>> fileMap) {
		List<VCFFile> fileList = new ArrayList<VCFFile>();
		for (String genome: fileMap.keySet()) {
			List<VCFFile> projectList = fileMap.get(genome);
			for (VCFFile file: projectList) {
				if (!fileList.contains(file)) {
					fileList.add(file);
				}
			}
		}
		return fileList.size();
	}
	
}