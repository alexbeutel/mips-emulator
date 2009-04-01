/*
 * Class for TextSegment memory
 * Holds instructions
 * 
 */

import java.util.*;
public class TextSegment extends Memory {
	public int pc;
	public TextSegment() {
		super(2*1024, Integer.parseInt("00400000", 16));
		pc = this.start;
	}
	public TextSegment(int size, int start) {
		super(size, start);
	}
	protected void throwOverflowError() {
		System.out.println("Your text segment memory is full");
	}
	
	//can return values like to quit program
	public boolean run(int loc) {
		// Increment PC + 4
		pc += 4;
		
		//not as clear division of I and J as expected, may need to combine
		
		// What type is it?
		int opcode = getBits(loc, 26, 31);
		int rs, rt, rd, immed, shamt, func, addr;
		if(opcode == 0) { //R-Type
			rs = getBits(loc, 21, 25);
			rt = getBits(loc, 16, 20);
			rd = getBits(loc, 11, 15);
			shamt = getBits(loc, 6, 10);
			func = getBits(loc, 0, 5);
			
			//COMMANDS!
			
		} else if(opcode != 0x2 && opcode != 0x3) { //I-Type
			rs = getBits(loc, 21, 25);
			rt = getBits(loc, 16, 20);
			immed = getBits(loc, 0, 15);
			
			//COMMANDS!
			
		} else { //J-Type
			addr = getBits(loc, 0, 25);
			
			//COMMANDS!
			
		}
		
		return false;
	}
}
