package ec.app.tutorial4;


import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

// capture the wait time of the coming containers
public class ArrivalTime extends GPNode {
    @Override
    public String toString() {return "ArrivalTime";}

    @Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem){
        DoubleData rd = (DoubleData)(input);


        rd.x = ((MultiValuedRegression)problem).arrivalTime;
    }

}
