import java.util.*;
public class MIPSEmulator {
	static Scanner in;
	public Register reg;
	public DataSegment data;
	public TextSegment instr;
	public StackSegment stack;
	public int pc;
	public MIPSEmulator() {
		reg = new Register();
		data = new DataSegment();
		instr = new TextSegment();
		stack = new StackSegment();
		in = new Scanner(System.in);
		
		loadFile();
		this.pc = instr.getStart();
	}
	
	public void runToCompletion() {
		int callOut = 0;
		// -1 means program is done
		while (callOut != -1) {
			callOut = instr.run(this);
		}
	}
	public void singleStep() {
		
		// Nikhil: modify this function for I/O
		
		int callOut = 0;
		while (callOut != -1) {
			callOut = instr.run(this);
		}
	}
	
	//Load instructions and static data from file into data and instr objects
	public void loadFile() {
		System.out.println("load file");
		String line = "";
		int i = 0;
		boolean hasStartedStaticData = false;
		while(in.hasNextLine()) {
			line = in.nextLine();
			if(line.equals("DATA SEGMENT")) {
				hasStartedStaticData = true;
			} else if(!line.startsWith("0x")) {
				System.out.println("ERROR on line "+ i + ": " + line);
			} else {
				if(hasStartedStaticData) {
					String[] nums = line.split(" ");
					line = nums[1].substring(2);
					int val = loadHex(line);
					data.add((Integer)val);
				} else {
					line = line.substring(2);
					int val = loadHex(line);
					instr.add((Integer)val);
				}
			}
			i++;
		}
	}
	public int getFromMemory(int start, int offset) {
		// Fill in, must get from either Data or Stack appropriately
		return 0;
	}
	public static int loadHex(String s) {
		int full = 0;
		for(int i = 0; i < s.length(); i++) {
			int t = Integer.parseInt(s.charAt(i) + "", 16);
			full = full << 4;
			full = full | t;
		}
		return full;
	}
	public static void main(String[] args) {
		MIPSEmulator m = new MIPSEmulator();
		
		//Nikhil, put I/O Code here to determine if single step or not
		boolean runFull = true;
		
		if(runFull) {
			m.runToCompletion();
		} else {
			m.singleStep();
		}

	}

}
