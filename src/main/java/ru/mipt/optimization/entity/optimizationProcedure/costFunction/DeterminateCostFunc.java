package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;

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
     * @param config - configurations, including interval of domain search vision and the search range
     */
    public DeterminateCostFunc(Function functionRule, Config config) {
        super(functionRule, config);
    }

    @Override
    public Vector<Real> getNearestDomainPoint(Vector<Real> pointNotInDomain,
                                     Vector<Real> directionPoint) {
        // TODO: 10.10.2017 realize
        return null;
    }
}
