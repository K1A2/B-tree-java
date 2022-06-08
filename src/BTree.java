import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

public class BTree {
    private static final boolean KEY_EXIST = true;
    private static final boolean KEY_NOT_EXIST = false;
    private static final int NODE_DATA_NULL = Integer.MAX_VALUE;
    private static final int SKIP_DELETE_KEY = -1;

    private Node root = null;
    private int order = 0;

    public BTree(int order) {
        if (order < 3) {
            this.order = 3;
        } else {
            this.order = order;
        }
    }

    /**
     * 키를 탐색하는 함수.
     *
     * @param target 대상 값
     * @return 존재한다면 노드와 위치를 리턴, 존재하지 않는다면 키가 추가될 수 있는 노드를 리턴.
     */
    private Object[] findNode(int target) {
        Node currentNode = root; // 현재 탐색중인 노드. 처음은 root
        // currentNode가 리프 노드일때까지 반복
        while (currentNode.getChildrenNum() != 0) {
            // 값을 찾거나 값이 있을 부분을 찾았다면 true, 아니면 false
            boolean find = false;
            // currentNode의 키를 하나씩 가져와 검사
            for (int i = 0; i < currentNode.getKeysNum(); i++) {
                // currentNode의 i번째 키가 대상 키보다 크다면
                // 그 위치의 자식 노드가 키가 존재할 수 있는 노드
                if (currentNode.getKey(i) > target) {
                    find = true; // 찾았다 = true
                    // 다음 자식 노드를 가져옴
                    currentNode = currentNode.getChildNode(i);
                    break;
                } else if (currentNode.getKey(i) == target) {
                    // 대상 키와 완전히 같은 키를 찾았다면
                    // 키가 존재한다는 값, 키가 존재하는 노드,
                    // keys의 몇 번째가 대상 키인지 리턴
                    return new Object[] {KEY_EXIST, currentNode, i};
                }
            }
            if (!find) {
                // 반복문 안에서 키 값이 존재할 다음 자식 노드를 찾지 못했다면
                // currentNode의 가장 마지막 자식 노드를 currentNode로 지정
                currentNode = currentNode.getChildNode(currentNode.getKeysNum());
            }
        }
        // 최종적으로 지정된 리프 노드에 이미 키 값이 존재하는지 검사
        for (int i = 0;i < currentNode.getKeysNum();i++) {
            if (currentNode.getKey(i) == target) {
                // 대상 키와 완전히 같은 키를 찾았다면
                // 키가 존재한다는 값, 키가 존재하는 노드,
                // keys의 몇 번째가 대상 키인지 리턴
                return new Object[] {KEY_EXIST, currentNode, i};
            }
        }
        // 대상 키와 완전히 같은 키를 찾지 못했다면
        // 키가 존재하지 않는다는 값, 키가 존재할 수 있는 노드 리턴
        return new Object[] {KEY_NOT_EXIST, currentNode};
    }

