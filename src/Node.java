import java.util.Arrays;

public class Node {
    private static final int NODE_DATA_NULL = Integer.MAX_VALUE;

    private int order = 0;
    private int keys[];
    private int numKeys = 0;
    private Node childrenNodes[];
    private int numChildren = 0;
    private Node parentNode;
    private NodeSorter nodeSorter = null;

    public Node(int order) {
        this.order = order;
        int least = (int) Math.ceil(order / 2.0);

        this.childrenNodes = new Node[order + least + 1];
        this.parentNode = null;
        this.keys = new int[order + least];
        this.nodeSorter = new NodeSorter();
        Arrays.fill(this.keys, NODE_DATA_NULL);
    }

    public int getKeysNum() {
        return numKeys;
    }

    public int[] getKeys() {
        return keys;
    }

    public Node[] getChildrenNodes() {
        return childrenNodes;
    }

    public int getChildrenNum() {
        return numChildren;
    }

    public void setChildrenNodes(Node[] childrenNodes) {
        this.childrenNodes = childrenNodes;
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

    public int getKey(int idx) {
        return keys[idx];
    }

    public Node getChildNode(int idx) {
        return childrenNodes[idx];
    }

    /**
     * 키의 중간 위치를 기준으로 노드를 분할하는 함수.
     * 노드 자신은 왼쪽 노드, 새로 만들어진 노드는 오른쪽 노드가 됨.
     *
     * @param total 키의 전체 길이
     * @return 분할 된 오른쪽 노드 리턴.
     */
    public Node divideNode(int total) {

        Node newNode = new Node(total);
        int divideIdx = (total - 1) / 2;
        int divideKey = keys[divideIdx];
        keys[divideIdx] = NODE_DATA_NULL;
        int toIdx = numKeys;
        for (int i = divideIdx + 1;i < toIdx;i++) {
            newNode.addKey(keys[i]);
            keys[i] = NODE_DATA_NULL;
            numKeys--;
        }
        numKeys--;
        if (numChildren == total + 1) {
            toIdx = numChildren;
            for (int i = 0;i < toIdx;i++) {
                if (childrenNodes[i].getKeys()[0] < divideKey) continue;
                newNode.addChildNode(childrenNodes[i]);
                childrenNodes[i].setParentNode(newNode);
                childrenNodes[i] = null;
                numChildren--;
            }
        }
        return newNode;
    }

    /**
     * 노드에 키를 추가하는 함수.
     *
     * @param key 추가 할 키 값
     * @return 추가하고 난 뒤 키의 개수가 최대보다 많다면 true, 아니라면 false 리턴.
     */
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

    /**
     * 자식노드를 추가하는 함수.
     *
     * @param child 추가 할 노드
     */
    public void addChildNode(Node child) {
        childrenNodes[numChildren] = child;
        numChildren++;
        Arrays.sort(childrenNodes, nodeSorter);
    }

    /**
     * 키를 삭제하는 함수.
     *
     * @param targetIdx 삭제 할 키의 위치
     */
    public void deleteKey(int targetIdx) {
        int i = targetIdx;
        for (;i < numKeys;i++) {
            keys[i] = keys[i + 1];
        }
        numKeys--;
    }

    /**
     * 자식노드를 삭제하는 함수
     *
     * @param targetIdx 삭제 할 자식노드의 위치
     */
    public void deleteChildNode(int targetIdx) {
        int i = targetIdx;
        for (; i < numChildren; i++) {
            childrenNodes[i] = childrenNodes[i + 1];
        }
        numChildren--;
    }
}
