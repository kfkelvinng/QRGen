package net.glxn.qrgen.javase;
import java.awt.*;

class AWTPanel extends Frame
{
Panel panel;
Button b1,b2;
    public AWTPanel()
    {
    
    // Set frame properties
    setTitle("AWT Panel"); // Set the title
    setSize(400,400); // Set size to the frame
    setLayout(new FlowLayout()); // Set the layout
    setVisible(true); // Make the frame visible
    setLocationRelativeTo(null);  // Center the frame
    
    // Create the panel
    panel=new Panel();
    
    // Set panel background
    panel.setBackground(Color.gray);
    
    // Create buttons
    b1=new Button(); // Create a button with default constructor
    b1.setLabel("I am button 1"); // Set the text for button
    
    b2=new Button("Button 2"); // Create a button with sample text
    b2.setBackground(Color.lightGray); // Set the background to the button
    
    // Add the buttons to the panel
    panel.add(b1);
    panel.add(b2);
    
    // Add the panel to the frame
    add(panel);
    
    }
    public static void main(String args[])
    {
    new AWTPanel();
    }
}