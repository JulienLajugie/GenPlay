package edu.yu.einstein.genplay.core.operation.geneList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.geneListView.GeneListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.ListOfListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.SimpleGeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListOfListViewsIterator;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;

/**
 * Creates a new {@link GeneList} object containing the element of the 2 specified {@link GeneList}.
 *
 * @author Julien Lajugie
 */
public class GLOMergeGeneLists implements Operation<GeneList> {

	private boolean			stopped = false;	// true if the operation must be stopped
	private final GeneList 	geneList1;			// 1st genelist to merge
	private final GeneList 	geneList2;			// 2nd genelist to merge


	/**
	 * Creates an instance of {@link GLOMergeGeneLists}
	 * @param geneList1
	 * @param geneList2
	 */
	public GLOMergeGeneLists(GeneList geneList1, GeneList geneList2) {
		this.geneList1 = geneList1;
		this.geneList2 = geneList2;
	}


	@Override
	public GeneList compute() throws Exception {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		ListViewBuilder<Gene> lvbPrototype = new GeneListViewBuilder();
		final ListOfListViewBuilder<Gene> resultListBuilder = new ListOfListViewBuilder<Gene>(lvbPrototype);

		for(final Chromosome currentChromosome : projectChromosomes) {
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					List<ListView<Gene>> listOfLV = new ArrayList<ListView<Gene>>();
					listOfLV.add(geneList1.get(currentChromosome));
					listOfLV.add(geneList2.get(currentChromosome));
					ListOfListViewsIterator<Gene> listOfLVIterator = new ListOfListViewsIterator<Gene>(listOfLV);
					while (listOfLVIterator.hasNext() && !stopped) {
						resultListBuilder.addElementToBuild(currentChromosome, listOfLVIterator.next());
					}
					op.notifyDone();
					return null;
				}
			};
			threadList.add(currentThread);
		}
		op.startPool(threadList);
		List<ListView<Gene>> data = resultListBuilder.getGenomicList();
		return new SimpleGeneList(data, geneList1.getGeneScoreType(), geneList1.getGeneDBURL());
	}


	@Override
	public String getDescription() {
		return "Operation: Merge gene layers";
	}


	@Override
	public String getProcessingDescription() {
		return "Merging Gene Layers";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
