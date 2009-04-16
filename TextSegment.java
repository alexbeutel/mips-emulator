/*
 * Class for TextSegment memory
 * Holds instructions
 */

import java.util.*;
import java.math.*;
public class TextSegment extends Memory {
	public TextSegment() {
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
		mips.reg.pc += 4;
		String cmd = "";
		
		// What type is it?
		int opcode = getBits(loc, 26, 31, false);
		int rs, rt, rd, immed, shamt, func, addr, immedU, addrU;
		if(opcode == 0) { //R-Type
			rs = getBits(loc, 21, 25, false);
			rt = getBits(loc, 16, 20, false);
			rd = getBits(loc, 11, 15, false);
			shamt = getBits(loc, 6, 10, false);
			func = getBits(loc, 0, 5, false);
			
			switch(func) {
				case 0: //SLL
					cmd = "SLL $"+rd+" = $"+rt+" << " + shamt;
					mips.reg.set(rd, mips.reg.get(rt) << shamt);
					break;
				case 2: //SRL
					cmd = "SRL $"+rd+" = $"+rt+" >>> " + shamt;
					mips.reg.set(rd, mips.reg.get(rt) >>> shamt);
					break;
				case 3:
					cmd = "SRA $"+rd+" = $"+rt+" >> "+shamt;
					mips.reg.set(rd, mips.reg.get(rt) >> shamt);
					break;
				case 8: //JR
					cmd = "JR $"+ rs;
					if(mips.reg.get(rs) == -1){
						return -1;
					}
					mips.reg.pc = mips.reg.get(rs);
					break;
				case 12:
					cmd="syscall "+mips.reg.get(2);
					if(mips.reg.get(2) == 1) {
						System.out.print(mips.reg.get(4)); //get corrector register
					} else if(mips.reg.get(2) == 4) { //print string
						boolean stringIncomplete = true;
						int l = mips.reg.get(4);
						while(stringIncomplete) {
							int x = mips.getMemoryByte(l, 0, false);
							if(x == 0) {
								stringIncomplete = false;
								break;
							}
							System.out.print((char)(x));
							l++;
						}
					} else if(mips.reg.get(2) == 5) {
						String temp = MIPSEmulator.getUserInput("");
						int t = 0;
						try {
							t =Integer.parseInt(temp);
							mips.reg.set(2, t);
						} catch (NumberFormatException e) {
							System.out.println("Invalid input");
						}
					} else if(mips.reg.get(2) == 8) {
						String temp = MIPSEmulator.getUserInput("");
						int i = 0;
						int sloc = mips.reg.get(4);
						while(i < temp.length() && i < mips.reg.get(5)) {
							int val = (int)temp.charAt(i);
							mips.setMemoryByte(sloc+i, 0, val);
							i++;
						}
					} else if(mips.reg.get(2) == 10) {
						return -1;
					}
					break;
				case 16:
					cmd = "MFHI $"+rd+" = $HI";
					mips.reg.set(rd, mips.reg.get("HI"));
					break;
				case 18:
					cmd = "MFLO $"+rd+" = $LO";
					mips.reg.set(rd, mips.reg.get("LO"));
					break;
				case 24:
					cmd = "MULT $LO =$"+rs+" x $"+rt;
					long l1 = mips.reg.get(rs);
					long l2 = mips.reg.get(rt);
					long m = l1 * l2;
					int lo = (int)((m << 32) >>> 32);
					int hi = (int)(m >>> 32);
					mips.reg.set("LO", lo);
					mips.reg.set("HI", hi);
					break;
				case 25:
					cmd = "MULTU $LO =$"+rs+" x $"+rt;
					mips.reg.set("LO", mips.reg.get(rs) * mips.reg.get(rt));
					break;
				case 32: //add with overflow
					cmd="ADD $"+rd+"=$"+rs+" + $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) + mips.reg.get(rt));
					break;
				case 33:
					cmd="ADDU $"+rd+"=$"+rs+" + $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) + mips.reg.get(rt));
					break;
				case 34:
					cmd="SUB $"+rd+"=$"+rs+" - $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) - mips.reg.get(rt));
					break;
				case 35:
					cmd="SUBU $"+rd+"=$"+rs+" - $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) - mips.reg.get(rt));
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
					cmd="XOR $"+rd+"=$"+rs+" ^ $"+rt;
					mips.reg.set(rd, mips.reg.get(rs) ^ mips.reg.get(rt));
					break;
				case 39:
					cmd = "NOR";
					break;
				case 42:
					//if $s < $t $d = 1; advance_pc (4); else $d = 0;
					cmd = "SLT if $"+rs+"<"+rt+" ? $"+rd+" = 1 : $"+rd+" = 0";
					if (mips.reg.get(rs) < mips.reg.get(rt))
						mips.reg.set(rd, 1);
					else
						mips.reg.set(rd, 0);
					break;
				case 43:
					cmd = "SLTU if $"+rs+"<"+rt+" ? $"+rd+" = 1 : $"+rd+" = 0";
					long val1 = ((long)(mips.reg.get(rs)) << 32) >>> 32;
					long val2 = ((long)(mips.reg.get(rt)) << 32) >>> 32;
					if (val1 < val2)
						mips.reg.set(rd, 1);
					else
						mips.reg.set(rd, 0);
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
				case 2:
					cmd = "J "+addrU;
					mips.reg.pc -= 4;
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (addrU << 2);
					break;
				case 3:
					cmd = "JAL " + addrU;
					mips.reg.set(31, mips.reg.pc);
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (addrU << 2);
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
				case 8:
					cmd = "ADDI $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed);
					break;
				case 9:;
					cmd = "ADDIU $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed);
					break;
				case 10:
					cmd = "SLTI if $"+rs+"<"+immed+" ? $"+rt+" = 1 : $"+rt+" = 0";
					if (mips.reg.get(rs) < immed)
						mips.reg.set(rt, 1);
					else
						mips.reg.set(rt, 0);
					break;
				case 11:
					cmd = "SLTIU if $"+rs+"<"+immed+" ? $"+rt+" = 1 : $"+rt+" = 0";
					long val1 = ((long)(mips.reg.get(rs)) << 32) >>> 32;
					long val2 = ((long)(immed) << 32) >>> 32;
					if (val1 < val2)
						mips.reg.set(rt, 1);
					else
						mips.reg.set(rt, 0);
					break;
				case 12:
					cmd = "ANDI";
					break;
				case 13:
					cmd = "ORI $"+rt+"=$"+rs+" | "+immedU;
					mips.reg.set(rt, mips.reg.get(rs) | immedU);
					break;
				case 0x0F:
					cmd = "LUI $" + rt + ", " + immedU;
					mips.reg.set(rt, immedU << 16);
					break;
				case 0x20:
					cmd = "LB $"+rt+" = MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getMemoryByte(mips.reg.get(rs), immed, true));
					break;
				case 0x23:
					cmd = "LW $"+ rt+"= MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getFromMemory(mips.reg.get(rs), immed));
					break;
				case 0x24:
					cmd = "LBU $"+rt+" = MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getMemoryByte(mips.reg.get(rs), immed, false));
					break;
				case 0x28:
					cmd = "SB MEM[ $" +rs+" + "+immed+"] = $"+rt;
					mips.setMemoryByte(mips.reg.get(rs), immed, mips.reg.get(rt));
					break;
				case 0x2B:
					cmd = "SW MEM[ $" +rs+" + "+immed+"] = $"+rt;
					mips.setMemory(mips.reg.get(rs), immed, mips.reg.get(rt));
					break;
				default:
					cmd = "OTHER I OR J: " + opcode + " -- " + Integer.toBinaryString(opcode) + " -- " + MIPSEmulator.formatHex(opcode);
					break;
			}			
		}
		//cmd += " -- 0x" + MIPSEmulator.formatHex(this.get(loc)); //for debugging
		if(!cmd.equals("") && printCmd) System.out.println(cmd);
		return 0;
	}
}
