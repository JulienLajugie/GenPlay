package edu.yu.einstein.genplay.dataStructure.list.genomeWideList.repeatFamilyList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.chromosomeWindow.ChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.repeatListView.RepeatFamilyListView;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;

public class SimpleRepeatFamilyList implements RepeatFamilyList, Serializable {

	/** Generated serial ID */
	private static final long serialVersionUID = -7553643226353657650L;

	@Override
	public ListView<List<RepeatFamilyListView>> get(Chromosome chromosome) throws InvalidChromosomeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RepeatFamilyListView> get(Chromosome chromosome, int index) throws InvalidChromosomeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RepeatFamilyListView> get(int chromosomeIndex, int elementIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size(Chromosome chromosome) throws InvalidChromosomeException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int size(int chromosomeIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<ListView<List<RepeatFamilyListView>>> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListView<List<RepeatFamilyListView>> get(int elementIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int findStart(List<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int findStop(List<ChromosomeWindow> list, int value, int indexStart, int indexStop) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void fitToScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected List<RepeatFamilyListView> getFittedData(int start, int stop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeObject(ObjectOutputStream out) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
