package ru.mipt.optimization.supportive;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for mathematics, i.e. for calculation domain of the function and minimum distance
 * Created by Inna on 21.07.2017.
 */
public class MathHelp {

    /*public static ??? calculateDomain ??? */

    public static double getDistance(Vector<Real> one, Vector<Real> two) {
        if (one == null || two == null
                || one.getDimension() != two.getDimension())
            throw new IllegalArgumentException("Can't find distance: given vectors have different dimensions or are null.");

        return norm(two.minus(one));
    }

    public static Vector<Real> addDistance(Vector<Real> vector, Vector<Real> direction, Double distance) {
        return vector.plus(normal(direction.minus(vector)).times(Real.valueOf(distance)));
    }

    public static double norm(Vector<Real> x) {
        double res = 0;
        for (int i=0; i<x.getDimension(); i++){
            res += Math.pow(x.get(i).doubleValue(), 2);
        }
        return Math.sqrt(res);
    }

    public static Vector<Real> normal(Vector<Real> x) {
        return (norm(x)==0) ? getTwinVector(x.getDimension(), Real.ZERO)
                : x.times(Real.valueOf(1/norm(x)));
    }

    /**
     * Returns vector with all identical elements value of r parameter
     * @param dim - dimension of required vector
     * @param r - value to set to all the elements of the vector
     * @return vector with all identical elements value of r parameter
     */
    public static Vector<Real> getTwinVector(int dim, Real r) {
        Real[] reals = new Real[dim];
        for (int i = 0; i < dim; i++ )
            reals[i] = r;
        return DenseVector.valueOf(reals);
    }

}
