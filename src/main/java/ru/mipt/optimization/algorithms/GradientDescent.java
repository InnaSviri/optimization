package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;

import java.util.function.Function;

/**
 * Represents gradient descent optimizatio algorithm.
 * Default Stop Criteria is based on byCostFuncChangeRate and byArgumentsChangeRate common template conditions.
 * See {@link ru.mipt.optimization.algorithms.PureAlgorithm.CommonStopping}  for details.
 * Created by Inna on 06.10.2017.
 */
public class GradientDescent extends PureAlgorithm {
    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, Function<Vector<Real>, Double> function) {
        Real[] reals = new Real[x.getDimension()];
        for (int i = 0; i < reals.length; i++ ) reals[i] = Real.valueOf(0.1);
        Vector<Real> delta = DenseVector.valueOf(reals);



        Double fPlus = null;
        Double fMinus = null;
        double j = 1;
        while (fPlus == null && j<100) {
            fPlus = function.apply(x.plus(delta.times(Real.valueOf(j))));
            j++;
        }
        double k = 1;
        while (fMinus == null && k<100) {
            fMinus = function.apply(x.minus(delta.times(Real.valueOf(k))));
            k++;
        }
        double d;
        if (fMinus == null || fPlus== null) d = 0.1;
         else d= fPlus-fMinus;
        if (d == 0) d= 0.00001;

        Real[] gradReal = new Real[x.getDimension()];
        for (int i = 0; i < reals.length; i++ )
            gradReal[i] = Real.valueOf(d/10);
        return DenseVector.valueOf(gradReal);
    }

    @Override
    protected void setDefaultParameters() {
        stopCriteria = new CommonStopping(false,true,true,false);
    }

    @Override
    public boolean isAble(Function<Vector<Real>, Double> function) {
        // TODO: 06.10.2017 inspect function
        return true;
    }

    @Override
    public String getName() {
        return "Gradient descent";
    }
}
