package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList;


public class oldBinList {
	//
	//	private static final long serialVersionUID = -6114967730638134020L; // generated ID
	//	private static final int SAVED_FORMAT_VERSION_NUMBER = 0; 			// saved format version
	//	/**
	//	 * @param binSize bin size of the BinList
	//	 * @return the number of steps needed to create a BinList with the specified bin size
	//	 */
	//	public static int getCreationStepCount(int binSize) {
	//		// 1 step for the creation original BinList
	//		// + 2 steps for calculating the statistics
	//		int count = 1 + 2;
	//		binSize *= ACCELERATOR_FACTOR;
	//		// 1 step per accelerator BinList
	//		while (binSize < ACCELERATOR_MAX_BINSIZE) {
	//			count++;
	//			binSize *= ACCELERATOR_FACTOR;
	//		}
	//		return count;
	//	}
	//	private ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome(); // Instance of the Chromosome Manager
	//	private int 				binSize;		// size of the bins
	//	private ScorePrecision 		precision;		// precision of the data
	//
	//	private int 				fittedBinSize;	// size of the bins of the fitted data
	//
	//	private boolean				isCompressed = false;			// true if the compressed mode is on
	//	/*
	//	 * The  following parameters are used for the display of the BinList.
	//	 * A BinList contains another BinList with a bigger binSize.
	//	 * When the method that returns the data to be printed on the screen is called,
	//	 * if the binSize of the current BinList is too small (ie if the screen resolution is
	//	 * not big enough to show all the bins) the equivalent method from the BinList
	//	 * with bigger window will be called.
	//	 * This BinList can also have a BinList with a bigger binSize.
	//	 */
	//	private final static int 	ACCELERATOR_FACTOR = 100;	// factor used for the acceleration. Indicates how much bigger is the binSize of the accelerator binlist
	//	private final static int	ACCELERATOR_MAX_BINSIZE =
	//			500,000,000 / ACCELERATOR_FACTOR;						// we don't create accelerator BinList with a window bigger than that
	//	transient private BinList 			acceleratorBinList = null;	// BinList with a bigger binSize
	//
	//	transient private double[]		 	acceleratorCurrentChromo;	// copy of the values of the currently displayed chromosome
	//	/*
	//	 * The following values are statistic values of the BinList
	//	 * They are transient because they depend on the chromosome manager that is also transient
	//	 * They are calculated at the creation of the BinList to avoid being recalculated
	//	 */
	//	private Double 	min = null;			// smallest value of the BinList
	//	private Double 	max = null;			// greatest value of the BinList
	//	private Double 	average = null;		// average of the BinList
	//	private Double 	stDev = null;		// standar deviation of the BinList
	//	private Double 	sumScore = null;	// sum of the scores of the BinList
	//
	//
	//	private Long 	binCount = null;	// count of none-null bins in the BinList
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param positions list of positions
	//	 * @throws IllegalArgumentException
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final GenomicListView<Integer> positions) throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((positions.get(currentChromosome) != null) && (positions.size(currentChromosome) != 0)) {
	//						int currentSize = (currentChromosome.getLength() / binSize) + 1;
	//						resultList = ListFactory.createList(precision, currentSize);
	//						for (int i = 0; i < positions.size(currentChromosome); i++) {
	//							if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
	//								int windowTmp = positions.get(currentChromosome, i) / binSize;
	//								resultList.set(windowTmp, resultList.get(windowTmp) + 1);
	//							}
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param positions list of positions
	//	 * @param scores list of score
	//	 * @throws IllegalArgumentException thrown if precision is not valid
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final GenomicListView<Integer> positions, final GenomicListView<Double> scores) throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((positions.get(currentChromosome) != null) && (positions.size(currentChromosome) != 0)) {
	//						int currentSize = (currentChromosome.getLength() / binSize) + 1;
	//						resultList = ListFactory.createList(precision, currentSize);
	//						for (int i = 0; i < positions.size(currentChromosome); i++) {
	//							if (positions.get(currentChromosome, i) <= currentChromosome.getLength()) {
	//								int currentWindow = positions.get(currentChromosome, i) / binSize;
	//								resultList.set(currentWindow, scores.get(currentChromosome, i));
	//							}
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param data data of the BinList
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(int binSize, ScorePrecision precision, List<List<Double>> data) throws InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		for (List<Double> currentList: data) {
	//			add(currentList);
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList} from another BinList. The new BinList can have a different bin size.
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param method method of the score calculation
	//	 * @param binList input BinList
	//	 * @param generateStatistics true if the statistics need to be calculated (we don't want to generate the statistics of the accelerator BinLists)
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 * @throws IllegalArgumentException thrown if precision is not valid
	//	 */
	//	public BinList(int binSize, ScorePrecision precision, final ScoreOperation method, final BinList binList, boolean generateStatistics) throws InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//			final List<Double> currentList = binList.get(currentChromosome);
	//
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((currentList != null) && (currentList.size() != 0)) {
	//						// size of the BinList for the current chromosome
	//						int currentSize = (currentChromosome.getLength() / getBinSize()) + 1;
	//						// array to count how many elements for the average
	//						int[] counts = null;
	//						if (method == ScoreOperation.AVERAGE) {
	//							counts = new int[currentSize];
	//						}
	//						resultList = ListFactory.createList(getPrecision(), currentSize);
	//						// for each input windows
	//						for  (int i = 0; i < binList.size(currentChromosome); i++) {
	//							int start = i * binList.getBinSize();
	//							int stop = (i + 1) * binList.getBinSize();
	//							double score = currentList.get(i);
	//							SimpleScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(start, stop, score);
	//							computeScore(method, scw, resultList, counts);
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//			threadList.add(currentThread);
	//		}
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//
	//		generateAcceleratorBinList();
	//		// we generate the statistics only if the parameter generateStatistics is set to true
	//		if(generateStatistics) {
	//			generateStatistics();
	//		}
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param method method of the score calculation
	//	 * @param geneList list of genes
	//	 * @throws IllegalArgumentException thrown if precision is not valid
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final ScoreOperation method, final GeneList geneList) throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//
	//		final GenomicListView<Integer> starts = ChromosomeWindowLists.getStartList(geneList);
	//		final GenomicListView<Integer> stops = ChromosomeWindowLists.getStopList(geneList);
	//
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		// for each chromosome
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((starts.get(currentChromosome) != null) && (starts.size(currentChromosome) != 0)) {
	//						// size of the BinList for the current chromosome
	//						int currentSize = (currentChromosome.getLength() / binSize) + 1;
	//						// array to count how many elements for the average
	//						int[] counts = null;
	//						if (method == ScoreOperation.AVERAGE) {
	//							counts = new int[currentSize];
	//						}
	//						resultList = ListFactory.createList(precision, currentSize);
	//						// for each input windows
	//						for  (int i = 0; i < starts.size(currentChromosome); i++) {
	//							SimpleScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(starts.get(currentChromosome, i), stops.get(currentChromosome, i), 0.0);
	//							computeScore(method, scw, resultList, counts);
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param method method of the score calculation
	//	 * @param positions list of positions
	//	 * @param scores list of scores
	//	 * @throws IllegalArgumentException
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final ScoreOperation method, final GenomicListView<Integer> positions, final GenomicListView<Double> scores) throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//			final List<Double> currentScores = scores.get(currentChromosome);
	//			final List<Integer> currentPositions = positions.get(currentChromosome);
	//
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((currentPositions != null) && (currentPositions.size() != 0)) {
	//						int currentSize = (currentChromosome.getLength() / getBinSize()) + 1;
	//						resultList = ListFactory.createList(getPrecision(), currentSize);
	//						// if the method is average we create an array to store the count of scores
	//						int[] counts = null;
	//						if (method == ScoreOperation.AVERAGE) {
	//							counts = new int[currentSize];
	//						}
	//
	//						for (int i = 0; i < currentPositions.size(); i++) {
	//							SimpleScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(currentPositions.get(i), currentPositions.get(i), currentScores.get(i));
	//							computeScore(method, scw, resultList, counts);
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param method method of the score calculation
	//	 * @param starts list of start positions
	//	 * @param stops list of stop positions
	//	 * @param scores list of scores
	//	 * @throws IllegalArgumentException thrown if precision is not valid
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final ScoreOperation method, final GenomicListView<Integer> starts, final GenomicListView<Integer> stops, final GenomicListView<Double> scores) throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		// for each chromosome
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((starts.get(currentChromosome) != null) && (starts.size(currentChromosome) != 0)) {
	//						// size of the BinList for the current chromosome
	//						int currentSize = (currentChromosome.getLength() / binSize) + 1;
	//						// array to count how many elements for the average
	//						int[] counts = null;
	//						if (method == ScoreOperation.AVERAGE) {
	//							counts = new int[currentSize];
	//						}
	//						resultList = ListFactory.createList(precision, currentSize);
	//						// for each input windows
	//						for  (int i = 0; i < starts.size(currentChromosome); i++) {
	//							SimpleScoredChromosomeWindow scw = new SimpleScoredChromosomeWindow(starts.get(currentChromosome, i), stops.get(currentChromosome, i), scores.get(currentChromosome, i));
	//							computeScore(method, scw, resultList, counts);
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Creates an instance of {@link BinList}
	//	 * @param binSize size of the bins
	//	 * @param precision precision of the data
	//	 * @param method method of the score calculation
	//	 * @param list list of {@link SimpleScoredChromosomeWindow}
	//	 * @throws IllegalArgumentException thrown if precision is not valid
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	public BinList(final int binSize, final ScorePrecision precision, final ScoreOperation method, final ImmutableGenomicDataList<ScoredChromosomeWindow> list)  throws IllegalArgumentException, InterruptedException, ExecutionException {
	//		super();
	//		this.binSize = binSize;
	//		this.precision = precision;
	//		// retrieve the instance of the OperationPool
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<List<Double>>> threadList = new ArrayList<Callable<List<Double>>>();
	//		for(final Chromosome currentChromosome : projectChromosome)  {
	//			final List<ScoredChromosomeWindow> currentList = list.getView(currentChromosome);
	//
	//			Callable<List<Double>> currentThread = new Callable<List<Double>>() {
	//				@Override
	//				public List<Double> call() throws Exception {
	//					List<Double> resultList = null;
	//					if ((currentList != null) && (currentList.size() != 0)) {
	//						// size of the BinList for the current chromosome
	//						int currentSize = (currentChromosome.getLength() / binSize) + 1;
	//						// array to count how many elements for the average
	//						int[] counts = null;
	//						if (method == ScoreOperation.AVERAGE) {
	//							counts = new int[currentSize];
	//						}
	//						resultList = ListFactory.createList(precision, currentSize);
	//						// for each input windows
	//						for  (int i = 0; i < currentList.size(); i++) {
	//							ScoredChromosomeWindow scw = currentList.get(i);
	//							computeScore(method, scw, resultList, counts);
	//						}
	//					}
	//					// tell the operation pool that a chromosome is done
	//					op.notifyDone();
	//					return resultList;
	//				}
	//			};
	//			threadList.add(currentThread);
	//		}
	//		List<List<Double>> result = null;
	//		// starts the pool
	//		result = op.startPool(threadList);
	//		// add the chromosome results
	//		if (result != null) {
	//			for (List<Double> currentList: result) {
	//				add(currentList);
	//			}
	//		}
	//		finalizeConstruction();
	//	}
	//
	//
	//	/**
	//	 * Tells the accelerator BinList that the chromosome changed
	//	 * And copies the values of the current chromosome in the accelerator array
	//	 */
	//	@Override
	//	protected void chromosomeChanged() {
	//		List<Double> currentList = get(fittedChromosome);;
	//		boolean uncompressed = false;
	//		// if the list is compressed we need to uncompress it first
	//		if (isCompressed()) {
	//			if (currentList instanceof CompressibleList) {
	//				try {
	//					((CompressibleList) currentList).uncompress();
	//					uncompressed = true;
	//				} catch (CompressionException e) {
	//					ExceptionManager.getInstance().caughtException(e);
	//				}
	//			}
	//		}
	//		if (currentList == null) {
	//			acceleratorCurrentChromo = null;
	//		} else {
	//			acceleratorCurrentChromo = new double[currentList.size()];
	//			for (int i = 0; i < currentList.size(); i++) {
	//				acceleratorCurrentChromo[i] = currentList.get(i);
	//			}
	//		}
	//		if (acceleratorBinList != null) {
	//			acceleratorBinList.fittedChromosome = fittedChromosome;
	//			acceleratorBinList.chromosomeChanged();
	//		}
	//		// if we uncompressed the list we need to recompress it
	//		if (uncompressed) {
	//			try {
	//				((CompressibleList) currentList).compress();
	//			} catch (CompressionException e) {
	//				ExceptionManager.getInstance().caughtException(e);
	//			}
	//		}
	//	}
	//
	//
	//	/**
	//	 * Compresses the BinList
	//	 * @throws CompressionException
	//	 */
	//	public void compress() throws CompressionException {
	//		for (int i = 0; i < size(); i++) {
	//			final List<Double> currentList = get(i);
	//			if (currentList instanceof CompressibleList) {
	//				((CompressibleList)currentList).compress();
	//			}
	//		}
	//		isCompressed = true;
	//		if (acceleratorBinList != null) {
	//			acceleratorBinList.compress();
	//		} else {
	//			// if there is no more accelerator BinList we call the garbage collector
	//			System.gc();
	//		}
	//	}
	//
	//
	//	/**
	//	 * Computes the score during the construction of a BinList
	//	 * @param method method for the calculation of the score
	//	 * @param window input data needed to generate the binlist
	//	 * @param resultList result list that's represent one chromosome of BinList that is created
	//	 * @param counts used only if the method is average. Stores the number of element inserted in order to be able to compute the average. Must be the same length as the result list
	//	 */
	//	private void computeScore(ScoreOperation method, ScoredChromosomeWindow window, List<Double>resultList, int[] counts) {
	//		double start = window.getStart() / (double) binSize;
	//		double stop = window.getStop() / (double) binSize;
	//		int currentWindowSize = window.getSize();
	//		for (int j = (int) start; j < stop; j++) {
	//			if (j < resultList.size()) {
	//				// we compute the proportional score
	//				double score = 0;
	//				// case where there is just one bin
	//				if ((j == (int) start) && ((j + 1) >= stop)) {
	//					score = window.getScore();
	//				} else { // case where the score is divided into more than one bin
	//					// if it's the first bin
	//					if ((j == (int) start)) {
	//						int firstWindowLength = ((j + 1) * binSize) - window.getStart();
	//						score = (window.getScore() / currentWindowSize) * firstWindowLength;
	//					} else if (((j + 1) >= stop)) { // if it's the last bin
	//						int lastWindowLength = window.getStop() - (j * binSize);
	//						score = (window.getScore() / currentWindowSize) * lastWindowLength;
	//					} else { // for the other bins
	//						score = (window.getScore() / currentWindowSize) * binSize;
	//					}
	//				}
	//				// add the score with the good method
	//				switch (method) {
	//				case AVERAGE:
	//					score = (resultList.get(j) * counts[j]) + score;
	//					counts[j]++;
	//					resultList.set(j, score / counts[j]);
	//					break;
	//				case MAXIMUM:
	//					if (resultList.get(j) != 0) {
	//						score = Math.max(resultList.get(j), score);
	//						resultList.set(j, score);
	//					} else {
	//						resultList.set(j, score);
	//					}
	//					break;
	//				case SUM:
	//					score = resultList.get(j) + score;
	//					resultList.set(j, score);
	//					break;
	//				}
	//			}
	//		}
	//	}
	//
	//
	//	/**
	//	 * Performs a deep clone of the current BinList
	//	 * @return a new BinList
	//	 */
	//	public BinList deepClone() {
	//		try {
	//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//			ObjectOutputStream oos = new ObjectOutputStream(baos);
	//			oos.writeObject(this);
	//			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	//			ObjectInputStream ois = new ObjectInputStream(bais);
	//			return ((BinList)ois.readObject());
	//		} catch (Exception e) {
	//			ExceptionManager.getInstance().caughtException(e);
	//			return null;
	//		}
	//	}
	//
	//
	//	/**
	//	 * Generates the BinList accelerator and the statistics.
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	private void finalizeConstruction() throws InterruptedException, ExecutionException {
	//		generateAcceleratorBinList();
	//		generateStatistics();
	//	}
	//
	//
	//	@Override
	//	protected void fitToScreen() {
	//		try {
	//			if (get(fittedChromosome) == null) {
	//				fittedDataList = null;
	//				return;
	//			}
	//
	//			// if there is to many bins to print we print the bins of the accelerator BinList
	//			// (same list) with bigger binsize
	//			if ((fittedXRatio * binSize) < (1 / (double)ACCELERATOR_FACTOR)) {
	//				// if the accelerator binlist doesn't exist we create it
	//				if (acceleratorBinList == null) {
	//					acceleratorBinList = new BinList(binSize * ACCELERATOR_FACTOR, getPrecision(), ScoreOperation.AVERAGE, this, false);
	//					acceleratorBinList.fittedChromosome = fittedChromosome;
	//					acceleratorBinList.chromosomeChanged();
	//				}
	//				acceleratorBinList.fittedXRatio = fittedXRatio;
	//				acceleratorBinList.fitToScreen();
	//				fittedDataList = acceleratorBinList.fittedDataList;
	//				fittedBinSize = acceleratorBinList.fittedBinSize;
	//				// else even if the binsize of the current binlist is adapted,
	//				// we might still need to calculate the average if we have to print
	//				//more than one bin per pixel
	//			} else {
	//				// we calculate how many windows are printable depending on the screen resolution
	//				fittedBinSize = binSize * (int)( 1 / (fittedXRatio * binSize));
	//				int binSizeRatio  = fittedBinSize / binSize;
	//				// if the fitted bin size is smaller than the regular bin size we don't modify the data
	//				if (fittedBinSize <= binSize) {
	//					fittedDataList = acceleratorCurrentChromo;
	//					fittedBinSize = binSize;
	//				} else {
	//					// otherwise we calculate the average because we have to print more than
	//					// one bin per pixel
	//					fittedDataList = new double[(acceleratorCurrentChromo.length / binSizeRatio) + 1];
	//					int newIndex = 0;
	//					for(int i = 0; i < acceleratorCurrentChromo.length; i += binSizeRatio) {
	//						double sum = 0;
	//						int n = 0;
	//						for(int j = 0; j < binSizeRatio; j ++) {
	//							if (((i + j) < acceleratorCurrentChromo.length) && (acceleratorCurrentChromo[i + j] != 0)){
	//								sum += acceleratorCurrentChromo[i + j];
	//								n++;
	//							}
	//						}
	//						if (n > 0) {
	//							fittedDataList[newIndex] = sum / n;
	//						}
	//						else {
	//							fittedDataList[newIndex] = 0;
	//						}
	//						newIndex++;
	//					}
	//				}
	//			}
	//		} catch (Exception e) {
	//			fittedDataList = null;
	//			ExceptionManager.getInstance().caughtException(e);
	//		}
	//	}
	//
	//
	//	/**
	//	 * Creates a BinList with a greater binSize in order to accelerate the display
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	private void generateAcceleratorBinList() throws InterruptedException, ExecutionException {
	//		if (binSize < ACCELERATOR_MAX_BINSIZE) {
	//			acceleratorBinList = new BinList(binSize * ACCELERATOR_FACTOR, getPrecision(), ScoreOperation.AVERAGE, this, false);
	//		}
	//	}
	//
	//
	//	/**
	//	 * Computes some statistic values for this BinList
	//	 * @throws ExecutionException
	//	 * @throws InterruptedException
	//	 */
	//	private void generateStatistics() throws InterruptedException, ExecutionException {
	//		// retrieve the instance of the OperationPool singleton
	//		final OperationPool op = OperationPool.getInstance();
	//		// list for the threads
	//		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
	//
	//		// set the default value
	//		min = Double.POSITIVE_INFINITY;
	//		max = Double.NEGATIVE_INFINITY;
	//		average = 0d;
	//		stDev = 0d;
	//		sumScore = 0d;
	//		binCount = 0l;
	//
	//		// create arrays so each statics variable can be calculated for each chromosome
	//		final double[] mins = new double[projectChromosome.size()];
	//		final double[] maxs = new double[projectChromosome.size()];
	//		final double[] stDevs = new double[projectChromosome.size()];
	//		final double[] sumScores = new double[projectChromosome.size()];
	//		final int[] binCounts = new int[projectChromosome.size()];
	//
	//		// computes min / max / total score / non null bin count for each chromosome
	//		for(short i = 0; i < size(); i++)  {
	//			final List<Double> currentList = get(i);
	//			final short currentIndex = i;
	//
	//			Callable<Void> currentThread = new Callable<Void>() {
	//				@Override
	//				public Void call() throws Exception {
	//					mins[currentIndex] = Double.POSITIVE_INFINITY;
	//					maxs[currentIndex] = Double.NEGATIVE_INFINITY;
	//					if (currentList != null) {
	//						for (Double currentValue: currentList) {
	//							if (currentValue != 0) {
	//								mins[currentIndex] = Math.min(mins[currentIndex], currentValue);
	//								maxs[currentIndex] = Math.max(maxs[currentIndex], currentValue);
	//								sumScores[currentIndex] += currentValue;
	//								binCounts[currentIndex]++;
	//							}
	//						}
	//					}
	//					// notify that the current chromosome is done
	//					op.notifyDone();
	//					return null;
	//				}
	//			};
	//
	//			threadList.add(currentThread);
	//		}
	//		// start the pool of thread
	//		op.startPool(threadList);
	//
	//		// compute the genome wide result from the chromosomes results
	//		for (int i = 0; i < projectChromosome.size(); i++) {
	//			min = Math.min(min, mins[i]);
	//			max = Math.max(max, maxs[i]);
	//			sumScore += sumScores[i];
	//			binCount += binCounts[i];
	//		}
	//
	//		if (binCount != 0) {
	//			// compute the average
	//			average = sumScore / (double) binCount;
	//			threadList.clear();
	//
	//			// compute the standard deviation for each chromosome
	//			for(short i = 0; i < size(); i++)  {
	//				final List<Double> currentList = get(i);
	//				final short currentIndex = i;
	//
	//				Callable<Void> currentThread = new Callable<Void>() {
	//					@Override
	//					public Void call() throws Exception {
	//						if (currentList != null) {
	//							for (Double currentValue: currentList) {
	//								if (currentValue != 0) {
	//									stDevs[currentIndex] += Math.pow(currentValue - average, 2);
	//								}
	//							}
	//						}
	//						// notify that the current chromosome is done
	//						op.notifyDone();
	//						return null;
	//					}
	//				};
	//
	//				threadList.add(currentThread);
	//			}
	//			// start the pool of thread
	//			op.startPool(threadList);
	//
	//			// compute the genome wide standard deviation
	//			for (int i = 0; i < projectChromosome.size(); i++) {
	//				stDev += stDevs[i];
	//			}
	//			stDev = Math.sqrt(stDev / (double) binCount);
	//		}
	//	}
	//
	//
	//	/**
	//	 * @return the average of the BinList
	//	 */
	//	public Double getAverage() {
	//		return average;
	//	}
	//
	//
	//	/**
	//	 * @return the count of none-null bins in the BinList
	//	 */
	//	public Long getBinCount() {
	//		return binCount;
	//	}
	//
	//
	//	/**
	//	 * @return the bin size of this {@link BinList}
	//	 */
	//	public int getBinSize() {
	//		return binSize;
	//	}
	//
	//
	//	/**
	//	 * @return the BinSize of the fitted data
	//	 */
	//	public int getFittedBinSize() {
	//		return fittedBinSize;
	//	}
	//
	//
	//	@Override
	//	protected double[] getFittedData(int start, int stop) {
	//		// for the binlist we return the entire fitted data for the current chromosome
	//		return fittedDataList;
	//	}
	//
	//
	//	/**
	//	 * @return the greatest value of the BinList
	//	 */
	//	public Double getMax() {
	//		return max;
	//	}
	//
	//
	//	/**
	//	 * @return the smallest value of the BinList
	//	 */
	//	public Double getMin() {
	//		return min;
	//	}
	//
	//
	//	/**
	//	 * @return the precision of the data
	//	 */
	//	public ScorePrecision getPrecision() {
	//		return precision;
	//	}
	//
	//
	//	/**
	//	 * @param position position in the fitted chromosome list
	//	 * @return the score of the specified position on the fitted chromosome
	//	 */
	//	public double getScore(int position) {
	//		// for the binlist we return the entire data for the current chromosome
	//		return acceleratorCurrentChromo[position / binSize];
	//	}
	//
	//
	//	/**
	//	 * @return the standard deviation of the BinList
	//	 */
	//	public Double getStDev() {
	//		return stDev;
	//	}
	//
	//
	//	/**
	//	 * @return the sum of the scores of the BinList
	//	 */
	//	public Double getSumScore() {
	//		return sumScore;
	//	}
	//
	//
	//	/**
	//	 * @return the if the BinList is compressed
	//	 */
	//	public boolean isCompressed() {
	//		return isCompressed;
	//	}
	//
	//
	//	/**
	//	 * Prints the {@link BinList} on the standard output
	//	 */
	//	public void print() {
	//		for(short i = 0; i < size(); i++) {
	//			if(get(i) != null) {
	//				for (int j = 0; j < size(i); j++) {
	//					System.out.println(projectChromosome.get(i).getName() + "\t" + (j * binSize) + "\t" + ((j + 1) * binSize) + "\t" + get(i, j));
	//				}
	//			}
	//		}
	//	}
	//
	//
	//	/**
	//	 * Unserializes the saved fields.  The number format field can be used to specify a
	//	 * different loading depending on the saved format
	//	 * @param in {@link ObjectInputStream}
	//	 * @throws IOException
	//	 * @throws ClassNotFoundException
	//	 */
	//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	//		in.readInt(); // read the saved format number (useful if there is different loading method depending on the saved format number)
	//		projectChromosome = (ProjectChromosome) in.readObject();
	//		binSize = in.readInt();
	//		precision = (ScorePrecision) in.readObject();
	//		fittedBinSize = in.readInt();
	//		min = in.readDouble();
	//		max = in.readDouble();
	//		average = in.readDouble();
	//		stDev = in.readDouble();
	//		sumScore = in.readDouble();
	//		binCount = in.readLong();
	//		try {
	//			generateAcceleratorBinList();
	//		} catch (Exception e) {
	//			throw new IOException();
	//		}
	//
	//	}
	//
	//
	//	/**
	//	 * Uncompresses the BinList
	//	 * @throws CompressionException
	//	 */
	//	public void uncompress() throws CompressionException {
	//		for (List<Double> currentList: this) {
	//			if (currentList instanceof CompressibleList) {
	//				((CompressibleList)currentList).uncompress();
	//			}
	//		}
	//		isCompressed = false;
	//		if (acceleratorBinList != null) {
	//			acceleratorBinList.uncompress();
	//		} else {
	//			// if there is no more accelerator BinList we call the garbage collector
	//			System.gc();
	//		}
	//	}
	//
	//
	//	/**
	//	 * Serializes the fields needed to save a BinList
	//	 * @param out
	//	 * @throws IOException
	//	 */
	//	private void writeObject(ObjectOutputStream out) throws IOException {
	//		out.writeInt(SAVED_FORMAT_VERSION_NUMBER); // save the version of the saved format number
	//		out.writeObject(projectChromosome);
	//		out.writeInt(binSize);
	//		out.writeObject(precision);
	//		out.writeInt(fittedBinSize);
	//		out.writeDouble(min);
	//		out.writeDouble(max);
	//		out.writeDouble(average);
	//		out.writeDouble(stDev);
	//		out.writeDouble(sumScore);
	//		out.writeLong(binCount);
	//	}
}
