import java.util.Arrays;

public class matrix_util {
    //ADD with matrix
    public static double[][] add(double[][] matrix1, double[][] matrix2) {
        int rows = matrix1.length;
        int cols = matrix1[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix1[i][j] + matrix2[i][j];
            }
        }
        return result;
    }

    // SUB with matrix
    public static double[][] subtract(double[][] matrix1, double[][] matrix2) {
        int rows = matrix1.length;
        int cols = matrix1[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix1[i][j] - matrix2[i][j];
            }
        }
        return result;
    }

    // MUL with matrix
    public static double[][] multiply(double[][] matrix1, double[][] matrix2) {
        int rows1 = matrix1.length;
        int cols1 = matrix1[0].length;
        int rows2 = matrix2.length;
        int cols2 = matrix2[0].length;

        // Check if matrix multiplication is possible
        if (cols1 != rows2) {
            throw new IllegalArgumentException("Matrix dimensions do not match for multiplication.");
        }

        double[][] result = new double[rows1][cols2];

        for (int i = 0; i < rows1; i++) {
            for (int j = 0; j < cols2; j++) {
                result[i][j] = 0; // Initialize to 0
                for (int k = 0; k < cols1; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return result;
    }


    // ScalarMUL with matrix
    public static double[][] scalarMultiply(double[][] matrix, double scalar) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] result = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = matrix[i][j] * scalar;
            }
        }
        return result;
    }

    // Transpose of matrix
    public static double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        
        return transposed;
    }

    // print matrix
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }


    // use main to check
    public static void main(String[] args) {
        double[][] matrix1 = {
            {1, 2, 3},
            {4, 5, 6},
            {1, 1, 3}
        };

        double[][] matrix2 = {
            {7, 8, 9},
            {10, 11, 12},
            {2, 5, 6}
        };

        double[][] addedMatrix = matrix_util.add(matrix1, matrix2);
        double[][] subtractedMatrix = matrix_util.subtract(matrix1, matrix2);
        double[][] multipliedMatrix = matrix_util.multiply(matrix1, matrix2);
        double[][] scalarMultipliedMatrix = matrix_util.scalarMultiply(matrix1, 2);

        System.out.println("Added Matrix:");
        matrix_util.printMatrix(addedMatrix);

        System.out.println("\nSubtracted Matrix:");
        matrix_util.printMatrix(subtractedMatrix);

        System.out.println("\nMultiplied Matrix:");
        matrix_util.printMatrix(multipliedMatrix);

        System.out.println("\nScalar Multiplied Matrix:");
        matrix_util.printMatrix(scalarMultipliedMatrix);
    }
}
