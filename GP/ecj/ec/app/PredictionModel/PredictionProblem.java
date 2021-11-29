package ec.app.PredictionModel;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class PredictionProblem extends GPProblem implements SimpleProblemForm {
    public int currentHour;
    public int currentDay;
    public double currentX;
    public double currentX1;
    public double currentX2;
    public double currentX3;
    public double currentX4;
    public double currentX5;
    public double currentX6;
    public double currentX7;
    public double currentX8;
    public double currentX9;
    public ArrayList<Double> cpuUsed = new ArrayList<>();
    public ArrayList<Double> memUsed = new ArrayList<>();
    public ArrayList<Double> day = new ArrayList<>();
    public ArrayList<Double> hour = new ArrayList<>();
    public List<ArrayList<Double>> CPUByHour;
    public List<ArrayList<Double>> MemByHour;
    public ArrayList<String> dataString = new ArrayList<>();
    public ArrayList<Double> cpuHours = new ArrayList<>();
    public ArrayList<Double> memHours = new ArrayList<>();

    public double maxDay;
    public double minDay;

    public int currentGen = 0;
    public ArrayList<Double> bestPrediction = new ArrayList<>();
    public double bestRMSE = 1000000000000.0;
    double[] test = new double[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
    public void setup(final EvolutionState state,
                      final Parameter base) {
        super.setup(state, base);

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof DoubleData)) {
            state.output.fatal("GPData class must subclass from " + DoubleData.class,
                    base.push(P_DATA), null);
        }
        Parameter dataPath = new Parameter("dataPath");
        String dataFile = state.parameters.getString(dataPath, null);
        this.dataString = readFromFiles(dataFile);
        ArrayList<Double[]> data = processData(dataString);
        List<ArrayList<Double>> CPUByHour = hours(this.cpuUsed);
        List<ArrayList<Double>> MemByHour = hours(this.memUsed);
        this.CPUByHour = CPUByHour;
        this.MemByHour = MemByHour;
        this.maxDay = this.day.get(this.day.size() - 1);
        this.minDay = this.day.get(0);
        conversion();
    }

    public ArrayList<String> readFromFiles(String filePath) {
        ArrayList<String> fileStrings = new ArrayList<>();
        try {
            File file = new File(filePath);    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {
                fileStrings.add(line);
                sb.append(line);      //appends line to string buffer
                sb.append("\n");     //line feed
            }
            fr.close();    //closes the stream and release the resources

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileStrings;

    }

    public ArrayList<Double[]> processData(ArrayList<String> datas) {// this method is written in the night so there are fullof sht
        ArrayList<String[]> sss = new ArrayList<>();//hold the values as string
        for (String s : datas
        ) {
            String[] ss = s.split(",");
            sss.add(ss);
//            System.out.println(ss);
        }
        ArrayList<Double[]> data = new ArrayList<>();

        for (String[] s : sss // Each record
        ) {
            if (s[0].equals("cpuUsed")) continue;
            else {
                Double[] oneRow = new Double[4];
                for (int i = 0; i < s.length; ++i) {
                    Double value = Double.parseDouble(s[i]);
                    oneRow[i] = value;
                }
                data.add(oneRow);
            }
        }
        ArrayList<Double> cpuUsed = new ArrayList<>();
        ArrayList<Double> memUsed = new ArrayList<>();
        ArrayList<Double> day = new ArrayList<>();
        ArrayList<Double> hour = new ArrayList<>();
        for (Double[] oneRow : data
        ) {
            cpuUsed.add(oneRow[0]);
            memUsed.add(oneRow[1]);
            day.add(oneRow[2]);
            hour.add(oneRow[3]);
        }
        this.cpuUsed = cpuUsed;
        this.memUsed = memUsed;
        this.day = day;
        this.hour = hour;
        return data;
    }

    public List<ArrayList<Double>> hours(ArrayList<Double> toSplit) {
        List<ArrayList<Double>> data = new ArrayList<>();
        double currentDay = this.day.get(0);
        double currentHour = this.hour.get(0);
        ArrayList<Double> dataOneHour = new ArrayList<>();
        for (int i = 0; i < toSplit.size(); i++) {
            if (currentDay == this.day.get(i)) {
                if (currentHour == this.hour.get(i)) {
                    dataOneHour.add(toSplit.get(i));
                } else { // still in the same day
                    data.add((ArrayList<Double>) dataOneHour.clone());
                    dataOneHour.clear();

                    if (currentHour == (this.hour.get(i) - 1)) { // the hour right now is one bigger than the previous so here we have data point in this hour
                        currentHour = this.hour.get(i);
                        dataOneHour.add(toSplit.get(i));
                        continue;
                    } else {//中间有0 的情况
                        int zerosToInsert = (int) ((this.day.get(i) * 24 - currentDay * 24) + (this.hour.get(i) - currentHour)) - 1;
                        for (int j = 0; j < zerosToInsert; j++) {
                            ArrayList<Double> temp = new ArrayList<>();
                            temp.add(0.0);
                            data.add(temp);
                        }
                        dataOneHour.add(toSplit.get(i));
                        currentDay = this.day.get(i);
                        currentHour = this.hour.get(i);
                        continue;
                    }
                }
            } else {
                data.add((ArrayList<Double>) dataOneHour.clone());
                int zeroToInsert = 0;
                zeroToInsert = (int) ((this.day.get(i) * 24 - currentDay * 24) + (this.hour.get(i) - currentHour)) - 1;
                for (int j = 0; j < zeroToInsert; j++) {
                    ArrayList<Double> temp = new ArrayList<>();
                    temp.add(0.0);
                    data.add(temp);
                }
                dataOneHour.clear();
                currentDay = this.day.get(i);
                currentHour = this.hour.get(i);
                continue;
            }
        }
        if (dataOneHour.size() != 0) data.add(dataOneHour);
        return data;
    }

    /**
     * This function simply just convert the cpu and mem points to its mean value by hourly interval
     */
    public void conversion() {
        ArrayList<Double> cpus = new ArrayList<>();
        ArrayList<Double> mems = new ArrayList<>();
        double cpu = 0;
        double mem = 0;
        for (int i = 0; i < this.CPUByHour.size(); i++) {
            int len = this.CPUByHour.get(i).size();
            if (len == 0) len = 1;
            for (Double cpu_d : this.CPUByHour.get(i)
            ) {
                cpu += cpu_d;
            }
            for (Double mem_d : this.MemByHour.get(i)
            ) {
                mem += mem_d;
            }
            this.cpuHours.add((cpu / len));
            this.memHours.add((mem / len));
            cpu = 0;
            mem = 0;
        }
    }

    public void evaluate(final EvolutionState state,
                         final Individual ind,
                         final int subpopulation,
                         final int threadnum) {
        if (!ind.evaluated)  // don't bother reevaluating
        {

            DoubleData input = (DoubleData) (this.input);
            int hits = 0;
            double sum = 0;
            this.currentGen = state.generation;//find the current generation and get the predicted data// the start index for data points
            // For each individual let it predict all the points in a chain and then evaluate the rmse
            // for each individual
            double predicted = 0;
            double rmseResult = 0;
            ArrayList<Double> prediction = new ArrayList<>();

            for (int i = 0; i < 360 ; i++) {
                double random =0.0;
                this.currentX = i + random;
                this.currentX1 = i+1 + random;
                this.currentX2 = i+2 + random;
                this.currentX3 = i+3 + random;
                this.currentX4 = i+4 +random;
                this.currentX5 = i+5 + random;
                this.currentX6 = i+6 + random;
                this.currentX7 = i+7 + random;
                this.currentX8 = i+8 + random;
                this.currentX9 = i+9 + random;
                ((GPIndividual) ind).trees[0].child.eval(
                        state, threadnum, input, stack, ((GPIndividual) ind), this);
                prediction.add(input.x);//add the predicted value to this individual
//                double result  = Math.log(Math.pow((i + 1) - input.x ,2 ) + 0.001);
//
                double result = Math.abs(input.x - (Math.pow(i+2,2) +i ) );
                if (result <=  0.1) hits++;
                sum += result;
            }
//            for (int i = 0; i < this.cpuHours.size() - 10; i++) {
//                this.currentX = this.cpuHours.get(i);
//                this.currentX1 = this.cpuHours.get(i + 1);
//                this.currentX2 = this.cpuHours.get(i + 2);
//                this.currentX3 = this.cpuHours.get(i + 3);
//                this.currentX4 = this.cpuHours.get(i + 4);
//                this.currentX5 = this.cpuHours.get(i + 5);
//                this.currentX6 = this.cpuHours.get(i + 6);
//                this.currentX7 = this.cpuHours.get(i + 7);
//                this.currentX8 = this.cpuHours.get(i + 8);
//                this.currentX9 = this.cpuHours.get(i + 9);
//                ((GPIndividual) ind).trees[0].child.eval(
//                        state, threadnum, input, stack, ((GPIndividual) ind), this);
//                prediction.add(input.x);//add the predicted value to this individual
//                double result = Math.pow(this.cpuHours.get(i + 10) - input.x ,2 );
//
//                if (result <= 500) hits++;
//                sum += result;
//
//            }
            double rmse_1 = sum;
            if (rmse_1 < bestRMSE) {
                bestRMSE = rmse_1;
                bestPrediction = prediction;
                writer(bestPrediction, bestRMSE);
            }


            KozaFitness f = ((KozaFitness) ind.fitness);
            f.setStandardizedFitness(state, rmse_1);
            f.hits = hits;
            ind.evaluated = true;
        }


    }
    // TODO  check the tree, print out the best tree for each generation
    public void writer(ArrayList<Double> predicted, double rmse){
        String path = "C:\\Users\\vini\\Desktop\\GP\\ecj\\ec\\app\\PredictionModel\\preditctedGP.csv";
            try {
                FileWriter myWriter = new FileWriter(path);

                for (int i = 10; i <predicted.size(); i++) {
                    String toWrite = "";
//                  toWrite = predictedList.get(i).toString() + ", "  + real.get(i).toString() + "\n";
                    toWrite = predicted.get(i).toString() + ", "  + this.memHours.get(i).toString() + "\n";
                    myWriter.write(toWrite);
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

    }




    public double RMSE(ArrayList<Double> predicted, ArrayList<Double> real) {
        int length = predicted.size();
        double rmse = 0;
        for (int i = 0; i < length; i++) {
            rmse += Math.pow((predicted.get(i) - real.get(i)), 2);
        }
        if (length == 0) length = 1;
        rmse = rmse / length;
        rmse = Math.sqrt(rmse);
        return rmse;
    }
}


