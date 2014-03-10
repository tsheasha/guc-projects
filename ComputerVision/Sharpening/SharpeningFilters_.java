import ij.*;
import ij.process.ImageProcessor.*;
import ij.process.*;
import ij.process.FHT.*;
import ij.plugin.filter.*;
import ij.plugin.*;
import ij.plugin.FFT.*;
import java.lang.Math.*;
import java.lang.String.*;
import javax.swing.*;
import ij.measure.Calibration;
import ij.gui.*;
import java.awt.*;

public class SharpeningFilters_ implements PlugInFilter
{
	private int threshold, order;
        private int M, N, size, w, h;
	private ImagePlus imp;
	private FHT fht;
        private ImageProcessor mask, ipFilter, ipFilter2;
        private double A, B;
        private String filter, filter2;

        // method from PlugInFilter Interface
	public int setup(String arg, ImagePlus imp)
	{
		this.imp = imp;
		return DOES_ALL;
	}

        // method from PlugInFilter Interface
	public void run(ImageProcessor ip)
        {
	    ip = imp.getProcessor();

            if (showDialog(ip))
            {
                filtering(ip,imp);
            }
            IJ.showProgress(1.0);
        }

        // the following method opens a window for users
	boolean showDialog(ImageProcessor ip)
	{
                A = 1;
                B = 1;
                int dim = 0;
	        M = ip.getWidth();
        	N = ip.getHeight();
        	if (M!=N) dim = (int)(Math.min(M,N)/2);
                else dim = M/2;
        	threshold = 20;
                order = 1;
                String[] choices = {"HighBoost","High Frequency Emphasis"};
                String[] choices2 = {"Ideal","Butterworth","Gaussian","Laplacian"};

	        GenericDialog gd = new GenericDialog("Sharpening Filters");
                gd.addChoice("main filter: ",choices, "HighBoost");
                gd.addChoice("highpass filter: ",choices2, "Ideal");
                gd.showDialog();
	        if (gd.wasCanceled())
	            return false;
                if(gd.invalidNumber())
                {
                    IJ.error("Error", "Invalid input number");
                    return false;
		}
                int choiceIndex = gd.getNextChoiceIndex();
		filter = choices[choiceIndex];
                int choiceIndex2 = gd.getNextChoiceIndex();
		filter2 = choices2[choiceIndex2];

	        GenericDialog gd2 = new GenericDialog("Sharpening Filter Parameters");
	        gd2.addNumericField("Costant A:", A, 1);
	        if (filter.equals("High Frequency Emphasis"))
                    gd2.addNumericField("Offset B:", B, 1);
	        if (!filter2.equals("Laplacian"))
                    gd2.addNumericField("Threshold Factor:", threshold, 0);
	        if (filter2.equals("Butterworth"))
                    gd2.addNumericField("Order:", order, 0);
	        gd2.showDialog();
	        if (gd2.wasCanceled())
	            return false;
                if(gd2.invalidNumber())
                {
                    IJ.error("Error", "Invalid input number");
                    return false;
		}
	        A = gd2.getNextNumber();
	        if (filter.equals("High Frequency Emphasis"))
                    B = gd2.getNextNumber();
	        if (!filter2.equals("Laplacian"))
                    threshold = (int) gd2.getNextNumber();
	        if (filter2.equals("Butterworth"))
                    order = (int) gd2.getNextNumber();

	        if (threshold>=0 && threshold<=dim)
	        		return true;
	        else
	        {
                    GenericDialog gd3;
                    boolean flag = true;
                    while (flag)
                    {
                        threshold = 20;
                        JOptionPane.showMessageDialog(null,"error, threshold must belong to [" + 0 + "," + dim + "]");
                        gd3 = new GenericDialog(" Threshold ");
                        gd3.addNumericField("Threshold Factor:", threshold, 0);
                        gd3.showDialog();
                        if (gd3.wasCanceled() || gd3.invalidNumber())
                            return false;
                        else
                        {
                            threshold = (int) gd3.getNextNumber();
                            if (threshold>=0 && threshold<=dim)
                                flag = false;
                        }
                    }
                }
                return true;
	}

