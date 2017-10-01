package ru.mipt.optimization.entity.optimizationProcedure;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.supportive.Tuple;
import ru.mipt.optimization.algorithms.Algorithm;

import java.util.LinkedList;

/**
 * Represents an optimization procedure for the given cost function.
 * Looks for an extremum of the cost function on the vector search space using given optimization algorithm.
 * Created by Inna on 28.02.2017.
 */
public class OptimizationProcedure {

    private double optimizationTime;
    private Algorithm algorithm; // the optimization algorithm
    private CostFunction costFunction; // objective (cost) function to optimize
    private StopCriteria stopCriteria;

    private LinkedList<Vector<Real>> procedurePoints = new LinkedList<>(); // decision points of optimization procedure


    /**
     * Creates new OptimizationProcedure object for given costFunction and selected optimization algorithm.
     * Note: selected algorithm should be able to deal with given costFunction, otherwise the IllegalArgumentException is thrown
     * @param algorithm selected optimization algorithm
     * @param costFunction function to optimize
     * @param stopCriteria condition to stop optimization procedure
     * @throws IllegalArgumentException if selected algorithm can not optimize given cost function and if any argument is null
     */
    public OptimizationProcedure(Algorithm algorithm, CostFunction costFunction, StopCriteria stopCriteria) {
        if (algorithm == null || costFunction == null || stopCriteria == null)
            throw new IllegalArgumentException("Algorithm, costFunction and stopCriteria can't be null");
        if (!algorithm.isAble(costFunction))
            throw new IllegalArgumentException(" Algorithm " + algorithm.getName() + " can not optimize given cost function" );

        this.algorithm = algorithm;
        this.costFunction = costFunction;
        this.stopCriteria = stopCriteria;
    }

    /**
     * Starts optimization procedure of {@link OptimizationProcedure#costFunction costFunction}
     * by selected {@link OptimizationProcedure#algorithm algorithm}
     * @param startPoint point from which optimization algorithm starts
     * @throws IllegalArgumentException if given startPoint is not in the domain
     * of the {@link ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure#costFunction}
     */
    public void start(Vector<Real> startPoint) {
        if (costFunction.apply(startPoint) == null) throw new IllegalArgumentException("Start point must be in the domain " +
                "of the given cost function! ");
        procedurePoints.add(startPoint);
        optimize();
    }

    /**
     * Returns optimized decision,
     * i.e. the maximum of {@link OptimizationProcedure#costFunction costFunction}
     * after applying optimization {@link OptimizationProcedure#algorithm algorithm}.
     * Note: it is necessary to start optimization procedure first,
     * i.e. to use {@link OptimizationProcedure#start(Vector)}  method.
     * @return {@link Tuple} of found optimal point and value of objective function in this point
     * @throws RuntimeException if optimization procedure has not been started
     */
    public Tuple<Vector<Real>, Double> getOptimizedDecision() {
        if (procedurePoints.isEmpty()) throw new RuntimeException("Can't get optimal decision without starting optimization procedure." +
                " Use method start(X startPoint) first");
        return new Tuple<Vector<Real>, Double>(procedurePoints.getLast(),
                costFunction.apply(procedurePoints.getLast()));
    }


    //optimizes costFunction using algorithm and stopCriteria
    private void optimize() {
        if (procedurePoints.isEmpty())
            throw new RuntimeException("Can't optimize without start point. Use method start(Vector startPoint)");

        Vector<Real> curPoint = procedurePoints.getLast();
        Vector<Real> nextPoint = algorithm.conductOneIteration(curPoint, costFunction);
        procedurePoints.add(nextPoint);
        if (!stopCriteria.isAchieved()) optimize();
    }

    //---------------------------------------- getters -----------------------------------------------------------------


    public LinkedList<Vector<Real>> getProcedurePoints() {
        return procedurePoints;
    }

    public double getOptimizationTime() {
        return optimizationTime;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public StopCriteria getStopCriteria() {
        return stopCriteria;
    }
}
