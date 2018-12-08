package vector_editor.controller;

import vector_editor.model.*;
import vector_editor.model.Rectangle;
import vector_editor.view.MainView;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainFrameController {

    //final static Logger logger = Logger.getLogger(ToolsMenu.class);

    private MainView view;
    private Model model; //temporary!!

    private ShapeObject drawShape;
    private boolean isNewShapePainted; //helpful flag while using the pen

    public MainFrameController(MainView view, Model model) //temporary model..
    {
        this.view = view;
        this.model = model;

        this.view.getToolbarComponent().addToolbarComponentListener(new ToolbarComponentListener());

        view.getWorkspaceComponent().addWorkspaceComponentMouseListener(new MouseListenerForWorkspace()); //need to set listeners
        view.getWorkspaceComponent().addWorkspaceComponentMouseMotionListener(new MouseMotionListenerForWorkspace()); //after create the new workspace

    }




    class ToolbarComponentListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) {
            switch(e.getActionCommand()){
                case "rectangle":
                    CurrentShape.setShapeType(ShapeEnum.RECTANGLE);
                    System.out.println("RECTANGLE");
                    isNewShapePainted=true;
//                    temporary to test the model, need to make a key binding
//                    CurrentShape.setShapeType(ShapeEnum.SQUARE);
//                    System.out.println("SQUARE ");
                    break;
                case "pencil":
                    CurrentShape.setShapeType(ShapeEnum.PENCIL);
                    System.out.println("PENCIL");
                    isNewShapePainted=true;
                    break;
                case "pen":
                    CurrentShape.setShapeType(ShapeEnum.PEN);  //
                    System.out.println("PEN");
                    isNewShapePainted=true;
                    break;
                case "oval":
//                    CurrentShape.setShapeType(ShapeEnum.OVAL);
//                    System.out.println("OVAL");
//                    temporary to test the model, need to make a key binding
                    CurrentShape.setShapeType(ShapeEnum.CIRCLE);
                    System.out.println("CIRCLE");
                    isNewShapePainted=true;
                    break;
                case "move":
                    System.out.println("MOVE");
                    break;
                case "zoom":
                    System.out.println("ZOOM");
                    break;
                case "text":
                    System.out.println("TEXT");
                    break;
                case "bitmap":
                    System.out.println("BITMAP");
                    break;

            }


        }
    }



    class MouseMotionListenerForWorkspace implements MouseMotionListener
    {

        @Override
        public void mouseDragged(MouseEvent event) //need to implement the method for the pencil
        {
           // System.out.println("Mouse dragged");

            if (!(drawShape == null))
            {
                if (drawShape instanceof Pencil)
                {
                    ((Pencil) drawShape).addPoint(new Point(event.getX(), event.getY()));
                    System.out.println("Instance of pencil");
                }
                else
                {
                    System.out.println(drawShape.toString());
                    drawShape.setX2(event.getX());
                    drawShape.setY2(event.getY());
                }
                view.getWorkspaceComponent().setTmpShape(drawShape);
                view.getWorkspaceComponent().repaint(); // draw the shape during user's action
            }



        }

        @Override
        public void mouseMoved(MouseEvent arg0)
        {
        }

    }

    class MouseListenerForWorkspace implements MouseListener
    {
        public void mouseClicked(MouseEvent e){
           // System.out.println("Mouse clicked");

        }

        public void mouseEntered(MouseEvent e){}

        public void mouseExited(MouseEvent e){}


        public void mousePressed(MouseEvent e)
        {
            //System.out.println("Mouse pressed");

            if(isNewShapePainted) //flag which check if the previous shape was fully painted (helpful with pen)
            {
                drawShape = getTmpShape(e.getX(), e.getY(), e.getX(), e.getY());
                view.getWorkspaceComponent().setTmpShape(drawShape);
                view.getWorkspaceComponent().repaint();
                isNewShapePainted=false; //not necesary
            }

        }

        public void mouseReleased(MouseEvent e)  //after user's action, it sets the new shape and reset the temp
        {
            System.out.println("Mouse released");
            if (!(drawShape == null))
            {
                if (!(drawShape instanceof Pen)) {

                    drawShape.setX2(e.getX());
                    drawShape.setY2(e.getY());

                    ArrayList<ShapeObject> shapes = view.getWorkspaceComponent().getShapes();
                    shapes.add(drawShape);
                    view.getWorkspaceComponent().setTmpShape(null);
                    view.getWorkspaceComponent().setShapes(shapes);
                    drawShape = null;
                    isNewShapePainted=true;

                }
                else
                {

                    System.out.println("Instance of pen");
                    ((Pen) drawShape).addPoint(new Point(e.getX(), e.getY()));
                    view.getWorkspaceComponent().setTmpShape(drawShape);
                    view.getWorkspaceComponent().repaint();

                    //need to find the solution for add the finished shape of Pen
                    ArrayList<ShapeObject> shapes = view.getWorkspaceComponent().getShapes();
                    shapes.add(drawShape);
                    view.getWorkspaceComponent().setTmpShape(null);
                    view.getWorkspaceComponent().setShapes(shapes);
                    isNewShapePainted=false;


                }


                view.getWorkspaceComponent().repaint();

            }
        }

        private ShapeObject getTmpShape(int x, int y, int x2, int y2)
        {
            switch (CurrentShape.getShapeType())
            {
                case RECTANGLE:
                    return new Rectangle(x, y, x2, y2, CurrentShape.getShapeColor());
                case SQUARE:
                    return new Square(x,y,x2,y2,CurrentShape.getShapeColor());
                case OVAL:
                    return new Oval(x,y,x2,y2,CurrentShape.getShapeColor());
                case CIRCLE:
                    return new Circle(x,y,x2,y2,CurrentShape.getShapeColor());
                case PENCIL:
                   return new Pencil(x, y, x2, y2, CurrentShape.getShapeColor());
                case PEN:
                    return new Pen(x, y, x2, y2, CurrentShape.getShapeColor());
                //return new Pen(x, y, x2, y2, CurrentShape.getShapeColor());
                default:
                    break;
            }
            return drawShape;
        }
    }


}
