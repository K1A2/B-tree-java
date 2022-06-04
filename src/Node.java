import java.util.Arrays;

public class Node {
    private int order = 0;
    private int keys[];
    private int numKeys = 0;
    private Node childNode[];
    private int numChild = 0;
    private Node parentNode;
    private NodeSorter nodeSorter = null;

    public Node(int order) {
        this.order = order;
        this.childNode = new Node[order + 1];
        this.parentNode = null;
        this.keys = new int[order];
        this.nodeSorter = new NodeSorter();
        Arrays.fill(this.keys, Integer.MAX_VALUE);
    }

    public int getNumKeys() {
        return numKeys;
    }

    public int[] getKeys() {
        return keys;
    }

    public Node[] getChildNode() {
        return childNode;
    }

    public int getNumChild() {
        return numChild;
    }

    public void setChildNode(Node[] childNode) {
        this.childNode = childNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setNumKeys(int numKeys) {
        this.numKeys = numKeys;
    }

    public Node divideNode() {
        Node newNode = new Node(order);
        int divideIdx = (order - 1) / 2;
        int divideKey = keys[divideIdx];
        keys[divideIdx] = Integer.MAX_VALUE;
        int toIdx = numKeys;
        for (int i = divideIdx + 1;i < toIdx;i++) {
            newNode.addKey(keys[i]);
            keys[i] = Integer.MAX_VALUE;
            numKeys--;
        }
        numKeys--;
        if (numChild == order + 1) {
            toIdx = numChild;
            for (int i = 0;i < toIdx;i++) {
                if (childNode[i].getKeys()[0] < divideKey) continue;
                newNode.addChildNode(childNode[i]);
                childNode[i].setParentNode(newNode);
                childNode[i] = null;
                numChild--;
            }
        }
        return newNode;
    }

    public boolean addKey(int key) {
        for (int i = 0;i < numKeys;i++) {
            if (keys[i] == key) {
                System.out.printf("이미 %d가 존재합니다.\n", key);
                return false;
            }
        }
        keys[numKeys] = key;
        Arrays.sort(keys);
        numKeys++;

        if (numKeys == order) {
            return true;
        } else {
            return false;
        }
    }

    public void addChildNode(Node child) {
        childNode[numChild] = child;
        numChild++;
        Arrays.sort(childNode, nodeSorter);
    }
}
