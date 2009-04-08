/*
 * Class for TextSegment memory
 * Holds instructions
 * 
 */

import java.util.*;
public class TextSegment extends Memory {
	public TextSegment() {
		// CHECK SIZE & START
		super(2*1024, 0x00400000);
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
	public int run(MIPSEmulator mips, boolean printCmd) {
		int loc = mips.reg.pc;
		// Increment PC + 4
		mips.reg.pc += 4;
		
		System.out.println("0x" + Integer.toHexString(this.get(loc)));
		//System.out.println(Integer.toBinaryString(this.get(loc)));
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
			//cmd = "rtype" + func;
			
			switch(func) {
				case 0x20: //add with overflow
					cmd="ADD $"+rd+"=$"+rs+" + $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) + mips.reg.get(rd));
				case 12:
					cmd="syscall";
					if((Integer)mips.reg.get(2) == 16) {
						return -1;
					}
				case 0x21:
					cmd="AND $"+rd+"=$"+rs+" & $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) & mips.reg.get(rt));
				case 0x25:
					cmd="OR $"+rd+"=$"+rs+" | $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) | mips.reg.get(rt));
				case 8: //JR
					cmd = "JR $"+ rs;
					mips.reg.pc = mips.reg.get(rs);
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
					cmd = "ADDI $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed); //make sure sign extended!
				case 13: //ORI
					cmd = "ORI $"+rt+"=$"+rs+" | "+immed;
					mips.reg.set(rt, mips.reg.get(rs) | immed); //sign extended?
				case 0x20: //LB
					cmd = "LB $"+rt+" = MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getFromMemory(mips.reg.get(rs), immed));
				case 2: //jump
					cmd = "J "+immed;
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (immed << 2);
				case 3: //JAL
					cmd = "JAL " + immed;
					// $31 = PC + 8 (or nPC + 4); PC = nPC; nPC = (PC &  0xf0000000) | (target << 2);
					mips.reg.set(31, mips.reg.pc + 4);
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (immed << 2);
			}
			
			//COMMANDS!
			
		}
		
		if(!cmd.equals("") && printCmd) System.out.println(cmd);
		return 0;
	}
}
