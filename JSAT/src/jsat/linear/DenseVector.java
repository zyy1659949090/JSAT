
package jsat.linear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Math.*;

/**
 *
 * @author Edward Raff
 */
public class DenseVector implements Vec
{
    private double[] array;


    public DenseVector(int initalSize)
    {
        array = new double[initalSize];
    }

    public DenseVector(ArrayList<Double> array)
    {
        this.array = new double[array.size()];
        for(int i = 0; i < array.size(); i++)
            this.array[i] = array.get(i);
    }

    protected DenseVector(double[] array)
    {
        this.array = array;
    }
    
    
    public int length()
    {
        return array.length;
    }

    public double get(int index)
    {
        return array[index];
    }

    public void set(int index, double val)
    {
        array[index] = val;
    }

    public double min()
    {
        double result = array[0];
        for(int i = 1; i < array.length; i++)
            result = Math.min(result, array[i]);

        return result;
    }

    public double max()
    {
        double result = array[0];
        for(int i = 1; i < array.length; i++)
            result = Math.max(result, array[i]);

        return result;
    }

    public double sum()
    {
        /*
         * Uses Kahan summation algorithm, which is more accurate then
         * naively summing the values in floating point. Though it
         * does not guarenty the best possible accuracy
         *
         * See: http://en.wikipedia.org/wiki/Kahan_summation_algorithm
         */

        double sum = 0;
        double c = 0;
        for(double d : array)
        {
            double y = d - c;
            double t = sum+y;
            c = (t - sum) - y;
            sum = t;
        }

        return sum;
    }

    public double median()
    {
        double[] copy = Arrays.copyOf(array, array.length);

        Arrays.sort(copy); 

        if(copy.length % 2 == 1)
            return copy[copy.length/2];
        else
            return copy[copy.length/2]/2+copy[copy.length/2+1]/2;//Divisions by 2 then add is more numericaly stable
    }

    public double mean()
    {
        return sum()/length();
    }

    public double skewness()
    {
        double mean = mean();
        
        double tmp = 0;
        
        for(double xi : array)
            tmp += pow(xi-mean, 3);
        
        return tmp / (pow(standardDeviation(), 3) * (array.length-1) );
    }

    public double kurtosis()
    {
        double mean = mean();
        
        double tmp = 0;
        
        for(double xi : array)
            tmp += pow(xi-mean, 4);
        
        return tmp / (pow(standardDeviation(), 4) * (array.length-1) ) - 3;
    }
    
    public double standardDeviation()
    {
        return sqrt(variance());
    }

    public DenseVector sortedCopy()
    {
        double[] copy = Arrays.copyOf(array, array.length);

        Arrays.sort(copy); 

        return new DenseVector(copy);
    }

    public double variance()
    {
        double mu = mean();
        double tmp = 0;

        double N = length();


        for(double x : array)
            tmp += pow(x-mu, 2)/N;
        
        return tmp;
    }

    public double dot(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");
        
        if(v instanceof SparceVector)//Let sparce do it, same both ways and sparce can do it efficently
            return ((SparceVector) v).dot(this);
        
        double dot = 0;
        for(int i = 0; i < length(); i++)
            dot += array[i] * v.get(i);
        
        return dot;
    }

    public DenseVector deepCopy()
    {
        return new DenseVector(Arrays.copyOf(array, array.length));
    }

    public Vec add(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = 0; i < length(); i++)
            dv.array[i] += c;
        
        return dv;
    }

    public Vec subtract(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = 0; i < length(); i++)
            dv.array[i] -= c;
        
        return dv;
    }

    public Vec multiply(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = 0; i < length(); i++)
            dv.array[i] *= c;
        
        return dv;
    }

    public Vec divide(double c)
    {
        DenseVector dv = new DenseVector(Arrays.copyOf(array, array.length));
        
        for(int i = 0; i < length(); i++)
            dv.array[i] /= c;
        
        return dv;
    }

    public Vec add(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");

        
        if(v instanceof SparceVector)//Sparce knows how to do this efficently
            return ((SparceVector) v).add(this);
        
        //Else also dense
        
        double[] ret = new double[length()];
        for(int i = 0; i < ret.length; i++)
            ret[i] = array[i] + v.get(i);
            
        return new DenseVector(ret);
    }

    public Vec subtract(Vec v)
    {
        if(this.length() != v.length())
            throw new ArithmeticException("Vectors must have the same length");
        
        //Subtractio isnt as clever...
        
        double[] ret = new double[length()];
        for(int i = 0; i < ret.length; i++)
            ret[i] = array[i] - v.get(i);
            
        return new DenseVector(ret);
    }
    
}