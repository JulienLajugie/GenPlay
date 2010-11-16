/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.SNPList.operation;

import java.util.List;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.SNPList.SNPList;
import yu.einstein.gdp2.core.operation.Operation;


/**
 * Finds the previous SNP from a specified position 
 * @author Julien Lajugie
 * @version 0.1
 */
public class SLOFindPrevious implements Operation<SNP> {

	private final SNPList 		inputList;	// input list containing the SNPs
	private final Chromosome 	chromosome;	// chromosome of the starting loci
	private final int 			position;	// position of the starting loci
	
	
	/**
	 * Creates an instance of {@link SLOFindPrevious}
	 * @param inputList input list containing the SNPs
	 * @param chromosome {@link Chromosome} of the starting loci
	 * @param position position of the starting loci
	 */
	public SLOFindPrevious(SNPList inputList, Chromosome chromosome, int position) {
		this.inputList = inputList;
		this.chromosome = chromosome;
		this.position = position;
	}
	
	
	@Override
	public SNP compute() throws Exception {
		List<SNP> list = inputList.get(chromosome);
		int previousSNPIndex = SNPList.findSNP(list, position, 0, list.size() - 1) - 1;
		// the previous SNP of the first SNP is the first element itself
		if (previousSNPIndex < 0) {
			return null;
		} else {
			return list.get(previousSNPIndex);
		}
	}

	@Override
	public String getDescription() {
		return "Search Previous SNP";
	}

	@Override
	public String getProcessingDescription() {
		return "Searching previous SNP";
	}

	@Override
	public int getStepCount() {
		return 1;
	}

	
	/**
	 * Does nothing
	 */
	@Override
	public void stop() {}
}
