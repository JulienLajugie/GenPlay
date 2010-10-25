package yu.einstein.gdp2.duplicateReadFilter;

/**
 * Class to provide a filter for duplicate reads
 * @author Chirag Gorasia 
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class DupReadFilter {
	private Map<String, List<ReadsData>> mapForInputFile = new HashMap<String, List<ReadsData>>();
	private Map<String, Integer> mapForReadsAlreadyExceeded = new HashMap<String, Integer>();
	private int maxDupCount;
	
	/**
	 * Private method to populate a hash map for the input file
	 * @param inputFile
	 * @throws IOException
	 */
	private void populateHashMap (File inputFile) throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(inputFile));
		String lineRead = bufReader.readLine();
		StringTokenizer strTok;
		int lengthOfFile = 0;
		//int exceedcount = 0;
		while (lineRead != null) {
			if (lineRead.length() != 0) {
				strTok = new StringTokenizer(lineRead, "\t\n");
				String readName = strTok.nextToken();
				System.out.println(lengthOfFile++);
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
//				String geneSequence = strTok.nextToken();
//				String unusedCodeField = strTok.nextToken();
//				int unusedUnknownField = Integer.parseInt(strTok.nextToken());
//				String mismatches = "";
//				if (strTok.hasMoreTokens()) {
//					mismatches = strTok.nextToken();
//				}
			//	ReadsData rd = new ReadsData(readName, strand, chromosome, start, geneSequence, unusedCodeField, unusedUnknownField, mismatches);
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);
//				if (this.mapForInputFile.containsKey(readName)) {
//					this.mapForInputFile.get(readName).add(rd);
//				} else {
//					List<ReadsData> readsData = new ArrayList<ReadsData>();
//					readsData.add(rd);
//					this.mapForInputFile.put(readName, readsData);
//				}
				
				if (this.mapForInputFile.containsKey(readName)) {
					if (this.mapForInputFile.get(readName).size() < this.maxDupCount) {
						this.mapForInputFile.get(readName).add(rd);
					} else {
						this.mapForReadsAlreadyExceeded.put(readName, 0);
						this.mapForInputFile.remove(readName);
					}
				} else if (!this.mapForReadsAlreadyExceeded.containsKey(readName)){
					List<ReadsData> readsData = new ArrayList<ReadsData>();
					readsData.add(rd);
					this.mapForInputFile.put(readName, readsData);
				} 
				
			}
			lineRead = bufReader.readLine();
		}		
	}
	
	/**
	 * Private method to write the output to a file
	 * @param outputFile
	 * @throws IOException
	 */
	private void printToOutputFile (File outputFile) throws IOException {
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
		Iterator<String> iter = mapForInputFile.keySet().iterator();
		while (iter.hasNext()) {
			String readName = iter.next();
			double score = 1;
			List<ReadsData> rdList = this.mapForInputFile.get(readName);
			if (rdList.size() <= this.maxDupCount) {
				score /= rdList.size();
				for (int i = 0; i < rdList.size(); i++) {
					ReadsData rd = rdList.get(i);
					bufWriter.write(readName + "\t" + rd.getChromosome() + "\t" + rd.getStart() + "\t" + (rd.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rd.getStrand() + "\n");
				}				
			}
		}
		bufWriter.close();
	}
	
	/**
	 * private method to sort the file
	 * @throws IOException
	 */
	private void sortRepositionedFile(File outputFile) throws IOException {
		BufferedReader buf = new BufferedReader(new FileReader(outputFile));
		List<BedFileSorter> file = new ArrayList<BedFileSorter>();
		String lineRead = buf.readLine();
		StringTokenizer strtok = new StringTokenizer(lineRead,"\t\n");
		while (lineRead != null) {
			String readName = strtok.nextToken();			// read name
			String chrName = strtok.nextToken();
			int start = Integer.parseInt(strtok.nextToken());
			int stop = Integer.parseInt(strtok.nextToken());
			strtok.nextToken();			// unused field denoted by dash
			double score = Double.parseDouble(strtok.nextToken());
			String strand = strtok.nextToken();
			BedFileSorter fileSorter = new BedFileSorter(readName, chrName, start, stop, score, strand);
			file.add(fileSorter);
			lineRead = buf.readLine();
			if (lineRead != null) {
				strtok = new StringTokenizer(lineRead,"\t\n");
			}
		}			
		
		Collections.sort(file);
		// write the sorted data to the file		
		//String sortedFile = outputFile.getParent() + "sorted" + outputFile.getName();
		//BufferedWriter bufWriter = new BufferedWriter(new FileWriter(sortedFile));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outputFile));
		Iterator<BedFileSorter> iter = file.iterator();
		while (iter.hasNext()) {
			BedFileSorter fs = iter.next();
			bufWriter.write(fs.getChromosomeName() + "\t" + fs.getStart() + "\t" + fs.getStop() + "\t-" + "\t" + fs.getScore() + "\t" + fs.getStrand() + "\n");
		}
		bufWriter.close();		
	}	
	
	/**
	 * main method
	 * @param args
	 * @throws IOException
	 */
	public static void main (String args[]) throws IOException {
		DupReadFilter drf = new DupReadFilter();
		if (args.length != 2) {
			System.out.println("Usage: " + "java -jar <inputfile> <outputfile>");
			System.exit(0);
		}
		Scanner in = new Scanner(System.in);
		System.out.println("\nEnter the maximum number of duplicate reads to be allowed i.e. a positive number");
		String input = in.nextLine();
		
		try {
			drf.maxDupCount = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.err.println("Invalid input " + "\"" + input + "\"");
			System.exit(0);
		} 
		
		File inputFile = new File(args[0]);
		
		drf.populateHashMap(inputFile);		
		
		File outputFile = new File(args[1]);
		
		drf.printToOutputFile(outputFile);
		
		drf.sortRepositionedFile(outputFile);
	}	
}