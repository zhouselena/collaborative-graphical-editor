import java.awt.*;
import java.util.List;
import java.util.TreeMap;

/**
 * Sketch class to hold list of shapes and IDs.
 * @author Selena Zhou, CS10 23W, PS6
 */
public class Sketch {

    private TreeMap<Integer, Shape> IDtoShapeMap;           // Map shapes to their IDs
    private int currentID;                                  // To create new unique ID

    public Sketch() {
        IDtoShapeMap = new TreeMap<>();
        currentID = 0;
    }

    /**
     * For server to determine what is the next available ID, and increments once returned.
     * @return next available ID
     */
    public synchronized int getAvailID() {
        return currentID++;
    }

    /**
     * Adds a shape to the sketch/map of shapes.
     * @param ID ID of the new shape
     * @param shape shape
     */
    public void addShape(int ID, Shape shape) {
        IDtoShapeMap.put(ID, shape);
    }

    /**
     * Given the ID, return the shape as long as the ID exists.
     * @param ID ID of the shape.
     * @return Shape under ID.
     */
    public synchronized Shape getShape(int ID) {
        if (IDtoShapeMap.containsKey(ID)) {
            return IDtoShapeMap.get(ID);
        }
        return null;
    }

    /**
     * Removes shape from the sketch.
     * @param ID ID of the shape.
     */
    public synchronized void deleteShape(int ID) {
        IDtoShapeMap.remove(ID);
    }

    /**
     * Moves a shape in the sketch to the given x y coordinates.
     * @param ID ID of the shape
     * @param x target x-coordinate
     * @param y target y-coordinate
     */
    public void moveShapeTo(int ID, int x, int y) {
        Shape shape = IDtoShapeMap.get(ID);
        String[] shapeInfo = shape.toString().split(" ");
        int dx = x - Integer.parseInt(shapeInfo[1]);            // creates dx and dy by subtracting the initial position from the target position
        int dy = y - Integer.parseInt(shapeInfo[2]);
        shape.moveBy(dx, dy);
    }

    /**
     * Recolors a shape.
     * @param ID ID of the shape to recolor
     * @param color color to recolor to
     */
    public void recolorShape(int ID, Color color) {
        IDtoShapeMap.get(ID).setColor(color);
    }

    /**
     * Returns shape ID of the shape the user is clicking on.
     * @param p point where user's mouse clicked.
     * @return shape ID if p resides on a shape, -1 if not.
     */
    public int mouseInShape(Point p) {
        for (int ID: IDtoShapeMap.descendingKeySet()) {
            if (IDtoShapeMap.get(ID).contains(p.x, p.y)) {
                 return ID;
            }
        }
        return -1;
    }

    /**
     * Draws all shapes in the sketch with least recent at the very back.
     * @param g graphics window
     */
    public void drawAllShapes(Graphics g) {
        for (int ID: IDtoShapeMap.navigableKeySet()) {
            IDtoShapeMap.get(ID).draw(g);
        }
    }

    /**
     * @return returns all IDs of the shapes in order of oldest to newest.
     */
    public List<Integer> getAllIDsOldestToNewest() {
        return IDtoShapeMap.navigableKeySet().stream().toList();
    }
}
