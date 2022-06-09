import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class Visualization {
    JFrame frame = null;

    public void setInformation(DefaultMutableTreeNode root) {
        frame = new JFrame();
        frame.setTitle("B-tree 시각화");
        frame.setSize(200, 500);

        JTree jt = new JTree(root);
        JScrollPane jScrollPane = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(jScrollPane);
        frame.setSize(200,500);
    }

    public void show(boolean autoExit) {
        if (autoExit) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frame.setVisible(true);
    }
}
