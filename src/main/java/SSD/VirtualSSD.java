package SSD;

import java.io.*;
import java.util.Scanner;

public class VirtualSSD {

    private static final int LBA_COUNT = 100; // 0 ~ 99
    private static final String NAND_FILE = "nand.txt";
    private static final String RESULT_FILE = "result.txt";
    private static final String DEFAULT_VALUE = "0x00000000";

    // 프로그램 초기화 시 nand.txt 파일을 준비
    private static void initializeNandFile() throws IOException {
        File file = new File(NAND_FILE);
        if (!file.exists()) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < LBA_COUNT; i++) {
                writer.write(DEFAULT_VALUE);
                writer.newLine();
            }
            writer.close();
        }
    }

    // LBA 범위 체크
    private static boolean isValidLBA(int lba) {
        return lba >= 0 && lba < LBA_COUNT;
    }

    // Write 명령어 처리: LBA 위치에 값을 기록
    public static void writeToSSD(int lba, String value) throws IOException {
        if (!isValidLBA(lba)) {
            System.out.println("Invalid LBA address.");
            return;
        }

        // nand.txt 전체를 읽어와서 메모리에 저장한 후 특정 위치를 업데이트
        BufferedReader reader = new BufferedReader(new FileReader(NAND_FILE));
        String[] nandData = new String[LBA_COUNT];

        for (int i = 0; i < LBA_COUNT; i++) {
            nandData[i] = reader.readLine();
        }
        reader.close();

        // LBA 값 업데이트
        nandData[lba] = value;

        // nand.txt 파일을 새로 작성하여 변경된 내용 저장
        BufferedWriter writer = new BufferedWriter(new FileWriter(NAND_FILE));
        for (int i = 0; i < LBA_COUNT; i++) {
            writer.write(nandData[i]);
            writer.newLine();
        }
        writer.close();
    }

    // Read 명령어 처리: LBA 위치에서 값을 읽음
    public static void readFromSSD(int lba) throws IOException {
        if (!isValidLBA(lba)) {
            System.out.println("Invalid LBA address.");
            return;
        }

        // nand.txt 파일을 읽어와서 특정 LBA의 값을 result.txt에 기록
        BufferedReader reader = new BufferedReader(new FileReader(NAND_FILE));
        String value = DEFAULT_VALUE;

        for (int i = 0; i <= lba; i++) {
            value = reader.readLine();
        }
        reader.close();

        // result.txt 파일에 읽은 값을 기록
        BufferedWriter resultWriter = new BufferedWriter(new FileWriter(RESULT_FILE));
        resultWriter.write(value);
        resultWriter.newLine();
        resultWriter.close();

        System.out.println("Read value: " + value);
    }

    public static void main(String[] args) {
        try {
            // 초기화: nand.txt 파일 준비
            initializeNandFile();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Enter command: ");
                String command = scanner.next();

                // Write 명령 처리
                if (command.equalsIgnoreCase("W")) {
                    int lba = scanner.nextInt();
                    String value = scanner.next();
                    writeToSSD(lba, value);
                }
                // Read 명령 처리
                else if (command.equalsIgnoreCase("R")) {
                    int lba = scanner.nextInt();
                    readFromSSD(lba);
                }
                // 종료 명령 처리
                else if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting program.");
                    break;
                } else {
                    System.out.println("Invalid command. Use W (Write) or R (Read).");
                }
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
