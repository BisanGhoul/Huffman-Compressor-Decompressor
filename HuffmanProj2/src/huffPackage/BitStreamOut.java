package huffPackage;

//Allows for writing up to 32 bits at a time.
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class BitStreamOut extends OutputStream {
	
	private WritableByteChannel channel;
	private OutputStream outputStream;
	private ByteBuffer byteBuffer;
	private long bitBuffer;	
	private int bitsWritten, freeBits;
	private static final long mask[] = new long [65];
	private static final int BUFFER_SIZE = 8;
	//public static final int BYTE_SIZE = 8;
	//private static final int INT_SIZE = 32;





	public BitStreamOut(String path) {
		this(new File(path));//file to be written to
	}
	

	public BitStreamOut(File srcFile) {
		try {
			initialize(new FileOutputStream(srcFile));//file to be written to
		}
		catch (FileNotFoundException fnf) {
			throw new RuntimeException(fnf);
		}
	}
	
	public BitStreamOut(OutputStream outStream) {
		initialize(outStream);//create BitStreamOut (stream for bits) from an output stream
		                      //outStream is where the bits will be written to
	}
	
	private void initialize(OutputStream out) {
		outputStream = out;
		channel = Channels.newChannel(outputStream);
		byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		freeBits = 64;
		bitsWritten = 0;
		bitBuffer = 0;

		mask[0]=0;
		for(int i=1;i<mask.length;i++) {
			mask[i]=(mask[i-1]*2)+1;
		}
	}
	

	private void emptyBitBuffer() {
		if (!byteBuffer.hasRemaining()) {
			emptyBuffer();
		}
		
		byteBuffer.putLong(bitBuffer);
		bitBuffer = 0;
		freeBits = 64;
	}
	
	private void emptyBitBufferFlush() {
		if (!byteBuffer.hasRemaining()) {
			emptyBuffer();
		}
		
		while (freeBits < 64) {
			byteBuffer.put((byte) (bitBuffer >>> 56));//0x38
			bitBuffer <<= 8;
			freeBits += 8;
		}
	}
	
	private void emptyBuffer() {
		try {
			byteBuffer.flip();
			channel.write(byteBuffer);
			byteBuffer.clear();
		}
		catch (IOException io) {
			throw new RuntimeException(io);
		}
	}

	public void flush() {//flush any unwritten bits
		emptyBitBufferFlush();
		emptyBuffer();
	}
	

	public void close() {//close this stream and all channels or stream used by it
		try {
			flush();
			channel.close();
			outputStream.close();
		}
		catch (IOException io) {
			throw new RuntimeException(io);
		}
		
	}
	


	public void write(int value) {
		writeBits(8, value);//writes 8 lsb bits of the value
	}
	

	public void writeBits(int numBits, int value) {
		if (numBits > 32 || numBits < 1) {//32
			throw new RuntimeException("Illegal argument: numBits must be between [1, 32]");
		}
		
		bitsWritten += numBits;
		
		if (numBits > freeBits) {
			bitBuffer |= Integer.toUnsignedLong(value) >>> (numBits - freeBits);

			value &= mask[numBits - freeBits];
			numBits -= freeBits;
			emptyBitBuffer();
		}
		
		bitBuffer |= Integer.toUnsignedLong(value) << (freeBits - numBits);
		freeBits -= numBits;
	}
	
	public int bitsWritten() {
		return bitsWritten;//return number of bits written as long as this bit out stream was used
	}
	

}
