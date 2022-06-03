public class Main {
    public static void main(String[] args) {
        BTree tree_2 = new BTree(6);

        for (int i = 0;i < 31;i ++) {
            tree_2.insertNode(i);
            tree_2.print();
        }
    }
}
