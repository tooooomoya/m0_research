import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class ND4JTest {
    public static void main(String[] args) {
        // Create two matrices
        INDArray matrixA = Nd4j.create(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        });

        INDArray matrixB = Nd4j.create(new double[][]{
            {9, 8, 7},
            {6, 5, 4},
            {3, 2, 1}
        });

        // Perform matrix addition
        INDArray result = matrixA.add(matrixB);

        // Print the result
        System.out.println("Matrix A:");
        System.out.println(matrixA);

        System.out.println("Matrix B:");
        System.out.println(matrixB);

        System.out.println("Result of A + B:");
        System.out.println(result);
    }
}
