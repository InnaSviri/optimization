package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseVector;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;

import javax.measure.unit.SI;
import java.util.function.Function;

/**
 * Represents the cubic approximation algorithm for one-dimensional function
 * Created by Inna on 21.10.2017.
 */
public class CubicApproximation extends PureAlgorithm {
    private static final double DEFAULT_H = 0.5;
    private double h;


    @Override
    protected Vector<Real> getAlgorithmStep(Vector<Real> x, CostFunction function, VaryingParams vp) {
        Vector<Real> x1;
        Real a;
        Real b;
        double fDerX = function.getPartialDerivative(x,0);
        if (fDerX<0) {
            x1 = x.plus(DenseVector.valueOf(Real.valueOf(h)));
            if (function.apply(x1) == null) x1 = function.getNearestDomainPoint(x1,x);
            a = x.get(0);
            b = x1.get(0);
        } else {
            x1 = x.minus(DenseVector.valueOf(Real.valueOf(h)));
            if (function.apply(x1) == null) x1 = function.getNearestDomainPoint(x1,x);
            b = x.get(0);
            a = x1.get(0);
        }

        Vector<Real> prevXPolinom = x1;

        if (function.getPartialDerivative(x1,0)*fDerX >= 0) return x1;
        while (b.minus(a).doubleValue()>stopCriteria.getError()) {
            double polinomMin = calculatePolinomMin(a,b,function);
            Vector<Real> xPol = DenseVector.valueOf(Real.valueOf(polinomMin));
            if (function.apply(xPol) == null) xPol =
                    function.getNearestDomainPoint(xPol,prevXPolinom);
            double fDerPol = function.getPartialDerivative(xPol,0);
            if (fDerPol<0)
                a = xPol.get(0);
            else b = a = xPol.get(0);
            prevXPolinom = xPol;
        }
        vp.done = true;
        Vector<Real> res = DenseVector.valueOf(b.plus(a).divide(2));
        if (function.apply(res) == null) res =
                function.getNearestDomainPoint(res,prevXPolinom);
        return res;
    }

    @Override
    protected String printParams() {
        return "h = " + h;
    }

    @Override
    public boolean isAble(CostFunction function) {
        return function.getDimension() == 1;
    }

    /**
     * Configures algorithm parameters.
     * @param params - algorithm parameters in the strict order:
     *               h - value of the step of the algorithm.
     *               If size of parameters is less than required, rest parameters will be default.
     * @return true if size of parameters corresponds required one.
     */
    @Override
    public boolean setParams(double... params) {
        if (params.length != 1) return false;
        h = params[0];
        return true;
    }

    @Override
    protected void setDefaultParameters() {
        h = DEFAULT_H;
        stopCriteria = new SimpleStopping();
    }

    @Override
    public String getName() {
        return "CubicApproximation";
    }

    /**
     * Configures stop criteria by the simple template of the cubic approximation algorithm.
     * See {@link ru.mipt.optimization.algorithms.CubicApproximation.SimpleStopping}  for details.
     * @param error - errors array of the optimization process in the strict order:
     *              error - accuracy for evaluating stop conditions.
     *              If size of errors is less than required, rest parameters will be default.
     * @param conditions - any boolean.
     */
     @Override
    public void configureStopCriteria(double[] error, boolean... conditions) {
        if (error.length != 0)
            stopCriteria = new SimpleStopping(error[0]);
        else stopCriteria = new SimpleStopping();
    }

    // TODO: 21.10.2017 привести к одному из типов
    private double calculatePolinomMin(Real a, Real b, CostFunction f) {
        return a.doubleValue() + (calculateGamma(a,b,f)*(b.minus(a).doubleValue()));
    }

    private double calculateOmega(Real a, Real b, CostFunction function) {
        double fafb = function.apply(DenseVector.valueOf(a))*function.apply(DenseVector.valueOf(b));
        double z = calculateZ(a,b,function);
        return Math.sqrt(z*z - fafb);
    }

    private double calculateZ(Real a, Real b, CostFunction f) {
        return 3*(f.apply(DenseVector.valueOf(a)) - f.apply(DenseVector.valueOf(b)))/b.minus(a).doubleValue()
                + f.getPartialDerivative(DenseVector.valueOf(a),0) + f.getPartialDerivative(DenseVector.valueOf(b),0);
    }

    private double calculateGamma(Real a, Real b, CostFunction f) {
        return (calculateZ(a,b,f)+calculateOmega(a,b,f)-f.apply(DenseVector.valueOf(a)))
                /(f.getPartialDerivative(DenseVector.valueOf(b),0) - f.getPartialDerivative(DenseVector.valueOf(a),0) + (2*calculateOmega(a,b,f)));
    }


    //------------------------------------------------ inner -----------------------------------------------------------

    /**
     * Represents simple stopping criteria that takes in the consideration only flag of the algorithm itself
     */
    private class SimpleStopping extends StopCriteria {

        public SimpleStopping(double error) {
            this.error = error;
        }

        public SimpleStopping() {}

        @Override
        protected boolean specifiedCriteria(OptimizationProcedure optimizationProcedure) {
            return optimizationProcedure.getAlgoVarParams().done;
        }

        @Override
        protected String getName() {
            return "SimpleStopping";
        }

        @Override
        protected String printParams() {
            return "";
        }
    }
}
