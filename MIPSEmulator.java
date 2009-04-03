import java.util.*;
import java.io.*;
public class MIPSEmulator {
	public Register reg;
	public DataSegment data;
	public TextSegment instr;
	public StackSegment stack;
	public MIPSEmulator(String s) {
		reg = new Register();
		data = new DataSegment();
		instr = new TextSegment();
		stack = new StackSegment();
		
		try {
			if(s.equals(""))
				s = getUserInput("Name of your file: ");
			loadFile(s);
		} catch (IOException e) {
			out("failed loading file");
			return;
		}
		reg.pc = instr.getStart();
		
		String opt = getUserInput("Single step (s) or run to complete (c): ");
		if(opt.toLowerCase().charAt(0) == 's') {
			singleStep();
		} else {
			runToCompletion();
		}
		
	}
	public MIPSEmulator() {
		this("");
	}
	
	//easier to call than sys out
	public static void out(String s) {
		System.out.println(s);
	}
	
	//Helper function for user input
	public static String getUserInput(String message) {
		System.out.print(message);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ret = "";
		try {
			ret = br.readLine();
		} catch (IOException ioe) {
			System.out.println("IO ERROR");
			return "";
		}	
		return ret;
	}
	
	public void runToCompletion() {
		int callOut = 0;
		// -1 means program is done
		while (callOut != -1) {
			callOut = instr.run(this, true);
		}
	}
	public void singleStep() {
		
		// Nikhil: modify this function for I/O
		
		int callOut = 0;
		while (callOut != -1) {
			callOut = instr.run(this, true);
		}
	}
	
	//Load instructions and static data from file into data and instr objects
	public void loadFile(String filename) throws IOException {
		System.out.println("load file");
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = "";
		int i = 0;
		boolean hasStartedStaticData = false;
		while((line = in.readLine()) != null) {
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
		in.close();
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
		// If running from command prompt, can run by: 
		// java MIPSEmulator a.in
		// Otherwise will ask for a filename
		MIPSEmulator m = new MIPSEmulator(args[0]);
	}

}
