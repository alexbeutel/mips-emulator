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
	protected void throwOverflowError() {
		System.out.println("Your text segment memory is full");
	}
	
	public int getStart() {
		return this.start;
	}
	
	//can return values like to quit program
	public int run(MIPSEmulator mips) {
		int loc = mips.pc;
		// Increment PC + 4
		mips.pc += 4;
		
		System.out.println(Integer.toHexString(this.get(loc)));
		String cmd = "";
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
			cmd = "rtype" + func;
			
			switch(func) {
				case 0x20: //add with overflow
					cmd="add";
					mips.reg.set(rd, (Integer)mips.reg.get(rs) + (Integer)mips.reg.get(rd));
				case 12:
					cmd="syscall";
					if((Integer)mips.reg.get(2) == 16) {
						return -1;
					}
				case 0x21:
					cmd="AND";
					mips.reg.set(rd, mips.reg.getI(rs) & mips.reg.getI(rt));
				case 0x25:
					cmd="OR";
					mips.reg.set(rd, mips.reg.getI(rs) | mips.reg.getI(rt));
			}
			
			//COMMANDS!
			
		} else { //I-Type and J-Type have to be considered together
			rs = getBits(loc, 21, 25);		// 	\
			rt = getBits(loc, 16, 20);		//	 > I-Type
			immed = getBits(loc, 0, 15);	//	/
			addr = getBits(loc, 0, 25);		// J-Type
			
			switch(opcode) {
				case 0xF: //LUI
					cmd = "LUI";
					mips.reg.set(rt, immed << 16);
				case 0x8: //ADDI
					cmd = "ADDI";
					mips.reg.set(rt, (Integer)mips.reg.get(rs) + immed); //make sure sign extended!
				case 13: //ORI
					cmd = "ORI";
					mips.reg.set(rt, mips.reg.getI(rs) | immed); //sign extended?
				
			}
			
			//COMMANDS!
			
		}
		
		if(!cmd.equals("")) System.out.println(cmd);
		return 0;
	}
}
