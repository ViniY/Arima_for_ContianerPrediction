package ec.app.PredictionModel;

/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

import ec.EvolutionState;
import ec.Problem;
import ec.app.PredictionModel.DoubleData;
import ec.app.PredictionModel.PredictionProblem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class X1 extends GPNode
{
    public String toString() { return "x1"; }

    public int expectedChildren() { return 0; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
    {
        DoubleData rd = ((DoubleData)(input));
        rd.x = ((PredictionProblem)problem).currentX1;
    }
}
