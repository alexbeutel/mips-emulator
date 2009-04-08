import java.util.*;
public class Register {
	public int[] r;
	public int HI, LO, pc;
	public Register() {
		r = new int[32];
		for(int i = 0; i < r.length; i++)
			r[i] = 0;
		r[2] = 16;
		HI = 0;
		LO = 0;
		pc = 0;
	}
	public int get(int index) {
		return r[index];
	}
	public void set(int index, int o) {
		if(index == 0) return;
		r[index] = o;
	}
	public void printAll() {
		for(int i = 0; i<r.length; i++)
			printReg(i);
		printReg("hi");
		printReg("lo");
		printReg("pc");
	}
	public void printReg(int i) {
		System.out.println("$"+i+" = 0x" + Integer.toHexString(r[i]));
	}
	public void printReg(String s) {
		if(s.equals("lo"))
			System.out.println("$LO=0x" + Integer.toHexString(this.LO));
		else if(s.equals("hi"))
			System.out.println("$HI=0x" + Integer.toHexString(this.HI));
		else if(s.equals("pc"))
			System.out.println("$PC=0x" + Integer.toHexString(this.pc));
	}
}