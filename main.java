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
        String path = "C:\\Users\\vini\\Desktop\\1411\\predictionData.csv";
        int[] qs = new int[]{0,1,2,3,4,5,6};
        int[] ds = new int[]{0,1,2,3,4,5,6};
        int[] ps = new int[]{0,1,2,3,4,5,6};
        ArrayList<double[]> rmses = new ArrayList<>();
        int q = 1;
        int d = 1;
        int p = 1;
        arima predictor_1_1 = new arima(q,d,p, path); // the window representing the number of days s
        double[] r1 = predictor_1_1.predictProcess(1,10);
        rmses.add(r1);
        double totalCPU = 0;
        double totalMem = 0;
        for (double[] dss: rmses
             ) {
            System.out.println("CPU : " + dss[0]);
            System.out.println("Mem : " + dss[1]);

        }


    }
}
