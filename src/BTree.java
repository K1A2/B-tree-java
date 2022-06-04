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
                    if (keys[i] != Integer.MAX_VALUE) {
                        if (keys[i] > newData) {
                            find = true;
                            currentNode = currentNode.getChildNode()[i];
                            break;
                        } else if (keys[i] == newData) {
                            System.out.printf("이미 %d가 존재합니다.\n", newData);
                            return;
                        }
                    }
                }
                if (!find) {
                    currentNode = currentNode.getChildNode()[currentNode.getNumKeys()];
                }
            }
            if (currentNode.addKey(newData)) {
                while (currentNode.getNumKeys() >= order) {
                    int divideKey = currentNode.getKeys()[(order - 1) / 2];
                    Node parentNode = currentNode.getParentNode();
                    if (parentNode != null) {
                        parentNode.addKey(divideKey);
                    } else {
                        parentNode = new Node(order);
                        parentNode.addKey(divideKey);
                        parentNode.addChildNode(currentNode);
                        currentNode.setParentNode(parentNode);
                    }
                    Node newNode = currentNode.divideNode();
                    newNode.setParentNode(parentNode);
                    parentNode.addChildNode(newNode);
                    if (currentNode == root) {
                        root = parentNode;
                    }
                    currentNode = parentNode;
                }
            }
        }
    }

    public void deleteNode(int target) {

    }

    public void print() {
        System.out.println("===========");
        if (root != null) {
            int prevLevel = 0;
            Deque<Object[]> deque = new ArrayDeque<>();
            deque.add(new Object[] {root, 1});
            while (!deque.isEmpty()) {
                Object[] now = deque.pop();
                Node nowNode = (Node)now[0];
                int level = (int)now[1];
                if (prevLevel != level) {
                    System.out.printf("\n레벨 %d\n", level);
                    prevLevel = level;
                }
                System.out.printf("부모: %d [ ", (level != 1 ? nowNode.getParentNode().getKeys()[0] : 0));
                for (int i : nowNode.getKeys()) {
                    if (i != Integer.MAX_VALUE) System.out.printf("%d ", i);
                }
                System.out.printf("] -- ");
                for (Node n : nowNode.getChildNode()) {
                    if (n != null) deque.add(new Object[] {n, level + 1});
                }
            }
            System.out.println("\n\n");
        } else {
            System.out.println("B-tree가 비었습니다.");
        }
    }
}
