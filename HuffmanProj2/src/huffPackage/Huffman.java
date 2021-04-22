package huffPackage;

import java.util.PriorityQueue;


public class Huffman {
	String s="";
	String info="";
	StringBuffer a;
	int headerLength;
	int fileCount;
	int decodedFile;
	
	public static final int word = 8;
	public static final int BITS_PER_INT = 32;
	public static final int ALPH_SIZE = 256;
	public static final int PSEUDO_EOF = ALPH_SIZE;
	public static final int HUFF_NUMBER = 64206;

	Heap heap;




	public void compress(BitStreamIn in, BitStreamOut out) {
		if(in.readBits(1) == -1)
			throw new RunTimeError("Empty input!");
		in.reset();
		int[] counts = calcFrequency(in); // creates frequency array
		Node root = createTreeFromCounts(counts); // creates tree with root
		String[] codings = createHuffmanCodes(root, "", new String[257]); // create
																			// array
																			// where
																			// index
																			// is
																			// value
																			// and
																			// String
																			// is

		writeHeader(root, out);

		System.out.println(s);// writing header

		in.reset(); // reset to write compression after reading stream for
					// counts
		writeBitsToFile(in, codings, out);
		printInfo(counts,codings);
	}

	

	private void printInfo(int[] counts,String[] codings) {
System.out.println("char |"+"ASCII |"+"freq |"+"code "+"\n");
info+=("char |"+"ASCII |"+"freq |"+"code "+"\n");
		for(int i=0; i<counts.length;i++) {
			if(counts[i]!=0) {
				

				System.out.print("("+(char)i+")"+" ");
				info+=("("+(char)i+")"+" ");
				System.out.print(i+"");
				info+=("  "+i+"");
				System.out.print("| "+counts[i]+" \t|");
				info+=("\t"+"| "+counts[i]+" \t|");
				decodedFile+=counts[i];
				System.out.print(codings[i]+"\n");
				info+=("\t"+codings[i]+"\n");
				
			
		}}
	}
	private int[] calcFrequency(BitStreamIn in) {
		int[] ans = new int[257]; // 257 is for PSEUDO_EOF
		while (true) { // reads each bit value and adds one to frequency count
			int val = in.readBits(word);
			fileCount++;//val=charecter ASCII value
			if (val == -1)//no more bits to read
				break;
			ans[val]++;//used the character's ASCII value as its index and incremented it 
		}
		ans[256] = 1;// =PSEUDO_EOF smallest char represented by 9 bits
		return ans;
	}

	private Node createTreeFromCounts(int[] counts) {

			 heap = new Heap(258);
		        //create a minheap
		        for(int i = 0; i < counts.length; i++) {
		            //set the frequency only if not equal to 0
		            if(counts[i] != 0) {
		                //instantiate the new node with the repsective frequency
		                Node node = new Node(i,counts[i] );
		                //set to the leafnode
		                node.setToLeaf();
		               
		                //insert the node in the heap
		                heap.insert(node);
		               // AlphFreq++;
		            }
		}
	
	
	        //get the size of the heap
	        int size = heap.getSize();
	        //repeat untill size
	        for(int i = 0; i < (size-1); ++i)
	        {
	        	
	        	
	        	Node left = heap.delMin();
				Node right = heap.delMin();
				Node t = new Node(-1, left.weight() + right.weight(), left, right);

	            heap.insert(t);
	        }
	        return heap.delMin();
	    
	}

	private String[] createHuffmanCodes(Node node, String codeSt, String[] codings) {
		if ((node.left() == null) && (node.right() == null)) { // base case if
																// leaf, then
																// this is
																// correct path
			codings[node.value()] = codeSt;
		}
		// search left and right, add 0 to path if left, 1 if right, continue
		// until you get all leaves
		if (node.left() != null) {
			codings = createHuffmanCodes(node.left(), codeSt + "0", codings);
		}
		if (node.right() != null) {
			codings = createHuffmanCodes(node.right(), codeSt + "1", codings);
		}
		return codings;
	}
	
	private void writeHeader(Node current, BitStreamOut out){
		if(current.left() == null && current.right() == null){
			out.writeBits(1, 1);
			out.writeBits(word + 1, current.value());
			headerLength++;
			s+="1"+(char)current.value()+"";
		//
			return;
		}
		out.writeBits(1, 0);
		s+="0";
		//headerLength++;                     
		writeHeader(current.left(), out);
		writeHeader(current.right(), out);
	}


	private void writeBitsToFile(BitStreamIn in, String[] codings, BitStreamOut out) {
		// read 8 bits at a time and replace them with encoded values
		int val = in.readBits(8);//read first char
		String encode;
		while (val != -1) {//if we there is still more bits to read
			encode = codings[val];//take the huffman code of the current char and save it in string encode
			out.writeBits(encode.length(), Integer.parseInt(encode, 2));
			val = in.readBits(8);
		}
		// add PSEUDO_EOF
		encode = codings[256];
		out.writeBits(encode.length(), Integer.parseInt(encode, 2));
	}


	public void decompress(BitStreamIn in, BitStreamOut out) {//added {} line 273

		//recreate the Hufftree from header
		Node root = readTreeHeader(in);
		// parse body of compressed file
		Node current = root;
		while (true) {
			decodedFile++;
			int bit = in.readBits(1);
			if(bit == -1) {
				break;
			}else {
				
			if(bit == 1)
				current = current.right();
			else 
				current = current.left();
			if(current.left() == null && current.right() == null){
				if(current.value() == PSEUDO_EOF)
					return;
				else { 
					out.writeBits(word, current.value());
				    current = root;
				    }
			}
			}
		}	
	}

	private Node readTreeHeader(BitStreamIn in){
		if(in.readBits(1) == 0){
			Node left = readTreeHeader(in);
			Node right = readTreeHeader(in);
			return new Node(-1, 0, left, right);
		} else {
			return new Node(in.readBits(word+1), 0);
		}
	}
}


