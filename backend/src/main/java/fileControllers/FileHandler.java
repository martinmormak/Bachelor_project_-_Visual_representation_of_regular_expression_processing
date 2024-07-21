package fileControllers;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHandler {
    public static void writeToFile(String fileName, String content) {
        try {
            // Get the directory path from the full file path
            String directoryPath = new File(fileName).getParent();

            // Create the directory if it doesn't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                if (directory.mkdirs()) {
                    System.out.println("Directory created: " + directoryPath);
                } else {
                    System.err.println("Failed to create directory: " + directoryPath);
                    return; // Don't proceed with writing the file if directory creation fails
                }
            }

            // Now proceed to write the file
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDirectory(String sourcePath, String zipFilePath) {
        try {
            File sourceDirectory = new File(sourcePath);
            FileOutputStream fileOutputStream = new FileOutputStream(zipFilePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            zipDirectoryContents(sourceDirectory, sourceDirectory.getName(), zipOutputStream);
            zipOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipDirectoryContents(File directory, String path, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectoryContents(file, path + "/" + file.getName(), zipOut);
                } else {
                    zipFile(file, path + "/" + file.getName(), zipOut);
                }
            }
        }
    }

    private static void zipFile(File fileToZip, String entryName, ZipOutputStream zipOut) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }
}