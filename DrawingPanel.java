import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DrawingPanel extends JPanel implements MouseListener,MouseMotionListener
{
    //abt all final shapes
    private List<Shape>shapes=new ArrayList<>();

    //abt shape while drawing
    private Shape currentShape=null;

    //abt undo history
    private Stack<List<Shape>>undoStack=new Stack<>();

    public enum Tool{RECTANGLE,OVAL,LINE,FREE_HAND,ERASER}

    //abt tool + style settings
    private Tool currentTool=Tool.FREE_HAND;
    private Color currentColor=Color.BLACK;
    private float strokeWidth=2f;
    private boolean filled=false;
    private boolean dotted=false;

    public DrawingPanel()
    {
        setBackground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d=(Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        //abt draw all shapes
        for(Shape shape:shapes)
        {
            shape.draw(g2d);
        }

        //abt preview shape
        if(currentShape!=null)
        {
            currentShape.draw(g2d);
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int x=e.getX();
        int y=e.getY();

        //abt save state for undo
        saveStateForUndo();

        switch(currentTool)
        {
            case RECTANGLE:
                currentShape=new RectangleShape(x,y,x,y,currentColor,strokeWidth,filled,dotted);
                break;

            case OVAL:
                currentShape=new OvalShape(x,y,x,y,currentColor,strokeWidth,filled,dotted);
                break;

            case LINE:
                currentShape=new LineShape(x,y,x,y,currentColor,strokeWidth,dotted);
                break;

            case FREE_HAND:
                FreeHandShape fh=new FreeHandShape(currentColor,strokeWidth);
                fh.addPoint(x,y);
                currentShape=fh;
                break;

            case ERASER:
                EraserShape eraser=new EraserShape();
                eraser.addPoint(x,y);
                currentShape=eraser;
                break;
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if(currentShape==null)
        {
            return;
        }

        int x=e.getX();
        int y=e.getY();

        switch(currentTool)
        {
            case RECTANGLE:
                ((RectangleShape)currentShape).setEndPoint(x,y);
                break;

            case OVAL:
                ((OvalShape)currentShape).setEndPoint(x,y);
                break;

            case LINE:
                ((LineShape)currentShape).setEndPoint(x,y);
                break;

            case FREE_HAND:
                ((FreeHandShape)currentShape).addPoint(x,y);
                break;

            case ERASER:
                ((EraserShape)currentShape).addPoint(x,y);
                break;
        }

        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(currentShape==null)
        {
            return;
        }

        shapes.add(currentShape);
        currentShape=null;
        repaint();
    }

    @Override public void mouseClicked(MouseEvent e){}
    @Override public void mouseEntered(MouseEvent e){}
    @Override public void mouseExited(MouseEvent e){}
    @Override public void mouseMoved(MouseEvent e){}

    //abt save state for undo
    private void saveStateForUndo()
    {
        undoStack.push(new ArrayList<>(shapes));
    }

    //abt undo last action
    public void undo()
    {
        if(!undoStack.isEmpty())
        {
            shapes=undoStack.pop();
            currentShape=null;
            repaint();
        }
    }

    //abt clear all shapes
    public void clearAll()
    {
        saveStateForUndo();
        shapes.clear();
        currentShape=null;
        repaint();
    }

    //abt export image
    public BufferedImage getImage()
    {
        BufferedImage img=new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d=img.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,getWidth(),getHeight());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        for(Shape shape:shapes)
        {
            shape.draw(g2d);
        }

        g2d.dispose();
        return img;
    }

    //abt load image as shape
    public void loadImage(java.awt.Image image)
    {
        saveStateForUndo();
        shapes.clear();

        shapes.add(new Shape(Color.BLACK,1f,false,false)
        {
            @Override
            public void draw(Graphics2D g2d)
            {
                g2d.drawImage(image,0,0,getWidth(),getHeight(),null);
            }
        });

        repaint();
    }

    //abt setters
    public void setCurrentTool(Tool tool)
    {
        this.currentTool=tool;
    }

    public void setCurrentColor(Color color)
    {
        this.currentColor=color;
    }

    public void setStrokeWidth(float width)
    {
        this.strokeWidth=width;
    }

    public void setFilled(boolean filled)
    {
        this.filled=filled;
    }

    public void setDotted(boolean dotted)
    {
        this.dotted=dotted;
    }
}