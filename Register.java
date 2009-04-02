import java.util.*;
public class Register {
	public Object[] r;
	public Register() {
		r = new Object[31];
		for(int i = 0; i < r.length; i++)
			r[i] = new Integer(0);
		//r[2] = 16;
	}
	public Object get(int index) {
		return r[index];
	}
	public int getI(int index) {
		return (Integer)r[index];
	}
	public void set(int index, Object o) {
		if(index == 0) return;
		r[index] = o;
	}
	public void set(int index, int o) {
		set(index, (Integer)o);
	}
}