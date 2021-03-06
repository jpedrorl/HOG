import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;


public class Serial {
    String file;

    long histogramsDuration, outputDuration;

    public Serial(String file){
	this.file = file;
    }

    public void Run(){
	long tic, toc;

	PrintWriter writer = null;
	try {
	    writer = new PrintWriter("output/" + file + ".dat", "UTF-8");
	} catch (FileNotFoundException | UnsupportedEncodingException e1) {
	    e1.printStackTrace();
	}

	Hog_Serial hog = new Hog_Serial(file);

	tic = System.nanoTime();
	double[][][] histograms = hog.getHistograms();
	toc = System.nanoTime();

	histogramsDuration = (toc - tic) / 1000l;
	
	for(double[][] arr : histograms){
	    for(double [] ar : arr){
		for(double value : ar)
		    writer.print(value + " ");
		writer.print("\n");
	    }
	}
	writer.print("\n");

	tic = System.nanoTime();
	double[][][] output = hog.getOutput(histograms);
	toc = System.nanoTime();
	outputDuration = (toc - tic) / 1000l;
	
	for(double[][] arr : output){
	    for(double [] ar : arr){
		for(double value : ar)
		    writer.print(value + " ");
	    }
	}
	writer.print("\n");

	writer.close();
    }
}
