import org.apache.commons.math3.linear.*;

public class MatrixInversionExample {
    public static void main(String[] args) {
        // 2x2行列のデータ
        double[][] matrixData = {
            {4, 7},
            {2, 6}
        };

        // 行列を作成
        RealMatrix matrix = MatrixUtils.createRealMatrix(matrixData);

        try {
            // 逆行列を計算
            RealMatrix inverseMatrix = new LUDecomposition(matrix).getSolver().getInverse();

            // 結果を表示
            System.out.println("Original Matrix:");
            System.out.println(matrix);
            System.out.println("Inverse Matrix:");
            System.out.println(inverseMatrix);
        } catch (SingularMatrixException e) {
            System.out.println("The matrix is singular and cannot be inverted.");
        }
    }
}
