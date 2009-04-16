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
		
		clrscr();
		
		try {
			if(s.equals(""))
				s = getUserInput("Name of your file: ");
			loadFile(s);
		} catch (IOException e) {
			out("failed loading file");
			return;
		}
		
		//Initialize register values
		reg.pc = instr.getStart();
		reg.set(29, 0x7FFFEFFC);
		reg.set(31, -1);
		
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
	public static void out(String s, boolean newLine) {
		if(newLine) System.out.println(s);
		else System.out.print(s);
	}
	public static void out(String s) {
		out(s, true);
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
	
	private void runToCompletion() {
		int callOut = 0;
		// -1 means program is done
		while (callOut != -1) {
			callOut = instr.run(this, false);
		}
	}
	
	public void singleStep() {
		int callOut = 0;
		boolean isDone = false;
		boolean isValid = false;
		int moveForward = 0;
		char cmd;
		while(!isDone) {
			isDone = false;
			callOut = 0;
			moveForward = 0;
			isValid = false;
			
			String instruction = getUserInput("Please type your single step instruction: ").toLowerCase();
			clrscr();
			if(instruction.length() == 0) {
				invalidCommand();
				continue;
			}
			cmd = instruction.charAt(0);
			
			if(cmd == 'p')
				isValid = pInstruction(instruction);
			else if (cmd == 'd')
				isValid = dInstruction(instruction);
			else if (cmd == 'q') {
				isDone = true;
				isValid = true;
			} else if (cmd == 'h' || cmd == '?'){
				outputHelp();
				isValid = true;
			} else if (cmd == 's') {
				if(instruction.length() <= 2) {
					moveForward = 0;
					isValid = false;
				} else {
				String instructionNum = instruction.substring(2);
					try{
						moveForward = Integer.parseInt(instructionNum);
						isValid = true;
					} catch (NumberFormatException e) {
						moveForward = 0;
						isValid = false;
					}
				}
			}
			
			if(!isValid)
				invalidCommand();
			
			int i = 0;
			while(i < moveForward && !isDone) {
				i++;
				callOut = instr.run(this, true);
				if (callOut == -1)
					isDone = true;
			}
		}
	}
	
	private void clrscr() {
		for(int i = 0; i < 100; i++) {
			out("\n");
		}
	}
	
	private boolean dInstruction(String instruction) {
		if(instruction.length() <= 2) return false;
		String[] parts = instruction.split("\\s+");
		if(parts.length < 2) return false;
		int location = 0;
		String address = parts[parts.length-1];
		boolean inputHex = false;
		int output = 0; //0 is decimal, 1 is binary, 2 is hex;
		for(int i = 1; i < parts.length-1; i++) {
			if(parts[i].equals("-h"))
				inputHex = true;
			if(parts[i].equals("-oh"))
				output = 2;
			if(parts[i].equals("-ob"))
				output = 1;
		}
		if(address.equals("stack")) {
			this.stack.printAll();
			return true;
		}else if (address.equals("data")) {
			this.data.printAll();
			return true;
		} else if(inputHex) {
			try {
				if(address.startsWith("0x"))
					address = address.substring(2);
				location = loadHex(address);
				address = "0x"+address;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			try{
				location = Integer.parseInt(address);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		String out = "";
		if(output == 0)
			out = getFromMemory(location, 0) + "";
		else if (output == 1)
			out = Integer.toBinaryString(getFromMemory(location, 0));
		else if (output == 2)
			out = "0x"+MIPSEmulator.formatHex(getFromMemory(location, 0));
		System.out.println("MEM[ " + address + " ] = " + out);
		return true;
	}
	
	private boolean pInstruction(String instruction) {
		if(instruction.length() <= 2) return false;
		String spec = instruction.substring(2);
		int loc = 0;
		boolean isInt = true;
		try{
			loc = Integer.parseInt(spec);
		} catch (NumberFormatException e) {
			isInt = false;
		}
		if (spec.equals("all"))
			reg.printAll();
		else if( isInt || spec.equals("hi") || spec.equals("lo") || spec.equals("pc"))
			if (isInt) reg.printReg(loc);
			else reg.printReg(spec);
		else
			return false;
		return true;
	}
	private void outputHelp() {
		out("p [#/HI/LO/PC/all] - print registers either specific #, hi, lo, pc, or all registers");
		out("d [#/data/stack] - print memory at specific location, default takes a decimal int\n\t-h : take in hex\n\t-oh : output hex\n\t-ob : output binary");
		out("s # - execute next # instructions (# is decimal)");
		out("q - quit");
	}
	private void invalidCommand() {
		out("Sorry you typed in an invalid command");
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
					int loc = loadHex(nums[0].substring(2));
					int val = loadHex(nums[1].substring(2));
					this.setMemory(loc, 0, val);
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
		if(start >= 0x10010000 && start <= 0x10010000 + 4*1024)
			return data.get(start+offset);
		if(start <= 0x7FFFEFFF && start >= 0x7FFFEFFF-2*1024)
			return stack.get(start-offset);
		if(start >= 0x00400000 && start <= 0x00400000 + 2*1024)
			return instr.get(start+offset);
		return 0;
	}
	public void setMemory(int start,int offset, int rt) {
		if(start >= 0x10010000 && start <= 0x10010000 + 4*1024)
			this.data.set(start+offset, rt);
		if(start <= 0x7FFFEFFF && start >= 0x7FFFEFFF-2*1024)
			this.stack.set(start-offset, rt);
		if(start >= 0x00400000 && start <= 0x00400000 + 2*1024)
			this.instr.set(start+offset, rt);
	}
	public void setMemoryByte(int start, int offset, int val) {
		val = Memory.getBitsFromVal(val, 0, 7, false);
		int word = getFromMemory(start+offset, 0);
		int byteOffset= (start+offset) % 4;
		word = (Memory.getBitsFromVal(word, 31-8*byteOffset, 31, false) << (31-8*byteOffset)) | (Memory.getBitsFromVal(word, 0, 7*(3-byteOffset), false));
		//word = Memory.getBitsFromVal(word, 8, 31, false) << 8;
		int newVal = word | (val<<(8*(3-byteOffset)));
		this.setMemory(start+offset, 0, newVal);
	}
	public int getMemoryByte(int start, int offset, boolean signed) {
		int word = getFromMemory(start, offset);
		int byteOffset = (start+offset) % 4;
		int val = Memory.getBitsFromVal(word,24-8*byteOffset,31-8*byteOffset, signed);
		return val;
	}
	public static int loadHex(String s) throws NumberFormatException {
		int full = 0;
		for(int i = 0; i < s.length(); i++) {
			int t = Integer.parseInt(s.charAt(i) + "", 16);
			full = full << 4;
			full = full | t;
		}
		return full;
	}
	public static String formatHex(int n) {
		String s = Integer.toHexString(n).toUpperCase();
		while(s.length() < 8) {
			s = "0"+s;
		}
		return s;
			
	}
	public static void main(String[] args) {
		// If running from command prompt, can run by: 
		// java MIPSEmulator a.in
		// Otherwise will ask for a filename
		String s = "";
		if(args.length > 0)
			s = args[0];
		MIPSEmulator m = new MIPSEmulator(s);
	}
}
