package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.supportive.MathHelp;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * Created by Inna on 21.07.2017.
 * Represents the cost function which domain is unknown.
 */
public class UndeterminateCostFunc <X extends Field<X>> extends CostFunction<X>  {

    private Map<Vector<X>, Vector<X>> covered;

    /**
     * Creates new UndeterminateCostFunc with specified accuracy.
     * @param functionRule - rule for mapping X in its Double cost
     * @param accuracy - interval of domain search vision
     */
    public UndeterminateCostFunc(Function functionRule, Double accuracy) {
        super(functionRule, accuracy);
    }


    @Override
    public void correctPointToDomain(Vector<X> pointNotInDomain, Vector<X> directionPoint) {
        if (apply(pointNotInDomain) != null) throw new IllegalArgumentException("argument pointNotInDomain " +
                "can't be in the domain of the function");
        domainSearch(pointNotInDomain, directionPoint, 1);
    }

    // writes in variable "in" nearest to the "out" domain point
    private void domainSearch(Vector<X> out, Vector<X> in, int iteration) {
        Double curDistance = MathHelp.getDistance(out,in) /(2*iteration);
        Vector<X> curPoint = MathHelp.addDistance(out,curDistance);
        if (apply(curPoint) != null) {
            in = curPoint;
            iteration = 1;
        } else
        if (curDistance > accuracy) {
            domainSearch(out,in,iteration);
        } else if (iteration != 1) {
            domainSearch(curPoint, in, 1);
        }
    }
}
