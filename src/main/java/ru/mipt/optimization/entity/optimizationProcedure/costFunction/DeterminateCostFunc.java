package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Inna on 21.07.2017.
 */
public class DeterminateCostFunc extends CostFunction {

    /* private ??? domain нужно чтобы
    и функция разрешенных значений
    и входит или не входит булен
    и найти ближайшее*/

    /**
     * Creates new DeterminateCostFunc with specified accuracy.
     * @param functionRule - rule for mapping X in its Double cost
     * @param dimension dimension of given function
     * @param config - configurations, including interval of domain search vision and the search range
     */
    public DeterminateCostFunc(Function functionRule, int dimension, Config config) {
        super(functionRule, dimension, config);
    }

    @Override
    public Vector<Real> getNearestDomainPoint(Vector<Real> pointNotInDomain,
                                     Vector<Real> directionPoint) {
        // TODO: 10.10.2017 realize
        return null;
    }

    @Override
    public double getPartialDerivative(Vector<Real> x, int dir) {
        // TODO: 11.10.2017 realize 
        return 0;
    }

    @Override
    public Vector<Real> getGradient(Vector<Real> x) {
        // TODO: 18.10.2017 realize 
        return null;
    }

    @Override
    public List<Vector<Real>> getSubGradients(Vector<Real> x, int n) {
        // TODO: 21.10.2017 realize
        return null;
    }
}
