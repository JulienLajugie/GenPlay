package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosome;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;

/**
 * Simple implementation of the {@link RepeatFamilyList} interface.
 * @author Julien Lajugie
 */
public class SimpleRepeatFamilyList implements RepeatFamilyList, Serializable, Iterator<ListView<RepeatFamilyListView>> {

	/** Generated serial ID */
	private static final long serialVersionUID = 5575142659472215610L;

	/** Version number of the class */
	private static final transient int CLASS_VERSION_NUMBER = 0;

	/** {@link GenomicDataArrayList} containing the Genes */
	private final List<ListView<RepeatFamilyListView>> data;

	/** Array containing the names of all the families present in the list in alphabetical order  */
	private final String[] familyNames;

	/** Current index of the iterator */
	private transient int iteratorIndex = 0;


	/**
	 * Creates an instance of {@link SimpleRepeatFamilyList}
	 * @param data {@link ListView} of {@link RepeatFamilyListView} organized by chromosome
	 */
	public SimpleRepeatFamilyList(List<ListView<RepeatFamilyListView>> data) {
		super();
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		this.data = new ArrayList<ListView<RepeatFamilyListView>>();
		for (int i = 0; i < data.size(); i++){
			data.add(data.get(i));
		}
		familyNames = retrieveFamilyNames();
	}


	@Override
	public ListView<RepeatFamilyListView> get(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex);
	}


	@Override
	public RepeatFamilyListView get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int chromosomeIndex = projectChromosome.getIndex(chromosome);
		return get(chromosomeIndex, index);
	}

	@Override
	public ListView<RepeatFamilyListView> get(int chromosomeIndex) {
		return data.get(chromosomeIndex);
	}


	@Override
	public RepeatFamilyListView get(int chromosomeIndex, int elementIndex) {
		return data.get(chromosomeIndex).get(elementIndex);
	}


	@Override
	public String[] getFamilyNames() {
		return familyNames;
	}


	@Override
	public boolean hasNext() {
		return iteratorIndex < size();
	}


	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}


	@Override
	public Iterator<ListView<RepeatFamilyListView>> iterator() {
		return this;
	}


	@Override
	public ListView<RepeatFamilyListView> next() {
		int currentIndex = iteratorIndex;
		iteratorIndex++;
		return get(currentIndex);
	}


	/**
	 * Method used for deserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		// read final fields
		in.defaultReadObject();
	}


	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}


	private String[] retrieveFamilyNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int size() {
		return data.size();
	}


	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		ProjectChromosome projectChromosome = ProjectManager.getInstance().getProjectChromosome();
		int index = projectChromosome.getIndex(chromosome);
		return size(index);
	}

	@Override
	public int size(int chromosomeIndex) {
		return data.get(chromosomeIndex).size();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CLASS_VERSION_NUMBER);
		out.defaultWriteObject();
	}
}
