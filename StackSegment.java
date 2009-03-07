import java.util.*;
public class StackSegment extends Memory {
	public StackSegment() {
		//fix this
		super(0,0);
	}
	public StackSegment(int size, int start) {
		super(size, start);
		// TODO Auto-generated constructor stub
	}
	private void throwOverflowError() {
		System.out.println("Your stack segment memory is full");
	}
}
