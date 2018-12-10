package vector_editor.controller;

import vector_editor.model.*;
import vector_editor.model.Shapes.*;
import vector_editor.model.Shapes.Rectangle;
import vector_editor.view.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainFrameController {

    //final static Logger logger = Logger.getLogger(ToolsMenu.class);

    private MainView view;
    private Model model; //temporary!!
    private InputMap inputMap; //key binding
    private ActionMap actionMap; //KB
    private ShapeObject drawShape;
    private boolean isNewShapePainted; //helpful flag while using the pen
    private boolean isShiftKeyPressed = false;

    public MainFrameController(MainView view, Model model) //temporary model..
    {
        this.view = view;
        this.model = model;

        this.view.getToolbarComponent().addToolbarComponentListener(new ToolbarComponentListener());
        this.view.addListenerToContainer(new ContainerListenerForMainFrame());

        //creating key binders
        inputMap = view.getJFrame().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        actionMap = view.getJFrame().getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK, false), "shiftPressed");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0, true), "shiftReleased");

        actionMap.put("shiftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isShiftKeyPressed = true;
            }
        });

        actionMap.put("shiftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isShiftKeyPressed = false;
            }
        });

    }





    class ContainerListenerForMainFrame extends ContainerAdapter{
    // initial solution when the new workspace is set
        @Override
        public void componentAdded(ContainerEvent e) {
            view.getWorkspaceComponent().addWorkspaceComponentMouseListener(new MouseListenerForWorkspace()); //need to set listeners
            view.getWorkspaceComponent().addWorkspaceComponentMouseMotionListener(new MouseMotionListenerForWorkspace()); //after create the new workspace
           int width = view.getWorkspaceComponent().getWidth();
           int height = view.getWorkspaceComponent().getHeight();
           String name = view.getWorkspaceComponent().getName();
           model.setWorkspace(new Workspace(width,height,name));
        }
    }

        class ToolbarComponentListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e) {

                switch(e.getActionCommand()){
                    case "rectangle":
    //                    CurrentShape.setShapeType(ShapeEnum.RECTANGLE);
    //                    System.out.println("RECTANGLE");
    //                    temporary to test the model, need to make a key binding
                        CurrentShape.setShapeType(ShapeEnum.SQUARE);
                        System.out.println("SQUARE ");
                        isNewShapePainted=true;

                        break;
                    case "pencil":
                        CurrentShape.setShapeType(ShapeEnum.PENCIL);
                        System.out.println("PENCIL");
                        isNewShapePainted = true;
                        break;
                    case "pen":
                        CurrentShape.setShapeType(ShapeEnum.PEN);  //
                        System.out.println("PEN");
                        isNewShapePainted = true;
                        break;
                    case "oval":
    //                    CurrentShape.setShapeType(ShapeEnum.OVAL);
    //                    System.out.println("OVAL");
    //                    temporary to test the model, need to make a key binding
                        CurrentShape.setShapeType(ShapeEnum.CIRCLE);
                        System.out.println("CIRCLE");
                        isNewShapePainted = true;
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



    class MouseMotionListenerForWorkspace extends MouseMotionAdapter
    {

        @Override
        public void mouseDragged(MouseEvent event)  //to set points in the pencil or to paint the shapes in real time before add them to the model
        {
            if (!(drawShape == null))
            {
                if (CurrentShape.getShapeType()==ShapeEnum.PENCIL )
                {
                    ((Polyline)drawShape).addPoint(new Point(event.getX(), event.getY()));
                }
                else {
                    if (isShiftKeyPressed) {
                        if (drawShape instanceof Rectangle) {
                            Square square = new Square(drawShape.getX(), drawShape.getY(), drawShape.getX2(), drawShape.getY2(), drawShape.getColor());
                            drawShape = square;
                        } else if (drawShape instanceof Oval) {
                            Circle circle = new Circle(drawShape.getX(), drawShape.getY(), drawShape.getX2(), drawShape.getY2(), drawShape.getColor());
                            drawShape = circle;
                        }
                    } else {
                        if (drawShape instanceof Square) {
                            Rectangle rectangle = new Rectangle(drawShape.getX(), drawShape.getY(), drawShape.getX(), drawShape.getY2(), drawShape.getColor());
                            drawShape = rectangle;
                        } else if (drawShape instanceof Circle) {
                            Oval oval = new Oval(drawShape.getX(), drawShape.getY(), drawShape.getX2(), drawShape.getY2(), drawShape.getColor());
                            drawShape = oval;
                        }
                    }
                    //System.out.println(drawShape.toString());
                    drawShape.setX2(event.getX());
                    drawShape.setY2(event.getY());
                }
                view.getWorkspaceComponent().setTmpShape(drawShape);
                view.getWorkspaceComponent().repaint(); // draw the shape during user's action
            }

        }


    }

    class MouseListenerForWorkspace extends MouseAdapter
    {
    @Override
        public void mousePressed(MouseEvent e)  //when new shape is created, to set the starting point
        {
            if(isNewShapePainted) //flag which check if there is a new shape (helpful with pen)
            {
                drawShape = getTmpShape(e.getX(), e.getY(), e.getX(), e.getY());
                view.getWorkspaceComponent().setTmpShape(drawShape);
                view.getWorkspaceComponent().repaint();
                isNewShapePainted=false; //not necesary
            }

        }

        public void mouseReleased(MouseEvent e)  //after user's action, it sets the finishing point and add the shape to the model
        {
            if (!(drawShape == null))
            {
                ArrayList<ShapeObject> shapes = view.getWorkspaceComponent().getShapes();

                if (CurrentShape.getShapeType()!=ShapeEnum.PEN ) {

                    drawShape.setX2(e.getX());
                    drawShape.setY2(e.getY());


                    shapes.add(drawShape);
                    model.getWorkspace().addShape(drawShape);  //need to set the instance of the shape before
                    view.getWorkspaceComponent().setTmpShape(null);
                    view.getWorkspaceComponent().setShapes(shapes);
                    drawShape = null;
                    isNewShapePainted=true;

                }
                else  //in the case of pen update the current pen shape in the view and the model
                {
                    //checking if its a new instance of pen
                    if(((Polyline)drawShape).getPoints().size()==0){
                        ((Polyline)drawShape).addPoint(new Point(e.getX(), e.getY()));
                    }
                    else //if the pen is actually used need to remove the previous shape and add new one with new points
                    {
                        ShapeObject tempPen= shapes.get(shapes.size()-1); //get the last Pen object and changed them
                        ((Polyline)tempPen).addPoint(new Point(e.getX(), e.getY()));
                        shapes.remove((shapes.size()-1));
                        model.getWorkspace().removeShape(shapes.size());
                        drawShape=tempPen;
                    }
                    shapes.add(drawShape);
                    view.getWorkspaceComponent().setShapes(shapes);
                    model.getWorkspace().addShape(drawShape);
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
                   return new Polyline(x, y, x2, y2, CurrentShape.getShapeColor());
                case PEN:
                    return new Polyline(x, y, x2, y2, CurrentShape.getShapeColor());
                default:
                    break;
            }
            return drawShape;
        }
    }
}


