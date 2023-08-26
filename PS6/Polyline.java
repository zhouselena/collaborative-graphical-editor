import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Selena Zhou, CS10 23W, PS6
 */
public class Polyline implements Shape {
	private List<Point> points;
	private Color color;

	public Polyline(Point p, Color color) {
		points = new ArrayList<>();
		points.add(p);
		this.color = color;
	}

	public void addPoint(Point p) {
		points.add(p);
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Point p: points) {
			p.x += dx;
			p.y += dy;
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Override
	public boolean contains(int x, int y) {
		for (int i = 0; i < points.size()-1; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			if (Segment.pointToSegmentDistance(x, y, p1.x, p1.y, p2.x, p2.y) <= 10) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {	//draw line between every two points in list
		g.setColor(this.color);
		for (int i = 0; i < points.size()-1; i++) {
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("freehand");
		for (Point p: points) {
			str.append(" ").append(p.x).append(" ").append(p.y);
		}
		str.append(" ").append(this.color.getRGB());
		return str.toString();
	}
}
