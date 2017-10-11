package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;

import java.util.function.Function;

/**
 * Represents gradient descent optimizatio algorithm.
 * Default Stop Criteria is based on byCostFuncChangeRate and byArgumentsChangeRate common template conditions.
 * See {@link ru.mipt.optimization.algorithms.PureAlgorithm.CommonStopping}  for details.
 * Created by Inna on 06.10.2017.
 */
public class GradientDescent extends PureAlgorithm {
    private static final double DEFAULT_STEP = 1.2;
    private double step;

    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function) {
        double d = function.getDerivative(x);

        Real[] gradReal = new Real[x.getDimension()];
        for (int i = 0; i < x.getDimension(); i++ )
            gradReal[i] =  Real.valueOf(step*(-d/10));
        return DenseVector.valueOf(gradReal);
    }

    @Override
    protected void setDefaultParameters() {
        stopCriteria = new CommonStopping(true,true,true,false);
        step = DEFAULT_STEP;
    }

    @Override
    public boolean isAble(Function<Vector<Real>, Double> function) {
        // TODO: 06.10.2017 inspect function
        return true;
    }

    /**
     * Configures algorithm parameters.
     * @param params - algorithm parameters in the strict order:
     *               step - value of the step of the algorithm.
     *               If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters corresponds required one.
     */
    @Override
    public boolean serParams(double... params) {
        if (params.length != 1) return false;
        step = params[0];
        return true;
    }

    @Override
    public String getName() {
        return "Gradient descent";
    }

}
