import java.util.*;
import java.io.*;
/**
 * Main class for holding all objects, I/O, and giving directions
 *
 */
public class MIPSEmulator {
	public Register reg;
	public DataSegment data;
	public TextSegment instr;
	public StackSegment stack;
	
	//Constructor - all executed during object instantiation
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
		reg.set(29, 0x7FFFEFFC); //initialize $sp
		reg.set(31, -1); // $ra so as to know when to end the program (on jr -1)
		reg.set(28, 0x10011000); // set $gp
		reg.set(30, 0x7FFFEFFC); // set $fp (not sure if right but never really used)
		
		
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
	
	/**
	 * Helper function to output to command line
	 * @param	s		Message to be output
	 * @param	newLine	Whether or not to use print or println
	 */
	public static void out(String s, boolean newLine) {
		if(newLine) System.out.println(s);
		else System.out.print(s);
	}
	/**
	 * Overloaded function for out, assumes that we want to print a new line
	 * @param s
	 */
	public static void out(String s) {
		out(s, true);
	}
	
	/**
	 * Static function to output a message and wait for user input
	 * @param message message to output before user input
	 * @return the user inputed string
	 */
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
	
	/**
	 * Run all commands loaded into MIPS until program is over
	 * Do not display commands as they are run
	 */
	private void runToCompletion() {
		int callOut = 0;
		// -1 means program is done
		while (callOut != -1) {
			callOut = instr.run(this, false);
		}
	}
	/**
	 * Run MIPS Emulator in single step mode
	 * Display commands as they are run, among other features
	 * Type h (for help) in this mode to see details of commands
	 */
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
	/**
	 * Command used to keep screen cleared except for current and last command.
	 */
	private void clrscr() {
		for(int i = 0; i < 100; i++) {
			out("\n");
		}
	}
	/**
	 * Parse instruction input if it is command involving memory
	 * @param instruction input from user
	 * @return boolean for if there was an error in the command
	 */
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
	
	/**
	 * Parse instruction input if it is command involving registers
	 * @param instruction input from user
	 * @return boolean for if there was an error in the command
	 */
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
	/**
	 * Display command help
	 */
	private void outputHelp() {
		out("p [#/HI/LO/PC/all] - print registers either specific #, hi, lo, pc, or all registers");
		out("d [#/data/stack] - print memory at specific location, default takes a decimal int\n\t-h : take in hex\n\t-oh : output hex\n\t-ob : output binary");
		out("s # - execute next # instructions (# is decimal)");
		out("q - quit");
	}
	/**
	 * Output message if error in user inputed command
	 */
	private void invalidCommand() {
		out("Sorry you typed in an invalid command");
	}	
	
	/**
	 * Load instructions and static data from file into data and instr objects
	 * @param filename file to read for hexadecimal formatted commands and data
	 * @throws IOException throws exception in case of error reading the file
	 */
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
	/**
	 * Get a value from memory
	 * Chooses which block of memory to read from based on memory address
	 * @param start Location in memory from which to offset from
	 * @param offset Offset in memory
	 * @return 32 bit (4 bytes) integer read from memory
	 */
	public int getFromMemory(int start, int offset) {
		if(start >= 0x10010000 && start <= 0x10010000 + 4*1024)
			return data.get(start+offset);
		if(start <= 0x7FFFEFFF && start >= 0x7FFFEFFF-2*1024)
			return stack.get(start+offset);
		if(start >= 0x00400000 && start <= 0x00400000 + 2*1024)
			return instr.get(start+offset);
		return 0;
	}
	/**
	 * Set one word, 4 bytes, in memory
	 * @param start Location in memory from which to offset from
	 * @param offset Offset in memory
	 * @param rt 32 bit (4 bytes) integer to set in memory
	 */
	public void setMemory(int start,int offset, int rt) {
		if(start >= 0x10010000 && start <= 0x10010000 + 4*1024)
			this.data.set(start+offset, rt);
		if(start <= 0x7FFFEFFF && start >= 0x7FFFEFFF-2*1024)
			this.stack.set(start+offset, rt);
		if(start >= 0x00400000 && start <= 0x00400000 + 2*1024)
			this.instr.set(start+offset, rt);
	}
	/**
	 * Set a byte of memory to given value
	 * @param start Location in memory to set
	 * @param offset Offset from the start location
	 * @param val 8 bit value to be saved
	 */
	public void setMemoryByte(int start, int offset, int val) {
		val = Memory.getBitsFromVal(val, 0, 7, false);
		int word = getFromMemory(start+offset, 0);
		int byteOffset= (start+offset) % 4;
		word = (Memory.getBitsFromVal(word, 31-8*byteOffset, 31, false) << (31-8*byteOffset)) | (Memory.getBitsFromVal(word, 0, 7*(3-byteOffset), false));
		//word = Memory.getBitsFromVal(word, 8, 31, false) << 8;
		int newVal = word | (val<<(8*(3-byteOffset)));
		this.setMemory(start+offset, 0, newVal);
	}
	/**
	 * Read 8 bits from memory
	 * @param start Memory location
	 * @param offset Optional offset from memory location
	 * @param signed Take the byte as a signed or unsigned value
	 * @return the value at the given location byte is returned as an int
	 */
	public int getMemoryByte(int start, int offset, boolean signed) {
		int word = getFromMemory(start, offset);
		int byteOffset = (start+offset) % 4;
		int val = Memory.getBitsFromVal(word,24-8*byteOffset,31-8*byteOffset, signed);
		return val;
	}
	/**
	 * Read a hexidecimal string
	 * Can't use parseInt because it fails for 8 character values
	 * @param s String to be parsed
	 * @return int value from inputed hexadecimal string
	 * @throws NumberFormatException throws exception if not a true hex string
	 */
	public static int loadHex(String s) throws NumberFormatException {
		int full = 0;
		for(int i = 0; i < s.length(); i++) {
			int t = Integer.parseInt(s.charAt(i) + "", 16);
			full = full << 4;
			full = full | t;
		}
		return full;
	}
	/**
	 * Format an int to be output as hexidecimal
	 * similar to Integer.toHexString() but always 8 characters long
	 * @param n 32 bit integer to be output as hex
	 * @return a string of 8 uppercase hexadecimal characters
	 */
	public static String formatHex(int n) {
		String s = Integer.toHexString(n).toUpperCase();
		while(s.length() < 8) {
			s = "0"+s;
		}
		return s;
			
	}
	/**
	 * If running from command prompt, can run by: 
	 * java MIPSEmulator a.in
	 * Otherwise will ask for a filename
	 * @param args optional first argument can be the filename to bread
	 */
	public static void main(String[] args) {
		String s = "";
		if(args.length > 0)
			s = args[0];
		MIPSEmulator m = new MIPSEmulator(s);
	}
}
