package edu.yu.einstein.genplay.core.operation.SCWList;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.enums.Nucleotide;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.nucleotideList.NucleotideList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Generates a new {@link SCWList} containing only the values on CG sub sequences.
 * The result is a list of windows covering the CG sequence and having the sum of the
 * score on the C and on the G base
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
		final SCWListBuilder resultListBuilder = new SCWListBuilder(inputList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = inputList.get(chromosome);
			if ((currentList != null) && (currentList.size() != 0)) {
				int lastStop = 0;
				for (int i = 0; (i < currentList.size()) && !stopped; i++) {
					int start = currentList.get(i).getStart();
					if ((start - 1) >= lastStop) {
						start--;
					}
					int stop = Math.min(currentList.get(i).getStop(), chromosome.getLength() - 1);
					int currentPosition;
					for (currentPosition = start; currentPosition < stop; currentPosition++) {
						if (isCG(chromosome, currentPosition)) {
							int cPosition = currentPosition;
							int gPosition = currentPosition + 1;
							float score = 0;
							if (cPosition >= currentList.get(i).getStart()) {
								score += currentList.get(i).getScore();
							}
							if (gPosition < currentList.get(i).getStop()) {
								score += currentList.get(i).getScore();
							} else if (((i + 1) < currentList.size()) && (gPosition == currentList.get(i + 1).getStart())) {
								score += currentList.get(i + 1).getScore();
							}
							if (score != 0) {
								resultListBuilder.addElementToBuild(chromosome, cPosition, gPosition + 1, score);
							}
							currentPosition++; // skip the G
						}
					}
					lastStop = currentPosition;
				}
			}
		}
		return resultListBuilder.getSCWList();
	}


	@Override
	public String getDescription() {
		return "Operation: Compute CG methylation profiles";
	}


	@Override
	public String getProcessingDescription() {
		return "Computing CG methylation profile";
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
