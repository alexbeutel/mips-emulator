/*
 * Class for TextSegment memory
 * Holds instructions
 * 
 */

import java.util.*;
import java.math.*;
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
		//System.out.println("0x" + MIPSEmulator.formatHex(this.get(loc)));
		//System.out.println(Integer.toBinaryString(this.get(loc)));
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
			
			//System.out.println(rs + " -- " + rt + " -- " + rd + " -- " + shamt + " -- " + func);
			
			switch(func) {
				case 0: //SLL
					cmd = "SLL $"+rd+" = $"+rt+" << " + shamt;
					//mips.reg.set(rd, (int)(mips.reg.get(rt) * Math.pow(10, shamt)));
					mips.reg.set(rd, mips.reg.get(rt) << shamt);
					break;
				case 2: //SRL
					cmd = "SRL $"+rd+" = $"+rt+" >>> " + shamt;
					//mips.reg.set(rd, (int)(mips.reg.get(rt) * Math.pow(.1, shamt)));
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
							int x = mips.getFromMemory(l, 0);
							for(int i = 0; i < 4; i++) {
								int val = getBitsFromVal(x,24-8*i,31-8*i, false);
								if(val == 0) {
									stringIncomplete=false;
									break;
								}
								System.out.print((char)(val));
							}
							l += 4;
						}
					} else if(mips.reg.get(2) == 5) {
						String temp = MIPSEmulator.getUserInput("");
						int t = 0;
						try {
							t =Integer.parseInt(temp);
							mips.reg.set(2, t); //mips.getFromMemory(mips.reg.get(4),0));
						} catch (NumberFormatException e) {
							System.out.println("Invalid input");
						}
					} else if(mips.reg.get(2) == 8) {
						String temp = MIPSEmulator.getUserInput("");
						int i = 0;
						while(i < temp.length() && i < mips.reg.get(3)) {
							int val = 0;
							String temp2 = temp;
							if(temp.length() > 4)
								temp2 = temp.substring(0, 4);
							//FIX THIS
							for(int j = 0; j < temp2.length(); j++) {
								val = (int)temp2.charAt(j)<<(24-8*j) | val;
							}
							//val = (int)temp2.charAt(0)<<24 | (int)temp2.charAt(1)<<16 | (int)temp2.charAt(2)<<8  | (int)temp2.charAt(3);
							mips.setMemory(mips.reg.get(2), i, val);
							if(temp.length() > 4)
								temp = temp.substring(4);
							i += 4;
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
					//BigInteger b1 = new BigInteger(mips.reg.get(rs) + "");
					//BigInteger b2 = new BigInteger(mips.reg.get(rt) + "");
					//b1.multiply(b2);
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
			int word, byteOffset, val;
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
					cmd = "J "+addrU;
					mips.reg.pc -= 4;
					mips.reg.pc = (mips.reg.pc & 0xF0000000) | (addrU << 2);
					break;
				case 3: //JAL
					cmd = "JAL " + addrU;
					// $31 = PC + 8 (or nPC + 4); PC = nPC; nPC = (PC &  0xf0000000) | (target << 2);
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
				case 8: //ADDI
					cmd = "ADDI $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed); //make sure sign extended!
					break;
				case 9:;
					cmd = "ADDIU $"+rt+"=$"+rs+" + "+immed;
					mips.reg.set(rt, mips.reg.get(rs) + immed); //make sure sign extended!
					break;
				case 10:
					//If $s is less than immediate, $t is set to one. It gets zero otherwise.
					cmd = "SLTI if $"+rs+"<"+immed+" then $"+rt+" = 1. Otherwise $"+rt+" = 0.";
					if (mips.reg.get(rs) < immed)
						mips.reg.set(rt, 1);
					else
						mips.reg.set(rt, 0);
					break;
				case 11:
					cmd = "SLTIU if $"+rs+"<"+immed+" then $"+rt+" = 1. Otherwise $"+rt+" = 0.";
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
					word = mips.getFromMemory(mips.reg.get(rs), immed);
					byteOffset = (mips.reg.get(rs) + immed) % 8;
					val = getBitsFromVal(word,24-8*byteOffset,31-8*byteOffset, true);
					mips.reg.set(rt, val);
					break;
				case 0x23:
					cmd = "LW $"+ rt+"= MEM[$"+rs+" + "+immed+"]";
					mips.reg.set(rt, mips.getFromMemory(mips.reg.get(rs), immed));
					break;
				case 0x24:
					cmd = "LBU";
					word = mips.getFromMemory(mips.reg.get(rs), immed);
					byteOffset = (mips.reg.get(rs) + immed) % 8;
					val = getBitsFromVal(word,24-8*byteOffset,31-8*byteOffset, false);
					break;
				case 0x28:
					cmd = "SB MEM[ $" +rs+" + "+immed+"] = $"+rt;
					mips.setMemoryByte(mips.reg.get(rs), immed, mips.reg.get(rt));
					break;
				case 0x2B:
					cmd = "SW MEM[ $" +rs+" + "+immed+"] = $"+rt;
					//int tloc = mips.reg.get(rs) + immed;
					mips.setMemory(mips.reg.get(rs), immed, mips.reg.get(rt));
					break;
				default:
					cmd = "OTHER I OR J: " + opcode + " -- " + Integer.toBinaryString(opcode) + " -- " + MIPSEmulator.formatHex(opcode);
					break;
			}			
		}
		cmd += " -- 0x" + MIPSEmulator.formatHex(this.get(loc)); //for debugging
		if(!cmd.equals("") && printCmd) System.out.println(cmd);
		return 0;
	}
}
