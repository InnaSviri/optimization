package ru.mipt.optimization.entity.optimizationProcedure;

import org.jscience.mathematics.structure.Field;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.supportive.Graphics;
import ru.mipt.optimization.supportive.Tuple;
import ru.mipt.optimization.algorithms.Algorithm;

import java.util.LinkedList;
import java.util.function.Function;

/**
 * Represents an optimization procedure for the given cost function.
 * Looks for an extremum of the cost function on the search space of X type using given optimization algorithm.
 * Created by Inna on 28.02.2017.
 * @param <X> type of the domain of the cost function.
 *           Extends {@link org.jscience.mathematics.vector.Vector}
 */
public class OptimizationProcedure <X extends Field<X>> {

    private double optimizationTime;
    private Algorithm algorithm; // the optimization algorithm
    private CostFunction<X> costFunction; // objective (cost) function to optimize
    private StopCriteria stopCriteria;

    private LinkedList<Tuple<Vector<X>, Double>> procedurePoints = new LinkedList<>(); // decision points of optimization procedure

    /**
     * Creates new OptimizationProcedure object for given costFunction and selected optimization algorithm.
     * Note: selected algorithm should be able to deal with given costFunction, otherwise the IllegalArgumentException is thrown
     * @param algorithm selected optimization algorithm
     * @param costFunction function to optimize
     * @param stopCriteria condition to stop optimization procedure
     * @throws IllegalArgumentException if selected algorithm can not optimize given cost function and if any argument is null
     */
    public OptimizationProcedure(Algorithm algorithm, CostFunction<X> costFunction, StopCriteria stopCriteria) {
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
    public void start(Vector<X> startPoint) {

        if (costFunction.apply(startPoint) == null) throw new IllegalArgumentException("Start point must be in the domain " +
                "of the given cost function! ");
        procedurePoints.add(new Tuple<Vector<X>, Double>(startPoint, costFunction.apply(startPoint)));
        optimize();
    }

    /**
     * Returns optimized decision,
     * i.e. the maximum of {@link OptimizationProcedure#costFunction costFunction}
     * after applying optimization {@link OptimizationProcedure#algorithm algorithm}.
     * Note: it is necessary to start optimization procedure first,
     * i.e. to use {@link OptimizationProcedure#start(Vector)}  start(X startPoint)} method.
     * @return {@link Tuple} of found optimal point and value of objective function in this point
     * @throws RuntimeException if optimization procedure has not been started
     */
    public Tuple<Vector<X>,Double> getOptimizedDecision() {
        if (procedurePoints.isEmpty()) throw new RuntimeException("Can't get optimal decision without starting optimization procedure." +
                " Use method start(X startPoint) first");
        return procedurePoints.getLast();
    }

    //TODO not only cost function value versus iteration number
    /**
     *Draws evolution of cost function in the optimization process, i.e cost function value versus iteration number
     */
    public void visualizeProcedure() {
        Graphics.drawPlot(getCostFunctionEvolution()); //TODO not only cost function but Tuple
    }

    //TODO remove
    //Returns list of cost function points in its evolution by optimization procedure order
    private LinkedList<Double> getCostFunctionEvolution() {
        LinkedList<Double> costFunctionEvolution = new LinkedList<>();
        for (Tuple<Vector<X>, Double> point: procedurePoints) costFunctionEvolution.add(point.y);
        return costFunctionEvolution;
    }

    //optimizes costFunction using algorithm and stopCriteria
    private void optimize() {
        if (procedurePoints.isEmpty()) throw new RuntimeException("Can't optimize without start point. Use method start(X startPoint)");

        Tuple<Vector<X>, Double> curPoint = procedurePoints.getLast();
        Vector<X> nextPoint = algorithm.conductOneIteration(curPoint.x, costFunction);
        procedurePoints.add(new Tuple<Vector<X>, Double>(nextPoint, costFunction.apply(nextPoint)));
        if (!stopCriteria.isAchieved()) optimize();
    }

    //---------------------------------------- getters -----------------------------------------------------------------
    public double getOptimizationTime() {
        return optimizationTime;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public CostFunction<X> getCostFunction() {
        return costFunction;
    }

    public StopCriteria getStopCriteria() {
        return stopCriteria;
    }
}
