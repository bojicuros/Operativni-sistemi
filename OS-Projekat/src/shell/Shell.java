package shell;

import java.io.File;

import assembler.Constants;
import assembler.Operations;
import kernel.Process;
import kernel.ProcessState;

public class Shell {

	public static File workingDirectory;
	public static int PC; // Program counter
	public static String IR; // Instruction register

	public static void main(String[] args) {
//		long startTime = System.currentTimeMillis();
//		while (System.currentTimeMillis() - startTime < 2) {
//			int d = 5;
//			System.out.println(d);
//		}
	}

	public static void executeProcess(Process process) {
		int startAdress = process.loadIntoRAM();
		process.setStartAdress(startAdress);
		PC = process.getStartAdress();
		int temp = 0; // RAM.getI(PC)
		String instruction = fromIntToInstruction(temp);
		IR = instruction;
		executeMachineInstruction(IR, process);
	}

	public static String asemblerToMachineInstruction(String command) {
		String instruction = "";
		String arr[] = command.split("[ |,]");

		// prevodjenje operacije
		switch (arr[0]) {
		case "MOV":
			instruction += Operations.mov;
			break;
		case "HALT":
			instruction += Operations.halt;
			break;
		case "STORE":
			instruction += Operations.store;
			break;
		case "ADD":
			instruction += Operations.add;
			break;
		case "SUB":
			instruction += Operations.sub;
			break;
		case "MUL":
			instruction += Operations.mul;
			break;
		case "JMP":
			instruction += Operations.jmp;
			break;
		case "JMPL":
			instruction += Operations.jmpl;
			break;
		case "JMPG":
			instruction += Operations.jmpg;
			break;
		case "JMPE":
			instruction += Operations.jmpe;
			break;
		case "LOAD":
			instruction += Operations.load;
			break;
		}

		if (arr[0].equals("HALT")) {
			return instruction;
		} else if (arr[0].equals("JMP")) { // npr: JMP 5(adr)
			instruction += toBinary(arr[1]);
			return instruction;
		} else if (arr[0].equals("JMPL") || arr[0].equals("JMPG") || arr[0].equals("JMPE")) { // npr.: JMPL
																						// R1,1(value),6(adr)
			switch (arr[1]) { // +registar
			case "R1":
				instruction += Constants.R1;
				break;
			case "R2":
				instruction += Constants.R2;
				break;
			case "R3":
				instruction += Constants.R3;
				break;
			case "R4":
				instruction += Constants.R4;
				break;
			case "R5":
				instruction += Constants.R5;
				break;
			}
			instruction += toBinary(arr[2]); // +vrijendost
			instruction += toBinary(arr[3]); // +adresa
			return instruction;
		} else if (arr[2].equals("R1") || arr[2].equals("R2") || arr[2].equals("R3") || arr[2].equals("R4")
				|| arr[2].equals("R5")) { // ako su oba argumenta registri (MOV,ADD,SUB,MUL)
			switch (arr[1]) {
			case "R1":
				instruction += Constants.R1;
				break;
			case "R2":
				instruction += Constants.R2;
				break;
			case "R3":
				instruction += Constants.R3;
				break;
			case "R4":
				instruction += Constants.R4;
				break;
			case "R5":
				instruction += Constants.R5;
				break;
			}
			switch (arr[2]) {
			case "R1":
				instruction += Constants.R1;
				break;
			case "R2":
				instruction += Constants.R2;
				break;
			case "R3":
				instruction += Constants.R3;
				break;
			case "R4":
				instruction += Constants.R4;
				break;
			case "R5":
				instruction += Constants.R5;
				break;
			}
			return instruction;
		} else {
			switch (arr[1]) { // +registar
			case "R1":
				instruction += Constants.R1;
				break;
			case "R2":
				instruction += Constants.R2;
				break;
			case "R3":
				instruction += Constants.R3;
				break;
			case "R4":
				instruction += Constants.R4;
				break;
			case "R5":
				instruction += Constants.R5;
				break;
			}
			instruction += toBinary(arr[2]); // +vrijednost
			return instruction;
		}

	}

