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

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getParentNode() {
        return parentNode;
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
        Node newNode = new Node(order); // 오른쪽 노드
        int divideIdx = (total - 1) / 2; // 노드를 나눌 기준 인덱스
        int divideKey = keys[divideIdx]; // 기준점에 있는 키
        keys[divideIdx] = NODE_DATA_NULL; // 기준점 키를 null로 만들어줌
        int toIdx = numKeys; // 오른쪽 노드로 넘길 키의 마지막 인덱스
        for (int i = divideIdx + 1;i < toIdx;i++) {
            newNode.addKey(keys[i]); // 새로운 노드에 왼쪽 노드에서 삭제하는 키 추가
            keys[i] = NODE_DATA_NULL; // 오른쪽 노드에 추가한 키는 null로 만들어줌
            numKeys--; // 키 개수 -1
        }
        numKeys--; // 기준점 키를 null로 만들었으므로 한번 더 -1
        if (numChildren == total + 1) { // 자식 노드가 존재한다면
            toIdx = numChildren; // 오른쪽 노드로 넘길 자식 노드의 마지막 인덱스
            for (int i = 0;i < toIdx;i++) {
                // 자식 노드의 첫 키가 기준 키보다 작다면 continue
                if (childrenNodes[i].getKeys()[0] < divideKey) continue;
                // 오른쪽 노드에 자식 노드 추가
                newNode.addChildNode(childrenNodes[i]);
                // 오른쪽 노드에 추가한 자식 노드의 부모 노드를 새로운 노드로 지정
                childrenNodes[i].setParentNode(newNode);
                // 오른쪽 노드에 추가한 노드는 왼쪽 노드에서는 지움
                childrenNodes[i] = null;
                numChildren--; // 자식 노드 개수 -1
            }
        }
        return newNode; // 오른쪽 노드 리턴
    }

    /**
     * 노드에 키를 추가하는 함수.
     *
     * @param key 추가 할 키 값
     * @return 추가하고 난 뒤 키의 개수가 최대보다 많다면 true, 아니라면 false 리턴.
     */
    public boolean addKey(int key) {
        // 이미 추가할 키가 존재 하는지 검사
        for (int i = 0;i < numKeys;i++) {
            if (keys[i] == key) {
                // 이미 존재한다면 함수 종료
                System.out.printf("이미 %d가 존재합니다.\n", key);
                return false;
            }
        }
        keys[numKeys] = key; // 키 추가
        Arrays.sort(keys); // 키를 오름차순으로 정렬
        numKeys++; // 키 개수 +1

        if (numKeys == order) {
            // 노드가 가지고 있는 키의 개수가 최대보다 많다면
            // true 리턴
            return true;
        } else {
            // 아니면 false 리턴
            return false;
        }
    }

    /**
     * 자식노드를 추가하는 함수.
     *
     * @param child 추가 할 노드
     */
    public void addChildNode(Node child) {
        childrenNodes[numChildren] = child; // 자식 노드 추가
        numChildren++; // 자식 노드 개수 +1
        // 자식 노드들을 자식 노드의 첫 키 값을 기준으로 오름차순으로 정렬
        Arrays.sort(childrenNodes, nodeSorter);
    }

    /**
     * 키를 삭제하는 함수.
     *
     * @param targetIdx 삭제 할 키의 위치
     */
    public void deleteKey(int targetIdx) {
        int i = targetIdx; // i의 시작값을 대상 위치로 지정
        for (;i < numKeys;i++) {
            // 키 값을 앞으로 한 칸씩 옮김
            keys[i] = keys[i + 1];
        }
        numKeys--; // 키 개수 -1
    }

    /**
     * 자식노드를 삭제하는 함수
     *
     * @param targetIdx 삭제 할 자식노드의 위치
     */
    public void deleteChildNode(int targetIdx) {
        int i = targetIdx; // i의 시작값을 대상 위치로 지정
        for (; i < numChildren; i++) {
            // 자식 노드를 앞으로 한 칸씩 옮김
            childrenNodes[i] = childrenNodes[i + 1];
        }
        numChildren--; // 자식 노드 개수 -1
    }
}
