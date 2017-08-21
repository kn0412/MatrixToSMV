package matrixtosmv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.util.Pair;

/**
 *
 * @author kn
 */
public class MatrixToSMV {

    public static void main(String[] args) throws Exception {
        String inputFile = "sample.txt";
        String outputFile = "";
        switch(args.length){
            case 2:
                outputFile = args[1];
            case 1:
                inputFile = args[0];
                break;
            case 0:
                break;
            default:
                return;
        }

        Map<Pair<Integer, Integer>, Integer> input = new HashMap();
        Map<Pair<Integer, Integer>, Integer> output = new HashMap();
        Map<Integer, Integer> marking = new HashMap();

        File file = new File(inputFile);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String str = br.readLine();
        
        String caption1 = "BackwardIncidenceMatrix";
        String caption2 = "ForwardIncidenceMatrix";
        String caption3 = "InitialMarking";

        //Read BackwardIncidenceMatrix
        if (!str.equals(caption1)) {
            return;
        }
        int i = 1;
        while (true) {
            str = br.readLine();
            if (str.equals(caption2)) {
                break;
            }
            String[] array = str.split(" ");
            for (int j = 0; j < array.length; j++) {
                input.put(new Pair(i, j + 1), Integer.parseInt(array[j]));
            }
            i++;
        }

        //Read ForwardIncidenceMatrix
        i = 1;
        while (true) {
            str = br.readLine();
            if (str.equals(caption3)) {
                break;
            }
            String[] array = str.split(" ");
            for (int j = 0; j < array.length; j++) {
                output.put(new Pair(i, j + 1), Integer.parseInt(array[j]));
            }
            i++;
        }

        //Read InitialMarking
        i = 1;
        while ((str = br.readLine()) != null) {
            marking.put(i, Integer.parseInt(str));
            i++;
        }

        br.close();

        String result = convert(input, output, marking);
        
        if(outputFile.isEmpty()){
            System.out.println(result);
        }
        else{
            file = new File(outputFile);
            FileWriter filewriter = new FileWriter(file);
            filewriter.write(result);
            filewriter.close();
        }
    }

    public static String convert(Map<Pair<Integer, Integer>, Integer> input, Map<Pair<Integer, Integer>, Integer> output, Map<Integer, Integer> marking) {
        //Pair<Place, Transition>の順
        String result = "";
        final String separator = System.lineSeparator();

        int p = 0; //プレース数
        for (Pair<Integer, Integer> pair : input.keySet()) {
            if (pair.getKey() > p) {
                p = pair.getKey();
            }
        }

        int t = input.keySet().size() / p; //トランジション数

        int max = 0;
        //トークンの最大数を設定
        for (int n : marking.values()) {
            if (n > max) {
                max = n;
            }
        }

        result += "MODULE main" + separator + separator;
        result += "DEFINE M := " + max + ";" + separator + separator; //最大値を宣言

        result += "VAR" + separator;
        result += "fire : 0.." + t + ";" + separator;  //次に発火するトランジション
        result += "p : array 1.." + p + " of 0..M;" + separator; //マーキング
        result += "t : array 1.." + t + " of boolean;" + separator + separator; //発火可能性

        result += "TRANS" + separator;
        for (int i = 1; i <= t; i++) {
            result += "(next(t[" + i + "]) & next(fire) = " + i + ") | " + separator;
        }
        result += "(";
        for (int i = 1; i <= t; i++) {
            result += "!next(t[" + i + "]) & ";
        }
        result += "next(fire) = 0)" + separator + separator;

        result += "ASSIGN" + separator;
        result += "init(fire) := case" + separator;
        for (int i = 1; i <= t; i++) {
            result += "t[" + i + "] : " + i + ";" + separator;
        }
        result += "TRUE : 0;" + separator + "esac;" + separator;
        for (int i = 1; i <= p; i++) {
            result += "init(p[" + i + "]) : " + marking.get(i) + ";" + separator;
        }

        for (int i = 1; i <= t; i++) {
            ArrayList<String> initCondList = new ArrayList();
            ArrayList<String> nextCondList = new ArrayList();
            //入力プレースを確認
            for (int j = 1; j <= p; j++) {
                if (input.get(new Pair(j, i)) > 0) {
                    initCondList.add("p[" + j + "] >= " + input.get(new Pair(j, t)));
                    nextCondList.add("next(p[" + j + "]) >= " + input.get(new Pair(j, t)));
                }
            }

            //トランジションの発火可能性
            result += "init(t[" + i + "]) := " + String.join("&", initCondList) + ";" + separator;
            result += "next(t[" + i + "]) := " + String.join("&", nextCondList) + ";" + separator;
        }

        for (int i = 1; i <= p; i++) {
            int connection = 0;
            result += "next(p[" + i + "]) := ";
            for (int j = 1; j <= t; j++) {
                int backward = input.get(new Pair(i, j));
                int forward = output.get(new Pair(i, j));
                if (backward != forward) {
                    if (connection == 0) {
                        result += "case:" + separator;
                    }
                    connection++;
                    result += "fire = " + j + " & ";
                    if (forward < backward) {
                        result += "p[" + i + "] - " + (backward - forward) + " <= M : p[" + i + "] - " + (backward - forward) + ";" + separator;
                    } else {
                        result += "p[" + i + "] + " + (forward - backward) + " <= M : p[" + i + "] + " + (forward - backward) + ";" + separator;
                    }
                }
            }
            if (connection > 0) {
                result += "TRUE : p[" + i + "];" + separator + "esac;" + separator;
            } else {
                result += "p[" + i + "];" + separator;
            }
        }

        return result;
    }
}
