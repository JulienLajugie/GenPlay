/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.ScoredChromosomeWindow;
import yu.einstein.gdp2.core.enums.DataPrecision;
import yu.einstein.gdp2.core.enums.ScoreCalculationMethod;
import yu.einstein.gdp2.core.list.ChromosomeListOfLists;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;
import yu.einstein.gdp2.util.ChromosomeManager;
import yu.einstein.gdp2.util.DoubleLists;


/**
 * The BinList class provides a representation of a list of genome positions grouped by bins.
 * A score is associated to every bin.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BinList extends DisplayableListOfLists<Double, double[]> implements Serializable {

	private static final long serialVersionUID = -6114967730638134020L; // generated ID

	private final int 				binSize;		// size of the bins
	private final DataPrecision 	precision;		// precision of the data
	private int 					fittedBinSize;	// size of the bins of the fitted data


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision) {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param positions list of positions
	 * @param scores list of score
	 * @throws IllegalArgumentException thrown if precision is not valid
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ChromosomeListOfLists<Integer> positions, ChromosomeListOfLists<Double> scores) throws IllegalArgumentException {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
		for(Chromosome currentChromosome : chromosomeManager)  {			
			if ((positions.get(currentChromosome) == null) || (positions.size(currentChromosome) == 0)) {
				this.add(null);
			} else {
				int currentSize = currentChromosome.getLength() / binSize + 1;
				List<Double> listToAdd = ListFactory.createList(precision, currentSize); 
				this.add(listToAdd);
				for (int i = 0; i < positions.size(currentChromosome); i++) {
					if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
						int currentWindow = positions.get(currentChromosome, i) / binSize;
						this.set(currentChromosome, currentWindow, scores.get(currentChromosome, i));
					}
				}
			}
		}
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param method method of the score calculation
	 * @param starts list of start positions
	 * @param stops list of stop positions
	 * @param scores list of scores
	 * @throws IllegalArgumentException thrown if precision is not valid
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ScoreCalculationMethod method, ChromosomeListOfLists<Integer> starts, ChromosomeListOfLists<Integer> stops, ChromosomeListOfLists<Double> scores) throws IllegalArgumentException {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
		for(Chromosome currentChromosome : chromosomeManager)  {
			if ((starts.get(currentChromosome) == null) || (starts.size(currentChromosome) == 0)) {
				this.add(null);
			} else {
				int currentSize = currentChromosome.getLength() / binSize + 1;
				List<Double> listToAdd = ListFactory.createList(precision, currentSize); 
				this.add(listToAdd);
				int k = 0;
				int previousStop = 0;
				for (int j = 0; j < size(currentChromosome); j++) {
					k = previousStop;
					List<Double> currentBinIntensities = new ArrayList<Double>();
					while ((k < stops.size(currentChromosome)) && (stops.get(currentChromosome, k) <= j * binSize)) {
						k++;
					}
					previousStop = k;
					while ((k < starts.size(currentChromosome)) && (starts.get(currentChromosome, k) < j * binSize)) {
						if (stops.get(currentChromosome, k) > (j * binSize)) {
							if (method == ScoreCalculationMethod.SUM) {
								int stop = Math.min((j + 1) * binSize, stops.get(currentChromosome, k));
								double intensity = scores.get(currentChromosome, k) * (stop - (j * binSize)) / (stops.get(currentChromosome, k) - starts.get(currentChromosome, k));
								currentBinIntensities.add(intensity);							
							} else {
								double intensity = scores.get(currentChromosome, k);
								currentBinIntensities.add(intensity);
							}
						}
						k++;
					} 						
					while ((k < starts.size(currentChromosome)) && (starts.get(currentChromosome, k) < (j + 1) * binSize)) {
						if ((stops.get(currentChromosome, k) > (j + 1) * binSize)) {
							if (method == ScoreCalculationMethod.SUM) {
								int start = Math.max(j * binSize, starts.get(currentChromosome, k));
								double intensity = scores.get(currentChromosome, k) * (((j + 1) * binSize) - start) / (stops.get(currentChromosome, k) - starts.get(currentChromosome, k));
								currentBinIntensities.add(intensity);
							} else {
								double intensity = scores.get(currentChromosome, k);
								currentBinIntensities.add(intensity);
							}																
						} else {
							if (stops.get(currentChromosome, k) > (j * binSize)) {
								double intensity = scores.get(currentChromosome, k);
								currentBinIntensities.add(intensity);
								previousStop = k;
							}
						}
						k++;
					}

					if (currentBinIntensities.size() == 0) {
						set(currentChromosome, j, 0d);
					} else {
						switch (method) {
						case AVERAGE: 
							set(currentChromosome, j, DoubleLists.average(currentBinIntensities));
							break;
						case MAXIMUM:
							set(currentChromosome, j, Collections.max(currentBinIntensities));
							break;
						case SUM:
							set(currentChromosome, j, DoubleLists.sum(currentBinIntensities));
							break;
						default:
							throw new IllegalArgumentException("Invalid method");
						}
					}
				}
			}
		}
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param method method of the score calculation
	 * @param list list of {@link ScoredChromosomeWindow}
	 * @throws IllegalArgumentException thrown if precision is not valid
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ScoreCalculationMethod method, ChromosomeListOfLists<ScoredChromosomeWindow> list)  throws IllegalArgumentException {
		super(chromosomeManager);
		this.binSize = binSize;	
		this.precision = precision;
		for(Chromosome currentChromosome : chromosomeManager)  {
			if ((list.get(currentChromosome) == null) || (list.size(currentChromosome) == 0)) {
				this.add(null);
			} else {
				int currentSize = currentChromosome.getLength() / binSize + 1;
				List<Double> listToAdd = ListFactory.createList(precision, currentSize); 
				this.add(listToAdd);
				int k = 0;
				int previousStop = 0;
				for (int j = 0; j < size(currentChromosome); j++) {
					k = previousStop;
					ArrayList<Double> currentBinIntensities = new ArrayList<Double>();
					while ((k < list.size(currentChromosome)) && (list.get(currentChromosome, k).getStop() <= j * binSize)) {
						k++;
					}
					previousStop = k;
					while ((k < list.size(currentChromosome)) && (list.get(currentChromosome, k).getStart() < j * binSize)) {
						if (method == ScoreCalculationMethod.SUM) {
							int stop = Math.min((j + 1) * binSize, list.get(currentChromosome, k).getStop());
							double intensity = list.get(currentChromosome, k).getScore() * (stop - (j * binSize)) / (list.get(currentChromosome, k).getStop() - list.get(currentChromosome, k).getStart());
							currentBinIntensities.add(intensity);							
						} else {
							double intensity = list.get(currentChromosome, k).getScore();
							currentBinIntensities.add(intensity);
						}
						k++;
					} 						
					while ((k < list.size(currentChromosome)) && (list.get(currentChromosome, k).getStart() < (j + 1) * binSize)) {
						if ((list.get(currentChromosome, k).getStop() > (j + 1) * binSize)) {
							if (method == ScoreCalculationMethod.SUM) {
								int start = Math.max(j * binSize, list.get(currentChromosome, k).getStart());
								double intensity = list.get(currentChromosome, k).getScore() * (((j + 1) * binSize) - start) / (list.get(currentChromosome, k).getStop() - list.get(currentChromosome, k).getStart());
								currentBinIntensities.add(intensity);
							} else {
								double intensity = list.get(currentChromosome, k).getScore();
								currentBinIntensities.add(intensity);
							}																
						} else {
							double intensity = list.get(currentChromosome, k).getScore();
							currentBinIntensities.add(intensity);
							previousStop = k;
						}
						k++;
					}

					if (currentBinIntensities.size() == 0) {
						set(currentChromosome, j, 0d);
					} else {
						switch (method) {
						case AVERAGE: 
							set(currentChromosome, j, DoubleLists.average(currentBinIntensities));
							break;
						case MAXIMUM:
							set(currentChromosome, j, Collections.max(currentBinIntensities));
							break;
						case SUM:
							set(currentChromosome, j, DoubleLists.sum(currentBinIntensities));
							break;
						default:
							throw new IllegalArgumentException("Invalid method");
						}
					}
				}
			}
		}
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param method method of the score calculation
	 * @param positions list of positions
	 * @param scores list of scores
	 * @throws IllegalArgumentException
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ScoreCalculationMethod method, ChromosomeListOfLists<Integer> positions, ChromosomeListOfLists<Double> scores) throws IllegalArgumentException {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
		for(Chromosome currentChromosome : chromosomeManager)  {
			if ((positions.get(currentChromosome) == null) || (positions.size(currentChromosome) == 0)) {
				this.add(null);
			} else {
				int currentSize = currentChromosome.getLength() / binSize + 1;
				List<Double> listToAdd = ListFactory.createList(precision, currentSize); 
				this.add(listToAdd);
			}
		}
		switch (method) {
		case AVERAGE:
			averageMethod(positions, scores);
			break;
		case MAXIMUM:
			maximumMethod(positions, scores);
			break;
		case SUM:
			sumMethod(positions, scores);
			break;
		default:
			throw new IllegalArgumentException("Invalid method");
		}
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param positions list of positions
	 * @throws IllegalArgumentException
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ChromosomeListOfLists<Integer> positions) throws IllegalArgumentException {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;

		for(Chromosome currentChromosome : chromosomeManager)  {
			if ((positions.get(currentChromosome) == null) || (positions.size(currentChromosome) == 0)) {
				this.add(null);
			} else {
				int currentSize = currentChromosome.getLength() / binSize + 1;
				List<Double> listToAdd = ListFactory.createList(precision, currentSize); 
				this.add(listToAdd);			
				for (int i = 0; i < positions.size(currentChromosome); i++) {
					if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
						int windowTmp = positions.get(currentChromosome, i) / binSize;
						set(currentChromosome, windowTmp, get(currentChromosome, windowTmp) + 1);
					}
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities and load the BinList from this data. 
	 * If more than one position is found for one bin, the intensity of the bin is the maximum of the intensities of the positions
	 * @param positions list of positions
	 * @param scores list of scores
	 */
	private void maximumMethod(ChromosomeListOfLists<Integer> positions, ChromosomeListOfLists<Double> scores) {
		for(Chromosome currentChromosome : chromosomeManager)  {
			for (int i = 0; i < positions.size(currentChromosome); i++) {
				if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
					int currentWindow = positions.get(currentChromosome, i) / binSize;
					double valueToAdd = Math.max(this.get(currentChromosome, currentWindow), scores.get(currentChromosome, i));
					this.set(currentChromosome, currentWindow, valueToAdd);
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities and load the BinList from this data.
	 * If more than one position is found for one bin, the intensity of the bin is the sum of the intensities of the positions
	 * @param positions list of positions
	 * @param scores list of scores
	 */
	private void sumMethod(ChromosomeListOfLists<Integer> positions, ChromosomeListOfLists<Double> scores) {
		for(Chromosome currentChromosome : chromosomeManager)  {
			for (int i = 0; i < positions.size(currentChromosome); i++) {
				if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
					int currentWindow = positions.get(currentChromosome, i) / binSize;
					double valueToAdd = this.get(currentChromosome, currentWindow) + scores.get(currentChromosome, i);
					this.set(currentChromosome, currentWindow, valueToAdd);
				}
			}
		}
	}


	/**
	 * Returns a list containing a value of intensity for each bin.
	 * It goes through the list of positions and a list of intensities and load the BinList from this data
	 * @param positions list of positions
	 * @param scores list of scores
	 */
	private void averageMethod(ChromosomeListOfLists<Integer> positions, ChromosomeListOfLists<Double> scores) {
		for(Chromosome currentChromosome : chromosomeManager)  {
			int currentSize = this.size(currentChromosome);
			int[] counts = new int[currentSize];
			double[] sums = new double[currentSize];
			for(int i = 0; i < positions.size(currentChromosome); i++) {
				if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
					int currentWindow = positions.get(currentChromosome, i) / binSize;
					if (scores.get(currentChromosome, i) != 0) {
						sums[currentWindow] += scores.get(currentChromosome, i);
						counts[currentWindow]++;
					}
				}
			}
			for (int i = 0; i < currentSize; i++) {
				if (counts[i] != 0) {
					this.set(currentChromosome, i, sums[i] / (double)counts[i]);
				}
			}
		}
	}





	@Override
	protected void fitToScreen() {
		try {
			List<Double> currentList = get(fittedChromosome);
			if (currentList == null) {
				fittedDataList = null;
			} else {
				// we calculate how many windows are printable depending on the screen resolution
				fittedBinSize = binSize * (int)( 1 / (fittedXRatio * binSize));
				int binSizeRatio  = fittedBinSize / binSize;

				// if the fitted bin size is smaller than the regular bin size we don't modify the data
				if (fittedBinSize <= binSize) {
					fittedBinSize = binSize;
					fittedDataList = new double[currentList.size()];
					for (int i = 0; i < currentList.size(); i++) {
						fittedDataList[i] = currentList.get(i);
					}
				} else {
					fittedDataList = new double[(int)(size(fittedChromosome) / binSizeRatio + 1)];
					int newIndex = 0;
					for(int i = 0; i < size(fittedChromosome); i += binSizeRatio) {
						double sum = 0;
						int n = 0;
						for(int j = 0; j < binSizeRatio; j ++) {
							if ((i + j < size(fittedChromosome)) && (get(fittedChromosome, i + j) != 0)){
								sum += get(fittedChromosome, i + j);
								n++;					
							}				
						}
						if (n > 0) {
							fittedDataList[newIndex] = sum / n;
						}
						else {
							fittedDataList[newIndex] = 0;
						}
						newIndex++;
					}		
				}
			}
		} catch (Exception e) {
			fittedDataList = null;
			e.printStackTrace();
		}
	}


	@Override
	protected double[] getFittedData(int start, int stop) {
		// for the binlist we return the entire fitted data for the current chromosome
		return fittedDataList;
	}


	/**
	 * @return the bin size of this {@link BinList}
	 */
	public int getBinSize() {
		return binSize;
	}


	/**
	 * @return the precision of the data
	 */
	public DataPrecision getPrecision() {
		return precision;
	}


	/**
	 * @return the BinSize of the fitted data
	 */
	public int getFittedBinSize() {
		return fittedBinSize;
	}


	/**
	 * Performs a deep clone of the current BinList
	 * @return a new BinList
	 */
	public BinList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((BinList)ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
