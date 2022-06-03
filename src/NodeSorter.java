import java.util.Comparator;

public class NodeSorter implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        if (o1 == null || o2 == null || o1.getKeys()[0] == o2.getKeys()[0]) {
            return 0;
        } else if (o1.getKeys()[0] < o2.getKeys()[0]) {
            return -1;
        } else {
            return 1;
        }
    }
}
