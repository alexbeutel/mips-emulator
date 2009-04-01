import java.util.*;
public class Register {
	public Object[] r;
	public Register() {
		r = new Object[31];
		r[0] = new Integer(0);
	}
	public Object get(int index) {
		return r[index];
	}
	public void set(int index, Object o) {
		if(index == 0) return;
		r[index] = o;
	}
}