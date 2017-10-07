package ru.mipt.optimization.entity.optimizationProcedure;

/**
 * Represents the stop criteria for the optimization procedure
 * Created by Inna on 08.04.2017.
 */
public abstract class StopCriteria {

    private static final int MAX_ITERATIONS_NUM = 1000000;

    protected final static double DEFAULT_ERROR = 0.1;

    /**
     * Checks if optimization procedure must be stopped.
     * Returns true if stop criteria is achieved for the given optimization procedure.
     * @param optimizationProcedure - optimization procedure to check
     * @return true if stop criteria is achieved for the given optimization procedure false otherwise.
     */
    public boolean isAchieved(OptimizationProcedure optimizationProcedure) {
        if (optimizationProcedure == null) throw new IllegalArgumentException("Can't check null optimization procedure!");
        int iterationsNumber = optimizationProcedure.getProcedurePoints().size();
        if (iterationsNumber == 0 || iterationsNumber == 1) return false;
        return specifiedCriteria(optimizationProcedure) || basicCriteria(iterationsNumber);
    }

    private boolean basicCriteria(int iterationsNumber) {
        return iterationsNumber >= MAX_ITERATIONS_NUM;
    }

    /**
     * Returns true if specified stop criteria is fulfilled for given optimization procedure
     * @param optimizationProcedure - optimization procedure to check
     * @return true if specified stop criteria is fulfilled for given optimization procedure
     */
    protected abstract boolean specifiedCriteria(OptimizationProcedure optimizationProcedure);

}
