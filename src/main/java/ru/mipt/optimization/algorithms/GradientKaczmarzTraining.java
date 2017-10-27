package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.Optimizator;
import ru.mipt.optimization.entity.inOut.Config;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.UndeterminateCostFunc;
import ru.mipt.optimization.supportive.MathHelp;
import ru.mipt.optimization.supportive.Tuple;

import java.util.*;
import java.util.function.Function;

/**
 * Created by Inna on 19.10.2017.
 */
public class GradientKaczmarzTraining extends HybridAlgorithm {
    private static final int MAX_SUBGRAD_NUM = 5;

    private Kaczmarz kaczmarz;
    private CubicApproximation oneDimSearchAlgo;

    private GuideParameters guideParams;


    @Override
    List<PureAlgorithm> getPureAlgorithms() {
        List<PureAlgorithm> inners = new ArrayList<>();
        inners.add(kaczmarz);
        inners.add(oneDimSearchAlgo);
        return inners;
    }

    @Override
    public VaryingParams getVaryingParamsConfiguration() {
        return new VaryingParams(guideParams.ek, guideParams.mk);
    }

    @Override
    String printOwnParams() {
        return "MAX_SUBGRAD_NUM = " + MAX_SUBGRAD_NUM;
    }

    // TODO: 27.10.2017 remove from field
    private VaryingParams currentVarParams; // parameters to conduct current iteration 
    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function, VaryingParams varParams) {
        currentVarParams = varParams;
        return currentVarParams.anew ? outerLoop(x, function)
                : innerLoop(x, function, currentVarParams.curDirection, currentVarParams.prevGradient);
    }

    @Override
    public boolean isAble(CostFunction function) {
        return true;
    }

    /**
     * Configures algorithm parameters.
     * @param params - 2k+3 algorithm parameters in the strict order:
     *               k - number of iterations of the outer loop of the algorithm (is used Math.round for convertation);
     *               e1,...,ek - parameters for evaluation outer loop criteria;
     *               m1,...,mk - parameters for evaluation inner loop criteria;
     *               step - value of the step of the one dimension search algorithm;
     *               relaxationParameter - value of the step of the direction search algorithm
     *               If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters corresponds required 2k+3.
     */
    @Override
    public boolean setParams(double... params) {
        if (params.length < 1) return false;
        int k = (int)Math.round(params[0]);

        int i = 1;
        Queue<Double> ek = new LinkedList<>();
        Queue<Double> mk = new LinkedList<>();
        boolean res = true;
        while(i < params.length) {
            if (i<k+1) ek.add(params[i]);
            else if (i < 2*k+1) mk.add(params[i]);
            else if (i == 2*k+1) res = res & oneDimSearchAlgo.setParams(params[i]);
            else res = res && kaczmarz.setParams(params[i]);
            i++;
        }
        if (mk.size() == k && ek.size() == k)
            guideParams = new GuideParameters(ek,mk);
        else if (ek.size() == k)
            guideParams = new GuideParameters(ek, VaryingParams.DEFAULT_MK);

        return i == 2*k+2 && res;
    }

    @Override
    public String getName() {
        return "GradientKaczmarzTraining";
    }

    @Override
    protected void setDefaultParameters() {
        kaczmarz = new Kaczmarz();
        oneDimSearchAlgo = new CubicApproximation();
        super.setDefaultParameters();
        kaczmarz.setDefaultParameters();
        oneDimSearchAlgo.setDefaultParameters();
        stopCriteria = createDefaultM1Stopping();
        guideParams = new GuideParameters(VaryingParams.DEFAULT_EK, VaryingParams.DEFAULT_MK);
    }

    /**
     *
     * @param errors - 2 parameters to evaluate stop conditions, namely, in the strict order:
     *               errorOneDimAlgo - error for stopping one dimension search algorithm;
     *               error - accuracy for evaluating stop conditions in the CommonStopping criteria.
     *                      Can be absent only if flag of common stopping is off.
     * @param conditions - flags to switch over stop conditions. It size must be greater than 1.
     *            Namely, in the strict order:
     *            commonStopping - if true turns on consideration of
     *              {@link ru.mipt.optimization.algorithms.PureAlgorithm.CommonStopping} criteria.
     *              otherwise only M1 criteria is used. If true size of conditions must be 6.
     *              It is strongly recommended to use common stopping - M1 criteria works only on differentiable functions
     *            byDecisionProximity - if true turns on consideration of the decision proximity condition;
     *            byCostFuncChangeRate - if true turns on consideration of the cost function change rate condition
     *            byArgumentsChangeRate - if true turns on consideration of the arguments change rate condition
     *            byArgumentsChangeNorm - if true turns on consideration of the arguments change rate norm condition
     *            byConstraintsFulfillment - if true turns on consideration of the constraints fulfillment condition
     * @throws IllegalArgumentException if size of errors isn't 2 or size of conditions isn't 1(and first is false) or 6.
     */
    @Override
    public void configureStopCriteria(double[] errors, boolean... conditions) {
        if (errors.length < 1) throw new IllegalArgumentException(" Wrong size of errors. See java doc");
        double[] error = {errors[0]};
        oneDimSearchAlgo.configureStopCriteria(error, true);

        switch (conditions.length) {
            case 1 :
                if (conditions[0])
                    throw new IllegalArgumentException("For configuration CommonStopping size of conditions must 6!");
                stopCriteria = new M1Stopping();
                break;
            case 6:
                boolean[] commonStopping = new boolean[conditions.length-1];
                for (int i = 0; i<conditions.length; i++) {
                    if (i == 0) {
                        if (conditions[i]) continue;
                        else break;
                    }
                    commonStopping[i-1] = conditions[i];
                }
                if (errors.length < 2)
                    throw new IllegalArgumentException("For configuration CommonStopping size of errors must 2!");
                double[] commonStErr = {errors[1]};
                super.configureStopCriteria(commonStErr, commonStopping);
                stopCriteria = new M1Stopping(this.stopCriteria);
                break;
            default:
                throw new IllegalArgumentException("Size of conditions isn't 1 or 6");
        }
    }

    private M1Stopping createDefaultM1Stopping() {

        CommonStopping cs = new CommonStopping(true,true,true,true,true,0.1);
        return new M1Stopping(cs);
    }

    private Vector<Real> outerLoop(Vector<Real> x, CostFunction function) {
        Vector<Real> nulVec = MathHelp.getTwinVector(x.getDimension(), Real.ZERO);
        currentVarParams.curDirection = DenseVector.valueOf(nulVec);
        currentVarParams.prevGradient = DenseVector.valueOf(nulVec);
        return innerLoop(x, function, currentVarParams.curDirection, currentVarParams.prevGradient);
    }

    private Vector<Real> innerLoop(Vector<Real> x, CostFunction function, Vector<Real> si, Vector<Real> gPrev) {
        Vector<Real> curGrad = function.getGradient(x);
        if (curGrad.equals(MathHelp.getTwinVector(x.getDimension(), Real.ZERO))) {
            currentVarParams.done = true;
            return x;
        }
        if (currentVarParams.curDirection.times(curGrad).isLargerThan(Real.ZERO)) {
            List<Vector<Real>> subgradients = function.getSubGradients(x, MAX_SUBGRAD_NUM);
            Collections.sort(subgradients, getSubgradComparator());
            if (!subgradients.isEmpty()) curGrad = subgradients.get(0); //nonetheless required condition may be not fulfilled
        }

        Vector<Real> newDirection = kaczmarz.getAlgorithmStep(currentVarParams.curDirection, curGrad, getPi(curGrad), Real.ONE)
                .plus(currentVarParams.curDirection);
        currentVarParams.curDirection = newDirection;
        Double gamma = getOptimizedGamma(x,function);
        currentVarParams.prevGradient = curGrad;
        return newDirection.times(Real.valueOf(-gamma));
    }

    //returns training vector pi
    private Vector<Real> getPi(Vector<Real> curGrad) {
        return (curGrad.times(currentVarParams.prevGradient).isLessThan(Real.ZERO))
                ? curGrad.minus(kaczmarz.getAlgorithmStep(curGrad, currentVarParams.prevGradient, Real.ZERO))
                : curGrad;
    }

    private Comparator<Vector<Real>> getSubgradComparator() {
        return new Comparator<Vector<Real>>() {
            @Override
            public int compare(Vector<Real> o1, Vector<Real> o2) {
                int res = 0;
                if(currentVarParams.curDirection.times(o1).doubleValue() <= 0
                        && currentVarParams.curDirection.times(o2).doubleValue() > 0) {
                    res = -1;
                } else if (currentVarParams.curDirection.times(o2).doubleValue() <= 0
                        && currentVarParams.curDirection.times(o1).doubleValue() > 0) {
                    res = 1;
                }

                return res;
            }
        };
    }

    // TODO: 21.10.2017 not works 
    //returns optimized bu gradient gamma for correction training on the current step
    private Double getOptimizedGamma(Vector<Real> x, CostFunction func) {
        Function<Vector<Real>, Double> f = new Function<Vector<Real>, Double>() {
            @Override
            public Double apply(Vector<Real> realVector) {
                Real gamma = realVector.get(0);
                return func.apply(x.minus(currentVarParams.curDirection.times(gamma)));
            }
        };
        double[] searchRange = {-100, 100};
        Config conf = new Config(0.01, searchRange, oneDimSearchAlgo);
        CostFunction oneDimCost = new UndeterminateCostFunc(f,1,conf);
        OptimizationProcedure optProc = new OptimizationProcedure(oneDimCost, conf);
        Vector<Real> start = MathHelp.getTwinVector(1, Real.ZERO);
        optProc.start(start);
        Tuple<Vector<Real>, Double> res = optProc.getOptimizedDecision();
        return res.x.get(0).doubleValue();
    }