	private static String toBinary(String s) {
		int num = Integer.parseInt(s);
		return Integer.toBinaryString(num);
	}

	public static void executeMachineInstruction(String command, Process process) {
		String operation = command.substring(0, 4);

		if (operation == Operations.halt) {
			process.setState(ProcessState.TERMINATED);
		} else if (operation == Operations.mov) {
			String r1 = command.substring(4, 8);
			String r2 = command.substring(8, 12);
			Operations.mov(r1, r2);
		} else if (operation == Operations.store) {
			String r1 = command.substring(4, 8);
			String val2 = command.substring(8, 16);
			Operations.store(r1, val2);
		} else if (operation == Operations.add) {
			if (command.length() == 12) { // oba registra
				String r1 = command.substring(4, 8);
				String r2 = command.substring(8, 12);
				Operations.add(r1, r2);
			} else if (command.length() == 16) { // registar pa vrijednost
				String r1 = command.substring(4, 8);
				String val2 = command.substring(8, 16);
				Operations.add(r1, val2);
			}
		} else if (operation == Operations.sub) {
			if (command.length() == 12) { // oba registra
				String r1 = command.substring(4, 8);
				String r2 = command.substring(8, 12);
				Operations.sub(r1, r2);
			} else if (command.length() == 16) { // registar pa vrijednost
				String r1 = command.substring(4, 8);
				String val2 = command.substring(8, 16);
				Operations.sub(r1, val2);
			}
		} else if (operation == Operations.mul) {
			if (command.length() == 12) { // oba registra
				String r1 = command.substring(4, 8);
				String r2 = command.substring(8, 12);
				Operations.mul(r1, r2);
			} else if (command.length() == 16) { // registar pa vrijednost
				String r1 = command.substring(4, 8);
				String val2 = command.substring(8, 16);
				Operations.mul(r1, val2);
			}
		} else if (operation == Operations.jmp) {
			String adr = command.substring(4, 12);
			Operations.jmp(adr);
		} else if (operation == Operations.jmpl) {
			String reg = command.substring(4, 8);
			String val = command.substring(8, 16);
			String adr = command.substring(16, 24);
			Operations.jmpl(reg, val, adr);
		} else if (operation == Operations.jmpg) {
			String reg = command.substring(4, 8);
			String val = command.substring(8, 16);
			String adr = command.substring(16, 24);
			Operations.jmpg(reg, val, adr);
		} else if (operation == Operations.jmpe) {
			String reg = command.substring(4, 8);
			String val = command.substring(8, 16);
			String adr = command.substring(16, 24);
			Operations.jmpe(reg, val, adr);
		} else if (operation == Operations.load) {
			String r1 = command.substring(4, 8);
			String adr = command.substring(8, 16);
			Operations.load(r1, adr);
		}

	}

	private static String fromIntToInstruction(int temp) {
		String help = toBinary(temp + "");
		if (temp == 0)
			help = "0000";
		else if (help.length() <= 12) {
			while (help.length() < 12)
				help = "0" + help;
		} else if (help.length() <= 16) {
			while (help.length() < 16)
				help = "0" + help;
		} else if (help.length() <= 24) {
			while (help.length() < 24)
				help = "0" + help;
		}
		return help;
	}

	public static void saveValues(Process p) {
		int[] registers = { Operations.R1.value, Operations.R2.value, Operations.R3.value, Operations.R4.value,
				Operations.R5.value };
		p.setValuesOfRegisters(registers);
		p.setPcValue(PC);
	}

	public static void loadValues(Process p) {
		int[] registers = p.getValuesOfRegisters();
		Operations.R1.value = registers[0];
		Operations.R2.value = registers[1];
		Operations.R3.value = registers[2];
		Operations.R4.value = registers[3];
		Operations.R5.value = registers[4];
		PC = p.getPcValue();
	}

}
