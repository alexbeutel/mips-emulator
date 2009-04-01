import java.util.*;
public class MIPSEmulator {
	static Scanner in;
	private Register reg;
	private DataSegment data;
	private TextSegment instr;
	private StackSegment stack;
	private int pc;
	public MIPSEmulator() {
		reg = new Register();
		data = new DataSegment();
		instr = new TextSegment();
		stack = new StackSegment();
		in = new Scanner(System.in);
		loadFile();
	}
	
	//Load instructions and static data from file into data and instr objects
	public void loadFile() {
		String line = "";
		int i = 0;
		boolean hasStartedStaticData = false;
		while(line == in.nextLine()) {
			if(line.equals("DATA SEGMENT")) {
				hasStartedStaticData = true;
			} else if(!line.startsWith("0x")) {
				System.out.println("ERROR on line "+ i + ": " + line);
			} else {
				if(hasStartedStaticData) {
					String[] nums = line.split(" ");
					line = nums[1].substring(2);
					int val = Integer.parseInt(line, 16);
					data.add((Integer)val);
				} else {
					line = line.substring(2);
					int val = Integer.parseInt(line, 16);
					instr.add((Integer)val);
				}
			}
			i++;
		}
	}
	
	public static void main(String[] args) {
		MIPSEmulator em = new MIPSEmulator();
		for(int i = 0; i < 10; i++) {
			System.out.println(i+".");
		}

	}

}