//------------------------------------------------ inner -----------------------------------------------------------

    private class M1Stopping extends StopCriteria{

        private StopCriteria baseStopping;


        public M1Stopping(){
        }


        public M1Stopping(StopCriteria baseStopping){
            this.baseStopping = baseStopping;
        }

        @Override
        protected boolean specifiedCriteria(OptimizationProcedure optimizationProcedure) {
            if (optimizationProcedure.getAlgoVarParams().done
                    || optimizationProcedure.getAlgoVarParams().ek.isEmpty()
                    || optimizationProcedure.getAlgoVarParams().mk.isEmpty()) return true;
            guideAlgorithm(optimizationProcedure);
            boolean baseCriteria = !(baseStopping == null) && baseStopping.isAchieved(optimizationProcedure);
            return optimizationProcedure.getAlgoVarParams().done || baseCriteria;
        }

        @Override
        protected String getName() {
            return "M1Stopping";
        }

        @Override
        protected String printParams() {
            return "mk = " + guideParams.mk.toString()
                    + "; ek = " + guideParams.ek.toString()
                    + "; baseStopping = " + baseStopping.toString();
        }

        private void guideAlgorithm(OptimizationProcedure optimizationProcedure) {
            optimizationProcedure.getAlgoVarParams().i++;
            Double epsK = optimizationProcedure.getAlgoVarParams().ek.peek();
            Double mK = optimizationProcedure.getAlgoVarParams().mk.peek();
            optimizationProcedure.getAlgoVarParams().curSum += 1 /(optimizationProcedure.getAlgoVarParams().prevGradient
                    .times(optimizationProcedure.getAlgoVarParams().prevGradient).doubleValue());
            if (1/Math.sqrt(optimizationProcedure.getAlgoVarParams().curSum) < epsK
                    || (optimizationProcedure.getAlgoVarParams().i - optimizationProcedure.getAlgoVarParams().qk > mK) ) {
                optimizationProcedure.getAlgoVarParams().anew = true;
                optimizationProcedure.getAlgoVarParams().qk = optimizationProcedure.getAlgoVarParams().i;
                optimizationProcedure.getAlgoVarParams().ek.remove();
                optimizationProcedure.getAlgoVarParams().mk.remove();
            } else optimizationProcedure.getAlgoVarParams().anew = false;
        }
        //--------------------------------------------------------------------------------------------------------------


        public GuideParameters getGuideParams() {
            return guideParams;
        }

        public StopCriteria getBaseStopping() {
            return baseStopping;
        }

    }

    public class GuideParameters {
        public final Queue<Double> ek; // parameters for evaluation loop1 criteria
        public final Queue<Double> mk; // parameters for evaluation loop2 criteria

        public GuideParameters(Queue<Double> ek, Queue<Double> mk) {
            this.ek = ek;
            this.mk = mk;
        }
    }
}
