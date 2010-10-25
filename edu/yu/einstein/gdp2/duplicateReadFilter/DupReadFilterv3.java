package yu.einstein.gdp2.duplicateReadFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import yu.einstein.gdp2.gui.dialog.DupReadFilterDialog;

public class DupReadFilterv3 {
	private int minDupCount;
	private int maxDupCount;
	private Map<String, List<ReadsData>> mapForInputFile;
	
	private static final int LESS_THAN_EQUAL_TO = 1;
	private static final int EXACTLY_EQUAL_TO = 2;
	private static final int BETWEEN = 3;
	private static final int MORE_THAN = 4;

	public static void main (String args[]) throws IOException {
		if (args.length != 2) {
			System.out.println("Usage: " + "java -jar <inputfile> <outputfile>");
			System.exit(0);
		}
		DupReadFilterDialog drfd = new DupReadFilterDialog();
		drfd.showDialog(null);
		DupReadFilterv3 drf = new DupReadFilterv3();		
		File inFile = new File (args[0]);
		File outFile = new File (args[1]);
		
		switch (drfd.getOptionSelected()) {
		case LESS_THAN_EQUAL_TO:	drf.maxDupCount = drfd.getMaxDupCount();
									drf.handleLessThanOrEqualTo(inFile, outFile);
									break;
									
		case EXACTLY_EQUAL_TO:		drf.maxDupCount = drfd.getMaxDupCount();
									drf.handleExactlyEqualTo(inFile, outFile);
									break;
									
		case BETWEEN:				drf.minDupCount = drfd.getMinDupCount();
									drf.maxDupCount = drfd.getMaxDupCount();
									drf.handleBetween(inFile, outFile);
									break;
		
		case MORE_THAN:				drf.minDupCount = drfd.getMinDupCount();
									drf.handleMoreThan(inFile, outFile);
									break;
		}	
		System.exit(0);
	}

	
	private void handleMoreThan(File inFile, File outFile) throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(inFile));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outFile));
		mapForInputFile = new HashMap<String, List<ReadsData>>();
		String lineRead = bufReader.readLine();
//		int lineNumber = 0;
		StringTokenizer strTok;
		String prevReadName = "";
		String readName = "";
		System.out.println("Writing to output file...");
		while (lineRead != null) {
			if (lineRead.length() != 0) {
//				System.out.println(lineNumber++);
				strTok = new StringTokenizer(lineRead, " \t\n");
				readName = strTok.nextToken();
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
				
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);				
				
				if (mapForInputFile.containsKey(readName)) {
					mapForInputFile.get(readName).add(rd);
					prevReadName = readName;
					lineRead = bufReader.readLine();
					continue;
				} else if (mapForInputFile.containsKey(prevReadName) && mapForInputFile.get(prevReadName).size() <= minDupCount) {
					prevReadName = readName;
					mapForInputFile.clear();
					lineRead = bufReader.readLine();
					continue;
				} else if (!mapForInputFile.isEmpty() && mapForInputFile.get(prevReadName).size() > minDupCount){					
					Iterator<String> iter = mapForInputFile.keySet().iterator();
					while (iter.hasNext()) {
						String readNameToPrint = iter.next();
						double score = 1;
						List<ReadsData> rdList = mapForInputFile.get(readNameToPrint);
						score /= rdList.size();
						for (int i = 0; i < rdList.size(); i++) {
							ReadsData rdFromMap = rdList.get(i);
							// for debug purposes readName is also written to the file
							bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
							//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
						}						
					}
					mapForInputFile.clear();
				}	
				List<ReadsData> readsData = new ArrayList<ReadsData>();
				if (!readName.equals(prevReadName)) {
					readsData.add(rd);
					prevReadName = readName;
					mapForInputFile.put(readName, readsData);
				}
			}
			lineRead = bufReader.readLine();			
		}
		if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() > minDupCount) {
			double score = 1;
			score /= mapForInputFile.get(readName).size();
			for (int i = 0; i < mapForInputFile.get(readName).size(); i++) {
				ReadsData rdFromMap = mapForInputFile.get(readName).get(i);
				// for debug purposes readName is also written to the file
				bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
				//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
			}
		}
		bufWriter.close();
	}


	private void handleBetween(File inFile, File outFile) throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(inFile));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outFile));
		mapForInputFile = new HashMap<String, List<ReadsData>>();
		String lineRead = bufReader.readLine();
