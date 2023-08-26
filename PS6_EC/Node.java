public class Node {
    Node prev;
    Node next;
    int currentID;
    public Node(Node prev, Node next, int currentID) {
        this.prev = prev;
        this.next = next;
        this.currentID = currentID;
    }
}
