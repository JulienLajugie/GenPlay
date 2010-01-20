/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The RepeatFamily class provides a representation of a family of repeats.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class RepeatFamily implements Serializable, Comparable<RepeatFamily> {

	private static final long serialVersionUID = -7691967168795920365L; // generated ID
	private String 						name;			// Name of the family of repeat
	private ArrayList<ChromosomeWindow> repeatList;		// 1 list of repeat per chromosome
	
	
	/**
	 * Creates an instance of {@link RepeatFamily}
	 * @param name name of the family
	 */
	public RepeatFamily(String name) {
		this.name = name; 
		repeatList = new ArrayList<ChromosomeWindow>();
	}
	
	
	/**
	 * @return the number of repeats
	 */
	public int repeatCount() {
		return repeatList.size();
	}
	
	
	/**
	 * Adds a repeat to the list
	 * @param repeat a repeat
	 */
	public void addRepeat(ChromosomeWindow repeat) {
		repeatList.add(repeat);
	}
	
	
	/**
	 * Returns the repeat at the specified position in this list. 
	 * @param index index of the repeat to return 
	 * @return the repeat at the specified position in this list 
	 */
	public ChromosomeWindow getRepeat(int index) {
		return repeatList.get(index);
	}
	
	
	/**
	 * @return the list of repeat
	 */
	public ArrayList<ChromosomeWindow> getRepeatList() {
		return repeatList;
	}
	
	
	/**
	 * @return the name of the family
	 */
	public String getName() {
		return name;
	}


	@Override
	public int compareTo(RepeatFamily otherRepeatFamily) {
		return this.getName().compareTo(otherRepeatFamily.getName());
	}
}
