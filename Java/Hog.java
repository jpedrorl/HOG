
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Hog {
    private static boolean drawImage = false;
	
    private BufferedImage image = null;
    private BufferedImage colorImage = null;
    private JFrame frame;
    private Graphics graphics;
    private int nThreads = 1;
    private String file;
    PrintWriter writer = null;

    public Hog(String file, int nThreads){
	this.file = file;
	    
	this.nThreads = nThreads;
		
	try {
	    colorImage = ImageIO.read(new File("input/" + file));
	} catch (IOException e) {
	    e.printStackTrace();
	}
	ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);  
	ColorConvertOp op = new ColorConvertOp(cs, null);  
	image = op.filter(colorImage, null);
		
	graphics = colorImage.getGraphics();
		
	if(drawImage){
	    this.frame = new JFrame();
	    this.frame.getContentPane().setLayout(new FlowLayout());
	    this.frame.getContentPane().add(new JLabel(new ImageIcon(image)));
	    this.frame.pack();
	    this.frame.setVisible(true);
	}		
	else
	    this.frame = null;
    }
	
    public double[][][] getHistograms(){
	double [][][] histograms = new double[image.getWidth() / 8 + 1][image.getHeight() / 8 + 1][9];
	double [][][] vectors = new double[image.getWidth() / 8 + 1][image.getHeight() / 8 + 1][2];
		
	Thread[] threads = new Thread[nThreads];
	for(int i = 0; i < nThreads; i++){
	    Thread t = new Thread(new HogGradients(this, histograms, i * getHeight() / nThreads, (i + 1) * getHeight() / nThreads, vectors));
	    t.start();
	    threads[i] = t;
	}
	for(int i = 0; i < nThreads; i++){
	    try {
		threads[i].join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
		
	for(int x = 0; x < image.getWidth() / 8; x++){
	    for(int y = 0; y < image.getHeight() / 8; y++){
		double x_vec = vectors[x][y][0], y_vec = vectors[x][y][1];
				
		double v = Math.pow(x_vec, 2) + Math.pow(y_vec, 2);
		double mag = Math.pow(v, 0.5);
				
		if(mag > 500){
		    x_vec /= mag/8;
		    y_vec /= mag/8;
					
		    double aux = x_vec;
		    x_vec = y_vec;
		    y_vec = -aux;
					
		    graphics.drawLine(x * 8 + 4, y * 8 + 4, (int)x_vec + x * 8 + 4, (int)y_vec + y * 8 + 4);

		}
	    }
	}		
	this.repaint();
	
	return histograms;
    }

    public double[][][] getOutput(double[][][] histograms){
		
	double output[][][] = new double[image.getWidth()/8 - 1][image.getHeight()/8 - 1][36];
	Thread[] threads = new Thread[nThreads];
	for(int i = 0; i < nThreads; i++){
	    Thread t = new Thread(new HogOutput(this, histograms, output, i * (getHeight() / 8 - 1) / nThreads, (i + 1) * (getHeight() / 8 - 1) / nThreads));
	    t.start();
	    threads[i] = t;
	}
	for(int i = 0; i < nThreads; i++){
	    try {
		threads[i].join();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
		
	return output;
    }

    public int getValue(int x, int y){
	return image.getRGB(x, y) & 0xFF;
    }
	
    public int getWidth(){
	return image.getWidth();
    }
    public int getHeight(){
	return image.getHeight();
    }
	
    public void repaint(){
	if(frame != null)
	    frame.repaint();

	try {
	    ImageIO.write(colorImage, "jpg", new File("output/" + file));
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }
}
