package ec.app.tutorial4;

import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.coevolve.GroupedProblemForm;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PredictionProblem extends GPProblem implements SimpleProblemForm
{
    public int currentHour;
    public int currentDay;
    public double currentX;
    public double predicted;
    public double arrivalTime;
    public ArrayList<Double> cpuUsed = new ArrayList<>();
    public ArrayList<Double> memUsed = new ArrayList<>();
    public ArrayList<Double> day = new ArrayList<>();
    public ArrayList<Double> hour = new ArrayList<>();
    public List<ArrayList<Double>> CPUByHour;
    public List<ArrayList<Double>> MemByHour;
    public ArrayList<String> dataString = new ArrayList<>();


    public double maxDay;
    public double minDay;

    public int currentGen = 0;
    public void setup(final EvolutionState state,
                      final Parameter base)
    {
        super.setup(state, base);

        // verify our input is the right class (or subclasses from it)
        if (!(input instanceof DoubleData)){
            state.output.fatal("GPData class must subclass from " + DoubleData.class,
                    base.push(P_DATA), null);}
        Parameter dataPath = new Parameter("dataPath");
        String dataFile = state.parameters.getString(dataPath, null);
        this.dataString = readFromFiles(dataFile);
        ArrayList<Double[]> data = processData(dataString);
        List<ArrayList<Double>> CPUByHour = hours( this.cpuUsed);
        List<ArrayList<Double>> MemByHour = hours( this.memUsed);
        this.CPUByHour = CPUByHour;
        this.MemByHour = MemByHour;
        this.maxDay = this.day.get(this.day.size()-1);
        this.minDay = this.day.get(0);
    }
    public ArrayList<String> readFromFiles(String filePath){
        ArrayList<String> fileStrings = new ArrayList<>();
        try
        {
            File file=new File(filePath);    //creates a new file instance
            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line=br.readLine())!=null)
            {
                fileStrings.add(line);
                sb.append(line);      //appends line to string buffer
                sb.append("\n");     //line feed
            }
            fr.close();    //closes the stream and release the resources
//            System.out.println("Contents of File: ");
//            System.out.println(sb.toString());   //returns a string that textually represents the object
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return fileStrings;

    }

    public ArrayList<Double[]> processData(ArrayList<String> datas){// this method is written in the night so there are fullof sht
        ArrayList<String[]> sss = new ArrayList<>();//hold the values as string
        for (String s: datas
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
        ArrayList<Double> hour  = new ArrayList<>();
        for (Double[] oneRow: data
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

    public List<ArrayList<Double>> hours( ArrayList<Double> toSplit){
        List<ArrayList<Double>> data = new ArrayList<>();
        double currentDay = this.day.get(0);
        double currentHour = this.hour.get(0);
        ArrayList<Double> dataOneHour = new ArrayList<>();
        for (int i = 0; i < toSplit.size(); i++) {
            if (currentDay == this.day.get(i)){
                if(currentHour == this.hour.get(i)){
                    dataOneHour.add(toSplit.get(i));
                }
                else{ // still in the same day
                    data.add((ArrayList<Double>) dataOneHour.clone());
                    dataOneHour.clear();

                    if(currentHour == (this.hour.get(i)-1)){ // the hour right now is one bigger than the previous so here we have data point in this hour
                        currentHour = this.hour.get(i);
                        dataOneHour.add(toSplit.get(i));
                        continue;
                    }
                    else{//中间有0 的情况
                        int zerosToInsert = (int) ((this.day.get(i) *24 - currentDay* 24 )+ (this.hour.get(i) - currentHour))-1;
                        for (int j = 0; j < zerosToInsert ; j++) {
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
            }
            else {// changed to the next day
//                if(currentHour == 23){ // the previous day reach the end
//                    data.add((ArrayList<Double>) dataOneHour.clone());//add the data from the last hour of previous day
//                    dataOneHour.clear();
//                    currentDay = this.day.get(i);
//                    currentHour = this.hour.get(i);
//                    continue;
//                }
//                else{
                data.add((ArrayList<Double>) dataOneHour.clone());
                int zeroToInsert = 0;
                zeroToInsert = (int) ((this.day.get(i) *24 - currentDay* 24 )+ (this.hour.get(i) - currentHour))-1;
                for (int j = 0; j < zeroToInsert; j++) {
                    ArrayList<Double> temp = new ArrayList<>();
                    temp.add(0.0);
                    data.add(temp);
                }
                dataOneHour.clear();
                currentDay = this.day.get(i);
                currentHour = this.hour.get(i);
                continue;
//                }
            }
        }
        if (dataOneHour.size()!=0)  data.add(dataOneHour);
        return data;
    }


    public void evaluate(final EvolutionState state,
                         final Individual ind,
                         final int subpopulation,
                         final int threadnum)
        {
            if (!ind.evaluated)  // don't bother reevaluating
            {
                DoubleData input = (DoubleData)(this.input);

                int hits = 0;
                double sum = 0.0;
                double expectedResult;
                double result;
                this.currentGen = state.generation;//find the current generation and get the predicted data
                int startIndex = this.currentGen * 24;

                ArrayList<Double> memForday = new ArrayList<>();
                for (int i = 0; i < 24; i++) {


                }
                ArrayList<Double> mem = this.MemByHour.get(currentGen);
                int countHour = 0;
                this.currentDay = currentGen;
                ArrayList<Double> predicted = new ArrayList<>();

                int indexReal = startIndex+24;
                ArrayList<Double> real = new ArrayList<>();
                for (int i=0; i < 24; i++){
                    real.add(0.0);
                }
                double fit = 0;

                fit = RMSE(predicted,real);


                // the fitness better be KozaFitness!
                KozaFitness f = ((KozaFitness)ind.fitness);
                f.setStandardizedFitness(state, fit);
                if (fit< 5000) hits++;
                sum += fit;
                f.hits = hits;
                ind.evaluated = true;
            }
        }
    public double RMSE(ArrayList<Double> predicted, ArrayList<Double> real){
        int length = predicted.size();
        double rmse = 0;
        for (int i = 0; i < length; i++) {
            rmse+= Math.pow((predicted.get(i)-real.get(i)),2);
        }
        if(length==0) length =1;
        rmse = rmse/length;
        rmse = Math.sqrt(rmse);
        return rmse;
    }
    }

