/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixtosmv;

import java.util.ArrayList;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author kn
 */
public class MatrixToSMV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
    public static String convert(Map<Pair<Integer, Integer>, Integer> input, Map<Pair<Integer, Integer>, Integer> output, Map<Integer, Integer> marking){
        String result = "";
        final String separator = System.lineSeparator();
        
        int max = 0;
        //トークンの最大数を設定
        for(int n : marking.values()){
            if(n > max)
                max = n;
        }
        
        result += "MODULE main" + separator + separator;
        result += "DEFINE M := " + max + ";" + separator + separator;
        
        result += "VAR" + separator;
        result += "fire : 0.." + input.values().size() + ";" + separator;  //次に発火するトランジション
        result += "p : array 1.." + input.keySet().size() + "of 0..M;" + separator; //マーキング
        result += "t : array 1.." + input.values().size() + "of boolean;" + separator + separator; //発火可能性
        
        result += "TRANS" + separator;
        for(int i = 1; i <= input.values().size(); i++){
            result += "(next(t[" + i + "]) & next(fire) = " + i + ") | " + separator;
        }
        result += "(";
        for(int i = 1; i <= input.values().size(); i++){
            result += "!next(t[" + i + "]) & ";
        }
        result += "next(fire) = 0)" + separator + separator;
        
        result += "ASSIGN" + separator;
        result += "init(fire) := case" + separator;
        for(int i = 1; i <= input.values().size(); i++){
            result += "t[" + i + "] : " + i + ";" + separator;
        }
        result += "TRUE : 0;" + separator + "esac;" + separator;
        for(int i = 1; i <= input.keySet().size(); i++){
            result += "init(p[" + i + "]) : " + marking.get(i) + ";" + separator;
        }
        
        for(int i = 1; i <= input.values().size(); i++){
            ArrayList tmpList = new ArrayList();
            for(int j = 1; j <= input.keySet().size(); j++){
                
            }
        }
        
        return result;
    }
}
