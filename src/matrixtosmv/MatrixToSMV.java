/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrixtosmv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author kn
 */
public class MatrixToSMV {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Map<Pair<Integer, Integer>, Integer> input = new HashMap();
        Map<Pair<Integer, Integer>, Integer> output = new HashMap();
        Map<Integer, Integer> marking = new HashMap();
        
        File file = new File("sample.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = br.readLine();
        
        //Read BackwardIncidenceMatrix
        if(!str.equals("BackwardIncidenceMatrix")){
            return;
        }
        int i = 1;
        while(true){
            str = br.readLine();
            if(str.equals("ForwardIncidenceMatrix")){
                break;
            }
            String[] array = str.split(" ");
            for(int j = 0; j < array.length; j++){
                input.put(new Pair(i, j+1), Integer.parseInt(array[j]));
            }
            i++;
        }
        
        //Read ForwardIncidenceMatrix
        i = 1;
        while(true){
            str = br.readLine();
            if(str.equals("InitialMarking")){
                break;
            }
            String[] array = str.split(" ");
            for(int j = 0; j < array.length; j++){
                output.put(new Pair(i, j+1), Integer.parseInt(array[j]));
            }
            i++;
        }
        
        //Read InitialMarking
        i = 1;
        while((str = br.readLine()) != null){
            marking.put(i, Integer.parseInt(str));
            i++;
        }
        
        br.close();
        
        String result = convert(input, output, marking);
        System.out.println(result);
    }
    
    public static String convert(Map<Pair<Integer, Integer>, Integer> input, Map<Pair<Integer, Integer>, Integer> output, Map<Integer, Integer> marking){
        //Pair<Place, Transition>の順
        String result = "";
        final String separator = System.lineSeparator();
        
        int p = 0; //プレース数
        for(Pair<Integer, Integer> pair : input.keySet()){
            if(pair.getKey() > p){
                p = pair.getKey();
            }
        }
        
        int t = input.keySet().size() / p; //トランジション数
        
        int max = 0;
        //トークンの最大数を設定
        for(int n : marking.values()){
            if(n > max)
                max = n;
        }
        
        result += "MODULE main" + separator + separator;
        result += "DEFINE M := " + max + ";" + separator + separator;
        
        result += "VAR" + separator;
        result += "fire : 0.." + t + ";" + separator;  //次に発火するトランジション
        result += "p : array 1.." + p + " of 0..M;" + separator; //マーキング
        result += "t : array 1.." + t + " of boolean;" + separator + separator; //発火可能性
        
        result += "TRANS" + separator;
        for(int i = 1; i <= t; i++){
            result += "(next(t[" + i + "]) & next(fire) = " + i + ") | " + separator;
        }
        result += "(";
        for(int i = 1; i <= t; i++){
            result += "!next(t[" + i + "]) & ";
        }
        result += "next(fire) = 0)" + separator + separator;
        
        result += "ASSIGN" + separator;
        result += "init(fire) := case" + separator;
        for(int i = 1; i <= t; i++){
            result += "t[" + i + "] : " + i + ";" + separator;
        }
        result += "TRUE : 0;" + separator + "esac;" + separator;
        for(int i = 1; i <= p; i++){
            result += "init(p[" + i + "]) : " + marking.get(i) + ";" + separator;
        }
        
        for(int i = 1; i <= t; i++){
            ArrayList tmpList = new ArrayList();
            for(int j = 1; j <= p; j++){
                if(input.get(new Pair(j, i)) > 0){
                    
                }
            }
        }
        
        return result;
    }
}
