import java.util.*;
public class Register {
	public int[] r;
	public int HI, LO, pc;
	/**
	 * Initialize all registers to 0
	 */
	public Register() {
		r = new int[32];
		for(int i = 0; i < r.length; i++)
			r[i] = 0;
		HI = 0;
		LO = 0;
		pc = 0;
	}
	/**
	 * Get value from register
	 * @param index register number
	 * @return return 4 byte value from register (int)
	 */
	public int get(int index) {
		return r[index];
	}
	/**
	 * Given a string name of a register (hi, lo, or pc) return that register
	 * @param type Register name
	 * @return 4 byte value in register (int)
	 */
	public int get(String type){
		if (type.toLowerCase().equals("hi"))
			return HI;
		else if (type.toLowerCase().equals("lo"))
			return LO;
		else if (type.toLowerCase().equals("pc"))
			return pc;
		else
			return -1;
	}
	/**
	 * Set value at given register
	 * Register 0 must remain 0
	 * @param index index of register to change
	 * @param o int value to put in register
	 */
	public void set(int index, int o) {
		if(index == 0) return;
		r[index] = o;
	}
	/**
	 * Set value at given register
	 * @param type string name of register (hi,lo,pc)
	 * @param value int value to put in register
	 */
	public void set(String type, int value){
		if (type.toLowerCase().equals("hi"))
			HI = value;
		else if (type.toLowerCase().equals("lo"))
			LO = value;
		else if (type.toLowerCase().equals("pc"))
			pc = value;
	}
	/**
	 * Print all registers as hexadecimal values
	 */
	public void printAll() {
		for(int i = 0; i<r.length; i++)
			printReg(i);
		printReg("hi");
		printReg("lo");
		printReg("pc");
	}
	/**
	 * Print given register
	 * @param i index of register to print
	 */
	public void printReg(int i) {
		System.out.println("$"+i+" = 0x" + MIPSEmulator.formatHex(r[i]));
	}
	/**
	 * Print given register based on string (hi, lo, pc)
	 * @param s string name of register
	 */
	public void printReg(String s) {
		if(s.equals("lo"))
			System.out.println("$LO=0x" + MIPSEmulator.formatHex(this.LO));
		else if(s.equals("hi"))
			System.out.println("$HI=0x" + MIPSEmulator.formatHex(this.HI));
		else if(s.equals("pc"))
			System.out.println("$PC=0x" + MIPSEmulator.formatHex(this.pc));
	}
}