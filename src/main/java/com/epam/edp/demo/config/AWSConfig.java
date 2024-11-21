package com.epam.edp.demo.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sqs.SqsClient;
@Configuration
public class AWSConfig {
    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.sessionToken}")
    private String sessionToken;

    @Value("${aws.region}")
    private String region;
    @Bean
    public SqsClient amazonSQSClient() {
        AwsSessionCredentials awsCredentials = AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken);

        return SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
    @Bean
    public SesV2Client amazonSESClient() {
        AwsSessionCredentials awsCredentials = AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken);
        return SesV2Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.EU_WEST_2)
                .build();
    }
    @Bean
    public AmazonS3 createS3Client() {
        BasicSessionCredentials awsCredentials = new BasicSessionCredentials(accessKeyId, secretAccessKey, sessionToken);

        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_2)  // Specify your region
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))  // Use BasicSessionCredentials
                .build();
    }
}
