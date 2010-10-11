/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.extractor;

import yu.einstein.gdp2.core.Chromosome;
import yu.einstein.gdp2.core.enums.Strand;


/**
 * Interface that should be implemented by all the extractor that have an information on the strand
 * @author Julien Lajugie
 * @version 0.1
 */
public interface StrandedExtractor {

	/**
	 * @return true if the specified strand is selected
	 */
	public boolean isStrandSelected(Strand aStrand);
	
	
	/**
	 * @param strandToSelect select the specified strand. Set the parameter to null to select both strands
	 */
	public void selectStrand(Strand strandToSelect);
	
	
	/**
	 * @param shiftValue shift value to set
	 */
	public void setStrandShift(int shiftValue);
	

	/**
	 * Returns the shifted position on a specified chromosome and a specified strand
	 * @param strand current {@link Strand}
	 * @param chromosome current {@link Chromosome}
	 * @param position a position
	 */
	public int getShiftedPosition(Strand strand, Chromosome chromosome, int position);
}
