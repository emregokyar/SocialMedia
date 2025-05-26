package com.socialmedia.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileDeleteUtil { //Don't forget use this when someone is deleting photo or user account
    public static void deleteFile(String directory) throws IOException {
        Path directoryPath = Paths.get(directory);
        if (!Files.exists(directoryPath)) {
            return;
        }
        Files.delete(directoryPath);
    }
}
