import java.util.ArrayList;

public class main {
    public static void main(String[] args){
        String pathToFiles = "./";
        int numberOfRuns = 1;
        String param = args[0];
        String seed = args[1];
//        System.out.println(param);
//        System.out.println(seed);

        String[] runConfig = new String[]{
                "-p", ("stat.file=$" + pathToFiles + "out.stat"),
                "-p", ("jobs=" + numberOfRuns),
                "-p", ("seed.0=" + seed),
        };
        String path = "C:\\Users\\vini\\Desktop\\1411\\predictionData.csv";
        int[] qs = new int[]{0,1,2,3,4,5,6};
        int[] ds = new int[]{0,1,2,3,4,5,6};
        int[] ps = new int[]{0,1,2,3,4,5,6};
        ArrayList<Double> rmses = new ArrayList<>();
        for (int i = 0; i < 7; i ++){
            for(int j =0; j < 7; j++){
                for(int m =0; m < 7; m++){
                    int q = qs[i];
                    int d = ds[i];
                    int p = ps[i];
                    arima predictor_1_1 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                    arima predictor_1_2 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
                    arima predictor_1_4 = new arima(q,d,p, path, 1,4); // the window representing the number of days s
                    arima predictor_2_1 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                    arima predictor_2_2 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
                    arima predictor_2_4 = new arima(q,d,p, path, 1,4); // the window representing the number of days s
                    
                    double r1 = predictor_1_1.predictProcess(1,1);
                    double r2 = predictor_1_2.predictProcess(1,2);
                    double r3 = predictor_1_4.predictProcess(1,4);
                    double r4 = predictor_2_1.predictProcess(2,4);
                    double r5 = predictor_2_2.predictProcess(2,4);
                    double r6 = predictor_2_4.predictProcess(2,4);

                }
            }

        }


    }
}
