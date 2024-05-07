package com.dqt.security.controller;

import com.dqt.security.dto.response.ResponseMessage;
import com.dqt.security.utils.FileUtil;
import com.dqt.security.utils.MinIOUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("${api.prefix}/minio")

@Slf4j
@RequiredArgsConstructor
public class MinIOController {

    @Value("${minio.bucket}")
    private String bucketName;

    private final MinIOUtils minIOUtils;

    @PostMapping(path = "upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = FileUtil.generateFileName(null, file);
            minIOUtils.putObject(bucketName, file, fileName);
            return ResponseEntity.ok(ResponseMessage.ok("File uploaded successfully", fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileName") String fileName) {
        try {
            InputStream stream = minIOUtils.getObject(bucketName, fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(stream));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/preview/{fileName}")
    public ResponseEntity<?> getPreview(@PathVariable("fileName") String fileName) {
        try {
            InputStream stream = minIOUtils.getObject(bucketName, fileName);
            byte[] bytes = FileUtil.convertInputStreamToByteArray(stream);
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));

            return ResponseEntity.ok()
                    .contentLength(bytes.length)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download-url/{fileName}")
    public ResponseEntity<?> getDownloadUrl(@PathVariable("fileName") String fileName) {
        try {
            String url = minIOUtils.getObjectDownloadUrl(bucketName, fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/preview-url/{fileName}")
    public ResponseEntity<?> getPreviewUrl(@PathVariable("fileName") String fileName) {
        try {
            String url = minIOUtils.getObjectPreviewUrl(bucketName, fileName);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket/create/{bucketName}")
    public ResponseEntity<?> createBucket(@PathVariable("bucketName") String bucketName) {
        try {
            boolean flag = minIOUtils.makeBucket(bucketName);
            if (flag) {
                return ResponseEntity.ok(ResponseMessage.ok("Create bucket successfully"));
            }
            return ResponseEntity.badRequest().body(ResponseMessage.error("Bucket already exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket/exists/{bucketName}")
    public ResponseEntity<?> existsBucket(@PathVariable("bucketName") String bucketName) {
        try {
            boolean flag = minIOUtils.bucketExists(bucketName);
            if (flag) {
                return ResponseEntity.ok(ResponseMessage.ok("Bucket is exists"));
            }
            return ResponseEntity.badRequest().body(ResponseMessage.error("Bucket don't exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket")
    public ResponseEntity<?> getListBucket() {
        try {
            List<String> strings = minIOUtils.listBucketNames();
            return ResponseEntity.ok(ResponseMessage.ok("List Bucket", strings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket/remove/{bucketName}")
    public ResponseEntity<?> removeBucket(@PathVariable("bucketName") String bucketName) {
        try {
            boolean flag = minIOUtils.removeBucket(bucketName);
            if (flag) {
                return ResponseEntity.ok(ResponseMessage.ok("Remove bucket successfully"));
            }
            return ResponseEntity.badRequest().body(ResponseMessage.error("Bucket don't exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket/object/{bucketName}")
    public ResponseEntity<?> listObjectNames(@PathVariable("bucketName") String bucketName) {
        try {
            if (Objects.isNull(bucketName)) bucketName = this.bucketName;

            List<String> listObjectNames = minIOUtils.listObjectNames(bucketName);
            return ResponseEntity.ok(ResponseMessage.ok("ListObjectNames bucket successfully", listObjectNames));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bucket/object/remove/{bucketName}/{objectName}")
    public ResponseEntity<?> removeObject(@PathVariable("bucketName") String bucketName, @PathVariable("objectName") String objectName) {
        try {
            if (Objects.isNull(bucketName)) bucketName = this.bucketName;

            boolean flag = minIOUtils.removeObject(bucketName, objectName);
            if (flag) {
                return ResponseEntity.ok(ResponseMessage.ok("Remove object in Bucket successfully"));
            }
            return ResponseEntity.badRequest().body(ResponseMessage.error("Bucket don't exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}

