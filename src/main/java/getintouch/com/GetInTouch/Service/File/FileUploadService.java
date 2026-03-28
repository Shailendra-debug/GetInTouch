package getintouch.com.GetInTouch.Service.File;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.bucket}")
    private String bucket;

    @Value("${cloudflare.r2.public-url}")
    private String publicUrl;


    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 🔒 Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            String key = folder + "/" + fileName;

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            return publicUrl + "/" + key;

        } catch (Exception e) {
            throw new RuntimeException("Upload failed: " + e.getMessage());
        }
    }
}