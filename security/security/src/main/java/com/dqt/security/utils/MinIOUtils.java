package com.dqt.security.utils;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinIOUtils {

    private final MinioClient minioClient;

    // Upload Files
    @SneakyThrows
    public void putObject(String bucketName, MultipartFile file, String filename) {
        InputStream inputStream = file.getInputStream();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(filename)
                .stream(inputStream, inputStream.available(), -1)
                .build());
    }

    // Check if bucket name exists
    @SneakyThrows
    public boolean bucketExists(String bucketName) {
        boolean found =
                minioClient.bucketExists(
                        BucketExistsArgs.builder().
                                bucket(bucketName).
                                build());

        if (found) {
            log.info("BucketExists : {} exists", bucketName);
        } else {
            log.info("BucketExists : {} does not exist", bucketName);
        }
        return found;
    }

    // Create bucket name
    @SneakyThrows
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build());

            return true;
        } else {
            return false;
        }
    }

    // List all buckets
    @SneakyThrows
    public List<Bucket> listBuckets() {
        return minioClient.listBuckets();
    }

    // List all bucket names
    @SneakyThrows
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        log.info("BucketListName size : {}", bucketListName.size());

        return bucketListName;
    }

    // List all objects from the specified bucket
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder().bucket(bucketName).build());
        }
        return null;
    }

    // Delete Bucket by its name from the specified bucket
    @SneakyThrows
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);

            for (Result<Item> result : myObjects) {
                Item item = result.get();
                //  Delete failed when There are object files in bucket

                log.info("RemoveBucket | item size : {}", item.size());

                if (item.size() > 0) {
                    return false;
                }
            }
            //  Delete bucket when bucket is empty
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            flag = bucketExists(bucketName);

            log.info("RemoveBucket | flag : {}", flag);
            if (!flag) {
                return true;
            }
        }
        return false;
    }

    // List all object names from the specified bucket
    @SneakyThrows
    public List<String> listObjectNames(String bucketName) {
        List<String> listObjectNames = new ArrayList<>();
        boolean flag = bucketExists(bucketName);

        if (flag) {
            Iterable<Result<Item>> myObjects = listObjects(bucketName);
            for (Result<Item> result : myObjects) {
                Item item = result.get();
                listObjectNames.add(item.objectName());
            }
        } else {
            listObjectNames.add("Bucket does not exist ");
        }

        log.info("ListObjectNames size : {}", listObjectNames.size());

        return listObjectNames;
    }

    // Delete object from the specified bucket
    @SneakyThrows
    public boolean removeObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
            return true;
        }
        return false;
    }

    // Get file path from the specified bucket
    @SneakyThrows
    public String getObjectDownloadUrl(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        String url = "";

        if (flag) {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(7, TimeUnit.DAYS)
                            .build());
            log.info("Url : {}", url);
        }
        return url;
    }

    @SneakyThrows
    public String getObjectPreviewUrl(String bucketName, String objectName) {
        String url = "";
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("response-content-type", "application/json");
        url = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(objectName)
                        .expiry(7, TimeUnit.DAYS)
                        .extraQueryParams(reqParams)
                        .build());
        return url;
    }

    // Get metadata of the object from the specified bucket
    @SneakyThrows
    public StatObjectResponse statObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder().bucket(bucketName).object(objectName).build());

            log.info("Stat : {}", stat.toString());

            return stat;
        }
        return null;
    }

    // Get a file object as a stream from the specified bucket
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        boolean flag = bucketExists(bucketName);

        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .build());

                log.info("getObject Stream : {}", stream.toString());
                return stream;
            }
        }
        return null;
    }

    // Delete multiple file objects from the specified bucket
    @SneakyThrows
    public boolean removeObjects(String bucketName, List<String> objectNames) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            List<DeleteObject> objects = new LinkedList<>();
            for (int i = 0; i < objectNames.size(); i++) {
                objects.add(new DeleteObject(objectNames.get(i)));
            }
            Iterable<Result<DeleteError>> results =
                    minioClient.removeObjects(
                            RemoveObjectsArgs.builder().bucket(bucketName).objects(objects).build());

            for (Result<DeleteError> result : results) {
                DeleteError error = result.get();

                log.info("Error : {} {}", error.objectName(), error.message());

                return false;
            }
        }
        return true;
    }

    ///////////////////////////////////////////////////////////////////////

    // Upload InputStream object to the specified bucket
    @SneakyThrows
    public boolean putObject(String bucketName, String objectName, InputStream inputStream, String contentType) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(objectName).stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build());
            StatObjectResponse statObject = statObject(bucketName, objectName);

            log.info("StatObject != null : {}, size: {}", statObject != null, statObject.size());
            if (statObject != null && statObject.size() > 0) {
                return true;
            }
        }
        return false;
    }

    // Get a file object as a stream from the specified bucket （ Breakpoint download )
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                InputStream stream =
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucketName)
                                        .object(objectName)
                                        .offset(offset)
                                        .length(length)
                                        .build());

                log.info("Stream : {}", stream.toString());
                return stream;
            }
        }
        return null;
    }

}

