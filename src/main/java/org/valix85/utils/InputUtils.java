package org.valix85.utils;

import java.sql.SQLOutput;
import java.util.Scanner;

public class InputUtils {

    public static String readLine(String msg){
        System.out.println(msg);
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

}
