package huffPackage;




//Heap class to set the heap for the class heap
class Heap {
  private Node[] heap;
  private int size = 0;
  //Get the size of the heap
  public int getSize() {
      return size;
  }
  //Constructor for the heap which initializes the heap at first
  public Heap(int i){
      heap = new Node[i];
  }
  //insert the node in minHeap
  public void insert(Node k) {
      size++;
      int i = size;
      while(i > 1 && heap[parent(i)].weight() > k.weight()) {
          heap[i] = heap[parent(i)];
          i = parent(i);
      }
      heap[i] = k;
  }
  //Get the minimum of the node
  public Node getMin(){
      if(size != 0)
          return heap[1];
      return null;
  }
  //Delete the minimum node from the Heap
  public Node delMin() {
      if(size != 0) {
    	  Node min = heap[1];
          heap[1] = heap[size];
          size--;
          heapify(1);
          return min;
      }
      return null;
  }
  //Heapify method to set the property of the minheap and arrange the elements as per the minheap
  public void heapify(int i) {
      int l = left(i);
      int r = right(i);
      int smallest;
      if(r <= size) {
          if(heap[l].weight() < heap[r].weight())
              smallest = l;
          else
              smallest = r;
          if(heap[i].weight() > heap[smallest].weight()) {
              swap(i, smallest);
              heapify(smallest);
          }
      }
      else if(l == size && heap[i].weight() > heap[l].weight()) {
          swap(i, l);
      }
  }
  //method to swap the nodes
  private void swap(int i, int l)
  {
	  Node tmp = heap[i];
      heap[i] = heap[l];
      heap[l] = tmp;
  }
  //method to return the parent position of the node in the heap
  public int parent(int i) {
      return i/2;
  }
  //method to return the left position of the node in the heap
  public int left(int i) {
      return 2*i;
  }
  //method to return the right position of the node in the heap
  public int right(int i) {
      return 2*i+1;
  }
}
