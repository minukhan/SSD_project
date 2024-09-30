package TEST;

import SSD.VirtualSSD;

import java.io.*;
import java.util.Scanner;

public class TestShell {

    // 명령어를 처리할 메인 메서드
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("shell> ");
            String command = scanner.nextLine();
            String[] commandParts = command.split(" ");

            try {
                switch (commandParts[0].toLowerCase()) {
                    case "write":
                        handleWrite(commandParts);
                        break;

                    case "read":
                        handleRead(commandParts);
                        break;

                    case "fullwrite":
                        handleFullWrite(commandParts);
                        break;

                    case "fullread":
                        handleFullRead();
                        break;

                    case "exit":
                        System.out.println("Exiting the shell...");
                        running = false;
                        break;

                    case "help":
                        printHelp();
                        break;

                    default:
                        System.out.println("INVALID COMMAND");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    // write 명령어 처리
    private static void handleWrite(String[] commandParts) throws IOException {
        if (commandParts.length != 3) {
            System.out.println("Usage: write [LBA] [value]");
            return;
        }

        int lba;
        try {
            lba = Integer.parseInt(commandParts[1]);
            if (!isValidLBA(lba)) {
                throw new IllegalArgumentException("Invalid LBA address.");
            }
        } catch (NumberFormatException e) {
            System.out.println("LBA should be an integer between 0 and 99.");
            return;
        }

        String value = commandParts[2];
        if (!isValidHex(value)) {
            System.out.println("Value must be a valid 8-character hexadecimal (e.g., 0xAAAABBBB).");
            return;
        }

        // ssd 프로그램에 명령어 전달 (SSD.VirtualSSD 클래스 사용)
        VirtualSSD.writeToSSD(lba, value);
    }

    // read 명령어 처리
    private static void handleRead(String[] commandParts) throws IOException {
        if (commandParts.length != 2) {
            System.out.println("Usage: read [LBA]");
            return;
        }

        int lba;
        try {
            lba = Integer.parseInt(commandParts[1]);
            if (!isValidLBA(lba)) {
                throw new IllegalArgumentException("Invalid LBA address.");
            }
        } catch (NumberFormatException e) {
            System.out.println("LBA should be an integer between 0 and 99.");
            return;
        }

        // ssd 프로그램에 명령어 전달 (SSD.VirtualSSD 클래스 사용)
        VirtualSSD.readFromSSD(lba);

        // 결과 출력
        BufferedReader reader = new BufferedReader(new FileReader("result.txt"));
        String result = reader.readLine();
        reader.close();

        System.out.println("LBA " + lba + ": " + result);
    }

    // fullwrite 명령어 처리
    private static void handleFullWrite(String[] commandParts) throws IOException {
        if (commandParts.length != 2) {
            System.out.println("Usage: fullwrite [value]");
            return;
        }

        String value = commandParts[1];
        if (!isValidHex(value)) {
            System.out.println("Value must be a valid 8-character hexadecimal (e.g., 0xAAAABBBB).");
            return;
        }

        for (int lba = 0; lba < 100; lba++) {
            VirtualSSD.writeToSSD(lba, value);
        }

        System.out.println("Full write complete.");
    }

    // fullread 명령어 처리
    private static void handleFullRead() throws IOException {
        for (int lba = 0; lba < 100; lba++) {
            VirtualSSD.readFromSSD(lba);

            // 결과 출력
            BufferedReader reader = new BufferedReader(new FileReader("result.txt"));
            String result = reader.readLine();
            reader.close();

            System.out.println("LBA " + lba + ": " + result);
        }
    }

    // LBA 유효성 검사
    private static boolean isValidLBA(int lba) {
        return lba >= 0 && lba < 100;
    }

    // 16진수 값 유효성 검사 (0x로 시작하고 8자리)
    private static boolean isValidHex(String value) {
        return value.matches("^0x[0-9A-Fa-f]{8}$");
    }

    // 명령어 도움말 출력
    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  write [LBA] [value]     - Write value to specific LBA (e.g., write 3 0xAAAABBBB)");
        System.out.println("  read [LBA]              - Read value from specific LBA (e.g., read 3)");
        System.out.println("  fullwrite [value]       - Write value to all LBAs (e.g., fullwrite 0xAAAABBBB)");
        System.out.println("  fullread                - Read values from all LBAs");
        System.out.println("  exit                    - Exit the shell");
        System.out.println("  help                    - Show this help message");
    }
}
