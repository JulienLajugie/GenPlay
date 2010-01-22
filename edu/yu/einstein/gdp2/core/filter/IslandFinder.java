/**
 * @author Alexander Golec
 * @version 0.1
 */

package yu.einstein.gdp2.core.filter;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import yu.einstein.gdp2.core.list.binList.BinList;
import yu.einstein.gdp2.core.stat.Poisson;
import yu.einstein.gdp2.exception.PoissonInvalidParameterException;

/**
 * @author Alexander Golec
 * @version 0.1 An implementation of the method for finding islands of reads in
 *          ChIP-Enriched domains as described in A clustering approach for
 *          identification of enriched domains deom histone modification
 *          ChIP-Seq data" by Zang, Schones, Zeng, Cui, Zhao, and Peng.
 */

public class IslandFinder {
	// The l-value to use as a threshold probability when determining the minimal 
	// eligible score for an island. 
	static private final double lValueProbability = .05;
	
		// A class to hold statistical data for a filtration operation 
		private static class StatStruct {
		// The total number of reads within the BinList. 
		private final int totalReads;
		// The width, in base pairs, of a window in the BinList. 
		private final int windowWidth;
		// The length, in windows. 
		private final int libraryLength;
		// Poisson distribution made for this particular list. 
		private final Poisson poisson;
		// The list. 
		private final BinList list;
		
		// Create a new instance of the StatStruct from a BinList. 
		public StatStruct (BinList list) {
			windowWidth = list.getBinSize();
			int totalReads_s = 0;
			int libraryLength_s = 0;
			// Count the total number of reads and the total length of the library. 
			// We count chromosome array with null values as zero length. 
			Iterator<List<Double>> t = list.iterator();
			while (t.hasNext()) {
				Iterator <Double> d = t.next().iterator();
				while (d.hasNext()) {
					libraryLength_s += getWindowWidth();
					totalReads_s += d.next();
				}
			}
			
			totalReads = totalReads_s;
			libraryLength = libraryLength_s;
			
			// If anything goes wrong with creation of the Poisson, then there is a serious problem. 
			Poisson poisson_s = null;
			try {
				poisson_s = new Poisson((double) windowWidth * (double) getTotalReads()
						/ (double) getLibraryLength());
			} catch (PoissonInvalidParameterException e) {
				e.printStackTrace();
			}
			poisson = poisson_s;
			this.list = list;
		}
		
		// Getters and setters. Boring. 
		public int getTotalReads()     {return totalReads;}
		public int getWindowWidth()    {return windowWidth;}
		public int getLibraryLength()  {return libraryLength;}
		public Poisson getPoisson()    {return poisson;}
		public BinList getList()       {return list;}
	}
	
	/**
	 * Perform a filtration on a BinList. This filtration searches the BinList and removes all
	 * islands and windows where reads are likely to have occurred by random statistical fluctuation, 
	 * saving only those islands that would be very unlikely to have formed by chance. 
	 * 
	 * @param in a BinList to filter
	 * @return a BinList containing only relevant data. 
	 */
	public static BinList filterList(BinList in) {
//		StatStruct stats = new StatStruct(in);
//
//		int minimalWindowCount = stats.getPoisson().lValueDetermination(lValueProbability);
//		BinList out = stats.getList().binListCounterpart();
//
//		// Copy all windows of the in list that have enough reads to be considerd eligible. 
//		for (int i = 0; i < stats.getList().getData().length; i++) {
//			for (int j = 0; j < stats.getList().getData()[i].length; j++){
//				double current = stats.getList().getData()[i][j];
//				if (current >= minimalWindowCount) {
//					//out.setBinListElement(i, j, current);
//				} else {
//					//out.setBinListElement(i, j, 0);
//				}
//			}
//		}
//
//		return out;
		return null;
	}
	/**
	 * Scores a chromosome.
	 * 
	 * @param stats the statistical singleton for this filtration instance. 
	 * @param chromosome which chromosome to score
	 */
	@SuppressWarnings("unused")
	private static double[] scoreChromosome(StatStruct stats, int chromosome) {
		double[] ret = new double[stats.getList().get(chromosome).size()];
		// If something goes wrong here, then something is seriously improperly
		// written.
		// This exception has no business being thrown here.
		try {
			int len = stats.getList().get(chromosome).size();
			for (int i = 0; i < len; i++) {
				ret[i] = windowScore(stats, chromosome, i);
			}
		} catch (PoissonInvalidParameterException e) {
			// TODO Bring the filter down in flames.
			e.printStackTrace();
		}
		return ret;
	}

	/*
	 * Scores a single window
	 * 
	 * @param stats the statistical singleton for this filtration instance. 
	 * @param chromosome the chromosome to work on. 
	 * @param window the position of the window to score.
	 */
	private static double windowScore(StatStruct stats, int chromosome, int window)
			throws PoissonInvalidParameterException {
		int numReads = stats.getList().get(chromosome, window).intValue();

		double p = stats.getPoisson().evaluate(numReads);
		p = -Math.log(p);

		return p;
	}
}
