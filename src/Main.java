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
        // 랜덤 테스트를 진행한 후, 명령어 목록을 텍스트 파일로 저장
        File file = new File("./commands.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static void random(int order) {
        // 랜덤한 숫자를 뽑아 테스트 진행
        Visualization visualization = new Visualization();
        ArrayList<Integer> arrayList = new ArrayList<>();
        Random random = new Random();

        StringBuilder builder = new StringBuilder();

        BTree btree = new BTree(order);
        try {
            for (int i = 0;i < 500;i ++) {
                int insert = random.nextInt(1000);
                if (arrayList.contains(insert)) continue;
                arrayList.add(insert);
                builder.append("i " + insert + "\n");
                btree.insertNode(insert);
            }

            System.out.println("입력 목록: " + Arrays.toString(arrayList.toArray()));

            for (int i = 0;i < 100;i++) {
                int pos = random.nextInt(arrayList.size());
                int delete = arrayList.remove(pos);
                builder.append("d " + delete + "\n");
                btree.deleteNode(delete);
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

    private static void input(int order) {
        // 명령어를 입력하여 B-tree에 데이터를 추가/삭제할 수 있는 함수
        System.out.println("\n=================== 명령어 입력 테스트 ===================");
        System.out.println("입력 i [숫자]: 숫자 추가");
        System.out.println("입력 d [숫자]: 숫자 삭제");
        System.out.println("입력 s: B-tree 시각화");
        System.out.println("입력 q: 종료");

        Visualization visualization = new Visualization();

        BTree btree = new BTree(order);
        String i = "";
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.printf("입력: ");
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
        System.out.println("=================== B-tree 구현 ===================");

        Scanner scanner = new Scanner(System.in);
        int order = 3;

        do {
            try {
                System.out.printf("차수 입력: ");
                order = scanner.nextInt();
            } catch (Exception e) {
                order = -1;
                scanner = new Scanner(System.in);
            }
        } while (order < 3);

        System.out.printf("입력 1: 랜덤한 값으로 테스트\n입력 2: 직접 명령어를 입력해서 테스트\n입력: ");
        switch (scanner.nextInt()) {
            case 1:
                random(order);
                break;
            case 2:
                input(order);
                break;
            default:
                System.out.println("종료");
        }
    }
}