    /**
     * B-tree에 키를 추가하는 함수.
     *
     * @param newData 추가 할 값
     */
    public void insertNode(int newData) {
        if (root == null) { // B-tree가 비었다면
            // 루트 노드를 만들고, 루트 노드에 키 추가
            root = new Node(order);
            root.addKey(newData);
        } else {
            // findNode함수를 이용해 키 값이 이미 존재하는지 검사
            Object[] result =  findNode(newData);
            Node currentNode = null;
            if ((boolean)result[0] == KEY_NOT_EXIST) {
                // 존재하지 않는다면
                // 키 값을 추가할 수 있는 노드를 currentNode로 지정
                currentNode = (Node)result[1];
            } else {
                // 이미 존재한다면
                // 키 중복을 방지하기 위해 함수 종료
                System.out.printf("이미 %d가 존재합니다.\n", newData);
                return;
            }
            // 노드에 키 추가
            if (currentNode.addKey(newData)) {
                // 노드가 가지는 키 개수가 최대 개수보다 많다면
                while (currentNode.getKeysNum() >= order) {
                    // keys의 중간 값을 구하고 divideKey에 저장
                    int divideKey = currentNode.getKey((order - 1) / 2);
                    // 현재 노드의 부모 노드
                    Node parentNode = currentNode.getParentNode();
                    if (parentNode != null) {
                        // 부모 노드가 null이 아니라면
                        // = 현재 노드가 root 노드가 존재한다면
                        // 부모 노드에 현재 노드의 중간 키 추가
                        parentNode.addKey(divideKey);
                    } else {
                        // 부모 노드가 null 이라면,
                        // = 현재 노드가 root 노드가 존재하지 않는다면
                        // root 노드를 만들고 B-tree 레벨 증가
                        parentNode = new Node(order);
                        // 새로 만든 루트 노드에 현재 노드의 중간 키 값 추가
                        parentNode.addKey(divideKey);
                        // 새로 만든 루트 노드에 자식 노드로 현재 노드 추가
                        parentNode.addChildNode(currentNode);
                        // 현재 노드의 부모 노드를 새로 만든 부모 노드로 지정
                        currentNode.setParentNode(parentNode);
                    }
                    // divideNode 함수를 이용해 노드를 분할
                    Node rightNode = currentNode.divideNode(order);
                    // 새로 만든 오른쪽 노드의 부모 노드 지정
                    rightNode.setParentNode(parentNode);
                    // 오른쪽 노드를 부모 노드의 자식 노드로 지정
                    parentNode.addChildNode(rightNode);
                    if (currentNode == root) {
                        // 현재 노드가 root 노드와 같다면
                        //현재 노드의 부모 노드를 root 노드로 지정
                        root = parentNode;
                    }
                    // cuttentNode를 부모 노드로 지정
                    currentNode = parentNode;
                }
            }
        }
    }

    /**
     * 대상 노드가 부모노드의 몇 번째 자식인지 검색하는 함수.
     *
     * @param targetNode 대상 노드
     * @return 부모 노드에서의 위치 리턴. 존재하지 않는다면 -1 리턴.
     */
    private int findChildrenPosition(Node targetNode) {
        Node parent = targetNode.getParentNode();
        for (int idx = 0; idx < parent.getChildrenNum(); idx++) {
            if (parent.getChildNode(idx) == targetNode) {
                return idx;
            }
        }
        return -1;
    }

    /**
     * 대상 노드의 형제 노드를 가져오는 함수.
     *
     * @param currNode 대상 노드
     * @param idx 형제 노드의 위치
     * @return 형제 노드 리턴.
     */
    private Node getSiblingNode(Node currNode, int idx) {
        return currNode.getParentNode().getChildNode(idx);
    }

    /**
     * 두 노드를 병합하는 함수.
     *
     * @param node1 대상 노드 1
     * @param node2 대상 노드 2
     * @return 병합된 노드 리턴.
     */
    private Node mergeNode(Node node1, Node node2) {
        Node newNode = new Node(order);
        for (int i : node1.getKeys()) if (i != NODE_DATA_NULL) newNode.addKey(i);
        for (int i : node2.getKeys()) if (i != NODE_DATA_NULL) newNode.addKey(i);
        for (Node i : node1.getChildrenNodes()) if (i != null) {
            newNode.addChildNode(i);
            i.setParentNode(newNode);
        }
        for (Node i : node2.getChildrenNodes()) if (i != null) {
            newNode.addChildNode(i);
            i.setParentNode(newNode);
        }
        return newNode;
    }

