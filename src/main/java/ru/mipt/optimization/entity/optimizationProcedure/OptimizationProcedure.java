package ru.mipt.optimization.entity.optimizationProcedure;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.typeWrapper.FieldWrapper;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.supportive.Tuple;
import ru.mipt.optimization.algorithms.Algorithm;

import java.util.LinkedList;

/**
 * Represents an optimization procedure for the given cost function by the given algorithm.
 * Looks for an extremum of the cost function on the vector search space using given optimization algorithm.
 * Created by Inna on 28.02.2017.
 */
public class OptimizationProcedure {

    private Timer timer = new Timer();
    private final Config config; // configurations (selected optimization algorithm and condition to stop optimization procedure)
    private final CostFunction costFunction; // objective (cost) function to optimize

    private LinkedList<Vector<Real>> procedurePoints = new LinkedList<>(); // decision points of optimization procedure


    /**
     * Creates new OptimizationProcedure object for given costFunction
     * and selected configurations (optimization algorithm, stop criteria).
     * Note: selected algorithm should be able to deal with given costFunction, otherwise the IllegalArgumentException is thrown
     * @param costFunction function to optimize
     * @param config configurations of optimization procedure,
     *               including selected optimization algorithm and condition to stop optimization procedure
     * @throws IllegalArgumentException if selected algorithm can not optimize given cost function and if any argument is null
     */
    public OptimizationProcedure(CostFunction costFunction, Config config) {
        if (costFunction == null || config == null)
            throw new IllegalArgumentException("CostFunction and config can't be null");
        if (!config.algorithm.isAble(costFunction))
            throw new IllegalArgumentException(" Algorithm " + config.algorithm.getName()
                    + " can not optimize given cost function" );

        this.config = config;
        this.costFunction = costFunction;
    }

    /**
     * Starts optimization procedure of {@link OptimizationProcedure#costFunction costFunction}
     * with selected {@link OptimizationProcedure#config configurations}
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
     * after applying optimization algorithm from {@link OptimizationProcedure#config configurations}.
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
        timer.start();

        if (procedurePoints.isEmpty())
            throw new RuntimeException("Can't optimize without start point. Use method start(Vector startPoint)");

        Vector<Real> curPoint = procedurePoints.getLast();
        Vector<Real> nextPoint = config.algorithm.conductOneIteration(curPoint, costFunction);
        procedurePoints.add(nextPoint);
        if (!config.stopCriteria.isAchieved()) optimize();

        timer.stop();
    }

    //---------------------------------------- getters -----------------------------------------------------------------


    public LinkedList<Vector<Real>> getProcedurePoints() {
        return procedurePoints;
    }

    public double getOptimizationTime() {
        if (timer.getMemoredTime() == 0) throw new RuntimeException("Timer hasn't been started properly. " +
                "May be you forgot to start optimization procedure? Use method start(Vector startPoint).");
        return timer.getMemoredTime();
    }

    public Algorithm getAlgorithm() {
        return config.algorithm;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public StopCriteria getStopCriteria() {
        return config.stopCriteria;
    }

    public Config getConfigurations() { return config;}

    //---------------------------------------------inner----------------------------------------------------------------

    private class Timer {
        private long startTime = 0;
        private long endTime = 0;

        public Timer() {}

        public void start(){
            startTime = System.nanoTime();
            endTime = 0;
        }

        public void stop(){
            endTime = System.nanoTime();
        }

        public double getMemoredTime() {
            return (startTime != 0 && endTime != 0) ? (double) (endTime - startTime)/1000000000 : 0;
        }
    }
}