package com.leonardo.inventory.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

@Service
public class S3StorageServiceImpl implements StorageService {

	@Value("${aws.s3.accessKey}")
	private String accessKey;
	@Value("${aws.s3.secretKey}")
	private String secretKey;
	@Value("${aws.s3.bucketName}")
	private String bucketName;

	private Regions region = Regions.SA_EAST_1;

	@Override
	public byte[] getFile(String filename) throws IOException {
		AmazonS3 s3client = this.getS3Client();

		S3Object s3object = s3client.getObject(bucketName, filename);

		return IOUtils.toByteArray(s3object.getObjectContent());
	}

	@Override
	public void putFile(MultipartFile file, String filename) throws IOException {
		AmazonS3 s3client = this.getS3Client();
		s3client.putObject(new PutObjectRequest(bucketName, filename, this.convertMultiPartToFile(file)));
	}

	private AmazonS3 getS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(region).build();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(file.getBytes());
		}
		return convFile;
	}

}
