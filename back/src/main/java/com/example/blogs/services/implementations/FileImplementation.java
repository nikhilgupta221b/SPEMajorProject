package com.example.blogs.services.implementations;

import com.example.blogs.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileImplementation implements FileService {

    private static final Logger logger = LogManager.getLogger(FileImplementation.class);

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        logger.info("Uploading image to path: {}", path);
        String name = file.getOriginalFilename();
        String randomID = UUID.randomUUID().toString();
        String fileName1 = randomID.concat(name.substring(name.lastIndexOf(".")));

        String filePath = path + File.separator + fileName1;

        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
            logger.info("Created directory: {}", path);
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));
        logger.info("Image uploaded successfully with file name: {}", fileName1);
        return fileName1;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        logger.info("Fetching resource from path: {} with file name: {}", path, fileName);
        String fullPath = path + File.separator + fileName;
        InputStream is = new FileInputStream(fullPath);
        logger.info("Resource fetched successfully from path: {}", fullPath);
        return is;
    }
}
