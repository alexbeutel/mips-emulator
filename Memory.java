import java.util.*;
public class Memory {
	protected Integer[] mem;
	protected int size;
	protected int start;
	protected int current;
	/*
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
	public Integer get(Integer loc) {
		return mem[getRealLoc(loc)];
	}
	public Integer get(String s) {
		return get(MIPSEmulator.loadHex(s));
	}
	public void set(Integer loc, Integer o) {
		mem[getRealLoc(loc)] = o;
	}
	public void set(String s, Integer o) {
		set(Integer.parseInt(s, 16), o);
	}
	public void add(Integer o) {
		for(int i = 0; i < mem.length; i++) {
			if(mem[i] != null) continue;
			else {
				mem[i] = o;
				return;
			}
		}
		throwOverflowError();
	}
	protected void throwOverflowError() {
		System.out.println("Overflow error");
	}
	protected int getRealLoc(int loc) {
		return (loc-start)/4;
	}
	// Bits going from 0 - 31 for a 32 bit int
	// start < end
	public int getBits(int loc, int start, int end) {
		if(start > end) {
			int temp = end;
			end = start;
			start = temp;
		}
		int max = 0xFFFFFFFF;
		int val = mem[getRealLoc(loc)];
		int mask = (max >>> (31-end)) & (max << start);
		val = val & mask;
		return val >> start;
	}
	/*
	private boolean checkLength() {
		int s = 0;
		for(int i = 0; i < mem.size(); i++) {
			if(s > size) return false;
			Object o = mem.get(i);
			if(o.getClass().equals(Integer.class))
				s += 4;
			else if (o.getClass().equals(String.class))
				s += 4 * ((String)o).length();
		}
		return true;
	}
	*/
}
