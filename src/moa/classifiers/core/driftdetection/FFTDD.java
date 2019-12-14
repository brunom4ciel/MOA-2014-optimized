/*
 *    CLTDD.java
 *    Copyright (C) 2019 Maciel, Hidalgo, Barros
 *    @authors Bruno Iran Ferreira Maciel (bifm@cin.ufpe.br)
 *    		   Juan Isidro González Hidalgo (jigh@cin.ufpe.br)
 *             Roberto S. M. Barros (roberto@cin.ufpe.br) 
 *    @version $Version: 1 $
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * CLTDD: Central Limit Theorem to confidence intervals as Concept Drift Detection Method
 * published as:
 * <p> Bruno I. F. Maciel, Juan I. G. Hidalgo, and Roberto S. M. Barros: 
 *     USDD: Ultimately Simple Concept Drift Detection Method.
 *     ...
 *     DOI: ...
 *
 */

package moa.classifiers.core.driftdetection;

import java.util.Arrays;


import moa.core.ObjectRepository;
import moa.options.FloatOption;
import moa.options.IntOption;
import moa.tasks.TaskMonitor;

public class FFTDD extends AbstractChangeDetector {
    private static final long serialVersionUID = 1L;
             
    public IntOption windowSizeOption = 
    		new IntOption("windowSize", 
            'r', "Sliding Window Size.",
            16, 0, Integer.MAX_VALUE);//comprimento de onda. lambda

    public IntOption minNumInstancesOption = 
    		new IntOption("minNumInstances",
            'n', "Minimum number of instances before permitting detecting change.",
            300, 0, Integer.MAX_VALUE);

    public FloatOption alphaOption = 
            new FloatOption("alpha",
            'a', "Alpha confidence interval/two-tailed test.", 0.1, 0.0, 1.0);

    private int w; 	// Sliding window size 
    private int minNumInst;  // Minimum number of instances before permitting detecting change
    private int pred;  // Prediction converted to integer
    
//    private int warnLimit;   // Number of errors in the sliding window needed to signal warnings
//    private int driftLimit;  // Number of errors in the sliding window needed to signal drifts
    private int numIgnoredInst;  // Number of ignored predictions in the start of a new concept
    private int numInst;	// Number of processed instances in current concept
    private double numErr;  // Number of errors in the predictions stored in the sliding window

    private int pos;  // Last/next used position in the sliding window (stPred)
    private double [] stPred;	// stored predictions

//    double driftLimitAlpha, warningLimitAlpha;
    
    
//    private int numErr2;  // Number of errors in the predictions stored in the sliding window
//    private int pos2;  // Last/next used position in the sliding window (stPred)
//    private double [] stPred2;	// stored predictions
//    int w2 = 21;
    
    int supportUpper2 = 30;
    int supportUpper = 30;
//	int lastMin = 0;
	int instLambda = 0;
//	int countConfirm = 0;
	boolean firstEvaluate = true;
    
    
	int maxNumErr = 0;
	int minNumErr = 0;
	
	
//    double ciAlpha;
//    double ZnValue=0;
    double alpha;
//    double yi;
//    double mi;
//    double si;
    int numInst2=0;
    
//    int numErroTotal=0;
    
    public FFTDD() {
        initialize();
    }
    
    // menor frequencia, maior amplitude
    // maior frequencia, menor amplitude
    
    
    public void initialize() {    	
    	w = windowSizeOption.getValue();  
    	alpha = alphaOption.getValue();
    	alpha = alpha/2;    	
    	alpha = 0.5 - alpha;
    	
    	maxNumErr = (int) ((double) w * alpha);
    	minNumErr = (int) ((double) maxNumErr * 0.8);
    	    	
//    	System.out.println("maxNumErr: "+maxNumErr+", minNumErr:"+minNumErr);
//    	System.exit(0);
    	
    	minNumInst = minNumInstancesOption.getValue();
    	numIgnoredInst = minNumInst - w;
    	stPred = new double[w];

        resetLearning(); 
    }

