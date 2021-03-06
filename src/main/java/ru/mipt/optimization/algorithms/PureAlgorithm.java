package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.supportive.MathHelp;

import java.util.function.Function;

/**
 * An abstract implementation of the Algorithm interface that covers pure optimization algorithms, not hybrid variations.
 * Created by Inna on 26.02.2017.
 */
public abstract class PureAlgorithm implements Algorithm {

    protected StopCriteria stopCriteria;

    {setDefaultParameters();}

    @Override
    public Vector<Real> conductOneIteration(Vector<Real> x, CostFunction function,
                                            VaryingParams varPar) throws IllegalArgumentException {

        Vector<Real> step = getAlgorithmStep(x,function, varPar);
        step = function.correctToSearchRange(step, null);

        Vector<Real> res = x.plus(step);
        if (function.apply(res) == null)
            res = function.getNearestDomainPoint(res, x);
       // else res = function.correctToSearchRange(res, x);
        return res;
    }

    @Override
    public StopCriteria getStopCriteria() {
        return stopCriteria;
    }


    /**
     * Configures stop criteria by the common template.
     * See {@link ru.mipt.optimization.algorithms.PureAlgorithm.CommonStopping}  for details.
     * @param error - errors array of the optimization process in the strict order:
     *              epsilon - accuracy for evaluating stop conditions.
     *              If size of errors is less than required, rest parameters will be default.
     * @param conditions - 5 flags to switch over 5 stop conditions, namely, in the strict order:
     *            byDecisionProximity - if true turns on consideration of the decision proximity condition;
     *            byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
     *            byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
     *            byArgumentsChangeNorm - if true turns on consideration of the arguments change rate nprm condition
     *            byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
     * @throws IllegalArgumentException if condition length is not equal to 5.
     */
    @Override
    public void configureStopCriteria(double[] error, boolean... conditions) {
        if (conditions.length != 5)
            throw new IllegalArgumentException("Wrong length of conditions argument!" +
                    "For configuration stop criteria by the common template  are necessary five criteria.");
        if (error.length < 1)
            stopCriteria = new CommonStopping(conditions[0],conditions[1],conditions[2],conditions[3],conditions[4]);
        else stopCriteria =
                new CommonStopping(conditions[0],conditions[1],conditions[2],conditions[3],conditions[4],error[0]);
    }

    @Override
    public String print() {
        return getName() + ":  " + printParams() + "; "
                + stopCriteria.toString();
    }

    @Override
    public VaryingParams getVaryingParamsConfiguration() {
        return new VaryingParams();
    }

