import java.util.*;
public class DataSegment extends Memory {
	public DataSegment() {
		super(4*1024, 0x10010000);
	}
	public DataSegment(int size, int start) {
		super(size, start);
	}
	protected void throwOverflowError() {
		System.out.println("Your static data segment memory is full");
	}
}
