package ru.mipt.optimization.algorithms;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.Vector;
import ru.mipt.optimization.entity.optimizationProcedure.StopCriteria;
import ru.mipt.optimization.entity.optimizationProcedure.costFunction.CostFunction;

import java.util.List;
import java.util.function.Function;

/**
 * An abstract implementation of the Algorithm interface that covers hybrid optimization algorithms,
 * i.e. variations of mixed pure algorithms.
 * Created by Inna on 26.02.2017.
 */
public abstract class HybridAlgorithm extends PureAlgorithm {

    /**
     * Returns the list of pure optimization algorithms to mix in hybrid variation
     * @return the list of pure optimization algorithms to mix in hybrid variation
     */
    abstract List<PureAlgorithm> getPureAlgorithms();

    /**
     * Prints in String own parameters of the hybrid algorithm
     * @return String with own parameters of the hybrid algorithm
     */
    abstract String printOwnParams();

    @Override
    protected String printParams() {
        String str = "inner algorithms[ ";
        for (PureAlgorithm innAlg: getPureAlgorithms())
         str += innAlg.print() + "; ";
        str = str.substring(0, str.length()-2).concat(" ]\n own parameters: ");
        str += printOwnParams();
        return str;
    }
}