    /**
     * 삭제 할 값이 리프 노드에 있을때 삭제를 처리하는 함수.
     *
     * @param currentNode 삭제할 키가 존재하는 노드
     * @param targetIdx 노드에서 삭제할 키의 번호
     * @param leastKeyNum 노드가 가져야 할 최소 키 개수 + 1
     */
    private void deleteLeafNodeKey(Node currentNode, int targetIdx, int leastKeyNum) {
        if (currentNode == root) {
            currentNode.deleteKey(targetIdx);
            if (currentNode.getKeysNum() == 0) root = null;
        } else {
            int currNodePos = findChildrenPosition(currentNode);
            Node parentNode = currentNode.getParentNode();

            if (currentNode.getKeysNum() >= leastKeyNum) {
                currentNode.deleteKey(targetIdx);
            } else if (currNodePos > 0 && getSiblingNode(currentNode, currNodePos - 1).getKeysNum() >= leastKeyNum) {
                Node leftNode = getSiblingNode(currentNode, currNodePos - 1);

                int maxKey = leftNode.getKey(leftNode.getKeysNum() - 1);
                leftNode.deleteKey(leftNode.getKeysNum() - 1);

                currentNode.deleteKey(targetIdx);

                currentNode.addKey(parentNode.getKey(currNodePos - 1));
                parentNode.deleteKey(currNodePos - 1);
                parentNode.addKey(maxKey);
            } else if (currNodePos < parentNode.getChildrenNum() - 1 && getSiblingNode(currentNode, currNodePos + 1).getKeysNum() >= leastKeyNum) {
                Node rightNode = getSiblingNode(currentNode, currNodePos + 1);

                int minKey = rightNode.getKey(0);
                rightNode.deleteKey(0);

                currentNode.deleteKey(targetIdx);

                currentNode.addKey(parentNode.getKey(currNodePos));
                parentNode.deleteKey(currNodePos);
                parentNode.addKey(minKey);
            } else if (parentNode.getKeysNum() >= leastKeyNum) {
                Node siblingNode = null;
                int parentTargetKeyIdx = 0;
                if (currNodePos == 0) {
                    siblingNode = getSiblingNode(currentNode, 1);
                } else {
                    siblingNode = getSiblingNode(currentNode, currNodePos - 1);
                    parentTargetKeyIdx = currNodePos - 1;
                }
                siblingNode.addKey(parentNode.getKey(parentTargetKeyIdx));
                parentNode.deleteKey(parentTargetKeyIdx);

                currentNode.deleteKey(targetIdx);
                Node mergedNode = mergeNode(siblingNode, currentNode);

                parentNode.deleteChildNode(currNodePos);
                parentNode.deleteChildNode(findChildrenPosition(siblingNode));
                parentNode.addChildNode(mergedNode);
                mergedNode.setParentNode(parentNode);
            } else {
                currentNode.deleteKey(targetIdx);

                int parentKeyPos = currNodePos;
                int siblingNodePos = currNodePos + 1;
                Node siblingNode = parentNode.getChildNode(siblingNodePos);
                if (siblingNode == null) {
                    siblingNodePos = currNodePos - 1;
                    siblingNode = parentNode.getChildNode(siblingNodePos);
                    parentKeyPos--;
                }
                currentNode.addKey(parentNode.getKey(parentKeyPos));
                parentNode.deleteKey(parentKeyPos);

                Node mergedNode = mergeNode(currentNode, siblingNode);

                parentNode.deleteChildNode(findChildrenPosition(currentNode));
                parentNode.deleteChildNode(findChildrenPosition(siblingNode));
                parentNode.addChildNode(mergedNode);
                mergedNode.setParentNode(parentNode);

                if (parentNode == root) {
                    if (parentNode.getKeysNum() == 0) {
                        root = mergedNode;
                        mergedNode.setParentNode(null);
                    }
                } else {
                    deleteInternalNodeLeastDown(parentNode, SKIP_DELETE_KEY, leastKeyNum);
                }
            }
        }
    }

