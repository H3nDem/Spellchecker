import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        System.out.println("Building map...");
        double d1 = System.nanoTime() / 10e8;
        Correcteur c = new Correcteur();
        double d2 = System.nanoTime() / 10e8;
        System.out.println("Map built in " + (d2-d1) + " seconds\n");

        Scanner reader = new Scanner(System.in);
        System.out.print("Write a word and you will find the most close: ");
        System.out.println("Now searching, please wait...");
        double d3 = System.nanoTime() / 10e8;
        //Scanner reader = new Scanner(new File("fautes.txt"));
//        while (reader.hasNextLine()) {
//            c.correctWord(reader.nextLine());
//        }
        c.correctWord(reader.nextLine());
        System.out.println("Search done !");
        double d4 = System.nanoTime() / 10e8;


        System.out.println("Word corrected in " + (d4 - d3) + " seconds\n");
    }

}
