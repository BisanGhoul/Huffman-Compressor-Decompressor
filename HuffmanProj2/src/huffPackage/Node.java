package huffPackage;


public class Node implements Comparable<Node> {

	private int value, weight;
	private Node left, right;
    private boolean isLeaf = false;
    //set the node as the leaf node
    public void setToLeaf() {
        isLeaf = true;
    }
    
    public Node() {
	}

	public void setWeight(int myWeight) {
		this.weight = myWeight;
	}

	//check if the node is the leaf or not
    public boolean isLeaf() {
        return isLeaf;
    }

	public void setLeft(Node myLeft) {
		this.left = myLeft;
	}
	public void setRight(Node myRight) {
		this.right = myRight;
	}

	public Node(int value, int weight) {
		this(value, weight, null, null);
	}


	public Node(int value, int weight, Node left, Node right) {
		this.value = value;
		this.weight = weight;
		this.left = left;
		this.right = right;
	}


	public int compareTo(Node other) {
		return weight - other.weight;
	}

	public int value() {
		return value;
	}

	public int weight() {
		return weight;
	}

	public Node left() {
		return left;
	}

	public Node right() {
		return right;
	}

	public String toString() {
		return Character.toString((char)value);
	}
}