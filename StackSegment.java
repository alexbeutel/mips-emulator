import java.util.*;
/**
 * Extends memory object for the stack
 * Predefined size and locations
 *
 */
public class StackSegment extends Memory {
	/**
	 * Initialize to proper size and location
	 * 2 KB ending at 0x7FFFEFFF
	 */
	public StackSegment() {
		super(2*1024,0x7FFFEFFF-2*1024);
	}
	public StackSegment(int size, int start) {
		super(size, start);
	}
	/**
	 * Throw statck specific overflow error
	 */
	protected void throwOverflowError() {
		System.out.println("Your stack segment memory is full");
	}
}
