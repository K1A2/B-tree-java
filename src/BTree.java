import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

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
                            currentNode = currentNode.getChildrenNode()[i];
                            break;
                        } else if (keys[i] == newData) {
                            System.out.printf("이미 %d가 존재합니다.\n", newData);
                            return;
                        }
                    }
                }
                if (!find) {
                    currentNode = currentNode.getChildrenNode()[currentNode.getNumKeys()];
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
                for (Node n : nowNode.getChildrenNode()) {
                    if (n != null) deque.add(new Object[]{n, level + 1});
                }
            }
            System.out.println("\n\n");
        } else {
            System.out.println("B-tree가 비었습니다.");
        }
    }

    public DefaultMutableTreeNode getTreeData() {
        if (root != null) {
            String keys = "[ ";
            for (int i : root.getKeys()) {
                if (i != Integer.MAX_VALUE) keys += String.valueOf(i);
            }
            keys += "]";
            DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(keys);

            HashMap<Integer, DefaultMutableTreeNode> hashMap = new HashMap<>();
            hashMap.put(root.getKeys()[0], rootTreeNode);

            Deque<Node> deque = new ArrayDeque<>();
            deque.add(root);

            while (!deque.isEmpty()) {
                Node nowNode = deque.pop();
                for (Node n : nowNode.getChildrenNode()) {
                    if (n != null) {
                        keys = "[ ";
                        for (int i : n.getKeys()) {
                            if (i != Integer.MAX_VALUE) keys += String.valueOf(i) + " ";
                        }
                        keys += "]";
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(keys);
                        hashMap.put(n.getKeys()[0], node);
                        hashMap.get(n.getParentNode().getKeys()[0]).add(node);
                        deque.add(n);
                    }
                }
            }

            return rootTreeNode;
        } else {
            System.out.println("B-tree가 비었습니다.");
            return null;
        }
    }
}
