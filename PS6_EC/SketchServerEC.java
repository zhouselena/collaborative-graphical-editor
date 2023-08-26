import java.net.*;
import java.util.*;
import java.io.*;

/**
 * A server to handle sketches: getting requests from the clients,
 * updating the overall state, and passing them on to the clients
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */
public class SketchServerEC {
	private ServerSocket listen;						// for accepting connections
	private ArrayList<SketchServerCommunicatorEC> comms;	// all the connections with clients
	private SketchEC sketch;								// the state of the world
	
	public SketchServerEC(ServerSocket listen) {
		this.listen = listen;
		sketch = new SketchEC();
		comms = new ArrayList<SketchServerCommunicatorEC>();
	}

	public SketchEC getSketch() {
		return sketch;
	}
	
	/**
	 * The usual loop of accepting connections and firing off new threads to handle them
	 */
	public void getConnections() throws IOException {
		System.out.println("server ready for connections");
		while (true) {
			SketchServerCommunicatorEC comm = new SketchServerCommunicatorEC(listen.accept(), this);
			comm.setDaemon(true);
			comm.start();
			addCommunicator(comm);
		}
	}

	/**
	 * Adds the communicator to the list of current communicators
	 */
	public synchronized void addCommunicator(SketchServerCommunicatorEC comm) {
		comms.add(comm);
	}

	/**
	 * Removes the communicator from the list of current communicators
	 */
	public synchronized void removeCommunicator(SketchServerCommunicatorEC comm) {
		comms.remove(comm);
	}

	/**
	 * Sends the message from the one communicator to all (including the originator)
	 */
	public synchronized void broadcast(String msg) {
		for (SketchServerCommunicatorEC comm : comms) {
			comm.send(msg);
		}
	}
	
	public static void main(String[] args) throws Exception {
		new SketchServerEC(new ServerSocket(4242)).getConnections();
	}
}
