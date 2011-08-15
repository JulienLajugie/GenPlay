package edu.yu.einstein.genplay.core.multiGenome.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.genome.Assembly;
import edu.yu.einstein.genplay.core.manager.ProjectManager;


/**
 * This class is used only for my development.
 * 
 * @author Nicolas Fourel
 * @version 0.1
 */
public class Development {

	private static Date delay_start;
	private static Date delays[] = new Date[2];
	private static Map<String, Chromosome> chromosomeList;
	
	
	/**
	 * Fake chromosome list, used for development.
<<<<<<< .working
	 * @return a fake chromosome list
=======
	 * @return a fake list of chromosome
>>>>>>> .merge-right.r413
	 */
	public static Map<String, Chromosome> getFakeChromosomeList () {
		if (chromosomeList == null) {
			chromosomeList = new HashMap<String, Chromosome>();
			
			//NCBI37/hg19
			/*chromosomeList.put("chr1", new Chromosome("chr1", 249250621));
			chromosomeList.put("chr2", new Chromosome("chr2", 243199373));
			chromosomeList.put("chr3", new Chromosome("chr3", 198022430));
			chromosomeList.put("chr4", new Chromosome("chr4", 191154276));
			chromosomeList.put("chr5", new Chromosome("chr5", 180915260));
			chromosomeList.put("chr6", new Chromosome("chr6", 171115067));
			chromosomeList.put("chr7", new Chromosome("chr7", 159138663));
			chromosomeList.put("chr8", new Chromosome("chr8", 146364022));
			chromosomeList.put("chr9", new Chromosome("chr9", 141213431));
			chromosomeList.put("chr10", new Chromosome("chr10", 135534747));
			chromosomeList.put("chr11", new Chromosome("chr11", 135006516));
			chromosomeList.put("chr12", new Chromosome("chr12", 133851895));
			chromosomeList.put("chr13", new Chromosome("chr13", 115169878));
			chromosomeList.put("chr14", new Chromosome("chr14", 107349540));
			chromosomeList.put("chr15", new Chromosome("chr15", 102531392));
			chromosomeList.put("chr16", new Chromosome("chr16", 90354753));
			chromosomeList.put("chr17", new Chromosome("chr17", 81195210));
			chromosomeList.put("chr18", new Chromosome("chr18", 78077248));
			chromosomeList.put("chr19", new Chromosome("chr19", 59128983));
			chromosomeList.put("chr20", new Chromosome("chr20", 63025520));
			chromosomeList.put("chr21", new Chromosome("chr21", 48129895));
			chromosomeList.put("chr22", new Chromosome("chr22", 51304566));
			chromosomeList.put("chrX", new Chromosome("chrX", 155270560));
			chromosomeList.put("chrY", new Chromosome("chrY", 59373566));*/
			
			//NCBI36/hg18
			chromosomeList.put("chr1", new Chromosome("chr1", 247249719));
			chromosomeList.put("chr2", new Chromosome("chr2", 242951149));
			chromosomeList.put("chr3", new Chromosome("chr3", 199501827));
			chromosomeList.put("chr4", new Chromosome("chr4", 191273063));
			chromosomeList.put("chr5", new Chromosome("chr5", 180857866));
			chromosomeList.put("chr6", new Chromosome("chr6", 170899992));
			chromosomeList.put("chr7", new Chromosome("chr7", 158821424));
			chromosomeList.put("chr8", new Chromosome("chr8", 146274826));
			chromosomeList.put("chr9", new Chromosome("chr9", 140273252));
			chromosomeList.put("chr10", new Chromosome("chr10", 135374737));
			chromosomeList.put("chr11", new Chromosome("chr11", 134452384));
			chromosomeList.put("chr12", new Chromosome("chr12", 132349534));
			chromosomeList.put("chr13", new Chromosome("chr13", 114142980));
			chromosomeList.put("chr14", new Chromosome("chr14", 106368585));
			chromosomeList.put("chr15", new Chromosome("chr15", 100338915));
			chromosomeList.put("chr16", new Chromosome("chr16", 88827254));
			chromosomeList.put("chr17", new Chromosome("chr17", 78774742));
			chromosomeList.put("chr18", new Chromosome("chr18", 76117153));
			chromosomeList.put("chr19", new Chromosome("chr19", 63811651));
			chromosomeList.put("chr20", new Chromosome("chr20", 62435964));
			chromosomeList.put("chr21", new Chromosome("chr21", 46944323));
			chromosomeList.put("chr22", new Chromosome("chr22", 49691432));
			chromosomeList.put("chrX", new Chromosome("chrX", 154913754));
			chromosomeList.put("chrY", new Chromosome("chrY", 57772954));
		}
		return chromosomeList;
	}
	
	
	public static void buildFakeCrhomosomeManager () {
		Assembly assembly = new Assembly("NCBI36/hg18", "01 1986");
		assembly.setChromosomeList(getFakeChromosomeList());
		ProjectManager.getInstance().setAssembly(assembly);
	}
	
	
	public static void startDelay () {
		delay_start = new Date();
		//memory();
	}
	
