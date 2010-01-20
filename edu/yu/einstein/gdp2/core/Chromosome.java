/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core;

import java.io.Serializable;


/**
 * The Chromosome class represents a chromosome with a name and a length.
 * @author Julien Lajugie
 * @version 0.1
 */
public final class Chromosome implements Cloneable, Serializable {

	private static final long serialVersionUID = -8339402742378578413L; // generated ID
	private String name;	// Name of the chromosome
	private int length;	// Length of the chromosome


	/**
	 * Constructor. Creates an instance of a Chromosome.
	 * @param name Name of the chromosome.
	 * @param length Length of the chromosome.
	 */
	public Chromosome(String name, int length) {
		this.setName(name);
		this.length = length;
	}


	/**
	 * @param length the length of a chromosome to set
	 */
	public void setLength(int length) {
		this.length = length;
	}


	/**
	 * @return the length of a chromosome
	 */
	public int getLength() {
		return length;
	}


	/**
	 * @param name the name of a chromosome to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the name of a chromosome
	 */
	public String getName() {
		return name;
	}


	@Override
	public String toString() {
		return name;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Chromosome other = (Chromosome) obj;
		if (length != other.length) {
			return false;
		}
		if (name == null) { 
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
