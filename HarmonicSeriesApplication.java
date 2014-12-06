package harmonicseries;

import java.math.BigInteger;

/**
 *
 * @author James Clark
 */
public class HarmonicSeriesApplication {
    
    private static class HarmonicSeriesSum implements Runnable
    {
        private double sum = 0.0;
        /*
        private int begin = 0; //Only useful for partial sums
        private int end = 0;   //Only useful for partial sums
        */
        private BigInteger begin = BigInteger.ZERO;
        private BigInteger end = BigInteger.ZERO;
        
        public HarmonicSeriesSum (BigInteger terms) throws IllegalArgumentException
        {
            if (terms.compareTo(BigInteger.ZERO) <= 0)
            {
                this.begin = BigInteger.ONE;
                this.end = terms;
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        
        //Useful for generating partial sums
        private HarmonicSeriesSum (BigInteger begin, BigInteger end) throws IllegalArgumentException
        {
            if (begin.compareTo(BigInteger.ONE) >= 0)
            {
                this.begin = begin.min(end);
                this.end = end.max(begin);
            }
            else 
            {
                this.begin = BigInteger.ONE;
                this.end = BigInteger.ONE;
                throw new IllegalArgumentException();
            } 
        }
                
        
        private double getSum()
        {
            return this.sum;
        }
        
        //Overloaded from java.lang.Thread
        public void run()
        {
            this.sum();
        }
        
        public static double parallelSum(BigInteger terms, int threads)
        {
            double sum = 0.0;
               
            //Limit to only one thread if there are fewer terms than threads requested
            if (terms.compareTo(BigInteger.valueOf(threads)) < 1)
                threads = 1;
            
            HarmonicSeriesSum partialSums[] = new HarmonicSeriesSum[threads];
            
            int delta = (int)Math.ceil((float)terms / (float)threads);
            int begin = 1;
            int end = 1;
            
            for (int i = 0; i < partialSums.length; i++)
            {
                //Limit end term based on how many threads left to allocate
                end = Math.min(begin + delta - 1, terms - (partialSums.length - 1 - i));
                    
                //Create and start new thread
                partialSums[i] = new HarmonicSeriesSum(begin, end);
                partialSums[i].start();
                
                begin = Math.min(end + 1, terms);
            }
            
            
            //Merge summing threads to main thread
            try
            {
                for (HarmonicSeriesSum partialSum : partialSums)
                {
                    partialSum.join();
                }
            }
            catch (InterruptedException e) {}
            
            //Add together all partial sums
            for (HarmonicSeriesSum partialSum: partialSums)
            {
                sum += partialSum.getSum();
            }
            
            return sum;
        }
        
        /*
        public static double parallelSum(int terms, int threads)
        {
            double sum = 0.0;
               
            //Limit to only one thread if there are fewer terms than threads requested
            if (terms < threads)
                threads = 1;
            
            HarmonicSeriesSum partialSums[] = new HarmonicSeriesSum[threads];
            
            int delta = (int)Math.ceil((float)terms / (float)threads);
            int begin = 1;
            int end = 1;
            
            for (int i = 0; i < partialSums.length; i++)
            {
                //Limit end term based on how many threads left to allocate
                end = Math.min(begin + delta - 1, terms - (partialSums.length - 1 - i));
                    
                //Create and start new thread
                partialSums[i] = new HarmonicSeriesSum(begin, end);
                partialSums[i].start();
                
                begin = Math.min(end + 1, terms);
            }
            
            
            //Merge summing threads to main thread
            try
            {
                for (HarmonicSeriesSum partialSum : partialSums)
                {
                    partialSum.join();
                }
            }
            catch (InterruptedException e) {}
            
            //Add together all partial sums
            for (HarmonicSeriesSum partialSum: partialSums)
            {
                sum += partialSum.getSum();
            }
            
            return sum;
        }
        */
        //Perform a single-threaded sum
        public double sum()
        {
            this.sum = 0.0;
            
            for (int i = this.begin; i <= this.end; i++)
                this.sum += 1.0/(double)i;
            
            return sum;
        }
        
        public double sum(int terms)
        {
            if (terms > 0)
            {
                this.begin = 1;
                this.end = terms;
                
                return this.sum(terms);
            }
            else return 0;
        }
        
        //Estimate terms to sum before exceeding M
        public BigInteger estimateThreshold (double M)
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
        
        double e = approximateEuler(30);
        System.out.println(e);
    }
    
}
