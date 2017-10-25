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

    // TODO: 19.10.2017 переделать, сейчас возможен вызов извне другой оптимизационной процедурой и сбой параметров
    private Vector<Real> curDirection; // current direction vector
    private Vector<Real> prevGradient; // gradient vector on the previous iteration
    private boolean anew = true; // flag to move back to first outer loop

    boolean done = false;


    @Override
    List<PureAlgorithm> getPureAlgorithms() {
        List<PureAlgorithm> inners = new ArrayList<>();
        inners.add(kaczmarz);
        inners.add(oneDimSearchAlgo);
        return inners;
    }

    @Override
    String printOwnParams() {
        return "MAX_SUBGRAD_NUM = " + MAX_SUBGRAD_NUM;
    }

    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function) {
        return anew ? outerLoop(x, function) : innerLoop(x, function, curDirection, prevGradient);
    }

    @Override
    public boolean isAble(CostFunction function) {
        return true;
    }

    /**
     * Configures algorithm parameters.
     * @param params - algorithm parameters in the strict order:
     *               step - value of the step of the one dimension search algorithm;
     *               relaxationParameter - value of the step of the direction search algorithm;
     *               error - error for stopping one dimension search algorithm.
     *               If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters corresponds required 3.
     */
    @Override
    public boolean setParams(double... params) {
        if (params.length != 3) return false;
        double[] error = {params[2]};
        oneDimSearchAlgo.configureStopCriteria(error, true);
        return oneDimSearchAlgo.setParams(params[0])&& kaczmarz.setParams(params[1]);
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
    }

    // TODO: 20.10.2017 error убрать в конец если человек не хочет пользоваться коммонстопинг
    /**
     *
     * @param errors - k+2 parameters to evaluate stop and conditions, namely, in the strict order:
     *               error - accuracy for evaluating stop conditions in the CommonStopping criteria;
     *               k - number of iterations of the outer loop of the algorithm (is used Math.round for convertation);
     *               e1,...,ek - parameters for evaluation outer loop criteria;
     *               m1,...,mk - parameters for evaluation inner loop criteria;
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
     * @throws IllegalArgumentException if size of errors isn't 2*k+2 or errors is empty
     *  or size of conditions isn't 1(and first is false) or 6.
     */
    @Override
    public void configureStopCriteria(double[] errors, boolean... conditions) {
        if (errors.length < 2)
            throw new IllegalArgumentException(" Too small size of errors. See java doc");
        int k = (int)Math.round(errors[1]);
        if (errors.length != 2*k+2)
            throw new IllegalArgumentException(" Wrong size of errors. See java doc");

        Queue<Double> ek = new LinkedList<>();
        Queue<Double> mk = new LinkedList<>();
        for(int j=2; j<errors.length; j++){
            if (j<k+2) ek.add(errors[j]);
            else mk.add(errors[j]);
        }

        switch (conditions.length) {
            case 1 :
                if (conditions[0])
                    throw new IllegalArgumentException("For configuration CommonStopping size of conditions must 6!");
                stopCriteria = new M1Stopping(ek, mk);
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
                super.configureStopCriteria(errors, commonStopping);
                stopCriteria = new M1Stopping(ek, mk, this.stopCriteria);
                break;
            default:
                throw new IllegalArgumentException("Size of conditions isn't 1 or 6");
        }
    }

    private M1Stopping createDefaultM1Stopping() {
        int k = 10;
        Queue<Double> ek = new LinkedList<>();
        Queue<Double> mk = new LinkedList<>();
        for(int j=0; j<2*k; j++){
            if (j<k-1) mk.add((double) 100/(j+1));
            else ek.add((double) 1/j);
        }
        CommonStopping cs = new CommonStopping(true,true,true,true,true,0.1);
        return new M1Stopping(ek, mk, cs);
    }

    private Vector<Real> outerLoop(Vector<Real> x, CostFunction function) {
        Vector<Real> nulVec = MathHelp.getZeroVector(x.getDimension());
        curDirection = DenseVector.valueOf(nulVec);
        prevGradient = DenseVector.valueOf(nulVec);
        return innerLoop(x, function, curDirection, prevGradient);
    }

    private Vector<Real> innerLoop(Vector<Real> x, CostFunction function, Vector<Real> si, Vector<Real> gPrev) {
        Vector<Real> curGrad = function.getGradient(x);
        if (curGrad.equals(MathHelp.getZeroVector(x.getDimension()))) {
            done = true;
            return x;
        }
        if (curDirection.times(curGrad).isLargerThan(Real.ZERO)) {
            List<Vector<Real>> subgradients = function.getSubGradients(x, MAX_SUBGRAD_NUM);
            Collections.sort(subgradients, getSubgradComparator());
            if (!subgradients.isEmpty()) curGrad = subgradients.get(0); //nonetheless required condition may be not fulfilled
        }

        Vector<Real> newDirection = kaczmarz.getAlgorithmStep(curDirection, curGrad, getPi(curGrad), Real.ONE).plus(curDirection);
        curDirection = newDirection;
        Double gamma = getOptimizedGamma(x,function);
        prevGradient = curGrad;
        return newDirection.times(Real.valueOf(-gamma));
    }

    //returns training vector pi
    private Vector<Real> getPi(Vector<Real> curGrad) {
        return (curGrad.times(prevGradient).isLessThan(Real.ZERO))
                ? curGrad.minus(kaczmarz.getAlgorithmStep(curGrad, prevGradient, Real.ZERO))
                : curGrad;
    }

    private Comparator<Vector<Real>> getSubgradComparator() {
        return new Comparator<Vector<Real>>() {
            @Override
            public int compare(Vector<Real> o1, Vector<Real> o2) {
                int res = 0;
                if(curDirection.times(o1).doubleValue() <= 0
                        && curDirection.times(o2).doubleValue() > 0) {
                    res = -1;
                } else if (curDirection.times(o2).doubleValue() <= 0
                        && curDirection.times(o1).doubleValue() > 0) {
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
                return func.apply(x.minus(curDirection.times(gamma)));
            }
        };
        double[] searchRange = {-10, 10};
        Config conf = new Config(0.01, searchRange, oneDimSearchAlgo);
        CostFunction oneDimCost = new UndeterminateCostFunc(f,1,conf);
        OptimizationProcedure optProc = new OptimizationProcedure(oneDimCost, conf);
        Vector<Real> start = MathHelp.getZeroVector(1);
        optProc.start(start);
        Tuple<Vector<Real>, Double> res = optProc.getOptimizedDecision();
        return res.x.get(0).doubleValue();
    }


//------------------------------------------------ inner -----------------------------------------------------------

    private class M1Stopping extends StopCriteria{
        private final Queue<Double> DEFAULT_EK = new LinkedList<>();
        private final Queue<Double> DEFAULT_MK = new LinkedList<>();

        private StopCriteria baseStopping;
        private double curSum = 0;
        private Queue<Double> ek; // parameters for evaluation loop1 criteria
        private Queue<Double> mk; // parameters for evaluation loop2 criteria

        int i = 0;
        int qk = 0;

        private final String ekStr;
        private final String mkStr;

        public M1Stopping(Queue<Double> ek, Queue<Double> mk){
            this.ek = ek;
            this.mk = mk;
            ekStr = ek.toString();
            mkStr = mk.toString();
        }


        public M1Stopping(Queue<Double> ek, Queue<Double> mk, StopCriteria baseStopping){
            this.ek = ek;
            this.mk = mk;
            ekStr = ek.toString();
            mkStr = mk.toString();
            this.baseStopping = baseStopping;
        }

        @Override
        protected boolean specifiedCriteria(OptimizationProcedure optimizationProcedure) {
            if (done || ek.isEmpty() || mk.isEmpty()) return true;
            guideAlgorithm();
            boolean baseCriteria = !(baseStopping == null) && baseStopping.isAchieved(optimizationProcedure);
            return done || baseCriteria;
        }

        @Override
        protected String getName() {
            return "M1Stopping";
        }

        @Override
        protected String printParams() {
            return "mk = " + mkStr
                    + "; ek = " + ekStr
                    + "; baseStopping = " + baseStopping.toString();
        }

        private void guideAlgorithm() {
            i++;
            Double epsK = ek.peek();
            Double mK = mk.peek();
            curSum += 1/(prevGradient.times(prevGradient).doubleValue());
            if (1/Math.sqrt(curSum) < epsK
                    || (i - qk > mK) ) {
                anew = true;
                qk = i;
                ek.remove();
                mk.remove();
            } else anew = false;
        }
    }

}
