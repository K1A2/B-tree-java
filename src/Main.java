import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static void writeFile(String s) {
        File file = new File("./commands.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static void random() {
        Visualization visualization = new Visualization();
        ArrayList<Integer> arrayList = new ArrayList<>();
        Random random = new Random();

        StringBuilder builder = new StringBuilder();

        BTree btree = new BTree(3);
        try {
            for (int i = 0;i < 500;i ++) {

                int command = random.nextInt(2);
                if (command == 0) {
                    int insert = random.nextInt(1000);
                    if (arrayList.contains(insert)) continue;
                    arrayList.add(insert);
                    builder.append("i " + insert + "\n");
                    btree.insertNode(insert);
                } else {
                    if (arrayList.size() == 0) continue;
                    int pos = random.nextInt(arrayList.size());
                    int delete = arrayList.remove(pos);
                    builder.append("d " + delete + "\n");
                    btree.deleteNode(delete);
                }
                System.out.println("입력 목록: " + Arrays.toString(arrayList.toArray()));
            }
        } catch (Exception e) {
            writeFile(builder.toString());
            System.out.println(e.getMessage());
            return;
        }
        btree.print();
        writeFile(builder.toString());

        visualization.setInformation(btree.getTreeData());
        visualization.show(true);
    }

    private static void input() {
        Visualization visualization = new Visualization();

        BTree btree = new BTree(5);
        String i = "";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            i = scanner.nextLine();
            if (i.equals("q")) break;
            System.out.println(i);
            String[] commands = i.split("\\s++");
            switch (commands[0]) {
                case "i":
                    btree.insertNode(Integer.parseInt(commands[1]));
                    break;
                case "d":
                    btree.deleteNode(Integer.parseInt(commands[1]));
                    break;
                case "s":
                    visualization.setInformation(btree.getTreeData());
                    visualization.show(false);
                    break;
            }
            btree.print();
        }
    }

    public static void main(String[] args) {
        random();
//        input();
    }
}
