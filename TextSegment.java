/*
 * Class for TextSegment memory
 * Holds instructions
 * 
 */

import java.util.*;
public class TextSegment extends Memory {
	public TextSegment() {
		super(2*1024, Integer.parseInt("00400000", 16));
	}
	public TextSegment(int size, int start) {
		super(size, start);
	}
	private void throwOverflowError() {
		System.out.println("Your text segment memory is full");
	}
}
