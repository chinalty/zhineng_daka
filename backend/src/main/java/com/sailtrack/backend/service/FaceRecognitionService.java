package com.sailtrack.backend.service;

import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.CompareFaceWithMaskRequest;
import com.aliyun.facebody20191230.models.CompareFaceWithMaskResponse;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FaceRecognitionService {
    
    @Value("${aliyun.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aliyun.access-key-secret:}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucket-name:}")
    private String bucketName;
    
    @Value("${aliyun.oss.endpoint:oss-cn-shanghai.aliyuncs.com}")
    private String ossEndpoint;
    
    @Value("${aliyun.facebody.endpoint:facebody.cn-shanghai.aliyuncs.com}")
    private String faceBodyEndpoint;
    
    @Value("${face.recognition.threshold:80.0}")
    private Float confidenceThreshold;
    
    private Client createFaceClient() throws Exception {
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            throw new RuntimeException("阿里云AccessKeyId未配置");
        }
        
        Config config = new Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint(faceBodyEndpoint);
        
        return new Client(config);
    }
    
    public boolean compareFace(String imageUrlA, String imageUrlB) throws Exception {
        Client client = createFaceClient();
        
        System.out.println("=== 开始人脸对比（使用文件流方式）===");
        System.out.println("图片A: " + imageUrlA);
        System.out.println("图片B: " + imageUrlB);
        
        try {
            // 从URL获取图片输入流
            java.net.URL urlA = new java.net.URL(imageUrlA);
            java.net.URL urlB = new java.net.URL(imageUrlB);
            java.io.InputStream inputStreamA = urlA.openConnection().getInputStream();
            java.io.InputStream inputStreamB = urlB.openConnection().getInputStream();
            
            // 使用 CompareFaceAdvanceRequest 以文件流形式传入图片（支持任意可访问的URL）
            com.aliyun.facebody20191230.models.CompareFaceAdvanceRequest advanceRequest = 
                new com.aliyun.facebody20191230.models.CompareFaceAdvanceRequest()
                    .setImageURLAObject(inputStreamA)
                    .setImageURLBObject(inputStreamB);
            
            RuntimeOptions runtime = new RuntimeOptions();
            com.aliyun.facebody20191230.models.CompareFaceResponse response = 
                client.compareFaceAdvance(advanceRequest, runtime);
            
            // 关闭输入流
            inputStreamA.close();
            inputStreamB.close();
            
            if (response.getBody() == null || response.getBody().getData() == null) {
                throw new RuntimeException("人脸识别服务返回数据为空");
            }
            
            Float confidence = response.getBody().getData().getConfidence();
            
            System.out.println("人脸相似度: " + confidence + "%");
            
            return confidence != null && confidence >= confidenceThreshold;
            
        } catch (Exception e) {
            System.err.println("人脸识别失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("人脸识别失败: " + e.getMessage());
        }
    }
    
    public String uploadFaceImage(MultipartFile file, Long userId) throws IOException {
        if (accessKeyId == null || accessKeyId.isEmpty()) {
            throw new RuntimeException("阿里云OSS配置未完成，暂不支持人脸识别功能");
        }
        
        System.out.println("=== 开始上传人脸照片到OSS ===");
        System.out.println("Bucket: " + bucketName);
        System.out.println("Endpoint: " + ossEndpoint);
        System.out.println("文件大小: " + file.getSize() + " bytes");
        
        OSS ossClient = new OSSClientBuilder().build(ossEndpoint, accessKeyId, accessKeySecret);
        
        try {
            String fileName = "faces/" + userId + "_" + System.currentTimeMillis() + ".jpg";
            
            // 上传文件到OSS
            com.aliyun.oss.model.PutObjectRequest putObjectRequest = 
                new com.aliyun.oss.model.PutObjectRequest(bucketName, fileName, file.getInputStream());
            
            // 设置文件访问权限为公共读（确保阿里云人脸识别服务可以访问）
            com.aliyun.oss.model.ObjectMetadata metadata = new com.aliyun.oss.model.ObjectMetadata();
            metadata.setContentType("image/jpeg");
            metadata.setObjectAcl(com.aliyun.oss.model.CannedAccessControlList.PublicRead);
            putObjectRequest.setMetadata(metadata);
            
            ossClient.putObject(putObjectRequest);
            
            // 生成公开访问的URL
            String imageUrl = "https://" + bucketName + "." + ossEndpoint + "/" + fileName;
            
            System.out.println("人脸照片上传成功: " + imageUrl);
            System.out.println("=== OSS上传完成 ===");
            
            return imageUrl;
            
        } catch (Exception e) {
            System.err.println("OSS上传失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("图片上传失败: " + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }
    
    public boolean isConfigured() {
        return accessKeyId != null && !accessKeyId.isEmpty() 
            && accessKeySecret != null && !accessKeySecret.isEmpty()
            && bucketName != null && !bucketName.isEmpty();
    }
}