	public static void stopDelay (int pos) {
		Date delay_stop = new Date();
		Date delay = new Date(delay_stop.getTime() - delay_start.getTime());
		delays[pos] = delay;
		delay_start = delay_stop;
		//memory();
	}
	
	public static void showDelay () {
		SimpleDateFormat sdf = new SimpleDateFormat("m 'min' s 'sec' S 'msec'");
		long total = 0;
		for (int i = 0; i < delays.length; i++) {
			total = total + delays[i].getTime();
		}
		//Date all = new Date(total);
		//System.out.println("=== Time counters");
		//System.out.println("total: " + sdf.format(all));
		for (int i = 0; i < delays.length; i++) {
			String info = i + ": " + sdf.format(delays[i]) + " (" + (delays[i].getTime()*100/total) + "%)";
			//System.out.println(info);
			addOutput1(info);
		}
	}
	
	public static void showAddInformation (VariantType type, Integer mgPosition, Integer position, Integer offset, int length) {
		System.out.println("Add: " + type + "; " + mgPosition + "; " + position + "; " + offset + "; " + length);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////// Writing	
	
	private static List<String> output1;
	private static List<String> output2;
	private static List<String> output3;
	
	
	public static void addOutput1 (String info) {
		if (output1 == null) {
			output1 = new ArrayList<String>();
		}
		output1.add(info);
	}
	
	
	public static void addOutput2 (String info) {
		if (output2 == null) {
			output2 = new ArrayList<String>();
		}
		output2.add(info);
	}
	
	
	public static void addOutput3 (String info) {
		if (output3 == null) {
			output3 = new ArrayList<String>();
		}
		output3.add(info);
	}
	
	
	public static void writeOutput1 () {
		String path = "D:\\Documents\\GenPlay\\output1.txt";
		try{
			// Create file 
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i=0; i<output1.size(); i++) {
				out.write(output1.get(i) + "\n");
			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	public static void writeOutput2 () {
		String path = "D:\\Documents\\VCF\\documents\\output2.txt";
		try{
			// Create file 
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i=0; i<output2.size(); i++) {
				out.write(output2.get(i) + "\n");
			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	public static void writeOutput3 () {
		String path = "D:\\Documents\\VCF\\documents\\output3.txt";
		try{
			// Create file 
			FileWriter fstream = new FileWriter(path);
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i=0; i<output3.size(); i++) {
				out.write(output3.get(i) + "\n");
			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////// Stats	
	
	private static int addChromosome = 0;
	private static int lines = 0;
	private static int chrLines = 0;
	private static int allPositions = 0;
	private static int commonPositions = 0;
	private static int differentPositions = 0;
	private static int insertionsVCF = 0;
	private static int deletionsVCF = 0;
	private static int snpsVCF = 0;
	private static int insertionsData = 0;
	private static int deletionsData = 0;
	private static int blanksData = 0;
	private static int blanksAdded = 0;
	private static int index = 0;
	private static int informationLoop = 0;
	private static int validInformationLoop = 0;
	
	
	public static void increaseAddChromosome () {
		addChromosome++;
	}
	
	public static void increaseLines () {
		lines++;
	}
	
	public static void increaseChrLines () {
		chrLines++;
	}
	
	public static void increaseCommonPositions () {
		commonPositions++;
	}
	
	public static void increaseDifferentPositions () {
		differentPositions++;
	}
	
	public static void increaseAllPositions () {
		allPositions++;
	}
	
	public static void increaseInsertionVCF () {
		insertionsVCF++;
	}
	
	public static void increaseDeletionVCF () {
		deletionsVCF++;
	}
	
	public static void increaseSnpsVCF () {
		snpsVCF++;
	}
	
	public static void increaseInsertionData () {
		insertionsData++;
	}
	
	public static void increaseDeletionData () {
		deletionsData++;
	}
	
	public static void increaseBlankData () {
		blanksData++;
	}
	
	public static void increaseBlankAdded () {
		blanksAdded++;
	}
	
	public static void increaseIndex () {
		index++;
	}
	
	public static void increaseInformationLoop () {
		informationLoop++;
	}
	
	public static void increaseValidInformationLoop () {
		validInformationLoop++;
	}
	
	public static void showChrLines () {
		System.out.println("Line: " + chrLines);
		chrLines = 0;
	}
	
	public static void showLines () {
		System.out.println("Line: " + lines);
	}
	
	public static void showStats () {
		System.out.println("=== VCF");
		System.out.println("Add chromosome: " + addChromosome);
		System.out.println("Line: " + lines);
		System.out.println("Insertions: " + insertionsVCF);
		System.out.println("Deletions: " + deletionsVCF);
		System.out.println("SNPs: " + snpsVCF);
		
		System.out.println("=== Position");
		System.out.println("All: " + allPositions);
		System.out.println("Common: " + commonPositions);
		System.out.println("Different: " + differentPositions);
		
		System.out.println("=== Data");
		System.out.println("Insertions: " + insertionsData);
		System.out.println("Deletions: " + deletionsData);
		System.out.println("Blanks: " + blanksData);
		System.out.println("Blanks added: " + blanksAdded);
		
		System.out.println("=== Loop");
		System.out.println("Index: " + index);
		System.out.println("Information: " + informationLoop);
		System.out.println("Valid information: " + validInformationLoop);
	}
	
	
	
	
	
	public static void memory () {
		System.out.println(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	}
	
	
	
	private static int maxInsertion = 0;
	private static int maxDeletion = 0;
	private static Map<Integer, Integer> numInsertions = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> numDeletions = new HashMap<Integer, Integer>();

	public static void setMax (int length, VariantType type) {
		if (type == VariantType.INSERTION) {
			setMaxInsertion(length);
		} else if (type == VariantType.DELETION) {
			setMaxDeletion(length);
		}
	}
	
	private static void setMaxInsertion(int maxInsertion) {
		if (Development.maxInsertion < maxInsertion) {
			Development.maxInsertion = maxInsertion;
		}
	}

	private static void setMaxDeletion(int maxDeletion) {
		if (Development.maxDeletion < maxDeletion) {
			Development.maxDeletion = maxDeletion;
		}
	}
	
	public static void addIndelCount (int length, VariantType type) {
		if (type == VariantType.INSERTION) {
			if (numInsertions.get(length) == null) {
				numInsertions.put(length, 1);
			}
			numInsertions.put(length, numInsertions.get(length)+1);
		} else if (type == VariantType.DELETION) {
			if (numDeletions.get(length) == null) {
				numDeletions.put(length, 1);
			}
			numDeletions.put(length, numDeletions.get(length)+1);
		}
	}
	
	public static void showIndelCounts () {
		System.out.println("Insertions");
		for (Integer length: numInsertions.keySet()) {
			System.out.println(length + ": " + numInsertions.get(length));
		}
		
		System.out.println("Deletions");
		for (Integer length: numDeletions.keySet()) {
			System.out.println(length + ": " + numDeletions.get(length));
		}
	}
	
	
	public static void showMax () {
		System.out.println("Max insertion: " + maxInsertion);
		System.out.println("Max deletion: " + maxDeletion);
	}
	
	
	
	
	
	private static int counter = 0;
	
	
	public static void increaseCounter () {
		counter++;
	}
	
	public static void showCounter () {
		System.out.println("counter: " + counter);
	}
	
}
