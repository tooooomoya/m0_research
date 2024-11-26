package main.utils;

public class matrix_util {
    // COPY matrix
    public static double[][] copyMatrix(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] newMatrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        return newMatrix;
    }
    

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

    //MUL matrix with vector
    public static double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        
        if (cols != vector.length) {
            throw new IllegalArgumentException("Matrix columns must be equal to vector length");
        }
        
        double[] result = new double[rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i] += matrix[i][j] * vector[j];
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

    // Create the Laplacian matrix L = D - A
    public static double[][] createL(double[][] adjacencyMatrix, int size) {
        int n = size;
        double[][] L = new double[n][n];

        // Create degree matrix D
        double[][] D = new double[n][n];
        for (int i = 0; i < n; i++) {
            double degree = 0;
            for (int j = 0; j < n; j++) {
                degree += adjacencyMatrix[i][j];
            }
            D[i][i] = degree;
        }

        // Compute L = D - A
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                L[i][j] = D[i][j] - adjacencyMatrix[i][j];
            }
        }

        return L;
    }

    // Create an identity matrix of size n x n
    public static double[][] createIdentityMatrix(int size) {
        double[][] identityMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            identityMatrix[i][i] = 1.0;
        }
        return identityMatrix;
    }

    public static double[][] createZeroMatrix(int rows, int cols) {
        double[][] matrix = new double[rows][cols]; // 指定されたサイズの行列を作成
        // デフォルトでは、double型の配列はすべて0で初期化される
        return matrix;
    }

    // print matrix
    public static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.printf("%.2f ", val);  // 少数第2位まで表示
            }
            System.out.println();  // 行の最後に改行
        }
    }

    //print vector
    public static void printVector(double[] vector) {
        for (double val : vector) {
            System.out.printf("%.2f, ", val);  // 小数第2位まで表示
        }
    }

    public static void printDist(double[] z){
        int a = 0, b = 0, c = 0, d = 0, e = 0;
            for (int t = 0; t < z.length; t++) {
                if (-1 <= z[t] && z[t] < -0.6) {
                    a++;
                } else if (z[t] < -0.2) {
                    b++;
                } else if (z[t] < 0.2) {
                    c++;
                } else if (z[t] < 0.6) {
                    d++;
                } else if (z[t] <= 1.0) {
                    e++;
                }
            }

            System.out.println("Confirm the distribution of z (opinions) ↓↓↓");
            System.out.printf("-1 ~ -0.6: %d\n", a);
            System.out.printf("-0.6 ~ -0.2: %d\n", b);
            System.out.printf("-0.2 ~ 0.2: %d\n", c);
            System.out.printf("0.2 ~ 0.6: %d\n", d);
            System.out.printf("0.6 ~ 1.0: %d\n", e);
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
