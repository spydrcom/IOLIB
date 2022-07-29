package net.myorb.gui.tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

/** http://stackoverflow.com/questions/8523445 */
@SuppressWarnings("serial")
public class StyledEditorTest extends JFrame {

    public StyledEditorTest() {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/HTML");
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setText("<hr>Welcome to <b>StackOverFlow!</b><hr>");
        JToolBar bar = new JToolBar();
        bar.add(new StyledEditorKit.ForegroundAction("Red", Color.red));
        bar.add(new StyledEditorKit.ForegroundAction("Blue", Color.blue));
        bar.add(new StyledEditorKit.FontSizeAction("12", 12));
        bar.add(new StyledEditorKit.FontSizeAction("14", 14));
        bar.add(new StyledEditorKit.FontSizeAction("16", 16));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.add(bar, BorderLayout.NORTH);
        this.add(editorPane, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new StyledEditorTest();
            }
        });
    }
}