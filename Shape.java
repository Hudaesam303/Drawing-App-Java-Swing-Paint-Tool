import java.awt.*;

public abstract class Shape
{
    protected Color color;
    protected float strokeWidth;
    protected boolean filled;
    protected boolean dotted;
    public Shape(Color color,float strokeWidth,boolean filled,boolean dotted)
    {
        this.color=color;
        this.strokeWidth=strokeWidth;
        this.filled=filled;
        this.dotted=dotted;
    }
    protected BasicStroke buildStroke()
    {
        if (dotted)
        {
            float[]dashPattern={6f,6f};
            return new BasicStroke(strokeWidth,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10f,dashPattern,0f);
        }
        else
        {
            return new BasicStroke(strokeWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        }
    }
    public abstract void draw(Graphics2D g2d);
}
class LineShape extends Shape
{
    private int x1,y1,x2,y2;
    public LineShape(int x1,int y1,int x2,int y2,Color color,float strokeWidth,boolean dotted)
    {super(color,strokeWidth,false,dotted);
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }
    public void setEndPoint(int x2,int y2)
    {
        this.x2=x2;
        this.y2=y2;
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        g2d.setColor(color);
        g2d.setStroke(buildStroke());
        g2d.drawLine(x1,y1,x2,y2);
    }
}
class RectangleShape extends Shape
{
    private int x1,y1,x2,y2;
    public RectangleShape(int x1,int y1,int x2,int y2,Color color,float strokeWidth,boolean filled,boolean dotted)
    {
        super(color,strokeWidth,filled,dotted);
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }
    public void setEndPoint(int x2,int y2)
    {
        this.x2=x2;
        this.y2=y2;
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        g2d.setColor(color);
        g2d.setStroke(buildStroke());
        int x=Math.min(x1,x2);
        int y=Math.min(y1,y2);
        int w=Math.abs(x2-x1);
        int h=Math.abs(y2-y1);
        if (filled)
        {
            g2d.fillRect(x,y,w,h);
        }
        else
        {
            g2d.drawRect(x,y,w,h);
        }
    }
}
//drawing ovel
class OvalShape extends Shape
{
    private int x1,y1,x2,y2;

    public OvalShape(int x1,int y1,int x2,int y2,Color color,float strokeWidth,boolean filled,boolean dotted)
    {
        super(color,strokeWidth,filled,dotted);
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }
    public void setEndPoint(int x2,int y2)
    {
        this.x2=x2;
        this.y2=y2;
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        g2d.setColor(color);
        g2d.setStroke(buildStroke());
        int x=Math.min(x1,x2);
        int y=Math.min(y1,y2);
        int w=Math.abs(x2-x1);
        int h=Math.abs(y2-y1);
        if (filled)
        {
            g2d.fillOval(x,y,w,h);
        }
        else
        {
            g2d.drawOval(x,y,w,h);
        }
    }
}
//stores a list of points the user dragged through
class FreeHandShape extends Shape
{
    private java.util.List<Point>points=new java.util.ArrayList<>();
    public FreeHandShape(Color color,float strokeWidth)
    {
        super(color, strokeWidth, false, false);
    }
    //mouse moving during dragging
    public void addPoint(int x,int y)
    {
        points.add(new Point(x,y));
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        if (points.size()<2) {return;} //entery point and end point todraw a line
        g2d.setColor(color);
        g2d.setStroke(buildStroke());
        for (int i=1;i<points.size();i++)
        {
            Point prev=points.get(i-1);
            Point curr=points.get(i);
            g2d.drawLine(prev.x,prev.y,curr.x,curr.y);
        }
    }
}
// EraserShape - draws a thick white line (simulates erasing)
class EraserShape extends Shape
{
    private java.util.List<Point>points=new java.util.ArrayList<>();
    private static final float ERASER_SIZE=20f;
    //public constructor
    public EraserShape()
    {
        super(Color.WHITE,ERASER_SIZE,false,false);
    }
    public void addPoint(int x,int y)
    {
        points.add(new Point(x,y));
    }
    @Override
    public void draw(Graphics2D g2d)
    {
        if (points.size()<1) return;
        //paint white to =>erase
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(ERASER_SIZE,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        if (points.size()==1)
        {//single click:draw a small white circle
            Point p = points.get(0);
            g2d.fillOval(p.x-(int)(ERASER_SIZE/2),p.y-(int)(ERASER_SIZE/2),
                    (int)ERASER_SIZE,(int)ERASER_SIZE);
        }
        else
        {
            for (int i=1;i<points.size();i++)
            {
                Point prev=points.get(i-1);
                Point curr=points.get(i);
                g2d.drawLine(prev.x,prev.y,curr.x,curr.y);
            }
        }
    }
}