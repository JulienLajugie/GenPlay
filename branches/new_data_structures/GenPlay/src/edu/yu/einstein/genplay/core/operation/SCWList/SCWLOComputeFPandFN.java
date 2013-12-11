package edu.yu.einstein.genplay.core.operation.SCWList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SCWLOComputeFPandFN {

	public static class FisherStats {
		public int a; //n
		public int b; //N - n
		public int c; //r
		public int d; //R - r
		public double p;
		public double q;
		public double odds;

		public FisherStats(int a, int b, int c, int d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}

	private class fpAndFn {
		private int falsePos, falseNeg;
		private final int islandSize;
		private final double islandWeight;

		public fpAndFn(int islandSize, double islandWeight) {
			this.islandSize = islandSize;
			this.islandWeight = islandWeight;
		}
	}

	public static class RStats {

		public static List<Double> calculateQValues(List<Double> pValues) throws NumberFormatException, IOException, InterruptedException {
			File tmpP = File.createTempFile("pct_p_", ".txt");
			File tmpQ = File.createTempFile("pct_q_", ".txt");
			File tmpScript = File.createTempFile("pct", ".R");
			File tmpOut = File.createTempFile("pct_rout", ".txt");
			PrintWriter sout = new PrintWriter(tmpScript);
			sout.println("library(qvalue)");
			sout.println("p = read.delim(file='" + tmpP.getAbsolutePath() + "', header=F)[,1]");
			sout.println("q = qvalue(p)");
			sout.println("write.table(q$qvalue, file='" + tmpQ.getAbsolutePath() + "', col.names=F, row.names=F)");
			sout.close();
			PrintWriter dout = new PrintWriter(tmpP);
			for(double p : pValues) {
				dout.println(p);
			}
			dout.close();
			String cmd = "R CMD BATCH " + tmpScript.getAbsolutePath() + " " + tmpOut.getAbsolutePath();
			Runtime.getRuntime().exec(cmd).waitFor();
			List<Double> qValues = new ArrayList<Double>(pValues.size());
			//Read the output
			BufferedReader in = new BufferedReader(new FileReader(tmpQ));
			for(int i = 0; i < pValues.size(); i++) {
				double q = Double.parseDouble(in.readLine());
				qValues.add(q);
			}
			in.close();

			return qValues;
		}

		public static void performFisher(List<FisherStats> data) throws IOException, InterruptedException {
			File tmpD = File.createTempFile("fish_in_", ".txt");
			File tmpR = File.createTempFile("fish_out_", ".txt");
			File tmpScript = File.createTempFile("fish", ".R");
			File tmpOut = File.createTempFile("fish_rout", ".txt");

			//data = matrix with columns a, b, c, d
			//test will be performed on each row
			PrintWriter sout = new PrintWriter(tmpScript);
			sout.println("library(qvalue)");
			sout.println("d = read.table('" + tmpD.getAbsolutePath() + "')");
			sout.println(
					"f = apply(d, 1, function(x) { " +
							"fisher.test(rbind(c(x[1],x[2]), c(x[3],x[4])), alternative='greater') " +
							"})"
					);
			sout.println("p = as.numeric(lapply(f, function(x) { x$p.value }))");
			sout.println("o = as.numeric(lapply(f, function(x) { x$estimate }))");
			sout.println("q = qvalue(p)$qvalue");
			sout.println("write.table(cbind(p, q, o), file='" + tmpR.getAbsolutePath() + "', col.names=F, row.names=F)");
			sout.close();

			//Write the data
			PrintWriter dout = new PrintWriter(tmpD);
			for(FisherStats s : data) {
				dout.println(s.a + "\t" + s.b + "\t" + s.c + "\t" + s.d);
			}
			dout.close();
			//Run the R command
			String cmd = "R CMD BATCH " + tmpScript.getAbsolutePath() + " " + tmpOut.getAbsolutePath();
			Runtime.getRuntime().exec(cmd).waitFor();
			//Read the output
			BufferedReader in = new BufferedReader(new FileReader(tmpR));
			for(int i = 0; i < data.size(); i++) {
				String[] cols = in.readLine().split(" ", 3);
				FisherStats s = data.get(i);
				s.p = Double.parseDouble(cols[0]);
				s.q = Double.parseDouble(cols[1]);
				s.odds = "Inf".equals(cols[2]) ? Double.POSITIVE_INFINITY : Double.parseDouble(cols[2]);
			}
			in.close();
		}
	}

	public SCWLOComputeFPandFN() {

	}
}
