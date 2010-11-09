/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.SNPList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import yu.einstein.gdp2.core.SNP;
import yu.einstein.gdp2.core.list.DisplayableListOfLists;


/**
 * A list of SNPs
 * @author Julien Lajugie
 * @version 0.1
 */
public class SNPList extends DisplayableListOfLists<SNP, List<SNP>> implements Serializable {

	private static final long serialVersionUID = 5581991460611158113L;	// generated ID

	
	/**
	 * Creates an instance of {@link SNPList} containing the specified data.
	 * @param data data of the list 
	 */
	public SNPList(Collection<? extends List<SNP>> data) {
		addAll(data);
		// add the eventual missing chromosomes
		if (size() < chromosomeManager.size()) {
			for (int i = size(); i < chromosomeManager.size(); i++){
				add(null);
			}
		}
		// sort the data
		sort();
	}
	
	
	@Override
	protected void fitToScreen() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected List<SNP> getFittedData(int start, int stop) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Sorts the SNPList
	 */
	public void sort() {
		for (List<SNP> currentList: this) {
			if (currentList != null) {
				Collections.sort(currentList);
			}
		}
	}
	
	
	/**
	 * Recursive and dichotomic search algorithm.  
	 * @param list List in which the search is performed.
	 * @param value Searched value
	 * @param indexStart Start index where to look for the value.
	 * @param indexStop Stop index where to look for the value.
	 * @return The index of a gene with a position start equals to value. 
	 * Index of the first gene with a start position superior to value if nothing found.
	 */
	private int findSNP(List<SNP> list, int value, int indexStart, int indexStop) {
		int middle = (indexStop - indexStart) / 2;
		if (indexStart == indexStop) {
			return indexStart;
		} else if (value == list.get(indexStart + middle).getPosition()) {
			return indexStart + middle;
		} else if (value > list.get(indexStart + middle).getPosition()) {
			return findSNP(list, value, indexStart + middle + 1, indexStop);
		} else {
			return findSNP(list, value, indexStart, indexStart + middle);
		}
	}
	
	
	/**
	 * Performs a deep clone of the current SNPList
	 * @return a new SNPList
	 */
	public SNPList deepClone() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ((SNPList) ois.readObject());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
