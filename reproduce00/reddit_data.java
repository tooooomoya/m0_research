import java.io.*;
import java.util.*;
import org.apache.commons.math3.linear.*;

public class reddit_data {
    public static void main(String[] args){
        int n_reddit=556;
        RealMatrix A=new Array2DRowRealMatrix(n_reddit, n_reddit);
        Map<Integer, List<Double>> z_dict=new HashMap<>();

        for (int i=0; i<n_reddit; i++){
            z_dict.put(i, new ArrayList<>());
        }
    }
}