    /**
     * 내부노드면서 자식이 리프노드가 아닌 노드에서 삭제를 처리하는 함수.
     *
     * @param currentNode 대상 노드
     * @param leftChildNode 대상 노드의 왼쪽 자식 노드
     * @param leastKeyNum 노드가 가져야 할 최소 키 개수 + 1
     * @param targetIdx 삭제 할 키의 위치
     */
    private void deleteWithSwapKey(Node currentNode, Node leftChildNode, int leastKeyNum, int targetIdx) {
        int maxKey = 0;
        while (leftChildNode.getChildrenNum() != 0) {
            leftChildNode = leftChildNode.getChildNode(leftChildNode.getChildrenNum() - 1);
        }
        maxKey = leftChildNode.getKey(leftChildNode.getKeysNum() - 1);
        leftChildNode.deleteKey(leftChildNode.getKeysNum() - 1);

        leftChildNode.addKey(currentNode.getKey(targetIdx));
        currentNode.deleteKey(targetIdx);
        currentNode.addKey(maxKey);

        deleteLeafNodeKey(leftChildNode, leftChildNode.getKeysNum() - 1, leastKeyNum);
    }

    /**
     * 내부노드면서 키가 최소 개수보다 많을 때 삭제를 처리하는 함수.
     * 
     * @param currentNode 대상 노드
     * @param targetIdx 삭제 할 키의 위치
     * @param leastKeyNum 노드가 가져야 할 최소 키 개수 + 1
     */
    private void deleteInternalNodeLeastUp(Node currentNode, int targetIdx, int leastKeyNum) {
        Node leftChildNode = currentNode.getChildNode(targetIdx);
        Node rightChildNode = currentNode.getChildNode(targetIdx + 1);
        if (leftChildNode.getChildrenNum() == 0) {
            if (leftChildNode.getKeysNum() >= leastKeyNum) {
                currentNode.deleteKey(targetIdx);
                int maxKeyIdx = leftChildNode.getKeysNum() - 1;
                int maxKey = leftChildNode.getKey(maxKeyIdx);
                leftChildNode.deleteKey(maxKeyIdx);
                currentNode.addKey(maxKey);
            } else if (rightChildNode.getKeysNum() >= leastKeyNum) {
                currentNode.deleteKey(targetIdx);
                int minKeyIdx = 0;
                int minKey = rightChildNode.getKey(minKeyIdx);
                rightChildNode.deleteKey(minKeyIdx);
                currentNode.addKey(minKey);
            } else if (currentNode.getKeysNum() >= leastKeyNum) {
                currentNode.deleteKey(targetIdx);
                Node mergedNode = mergeNode(leftChildNode, rightChildNode);
                mergedNode.setParentNode(currentNode);

                currentNode.deleteChildNode(findChildrenPosition(leftChildNode));
                currentNode.deleteChildNode(findChildrenPosition(rightChildNode));
                currentNode.addChildNode(mergedNode);
            } else {
                if (currentNode == root && currentNode.getKeysNum() > 0) {
                    deleteWithSwapKey(currentNode, leftChildNode, leastKeyNum, targetIdx);
                } else {
                    deleteInternalNodeLeastDown(currentNode, targetIdx, leastKeyNum);
                }
            }
        } else {
            deleteWithSwapKey(currentNode, leftChildNode, leastKeyNum, targetIdx);
        }
    }

