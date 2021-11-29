import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.util.ArrayList;
import java.io.*;
import java.util.Arrays;
import java.util.FormatterClosedException;
import java.util.List;

// TODO take the nature log for the data
public class arima {
    // Prepare input timeseries data.
    // here we simply changed the dataArray to a double[] hold
    // a set of mean value for cpu from the previous hours
    // change to the hourly mean/std value for both mem and cpu
    // to predict the next 24 values


    // the window should be moving

//    double[] dataArray = new double[] {2, 1, 2, 5, 2, 1, 2, 5, 2, 1, 2, 5, 2, 1, 2, 5};
    public ArrayList<Double> cpuUsed = new ArrayList<>();
    public ArrayList<Double> memUsed = new ArrayList<>();
    public ArrayList<Double> day = new ArrayList<>();
    public ArrayList<Double> hour = new ArrayList<>();
    public List<ArrayList<Double>> CPUByHour;
    public List<ArrayList<Double>> MemByHour;
    int window = 0;
    // Set ARIMA model parameters.
    public int p ;
    public int d ;
    public int q ;
    public int P = 1;
    public int D = -1; // if D or m is less than  1 then it is non-seasonal and we dont care about PDQ m
    public int Q = 0;
    public int m = 0;
    public int forecastSize = 1;
    public double maxDay;
    public double minDay;
    public arima(int q, int d, int p, String path){ // window is representing the time gap
        this.p = p;
        this.q = q;
        this.d = d;
        String filePath = path;
        this.window = window;
        this.forecastSize = 1;
        ArrayList<String> data_string = readData(filePath);
        ArrayList<Double[]> data = processData(data_string);
        // process all the data to double above this point
        // now we have all of the data in field
        this.maxDay = this.day.get(this.day.size()-1);
        this.minDay = this.day.get(0);
        List<ArrayList<Double>> CPUByHour = hours( this.cpuUsed);
        List<ArrayList<Double>> MemByHour = hours( this.memUsed);
        this.CPUByHour = CPUByHour;
        this.MemByHour = MemByHour;
    }


    public ArrayList<String> readData(String filePath){
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

    /**
     * This hours function transform the data to hourly counted data
     * Each inner list representing the data within that hour
     * if the list only consist one zero then means there is no data points in that gap
     * (proved correct)
    * */
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
            else {
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


    /***
     * This function used to tranform the data within a list to set
     */
    public double[] listToSet(ArrayList<Double> toTrans){
        int len = toTrans.size();
        double[] dd = new double[len];
        for (int i= 0; i < len; ++i){
            dd[i] = toTrans.get(i);
        }
        return dd;
    }
    /***
     * This function used to build the model
     */
    public ForecastResult buildModel(double[] dataArray){
        ArimaParams parm = new ArimaParams(p,d,q,P,D,Q,m);
        return Arima.forecast_arima(dataArray, 1, parm);
    }
    /***
     * Retrieve the predicted data
     */
    public double[] forcastData(ForecastResult model){
        return model.getForecast();
    }
    /***
     * Built in RMSE
     */
    public double rmse(ForecastResult model){
        return model.getRMSE();
    }
    /***
     * My own RMSE calculation function
     */
    public double calculateRmse(double[] predicted, double[] real){
        int length = predicted.length;
        double dif = 0;
        for(int i = 0; i < length; ++i){
            dif += Math.pow((predicted[i] - real[i]),2);
        }
        dif = Math.sqrt((dif/length));
        return dif;
    }

    public double maxNormalizedVariance(ForecastResult model){
        return model.getMaxNormalizedVariance();
    }


    /**
     * The input of this function taking the number of days for training
     * and the hours as well.
     *
     * And this function including the predicting for the cpu and memory
     *
     * */
    public double[] predictProcess( int days, int hours){

        /**
         * here get by x hours means for cpu and mem
         * */
        ArrayList<Double> cpuHours = new ArrayList<>();
        ArrayList<Double> memHours = new ArrayList<>();
        for (int i = 0; i <this.CPUByHour.size() ; i++) {
            double cpuHour = 0;
            double memHour = 0;
            int len = this.CPUByHour.get(i).size(); // get the number of points in current hour
            for (double d: this.CPUByHour.get(i)){
                cpuHour+=d;
            }
            for (double dd: this.MemByHour.get(i)){
                memHour+=dd;
            }
            if(len==0) len =1;
            cpuHour /= len;
            memHour /=len;
            cpuHours.add(cpuHour);
            memHours.add(memHour);
        }

        ArrayList<double[]> predicted_CPU = new ArrayList<>();
        ArrayList<double[]> predicted_Mem = new ArrayList<>();


        int windows_ = hours;
        // we first add the points we are not using for prediction to the predicted list
        for (int i = 0; i < windows_; i++) {
            double[] temp_cpu = new double[]{cpuHours.get(i)};
            predicted_CPU.add(temp_cpu);
            double[] temp_mem = new double[]{memHours.get(i)};
            predicted_Mem.add(temp_mem);

        }


        for(int i= 0; i < cpuHours.size()-windows_; i++){
            double[] temp_Mem = new double[windows_];
            double[] temp_CPU= new double[windows_];
            for (int j = 0; j <windows_; j++) {
                temp_Mem[j] = Math.log(memHours.get(i+j)+0.1);
                temp_CPU[j] = Math.log(cpuHours.get(i+j)+0.1);
            }
            ForecastResult modelMem = buildModel(temp_Mem);
            ForecastResult modelCPU = buildModel(temp_CPU);
            double[] preCPU = new double[]{Math.exp(modelCPU.getForecast()[0])-0.1};
            predicted_CPU.add(preCPU);
            double[] preMem= new double[]{Math.exp(modelMem.getForecast()[0])-0.1};
            predicted_CPU.add(preCPU);
            predicted_Mem.add(preMem);
        }

        String memPredictedPath = "C:\\Users\\vini\\Desktop\\1411\\Arima\\Atom.lnk\\memPrediction.csv";
        String CPUPredictedPath = "C:\\Users\\vini\\Desktop\\1411\\Arima\\Atom.lnk\\CPUPrediction.csv";


        writer(predicted_Mem,memHours, memPredictedPath);
        writer(predicted_CPU,cpuHours, CPUPredictedPath);

        double[] result = new double[]{0, 0};

        return result;
    }
    /**
     * The writer to write predicted result to a file nothing much
     * */
    public void writer(ArrayList<double[]> predicted, ArrayList<Double> real, String path){
        ArrayList<Double> predictedList = new ArrayList<>();

        for (double[] dd: predicted
             ) {
            for (double d : dd
                 ) {
                predictedList.add(d);
            }
        }
        try {
            FileWriter myWriter = new FileWriter(path);
            for (int i = 0; i <predicted.size(); i++) {
                String toWrite = "";
//                toWrite = predictedList.get(i).toString() + ", "  + real.get(i).toString() + "\n";
                toWrite = predictedList.get(i).toString() + ", "  + real.get(i).toString() + "\n";
                myWriter.write(toWrite);
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }
}