//		int lineNumber = 0;
		StringTokenizer strTok;
		String prevReadName = "";
		String readName = "";
		System.out.println("Writing to output file...");
		while (lineRead != null) {
			if (lineRead.length() != 0) {
//				System.out.println(lineNumber++);
				strTok = new StringTokenizer(lineRead, " \t\n");
				readName = strTok.nextToken();
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
				
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);				
				
				if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() < maxDupCount) {
					mapForInputFile.get(readName).add(rd);
					prevReadName = readName;
					lineRead = bufReader.readLine();
					continue;
				} else if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() == maxDupCount) {
					prevReadName = readName;
					mapForInputFile.clear();
					lineRead = bufReader.readLine();
					continue;
				} else if (!mapForInputFile.isEmpty() && mapForInputFile.get(prevReadName).size() <= maxDupCount && mapForInputFile.get(prevReadName).size() >= minDupCount){					
					Iterator<String> iter = mapForInputFile.keySet().iterator();
					while (iter.hasNext()) {
						String readNameToPrint = iter.next();
						double score = 1;
						List<ReadsData> rdList = mapForInputFile.get(readNameToPrint);
						score /= rdList.size();
						for (int i = 0; i < rdList.size(); i++) {
							ReadsData rdFromMap = rdList.get(i);
							// for debug purposes readName is also written to the file
							bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
							//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
						}						
					}
					mapForInputFile.clear();
				}	
				List<ReadsData> readsData = new ArrayList<ReadsData>();
				if (!readName.equals(prevReadName)) {
					readsData.add(rd);
					prevReadName = readName;
					mapForInputFile.put(readName, readsData);
				}
			}
			lineRead = bufReader.readLine();			
		}
		if (mapForInputFile.get(readName).size() <= maxDupCount && mapForInputFile.get(prevReadName).size() >= minDupCount) {
			double score = 1;
			score /= mapForInputFile.get(readName).size();
			for (int i = 0; i < mapForInputFile.get(readName).size(); i++) {
				ReadsData rdFromMap = mapForInputFile.get(readName).get(i);
				// for debug purposes readName is also written to the file
				bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
				//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
			}
		}
		bufWriter.close();
	}


	private void handleExactlyEqualTo(File inFile, File outFile) throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(inFile));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outFile));
		mapForInputFile = new HashMap<String, List<ReadsData>>();
		String lineRead = bufReader.readLine();
