package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import java.util.List;
import java.util.function.Function;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;

/**
 * Represents cost function and its domain
 *
 * Created by Inna on 29.05.2017.
 */
public abstract class CostFunction implements Function<Vector<Real>, Double> {

    protected final Config config; //interval of the domain search vision TODO replace to the config data class
    private Function<Vector<Real>, Double> functionRule; // rule for mapping argument in its Double cost
    private final int dimension;

    public CostFunction(Function<Vector<Real>, Double> functionRule, int dimension, Config configurations) {
        if (functionRule == null) throw new IllegalArgumentException("function rule can't be null");
        if (configurations == null) throw new IllegalArgumentException("configurations can't be null");
        this.functionRule = functionRule;
        this.config = configurations;
        this.dimension = dimension;
    }

    @Override
    public Double apply(Vector<Real> vector) {
        return functionRule.apply(vector);
    }

    @Override
    public <V> Function<V, Double> compose(Function<? super V, ? extends Vector<Real>> before) {
        return functionRule.compose(before);
    }

    @Override
    public <V> Function<Vector<Real>, V> andThen(Function<? super Double, ? extends V> after) {
        return functionRule.andThen(after);
    }

    /**
     * Returns given point with corrected to the search range elements
     * @param toCorrect - point to correct
     * @param directionPoint - direction point to correct given point in the given direction.
     *                       If is null returned point would not be checked on belonging to the domain.
     * @return given point with corrected to the search range elements
     */
    public Vector<Real> correctToSearchRange(Vector<Real> toCorrect, Vector<Real> directionPoint) {
        if (toCorrect == null) throw new IllegalArgumentException("Point to correct can't be null!");
        checkDimension(toCorrect);
        Vector res = correctToSearchRange(toCorrect);
        if (directionPoint != null && apply(res) == null)
            res = getNearestDomainPoint(res, directionPoint);
        return res;
    }

    //corrects to the search range without domain check
    protected Vector<Real> correctToSearchRange(Vector<Real> out) {
        Real[] toWrite = new Real[out.getDimension()];
        for (int i = 0; i<out.getDimension(); i++) {
            if (out.get(i).doubleValue() < config.searchRange[0]) {
                toWrite[i] = Real.valueOf(config.searchRange[0]);
            } else if (out.get(i).doubleValue()>config.searchRange[1]) {
                toWrite[i] = Real.valueOf(config.searchRange[1]);
            } else toWrite[i] = out.get(i);
        }
        return DenseVector.valueOf(toWrite);
    }

    /**
     * Returns nearest domain point to the given point not in domain.
     * Search of the domain point is performed with specified in the
     * {@link ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction#config}
     * accuracyOfDomainSearch in the direction to the given directionPoint.
     * @param pointNotInDomain - point not in the domain of the cost function
     * @param directionPoint - point to specify the search direction
     * @return nearest domain point to the given point not in domain
     */
    public abstract Vector<Real> getNearestDomainPoint(Vector<Real> pointNotInDomain,
                                              Vector<Real> directionPoint);

    /**
     * Returns partial derivative in the given direction of this cost function in the given point
     * @param x point in the domain of the cost function to calculate derivative in.
     * @param direction - dimension to calculate derivative in. Must be within bounds [0;x.dimension-1]
     * @return partial derivative in the given direction of this cost function in given point
     * @throws IllegalArgumentException if the given point is out of the domain of the cost function
     *          or of direction is not within bounds.
     */
    public abstract double getPartialDerivative (Vector<Real> x, int direction);

    /**
     * Returnes gradient in the given point x
     * @param x - point in the domain of the cpst function to caalculate gradient in
     * @return gradient in the given point x
     * @throws IllegalArgumentException if the given point is out of the domain of the cost function
     */
    public abstract Vector<Real> getGradient(Vector<Real> x);

    /**
     * Retirnes gradients in nearest to x n points
     * @param x - point to calculate subgradients
     * @param area - area to calculate gradients
     * @return gradients in nearest to x n points
     */
    public abstract List<Vector<Real>> getSubGradients(Vector<Real> x, double area);


    protected void checkDimension(Vector<Real> toCheck) {
        if (toCheck.getDimension() != dimension)
            throw new IllegalArgumentException("Dimension of the given point is wrong!");
    }

    //------------------------------------------------------------------------------------------------------------------

    public Function<Vector<Real>, Double> getFunctionRule() {
        return functionRule;
    }

    public Config getConfig() {
        return config;
    }

    public int getDimension() {
        return dimension;
    }
}