        // shows the power spectrum and filters the image
	public void filtering(ImageProcessor ip, ImagePlus imp)
	{
		int maxN = Math.max(M, N);
		size = 2;
		while(size<maxN) size *= 2;
		IJ.runPlugIn("ij.plugin.FFT", "forward");
		h = Math.round((size-N)/2);
		w = Math.round((size-M)/2);
		ImageProcessor ip2 = ip.createProcessor(size, size);  // processor of the padded image
		ip2.fill();
		ip2.insert(ip, w, h);
                if (ip instanceof ColorProcessor)
                {
			ImageProcessor bright = ((ColorProcessor)ip2).getBrightness();
                        fht = new FHT(bright);
			fht.rgb = (ColorProcessor)ip.duplicate(); // get a duplication of brightness in order to add it after filtering
		}
                else  fht = new FHT(ip2);

		fht.originalColorModel = ip.getColorModel();
		fht.originalBitDepth = imp.getBitDepth();
		fht.transform();	// calculates the Fourier transformation

                if (filter.equals("HighBoost"))                ipFilter = HighBoost();
                if (filter.equals("High Frequency Emphasis"))  ipFilter = HighFrequency();
                fht.swapQuadrants(ipFilter);

                byte[] pixels_id = (byte[])ipFilter.getPixels();
                float[] pixels_fht = (float[])fht.getPixels();

                for (int i=0; i<size*size; i++)
		{
			pixels_fht[i] = (float)(pixels_fht[i]*(pixels_id[i]&255)/255.0);
		}

                mask = fht.getPowerSpectrum();
                ImagePlus imp2 = new ImagePlus("inverse FFT of "+imp.getTitle(), mask);
                imp2.setProperty("FHT", fht);
                imp2.setCalibration(imp.getCalibration());
                doInverseTransform(fht);
	}

        // creates the Highboost filter with the highpass filter chosen by user
        public ByteProcessor HighBoost()
        {
                ByteProcessor proc = new ByteProcessor(M, N);
                double value = 0;
                double valueLapl = 0;
                int xcenter = (M/2)+1;
                int ycenter = (N/2)+1;
                if (filter2.equals("Ideal"))           ipFilter2 = Ideal();
                if (filter2.equals("Butterworth"))     ipFilter2 = Butterworth(order);
                if (filter2.equals("Gaussian"))        ipFilter2 = Gaussian();
                if (filter2.equals("Laplacian"))       ipFilter2 = Laplacian();

                for (int y = 0; y < N; y++)
                {
                        for (int x = 0; x < M; x++)
                        {
                                int valueOrig = ipFilter2.getPixel(x, y);
                               	A = ((Math.round(A)-1) & 255)/255;
                                value = A + valueOrig;
                                proc.putPixelValue(x,y,value);
		      	}
                }

                ByteProcessor ip2 = new ByteProcessor(size,size);
                byte[] p = (byte[]) ip2.getPixels();
                for (int i=0; i<size*size; i++) p[i] = (byte)255;
                ip2.insert(proc, w, h);
                return ip2;
        }

        // creates the HighFrequency filter with the highpass filter chosen by user
        public ByteProcessor HighFrequency()
        {
                ByteProcessor proc = new ByteProcessor(M, N);
                double value = 0;
                double valueLapl = 0;
                int xcenter = (M/2)+1;
                int ycenter = (N/2)+1;
                if (filter2.equals("Ideal"))           ipFilter2 = Ideal();
                if (filter2.equals("Butterworth"))     ipFilter2 = Butterworth(order);
                if (filter2.equals("Gaussian"))        ipFilter2 = Gaussian();
                if (filter2.equals("Laplacian"))       ipFilter2 = Laplacian();

                for (int y = 0; y < N; y++)
                {
                        for (int x = 0; x < M; x++)
                        {
                                int valueOrig = ipFilter2.getPixel(x, y);
                                A = (Math.round(A) & 255)/255;
                                value = A + B * valueOrig;
                                proc.putPixelValue(x,y,value);
		      	}
                }

                ByteProcessor ip2 = new ByteProcessor(size,size);
                byte[] p = (byte[]) ip2.getPixels();
                for (int i=0; i<size*size; i++) p[i] = (byte)255;
                ip2.insert(proc, w, h);
                return ip2;
        }

