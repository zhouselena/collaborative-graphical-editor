import java.awt.*;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 *
 * @author Selena Zhou, CS10 23W, PS6
 */
public class EditorCommunicatorEC extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected EditorEC editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicatorEC(String serverIP, EditorEC editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("received: " + line);
				processCommand(line);
				editor.repaint();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}

	/**
	 * Processes command received from SketchServerCommunicator and updates local editor sketch.
	 * @param str String command, as outlined in command_message_key.txt
	 */
	private void processCommand(String str) {
		String[] tokens = str.split(" ");
		int shapeID = Integer.parseInt(tokens[1]);
		switch (tokens[0]) {
			case "s" -> addShapeToSketch(tokens);						// new shape created
			case "d" -> editor.getSketch().deleteShape(shapeID);		// delete existing shape
			case "m" ->													// move existing shape
					editor.getSketch().moveShapeTo(shapeID, Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
			case "r" ->													// recolor existing shape
					editor.getSketch().recolorShape(shapeID, new Color(Integer.parseInt(tokens[2])));
			case "f" -> editor.getSketch().bringShapeToTop(shapeID);	// brings shape in front of all shapes
			case "b" -> editor.getSketch().pushShapeBack(shapeID);		// sends shape behind all shapes
		}
	}

	/**
	 * Private helper function that adds a new shape to sketch based on what type of shape it is.
	 * @param tokens String command split into tokens, outlined in command_message_key.txt
	 */
	private void addShapeToSketch(String[] tokens) {
		SketchEC editorSketch = editor.getSketch();
		int shapeID = Integer.parseInt(tokens[1]);
		ShapeEC shape = null;
		switch (tokens[2]) {
			case "ellipse":
				shape = new EllipseEC(Integer.parseInt(tokens[3]),
						Integer.parseInt(tokens[4]),
						Integer.parseInt(tokens[5]),
						Integer.parseInt(tokens[6]),
						new Color(Integer.parseInt(tokens[7])));
				break;
			case "rectangle":
				shape = new RectangleEC(Integer.parseInt(tokens[3]),
						Integer.parseInt(tokens[4]),
						Integer.parseInt(tokens[5]),
						Integer.parseInt(tokens[6]),
						new Color(Integer.parseInt(tokens[7])));
				break;
			case "freehand":
				int size = tokens.length;
				shape = new PolylineEC(new Point(Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4])),
									 new Color(Integer.parseInt(tokens[size-1])));
				for (int i = 5; i < size-1; i+=2) {
					int x = Integer.parseInt(tokens[i]);
					int y = Integer.parseInt(tokens[i+1]);
					((PolylineEC)shape).addPoint(new Point(x, y));
				}
				break;
			case "segment":
				shape = new SegmentEC(Integer.parseInt(tokens[3]),
						Integer.parseInt(tokens[4]),
						Integer.parseInt(tokens[5]),
						Integer.parseInt(tokens[6]),
						new Color(Integer.parseInt(tokens[7])));
				break;
		}
		if (shape != null) {
			editorSketch.addShape(shapeID, shape);
		}
	}

	/** Send editor requests to the server **/
	// messages are more clearly outlined in command_message_key.txt

	public void sendNewShapeRequest(ShapeEC s) {
		send("a " + s.toString());
	}

	public void sendMoveRequest(int shapeID, int x, int y) {
		send(String.format("m %d %d %d", shapeID, x, y));
	}

	public void sendDeleteRequest(int shapeID) {
		send(String.format("d %d", shapeID));
	}

	public void sendRecolorRequest(int shapeID, Color c) {
		send(String.format("r %d %d", shapeID, c.getRGB()));
	}
	public void sendFrontRequest(int shapeID) {
		send(String.format("f %d", shapeID));
	}
	public void sendBackRequest(int shapeID) {
		send(String.format("b %d", shapeID));
	}

}
