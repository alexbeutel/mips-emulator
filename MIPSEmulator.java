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
				String instructionNum = instruction.substring(2);
				try{
					moveForward = Integer.parseInt(instructionNum);
					isValid = true;
				} catch (NumberFormatException e) {
					moveForward = 0;
					isValid = false;
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
			/*
			//iterate as many times as specified (once unless otherwise specified)
			for (int i = 0; i < moveForward; i++) {
				callOut = instr.run(this, true);
				if (callOut == -1) {
					isDone = true;
					break;
				}
			}
			*/
		}
	}
	
	private void clrscr() {
		for(int i = 0; i < 100; i++) {
			out("\n");
		}
	}
	
	private boolean dInstruction(String instruction) {
		String address = instruction.substring(2);
		int location = 0;
		try{
			location = loadHex(address);
		} catch (NumberFormatException e) {
			return false;
		}
		System.out.println("MEM[ " + location + " ] = " + getFromMemory(location, 0));
		return true;
	}
	
	private boolean pInstruction(String instruction) {
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
		out("d # - print memory at specific location");
		out("s # - execute next # instructions");
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
	public static int loadHex(String s) throws NumberFormatException {
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
		String s = "";
		if(args.length > 0)
			s = args[0];
		MIPSEmulator m = new MIPSEmulator(s);
	}
}