    @Override
    public void resetLearning() { 
    	
    	lastValue = -1;lastCount=0;
    	fError = 0;
    	fAccuracy = 0;
    	fErrorSum=0;
    	fErrorSumCount=0;
    	fcount = 0;
    	instErr = 0; 
    	lastFErrorCount = 0;
    	lastFError = 0;
    	 
    	supportUpper = supportUpper2 = minNumErr;
    	instLambda = 0;
    	firstEvaluate = true;
    	
//    	numErroTotal=0;
    	numErr=0;
    	numInst = pos = 0;    	
        Arrays.fill(stPred, 0); 
        isChangeDetected = false;    
        isWarningZone = false;
    }
        
    
    public double mean(double []data) {
    	double sum=0;
    	int n = data.length;
    	
    	for(int i=0;i < data.length; i++)
    		sum += data[i];    		
    	
    	return sum/n;
    }
    
    
    int lastValue = -1;
    int lastCount = 0;
    
    double fError = 0;
    int fAccuracy = 0;
    
    double fErrorSum=0;
    int fErrorSumCount=0;
    
    int fcount = 0;
    int instErr =0;
    double lastFError=0;
    int lastFErrorCount = 0;
    
    @Override
	public void input(double prediction) { 
    	if (!this.isInitialized) {
            initialize();
            this.isInitialized = true;
        }
    	
	    pred = (int) prediction;
		numInst++;
		numInst2++;
//		numErroTotal += pred;
		
//		if(numInst2 > 4000) {
//			System.out.print("-");
//		}
		
		if(numInst2 > 4500) {
//
//			for(int i = 0; i < stPred.length; i++) {
//				System.out.print(stPred[i]+", ");
//			}
//			System.exit(0);
		}
    
		
//		double d1[] = new double[10];
//		double d2[] = new double[10];
//		double dd[] = fft(d1,d2,true);
//		
//		for(int i=0; i < dd.length; i++) {
//			System.out.println("["+i+"]="+dd[i]);
//		}
//		System.exit(0);
		
		
		
		
		
		
		
		if(numInst > numIgnoredInst){	
				
			
			
			fcount++;
			
			if(pred == 1) {
				fError++;
			}
			
			final int freq = 5;
			
			
			if(fcount == freq) {
				
				fError = fError/freq;
				
				
				
				
//				numErr = numErr - stPred[pos]  + fError;
//				if(numErr < 0) {
//					numErr = 0;
//				}
				stPred[pos] = fError;  // The last prediction result is stored in the sliding window
				
				pos++;  // Updates the position in the sliding window	
				if (pos == w) 
				   pos = 0;	
				
				
				
				
				
				fErrorSum += fError;
				fErrorSumCount++;
				
								
//				System.out.print(numInst2+", "+mi+", "+mu+"\n");
				
				instErr++;
				if(instErr > w) {
					
					double mi = mean(stPred);
//					double mu = fErrorSum/fErrorSumCount;
					
					final double drift = 0.45;
					final double warning = drift*0.9;
					
//					if(mi > 0.25) {
						if(mi < warning) {
							isWarningZone = false;
						}else {
							
							if(mi < drift) {
								isWarningZone = true;	
//								System.out.println("W, "+numInst2);
							}else {
								System.out.println("DRIFT, "+numInst2+", \t"+mi+", "+"\tmu\t"+numErr);
								resetLearning();
								isChangeDetected = true;
							}
							
						}
//					}
					
				}
					
//				if(mi > mu * 1.6) {
//					System.out.println("DRIFT, "+numInst2+", \t"+mi+", "+"\t"+mu+"\t"+numErr);
//					resetLearning();
//					isChangeDetected = true;
//				}
				
				
//				if(mi < warning) {
//					isWarningZone = false;
//				}else {
//					
//					if(mi < drift) {
//						isWarningZone = true;	
////						System.out.println("W, "+numInst2);
//					}else {
//						System.out.println("DRIFT, "+numInst2+", \t"+mi+", "+"\t"+mu+"\t"+numErr);
//						resetLearning();
//						isChangeDetected = true;
//					}
//					
//				}

				
				
//				if(lastFError < mu) {
//					lastFErrorCount++;					
//				}else {
//					lastFErrorCount=0;
//				}
//				lastFError = mu;
//				
////				System.out.print(numInst2+", "+mi+", "+mu+"\n");
//				
//				if(lastFErrorCount > 6) {
////					System.exit(0);
////					System.out.println("DRIFT, "+numInst2+", "+"\t"+mu+"\t"+numErr);
////					resetLearning();
////					isChangeDetected = true;
//				}
				
				
				
				
//				if(instErr > w) {
//					double mi = numErr/w;//Math.abs(numErr/w);
					
//					System.out.print(numInst2+", "+mi+", "+mu+", "+numErr+"\n");
					
//					if(mi > mu*1.2) {
//						System.out.println("DRIFT, "+numInst2+", "+mi+"\t"+mu+"\t"+numErr);
//						resetLearning();
//						isChangeDetected = true;
//						
//					}
					
//					if(mi > mu*1.1 && mi > 0.2) {
//////					if(fError > 0.8) {
//						System.out.println("DRIFT, "+numInst2+", "+mi+"\t"+mu);
//////						System.exit(0);
//						resetLearning();
//						isChangeDetected = true;	
//					}
					
					
//				}
//				
//				instErr++;
				
				
//				if(fErrorSumCount > 10) {
					
//				}
				
				
				
				
//				if(lastFErrorCount > 3) {
//					System.out.println("up-"+numInst2);
//					if((numErr/w) > 0.4) {
////					if(mu * 2 < fError) {
//						System.out.println("DRIFT, "+numInst2+", "+(fErrorSum/fErrorSumCount)+"\t"+fError);
//						resetLearning();
//						isChangeDetected = true;	
//					}
//				}
				
				fcount = 0;
				fError = 0;
			}
			
			
			
			
			
			
			
//			if(lastValue > -1) {
//				if(lastValue == pred) {
//					lastCount++;
//				}else {
//					
//					if(lastValue == 1) {					
//	//					numErr = numErr - stPred[pos] + lastCount;  // Updates the number of errors in the sliding window
//						stPred[pos] = lastCount;  // The last prediction result is stored in the sliding window
//						
////						System.out.print(stPred[pos]+", ");
//						
//						pos++;  // Updates the position in the sliding window	
//						if (pos == w) 
//						   pos = 0;		
//						
//					}
//					
//					lastValue = pred;
//					lastCount=0;
//					
//				}
//			}else {
//				lastValue = pred;
//				if(pred == 1) {
//					lastCount++;
//				}				
//			}
			
			
			
			
			
			
					
			
			
			
			
			if (numInst >= minNumInst+w) {
				
				
				
//				instLambda++;
//				
//				if(instLambda < minNumInst) {		
//					
//					if(supportUpper2 < numErr) {
//						
//						if(numErr > maxNumErr) {
//							supportUpper2 = maxNumErr;
//						}else {
//							supportUpper2 = numErr;
//						}
//						
//						if(firstEvaluate) {
//							supportUpper = supportUpper2;
//						}
//					}
//					
//				}else {
//					instLambda = 0;
//					firstEvaluate = false;
//					supportUpper = supportUpper2;
//				}
//
////				System.out.println("STABLE, "+numInst2+", "+numInst+"\t"+numErr+"\t"+supportUpper);
//								
//		    	if(supportUpper > numErr*1.1) {
//		    		isWarningZone = false;			    		
//		    	}else {
//		    		
//		    		if(supportUpper < numErr) {		    				
////			    			System.out.println("DRIFT, "+numInst2+", "+numInst+"\t"+numErr+"\t"+supportUpper);
////				    		System.exit(0);
//			    		resetLearning();
//						isChangeDetected = true;	
//		    		}
//		    		else {
//		    			isWarningZone = true;	
////			    			System.out.println("WARNING, "+numInst2+"\t"+numErr+"\t"+supportUpper);			    			
//		    		}			    		
//		    	}	 
			}
			
		}
	} 
    
    
    /**
     * The Fast Fourier Transform (generic version, with NO optimizations).
     *
     * @param inputReal
     *            an array of length n, the real part
     * @param inputImag
     *            an array of length n, the imaginary part
     * @param DIRECT
     *            TRUE = direct transform, FALSE = inverse transform
     * @return a new array of length 2n
     */
    public static double[] fft(final double[] inputReal, double[] inputImag,
                               boolean DIRECT) {
        // - n is the dimension of the problem
        // - nu is its logarithm in base e
        int n = inputReal.length;

        // If n is a power of 2, then ld is an integer (_without_ decimals)
        double ld = Math.log(n) / Math.log(2.0);

        // Here I check if n is a power of 2. If exist decimals in ld, I quit
        // from the function returning null.
        if (((int) ld) - ld != 0) {
            System.out.println("The number of elements is not a power of 2.");
            return null;
        }

        // Declaration and initialization of the variables
        // ld should be an integer, actually, so I don't lose any information in
        // the cast
        int nu = (int) ld;
        int n2 = n / 2;
        int nu1 = nu - 1;
        double[] xReal = new double[n];
        double[] xImag = new double[n];
        double tReal, tImag, p, arg, c, s;

        // Here I check if I'm going to do the direct transform or the inverse
        // transform.
        double constant;
        if (DIRECT)
            constant = -2 * Math.PI;
        else
            constant = 2 * Math.PI;

        // I don't want to overwrite the input arrays, so here I copy them. This
        // choice adds \Theta(2n) to the complexity.
        for (int i = 0; i < n; i++) {
            xReal[i] = inputReal[i];
            xImag[i] = inputImag[i];
        }

        // First phase - calculation
        int k = 0;
        for (int l = 1; l <= nu; l++) {
            while (k < n) {
                for (int i = 1; i <= n2; i++) {
                    p = bitreverseReference(k >> nu1, nu);
                    // direct FFT or inverse FFT
                    arg = constant * p / n;
                    c = Math.cos(arg);
                    s = Math.sin(arg);
                    tReal = xReal[k + n2] * c + xImag[k + n2] * s;
                    tImag = xImag[k + n2] * c - xReal[k + n2] * s;
                    xReal[k + n2] = xReal[k] - tReal;
                    xImag[k + n2] = xImag[k] - tImag;
                    xReal[k] += tReal;
                    xImag[k] += tImag;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 /= 2;
        }

        // Second phase - recombination
        k = 0;
        int r;
        while (k < n) {
            r = bitreverseReference(k, nu);
            if (r > k) {
                tReal = xReal[k];
                tImag = xImag[k];
                xReal[k] = xReal[r];
                xImag[k] = xImag[r];
                xReal[r] = tReal;
                xImag[r] = tImag;
            }
            k++;
        }

        // Here I have to mix xReal and xImag to have an array (yes, it should
        // be possible to do this stuff in the earlier parts of the code, but
        // it's here to readibility).
        double[] newArray = new double[xReal.length * 2];
        double radice = 1 / Math.sqrt(n);
        for (int i = 0; i < newArray.length; i += 2) {
            int i2 = i / 2;
            // I used Stephen Wolfram's Mathematica as a reference so I'm going
            // to normalize the output while I'm copying the elements.
            newArray[i] = xReal[i2] * radice;
            newArray[i + 1] = xImag[i2] * radice;
        }
        return newArray;
    }

    /**
     * The reference bitreverse function.
     */
    private static int bitreverseReference(int j, int nu) {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
      }
    
    
    
    @Override
    public void getDescription(StringBuilder sb, int indent) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void prepareForUseImpl(TaskMonitor monitor,
            ObjectRepository repository) {
        // TODO Auto-generated method stub   
        initialize();
    }
}
