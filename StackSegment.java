import java.util.*;
public class StackSegment extends Memory {
	public StackSegment() {
		super(2*1024,0x7FFFEFFF-2*1024);
	}
	public StackSegment(int size, int start) {
		super(size, start);
	}
	protected void throwOverflowError() {
		System.out.println("Your stack segment memory is full");
	}
}
