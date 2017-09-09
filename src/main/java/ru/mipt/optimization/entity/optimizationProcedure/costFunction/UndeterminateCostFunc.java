package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.supportive.MathHelp;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Inna on 21.07.2017.
 * Represents the cost function which domain is unknown.
 */
public class UndeterminateCostFunc extends CostFunction  {

    private Map<Vector<? extends FieldWrapper>, Vector<? extends FieldWrapper>> covered;

    /**
     * Creates new UndeterminateCostFunc with specified accuracy.
     * @param functionRule - rule for mapping X in its Double cost
     * @param accuracy - interval of domain search vision
     */
    public UndeterminateCostFunc(Function functionRule, Double accuracy) {
        super(functionRule, accuracy);
    }


    @Override
    public void correctPointToDomain(Vector<? extends FieldWrapper> pointNotInDomain,
                                     Vector<? extends FieldWrapper> directionPoint) {
        if (apply(pointNotInDomain) != null) throw new IllegalArgumentException("argument pointNotInDomain " +
                "can't be in the domain of the function");
        domainSearch(pointNotInDomain, directionPoint, 1);
    }

    // writes in variable "in" nearest to the "out" domain point
    private void domainSearch(Vector<? extends FieldWrapper> out, Vector<? extends FieldWrapper> in, int iteration) {
        Double curDistance = MathHelp.getDistance(out,in) /(2*iteration);
        Vector<? extends FieldWrapper> curPoint = MathHelp.addDistance(out,curDistance);
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
