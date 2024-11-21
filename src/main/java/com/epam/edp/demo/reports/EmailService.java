package com.epam.edp.demo.reports;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.epam.edp.demo.config.SesConfigMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private SesConfigMethods sesConfigMethods;

    @Autowired
    private AmazonS3 amazonS3;

    private final String bucketName = "tm2-gym-application"; // Replace with your S3 bucket name
    private final String fileKey = "coach_reports.xlsx";  // Replace with your S3 file key

    @Scheduled(fixedRate = 50000)
    public void sendEmail() throws MessagingException, IOException {
        System.out.println("Sending email");

        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the date range: from (today - 7 days) to (today - 1 day)
        LocalDate startDate = today.minusDays(7);
        LocalDate endDate = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");

        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        // Create the HTML body with the calculated date range
        String bodyHtml = "<h1>Coach Weekly Reports</h1>"
                + "<p>Attached are the weekly reports for the coaches.</p>"
                + "<p>The reports cover the period from <b>" + formattedStartDate + "</b> to <b>" + formattedEndDate + "</b>.</p>";

        // Download the file from S3 using AmazonS3
        File downloadedFile = downloadFileFromS3();

        try {
            // Send email with the downloaded file
            sesConfigMethods.sendEmailAttachment(
                    "arn:aws:iam::471112613959:role/org/DeveloperAccessRoleTeam2",
                    "mySession",
                    "mahi.katamreddy@gmail.com",
                    "mahireddy.katam@gmail.com",
                    bodyHtml,
                    downloadedFile.getAbsolutePath()); // Send the local file

            System.out.println("Email sent successfully with date range from " + formattedStartDate + " to " + formattedEndDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // Clean up the downloaded file after sending the email
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
    }

    private File downloadFileFromS3() throws IOException {
        // Define where the file will be downloaded locally
        File localFile = new File(System.getProperty("java.io.tmpdir") + File.separator + fileKey);

        // Get the object from S3
        S3Object s3Object = amazonS3.getObject(bucketName, fileKey);

        // Save the object content to a local file
        try (InputStream inputStream = s3Object.getObjectContent();
             FileOutputStream fos = new FileOutputStream(localFile)) {
            byte[] read_buf = new byte[1024];
            int read_len;
            while ((read_len = inputStream.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
        }

        return localFile;
    }
}
