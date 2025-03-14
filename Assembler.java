
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Assembler {
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Error: Example usage.\n\t[sourceFileName assemblyFileName]");
            return;
        }
        try{
            FileReader fileReader = new FileReader(args[0]);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            File outputFile = new File("output.hex"); // digitalLogicDesign/Assembler/
            FileWriter fileWriter = new FileWriter(outputFile);

            String line = bufferedReader.readLine();
            
            String hexInstruction = "";
            while(line != null){
                
                String elements[] = line.toLowerCase().split("[\\s,]+"); // split with empty character and comma
                String opcode = elements[0];
                switch (opcode){
                    case "add" -> hexInstruction = convertAssemblyToHex1(1, elements[1], elements[2], 0, elements[3]);
                    case "addi" -> hexInstruction = convertAssemblyToHex1(1, elements[1], elements[2], 1, elements[3]);
                    case "or" -> hexInstruction = convertAssemblyToHex1(2, elements[1], elements[2], 0, elements[3]);
                    case "ori" -> hexInstruction = convertAssemblyToHex1(2, elements[1], elements[2], 1, elements[3]);
                    case "nand" -> hexInstruction = convertAssemblyToHex1(3, elements[1], elements[2], 0, elements[3]);
                    case "nandi" -> hexInstruction = convertAssemblyToHex1(3, elements[1], elements[2], 1, elements[3]);
                    case "sub" -> hexInstruction = convertAssemblyToHex1(4, elements[1], elements[2], 0, elements[3]);
                    case "subi" -> hexInstruction = convertAssemblyToHex1(4, elements[1], elements[2], 1, elements[3]);
                    case "sll" -> hexInstruction = convertAssemblyToHex1(5, elements[1], elements[2], 0, elements[3]);
                    case "slli" -> hexInstruction = convertAssemblyToHex1(5, elements[1], elements[2], 1, elements[3]);
                    case "ld" -> hexInstruction = convertAssemblyToHex2(6, elements[1], elements[2]);
                    case "st" -> hexInstruction = convertAssemblyToHex2(6, elements[1], elements[2]);
                    case "jump" -> hexInstruction = convertJumpToHex(8, elements[1]);
                    case "beq" -> hexInstruction = convertAssemblyToHex3(9, "010", elements[1], elements[2], elements[3]); // n, z, p binary values will be 0, 1, 0
                    case "blt" -> hexInstruction = convertAssemblyToHex3(10, "100", elements[1], elements[2], elements[3]); // n, z, p binary values will be 1, 0, 0
                    case "bgt" -> hexInstruction = convertAssemblyToHex3(11, "001", elements[1], elements[2], elements[3]); // n, z, p binary values will be 0, 0, 1.
                    case "ble" -> hexInstruction = convertAssemblyToHex3(12, "110", elements[1], elements[2], elements[3]); //n, z, p binary values will be 1, 1, 0
                    case "bge" -> hexInstruction = convertAssemblyToHex3(13, "011", elements[1], elements[2], elements[3]); // n, z, p binary values will be 0, 1, 1
                }
                fileWriter.append(hexInstruction.toUpperCase() + " ");
                line = bufferedReader.readLine();
            }
            fileWriter.close();
            bufferedReader.close();
        }catch (FileNotFoundException e){
            System.err.println("File is not found in the given location.");
        }catch(IOException e){
            System.err.println("IO exception while reading the file.");
        }
    
    }

    // For arithmetic operations
    public static String convertAssemblyToHex1(int opcodeNumber, String destStr, String sourceStr1, int distincter,
            String sourceStr2) {
        // sourceStr2 may also be a immediate value
        int destNumber = extractInteger(destStr);
        int sourceNumber1 = extractInteger(sourceStr1);
        int sourceNumber2 = extractInteger(sourceStr2);

        String hexVersion = Integer.toHexString(opcodeNumber) +
                Integer.toHexString(destNumber) +
                Integer.toHexString(sourceNumber1);

        if (distincter == 0) {
            hexVersion += "0" +
                    Integer.toHexString(sourceNumber2);
        } else {                                                // distincter == 1
            String imm = Integer.toBinaryString(sourceNumber2);
            String sign = (sourceNumber2 < 0) ? "1" : "0";

            while (imm.length() != 7) {
                imm = sign + imm;
            }
            imm = "1" + imm;

            hexVersion += toHex(imm);
        }
        return hexVersion;
    }

    // For loading and storing operations
    public static String convertAssemblyToHex2(int opcodeNumber, String destStr, String address) {
        int destNumber = extractInteger(destStr);
        String hexVersion = Integer.toHexString(opcodeNumber) +
                Integer.toHexString(destNumber);

        int addressNumber = extractInteger(address);
        String addressHex = Integer.toBinaryString(addressNumber);
        while (addressHex.length() != 12) {
            addressHex = "0" + addressHex;
        }
        hexVersion += toHex(addressHex);
        return hexVersion;
    }

    // For JUMP operation
    public static String convertJumpToHex(int opcodeNumber, String address) {
        String hexVersion = Integer.toHexString(opcodeNumber);

        int addressNumber = extractInteger(address);
        String addressHex = Integer.toHexString(addressNumber);
        while (addressHex.length() != 16) {
            addressHex = "0" + addressHex;
        }
        hexVersion += toHex(addressHex);
        return hexVersion;
    }

    // For branching operations
    public static String convertAssemblyToHex3(int opcodeNumber, String nzp, String op1, String op2, String address) {
        String hexVersion = Integer.toHexString(opcodeNumber);

        int op1Number = extractInteger(op1);
        String op1Hex = Integer.toBinaryString(op1Number);
        while(op1Hex.length() != 4){
            op1Hex = "0" + op1Hex;
        }

        int op2Number = extractInteger(op2);
        String op2Hex = Integer.toBinaryString(op2Number);
        while(op2Hex.length() != 4){
            op2Hex = "0" + op2Hex;
        }

        int addressNumber = extractInteger(address);
        String addressHex = Integer.toBinaryString(addressNumber);
        while(addressHex.length() != 5){
            addressHex = "0" + addressHex;
        }
        String str = nzp + op1Hex + op2Hex + addressHex;
        hexVersion += toHex(str);
        return hexVersion;
    }

    public static String toHex(String str){
        int times = str.length() / 4;
        String hexNumber = "";
        for(int i = 0; i < times; i++){
            String strNum = str.substring(i * 4, (i + 1) * 4);
            int num = binToInt(strNum);// this should be converted to integer first
            hexNumber += Integer.toHexString(num);
        }
        return hexNumber;
    }

    public static int extractInteger(String str){
        int number = 0;
        int size = str.length();
        for(int i = 0; i < size; i++){
            char ch = str.charAt(i);
            if(ch >= '0' && ch <= '9'){
                while(i < size){
                    ch = str.charAt(i);
                    number *= 10;
                    number += ch - '0';
                    i++;
                }
                break;
            }
        }
        return number;
    }

    // convert binary number to integer
    public static int binToInt(String str){
        int size = str.length();
        int result = 0;
        for(int i = 0; i < size; i++){
            int num = str.charAt(i) - '0';
            result *= 2;
            result += num;
        }
        return result;
    }
}
