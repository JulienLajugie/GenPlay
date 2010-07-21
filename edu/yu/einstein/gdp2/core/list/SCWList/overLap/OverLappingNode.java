/**
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
package yu.einstein.gdp2.core.list.SCWList.overLap;

import yu.einstein.gdp2.core.ScoredChromosomeWindow;

/**
 * An overlap node is node on an overlapping region
 * A node is composed of a scored chromosome window and a boolean to know if it refers to a start or stop position.
 * 
 * @author Nicolas
 * @version 0.1
 */
final class OverLappingNode implements Comparable<OverLappingNode> {
	
	private boolean 				start;	//the node refer to a start position
	private ScoredChromosomeWindow 	scw;	//scored chromosome window
	
	/**
	 * OverLapNode constructor
	 * 
	 * @param start	true if the node refers to a start position
	 * @param scw	scored chromosome window associated
	 */
	protected OverLappingNode(boolean start, ScoredChromosomeWindow scw) {
		this.start = start;
		this.scw = scw;
	}
	
	/**
	 * isStart method
	 * This method allows to know if the node refers to a start or a stop position.
	 * 
	 * @return	start boolean value
	 */
	protected boolean isStart() {
		return this.start;
	}
	
	/**
	 * getScw method
	 * 
	 * @return the scored chromosome window of the node
	 */
	protected ScoredChromosomeWindow getScw() {
		return scw;
	}
	
	/**
	 * getValue method
	 * This method return the position of the node.
	 * If the node refers to a start, the start position of the scored chromosome window is returned.
	 * If it refers to a stop, the stop position of the scored chromosome window is returned.
	 * 
	 * @return position associated
	 */
	protected int getValue () {
		if (this.start) {
			return this.scw.getStart();
		} else {
			return this.scw.getStop();
		}
	}
	
	@Override
	public int compareTo(OverLappingNode arg) {
		if (this.getValue() > arg.getValue()) {
			return 1;
		} else if (this.getValue() == arg.getValue()) {
			return 0;
		} else {
			return -1;
		}
	}
	
}