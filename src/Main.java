import java.io.IOException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws ParseException {
        Instant start = Instant.now();
        DataReader dataReader = new DataReader();
        double[][] doubleData;
        try {
            ImageMatrix[] testingData = dataReader.readData("t10k-images.idx3-ubyte", "t10k-labels.idx1-ubyte");
            ImageMatrix[] data = dataReader.readData("train-images.idx3-ubyte", "train-labels.idx1-ubyte");
            normalizeData(data); normalizeData(testingData);
            Network network = new Network(0.3,784,(800),(600), 10);
            //Train the network with the data twice
            for(int i = 0; i < 60000; i++){
                double[] input = data[i].getNormalizedData();
                double[] label = data[i].getLabel();
                network.train(input,label);
            }
            evaluate(testingData,network,10000);


        } catch (IOException e){
            e.printStackTrace();
        }
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start,finish).toMillis();
        System.out.println("The total time elapsed running this program is: " + (timeElapsed/1000)  + "seconds");

    }

    //Data normalizer method
    private static void normalizeData(ImageMatrix[] data){
        for(int i = 0; i < data.length; i++){
            data[i].setNormalizedData(data[i].getDoubleDataArray());
        }
    }
    private static void evaluate(ImageMatrix[] testingData, Network network, int comparisonEpochs){
        int correctCount = 0;
        if(comparisonEpochs > testingData.length){
            return;
        }
        for(int i = 0; i < comparisonEpochs; i++){


            double[] testingInfo = testingData[i].getNormalizedData();
            double[] labelResult = testingData[i].getLabel();
            double[] feedForwardResult = network.compute(testingInfo);


            for(int j = 0; j < 10; j++){
                if(feedForwardResult[j] < 0.5){
                    feedForwardResult[j] = 0;
                }
                if(feedForwardResult[j] >= 0.5){
                    feedForwardResult[j] = 1;
                }
            }

            boolean match = true;
            for(int k = 0; k < feedForwardResult.length; k++){
                if(feedForwardResult[k] != labelResult[k]){
                    match = false;
                }
            } if (match) correctCount++;



        }
        System.out.println("The number of matches are...." + correctCount);
        System.out.println("The accuracy of our network was... " + (((double)correctCount / (double)comparisonEpochs)*100) + "%");
    }

}
