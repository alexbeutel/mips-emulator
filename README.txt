MIPS Emulator
CS 104 - Duke University Spring 2009
Alex Beutel and Nikhil Arun

How to run:
To run this project, simply type:

	java MIPSEmulator
	
OR

	java MIPSEmulator [filename]

where filename is the name of the file containing the SPIM output (hexadecimal encoding of the instructions).  If you run the first command you will be asked for the file location in the program.  You will then be asked if you prefer single step mode or to run the program to completion.  In single step mode enter 'h' for help and to see a list of possible commands.  We have expanded upon the required commands as it was useful for debugging.

Principles:
The project tried to keep fairly traditionally to OOP standards with the exception of not adhering to strict scope rules.  The main class is MIPSEmulator which contains all memory objects and registers; functions in this class are generally ones that run across multiple objects.  This includes accessing and setting memory, which can be in either in the data segment, stack, or instructions.  The other main function of this class is I/O which includes the initial parsing of the hex file and then the user commands for single step mode.  Last the function has some useful static methods such as formatting an integer for output as hex and reading in a hex string.  Other than these functions, most of the other work is delegated to the Memory class and the TextSegment class.  The Memory class contains the real structure of the program as this is where most of the complexity is.  The memory class is essentially an abstract class, as it is never instantiated, although it is robust enough to be used as a memory segment.  Aside from holding the actual values for the memory, the memory class contains accessor and mutator methods, and extremely useful functions for getting specific ranges of bits from a value.  TextSegment extends the memory class and contains the function run() which is where the execution of the MIPS commands take place.  Other than this DataSegment and StackSegment are merely specific subclasses of Memory and Register just contains all the registers with appropriate accessors, mutators, and print functions.

Division of work:
