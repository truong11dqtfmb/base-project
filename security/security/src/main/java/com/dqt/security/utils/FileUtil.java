package com.dqt.security.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Component
public class FileUtil {

    public static String generateFileName(String prefix, MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileCode = RandomStringUtils.randomAlphanumeric(8);
        if (Objects.nonNull(prefix)) {
            return prefix + "-" + fileCode + "-" + fileName;
        }
        return fileCode + "-" + fileName;
    }

    public static byte[] convertInputStreamToByteArray(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[4096];
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }
}
