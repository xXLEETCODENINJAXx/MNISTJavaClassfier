

import java.util.Arrays;

public class Network {
    //networkSizes is our array of layers
    public final int[] networkSizes;
    public final int outputSize;
    public  final int inputSize;
    public  final int networkLength;
    private final double learningRate;

    private double[][] output;
    public double[][][] weights;
    private double[][] bias;
    private double[][] errorSigns;
    //We want the derivative of the output at each output in the feed forward process.
    private double[][] outputDerivative;

    public Network(double learningRate, int... networkSizes){
        //Initialize the data structures
        this.networkSizes = networkSizes;
        //The last layer is the output
        this.outputSize = networkSizes[networkSizes.length -1];
        this.networkLength = networkSizes.length;
        this.inputSize = networkSizes[0];
        //Each neuron has an error, an output, and an output derivative
        this.errorSigns = new double[networkLength][];
        this.output = new double[networkLength][];
        this.outputDerivative = new double[networkLength][];
        this.bias = new double[networkLength][];
        //Every neuron has an array of weights connecting to the previous layer
        this.weights = new double[networkLength][][];
        this.learningRate = learningRate;

        for(int i = 0; i < networkLength; i++){
            this.output[i] = new double[networkSizes[i]];
            this.outputDerivative[i] = new double[networkSizes[i]];
            this.bias[i] = randomArrayGen(networkSizes[i], 1, 0.01);
            this.errorSigns[i] = new double[networkSizes[i]];
            //No weights for the input layer
            if(i > 0){
                this.weights[i] = random2dArrayGen(networkSizes[i], networkSizes[i-1], 0.1, -0.1);
            }
        }
    }
    public static void main(String[] args){

        Network network = new Network(0.01,4,10,15,4);
        double[] input = new double[]{0.1, 0.5, 0.2, 0.9};
        double[] input2 = new double[]{1.5, 1.5, 0.4, 1.8};
        double[] target = new double[]{0, 0, 0, 1};
        double[] target2 = new double[]{1,0,0,0};
        for(int i = 0; i < 10000; i++) {
            network.train(input,target);
            network.train(input2, target2);
        }
        double[] inputResult = network.compute(new double[]{0.2,0.5,0.2,0.9});
        System.out.println(Arrays.toString(inputResult));
    }
    //Train method
    public void train(double[] input, double[] target){
        if(input.length != inputSize || target.length != outputSize){
            return;
        }
        compute(input);
        backpropagationError(target);
        updateWeights(learningRate);

    }
    //Weight updater
    public void updateWeights(double number){
        for(int i = 1; i < networkLength; i++){
            for(int j = 0; j < networkSizes[i]; j++){
                for(int k = 0; k < networkSizes[i -1]; k++){
                    double change = - number * output[i -1][k]  * errorSigns[i][j];
                    weights[i][j][k] += change;
                }
                double change =  - number * errorSigns[i][j];
                bias[i][j] += change;
            }
        }
    }
    //Changes the error signs of the networks.
    public void backpropagationError(double[] target){
        for(int i = 0; i < networkSizes[networkLength -1]; i++){
            //Chain rule to minimize loss function for the output layer
            errorSigns[networkLength -1][i] = (output[networkLength -1][i] - target[i]) * outputDerivative[networkLength -1][i];
        }
        //Algorithm to minimize loss function for all the hidden layers
        for(int i = networkLength -2; i > 0; i--){
            for(int j = 0; j < networkSizes[i]; j++){
                double error = 0;
                for(int k = 0; k < networkSizes[i +1]; k++){
                    error += weights[i +1][k][j] * errorSigns[i+1][k];
                }
                this.errorSigns[i][j] = error * outputDerivative[i][j];
            }
        }
    }
    //Feed forward method
    public double[] compute(double... input){
        if(input.length != inputSize) {
            System.out.println("ERRRRRRR. Different size between network input layer and input data!");
            return null;
        }
        this.output[0] = input;
        // i == layer
        // j == neuron
        // k == previous neuron

        for(int i = 1; i < networkLength; i++){
            for(int j = 0; j < networkSizes[i]; j++){
                double neuronOutput = 0;
                for(int k = 0; k < networkSizes[i -1] ; k++){
                    neuronOutput += output[i-1][k] * weights[i][j][k];
                }
                neuronOutput += bias[i][j];
                output[i][j] = sigmoidFunction(neuronOutput);
                outputDerivative[i][j] =  output[i][j] * (1 - output[i][j]);
            }
        }
        return output[networkLength -1];
    }
    //Sigmoid function as our activation function in the network
    public double sigmoidFunction(double x){
        return 1d/(1 + Math.exp(-x));
    }
    //Unused tanh activation function
    public double tanhFunction(double x){
        return Math.tanh(x);
    }
    //Method for testing error reduction
    public double differenceError(double[] input, double[] target){
        if(input.length != inputSize || target.length != outputSize){
            System.out.println("Not of the same size! Somehow...");
            return 0;
        }
        double[] comparer = compute(input);
        double error = 0;
        for(int i = 0; i < target.length; i++){
            error += Math.abs(comparer[i] - target[i]);
        }
        return error;
    }


    //Generator for a given random array.
    public static double[] arrayGen(int size, double startingValue){
        if(size < 1){
            return null;
        }
        double[] returner = new double[size];
        for(int i = 0; i < size; i++){
            returner[i] = startingValue;
        }
        return returner;
    }
    //Returns a random double array
    public static double[] randomArrayGen(int size, double max, double min){
        if(size < 1){
            return null;
        }
        double[] returner = new double[size];
        for(int i = 0 ; i < returner.length; i++){
            returner[i] = (Math.random() * (max - min)) + min;
        }
        return returner;
    }
    //Returns a random 2d Double array, using randomArrayGen()
    public static double[][] random2dArrayGen(int length, int width, double max, double min){
        if( length < 1 || width < 1){
            return null;
        }
        double[][] returner = new double[length][width];
        for(int i = 0; i < length; i++){
            returner[i] = randomArrayGen(width, max, min);
        }
        return returner;
    }
    //To check for unique values
    public static <T extends Comparable<T>> boolean containsValue(T[] ar, T value){
        for(int i = 0; i < ar.length; i++){
            if(ar[i] != null){
                if(value.compareTo(ar[i]) == 0){
                    return true;
                }
            }
        }
        return false;
    }


    //Creates a random Integer Array
    public static Integer[] createRandomArray(int lowerBound, int upperBound, int size){
        Integer[] returner = new Integer[size];
        for(int i = 0; i < size; i++){
            int x = (int)(Math.random() * (upperBound - lowerBound) + lowerBound);
            while(containsValue(returner, x)){
                x = (int)(Math.random() * (upperBound - lowerBound) + lowerBound);
            }
            returner[i] = x;
        }
        return returner;
    }
    //Finds the maximum value from a given double array.
    public static int maxValue(double[] values){
        int index = 0;
        for(int i = 0; i < values.length; i++){
            if(values[i] > values[index]){
                index = i;
            }
        }
        return index;
    }


}
