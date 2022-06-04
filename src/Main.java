import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) {

        Visualization visualization = new Visualization();

        BTree btree = new BTree(3);
//        int[] insert = {8,77,57,26,74,0,15,49,71,15,17,95,35,95,2,56,9,29,19,24,20,50,46,15,51,59,40,64,23,2,91,45,45,84,73,78,17,22,97,31,27,4,58,25,0,32,56,2,16,81,77,23,87,67,8,32,61,38,93,18,96,81,76,93,41,73,95,10,28,80,68,21,6,51,80,9,61,45,25,88,1,66,84,24,84,71,7};
//        System.out.printf("[");
        for (int i = 0;i < 200;i ++) {
//            System.out.printf("%d\n", insert[i]);
//            tree_2.insertNode(insert[i]);

            int insert = (int)(Math.random() * 1000);
//            System.out.printf("%d,", insert);
            btree.insertNode(insert);
//            tree_2.print();
        }
//        System.out.println("]");
        btree.print();

        visualization.setInformation(btree.getTreeData());
        visualization.show();
    }
}
