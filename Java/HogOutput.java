
public class HogOutput implements Runnable{
	
    Hog hog;
    double[][][] histograms;
    double[][][] output;
    int ybegin, yend;
	
    public HogOutput(Hog hog, double[][][] histograms, double[][][] output, int begin, int end){
	this.hog = hog;
	this.histograms = histograms;
	this.output = output;
	this.ybegin = begin;
	this.yend = end;
    }

    @Override
    public void run() {
	for(int y = ybegin; y < yend; y++){
	    for(int x = 0; x < hog.getWidth() / 8 - 1; x++){
		int counter = 0;
		double mag = 0;
		for(int a = 0; a < 2; a++){
		    for(int b = 0; b < 2; b++){
			for(int z = 0; z < 9; z++){
			    output[x][y][counter] = histograms[x + a][y + b][z];
			    mag += output[x][y][counter];
			    counter++;				        				
			}
		    }
		}

		if(mag != 0){
		    for(int z = 0; z < 36; z++){
			output[x][y][z] /= mag;
		    }
		}
	    }
	}
    }

}
