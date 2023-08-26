import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Selena Zhou, CS10 23W, PS6
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			for (int shapeIDKey: server.getSketch().getAllIDsOldestToNewest()) {
				String cmd = "s " + shapeIDKey + " " + server.getSketch().getShape(shapeIDKey).toString();
				out.println(cmd);
			}

			// Keep getting and handling messages from the client
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println("received: " + line);
				processCommand(line);
			}

			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Helper function that processes a command received.
	 * @param cmd String command, outlined by command_message_key.txt
	 */
	private void processCommand(String cmd) {
		String[] tokens = cmd.split(" ");
		switch (tokens[0]) {
			case "a" -> {		// add new shape
				int shapeID = addShapeToSketch(tokens);
				server.broadcast(String.format("s %d %s", shapeID, server.getSketch().getShape(shapeID).toString()));
			}
			case "d" -> {		// delete existing shape
				server.getSketch().deleteShape(Integer.parseInt(tokens[1]));
				server.broadcast(cmd);
			}
			case "m" -> {		// move existing shape
				server.getSketch().moveShapeTo(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
				server.broadcast(cmd);
			}
			case "r" -> {		// recolor existing shape
				server.getSketch().recolorShape(Integer.parseInt(tokens[1]), new Color(Integer.parseInt(tokens[2])));
				server.broadcast(cmd);
			}
		}
	}

	// Helper function to determine which shape to create and then add it to sketch
	private int addShapeToSketch(String[] tokens) {
		Sketch serverSketch = server.getSketch();
		Shape shape = null;
		switch (tokens[1]) {
			case "ellipse":
				shape = new Ellipse(Integer.parseInt(tokens[2]),
											Integer.parseInt(tokens[3]),
											Integer.parseInt(tokens[4]),
											Integer.parseInt(tokens[5]),
											new Color(Integer.parseInt(tokens[6])));
				break;
			case "rectangle":
				shape = new Rectangle(Integer.parseInt(tokens[2]),
												Integer.parseInt(tokens[3]),
												Integer.parseInt(tokens[4]),
												Integer.parseInt(tokens[5]),
												new Color(Integer.parseInt(tokens[6])));
				break;
			case "freehand":
				int size = tokens.length;
				shape = new Polyline(new Point(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3])),
						new Color(Integer.parseInt(tokens[size-1])));
				for (int i = 4; i < size-1; i+=2) {
					int x = Integer.parseInt(tokens[i]);
					int y = Integer.parseInt(tokens[i+1]);
					((Polyline)shape).addPoint(new Point(x, y));
				}
				break;
			case "segment":
				shape = new Segment(Integer.parseInt(tokens[2]),
											Integer.parseInt(tokens[3]),
											Integer.parseInt(tokens[4]),
											Integer.parseInt(tokens[5]),
											new Color(Integer.parseInt(tokens[6])));
				break;
		}
		if (shape != null) {
			int newID = serverSketch.getAvailID();
			serverSketch.addShape(newID, shape);
			return newID;
		}
		return -1;
	}

}
