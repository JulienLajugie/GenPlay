/**
 * @author Julien Lajugie
 * @version 0.1
 */
package yu.einstein.gdp2.core.DASconnector;

import yu.einstein.gdp2.core.enums.Strand;


/**
 * An Entry Point as described in the DAS 1.53 specifications:
 * <br/><a href="http://www.biodas.org/documents/spec.html">http://www.biodas.org/documents/spec.html</a>
 * @author Julien Lajugie
 * @version 0.1
 */
public class EntryPoint {
	private String 	ID;			// ID of the entry point
	private int 	start;		// start position of the entry point 
	private int 	stop;		// stop position of the entry point
	private Strand 	orientation;// orientation of the entry point
	
	
	/**
	 * @return the iD
	 */
	public final String getID() {
		return ID;
	}
	
	
	/**
	 * @param iD the iD to set
	 */
	public final void setID(String iD) {
		ID = iD;
	}
	
	
	/**
	 * @return the start
	 */
	public final int getStart() {
		return start;
	}
	
	
	/**
	 * @param start the start to set
	 */
	public final void setStart(int start) {
		this.start = start;
	}
	
	
	/**
	 * @return the stop
	 */
	public final int getStop() {
		return stop;
	}
	
	
	/**
	 * @param stop the stop to set
	 */
	public final void setStop(int stop) {
		this.stop = stop;
	}
	
	
	/**
	 * @return the orientation
	 */
	public final Strand getOrientation() {
		return orientation;
	}
	
	
	/**
	 * @param strand the orientation to set
	 */
	public final void setOrientation(Strand orientation) {
		this.orientation = orientation;
	}
}
