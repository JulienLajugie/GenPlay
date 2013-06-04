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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.gui.mainFrame.MainFrame;
import edu.yu.einstein.genplay.gui.track.layer.AbstractSCWLayer;
import edu.yu.einstein.genplay.gui.track.layer.GeneLayer;
import edu.yu.einstein.genplay.gui.track.layer.Layer;
import edu.yu.einstein.genplay.gui.track.layer.MaskLayer;
import edu.yu.einstein.genplay.gui.track.layer.NucleotideLayer;
import edu.yu.einstein.genplay.gui.track.layer.RepeatLayer;


/**
 * Associates data scalers objects to layers and their data.
 * Makes sure that if many layers display the same data they share a unique data scaler
 * that scale the data.
 * Allows the data scalers to notify their associated layers that their data has been scaled
 * and are ready to be printed.
 * @author Julien Lajugie
 */
public class DataScalerManager {

	/**
	 * Threads that cleans the map of data scalers associated to the data to scale
	 * and the map of layers associated to the data scalers that scales their data.
	 * Remove the elements that don't need to be scaled anymore because they are not displayed
	 * in order to avoid memory leaks.
	 * @author Julien Lajugie
	 */
	private class DataScalerManagerCleaner extends Thread {

		/** Interval between two cleanings in seconds */
		private final static int CLEANING_INTERVALS = 10;

		/**
		 * Cleans the map that associates data keys to data scaler values that scale their associated keys.
		 */
		private void cleanDataScalerMap() {
			List<Layer<?>> displayedLayers = MainFrame.getInstance().getTrackListPanel().getModel().getAllLayers(); // retrieve the list of all the layers displayed
			Map<Object, Boolean> isDataDisplayedMap = new HashMap<Object, Boolean>(); // map with data as key and isDisplayed as value
			for (Object currentData: dataScalerMap.keySet()) {
				isDataDisplayedMap.put(currentData, false);
			}
			// set to true the is displayed value of data of displayed layer
			for (Layer<?> currentLayer: displayedLayers) {
				isDataDisplayedMap.put(currentLayer.getData(), true);
			}
			// remove data scalers that are not displayed
			Iterator<Entry<Object, DataScalerForTrackDisplay<?, ?>>> dataScalerMapEntryIterator = dataScalerMap.entrySet().iterator();
			while (dataScalerMapEntryIterator.hasNext()) {
				Entry<Object, DataScalerForTrackDisplay<?, ?>> currentEntry = dataScalerMapEntryIterator.next();
				if (!isDataDisplayedMap.get(currentEntry.getKey())) {
					dataScalerMapEntryIterator.remove();
				}
			}
		}


		/**
		 * Cleans the map that associates data scaler keys to sets of layers that use their keys to scale their data
		 */
		private void cleanLayerListMap() {
			Iterator<Entry<DataScalerForTrackDisplay<?, ?>, Set<Layer<?>>>> layerListMapIterator = layerListMap.entrySet().iterator();
			while(layerListMapIterator.hasNext()) {
				Entry<DataScalerForTrackDisplay<?, ?>, Set<Layer<?>>> currentLayerListMapEntry = layerListMapIterator.next();
				Iterator<Layer<?>> layerSetIterator = currentLayerListMapEntry.getValue().iterator();
				while (layerSetIterator.hasNext()) {
					Layer<?> currentLayer = layerSetIterator.next();
					DataScalerForTrackDisplay<?, ?> layerScaler = dataScalerMap.get(currentLayer.getData());
					if ((layerScaler == null) || (layerScaler != currentLayerListMapEntry.getKey())) {
						layerSetIterator.remove();
					}
				}
				if (currentLayerListMapEntry.getValue().isEmpty()) {
					layerListMapIterator.remove();
				}
			}
		}


		@Override
		public void run() {
			Thread thisThread = Thread.currentThread();
			setName("Data Scaler Cleaner Thread");
			while (DataScalerManager.getInstance().cleanerThread == thisThread) {
				cleanDataScalerMap();
				cleanLayerListMap();
				try {
					sleep(CLEANING_INTERVALS * 1000);
				} catch (InterruptedException e) {
					ExceptionManager.getInstance().caughtException(e);
				}
			}
		}
	}

	/** Instance of the {@link DataScalerManager} singleton */
	private static DataScalerManager instance = null;


