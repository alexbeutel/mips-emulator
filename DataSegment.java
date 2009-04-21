import java.util.*;
/**
 * Object extending memory, specific to the data segment
 *
 */
public class DataSegment extends Memory {
	/**
	 * Initializes to proper location and size
	 * 4 KB starting at 0x10010000
	 */
	public DataSegment() {
		super(4*1024, 0x10010000);
	}
	public DataSegment(int size, int start) {
		super(size, start);
	}
	/**
	 * Throw data segment specific overflow error
	 */
	protected void throwOverflowError() {
		System.out.println("Your static data segment memory is full");
	}
}
