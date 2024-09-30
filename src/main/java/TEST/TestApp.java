package TEST;

import SSD.VirtualSSD;

import java.io.*;

public class TestApp {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            boolean running = true;

            while (running) {
                System.out.print("shell> ");
                String command = reader.readLine();

                switch (command.toLowerCase()) {
                    case "testapp1":
                        runTestApp1();
                        break;
                    case "testapp2":
                        runTestApp2();
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
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // TestApp1: fullwrite 수행 후 fullread로 검증
    private static void runTestApp1() throws IOException {
        String testValue = "0xABCDEF12";  // 테스트에 사용할 값
        System.out.println("Starting TestApp1...");

        // fullwrite 수행
        for (int lba = 0; lba < 100; lba++) {
            VirtualSSD.writeToSSD(lba, testValue);
        }
        System.out.println("Fullwrite completed.");

        // fullread 수행
        boolean isSuccess = true;
        for (int lba = 0; lba < 100; lba++) {
            VirtualSSD.readFromSSD(lba);
            BufferedReader reader = new BufferedReader(new FileReader("result.txt"));
            String readValue = reader.readLine();
            reader.close();

            if (!testValue.equals(readValue)) {
                System.out.println("Error at LBA " + lba + ": Expected " + testValue + " but got " + readValue);
                isSuccess = false;
            }
        }

        if (isSuccess) {
            System.out.println("TestApp1 passed. All values match.");
        } else {
            System.out.println("TestApp1 failed. Some values do not match.");
        }
    }

    // TestApp2: 0~5 LBA에 30번 AAAABBBB, 마지막으로 12345678 덮어쓰기 후 비교
    private static void runTestApp2() throws IOException {
        String initialWriteValue = "0xAAAABBBB";
        String finalWriteValue = "0x12345678";
        System.out.println("Starting TestApp2...");

        // 0~5번 LBA에 30번 초기 값 쓰기
        for (int i = 0; i < 30; i++) {
            for (int lba = 0; lba <= 5; lba++) {
                VirtualSSD.writeToSSD(lba, initialWriteValue);
            }
        }
        System.out.println("Write aging with value " + initialWriteValue + " completed.");

        // 0~5번 LBA에 1회 덮어쓰기
        for (int lba = 0; lba <= 5; lba++) {
            VirtualSSD.writeToSSD(lba, finalWriteValue);
        }
        System.out.println("Overwrite with value " + finalWriteValue + " completed.");

        // 0~5번 LBA 읽어서 확인
        boolean isSuccess = true;
        for (int lba = 0; lba <= 5; lba++) {
            VirtualSSD.readFromSSD(lba);
            BufferedReader reader = new BufferedReader(new FileReader("result.txt"));
            String readValue = reader.readLine();
            reader.close();

            if (!finalWriteValue.equals(readValue)) {
                System.out.println("Error at LBA " + lba + ": Expected " + finalWriteValue + " but got " + readValue);
                isSuccess = false;
            }
        }

        if (isSuccess) {
            System.out.println("TestApp2 passed. All values match.");
        } else {
            System.out.println("TestApp2 failed. Some values do not match.");
        }
    }

    // 명령어 도움말 출력
    private static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  testapp1                - Full write/read and compare");
        System.out.println("  testapp2                - Write aging and overwrite test");
        System.out.println("  exit                    - Exit the shell");
        System.out.println("  help                    - Show this help message");
    }
}