//		int lineNumber = 0;
		StringTokenizer strTok;
		String prevReadName = "";
		String readName = "";
		System.out.println("Writing to output file ...");
		while (lineRead != null) {
			if (lineRead.length() != 0) {
//				System.out.println(lineNumber++);
				strTok = new StringTokenizer(lineRead, " \t\n");
				readName = strTok.nextToken();
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
				
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);				
				
				if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() < maxDupCount) {
					mapForInputFile.get(readName).add(rd);
					prevReadName = readName;
					lineRead = bufReader.readLine();
					continue;
				} else if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() == maxDupCount) {
					prevReadName = readName;
					mapForInputFile.clear();
					lineRead = bufReader.readLine();
					continue;
				} else if (!mapForInputFile.isEmpty() && mapForInputFile.get(prevReadName).size() == maxDupCount){					
					Iterator<String> iter = mapForInputFile.keySet().iterator();
					while (iter.hasNext()) {
						String readNameToPrint = iter.next();
						double score = 1;
						List<ReadsData> rdList = mapForInputFile.get(readNameToPrint);
						score /= rdList.size();
						for (int i = 0; i < rdList.size(); i++) {
							ReadsData rdFromMap = rdList.get(i);
							// for debug purposes readName is also written to the file
							bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
							//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
						}						
					}
					mapForInputFile.clear();
				}	
				List<ReadsData> readsData = new ArrayList<ReadsData>();
				if (!readName.equals(prevReadName)) {
					readsData.add(rd);
					prevReadName = readName;
					mapForInputFile.clear();
					mapForInputFile.put(readName, readsData);
				}
			}
			lineRead = bufReader.readLine();			
		}
		if (mapForInputFile.get(readName).size() == maxDupCount) {
			double score = 1;
			score /= mapForInputFile.get(readName).size();
			for (int i = 0; i < mapForInputFile.get(readName).size(); i++) {
				ReadsData rdFromMap = mapForInputFile.get(readName).get(i);
				// for debug purposes readName is also written to the file
				bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
				//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
			}
		}
		bufWriter.close();
	}


	private void handleLessThanOrEqualTo(File inFile, File outFile) throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader(inFile));
		BufferedWriter bufWriter = new BufferedWriter(new FileWriter(outFile));
		mapForInputFile = new HashMap<String, List<ReadsData>>();
		String lineRead = bufReader.readLine();
//		int lineNumber = 0;
		StringTokenizer strTok;
		String prevReadName = "";
		String readName = "";
		System.out.println("Writing to output file...");
		while (lineRead != null) {
			if (lineRead.length() != 0) {
//				System.out.println(lineNumber++);
				strTok = new StringTokenizer(lineRead, " \t\n");
				readName = strTok.nextToken();
				String strand = strTok.nextToken();
				String chromosome = strTok.nextToken();
				int start = Integer.parseInt(strTok.nextToken());
				
				ReadsData rd = new ReadsData(readName, strand, chromosome, start);				
				
				if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() < maxDupCount) {
					mapForInputFile.get(readName).add(rd);
					prevReadName = readName;
					lineRead = bufReader.readLine();
					continue;
				} else if (mapForInputFile.containsKey(readName) && mapForInputFile.get(readName).size() == maxDupCount) {
					prevReadName = readName;
					mapForInputFile.clear();
					lineRead = bufReader.readLine();
					continue;
				} else if (!mapForInputFile.isEmpty()){					
					Iterator<String> iter = mapForInputFile.keySet().iterator();
					while (iter.hasNext()) {
						String readNameToPrint = iter.next();
						double score = 1;
						List<ReadsData> rdList = mapForInputFile.get(readNameToPrint);
						score /= rdList.size();
						for (int i = 0; i < rdList.size(); i++) {
							ReadsData rdFromMap = rdList.get(i);
							// for debug purposes readName is also written to the file
							bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
							//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
						}						
					}
					mapForInputFile.clear();
				}	
				List<ReadsData> readsData = new ArrayList<ReadsData>();
				if (!readName.equals(prevReadName)) {
					readsData.add(rd);
					prevReadName = readName;
					mapForInputFile.put(readName, readsData);
				}
			}
			lineRead = bufReader.readLine();			
		}
		if (mapForInputFile.get(readName).size() <= maxDupCount) {
			double score = 1;
			score /= mapForInputFile.get(readName).size();
			for (int i = 0; i < mapForInputFile.get(readName).size(); i++) {
				ReadsData rdFromMap = mapForInputFile.get(readName).get(i);
				// for debug purposes readName is also written to the file
				bufWriter.write(prevReadName + "\t" + rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
				//bufWriter.write(rdFromMap.getChromosome() + "\t" + rdFromMap.getStart() + "\t" + (rdFromMap.getStart()+1) + "\t" + "-" + "\t" + score + "\t" + rdFromMap.getStrand() + "\n");
			}
		}
		bufWriter.close();
	}	
}