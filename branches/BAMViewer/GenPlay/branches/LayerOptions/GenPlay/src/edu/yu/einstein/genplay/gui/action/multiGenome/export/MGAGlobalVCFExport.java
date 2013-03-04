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
package edu.yu.einstein.genplay.gui.action.multiGenome.export;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.JOptionPane;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGABGZIPCompression;
import edu.yu.einstein.genplay.gui.action.multiGenome.VCFAction.MGATBIIndex;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.ExportSettings;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.export.ExportVCFDialog;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.trackAction.mainDialog.MultiGenomeTrackActionDialog;
import edu.yu.einstein.genplay.gui.track.layer.variantLayer.VariantLayer;


/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGAGlobalVCFExport extends TrackListActionWorker<Boolean> {

	private static final long serialVersionUID = 6498078428524511709L;	// generated ID
	private static final String 	DESCRIPTION =
			"Performs the multi genome export function"; 							// tooltip
	private static final int 				MNEMONIC = KeyEvent.VK_M; 				// mnemonic key
	private static		 String 			ACTION_NAME = "Export as VCF";	// action name


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "Multi Genome Export";

	private ExportVCFDialog dialog;
	private ExportSettings settings;
	private File outputFile;
	private boolean hasBeenCancelled = false;

	private MGAVCFExport 			exportVCFAction;
	private MGABGZIPCompression 	compressionAction;
	private MGATBIIndex 			indexAction;

	/**
	 * Creates an instance of {@link MGAGlobalVCFExport}.
	 */
	public MGAGlobalVCFExport() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		exportVCFAction = null;
		compressionAction = null;
		indexAction = null;
	}


	@Override
	protected Boolean processAction() {
		ProjectManager projectManager = ProjectManager.getInstance();
		if (projectManager.isMultiGenomeProject()) {

			// Get layer information
			VariantLayer selectedLayer = (VariantLayer) getValue("Layer");

			// Create the export settings
			settings = new ExportSettings(selectedLayer);

			// Create the dialog
			dialog = new ExportVCFDialog(settings, selectedLayer);

			// Show the dialog
			if (dialog.showDialog(null) == MultiGenomeTrackActionDialog.APPROVE_OPTION) {
				outputFile = new File(dialog.getVCFPath());

				// Create the VCF thread
				ExportVCFThread vcfThread = new ExportVCFThread();
				CountDownLatch vcfLatch = new CountDownLatch(1);
				vcfThread.setLatch(vcfLatch);
				vcfThread.start();

				// The current thread is waiting for the VCF thread to finish
				try {
					vcfLatch.await();
				} catch (InterruptedException e) {
					ExceptionManager.getInstance().caughtException(e);
				}

				// Compress the VCF
				if (dialog.compressVCF() && exportVCFAction.hasBeenDone()) {
					// Create the compress thread
					CompressThread compressThread = new CompressThread();
					CountDownLatch compressLatch = new CountDownLatch(1);
					compressThread.setLatch(compressLatch);
					compressThread.start();

					// The current thread is waiting for the compress thread to finish
					try {
						compressLatch.await();
					} catch (InterruptedException e) {
						ExceptionManager.getInstance().caughtException(e);
					}

					// Index the BGZIP
					if (dialog.indexVCF() && compressionAction.hasBeenDone()) {
						// Create the index thread
						IndexThread indexThread = new IndexThread();
						CountDownLatch indexLatch = new CountDownLatch(1);
						indexThread.setLatch(indexLatch);
						indexThread.start();
						// The current thread is waiting for the index thread to finish
						try {
							indexLatch.await();
						} catch (InterruptedException e) {
							ExceptionManager.getInstance().caughtException(e);
						}
					}
				}
				return true;
			} else {
				hasBeenCancelled = true;
			}
		}
		return false;
	}


	@Override
	protected void doAtTheEnd(Boolean actionResult) {
		if (!hasBeenCancelled && actionResult) {
			String description = getMessageDescription();
			JOptionPane.showMessageDialog(getRootPane(), description, "Export report", JOptionPane.INFORMATION_MESSAGE);
		}

	}


	/**
	 * @return a description to sum up the result of the operation
	 */
	private String getMessageDescription () {
		String result = "";
		String action = "";
		String files = "";

		if (dialog != null) {

			if (exportVCFAction != null) {
				action += "Export as VCF: ";
				if (exportVCFAction.hasBeenDone()) {
					action += "success.";
					files += outputFile.getName();
				} else {
					action += "error.";
					files += "no VCF file";
				}

				if (compressionAction != null) {
					action += "\nCompress in BGZIP: ";
					if (compressionAction.hasBeenDone()) {
						action += "success.";
						files += "\n" + compressionAction.getCompressedFile().getName();
					} else {
						action += "error.";
						files += "\nno BGZIP file";
					}

					if (indexAction != null) {
						action += "\nIndex with Tabix: ";
						if (indexAction.hasBeenDone()) {
							action += "success.";
							files += "\n" + indexAction.getIndexedFile().getName();
						} else {
							action += "error.";
							files += "\nno Tabix file";
						}

					}
				}
			}

			result = "Operation:\n" + action + "\nGenerated files:\n" + files;
		}
		return result;
	}


	/////////////////////////////////////////////////////////////////////// ExportVCFThread class

	/**
	 * The export VCF thread class.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class ExportVCFThread extends Thread {

		/**
		 * Constructor of the {@link ExportVCFThread}
		 */
		public ExportVCFThread () {
			exportVCFAction = new MGAVCFExport(outputFile, settings);
		}

		@Override
		public void run() {
			exportVCFAction.actionPerformed(null);
		}

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			exportVCFAction.setLatch(latch);
		}
	}


	/////////////////////////////////////////////////////////////////////// CompressThread class

	/**
	 * The compress VCF thread class.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class CompressThread extends Thread {

		/**
		 * Constructor of the {@link CompressThread}
		 */
		public CompressThread () {
			compressionAction = new MGABGZIPCompression(outputFile);
		}

		@Override
		public void run() {
			compressionAction.actionPerformed(null);
		}

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			compressionAction.setLatch(latch);
		}
	}


	/////////////////////////////////////////////////////////////////////// IndexThread class

	/**
	 * The index BGZIP thread class.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class IndexThread extends Thread {

		/**
		 * Constructor of the {@link IndexThread}
		 */
		public IndexThread () {
			File bgzipFile = compressionAction.getCompressedFile();
			indexAction = new MGATBIIndex(bgzipFile);
		}

		@Override
		public void run() {
			indexAction.actionPerformed(null);
		}

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			indexAction.setLatch(latch);
		}
	}

}