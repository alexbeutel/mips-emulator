import java.util.*;
public class DataSegment extends Memory {
	public DataSegment() {
		super(4*1024, Integer.parseInt("10010000", 16));
	}
	public DataSegment(int size, int start) {
		super(size, start);
	}
	protected void throwOverflowError() {
		System.out.println("Your static data segment memory is full");
	}
	public static void hello() {
		System.out.println("hi");
	}
}
