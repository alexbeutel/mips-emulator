import java.util.*;
public class Register {
	public int[] r;
	public Register() {
		r = new int[31];
		for(int i = 0; i < r.length; i++)
			r[i] = 0;
		r[2] = 16;
	}
	public int get(int index) {
		return r[index];
	}
	public void set(int index, int o) {
		if(index == 0) return;
		r[index] = o;
	}
}