    /**
     * 내부노드면서 키가 최소 개수보다 적을 때 삭제를 처리하는 함수.
     *
     * @param currentNode 대상 노드
     * @param targetIdx 삭제 할 키의 위치
     * @param leastKeyNum 노드가 가져야 할 최소 키 개수 + 1
     */
    private void deleteInternalNodeLeastDown(Node currentNode, int targetIdx, int leastKeyNum) {
        Node parentNode = currentNode.getParentNode();
        if (targetIdx != SKIP_DELETE_KEY) {
            currentNode.deleteKey(targetIdx);
            Node leftChildNode = currentNode.getChildNode(targetIdx);
            Node rightChildNode = currentNode.getChildNode(targetIdx + 1);
            Node mergedNode = mergeNode(leftChildNode, rightChildNode);

            currentNode.deleteChildNode(findChildrenPosition(leftChildNode));
            currentNode.deleteChildNode(findChildrenPosition(rightChildNode));

            currentNode.addChildNode(mergedNode);
            mergedNode.setParentNode(currentNode);
        }
        if (currentNode == root) {
            root = currentNode.getChildNode(0);
            root.setParentNode(null);
            return;
        }
        int currPosition = findChildrenPosition(currentNode);
        Node siblingNode = getSiblingNode(currentNode, currPosition + 1);
        if (siblingNode == null) {
            currPosition--;
            siblingNode = getSiblingNode(currentNode, currPosition);
        }

        siblingNode.addKey(parentNode.getKey(currPosition));
        parentNode.deleteKey(currPosition);

        parentNode.deleteChildNode(findChildrenPosition(currentNode));
        parentNode.deleteChildNode(findChildrenPosition(siblingNode));

        Node mergedNode = mergeNode(currentNode, siblingNode);
        parentNode.addChildNode(mergedNode);
        mergedNode.setParentNode(parentNode);

        if (mergedNode.getKeysNum() >= order) {
            parentNode.deleteChildNode(findChildrenPosition(mergedNode));

            int divideKey = mergedNode.getKey((mergedNode.getKeysNum() - 1) / 2);
            Node newRightNode = mergedNode.divideNode(mergedNode.getKeysNum());

            parentNode.addKey(divideKey);
            parentNode.addChildNode(mergedNode);
            parentNode.addChildNode(newRightNode);
            newRightNode.setParentNode(parentNode);
        } else if (parentNode != root && parentNode.getKeysNum() < leastKeyNum - 1) {
            deleteInternalNodeLeastDown(parentNode, SKIP_DELETE_KEY, leastKeyNum);
        } else if (parentNode == root && parentNode.getKeysNum() == 0) {
            root = mergedNode;
            mergedNode.setParentNode(null);
        }
    }

    /**
     * B-tree에서 대상 키 값을 삭제하는 함수
     *
     * @param target 삭제 할 키 값
     */
    public void deleteNode(int target) {
        if (root != null) {
            Object[] result =  findNode(target);
            if ((boolean)result[0] == KEY_EXIST) {
                int targetIdx = (int)result[2];
                Node currentNode = (Node)result[1];
                int leastKeyNum = (int) Math.ceil(order / 2.0);
                if (currentNode.getChildrenNum() == 0) {
                    deleteLeafNodeKey(currentNode, targetIdx, leastKeyNum);
                } else {
                    deleteInternalNodeLeastUp(currentNode, targetIdx, leastKeyNum);
                }
            } else {
                System.out.printf("%d를 찾을 수 없습니다.\n", target);
            }
        } else {
            System.out.println("B-tree가 비었습니다.");
        }
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
                System.out.printf("부모: %d [ ", (level != 1 ? nowNode.getParentNode().getKey(0) : 0));
                for (int i : nowNode.getKeys()) {
                    if (i != NODE_DATA_NULL) System.out.printf("%d ", i);
                }
                System.out.printf("] -- ");
                for (Node n : nowNode.getChildrenNodes()) {
                    if (n != null) deque.add(new Object[]{n, level + 1});
                }
            }
            System.out.println("\n");
        } else {
            System.out.println("B-tree가 비었습니다.");
        }
    }

    public DefaultMutableTreeNode getTreeData() {
        if (root != null) {
            String keys = "[ ";
            for (int i : root.getKeys()) {
                if (i != NODE_DATA_NULL) keys += i + " ";
            }
            keys += "]";
            DefaultMutableTreeNode rootTreeNode = new DefaultMutableTreeNode(keys);

            HashMap<Integer, DefaultMutableTreeNode> hashMap = new HashMap<>();
            hashMap.put(root.getKey(0), rootTreeNode);

            Deque<Node> deque = new ArrayDeque<>();
            deque.add(root);

            while (!deque.isEmpty()) {
                Node nowNode = deque.pop();
                for (Node n : nowNode.getChildrenNodes()) {
                    if (n != null) {
                        keys = "[ ";
                        for (int i : n.getKeys()) {
                            if (i != NODE_DATA_NULL) keys += i + " ";
                        }
                        keys += "]";
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(keys);
                        hashMap.put(n.getKey(0), node);
                        hashMap.get(n.getParentNode().getKey(0)).add(node);
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
