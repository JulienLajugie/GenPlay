/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.duplicateReadFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to sort the file based on chromosome names and start values
 * @author Chirag Gorasia
 * @version 0.1
 */
public class BedFileSorter implements Comparable<BedFileSorter>{

	private String readName;
	private String chromosomeName;														// chromosome name
	private int start;																	// start position
	private int stop;																	// stop position
	private double score;	
	private String strand;
	private static Map<String,Integer> chromoValue = new HashMap<String, Integer>();	// map containing chromosome numbers indexed by chromosome names
	
	static {
		chromoValue.put("chr1",1);
		chromoValue.put("chr1_gl000191_random",2);
		chromoValue.put("chr1_gl000192_random",3);
		chromoValue.put("chr2",4);
		chromoValue.put("chr3",5);
		chromoValue.put("chr4",6);
		chromoValue.put("chr4_ctg9_hap1",7);
		chromoValue.put("chr4_gl000193_random",8);
		chromoValue.put("chr4_gl000194_random",9);
		chromoValue.put("chr5",10);
		chromoValue.put("chr6",11);
		chromoValue.put("chr6_apd_hap1",12);
		chromoValue.put("chr6_cox_hap2",13);
		chromoValue.put("chr6_dbb_hap3",14);
		chromoValue.put("chr6_mann_hap4",15);
		chromoValue.put("chr6_mcf_hap5",16);
		chromoValue.put("chr6_qbl_hap6",17);
		chromoValue.put("chr6_ssto_hap7",18);
		chromoValue.put("chr7",19);
		chromoValue.put("chr7_gl000195_random",20);
		chromoValue.put("chr8",21);
		chromoValue.put("chr8_gl000196_random",22);
		chromoValue.put("chr8_gl000197_random",23);
		chromoValue.put("chr9",24);
		chromoValue.put("chr9_gl000198_random",25);
		chromoValue.put("chr9_gl000199_random",26);
		chromoValue.put("chr9_gl000200_random",27);
		chromoValue.put("chr9_gl000201_random",28);
		chromoValue.put("chr10",29);
		chromoValue.put("chr11",30);
		chromoValue.put("chr11_gl000202_random",31);
		chromoValue.put("chr12",32);
		chromoValue.put("chr13",33);
		chromoValue.put("chr14",34);
		chromoValue.put("chr15",35);
		chromoValue.put("chr16",36);
		chromoValue.put("chr17",37);
		chromoValue.put("chr17_ctg5_hap1",38);
		chromoValue.put("chr17_gl000203_random",39);
		chromoValue.put("chr17_gl000204_random",40);
		chromoValue.put("chr17_gl000205_random",41);
		chromoValue.put("chr17_gl000206_random",42);
		chromoValue.put("chr18",43);
		chromoValue.put("chr18_gl000207_random",44);
		chromoValue.put("chr19",45);
		chromoValue.put("chr19_gl000208_random",46);
		chromoValue.put("chr19_gl000209_random",47);
		chromoValue.put("chr20",48);
		chromoValue.put("chr21",49);
		chromoValue.put("chr21_gl000210_random",50);
		chromoValue.put("chr22",51);
		chromoValue.put("chrX",52);
		chromoValue.put("chrY",53);
		chromoValue.put("chrUn_gl000211",54);
		chromoValue.put("chrUn_gl000212",55);
		chromoValue.put("chrUn_gl000213",56);
		chromoValue.put("chrUn_gl000214",57);
		chromoValue.put("chrUn_gl000215",58);
		chromoValue.put("chrUn_gl000216",59);
		chromoValue.put("chrUn_gl000217",60);
		chromoValue.put("chrUn_gl000218",61);
		chromoValue.put("chrUn_gl000219",62);
		chromoValue.put("chrUn_gl000220",63);
		chromoValue.put("chrUn_gl000221",64);
		chromoValue.put("chrUn_gl000222",65);
		chromoValue.put("chrUn_gl000223",66);
		chromoValue.put("chrUn_gl000224",67);
		chromoValue.put("chrUn_gl000225",68);
		chromoValue.put("chrUn_gl000226",69);
		chromoValue.put("chrUn_gl000227",70);
		chromoValue.put("chrUn_gl000228",71);
		chromoValue.put("chrUn_gl000229",72);
		chromoValue.put("chrUn_gl000230",73);
		chromoValue.put("chrUn_gl000231",74);
		chromoValue.put("chrUn_gl000232",75);
		chromoValue.put("chrUn_gl000233",76);
		chromoValue.put("chrUn_gl000234",77);
		chromoValue.put("chrUn_gl000235",78);
		chromoValue.put("chrUn_gl000236",79);
		chromoValue.put("chrUn_gl000237",80);
		chromoValue.put("chrUn_gl000238",81);
		chromoValue.put("chrUn_gl000239",82);
		chromoValue.put("chrUn_gl000240",83);
		chromoValue.put("chrUn_gl000241",84);
		chromoValue.put("chrUn_gl000242",85);
		chromoValue.put("chrUn_gl000243",86);
		chromoValue.put("chrUn_gl000244",87);
		chromoValue.put("chrUn_gl000245",88);
		chromoValue.put("chrUn_gl000246",89);
		chromoValue.put("chrUn_gl000247",90);
		chromoValue.put("chrUn_gl000248",91);
		chromoValue.put("chrUn_gl000249",92);
		chromoValue.put("chrM",93);		
	}
	
	
	/**
	 * Creates an instance of {@link BedFileSorter}
	 * @param readName name of the read
	 * @param chromosomeName name of the chromosome of the read
	 * @param start start position of the read
	 * @param stop stop position of the read
	 * @param score score of the read
	 * @param strand strand of the read
	 */
	public BedFileSorter(String readName, String chromosomeName, int start, int stop, double score, String strand) {
		this.readName = readName;
		this.chromosomeName = chromosomeName;
		this.start = start;
		this.stop = stop;
		this.score = score;		
		this.strand = strand;
	}
	
	/**
	 * Returns the chromosomeNumber 
	 * @return chromosomeNumber
	 */
	public int getChromosomeNumber() {
		if (!chromoValue.containsKey(getChromosomeName())) {
			return 0;
		}
		return chromoValue.get(getChromosomeName());
	}
	

	/**
	 * @return the readName
	 */
	public String getReadName() {
		return readName;
	}
	
	/**
	 * Returns the chromosmeName			
	 * @return chromosomeName
	 */
	public String getChromosomeName() {
		return chromosomeName;
	}
	
	/**
	 * Returns the start position
	 * @return start
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Returns the stop position
	 * @return stop
	 */
	public int getStop() {
		return stop;
	}
	
	/**
	 * Returns the score
	 * @return score
	 */
	public double getScore() {
		return score;
	}
	
	/**
	 * @return the strand
	 */
	public String getStrand() {
		return strand;
	}

	@Override
	public int compareTo(BedFileSorter o) {
		if (this.getChromosomeNumber() > o.getChromosomeNumber()) {
			return 1;
		} else if (this.getChromosomeNumber() < o.getChromosomeNumber()) {
			return -1;
		} else { 
			if (this.start > o.start) {
				return 1;
			} else if (this.start < o.start) {
				return -1;
			} else {
				if (this.stop > o.stop) {
					return 1;
				} else if (this.stop < o.stop) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}
}
