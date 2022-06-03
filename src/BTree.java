import java.util.ArrayDeque;
import java.util.Deque;

public class BTree {
    private Node root = null;
    private int order = 0;

    public BTree(int order) {
        this.order = order;
    }

    public void insertNode(int newData) {
        if (root == null) {
            root = new Node(order);
            root.addKey(newData);
        } else {
            Node currentNode = root;
            while (currentNode.getNumChild() != 0) {
                int[] keys = currentNode.getKeys();
                boolean find = false;
                for (int i = 0;i < currentNode.getNumKeys();i++) {
                    if (keys[i] != Integer.MAX_VALUE && keys[i] > newData) {
                        find = true;
                        currentNode = currentNode.getChildNode()[i];
                        break;
                    }
                }
                if (!find) {
                    currentNode = currentNode.getChildNode()[currentNode.getNumKeys()];
                }
            }
            if (currentNode.addKey(newData)) {
                while (currentNode.getNumKeys() >= order) {
                    int divide_key = currentNode.getKeys()[(order - 1) / 2];
                    Node parentNode = currentNode.getParentNode();
                    if (parentNode != null) {
                        parentNode.addKey(divide_key);
                    } else {
                        parentNode = new Node(order);
                        parentNode.addKey(divide_key);
                        parentNode.addChildNode(currentNode);
                        currentNode.setParentNode(parentNode);
                    }
                    Node newNode = currentNode.devideNode();
                    newNode.setParentNode(parentNode);
                    parentNode.addChildNode(newNode);
//                    System.out.printf("key: %d, child count: %d\n", parentNode.getKeys()[0], parentNode.getNumChild());
                    if (currentNode == root) {
                        root = parentNode;
                    }
                    currentNode = parentNode;
                }
            }
        }
    }

    public void print() {
        System.out.println("===========");
        if (root != null) {
            Deque<Object[]> deque = new ArrayDeque<>();
            deque.add(new Object[] {root, 1});
            while (!deque.isEmpty()) {
                Object[] now = deque.pop();
                Node nowNode = (Node)now[0];
                int level = (int)now[1];
                System.out.printf("레벨 %d: [ ", level);
                for (int i : nowNode.getKeys()) {
                    if (i != Integer.MAX_VALUE) System.out.printf("%d ", i);
                }
                System.out.println("]");
                for (Node n : nowNode.getChildNode()) {
                    if (n != null) deque.add(new Object[] {n, level + 1});
                }
            }
        } else {
            System.out.println("B-tree가 비었습니다.");
        }
    }
}
