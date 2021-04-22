package huffPackage;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class BitStreamIn extends InputStream {
	
	private static final long mask[] = new long [65];
		

	private static final int byte_s = 8;
	private static final int bitBuffer_size = 8;
	private static final int bufferSize = 8;

	
	private InputStream inputStream;
	private ReadableByteChannel channel;
	private ByteBuffer byteBuffer;
	private int bitsRead,limit,unusedBits;
	private long buffer;
	
	public BitStreamIn(String path) {
		this(new File(path));
	}
	
	public BitStreamIn(File srcFile) {
		try {
			initialize(new FileInputStream(srcFile));
		}
		catch (FileNotFoundException ntFoundExcptn) {
			throw new RuntimeException(ntFoundExcptn);
		}
	}
	
	public BitStreamIn(InputStream inputStream) {
		initialize(inputStream);
	}
	
	private void initialize(InputStream inStream) {
		inputStream = new BufferedInputStream(inStream);
		for(int i=1;i<mask.length;i++) {
			mask[i]=(mask[i-1]*2)+1;
		inputStream.mark(Integer.MAX_VALUE);
		bitsRead = unusedBits = 0;
		buffer = 0;
		limit = bufferSize;
		channel = Channels.newChannel(inputStream);
		byteBuffer = ByteBuffer.allocate(bufferSize);
		byteBuffer.position(bufferSize);
		mask[0]=0;

			//System.out.println(mask[i]);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public int bitsRead() {
		return bitsRead;
	}
	
	public void reset() {
		try {
			inputStream.reset();
			inputStream.mark(Integer.MAX_VALUE);
			bitsRead = unusedBits = 0;buffer = 0;
			limit = bufferSize;
			channel = Channels.newChannel(inputStream);
			byteBuffer = ByteBuffer.allocate(bufferSize);
			byteBuffer.position(bufferSize);
		}
		catch (IOException io) {
			throw new RuntimeException(io);
		}
	}
	
	public void close() {
		try {
			inputStream.close();
			channel.close();
		}
		catch (IOException io) {
			throw new RuntimeException(io);
		}
	}
	
	public int read() {
		return readBits(byte_s);
	}
	
	public int readBits(int numOfBits) {//8
		if (numOfBits > 32 || numOfBits < 1) {
			throw new RuntimeException("numOfBits must be between 1 & 32");
		}
		
		bitsRead += numOfBits; //0=0+8=8
		
		int value = 0;
		
		if (numOfBits > unusedBits) {//8>0
			value = (int) buffer; //value=0
			numOfBits -= unusedBits;//8=8-0=8
			value <<= numOfBits;//0=0<<8=16
			if (!fillBitBuffer()) {
				return -1;
			}
		}
		
		if (numOfBits > unusedBits) {
			return -1;
		}
		
		value |= buffer >>> (unusedBits - numOfBits);
		buffer =buffer& mask[unusedBits - numOfBits];
		unusedBits -= numOfBits;
		
		return value;
	}
	
	
	
	private boolean fillBitBuffer() {
		if (!byteBuffer.hasRemaining()) {
			if (!fillBuffer()) {
				return false;
			}
		}
		
		unusedBits = byte_s * Math.min(bitBuffer_size, limit - byteBuffer.position());
		buffer = byteBuffer.getLong();
		buffer >>>= (int) Math.pow(2, bitBuffer_size) - unusedBits;
		return true;
	}
	
	private boolean fillBuffer() {
		try {
			byteBuffer.clear();
			limit = channel.read(byteBuffer);
			byteBuffer.flip();
			if (limit == -1) {
				return false;
			}
			byteBuffer.limit(limit + (bitBuffer_size - limit % bitBuffer_size) % bitBuffer_size);
			return true;
		}
		catch (IOException io) {
			throw new RuntimeException(io);
		}
	}
}