import java.io.*;
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

    public static void testcase() {
        int order = 3;
        BTree bTree = new BTree(3);
        Visualization visualization = new Visualization();

        System.out.println("=================== 테스트 케이스 실행 ===================");
        System.out.println("테스트 케이스 출력은 차수가 3으로 고정됩니다.");

        System.out.println("\n트리 출력 방식");
        System.out.printf("입력 1: 콘솔에서 텍스트로 출력\n입력 2: GUI로 출력\n입력 3: 둘 다 출력\n");

        Scanner scanner = new Scanner(System.in);
        int printMethod = getNumber(1, 3, "트리 출력 방식: ");

        try {
            BufferedReader reader = new BufferedReader(new FileReader("test_case.txt"));
            String str;
            while ((str = reader.readLine()) != null) {
                String[] str_s = str.split(",");
                System.out.println("-- " + str_s[2] + " " + str_s[1] + ", " + str_s[3] + " --");
                switch (str_s[0]) {
                    case "i":
                        bTree.insertNode(Integer.parseInt(str_s[1]));
                        break;
                    case "d":
                        bTree.deleteNode(Integer.parseInt(str_s[1]));
                        break;
                    default:
                        throw new Exception();
                }
                if (printMethod != 2) bTree.print();
                if (printMethod >= 2) {
                    visualization.setInformation(bTree.getTreeData());
                    visualization.show(false);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("케이스를 저장한 파일을 찾을 수 없습니다.");
        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int getNumber(int min, int max, String msg) {
        Scanner scanner = new Scanner(System.in);
        int num = 0;
        do {
            try {
                System.out.printf(msg);
                num = scanner.nextInt();
            } catch (Exception e) {
                num = -1;
                scanner = new Scanner(System.in);
            }
        } while (num < min || num > max);

        return num;
    }

    public static int getOrder() {
        return getNumber(3, Integer.MAX_VALUE, "차수 입력: ");
    }

    public static void main(String[] args) {
        System.out.println("=================== B-tree 구현 ===================");

        System.out.printf("입력 1: 랜덤한 값으로 테스트\n입력 2: 직접 명령어를 입력해서 테스트\n입력 3: 테스트 케이스 실행\n");
        int selected = getNumber(1, 3, "실행할 모드: ");
        switch (selected) {
            case 1:
                random(getOrder());
                break;
            case 2:
                input(getOrder());
                break;
            case 3:
                testcase();
                break;
            default:
                System.out.println("종료");
        }
        System.exit(0);
    }
}
