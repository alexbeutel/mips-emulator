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
		//System.out.println(loc);
		//System.out.println("0x" + Integer.toHexString(this.get(loc)));
		//System.out.println(Integer.toBinaryString(this.get(loc)));
		String cmd = "";
		//not as clear division of I and J as expected, may need to combine
		
		// What type is it?
		int opcode = getBits(loc, 26, 31, false);
		int rs, rt, rd, immed, shamt, func, addr, immedU, addrU;
		if(opcode == 0) { //R-Type
			rs = getBits(loc, 21, 25, false);
			rt = getBits(loc, 16, 20, false);
			rd = getBits(loc, 11, 15, false);
			shamt = getBits(loc, 6, 10, false);
			func = getBits(loc, 0, 5, false);
			
			//System.out.println(rs + " -- " + rt + " -- " + rd + " -- " + shamt + " -- " + func);
			
			//cmd = "rtype" + func;
			
			switch(func) {
				case 0: //SLL
					cmd = "SLL";
					break;
				case 2: //SRL
					cmd = "SRL";
					break;
				case 3:
					cmd = "SRA";
					break;
				case 8: //JR
					cmd = "JR $"+ rs;
					mips.reg.pc = mips.reg.get(rs);
					break;
				case 12:
					cmd="syscall";
					if((Integer)mips.reg.get(2) == 16) {
						return -1;
					}
					break;
				case 16:
					cmd = "MFHI";
					break;
				case 18:
					cmd = "MFLO";
					break;
				case 24:
					cmd = "MULT";
					break;
				case 25:
					cmd = "MULTU";
					break;
				case 32: //add with overflow
					cmd="ADD $"+rd+"=$"+rs+" + $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) + mips.reg.get(rd));
					break;
				case 33:
					cmd="ADDU $"+rd+"=$"+rs+" + $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) + mips.reg.get(rd));
					break;
				case 34:
					cmd = "SUB";
					break;
				case 35:
					cmd = "SUBU";
					break;
				case 36:
					cmd="AND $"+rd+"=$"+rs+" & $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) & mips.reg.get(rt));
					break;
				case 37:
					cmd="OR $"+rd+"=$"+rs+" | $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) | mips.reg.get(rt));
					break;
				case 38:
					cmd = "XOR";
					break;
				case 39:
					cmd = "NOR";
					break;
				case 42:
					cmd = "SLT";
					break;
				case 43:
					cmd = "SLTU";
					break;
				default:
					cmd = "OTHER R INSTRUCTION -- " + opcode + " -- " + func;
					break;
			}
			
		} else { //I-Type and J-Type have to be considered together
			rs = getBits(loc, 21, 25, false);
			rt = getBits(loc, 16, 20, false);		//	 > I-Type
			immed = getBits(loc, 0, 15);
			immedU = getBits(loc, 0, 15, false);
			addr = getBits(loc, 0, 25);		// J-Type
			addrU = getBits(loc, 0, 25, false);
			
			switch(opcode) {
				case 1:
					if(rt == 1) {
						cmd = "BGEZ $"+rs+", "+immed;
						if(mips.reg.get(rs) >= 0) {
							mips.reg.pc -= 4;
							mips.reg.pc += (immed << 2);
						}
					} else if (rt == 0) {
						cmd = "BLTZ $"+rs+", "+immed;
						if(mips.reg.get(rs) < 0) {
							mips.reg.pc -= 4;
							mips.reg.pc += (immed << 2);
						}
					}
					break;
				case 2: //jump
					cmd = "J "+immed;
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (immed << 2);
					break;
				case 3: //JAL
					cmd = "JAL " + immed;
					// $31 = PC + 8 (or nPC + 4); PC = nPC; nPC = (PC &  0xf0000000) | (target << 2);
					mips.reg.set(31, mips.reg.pc + 4);
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (immed << 2);
					break;
				case 4:
					cmd = "BEQ $"+rs+", $"+rt+", "+immed;
					if(mips.reg.get(rs) == mips.reg.get(rt)) {
						mips.reg.pc -= 4;
						mips.reg.pc += (immed << 2);
					}
					break;
				case 5:
					cmd = "BNE $"+rs+", $"+rt+", "+immed;
					if(mips.reg.get(rs) != mips.reg.get(rt)) {
						mips.reg.pc -= 4;
						mips.reg.pc += (immed << 2);
					}
					break;
				case 6:
					cmd = "BLEZ $"+rs+", "+immed;
					if(mips.reg.get(rs) <= 0) {
						mips.reg.pc -= 4;
						mips.reg.pc += (immed << 2);
					}
					break;
				case 7:
					cmd = "BGTZ $"+rs+", "+immed;
					if(mips.reg.get(rs) > 0) {
						mips.reg.pc -= 4;
						mips.reg.pc += (immed << 2);
					}
					break;
				case 8: //ADDI
					cmd = "ADDI $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed); //make sure sign extended!
					break;
				case 9:;
					cmd = "ADDIU $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed); //make sure sign extended!
					break;
				case 10:
					cmd = "SLTI";
					break;
				case 11:
					cmd = "SLTIU";
					break;
				case 12:
					cmd = "ANDI";
					break;
				case 13: //ORI
					cmd = "ORI $"+rt+"=$"+rs+" | "+immedU;
					mips.reg.set(rt, mips.reg.get(rs) | immedU);
					break;
				case 0x0F: //LUI
					cmd = "LUI $" + rt + ", " + immedU;
					mips.reg.set(rt, immedU << 16);
					break;
				/*
				case 0x14:
					cmd = "BEQL";
					break;
				case 0x15:
					cmd = "BNEL";
					break;
				case 0x16:
					cmd = "BLEZL";
					break;
				case 0x17:
					cmd = "BGTZL";
					break;
				*/
				case 0x20: //LB
					cmd = "LB $"+rt+" = MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getFromMemory(mips.reg.get(rs), immed));
					break;
				case 0x23:
					cmd = "LW";
					break;
				case 0x24:
					cmd = "LBU";
					break;
				case 0x28:
					cmd = "SB";
					break;
				case 0x2B:
					cmd = "SW";
					break;
				default:
					cmd = "OTHER I OR J: " + opcode + " -- " + Integer.toBinaryString(opcode) + " -- " + Integer.toHexString(opcode);
					break;
			}			
		}
		cmd += " -- 0x" + Integer.toHexString(this.get(loc)); //for debugging
		if(!cmd.equals("") && printCmd) System.out.println(cmd);
		return 0;
	}
}
