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
package edu.yu.einstein.genplay.core.operation.SCWList.simulation;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operation.SCWList.MCWLOInvertMask;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOConvertIntoBinList;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOFilterThreshold;
import edu.yu.einstein.genplay.core.operation.SCWList.SCWLOTwoLayers;
import edu.yu.einstein.genplay.core.operation.binList.BLOGauss;
import edu.yu.einstein.genplay.core.operation.binList.BLOTwoLayers;
import edu.yu.einstein.genplay.core.operation.geneList.GLOScoreFromSCWList;
import edu.yu.einstein.genplay.dataStructure.enums.GeneScoreType;
import edu.yu.einstein.genplay.dataStructure.enums.ScoreOperation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.binList.BinList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;


/**
 * Runs a simulation for a given island size and a given
 * @author Julien Lajugie
 */
public class SCWLORunSimulation implements Operation<SimulationResult> {

	private final int		ISLAND_DISTANCE = 4000000; 	// space between 2 island starts position
	private final int 		islandSize;					// size of the islands to use in the simulation
	private final double 	percentageReadToAdd;		// percentage of reads to add in the S phase in the islands
	private final SCWList 	sList;						// s phase data
	private final SCWList 	g1List;						// g1 phase data


	/**
	 * Creates an instance of {@link SCWLORunSimulation}
	 * @param islandSize size of the islands to use in the simulation
	 * @param percentageReadToAdd percentage of reads to add in the S phase in the islands
	 * @param sList s phase data
	 * @param g1List g1 phase data
	 */
	public SCWLORunSimulation(int islandSize,
			double percentageReadToAdd,
			SCWList sList,
			SCWList g1List) {
		this.islandSize = islandSize;
		this.percentageReadToAdd = percentageReadToAdd;
		this.sList = sList;
		this.g1List = g1List;
	}


	@Override
	public SimulationResult compute() throws Exception {
		// 1 - generate control lists
		SCWList[] resampledList = new ResampleLayers(sList, g1List, 0).compute();
		SCWList controlS = resampledList[0];
		SCWList controlG1 = resampledList[1];

		// 2 - generate sample lists

		// 2a - generate list with reads added
		resampledList = new ResampleLayers(sList, g1List, percentageReadToAdd).compute();
		SCWList resampledSReadAdded = resampledList[0];
		SCWList resampledG1ReadAdded = resampledList[1];

		// 2b - generate list with no reads added
		resampledList = new ResampleLayers(sList, g1List, 0).compute();
		SCWList resampledSNoReadAdded = resampledList[0];
		SCWList resampledG1NoReadAdded = resampledList[1];

		// 2c - generate islands mask list and its inverse
		SCWList islandMask = new GenerateIslands(ISLAND_DISTANCE, islandSize).compute();
		SCWList islandInvertedMask = new MCWLOInvertMask(islandMask).compute();

		// 2d - sum the island with reads added with the baseline
		resampledSReadAdded = new SCWLOTwoLayers(resampledSReadAdded, islandMask, ScoreOperation.MULTIPLICATION).compute();
		resampledSNoReadAdded = new SCWLOTwoLayers(resampledSNoReadAdded, islandInvertedMask, ScoreOperation.MULTIPLICATION).compute();
		SCWList resampledS = new SCWLOTwoLayers(resampledSReadAdded, resampledSNoReadAdded, ScoreOperation.ADDITION).compute();

		resampledG1ReadAdded = new SCWLOTwoLayers(resampledG1ReadAdded, islandMask, ScoreOperation.MULTIPLICATION).compute();
		resampledG1NoReadAdded = new SCWLOTwoLayers(resampledG1NoReadAdded, islandInvertedMask, ScoreOperation.MULTIPLICATION).compute();
		SCWList resampledG1 = new SCWLOTwoLayers(resampledG1ReadAdded, resampledG1NoReadAdded, ScoreOperation.ADDITION).compute();

		// 3 - convert into binlist
		BinList binnedControlS = new SCWLOConvertIntoBinList(controlS, 500, ScoreOperation.ADDITION).compute();
		BinList binnedControlG1 = new SCWLOConvertIntoBinList(controlG1, 500, ScoreOperation.ADDITION).compute();
		BinList binnedResampledS = new SCWLOConvertIntoBinList(resampledS, 500, ScoreOperation.ADDITION).compute();
		BinList binnedResampledG1 = new SCWLOConvertIntoBinList(resampledG1, 500, ScoreOperation.ADDITION).compute();

		// 4 - gauss binlists
		binnedControlS = new BLOGauss(binnedControlS, 400000, false).compute();
		binnedControlG1 = new BLOGauss(binnedControlG1, 400000, false).compute();
		binnedResampledS = new BLOGauss(binnedResampledS, 400000, false).compute();
		binnedResampledG1 = new BLOGauss(binnedResampledG1, 400000, false).compute();

		// 5 - compute S / G1 ratios
		BinList controlSG1 = (BinList) new BLOTwoLayers(binnedControlS, binnedControlG1, ScoreOperation.DIVISION).compute();
		BinList sampleSG1 = (BinList) new BLOTwoLayers(binnedResampledS, binnedResampledG1, ScoreOperation.DIVISION).compute();

		// 6 - compute sample - control difference
		BinList sampleCtrlDifference = (BinList) new BLOTwoLayers(sampleSG1, controlSG1, ScoreOperation.SUBTRACTION).compute();

		// 7 - call islands
		GeneList islands = new FindIslands(sampleCtrlDifference).compute();

		// 8 - score islands
		GeneList controlIslandsS = new GLOScoreFromSCWList(islands, controlS, GeneScoreType.BASE_COVERAGE_SUM).compute();
		GeneList controlIslandsG1 = new GLOScoreFromSCWList(islands, controlG1, GeneScoreType.BASE_COVERAGE_SUM).compute();
		GeneList sampleIslandsS = new GLOScoreFromSCWList(islands, resampledS, GeneScoreType.BASE_COVERAGE_SUM).compute();
		GeneList sampleIslandsG1 = new GLOScoreFromSCWList(islands, resampledG1, GeneScoreType.BASE_COVERAGE_SUM).compute();

		// 9 - compute fisher exact test and retrieve qvalues
		SCWList islandsQValues = new ComputeQValues(controlIslandsS, controlIslandsG1, sampleIslandsS, sampleIslandsG1).compute();

		// 10 - filter islands with qvalue under 0.05
		SCWList filteredIslands = new SCWLOFilterThreshold(islandsQValues, 0f, 0.5f, false).compute();

		// 11 - compute false positive and false negatives
		SimulationResult simulationResult = new ComputeSimulationResult(islandSize, percentageReadToAdd, islandMask, filteredIslands).compute();

		return simulationResult;
	}


	@Override
	public String getDescription() {
		return "Operation: Run Simulation";
	}


	@Override
	public String getProcessingDescription() {
		return "Running Simmulation";
	}


	@Override
	public int getStepCount() {
		return 0;
	}


	@Override
	public void stop() {
		// stop operation not implemented
	}
}
