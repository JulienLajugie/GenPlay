package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Combines the scores of the C and G sites
 * @author Julien Lajugie
 */
public class SCWLOCombineCsAndGs implements Operation<SCWList> {

	private boolean					stopped = false;	// true if the operation must be stopped
	private final SCWList 			inputList; 			// input SCW list
	private final NucleotideList	referenceSequence;	// nucleotide list containing the reference sequence


	/**
	 * Creates an instance of {@link SCWLOCombineCsAndGs}
	 * @param inputList
	 * @param referenceSequence
	 */
	public SCWLOCombineCsAndGs(SCWList inputList, NucleotideList referenceSequence) {
		this.inputList = inputList;
		this.referenceSequence = referenceSequence;
	}


	@Override
	public SCWList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(inputList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if ((currentList != null) && (currentList.size() != 0)) {
						int lastStop = 0;
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							int start = currentList.get(i).getStart();
							if ((start - 1) >= lastStop) {
								start--;
							}
							int stop = Math.min(currentList.get(i).getStop(), chromosome.getLength() - 1);
							for (int currentPosition = start; currentPosition < stop; currentPosition++) {

								if (isCG(chromosome, currentPosition)) {

								}

							}
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return resultListBuilder.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Combine Cs and Gs methylation profiles";
	}


	@Override
	public String getProcessingDescription() {
		return "Combining Cs and Gs methylation profiles";
	}


	@Override
	public int getStepCount() {
		return 1 + inputList.getCreationStepCount();
	}


	/**
	 * @param chromosome
	 * @param currentPosition
	 * @return true if the specified position is the C of a CG subsequence
	 */
	protected boolean isCG(Chromosome chromosome, int currentPosition) {
		Nucleotide currentBase = referenceSequence.get(chromosome, currentPosition);
		Nucleotide nextBase = referenceSequence.get(chromosome, currentPosition + 1);
		return (currentBase == Nucleotide.CYTOSINE) && (nextBase == Nucleotide.GUANINE);
	}

	@Override
	public void stop() {
		stopped = true;
	}
}