	/**
	 * @return the instance of the {@link DataScalerManager} singleton
	 */
	public synchronized static DataScalerManager getInstance() {
		if (instance == null) {
			// we synchronize to make sure that there is no 2 instances created
			synchronized(DataScalerManager.class) {
				if (instance == null) {
					instance = new DataScalerManager();
				}
			}
		}
		return instance;
	}

	/** Thread that cleans the maps */
	private final DataScalerManagerCleaner cleanerThread;

	/** Map that associates data scaler keys to sets of layers values that use their keys to scale their data */
	private final Map<DataScalerForTrackDisplay<?, ?>, Set<Layer<?>>> layerListMap;

	/** Map that associates data keys to data scaler values that scale their associated keys */
	private final Map<Object, DataScalerForTrackDisplay<?, ?>> dataScalerMap; // map with data as key and data scalers as values


	/**
	 * Creates an instance of {@link DataScalerManager}
	 */
	private DataScalerManager() {
		layerListMap = new ConcurrentHashMap<DataScalerForTrackDisplay<?,?>, Set<Layer<?>>>();
		dataScalerMap = new ConcurrentHashMap<Object, DataScalerForTrackDisplay<?,?>>();
		cleanerThread = new DataScalerManagerCleaner();
		cleanerThread.start();
	}



	/**
	 * @param layer
	 * @return the data of the specified layer scaled for display
	 */
	@SuppressWarnings("unchecked")
	public ListView<ScoredChromosomeWindow> getScaledData(AbstractSCWLayer<?> layer) {
		return (ListView<ScoredChromosomeWindow>) retrieveDataScaler(layer).getDataScaledForTrackDisplay();
	}


	/**
	 * @param layer
	 * @return the data of the specified layer scaled for display
	 */
	@SuppressWarnings("unchecked")
	public List<ListView<Gene>> getScaledData(GeneLayer layer) {
		return (List<ListView<Gene>>) retrieveDataScaler(layer).getDataScaledForTrackDisplay();
	}


	/**
	 * @param layer
	 * @return the data of the specified layer scaled for display
	 */
	@SuppressWarnings("unchecked")
	public ListView<ScoredChromosomeWindow> getScaledData(MaskLayer layer) {
		return (ListView<ScoredChromosomeWindow>) retrieveDataScaler(layer).getDataScaledForTrackDisplay();
	}


	/**
	 * @param layer
	 * @return the data of the specified layer scaled for display
	 */
	public Nucleotide[] getScaledData(NucleotideLayer layer) {
		return (Nucleotide[]) retrieveDataScaler(layer).getDataScaledForTrackDisplay();
	}


	/**
	 * @param layer
	 * @return the data of the specified layer scaled for display
	 */
	@SuppressWarnings("unchecked")
	public List<RepeatFamilyListView> getScaledData(RepeatLayer layer) {
		return (List<RepeatFamilyListView>) retrieveDataScaler(layer).getDataScaledForTrackDisplay();
	}


	/**
	 * Redraws the layers that displays data scaled by the specified data scaler
	 * @param dataScaler a {@link DataScalerForTrackDisplay}
	 */
	void redrawLayers(DataScalerForTrackDisplay<?, ?> dataScaler) {
		Set<Layer<?>> layersToRedraw = layerListMap.get(dataScaler);
		for (Layer<?> currentLayer: layersToRedraw) {
			currentLayer.getTrack().repaint();
		}
	}


	/**
	 * Register a layer to the map of layer sets and register and map a data scaler to the data it scales
	 * @param layer
	 */
	private void registerLayer(Layer<?> layer) {
		if (!dataScalerMap.containsValue(layer.getData())) {
			DataScalerForTrackDisplay<?, ?> dataScaler = DataScalerFactory.createDataScaler(layer);
			dataScalerMap.put(layer.getData(), dataScaler);
			Set<Layer<?>> layerList = new HashSet<Layer<?>>();
			layerList.add(layer);
			layerListMap.put(dataScaler, layerList);
		}
	}


	/**
	 * @param layer a {@link Layer}
	 * @return the {@link DataScalerForTrackDisplay} object that scales the data of the
	 * specified layer. Creates it if it doesn't exist.
	 */
	private DataScalerForTrackDisplay<?, ?> retrieveDataScaler(Layer<?> layer) {
		DataScalerForTrackDisplay<?, ?> dataScaler = dataScalerMap.get(layer.getData());
		if (dataScaler == null) {
			registerLayer(layer);
			dataScaler = dataScalerMap.get(layer.getData());
		}
		layerListMap.get(dataScaler).add(layer); // add the layer to the list associated with the data scaler if not present
		return dataScaler;
	}
}
