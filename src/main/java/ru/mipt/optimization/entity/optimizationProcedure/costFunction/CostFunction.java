package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import java.util.function.Function;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;

/**
 * Represents cost function and its domain
 *
 * Created by Inna on 29.05.2017.
 */
public abstract class CostFunction implements Function<Vector<Real>, Double> {

    protected final Double accuracy; //interval of the domain search vision TODO replace to the config data class
    private Function<Vector<Real>, Double> functionRule; // rule for mapping argument in its Double cost

    public CostFunction(Function<Vector<Real>, Double> functionRule, Double accuracy) {
        if (functionRule == null) throw new IllegalArgumentException("function rule can't be null");
        if (accuracy == null) throw new IllegalArgumentException("accuracy can't be null");
        this.functionRule = functionRule;
        this.accuracy = accuracy;
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
     * Returns nearest domain point to the given point not in domain.
     * Search of the domain point is performed with specified
     * {@link ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction#accuracy}
     * in the direction to the given directionPoint.
     * @param pointNotInDomain - point not in the domain of the cost function
     * @param directionPoint - point to specify the search direction
     * @return nearest domain point to the given point not in domain
     */
    public abstract Vector<Real> getNearestDomainPoint(Vector<Real> pointNotInDomain,
                                              Vector<Real> directionPoint);

    //------------------------------------------------------------------------------------------------------------------

    public Function<Vector<Real>, Double> getFunctionRule() {
        return functionRule;
    }

    public Double getAccuracy() {
        return accuracy;
    }
}
