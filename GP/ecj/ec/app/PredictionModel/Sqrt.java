package ec.app.PredictionModel;

import ec.EvolutionState;
import ec.Problem;
import ec.app.PredictionModel.DoubleData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Sqrt extends GPNode {

    public String toString() { return "Sqrt"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem) {
        Double result;
        DoubleData rd = ((DoubleData)(input));

        children[0].eval(state,thread,input,stack,individual,problem);
        result = rd.x;

        rd.x = Math.sqrt(Math.abs(rd.x));
    }
}
