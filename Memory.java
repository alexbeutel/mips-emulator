import java.util.*;
public class Memory {
	protected Object[] mem;
	protected int size;
	protected int start;
	protected int current;
	/*
	 * Size in bytes (divisible by 4 so word aligned)
	 */
	public Memory(int size, int start) {
		mem = new Object[(size / 4)];
		for(int i = 0; i < mem.length; i++) {
			mem[i] = null;
		}
		this.size = (size / 4);
		this.start = start;
		current = start;
	}
	public Object get(Integer loc) {
		return mem[loc-start];
	}
	public Object get(String s) {
		return get(Integer.parseInt(s, 16));
	}
	public void set(Integer loc, Object o) {
		mem[loc-start] = o;
	}
	public void set(String s, Object o) {
		set(Integer.parseInt(s, 16), o);
	}
	public void add(Object o) {
		for(int i = 0; i < mem.length; i++) {
			if(mem[i] != null) continue;
			else {
				mem[i] = o;
				return;
			}
		}
		throwOverflowError();
	}
	private void throwOverflowError() {
		System.out.println("Overflow error");
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
