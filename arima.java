import com.workday.insights.timeseries.arima.Arima;
import com.workday.insights.timeseries.arima.struct.ArimaParams;
import com.workday.insights.timeseries.arima.struct.ForecastResult;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.io.*;
import java.util.List;


public class arima {
    // Prepare input timeseries data.
    // here we simply changed the dataArray to a double[] hold
    // a set of mean value for cpu from the previous hours
    // change to the hourly mean/std value for both mem and cpu
    // to predict the next 24 values

//    double[] dataArray = new double[] {2, 1, 2, 5, 2, 1, 2, 5, 2, 1, 2, 5, 2, 1, 2, 5};
    public ArrayList<Double> cpuUsed = new ArrayList<>();
    public ArrayList<Double> memUsed = new ArrayList<>();
    public ArrayList<Double> day = new ArrayList<>();
    public ArrayList<Double> hour = new ArrayList<>();
    public List<ArrayList<Double>> CPUByHour;
    public List<ArrayList<Double>> MemByHour;
    int window = 0;
    // Set ARIMA model parameters.
    public int p = 3;
    public int d = 0;
    public int q = 3;
    public int P = 1;
    public int D = -1; // if D or m is less than  1 then it is non-seasonal and we dont care about PDQ m
    public int Q = 0;
    public int m = 0;
    public int forecastSize = 1;
    public double maxDay;
    public double minDay;
    public arima(int q, int d, int p, String path, int days, int hours){ // window is representing the time gap
        this.p = p;
        this.q = q;
        this.d = d;
        String filePath = path;
        this.window = window;
        this.forecastSize = 24;
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

    /***
     * This function can be used for read the csv consist the mean value for each hour
     * (not used)
     */

    public ArrayList<Double[]> readMean (String fp){
        ArrayList<Double[]> readData =new ArrayList<>();
        try
        {
            File file=new File(fp);    //creates a new file instance
            FileReader fr=new FileReader(file);   //reads the file
            BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while((line=br.readLine())!=null)
            {
//                fileStrings.add(line);
                sb.append(line);      //appends line to string buffer
                sb.append("\n");     //line feed
            }
            fr.close();    //closes the stream and release the resources
            System.out.println("Contents of File: ");
            System.out.println(sb.toString());   //returns a string that textually represents the object
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }


        return readData;
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
        return Arima.forecast_arima(dataArray, forecastSize, parm);
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


    public double predictProcess(  int days, int hours){

//        ForecastResult model = buildModel(trainData);
//        double[] predicted = forcastData(model);
//        double[] realSet = listToSet(real);
//        double rmse = calculateRmse(predicted,realSet);


        int points = this.forecastSize * days;
        ArrayList<Double> predictTrain_Mem= new ArrayList<>();
        ArrayList<Double> predictTrain_CPU= new ArrayList<>();
        ArrayList<Double> real_Mem= new ArrayList<>();
        ArrayList<Double> real_CPU= new ArrayList<>();
        int count = 1;
        double total_Mem = 0;
        double total_CPU = 0;

        double Mem_hour = 0;
        double CPU_hour = 0;

        int length = 0;
        /**
         * here get by x hours means for cpu and mem for training
         * */
        for (int i = 0; i < this.MemByHour.size()-(24*days); i++) {//rule out the last day
            if(count < hours){
                length += this.MemByHour.get(i).size(); // the length of the current hour
                count+=1;
            }
            else{
                Mem_hour = total_Mem/length;
                CPU_hour = total_CPU/length;
                predictTrain_Mem.add(Mem_hour);
                predictTrain_CPU.add(CPU_hour);
                total_Mem = 0;
                total_CPU = 0;
                length = 0;
                count =1;
            }
            for (int j = 0; j < this.MemByHour.get(i).size(); j++) {
                total_Mem += this.MemByHour.get(i).get(j);
                total_CPU += this.CPUByHour.get(i).get(j);
            }
        }
        count =0;
        length = 0;
        total_CPU = 0;
        total_Mem =0;
        /**
         * here get by x hours means for cpu and mem for testing
         * */
        for(int i=(24*days)-1; i < this.MemByHour.size(); i++){
            if(count < hours){
                length += this.MemByHour.get(i).size(); // the length of the current hour
                count+=1;
            }
            else{
                Mem_hour = total_Mem/length;
                CPU_hour = total_CPU/length;
                real_Mem.add(Mem_hour);
                real_CPU.add(CPU_hour);
                total_Mem = 0;
                total_CPU = 0;
                length = 0;
                count =1;
            }
            for (int j = 0; j < this.MemByHour.get(i).size(); j++) {
                total_Mem += this.MemByHour.get(i).get(j);
                total_CPU += this.CPUByHour.get(i).get(j);
            }
        }

        int minDay = (int)this.minDay;
        int maxDay = (int)this.maxDay;
        int counter =0;
        double[] predictTrain_CPUSet = listToSet(predictTrain_CPU);
        double[] predictTrain_MemSet = listToSet(predictTrain_Mem);
        double[] real_CPUSet = listToSet(real_CPU);
        double[] real_MemSet = listToSet(real_Mem);
        int takenNum = days *24 / hours; // this variable is used to track how many points taken to predict for the next days
        // TODO need to get the predict value and RMSE over different settings
        for (int i = 0; i < predictTrain_CPUSet.length; i++) {

//            Arrays.copyOfRange(arr, 0, 2);
        }

        this.forecastSize = days*24;
        ForecastResult modelCPU = buildModel(predictTrain_CPUSet);
        ForecastResult modelMem = buildModel(predictTrain_MemSet);
        double[] predicted_trainCPU = modelCPU.getForecast();
        double[] predicted_trainMem = modelMem.getForecast();
        

        return 0.0;
    }
}
