
public class HogGradients implements Runnable{
    Hog hog;
    double [][][]histograms;
    double [][][]vectors;
    int ybegin, yend;
	
    public HogGradients(Hog hog, double[][][] histograms, int begin, int end, double[][][] vectors){
	this.hog = hog;
	this.histograms = histograms;
	this.vectors = vectors;
	this.ybegin = begin;
	this.yend = end;
		
    }

    @Override
    public void run() {
		

	for(int y = ybegin; y < yend; y++){
	    for(int x = 0; x < hog.getWidth(); x++){
				
		double x_vec, y_vec;

		if (x == 0 || x == hog.getWidth() - 1 || y == 0 || y == hog.getHeight() - 1){
		    x_vec = 0;
		    y_vec = 0;
		}

		else{
		    x_vec = hog.getValue(x + 1, y) - hog.getValue(x - 1, y);
		    y_vec = hog.getValue(x, y + 1) - hog.getValue(x, y - 1);
		}
		double v = Math.pow(x_vec, 2) + Math.pow(y_vec, 2);
		double mag = Math.pow( v, 0.5);
		double ang = Math.atan2(Math.abs(y_vec), Math.abs(x_vec));

		double angRad = Math.toDegrees(ang);

		int pos1 = (int)((angRad + 10) / 20 - 1) % 9;
		int pos2 = (int)((angRad + 10) / 20) % 9;

		histograms[x / 8][y / 8][pos1] += ((20 - ((angRad % 10.0) % 20)) / 20) * mag;
		histograms[x / 8][y / 8][pos2] += (1 - (20 - ((angRad % 10.0) % 20)) / 20) * mag;
				
		vectors[x / 8][y / 8][0] += x_vec;
		vectors[x / 8][y / 8][1] += y_vec;

	    }
	}		
    }

}
