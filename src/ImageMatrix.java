
//ImageMatrix object to store data in a convenient fashion
public class ImageMatrix {
    private double[][] data;
    private int rows;
    private int columns;
    private double[] label;
    private double[] normalizedData;

    public ImageMatrix(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        this.data = new double[rows][columns];
    }

    public double getValue(int row, int column){
        return this.data[row][column];
    }
    public void setValue(int row, int column, double value){
        data[row][column] = value;
    }

    public double[] getLabel(){
        return this.label;
    }
    public void setLabel(double[] label){
        this.label = label;
    }
    public int RowCount(){
        return rows;
    }
    public int columnCount(){
        return columns;
    }
    //Method to compress the data into a single double array
    public double[] getDoubleDataArray(){
        double[] data = new double[this.columns * this.rows];
        for(int i = 0; i < this.rows; i++){
            for(int j = 0; j < this.columns; j++){
                double k  = this.data[i][j];
                int position = i * j;
                data[(28 * i) + j] = k;
            }
        }
        return data;
    }
    //Normalized data method
    public void setNormalizedData(double[] input){
        double[] returner = new double[input.length];
        for(int i = 0; i < input.length; i++){
            returner[i] = input[i] / 255;
        }
        this.normalizedData = returner;
    }
    public double[] getNormalizedData(){
        return this.normalizedData;
    }
}
