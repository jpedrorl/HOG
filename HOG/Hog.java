
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Hog {
	private BufferedImage image = null;

	public Hog(String file){
		BufferedImage colorImage = null;
		try {
			colorImage = ImageIO.read(new File(file));
		} catch (IOException e) {
		}
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
		ColorConvertOp op = new ColorConvertOp(cs, null);  
		image = op.filter(colorImage, null);

	}
	public double[][][] getHistograms(){

		double [][][] histograms = new double[image.getWidth()][image.getHeight()][9];

		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){

				double x_vec, y_vec;

				if (x == 0 || x == image.getWidth() - 1 || y == 0 || y == image.getHeight() - 1){
					x_vec = 0;
					y_vec = 0;
				}

				else{
					x_vec = getValue(x + 1, y) - getValue(x - 1, y);
					y_vec = getValue(x, y + 1) - getValue(x, y - 1);
				}
				double v = Math.pow(x_vec, 2) + Math.pow(y_vec, 2);
				double mag = Math.pow( v, 0.5);
				double ang = Math.atan2(Math.abs(y_vec), Math.abs(x_vec));

				double angRad = Math.toDegrees(ang);

				int pos1 = (int)((angRad + 10) / 20 - 1) % 9;
				int pos2 = (int)((angRad + 10) / 20) % 9;

				histograms[x][y][pos1] += ((20 - ((angRad % 10.0) % 20)) / 20) * mag;
				histograms[x][y][pos2] += (1 - (20 - ((angRad % 10.0) % 20)) / 20) * mag;

			}
		}
		/*
		for(int x = 0; x < image.getWidth(); x++){				
			for(int y = 0; y < image.getHeight(); y++){
				for(int z = 0; z < 9; z++)
					if(histograms[x][y][z] > 5)
						System.out.print(histograms[x][y][z] + "\n");
			}
		}
		*/
		return histograms;
	}

	public double[][][] getOutput(double[][][] histograms){
		double output[][][] = new double[image.getWidth() - 1][image.getHeight() - 1][36];
		for(int x = 0; x < image.getWidth() / 8 - 1; x++){

			for(int y = 0; y < image.getHeight() / 8 - 1; y++){
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

		for(int x = 0; x < image.getWidth() / 8 -1; x++){
			for(int y = 0; y < image.getHeight() / 8 -1; y++){
				for(int z = 0; z < 36; z++)
						System.out.print(output[x][y][z] + " ");
				System.out.print("\n");
			}
		}
		return output;

	}

	private int getValue(int x, int y){
		return image.getRGB(x, y) & 0xFF;
	}

}