package harmonicseries;

import java.math.BigInteger;
import java.lang.Runnable;

/**
 *
 * @author James Clark
 */
public class HarmonicSeriesApplication {
    private static class PartialHarmonicSeriesSum extends Thread
    {
        private int begin = 0;
        private int end = 0;
        private double sum = 0.0;
        
        public PartialHarmonicSeriesSum(int begin, int end)
        {
            if (begin > end)
            {
                throw new IllegalArgumentException("begin > end");
            }
            else if (begin < 0)
            {
                throw new IllegalArgumentException("begin < 0");
            }
            else if (end < 0)
            {
                throw new IllegalArgumentException("end < 0");
            }
            
            this.begin = begin;
            this.end = end;
            this.sum = 0.0;
        }
        
        public void run()
        {
            for (int i = this.begin; i <= this.end; i++)
                this.sum += 1.0 / (double)i;
        }
        
        public double getSum()
        {
            return this.sum;
        }
    }
    
    
    private static class HarmonicSeriesSum
    {
        private double sum = 0.0;
        private int terms = 0;
        
        
        public HarmonicSeriesSum(int terms) throws IllegalArgumentException
        {
            if (terms < 0)
            {
                throw new IllegalArgumentException("terms < 0");
            }
            else
            {
                this.sum = 0;
                this.terms = terms;
            }
        }
        
        public void parallelSum(int threads)
        {
            this.sum = 0.0;
               
            //Limit to only one thread if there are fewer terms than threads requested
            if (this.terms < threads)
                threads = 1;
            
            PartialHarmonicSeriesSum partialSums[] = new PartialHarmonicSeriesSum[threads];
            
            int delta = (int)Math.ceil((float)terms / (float)threads);
            int begin = 1;
            int end = 1;
            
            for (int i = 0; i < partialSums.length; i++)
            {
                //Limit end term based on how many threads left to allocate
                end = Math.min(begin + delta - 1, terms - (partialSums.length - 1 - i));
                    
                //Create and start new thread
                partialSums[i] = new PartialHarmonicSeriesSum(begin, end);
                partialSums[i].start();
                
                begin = Math.min(end + 1, terms);
            }
            
            
            //Merge summing threads to main thread
            try
            {
                for (PartialHarmonicSeriesSum partialSum : partialSums)
                {
                    partialSum.join();
                }
            }
            catch (InterruptedException e) {}
            
            //Add together all partial sums
            for (PartialHarmonicSeriesSum partialSum : partialSums)
            {
                this.sum += partialSum.getSum();
            }
        }
        
        //Perform a single-threaded sum
        public void sum()
        {
            this.sum = 0.0;
            
            for (int i = 1; i <= terms; i++)
                this.sum += 1.0/(double)i;
        }
        
        public double getSum()
        {
            return this.sum;
        }
        
        public int getTerms()
        {
            return this.terms;
        }
        
        public void addTerm()
        {
            this.terms++;
            this.sum += 1/(float)this.terms;
        }
        
        public void subtractTerm()
        {
            if (this.terms > 0)
            {
                this.terms--;
                this.sum = 1/(float)this.terms;
            }
            else
            {
                this.terms = 0;
                this.sum = 0.0;
            }
        }
        
        //Estimate terms to sum before exceeding M
        public int estimateTermsToSum (double M)
        {
            return 0;
        }
    }
    
    public static double approximateEuler (int iterations)
    {
        double factorial = 1;
        double e = 2;
        
        for (int i = 2; i <= iterations - 1; i++)
        {
            factorial *= i;
            e += 1/factorial;
        }
        
        return e;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        /*
        int processors = Runtime.getRuntime().availableProcessors();
        double sum = HarmonicSeriesSum.parallelSum(1000, processors);
        
        System.out.println(sum);
        */
        
        HarmonicSeriesSum series = new HarmonicSeriesSum(100);
        
        series.parallelSum(8);
        double sum = series.getSum();
        
        double e_to_m = Math.exp(sum);
        System.out.println("e^" + sum + " = " + e_to_m);
        System.out.println(series.getTerms() + "/" + e_to_m + " = " + series.getTerms()/e_to_m);
        System.out.println(sum);
    }
    
}
