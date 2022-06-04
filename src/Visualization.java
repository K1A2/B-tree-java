import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class Visualization {
    JFrame frame = null;

    public Visualization() {
        frame = new JFrame();
        frame.setTitle("B-tree 시각화");
        frame.setSize(200, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void setInformation(DefaultMutableTreeNode root) {
        JTree jt = new JTree(root);
        JScrollPane jScrollPane = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(jScrollPane);
        frame.setSize(200,500);
    }

    public void show() {
        frame.setVisible(true);
    }
}
