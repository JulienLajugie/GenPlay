//package yu.einstein.gdp2.core.writer.binListWriter;
//
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import yu.einstein.gdp2.core.Chromosome;
//import yu.einstein.gdp2.core.list.binList.BinList;
//import yu.einstein.gdp2.exception.BinListDifferentWindowSizeException;
//import yu.einstein.gdp2.util.ChromosomeManager;
//
//public class ConcatainedBinListWriter {
//
//	private final ChromosomeManager cm;
//	private final BinList[] binListArray;
//	private final String[] nameArray;
//	private final File outputFile;
//
//
//
//
//	public ConcatainedBinListWriter(ChromosomeManager cm, BinList[] binListArray, String[] nameArray, File outputFile) {
//		this.cm = cm;
//		this.binListArray = binListArray;
//		this.nameArray = nameArray;
//		this.outputFile = outputFile;		
//	}
//
//
//	public void write() throws IOException, BinListDifferentWindowSizeException {
//		if (binListArray.length > 0) {
//			int binSize = binListArray[0].getBinSize();
//			for (BinList currentList: binListArray) {
//				if (currentList.getBinSize() != binSize) {
//					throw new BinListDifferentWindowSizeException();
//				}
//			}
//
//			BufferedWriter writer = null;
////			try {
////				// try to create a output file
////				writer = new BufferedWriter(new FileWriter(outputFile));
////				// print the title of the graph
////				writer.write("track type=concatained file name=");
////				for (String currentName: nameArray) {
////					writer.write(currentName + " + ");
////				}
////				writer.newLine();
////
////				for (BinList currentList: binListArray) {
////
////				}
////
////				for (Chromosome currentChromo: cm) {
////					//if ()
////
////
////				}
////
////			} catch()	
//		}
//	}
//}
