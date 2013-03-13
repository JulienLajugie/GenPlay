package edu.yu.einstein.genplay.core.list.ScoredWindowList;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.chromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.core.enums.DataPrecision;
import edu.yu.einstein.genplay.core.enums.ScoreCalculationMethod;
import edu.yu.einstein.genplay.core.list.GenomicDataList;
import edu.yu.einstein.genplay.core.reader.SCWReader;

public class ScoredWindowListFactory {


	public static GenomicDataList<Double> createBinList(int binSize, DataPrecision dataPrecision, ScoreCalculationMethod scoreCalculationMethod,
			SCWReader reader) {

		BinList binList = new BinList(binSize, dataPrecision);
		ScoredChromosomeWindow currentWindow = null;
		while ((currentWindow = reader.readScoredChromosomeWindow()) != null) {
			Chromosome currentChromosome = reader.getCurrentChromosome();
			//binList.add(currentChromosome, currentWindow, scoreCalculationMethod,)
		}
		return binList;
	}

}
