import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Visualization {
    JFrame frame = null;
    Object lock = new Object();

    public void setInformation(DefaultMutableTreeNode root) {
        frame = new JFrame();
        frame.setTitle("B-tree 시각화");
        frame.setSize(200, 500);

        JTree jt = new JTree(root);
        JScrollPane jScrollPane = new JScrollPane(jt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.add(jScrollPane);
        frame.setSize(200,500);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width - 200) / 2, (screenSize.height - 500) / 2);

        for (int i = 0;i < jt.getRowCount();i++) {
            jt.expandRow(i);
        }
    }

    public void show(boolean autoExit) {
        if (autoExit) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();

        Thread t = new Thread() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (frame.isVisible()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        t.start();

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (lock) {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
    }
}
