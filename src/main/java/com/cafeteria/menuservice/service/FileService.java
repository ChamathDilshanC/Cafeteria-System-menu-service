package com.cafeteria.menuservice.service;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * Handles file uploads/deletions to Google Cloud Storage.
 *
 * Authentication: Set GOOGLE_APPLICATION_CREDENTIALS to the path of your
 * service-account key JSON (local dev), or deploy on a GCP VM / Cloud Run
 * instance with the appropriate IAM role for credential auto-injection.
 *
 * Config properties (from menu-service.yml / Spring Cloud Config):
 * spring.cloud.gcp.storage.bucket — GCS bucket name
 * spring.cloud.gcp.project-id — GCP project ID
 */
@Service
public class FileService {

    private final Storage storage;
    private final String bucketName;

    public FileService(
            @Value("${spring.cloud.gcp.storage.bucket}") String bucketName,
            @Value("${spring.cloud.gcp.project-id}") String projectId) {
        this.bucketName = bucketName;
        // StorageOptions picks up GOOGLE_APPLICATION_CREDENTIALS automatically
        this.storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
    }

    /**
     * Uploads a multipart image to GCS and returns its public-read URL.
     * The object is stored under the path: menu-images/<uuid>.<ext>
     */
    public String uploadImage(MultipartFile file) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String objectName = "menu-images/" + UUID.randomUUID() + "." + ext;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());

        // Public access is controlled via bucket-level IAM policy (allUsers: Storage
        // Object Viewer).
        // Per-object ACLs are disabled because the bucket uses Uniform Bucket-Level
        // Access.

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

    /**
     * Deletes an object from GCS by its public URL.
     * Silently ignores if the object does not exist.
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank())
            return;
        try {
            // Extract object name from the URL path after bucket name
            String prefix = "https://storage.googleapis.com/" + bucketName + "/";
            if (imageUrl.startsWith(prefix)) {
                String objectName = imageUrl.substring(prefix.length());
                storage.delete(BlobId.of(bucketName, objectName));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Generates a short-lived (15-min) signed URL for private buckets.
     * Not used when the bucket is public-read, but kept for reference.
     */
    public URL generateSignedUrl(String objectName) {
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, objectName)).build();
        return storage.signUrl(blobInfo, 15, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature());
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains("."))
            return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
