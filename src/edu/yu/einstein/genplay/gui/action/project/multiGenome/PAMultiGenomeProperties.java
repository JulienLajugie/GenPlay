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
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.ActionMap;
import javax.swing.KeyStroke;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFilter;
import edu.yu.einstein.genplay.gui.MGDisplaySettings.MGDisplaySettings;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.multiGenomeDialog.properties.PropertiesDialog;
import edu.yu.einstein.genplay.util.Utils;


/**
 * Displays the multi genome project properties dialog
 * 
 * @author Nicolas Fourel
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PAMultiGenomeProperties extends TrackListActionWorker<Boolean> {

	private static final 	long serialVersionUID = -6475180772964541278L; 			// generated ID
	private static final 	String ACTION_NAME = "Multi Genome Properties";			// action name
	private static final 	String DESCRIPTION = "Shows the project properties"; 	// tooltip
	private static final 	int 	MNEMONIC = KeyEvent.VK_P; 						// mnemonic key

	private 				PropertiesDialog 	dialog;								// the dialog properties
	private					MGDisplaySettings 	settings;							// the multi genome settings object shortcut


	/**
	 * action accelerator {@link KeyStroke}
	 */
	public static final KeyStroke 	ACCELERATOR = KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK); 


	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = "PAMultiGenomeProperties";


	/**
	 * Creates an instance of {@link PAMultiGenomeProperties}
	 */
	public PAMultiGenomeProperties() {
		super();
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
		putValue(MNEMONIC_KEY, MNEMONIC);
		settings = MGDisplaySettings.getInstance();
	}


	/**
	 * Shows the Multi Genome Project Properties dialog
	 */
	/*@Override
	public void actionPerformed(ActionEvent arg0) {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {		// if it is a multi genome project
			if (dialog == null){										// and the dialog has not been created,
				dialog = new PropertiesDialog();						// we create it
			}
			dialog.setSettings(settings);								// and set it with the current settings
			if (dialog.showDialog(getRootPane(), PropertiesDialog.GENERAL) == PropertiesDialog.APPROVE_OPTION) {	// we show it waiting to be approved
				DoAtTheEndThread thread = new DoAtTheEndThread();		// if it is approved (OK button), we create the thread running the content of a DoAtTheEnd-like method
				thread.start();											// we start the thread
			}
		}
	}*/

	
	@Override
	protected Boolean processAction() throws Exception {
		if (ProjectManager.getInstance().isMultiGenomeProject()) {		// if it is a multi genome project
			if (dialog == null){										// and the dialog has not been created,
				dialog = new PropertiesDialog();						// we create it
			}
			dialog.setSettings(settings);								// and set it with the current settings
			if (dialog.showDialog(getRootPane(), PropertiesDialog.GENERAL) == PropertiesDialog.APPROVE_OPTION) {	// we show it waiting to be approved
				// Notifies the action
				notifyActionStart("Checking for updates", 1, false);
				CountDownLatch propertiesLatch = new CountDownLatch(1);	// one for the SNP action
				
				DoAtTheEndThread thread = new DoAtTheEndThread();		// if it is approved (OK button), we create the thread running the content of a DoAtTheEnd-like method
				thread.setLatch(propertiesLatch);
				thread.start();											// we start the thread
				
				// The current thread is waiting for the SNP thread to finish
				try {
					propertiesLatch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}


	/////////////////////////////////////////////////////////////////////// DoAtTheEndThread class

	/**
	 * Thread running the content of the DoAtTheEnd-like method.
	 * That method is running two other actions {@link PAMultiGenomeSNP} and {@link PAMultiGenomeFilters}
	 * supposed to be ran one by one, consecutively. One waiting for the other to stop in order to start.
	 * These two actions are ran into separated thread.
	 * The {@link DoAtTheEndThread} handle these waiting breaks but must not stop the main thread of the application.
	 * That is why the content of the DoAtTheEnd-like method is executed into a new thread.
	 * These thread synchronization is handled with {@link CountDownLatch} methods.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class DoAtTheEndThread extends Thread {

		CountDownLatch latch = null;	// CountDownLatch object for synchronizing threads
		
		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}
		
		@Override
		public void run() {

			// Set the various settings
			settings.getVariousSettings().setVariousSettings(dialog.getTransparency(), dialog.isShowLegend());

			// Set the filters
			List<VCFFilter> previousFilterList = settings.getFilterSettings().getAllVCFFilters();
			settings.getFilterSettings().setFiltersSettings(dialog.getFiltersData());

			// Set the stripes
			settings.getStripeSettings().setStripesSettings(dialog.getStripesData());

			// Set the static options
			MGDisplaySettings.DRAW_INSERTION_EDGE = dialog.getOptionValueList().get(0);
			MGDisplaySettings.DRAW_DELETION_EDGE = dialog.getOptionValueList().get(1);
			MGDisplaySettings.DRAW_INSERTION_LETTERS = dialog.getOptionValueList().get(2);
			MGDisplaySettings.DRAW_DELETION_LETTERS = dialog.getOptionValueList().get(3);
			MGDisplaySettings.DRAW_SNP_LETTERS = dialog.getOptionValueList().get(4);

			// Create the two CountDownLatch,
			CountDownLatch SNPLatch = new CountDownLatch(1);		// one for the SNP action
			CountDownLatch filterLatch = new CountDownLatch(1);		// one for the filter action

			// Create and start the SNP thread
			SNPThread snpThread = new SNPThread();
			snpThread.setLatch(SNPLatch);
			snpThread.start();

			// The current thread is waiting for the SNP thread to finish
			try {
				SNPLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Create and start the filter thread
			FilterThread filterThread = new FilterThread();
			filterThread.setPreviousFilterList(previousFilterList);
			filterThread.setLatch(filterLatch);
			filterThread.start();

			// The current thread is waiting for the filter thread to finish
			try {
				filterLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Many data has been loaded, removed, garbage collecting free some memory 
			Utils.garbageCollect();


			// Update tracks
			/*Track<?>[] tracks = getTrackList().getTrackList();
			for (Track<?> track: tracks) {
				List<VCFFilter> filtersList = settings.getFilterSettings().getVCFFiltersForTrack(track);
				List<StripesData> stripesList = settings.getStripeSettings().getStripesForTrack(track);
				track.updateMultiGenomeInformation(stripesList, filtersList);
			}*/
			
			CountDownLatch trackLatch = new CountDownLatch(1);		// one for the track update action
			TrackThread trackThread = new TrackThread();
			trackThread.setLatch(trackLatch);
			trackThread.start();
			
			// The current thread is waiting for the filter thread to finish
			try {
				trackLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			latch.countDown();
		}
	}



	/////////////////////////////////////////////////////////////////////// SNPThread class

	/**
	 * The SNP thread class.
	 * This class starts the SNP action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class SNPThread extends Thread {

		CountDownLatch latch = null;	// CountDownLatch object for synchronizing threads

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			PAMultiGenomeSNP multiGenomeSNP = new PAMultiGenomeSNP();
			multiGenomeSNP.setLatch(latch);
			multiGenomeSNP.actionPerformed(null);
		}
	}



	/////////////////////////////////////////////////////////////////////// FilterThread class

	/**
	 * The filter thread class.
	 * This class starts the filter action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class FilterThread extends Thread {

		CountDownLatch latch = null;					// CountDownLatch object for synchronizing threads
		List<VCFFilter> previousFilterList = null;		// the list of previous filters

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		/**
		 * @param previousFilterList the previousFilterList to set
		 */
		public void setPreviousFilterList(List<VCFFilter> previousFilterList) {
			this.previousFilterList = previousFilterList;
		}

		@Override
		public void run() {
			PAMultiGenomeFilters multigenomeFilters = new PAMultiGenomeFilters();
			multigenomeFilters.setPreviousFilterList(previousFilterList);
			multigenomeFilters.setLatch(latch);
			multigenomeFilters.actionPerformed(null);
			previousFilterList = null;
		}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////// TrackThread class

	/**
	 * The track update thread class.
	 * This class starts the Track Update action.
	 * 
	 * @author Nicolas Fourel
	 * @version 0.1
	 */
	private class TrackThread extends Thread {

		CountDownLatch latch = null;	// CountDownLatch object for synchronizing threads

		/**
		 * @param latch the latch to set
		 */
		public void setLatch(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void run() {
			PAUpdateTrack updateTrackThread = new PAUpdateTrack();
			updateTrackThread.setLatch(latch);
			updateTrackThread.actionPerformed(null);
		}
	}

}