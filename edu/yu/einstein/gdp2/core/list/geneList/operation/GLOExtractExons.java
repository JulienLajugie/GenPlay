/**
 * @author Chirag Gorasia
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import yu.einstein.gdp2.core.Gene;
import yu.einstein.gdp2.core.enums.Strand;
import yu.einstein.gdp2.core.list.geneList.GeneList;
import yu.einstein.gdp2.core.operation.Operation;
import yu.einstein.gdp2.core.operationPool.OperationPool;

/**
 * Extracts exons
 * @author Chirag Gorasia
 * @version 0.1
 */
public class GLOExtractExons implements Operation<GeneList> {

	private final GeneList geneList;			// input list
	private final int exonOption;				// exon option: first, last or all
	
	public static final int FIRST_EXON = 0;
	public static final int LAST_EXON = 1;
	public static final int ALL_EXONS = 2;
	
	/**
	 * Creates an instance of {@link GLOExtractExons}
	 * @param geneList
	 */
	public GLOExtractExons(GeneList geneList, int exonOption) {
		this.geneList = geneList;		
		this.exonOption = exonOption;
	}
	
	@Override
	public GeneList compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<List<Gene>>> threadList = new ArrayList<Callable<List<Gene>>>();
		
		for(short i = 0; i < geneList.size(); i++) {
			final List<Gene> currentList = geneList.get(i);
						
			Callable<List<Gene>> currentThread = new Callable<List<Gene>>() {
				@Override
				public List<Gene> call() throws Exception {					
					if (currentList == null) {
						return null;
					}
					List<Gene> resultExonList = new ArrayList<Gene>();
					int[] exonStart;
					int[] exonStop;
					double[] exonScore;
					List<Gene> smallerGenes = null;
					for (int j = 0; j < currentList.size(); j++) {
						Gene currentGene = currentList.get(j); 
						Gene geneToAdd = new Gene(currentList.get(j));
						
						switch (exonOption) {
						case FIRST_EXON:
							if (currentGene.getStrand() == Strand.FIVE) {
								geneToAdd.setTxStart(geneToAdd.getExonStarts()[0]);
								geneToAdd.setTxStop(geneToAdd.getExonStops()[0]);
								exonStart = new int[1];
								exonStart[0] = geneToAdd.getExonStarts()[0];
								exonStop = new int[1];
								exonStop[0] = geneToAdd.getExonStops()[0];
								geneToAdd.setExonStarts(exonStart);
								geneToAdd.setExonStops(exonStop);
								geneToAdd.setName(geneToAdd.getName() + "(F)");
							} else {
								geneToAdd.setTxStart(geneToAdd.getExonStarts()[geneToAdd.getExonStarts().length - 1]);
								geneToAdd.setTxStop(geneToAdd.getExonStops()[geneToAdd.getExonStops().length - 1]);
								exonStart = new int[1];
								exonStart[0] = geneToAdd.getExonStarts()[geneToAdd.getExonStarts().length - 1];
								exonStop = new int[1];
								exonStop[0] = geneToAdd.getExonStops()[geneToAdd.getExonStops().length - 1];
								geneToAdd.setExonStarts(exonStart);
								geneToAdd.setExonStops(exonStop);
								geneToAdd.setName(geneToAdd.getName() + "(F)");
							} break;
							
						case LAST_EXON:
							if (currentGene.getStrand() == Strand.FIVE) {
								geneToAdd.setTxStart(geneToAdd.getExonStarts()[geneToAdd.getExonStarts().length - 1]);
								geneToAdd.setTxStop(geneToAdd.getExonStops()[geneToAdd.getExonStops().length - 1]);
								exonStart = new int[1];
								exonStart[0] = geneToAdd.getExonStarts()[geneToAdd.getExonStarts().length - 1];
								exonStop = new int[1];
								exonStop[0] = geneToAdd.getExonStops()[geneToAdd.getExonStops().length - 1];
								geneToAdd.setExonStarts(exonStart);
								geneToAdd.setExonStops(exonStop);
								geneToAdd.setName(geneToAdd.getName() + "(L)");
							} else {
								geneToAdd.setTxStart(geneToAdd.getExonStarts()[0]);
								geneToAdd.setTxStop(geneToAdd.getExonStops()[0]);
								exonStart = new int[1];
								exonStart[0] = geneToAdd.getExonStarts()[0];
								exonStop = new int[1];
								exonStop[0] = geneToAdd.getExonStops()[0];
								geneToAdd.setExonStarts(exonStart);
								geneToAdd.setExonStops(exonStop);
								geneToAdd.setName(geneToAdd.getName() + "(L)");
							} break;
							
						case ALL_EXONS:
							Gene smallerGene;
							smallerGenes = new ArrayList<Gene>();
							if (currentGene.getStrand() == Strand.FIVE) {								
								for (int i = 0; i < geneToAdd.getExonStarts().length; i++) {
									smallerGene = new Gene(geneToAdd);
									smallerGene.setTxStart(smallerGene.getExonStarts()[i]);
									smallerGene.setTxStop(smallerGene.getExonStops()[i]);
									exonStart = new int[1];
									exonStart[0] = smallerGene.getExonStarts()[i];
									exonStop = new int[1];
									exonStop[0] = smallerGene.getExonStops()[i];
									if (smallerGene.getExonScores().length == 1) {
										exonScore = new double[1];
										exonScore[0] = smallerGene.getExonScores()[0];
									} else if (smallerGene.getExonScores().length > 1) {
										exonScore = new double[1];
										exonScore[0] = smallerGene.getExonScores()[i];
									}
									smallerGene.setExonStarts(exonStart);
									smallerGene.setExonStops(exonStop);
									smallerGene.setName(geneToAdd.getName() + "(" + Integer.toString(i+1) + ")");
									
									smallerGenes.add(smallerGene);
								}
							} else {
								for (int i = geneToAdd.getExonStarts().length - 1; i >= 0; i--) {
									smallerGene = new Gene(geneToAdd);
									smallerGene.setTxStart(smallerGene.getExonStarts()[i]);
									smallerGene.setTxStop(smallerGene.getExonStops()[i]);
									exonStart = new int[1];
									exonStart[0] = smallerGene.getExonStarts()[i];
									exonStop = new int[1];
									exonStop[0] = smallerGene.getExonStops()[i];
									if (smallerGene.getExonScores().length == 1) {
										exonScore = new double[1];
										exonScore[0] = smallerGene.getExonScores()[0];
									} else if (smallerGene.getExonScores().length > 1) {
										exonScore = new double[1];
										exonScore[0] = smallerGene.getExonScores()[i];
									}
									smallerGene.setExonStarts(exonStart);
									smallerGene.setExonStops(exonStop);
									smallerGene.setName(geneToAdd.getName() + "(" + Integer.toString(geneToAdd.getExonStarts().length-i) + ")");
									smallerGenes.add(smallerGene);
								}
							} break;

						default:
							// invalid argument
							throw new IllegalArgumentException("Invalid Choice for Exon");
						}
						if (exonOption == ALL_EXONS) {
							resultExonList.addAll(smallerGenes);
						} else {
						resultExonList.add(geneToAdd);
						}
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return resultExonList;
				}
			};
			threadList.add(currentThread);
		}
		List<List<Gene>> result = op.startPool(threadList);
		if (result == null) {
			return null;
		} else {
			return new GeneList(result, geneList.getSearchURL());
		}
	}

	@Override
	public String getDescription() {
		return "Operation: Extract Exons";
	}

	@Override
	public String getProcessingDescription() {
		return "Extracting Exons";
	}

	@Override
	public int getStepCount() {
		return 0;
	}	
}