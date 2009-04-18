import java.util.*;
/**
 * A genreal super class to be used to implement memory segments
 * For all practical purposes could be called as an abstract class,
 * but has enough methods to be instantiated and used.
 */
public class Memory {
	protected Integer[] mem;
	protected int size;
	protected int start;
	protected int current;
	/**
	 * Size in bytes (divisible by 4 so word aligned)
	 */
	public Memory(int size, int start) {
		mem = new Integer[(size / 4)];
		for(int i = 0; i < mem.length; i++) {
			mem[i] = null;
		}
		this.size = (size / 4);
		this.start = start;
		current = start;
	}
	/**
	 * Gets the 4 bytes at given memory location
	 * This assumes word aligned (4 bytes asked for can not span across two words)
	 * @param loc Memory location
	 * @return 32 bits (4 bytes) at that location
	 */
	public int get(Integer loc) {
		if(mem[getRealLoc(loc)] == null)
			return 0;
		return mem[getRealLoc(loc)];
	}
	/**
	 * Get based on hex value
	 * @param s hexadecimal string of location
	 * @return 4 bytes at that location
	 */
	public int get(String s) {
		return get(MIPSEmulator.loadHex(s));
	}
	/**
	 * Set word at given location
	 * @param loc Memory location
	 * @param o int to put into the location
	 */
	public void set(int loc, int o) {
		mem[getRealLoc(loc)] = o;
	}
	/**
	 * Set based on hexadecimal location
	 * @param s Hexadecimal string
	 * @param o intput value
	 */
	public void set(String s, int o) {
		set(MIPSEmulator.loadHex(s), o);
	}
	/**
	 * Add a value to the next available spot in memory
	 * @param o value to add
	 */
	public void add(int o) {
		for(int i = 0; i < mem.length; i++) {
			if(mem[i] != null) continue;
			else {
				mem[i] = o;
				return;
			}
		}
		throwOverflowError();
	}
	/**
	 * Standard overflow error message
	 */
	protected void throwOverflowError() {
		System.out.println("Overflow error");
	}
	/**
	 * Gets index value based on memory location
	 * @param loc Memory location
	 * @return index value in the array mem[]
	 */
	protected int getRealLoc(int loc) {
		return (loc-start)/4;
	}
	/**
	 * Get specific bits from a memory location
	 * @param loc Memory locatino to pull from
	 * @param start starting bit value
	 * @param end end bit value
	 * @param signed Boolean for whether or not to sign extend
	 * @return Requested bits in an int
	 */
	public int getBits(int loc, int start, int end, boolean signed) {
		if(start > end) {
			int temp = end;
			end = start;
			start = temp;
		}
		int val = mem[getRealLoc(loc)];
		return getBitsFromVal(val, start, end, signed);
	}
	/**
	 * Overload if signed term is left out, then sign extend
	 * @param loc Memory locatino to pull from
	 * @param start starting bit value
	 * @param end end bit value
	 * @return Requested bits in an int
	 */
	public int getBits(int loc, int start, int end) {
		return getBits(loc, start, end, true);
	}
	/**
	 * Static function to get specific bits from a given value
	 * @param val Value to take bits from
	 * @param start starting bit value
	 * @param end ending bit value
	 * @param signed boolean for whether or not to signe extend
	 * @return requested bits as an int
	 */
	public static int getBitsFromVal(int val, int start, int end,  boolean signed) {
		int max = 0xFFFFFFFF;
		int mask = (max >>> (31-end)) & (max << start);
		val = val & mask;
		if(signed) {
			val = val << (31 - end);
			return val >> (start + (31-end));
		}
		return val >>> start;
	}
	/**
	 * Print all memory locations with a value that is not 0 nor null.
	 */
	public void printAll() {
		for(int i = 0; i < mem.length; i++) {
			if(mem[i] != null && mem[i] != 0) {
				int loc = i*4 + start;
				System.out.println("0x"+MIPSEmulator.formatHex(loc).toUpperCase()+": 0x"+MIPSEmulator.formatHex(mem[i]).toUpperCase());
			}
		}
	}
}
