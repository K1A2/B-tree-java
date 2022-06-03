public class Main {
    public static void main(String[] args) {
        BTree tree_2 = new BTree(7);
//        int[] insert = {38, 15, 85, 22, 12, 87, 32, 48, 91, 95, 1, 73, 90, 65, 96, 92, 42, 71, 35, 94, 84, 50, 42, 50, 40, 46, 6, 66, 5, 16, 64, 55, 36, 18, 87, 25, 50, 97, 97, 48, 17, 83, 63, 9, 34, 52, 71, 97, 87, 24, 4};
        System.out.printf("[");
        for (int i = 0;i < 51;i ++) {
            int insert = (int)(Math.random() * 100);
            System.out.printf("%d,", insert);
            tree_2.insertNode(insert);
//            tree_2.print();
        }
        System.out.println("]");
        tree_2.print();
    }
}
