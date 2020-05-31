
import java.io.*;



public class DataReader {
    private ImageMatrix imageMatrix;

    public DataReader(){

    }
    public static ImageMatrix[] readData(String file_name, String label_file_path) throws IOException{
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file_name));
        DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);
        int magicNumber = dataInputStream.readInt();
        int numberOfItems = dataInputStream.readInt();
        int rowCount = dataInputStream.readInt();
        int columnCount = dataInputStream.readInt();

        System.out.println("magic number is " + magicNumber);
        System.out.println("number of items is " + numberOfItems);
        System.out.println("number of rows is: " + rowCount);
        System.out.println("number of cols is: " + columnCount);

        DataInputStream labelInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(label_file_path)));
        int labelMagicNumber = labelInputStream.readInt();
        int numberOfLabels = labelInputStream.readInt();

        System.out.println("label magic number is " + labelMagicNumber);
        System.out.println("number of labels are " + numberOfLabels);

        ImageMatrix[] data = new ImageMatrix[numberOfItems];

        assert numberOfItems == numberOfLabels;

        for(int i = 0; i < numberOfItems; i++){
            //Algorithm for filling the ImageMatrix array
            ImageMatrix imageMatrix = new ImageMatrix(rowCount, columnCount);
            double[] label = new double[10];
            int givenLabel = labelInputStream.readUnsignedByte();
            label[givenLabel] = 1;
            imageMatrix.setLabel(label);
            for(int j = 0 ; j < rowCount; j++){
                for(int k = 0; k < columnCount; k++){
                    imageMatrix.setValue(j, k ,(double)dataInputStream.readUnsignedByte());
                }
            }
            data[i] = imageMatrix;
        }
        dataInputStream.close();
        labelInputStream.close();
        return data;

    }
}
