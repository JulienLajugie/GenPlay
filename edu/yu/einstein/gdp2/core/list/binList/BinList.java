/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.binList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import yu.einstein.gdp2.core.list.arrayList.CompressibleList;
import yu.einstein.gdp2.exception.CompressionException;
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

	private boolean					isCompressed = false;			// true if the compressed mode is on

	/*
	 * The  following parameters are used for the display of the BinList.
	 * A BinList contains another BinList with a bigger binSize. 
	 * When the method that returns the data to be printed on the screen is called,
	 * if the binSize of the current BinList is too small (ie if the screen resolution is 
	 * not big enough to show all the bins) the equivalent method from the BinList
	 * with bigger window will be called. 
	 * This BinList can also have a BinList with a bigger binSize. 
	 */
	private final static int 	ACCELERATOR_FACTOR = 50;	// factor used for the acceleration. Indicates how much bigger is the binSize of the accelerator binlist
	private final static int	ACCELERATOR_MAX_BINSIZE = 
		500000000 / ACCELERATOR_FACTOR;						// we don't create accelerator BinList with a window bigger than that 
	private BinList 			acceleratorBinList = null;	// BinList with a bigger binSize
	private double[]		 	acceleratorCurrentChromo;	// copy of the values of the currently displayed chromosome

	/*
	 * The following values are statistic values of the BinList
	 * They are transient because they depend on the chromosome manager that is also transient
	 * They are calculated at the creation of the BinList to avoid being recalculated
	 */
	transient private Double 	min = null;			// smallest value of the BinList
	transient private Double 	max = null;			// greatest value of the BinList 
	transient private Double 	average = null;		// average of the BinList
	transient private Double 	stDev = null;		// standar deviation of the BinList
	transient private Double 	sumScore = null;	// sum of the scores of the BinList
	transient private Long 		binCount = null;	// count of none-null bins in the BinList


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
		finalizeConstruction();
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
		finalizeConstruction();
	}


	/**
	 * Creates an instance of {@link BinList} from another BinList. The new BinList can have a different bin size.
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param method method of the score calculation
	 * @param binList input BinList
	 * @throws IllegalArgumentException thrown if precision is not valid
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, ScoreCalculationMethod method, BinList binList) {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
		for(Chromosome currentChromosome : chromosomeManager)  {
			if ((binList.get(currentChromosome) == null) || (binList.size(currentChromosome) == 0)) {
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
					while ((k < binList.size(currentChromosome)) && (binList.getBinSize() * (k + 1) <= j * binSize)) {
						k++;
					}
					previousStop = k;
					while ((k < binList.size(currentChromosome)) && (binList.getBinSize() * k < j * binSize)) {
						if ((binList.getBinSize() * (k + 1)) > (j * binSize)) {
							if (method == ScoreCalculationMethod.SUM) {
								int stop = Math.min((j + 1) * binSize, (k + 1) * binList.getBinSize());
								double intensity = binList.get(currentChromosome, k) * (stop - (j * binSize)) / (binList.getBinSize());
								currentBinIntensities.add(intensity);							
							} else {
								double intensity = binList.get(currentChromosome, k);
								currentBinIntensities.add(intensity);
							}
						}
						k++;
					} 						
					while ((k < binList.size(currentChromosome)) && (k * binList.getBinSize() < (j + 1) * binSize)) {
						if ((k + 1) * binList.getBinSize() > (j + 1) * binSize) {
							if (method == ScoreCalculationMethod.SUM) {
								int start = Math.max(j * binSize, k * binList.getBinSize());
								double intensity = binList.get(currentChromosome, k) * (((j + 1) * binSize) - start) / (binList.getBinSize());
								currentBinIntensities.add(intensity);
							} else {
								double intensity = binList.get(currentChromosome, k);
								currentBinIntensities.add(intensity);
							}																
						} else {
							if ((k + 1) * binList.getBinSize() > (j * binSize)) {
								double intensity = binList.get(currentChromosome, k);
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
		finalizeConstruction();
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
		finalizeConstruction();
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
		finalizeConstruction();
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
		finalizeConstruction();
	}


	/**
	 * Creates an instance of {@link BinList}
	 * @param chromosomeManager {@link ChromosomeManager}
	 * @param binSize size of the bins
	 * @param precision precision of the data
	 * @param data data of the BinList
	 */
	public BinList(ChromosomeManager chromosomeManager, int binSize, DataPrecision precision, List<List<Double>> data) {
		super(chromosomeManager);
		this.binSize = binSize;
		this.precision = precision;
		for (List<Double> currentList: data) {
			add(currentList);
		}
		finalizeConstruction();
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
			if (get(fittedChromosome) == null) {
				fittedDataList = null;
				return;
			}
			
			// if there is to many bins to print we print the bins of the accelerator BinList 
			// (same list) with bigger binsize
			if ((fittedXRatio * binSize) < (1 / (double)ACCELERATOR_FACTOR)) {
				// if the accelerator binlist doesn't exist we create it
				if (acceleratorBinList == null) {
					acceleratorBinList = new BinList(getChromosomeManager(), binSize * ACCELERATOR_FACTOR, getPrecision(), ScoreCalculationMethod.AVERAGE, this);
					acceleratorBinList.fittedChromosome = fittedChromosome;
					acceleratorBinList.chromosomeChanged();
				}
				acceleratorBinList.fittedXRatio = fittedXRatio;				
				acceleratorBinList.fitToScreen();
				this.fittedDataList = acceleratorBinList.fittedDataList;
				this.fittedBinSize = acceleratorBinList.fittedBinSize;
				// else even if the binsize of the current binlist is adapted,
				// we might still need to calculate the average if we have to print 
				//more than one bin per pixel
			} else {
				// we calculate how many windows are printable depending on the screen resolution
				this.fittedBinSize = binSize * (int)( 1 / (fittedXRatio * binSize));
				int binSizeRatio  = fittedBinSize / binSize;
				// if the fitted bin size is smaller than the regular bin size we don't modify the data
				if (fittedBinSize <= binSize) {
					this.fittedDataList = acceleratorCurrentChromo;
					this.fittedBinSize = binSize;	
				} else {
					// otherwise we calculate the average because we have to print more than
					// one bin per pixel
					fittedDataList = new double[(int)(acceleratorCurrentChromo.length / binSizeRatio + 1)];
					int newIndex = 0;
					for(int i = 0; i < acceleratorCurrentChromo.length; i += binSizeRatio) {
						double sum = 0;
						int n = 0;
						for(int j = 0; j < binSizeRatio; j ++) {
							if ((i + j < acceleratorCurrentChromo.length) && (acceleratorCurrentChromo[i + j] != 0)){
								sum += acceleratorCurrentChromo[i + j];
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


	/**
	 * Tells the accelerator BinList that the chromosome changed
	 * And copies the values of the current chromosome in the accelerator array
	 */
	@Override
	protected void chromosomeChanged() {
		List<Double> currentList = get(fittedChromosome);;
		boolean uncompressed = false;
		// if the list is compressed we need to uncompress it first
		if (isCompressed()) {
			if (currentList instanceof CompressibleList) {
				try {
					((CompressibleList) currentList).uncompress();
					uncompressed = true;
				} catch (CompressionException e) {
					e.printStackTrace();
				}
			}
		}
		if (currentList == null) {
			acceleratorCurrentChromo = null;
		} else {
			acceleratorCurrentChromo = new double[currentList.size()];
			for (int i = 0; i < currentList.size(); i++) {
				acceleratorCurrentChromo[i] = currentList.get(i);
			}
		}
		if (acceleratorBinList != null) {
			acceleratorBinList.fittedChromosome = fittedChromosome;
			acceleratorBinList.chromosomeChanged();
		}
		super.chromosomeChanged();
		// if we uncompressed the list we need to recompress it
		if (uncompressed) {
			try {
				((CompressibleList) currentList).compress();
			} catch (CompressionException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	protected double[] getFittedData(int start, int stop) {
		// for the binlist we return the entire fitted data for the current chromosome
		return fittedDataList;
	}


	/**
	 * @return the score of the specified position on the fitted chromosome
	 */
	public double getScore(int position) {
		// for the binlist we return the entire data for the current chromosome
		return acceleratorCurrentChromo[position / binSize];
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
	 * Prints the {@link BinList} on the standard output
	 */
	public void print() {
		for(short i = 0; i < size(); i++) {
			if(get(i) != null) {
				for (int j = 0; j < size(i); j++) {
					System.out.println(getChromosomeManager().getChromosome(i).getName() + "\t" + (j * binSize) + "\t" + ((j + 1) * binSize) + "\t" + get(i, j));
				}
			}
		}
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


	/**
	 * Generates the BinList accelerator and the statistics.
	 */
	private void finalizeConstruction() {
		generateAcceleratorBinList();
		generateStatistics();	
	}


	/**
	 * Creates a BinList with a greater binSize in order to accelerate the display
	 */
	private void generateAcceleratorBinList() {
		if (binSize < ACCELERATOR_MAX_BINSIZE) {			
			acceleratorBinList = new BinList(getChromosomeManager(), binSize * ACCELERATOR_FACTOR, getPrecision(), ScoreCalculationMethod.AVERAGE, this);
		}
	}


	/**
	 * Computes some statistic values for this BinList
	 */
	private void generateStatistics() {
		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;
		average = 0d;
		stDev = 0d;
		sumScore = 0d;
		binCount = 0l;
		for (List<Double> currentList: this) {
			if (currentList != null) {				
				for (Double currentValue: currentList) {
					if (currentValue != 0) {
						min = Math.min(min, currentValue);
						max = Math.max(max, currentValue);
						sumScore += currentValue;
						binCount++;
					}
				}
			}
		}
		if (binCount != 0) {
			average = sumScore / (double) binCount;
			for (List<Double> currentList: this) {
				if (currentList != null) {				
					for (Double currentValue: currentList) {
						if (currentValue != 0) {
							stDev += Math.pow(currentValue - average, 2);
						}
					}
				}
			}
			stDev = Math.sqrt(stDev / (double) binCount);
		}
	}


	/**
	 * @return the smallest value of the BinList
	 */
	public Double getMin() {
		return min;
	}


	/**
	 * @return the greatest value of the BinList
	 */
	public Double getMax() {
		return max;
	}


	/**
	 * @return the average of the BinList
	 */
	public Double getAverage() {
		return average;
	}


	/**
	 * @return the standard deviation of the BinList
	 */
	public Double getStDev() {
		return stDev;
	}


	/**
	 * @return the sum of the scores of the BinList
	 */
	public Double getSumScore() {
		return sumScore;
	}


	/**
	 * @return the count of none-null bins in the BinList
	 */
	public Long getBinCount() {
		return binCount;
	}


	/**
	 * @return the if the BinList is compressed
	 */
	public boolean isCompressed() {
		return isCompressed;
	}


	/**
	 * Compresses the BinList
	 * @throws CompressionException
	 */
	public void compress() throws CompressionException {
		for (int i = 0; i < size(); i++) {
			final List<Double> currentList = get(i);
			if (currentList instanceof CompressibleList) {
				((CompressibleList)currentList).compress();
			}
		}
		isCompressed = true;
		if (acceleratorBinList != null) {
			acceleratorBinList.compress();				
		} else {
			// if there is no more accelerator BinList we call the garbage collector
			System.gc();
		}
	}


	/**
	 * Uncompresses the BinList
	 * @throws CompressionException
	 */
	public void uncompress() throws CompressionException {
		for (List<Double> currentList: this) {
			if (currentList instanceof CompressibleList) {
				((CompressibleList)currentList).uncompress();
			}
		}
		isCompressed = false;
		if (acceleratorBinList != null) {
			acceleratorBinList.uncompress();
		} else {
			// if there is no more accelerator BinList we call the garbage collector
			System.gc();
		}
	}




	/**
	 * Recompresses the list if needed after unserialization
	 * @param in {@link ObjectInputStream}
	 * @throws IOExceptionm
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		generateStatistics();
		if (isCompressed) {
			try {
				compress();
			} catch (CompressionException e) {
				e.printStackTrace();
			}
		}
	}
}
