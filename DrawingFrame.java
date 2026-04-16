import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DrawingFrame extends JFrame
{
    //abt main canvas
    private DrawingPanel drawingPanel;
    //abt selected color
    private JButton selectedColorButton=null;
    //abt selected tool highlight
    private JButton lastHighlightedBtn=null;
    public DrawingFrame()
    {
        setTitle("Paint Brush");
        setSize(900,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //abt init panel
        drawingPanel=new DrawingPanel();
        //abt toolbar
        JPanel toolbar=buildToolbar();
        add(toolbar,BorderLayout.NORTH);
        add(drawingPanel,BorderLayout.CENTER);
        setVisible(true);
    }

    //abt build toolbar
    private JPanel buildToolbar()
    {
        JPanel toolbar=new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.LEFT,5,5));
        toolbar.setBackground(new Color(45,45,48));
        //abt colors section
        toolbar.add(makeSectionLabel("Colors:"));
        JButton redBtn=makeColorButton(Color.RED,"Red");
        JButton greenBtn=makeColorButton(Color.GREEN,"Green");
        JButton blueBtn=makeColorButton(Color.BLUE,"Blue");
        JButton blackBtn=makeColorButton(Color.BLACK,"Black");
        JButton yellowBtn=makeColorButton(Color.YELLOW,"Yellow");
        JButton orangeBtn=makeColorButton(new Color(255,140,0),"Orange");
        //toolbar
        toolbar.add(redBtn);
        toolbar.add(greenBtn);
        toolbar.add(blueBtn);
        toolbar.add(blackBtn);
        toolbar.add(yellowBtn);
        toolbar.add(orangeBtn);
        //abt custom color
        JButton customColorBtn=makeToolButton("Custom");
        customColorBtn.addActionListener(e->
        {
            Color chosen=JColorChooser.showDialog(this,"Pick a Color",Color.BLACK);
            if(chosen!=null)
            {
                drawingPanel.setCurrentColor(chosen);
                highlightButton(customColorBtn);
            }
        });
        toolbar.add(customColorBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        //abt shapes section
        toolbar.add(makeSectionLabel("Shapes:"));
        JButton rectBtn=makeToolButton("Rect");
        JButton ovalBtn=makeToolButton("Oval");
        JButton lineBtn=makeToolButton("Line");
        rectBtn.addActionListener(e->
        {
            drawingPanel.setCurrentTool(DrawingPanel.Tool.RECTANGLE);
            highlightButton(rectBtn);
        });
        ovalBtn.addActionListener(e->
        {
            drawingPanel.setCurrentTool(DrawingPanel.Tool.OVAL);
            highlightButton(ovalBtn);
        });
        lineBtn.addActionListener(e->
        {
            drawingPanel.setCurrentTool(DrawingPanel.Tool.LINE);
            highlightButton(lineBtn);
        });
        toolbar.add(rectBtn);
        toolbar.add(ovalBtn);
        toolbar.add(lineBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        //abt free draw
        JButton freeHandBtn=makeToolButton("Free");
        freeHandBtn.addActionListener(e->
        {
            drawingPanel.setCurrentTool(DrawingPanel.Tool.FREE_HAND);
            highlightButton(freeHandBtn);
        });
        toolbar.add(freeHandBtn);
        //abt eraser
        JButton eraserBtn=makeToolButton("x Erase");
        eraserBtn.addActionListener(e->
        {drawingPanel.setCurrentTool(DrawingPanel.Tool.ERASER);
            highlightButton(eraserBtn);
        });
        toolbar.add(eraserBtn);
        //abt clear all
        JButton clearBtn=makeToolButton("Clear");
        clearBtn.setBackground(new Color(180,50,50));
        clearBtn.addActionListener(e->
        {
            int confirm=JOptionPane.showConfirmDialog(this,"Are you sure you want to clear all drawings?","Clear All",JOptionPane.YES_NO_OPTION);
            if(confirm==JOptionPane.YES_OPTION)
            {
                drawingPanel.clearAll();
            }
        });
        toolbar.add(clearBtn);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        //abt dotted
        JCheckBox dottedBox=new JCheckBox("Dotted");
        dottedBox.setForeground(Color.WHITE);
        dottedBox.setBackground(new Color(45,45,48));
        dottedBox.addItemListener(e->drawingPanel.setDotted(dottedBox.isSelected()));
        toolbar.add(dottedBox);
        //abt filled
        JCheckBox filledBox=new JCheckBox("Filled");
        filledBox.setForeground(Color.WHITE);
        filledBox.setBackground(new Color(45,45,48));
        filledBox.addItemListener(e->drawingPanel.setFilled(filledBox.isSelected()));
        toolbar.add(filledBox);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        //abt stroke size
        toolbar.add(makeSectionLabel("Size:"));
        //spiner
        JSpinner strokeSpinner=new JSpinner(new SpinnerNumberModel(2,1,20,1));
        strokeSpinner.setMaximumSize(new Dimension(55,25));
        strokeSpinner.setPreferredSize(new Dimension(55,25));
        strokeSpinner.addChangeListener(e->
        {
            int val=(int)strokeSpinner.getValue();
            drawingPanel.setStrokeWidth((float)val);
        });
        toolbar.add(strokeSpinner);
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        //abt undo
        JButton undoBtn=makeToolButton("Undo");
        undoBtn.addActionListener(e->drawingPanel.undo());
        toolbar.add(undoBtn);
        //abt save
        JButton saveBtn=makeToolButton("Save");
        saveBtn.addActionListener(e->saveImage());
        toolbar.add(saveBtn);
        //abt open
        JButton openBtn=makeToolButton("Open");
        openBtn.addActionListener(e->openImage());
        toolbar.add(openBtn);
        return toolbar;
    }
    //abt color button
    private JButton makeColorButton(Color color,String tooltip)
    {
        JButton btn=new JButton();
        btn.setBackground(color);
        btn.setPreferredSize(new Dimension(28,28));
        btn.setBorderPainted(true);
        btn.setToolTipText(tooltip);
        btn.setOpaque(true);
        btn.addActionListener(e->
        {
            drawingPanel.setCurrentColor(color);
            highlightButton(btn);
            selectedColorButton=btn;
        });
        return btn;
    }

    //abt tool button
    private JButton makeToolButton(String text)
    {
        JButton btn=new JButton(text);
        btn.setBackground(new Color(70,70,73));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif",Font.PLAIN,12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter()
        {
            @Override public void mouseEntered(MouseEvent e)
            {
                if(!btn.getBackground().equals(new Color(100,149,237)))
                {
                    btn.setBackground(new Color(90,90,93));
                }
            }
            @Override public void mouseExited(MouseEvent e)
            {
                if(!btn.getBackground().equals(new Color(100,149,237)))
                {
                    btn.setBackground(new Color(70,70,73));
                }
            }
        });
        return btn;
    }

    //abt label
    private JLabel makeSectionLabel(String text)
    {
        JLabel lbl=new JLabel(text);
        lbl.setForeground(new Color(180,180,180));
        lbl.setFont(new Font("SansSerif",Font.BOLD,11));
        return lbl;
    }
    //abt highlight button
    private void highlightButton(JButton btn)
    {
        if(lastHighlightedBtn!=null)
        {
            lastHighlightedBtn.setBackground(new Color(70,70,73));
        }
        btn.setBackground(new Color(100,149,237));
        lastHighlightedBtn=btn;
    }
    //abt save image
    private void saveImage()
    {
        JFileChooser chooser=new JFileChooser();
        chooser.setDialogTitle("Save Drawing");
        chooser.setFileFilter(new FileNameExtensionFilter("PNG Images (*.png)","png"));
        chooser.setSelectedFile(new File("drawing.png"));

        int result=chooser.showSaveDialog(this);
        if(result==JFileChooser.APPROVE_OPTION)
        {
            File file=chooser.getSelectedFile();
            if(!file.getName().toLowerCase().endsWith(".png"))
            {
                file=new File(file.getAbsolutePath()+".png");
            }
            try
            {
                BufferedImage img=drawingPanel.getImage();
                ImageIO.write(img,"PNG",file);
                JOptionPane.showMessageDialog(this,"Saved to: "+file.getAbsolutePath());
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(this,"Error saving: "+ex.getMessage(),"Save Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //abt open image
    private void openImage()
    {
        JFileChooser chooser=new JFileChooser();
        chooser.setDialogTitle("Open Image");
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files","png","jpg","jpeg","bmp","gif"));
        int result=chooser.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION)
        {
            File file=chooser.getSelectedFile();
            try
            {
                BufferedImage img=ImageIO.read(file);
                if(img!=null)
                {
                    drawingPanel.loadImage(img);
                }
                else
                {
                    JOptionPane.showMessageDialog(this,"Could not read image file.","Open Error",JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(IOException ex)
            {
                JOptionPane.showMessageDialog(this,"Error opening: "+ex.getMessage(),"Open Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}