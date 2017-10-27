package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Parameters that varies while {@link ru.mipt.optimization.entity.optimizationProcedure.OptimizationProcedure} works
 */
public class VaryingParams {

    static final Queue<Double> DEFAULT_EK = new LinkedList<>();
    static final Queue<Double> DEFAULT_MK = new LinkedList<>();

    static {createDefaultEkMk();}

    Vector<Real> curDirection; // current direction vector for GradientKaczmaezSearch
    Vector<Real> prevGradient; // gradient vector on the previous iteration for GradientKaczmaezSearch
    boolean anew = true; // flag to move back to first outer loop for GradientKaczmaezSearch

    boolean done = false;

    double curSum = 0; // for GradientKaczmaezSearch Stop criteria
    Queue<Double> ek; // parameters for evaluation loop1 criteria for GradientKaczmaezSearch stopCriteria
    Queue<Double> mk; // parameters for evaluation loop2 criteria for GradientKaczmaezSearch stop criteria

    int i = 0; // iteration
    int qk = 0; // for GradientKaczmaezSearch loop stop criteria

    public VaryingParams(Queue<Double> ek, Queue<Double> mk) {
        this.ek = ek;
        this.mk = mk;
    }

    public VaryingParams() {}


    private static void createDefaultEkMk() {
        int k = 10;
        for(int j=0; j<2*k; j++){
            if (j<k-1) DEFAULT_MK.add((double) 100/(j+1));
            else DEFAULT_EK.add((double) 1/j);
        }
    }
}
