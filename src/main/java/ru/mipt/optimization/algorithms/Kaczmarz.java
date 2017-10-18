package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.supportive.MathHelp;

import java.util.function.Function;

/**
 * Represents an identification algorithm for solving linear equation systems.
 * Had been adopted for optimization problem
 * (see {@link ru.mipt.optimization.algorithms.Kaczmarz#getAlgorithmStep(Vector, CostFunction)})
 * but can be used for identification problem as well
 * (see {@link ru.mipt.optimization.algorithms.Kaczmarz#getAlgorithmStep(Vector, Vector, Real)}).
 * Created by Inna on 18.10.2017.
 */
public class Kaczmarz extends PureAlgorithm{

    private static final double DEFAULT_RELAX_PARAM = 0.2;
    private double relaxationParameter;

    /**
     * Represents the step of the Kaczmarz algorithm for identification problem.
     * Returns algorithm's step (delta vector) to add to the current approximation of the vector x
     * @param x current approximation of the vector
     * @param ai the ith row of complex-valued matrix A. Note: dimension of the ai must coincide x's dimension.
     * @param bi the constant terms vector
     * @return the vector-step of the algorithm to add to the current approximation of the vector x
     * @throws IllegalArgumentException if dimension of ai doesn't coincide x's dimension
     */
    public Vector<Real> getAlgorithmStep(Vector<Real> x, Vector<Real> ai, Real bi) {
        if (x.getDimension() != ai.getDimension())
            throw new IllegalArgumentException("Dimension of ai doesn't coincide x's dimension");
        Double norm = MathHelp.norm(ai);
        Real normReal = Real.valueOf(norm);
        return ai.times(bi.minus(ai.times(x)).divide(normReal.times(normReal)));
    }

    @Override
    public boolean isAble(Function<Vector<Real>, Double> function) {
        // TODO: 18.10.2017 add checking 
        return false;
    }

    @Override
    protected void setDefaultParameters() {
        super.setDefaultParameters();
        relaxationParameter = DEFAULT_RELAX_PARAM;
    }

    /**
     * Configures algorithm parameters.
     * @param params - algorithm parameters in the strict order:
     *               relaxationParameter - value of the step of the algorithm.
     *               If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters corresponds required one.
     */
    @Override
    public boolean setParams(double... params) {
        if (params.length != 1) return false;
        relaxationParameter = params[0];
        return true;
    }

    @Override
    public String getName() {
        return "Kaczmarz";
    }

    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function) {
        return getAlgorithmStep(x, function.getGradient(x),Real.ZERO);
    }
}
