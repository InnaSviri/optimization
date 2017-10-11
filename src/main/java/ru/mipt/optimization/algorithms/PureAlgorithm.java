package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;

import java.util.function.Function;

/**
 * An abstract implementation of the Algorithm interface that covers pure optimization algorithms, not hybrid variations.
 * Created by Inna on 26.02.2017.
 */
public abstract class PureAlgorithm implements Algorithm {

    protected StopCriteria stopCriteria;

    {setDefaultParameters();}

    @Override
    public Vector<Real> conductOneIteration(Vector<Real> x, CostFunction function) throws IllegalArgumentException {
        //// TODO: 29.05.2017

        Vector<Real> res = x.plus(getAlgorithmStep(x,function));
        if (function.apply(res) == null)
            res = function.getNearestDomainPoint(res, x);
        return res;
    }

    @Override
    public StopCriteria getStopCriteria() {
        return stopCriteria;
    }


    /**
     * Configures stop criteria by the common template.
     * See {@link ru.mipt.optimization.algorithms.PureAlgorithm.CommonStopping}  for details.
     * @param error - error of the optimization process
     * @param conditions - 4 flags to switch over 4 stop conditions, namely, in the strict order:
     *            byDecisionProximity - if true turns on consideration of the decision proximity condition;
     *            byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
     *            byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
     *            byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
     * @throws IllegalArgumentException if condition length is not equal to 4.
     */
    @Override
    public void configureStopCriteria(double error, boolean... conditions) {
        if (conditions.length != 4)
            throw new IllegalArgumentException("Wrong length of conditions argument!" +
                    "For configuration stop criteria by the common template  are necessary four criteria.");
        stopCriteria = new CommonStopping(conditions[0],conditions[1], conditions[2], conditions[3],error);
    }

    // returns delta vector to add to the current point x
    protected abstract Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function);

    protected void setDefaultParameters() {
        stopCriteria = new CommonStopping(true,true,true,true);
    }

    //----------------------------------------inner---------------------------------------------------------------------

    /**
     * Represents set of common conditions to stop optimization procedure, such as:
     * decision proximity - considers closeness of the obtained decision to the optimum according to determined error;
     * cost function change rate - considers current rate of change of the cost function in optimization process;
     * arguments change rate - considers current rate of change of the argument of the cost function in optimization process;
     * constraints fulfillment - controls accuracy of constraints fulfillment.
     */
    protected static class CommonStopping extends StopCriteria {

        private boolean byDecisionProximity;
        private boolean byCostFuncChangeRate;
        private boolean byArgumentsChangeRate;
        private boolean byConstraintsFulfillment;

        double epsilon = DEFAULT_ERROR;

        /**
         * Creates CommonStopping object with configured set of stop conditions
         * @param byDecisionProximity - if true turns on consideration of the decision proximity condition
         * @param byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
         * @param byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
         * @param byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
         */
        public CommonStopping(boolean byDecisionProximity, boolean byCostFuncChangeRate, boolean byArgumentsChangeRate, boolean byConstraintsFulfillment) {
            this.byDecisionProximity = byDecisionProximity;
            this.byCostFuncChangeRate = byCostFuncChangeRate;
            this.byArgumentsChangeRate = byArgumentsChangeRate;
            this.byConstraintsFulfillment = byConstraintsFulfillment;
        }

        /**
         * Creates CommonStopping object with configured set of stop conditions and determined error
         * @param byDecisionProximity - if true turns on consideration of the decision proximity condition
         * @param byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
         * @param byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
         * @param byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
         */
        public CommonStopping(boolean byDecisionProximity, boolean byCostFuncChangeRate, boolean byArgumentsChangeRate, boolean byConstraintsFulfillment, double error) {
            epsilon = error;
            new CommonStopping(byDecisionProximity,byCostFuncChangeRate,
                    byArgumentsChangeRate, byConstraintsFulfillment);
        }

        @Override
        protected boolean specifiedCriteria(OptimizationProcedure optimizationProcedure) {
            boolean decisionProximity = !byDecisionProximity
                    || checkForDecisionProximity(optimizationProcedure);
            boolean constraintsFulfillment = !byConstraintsFulfillment
                    || checkForConstraintsFulfillment(optimizationProcedure);
            boolean costFuncChangeRate = !byCostFuncChangeRate
                    || checkForCostFuncChangeRate(optimizationProcedure);
            boolean argumentsChangeRate = !byArgumentsChangeRate
                    || checkForArgumentsChangeRate(optimizationProcedure);

            return (decisionProximity&&constraintsFulfillment) || (costFuncChangeRate && argumentsChangeRate);
        }

        //--------------------------------------------------------------------------------------------------------------

        private boolean checkForDecisionProximity(OptimizationProcedure optProc) {
            Vector<Real> xk = optProc.getProcedurePoints().peekLast();
            return Math.abs(optProc.getCostFunction().getDerivative(xk)) <= epsilon;
        }

        // TODO: 06.10.2017 add constraints consideration
        private boolean checkForConstraintsFulfillment(OptimizationProcedure optProc) {
            return true;
        }

        private boolean checkForCostFuncChangeRate(OptimizationProcedure optProc) {
            double epsilon1 = 100*epsilon;
            int size = optProc.getProcedurePoints().size();
            Vector<Real> xk = optProc.getProcedurePoints().get(size-1);
            Vector<Real> xkMinus = optProc.getProcedurePoints().get(size-2);
            return Math.abs(optProc.getCostFunction().apply(xkMinus) - optProc.getCostFunction().apply(xk))
                    <= epsilon1 * (1 + Math.abs(optProc.getCostFunction().apply(xk)));
        }

        private boolean checkForArgumentsChangeRate(OptimizationProcedure optProc){
            double delta = Math.sqrt(100*epsilon);
            int size = optProc.getProcedurePoints().size();
            Vector<Real> xk = optProc.getProcedurePoints().get(size-1);
            Vector<Real> xkMinus = optProc.getProcedurePoints().get(size-2);
            boolean res = true;
            Vector<Real> subtraction = xkMinus.minus(xk);
            for (int i=0; i<subtraction.getDimension(); i++)
                if (Math.abs(subtraction.get(i).doubleValue())
                        > delta * (1 + Math.abs(xk.get(i).doubleValue())) ) {
                    res = false;
                    break;
                }
            return res;
        }


    }
}
