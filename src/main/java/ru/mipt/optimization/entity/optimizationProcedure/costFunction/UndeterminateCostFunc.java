package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.supportive.MathHelp;

import java.util.Map;
import java.util.function.Function;

/**
 * Created by Inna on 21.07.2017.
 * Represents the cost function which domain is unknown.
 */
public class UndeterminateCostFunc extends CostFunction  {

    private Map<Vector<Real>, Vector<Real>> covered;

    /**
     * Creates new UndeterminateCostFunc with specified accuracy.
     * @param functionRule - rule for mapping X in its Double cost
     * @param config - configurations, including interval of domain search vision and the search range
     */
    public UndeterminateCostFunc(Function<Vector<Real>, Double> functionRule, Config config) {
        super(functionRule, config);
    }


    @Override
    public Vector<Real> getNearestDomainPoint(Vector<Real> pointNotInDomain,
                                     Vector<Real> directionPoint) {
        if (apply(pointNotInDomain) != null) throw new IllegalArgumentException("argument pointNotInDomain " +
                "can't be in the domain of the function");
        Vector<Real> find = DenseVector.valueOf(directionPoint);
        domainSearch(correctToSearchRange(pointNotInDomain), find, 1);
        return find;
    }

    // writes in variable "in" nearest to the "out" domain point
    private void domainSearch(Vector<Real> out, Vector<Real> in, int iteration) {

        if (iteration > (config.searchRange[1] - config.searchRange[0])/config.accuracyOfDomainSearch) return;
        Double curDistance = MathHelp.getDistance(out,in) /(2*iteration);
        Vector<Real> curPoint = MathHelp.addDistance(out,in, curDistance);
        if (apply(curPoint) != null) {
            in = curPoint;
            iteration = 1;
            domainSearch(out,in,iteration);
        } else if (curDistance > config.accuracyOfDomainSearch) {
            iteration++;
            domainSearch(out,in,iteration);
        } else if (iteration != 1) {
            domainSearch(curPoint, in, 1);
        }
    }

    //corrects all vector elements to the search range value
    private Vector<Real> correctToSearchRange(Vector<Real> out) {
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
}
