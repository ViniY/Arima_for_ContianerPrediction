import java.util.ArrayList;

public class main {
    public static void main(String[] args){
        String pathToFiles = "./";
        int numberOfRuns = 1;
        String param = args[0];
        String seed = args[1];


        String[] runConfig = new String[]{
                "-p", ("stat.file=$" + pathToFiles + "out.stat"),
                "-p", ("jobs=" + numberOfRuns),
                "-p", ("seed.0=" + seed),
        };
        String path = "/home/yuyong1/Desktop/Arima/predictionData.csv";
        int[] qs = new int[]{0,1,2,3,4,5,6};
        int[] ds = new int[]{0,1,2,3,4,5,6};
        int[] ps = new int[]{0,1,2,3,4,5,6};
        ArrayList<double[]> rmses = new ArrayList<>();
        int counter = 0;
//        for (int i = 0; i < 7; i ++){
//            for(int j =0; j < 7; j++){
//                for(int m =0; m < 7; m++){
                    counter++;
                    int q = 1;
                    int d = 0;
                    int p = 1;
                    arima predictor_1_1 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                    arima predictor_1_2 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
                    arima predictor_1_4 = new arima(q,d,p, path, 1,4); // the window representing the number of days s
                    arima predictor_2_1 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                    arima predictor_2_2 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
                    arima predictor_2_4 = new arima(q,d,p, path, 1,4); // the window representing the number of days s
                    
                    double[] r1 = predictor_1_1.predictProcess(1,1);

//                    double[] r2 = predictor_1_2.predictProcess(1,2);
//                    double[] r3 = predictor_1_4.predictProcess(1,4);
//                    double[] r4 = predictor_2_1.predictProcess(2,1);
//                    double[] r5 = predictor_2_2.predictProcess(2,2);
//                    double[] r6 = predictor_2_4.predictProcess(2,4);

//                }
//            }
//
//        }
                rmses.add(r1);
//                rmses.add(r2);
                q = 2;
                d = 1;
                p = 2;
                arima predictor_1_11 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                arima predictor_1_21 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
                double[] r3 = predictor_1_11.predictProcess(1,1);

//                double[] r4 = predictor_1_21.predictProcess(1,2);
//                rmses.add(r3);
//                rmses.add(r4);

                q = 2;
                d = 0;
                p = 3;
                arima predictor_1_14 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
                arima predictor_1_24 = new arima(q,d,p, path, 1,2); // the window representing the number of days s
//                double[] r5 = predictor_1_14.predictProcess(1,1);
//
//                double[] r6 = predictor_1_24.predictProcess(1,2);
//                rmses.add(r5);
//                rmses.add(r6);
//
//                q = 3;
//                d = 1;
//                p = 3;
//                arima predictor_1_15 = new arima(q,d,p, path, 1,1); // the window representing the number of days s
//                arima predictor_1_25= new arima(q,d,p, path, 1,2); // the window representing the number of days s
//                double[] r7 = predictor_1_15.predictProcess(1,1);
//
//                double[] r8 = predictor_1_25.predictProcess(1,2);
//                rmses.add(r7);
//                rmses.add(r8);d
        double totalCPU = 0;
        double totalMem = 0;
        for (double[] dss: rmses
             ) {
            System.out.println("CPU : " + dss[0]);
            System.out.println("Mem : " + dss[1]);

        }


    }
}