        // creates an ideal highpass filter 
        public ByteProcessor Ideal()
	{
                ByteProcessor ip = new ByteProcessor(M,N);
                ip.setColor(Color.white);
                ip.fill();
                int xcenter = M/2;
                int ycenter = N/2;

                for (int radius=0; radius<threshold;radius++)
                {
                        for (double counter = 0; counter < 10; counter = counter + 0.001)
                        {
                                double x = Math.sin(counter) * radius + xcenter;
                                double y = Math.cos(counter) * radius + ycenter;
                                ip.putPixel((int)x, (int)y, 0);
                        }
                }
                return ip;
        }

        // creates a Butterworth highpass filter 
        public ByteProcessor Butterworth(int n)
        {
                ByteProcessor ip = new ByteProcessor(M,N);
                double value = 0;
                double distance = 0;
                int xcenter = (M/2)+1;
                int ycenter = (N/2)+1;

                for (int y = 0; y < N; y++)
                {
                        for (int x = 0; x < M; x++)
                        {
                                distance = Math.abs(x-xcenter)*Math.abs(x-xcenter)+Math.abs(y-ycenter)*Math.abs(y-ycenter);
                                distance = Math.sqrt(distance);
                                double parz = Math.pow(threshold/distance,2*n);
                                value = 255*(1/(1+parz));
                                ip.putPixelValue(x,y,value);
		      	}
                }
                return ip;
        }

        // creates a Gaussian highpass filter 
        public ByteProcessor Gaussian()
        {
                ByteProcessor ip = new ByteProcessor(M,N);
                double value = 0;
                double distance = 0;
                int xcenter = (M/2)+1;
                int ycenter = (N/2)+1;

                for (int y = 0; y < N; y++)
                {
   		        for (int x = 0; x < M; x++)
   		        {
                                distance = Math.abs(x-xcenter)*Math.abs(x-xcenter)+Math.abs(y-ycenter)*Math.abs(y-ycenter);
                                distance = Math.sqrt(distance);
                                value = 255-(255*Math.exp((-1*distance*distance)/(2*threshold*threshold)));
                                ip.putPixelValue(x,y,value);
   		       	}
                }
                return ip;
        }

        // creates a Laplacian filter 
        public ByteProcessor Laplacian()
        {
                ByteProcessor ip = new ByteProcessor(M, N);
                double value = 0;
                int xcenter = (M/2)+1;
                int ycenter = (N/2)+1;

                for (int y = 0; y < N; y++)
                {
                        for (int x = 0; x < M; x++)
                        {
                                value = (1 & 255)/255 + Math.abs(x-xcenter)*Math.abs(x-xcenter)+Math.abs(y-ycenter)*Math.abs(y-ycenter);
                                ip.putPixelValue(x,y,value);
		      	}
                }
                return ip;
        }

        // applies the inverse Fourier transform to the filtered image
        void doInverseTransform(FHT fht)
        {
		fht = fht.getCopy();
		fht.inverseTransform();
		fht.resetMinAndMax();
		ImageProcessor ip2 = fht;
                fht.setRoi(w, h, M, N);
                ip2 = fht.crop();

		int bitDepth = fht.originalBitDepth>0?fht.originalBitDepth:imp.getBitDepth();
		switch (bitDepth)
                {
			case 8:  ip2 = ip2.convertToByte(true); break;
			case 16: ip2 = ip2.convertToShort(true); break;
			case 24:
                                if (fht.rgb==null || ip2==null)
                                {
                                    IJ.error("FFT", "Unable to set brightness");
                                    return;
				}
				ColorProcessor rgb = (ColorProcessor)fht.rgb.duplicate();
				rgb.setBrightness((FloatProcessor)ip2);
				ip2 = rgb;
				fht.rgb = null;
				break;
			case 32: break;
		}
		if (bitDepth!=24 && fht.originalColorModel!=null)
			ip2.setColorModel(fht.originalColorModel);
		String title = imp.getTitle();
		if (title.startsWith("FFT of "))
			title = title.substring(7, title.length());
		ImagePlus imp2 = new ImagePlus("Inverse FFT of "+title, ip2);
		if (imp2.getWidth()==imp.getWidth())
			imp2.setCalibration(imp.getCalibration());
		imp2.show();
	}
}
