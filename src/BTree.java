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

    private Object[] findNode(int target) {
        Node currentNode = root;
        while (currentNode.getChildrenNum() != 0) {
            boolean find = false;
            for (int i = 0; i < currentNode.getKeysNum(); i++) {
                if (currentNode.getKey(i) > target) {
                    find = true;
                    currentNode = currentNode.getChildNode(i);
                    break;
                } else if (currentNode.getKey(i) == target) {
                    return new Object[] {KEY_EXIST, currentNode, i};
                }
            }
            if (!find) {
                currentNode = currentNode.getChildNode(currentNode.getKeysNum());
            }
        }
        for (int i = 0;i < currentNode.getKeysNum();i++) {
            if (currentNode.getKey(i) == target) {
                return new Object[] {KEY_EXIST, currentNode, i};
            }
        }
        return new Object[] {KEY_NOT_EXIST, currentNode};
    }

    public void insertNode(int newData) {
        if (root == null) {
            root = new Node(order);
            root.addKey(newData);
        } else {
            Object[] result =  findNode(newData);
            Node currentNode = null;
            if ((boolean)result[0] == KEY_NOT_EXIST) {
                currentNode = (Node)result[1];
            } else {
                System.out.printf("이미 %d가 존재합니다.\n", newData);
                return;
            }
            if (currentNode.addKey(newData)) {
                while (currentNode.getKeysNum() >= order) {
                    int divideKey = currentNode.getKey((order - 1) / 2);
                    Node parentNode = currentNode.getParentNode();
                    if (parentNode != null) {
                        parentNode.addKey(divideKey);
                    } else {
                        parentNode = new Node(order);
                        parentNode.addKey(divideKey);
                        parentNode.addChildNode(currentNode);
                        currentNode.setParentNode(parentNode);
                    }
                    Node newNode = currentNode.divideNode(order);
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

    private int findChildrenPosition(Node targetNode) {
        Node parent = targetNode.getParentNode();
        for (int idx = 0; idx < parent.getChildrenNum(); idx++) {
            if (parent.getChildNode(idx) == targetNode) {
                return idx;
            }
        }
        return -1;
    }

    private Node getSiblingNode(Node currNode, int idx) {
        return currNode.getParentNode().getChildNode(idx);
    }

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