    // returns delta vector to add to the current point x
    protected abstract Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function, VaryingParams varyingParams);

    // prints algorithms configuration parameters
    protected abstract String printParams();

    protected void setDefaultParameters() {
        stopCriteria = new CommonStopping(true,true,true,true, true);
    }

    //----------------------------------------inner---------------------------------------------------------------------

    /**
     * Represents set of common conditions to stop optimization procedure, such as:
     * decision proximity - considers closeness of the obtained decision to the optimum according to determined error;
     * cost function change rate - considers current rate of change of the cost function in optimization process;
     * arguments change rate - considers current rate of change of the argument of the cost function in optimization process;
     * arguments change rate norm - considers norm of the current rate of change
     *                              of the argument of the cost function in optimization process;
     * constraints fulfillment - controls accuracy of constraints fulfillment.
     */
    protected static class CommonStopping extends StopCriteria {

        private boolean byDecisionProximity;
        private boolean byCostFuncChangeRate;
        private boolean byArgumentsChangeRate;
        private boolean byArgumentsChangeRateNorm;
        private boolean byConstraintsFulfillment;

        /**
         * Creates CommonStopping object with configured set of stop conditions
         * @param byDecisionProximity - if true turns on consideration of the decision proximity condition
         * @param byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
         * @param byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
         * @param byArgumentsChangeRateNorm - if true turns on consideration of the arguments change rate norm condition
         * @param byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
         */
        public CommonStopping(boolean byDecisionProximity, boolean byCostFuncChangeRate,
                              boolean byArgumentsChangeRate, boolean byArgumentsChangeRateNorm, boolean byConstraintsFulfillment) {
            this.byDecisionProximity = byDecisionProximity;
            this.byCostFuncChangeRate = byCostFuncChangeRate;
            this.byArgumentsChangeRate = byArgumentsChangeRate;
            this.byArgumentsChangeRateNorm = byArgumentsChangeRateNorm;
            this.byConstraintsFulfillment = byConstraintsFulfillment;
        }

        /**
         * Creates CommonStopping object with configured set of stop conditions and determined error
         * @param byDecisionProximity - if true turns on consideration of the decision proximity condition
         * @param byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
         * @param byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
         * @param byArgumentsChangeRateNorm - if true turns on consideration of the arguments change rate norm condition
         * @param byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
         * @param epsilon accuracy for evaluating stop conditions
         */
        public CommonStopping(boolean byDecisionProximity, boolean byCostFuncChangeRate, boolean byArgumentsChangeRateNorm,
                              boolean byArgumentsChangeRate, boolean byConstraintsFulfillment, double epsilon) {
            this.error = epsilon;
            this.byDecisionProximity = byDecisionProximity;
            this.byCostFuncChangeRate = byCostFuncChangeRate;
            this.byArgumentsChangeRate = byArgumentsChangeRate;
            this.byArgumentsChangeRateNorm = byArgumentsChangeRateNorm;
            this.byConstraintsFulfillment = byConstraintsFulfillment;
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
            boolean argumentsChangeRateNorm = !byArgumentsChangeRateNorm
                    || checkForArgumentsChangeRateNorm(optimizationProcedure);

            return decisionProximity && constraintsFulfillment && costFuncChangeRate
                    && argumentsChangeRate && argumentsChangeRateNorm;
        }

        @Override
        protected String getName() {
            return "CommonStopping";
        }

        @Override
        protected String printParams() {
            return "byArgumentsChangeRateNorm = " + byArgumentsChangeRateNorm
                    + "; byArgumentsChangeRate = " + byArgumentsChangeRate
                    + "; byCostFuncChangeRate = " + byCostFuncChangeRate
                    + "; byConstraintsFulfillment = " + byConstraintsFulfillment
                    + "; byDecisionProximity = " + byDecisionProximity;
        }

        //--------------------------------------------------------------------------------------------------------------

        private boolean checkForDecisionProximity(OptimizationProcedure optProc) {
            Vector<Real> xk = optProc.getProcedurePoints().peekLast();
            boolean res = true;
            for (int i = 0; i < xk.getDimension(); i++)
                if (Math.abs(optProc.getCostFunction().getPartialDerivative(xk, i)) > error){
                    res = false;
                    break;
                }

            return res;
        }

        // TODO: 06.10.2017 add constraints consideration
        private boolean checkForConstraintsFulfillment(OptimizationProcedure optProc) {
            return true;
        }

        private boolean checkForCostFuncChangeRate(OptimizationProcedure optProc) {
            double epsilon1 = 100*error;
            int size = optProc.getProcedurePoints().size();
            Vector<Real> xk = optProc.getProcedurePoints().get(size-1);
            Vector<Real> xkMinus = optProc.getProcedurePoints().get(size-2);
            return Math.abs(optProc.getCostFunction().apply(xkMinus) - optProc.getCostFunction().apply(xk))
                    <= epsilon1;
        }

        private boolean checkForArgumentsChangeRate(OptimizationProcedure optProc){
            double delta = Math.sqrt(100*error);
            int size = optProc.getProcedurePoints().size();
            Vector<Real> xk = optProc.getProcedurePoints().get(size-1);
            Vector<Real> xkMinus = optProc.getProcedurePoints().get(size-2);
            boolean res = true;
            Vector<Real> subtraction = xkMinus.minus(xk);
            for (int i=0; i<subtraction.getDimension(); i++)
                if (Math.abs(subtraction.get(i).doubleValue()) > delta) {
                    res = false;
                    break;
                }
            return res;
        }

        private boolean checkForArgumentsChangeRateNorm(OptimizationProcedure optProc){

            int size = optProc.getProcedurePoints().size();
            Vector<Real> xk = optProc.getProcedurePoints().get(size-1);
            Vector<Real> xkMinus = optProc.getProcedurePoints().get(size-2);
            Vector<Real> subtraction = xkMinus.minus(xk);

            return MathHelp.norm(subtraction) < error;
        }

    }
}
