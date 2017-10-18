package ru.mipt.optimization.entity.optimizationProcedure.costFunction;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.supportive.MathHelp;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Inna on 21.07.2017.
 * Represents the cost function which domain is unknown.
 */
public class UndeterminateCostFunc extends CostFunction  {

    private MultiKeyMap<Vector<Real>, Vector<Real>> covered = new MultiKeyMap<>();

    private int recursionNum = 0;
    private Map.Entry<Vector<Real>, Vector<Real>> currentSearchPare;

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

        Vector<Real> find = covered.get(pointNotInDomain, directionPoint);
        if ( find != null) return find;
        initiateSearch(pointNotInDomain, directionPoint);
        return covered.get(pointNotInDomain,directionPoint);
    }

    @Override
    public double getPartialDerivative(Vector<Real> x, int dir) {
        if (dir < 0 || dir > x.getDimension()-1)
            throw new IllegalArgumentException("Given direction isn't within its bounds!");
        Real[] reals = new Real[x.getDimension()];
        for (int i = 0; i < reals.length; i++ )
            if (i == dir) reals[i] = x.get(i).plus(Real.valueOf(config.accuracyOfDomainSearch));
            else reals[i] =  x.get(i);
        Vector<Real> xPlus = DenseVector.valueOf(reals);

        Double f = apply(x);
        if (f == null)
            throw new IllegalArgumentException("Given point x is out of the domain. Can't calculate partial derivative!");

        Double fPlus = apply(xPlus);
        if(fPlus == null)
            fPlus = apply(getNearestDomainPoint(xPlus, x));

        double d = fPlus-f;
        if (d == 0) d= 0.00001;
        return d/(config.accuracyOfDomainSearch);
    }

    @Override
    public Vector<Real> getGradient(Vector<Real> x) {
        Real[] reals = new Real[x.getDimension()];
        for (int i = 0; i < reals.length; i++ ) {
            reals[i] = Real.valueOf(getPartialDerivative(x, i));
        }
        return DenseVector.valueOf(reals);
    }

    // writes in variable "in" nearest to the "out" domain point
    private void domainSearch(Vector<Real> out, Vector<Real> in, int iteration) {
        if (recursionNum > config.getMaxRecursionNumber()) return;
        recursionNum++;

        Double curDistance = MathHelp.getDistance(out,in) /(2*iteration);
        Vector<Real> curPoint = MathHelp.addDistance(out,in, curDistance);
        if (apply(curPoint) != null) {
            covered.put(currentSearchPare.getKey(),currentSearchPare.getValue(),curPoint);
            iteration = 1;
            if (curDistance > config.accuracyOfDomainSearch) domainSearch(out,curPoint,iteration);
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

    private void initiateSearch(Vector<Real> pointNotInDomain, Vector<Real> directionPoint) {
        recursionNum = 0;
        currentSearchPare = new AbstractMap.SimpleEntry<Vector<Real>, Vector<Real>>(pointNotInDomain,
                directionPoint);

        Vector<Real> correctedToRange = correctToSearchRange(pointNotInDomain);
        covered.put(pointNotInDomain, directionPoint, directionPoint);
        if (apply(correctedToRange) != null) covered.put(pointNotInDomain, directionPoint, correctedToRange);
        else domainSearch(correctedToRange, directionPoint, 1);
    }
}
