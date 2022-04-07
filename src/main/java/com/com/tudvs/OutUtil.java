package com.com.tudvs;

/**
 * @author admin
 * @create 2022-04-02 16:48
 * @
 */
public class OutUtil {
    public static void println(Object object) {
        System.out.println("\033[40;31;4m" + object.toString() + "\033[0m");
/*      其他颜色。。
        System.out.println("\033[30;4m" + object.toString() + "\033[0m");
        System.out.println("\033[31;4m" + object.toString() + "\033[0m");
        System.out.println("\033[32;4m" + object.toString() + "\033[0m");
        System.out.println("\033[33;4m" + object.toString() + "\033[0m");
        System.out.println("\033[34;4m" + object.toString() + "\033[0m");
        System.out.println("\033[35;4m" + object.toString() + "\033[0m");
        System.out.println("\033[36;4m" + object.toString() + "\033[0m");
        System.out.println("\033[37;4m" + object.toString() + "\033[0m");
        System.out.println("\033[40;31;4m" + object.toString() + "\033[0m");
        System.out.println("\033[41;32;4m" + object.toString() + "\033[0m");
        System.out.println("\033[42;33;4m" + object.toString() + "\033[0m");
        System.out.println("\033[43;34;4m" + object.toString() + "\033[0m");
        System.out.println("\033[44;35;4m" + object.toString() + "\033[0m");
        System.out.println("\033[45;36;4m" + object.toString() + "\033[0m");
        System.out.println("\033[46;37;4m" + object.toString() + "\033[0m");
        System.out.println("\033[47;4m" + object.toString() + "\033[0m");*/
    }
    public static void printlnBlue(Object object){
        System.out.println("\033[32;4m" + object.toString() + "\033[0m");
    }
    public static void main(String[] args) {
        printlnBlue(1);
    }
}
