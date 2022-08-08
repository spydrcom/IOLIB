
package net.myorb.gui.tests;

import net.myorb.gui.StyleManager;

import javax.swing.JFrame;  
import javax.swing.JTextPane;  
import javax.swing.JScrollPane;  

import javax.swing.text.BadLocationException;  
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;  
import javax.swing.text.Document;  
import javax.swing.text.Style;

import java.awt.BorderLayout;  
import java.awt.Container;  
import java.awt.Color;  

public class TextPaneTest
{  

	public static void main(String args[]) throws BadLocationException
	{  
		StyleManager styles = new StyleManager ();
		styles.readScript ("cfg/gui/SnipStyles.xml");
		Style libStyle = styles.getStyle ("UnknownID");

		JFrame frame = new JFrame("JTextPane Example");  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        Container cp = frame.getContentPane();  
        JTextPane pane = new JTextPane();  

        SimpleAttributeSet attributeSet = new SimpleAttributeSet();  
        StyleConstants.setBold(attributeSet, true);  
  
        // Set the attributes before adding text  
        pane.setCharacterAttributes(attributeSet, true);  
        pane.setText("Welcome");  
  
        attributeSet = new SimpleAttributeSet();  
        StyleConstants.setItalic(attributeSet, true);  
        StyleConstants.setForeground(attributeSet, Color.red);  
        StyleConstants.setBackground(attributeSet, Color.blue);  
        //StyleConstants.setUnderline(attributeSet, true);

        String txt = "To Java ";
        Document doc = pane.getStyledDocument();  

        int pos = doc.getLength();
        doc.insertString(pos, txt, attributeSet);  
  
        //attributeSet = new SimpleAttributeSet();  
        doc.insertString(doc.getLength(), "World", attributeSet);  
  
        JScrollPane scrollPane = new JScrollPane(pane);  
        cp.add(scrollPane, BorderLayout.CENTER);  

        doc.remove(pos, txt.length());
        doc.insertString(pos, txt, libStyle);  

        frame.setSize(400, 300);  
        frame.setVisible(true);  
      }  
}  