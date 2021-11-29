package ec.app.PredictionModel;

import ec.EvolutionState;
import ec.gp.GPIndividual;

import java.util.ArrayList;

public class myIndividual extends GPIndividual {
    ArrayList<Double> predicted = new ArrayList<>();
    @Override
    public void printTrees(EvolutionState state, int log) {
        for (int x = 0; x < this.trees.length; ++x) {
            // graph representation
            state.output.println("Tree " + x + ":", log);
            state.output.println("\n", log);
            state.output.println(this.trees[x].child.makeGraphvizTree(), log);
            // lisp tree
            state.output.println("lisp style: ", log);
            this.trees[x].child.printRootedTreeForHumans(state, log, 0, 0);
            state.output.println("\n", log);
            // c tree
            state.output.println("c style: ", log);
            state.output.println(this.trees[x].child.makeCTree(true,
                    this.trees[x].printTerminalsAsVariablesInC, this.trees[x].printTwoArgumentNonterminalsAsOperatorsInC), log);

        }
    }
}
