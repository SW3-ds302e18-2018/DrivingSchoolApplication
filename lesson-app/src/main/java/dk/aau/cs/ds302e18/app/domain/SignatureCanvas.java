package dk.aau.cs.ds302e18.app.domain;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Handles the data transport between AmazonS3 bucket and the app for storing signature images.
 */
public class SignatureCanvas {
    private static String ACCESS_KEY;
    private static String SECRET_KEY;

    static { // Creating a static constructor for one time initialize authconfig.properties att.
        ResourceBundle reader = ResourceBundle.getBundle("authconfig");
        ACCESS_KEY = reader.getString("aws.accesskey");
        SECRET_KEY = reader.getString("aws.secretkey");
    }

    /**
     * Uploads a signature image to the Amazon S3 bucket.
     * @param bucketName
     * @param imageName
     * @param imageData
     */
    public void upload(String bucketName, String imageName, String imageData) {
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        Region region = Region.getRegion(Regions.EU_WEST_2);
        s3.setRegion(region);

        try {
            s3.putObject(new PutObjectRequest(bucketName, imageName + ".png", createFile(imageName, imageData)));
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    /**
     * Gets signature date from the Amazon S3 bucket.
     * @param bucketName
     * @param imageName
     * @return
     */
    public String getSignatureDate(String bucketName, String imageName) {
        AmazonS3 s3 = new AmazonS3Client(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY));
        Region region = Region.getRegion(Regions.EU_WEST_2);
        s3.setRegion(region);

        String lastModifyed;

        try {
            S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, (imageName + ".png")));
            lastModifyed = String.valueOf(s3Object.getObjectMetadata().getLastModified());
        } catch (Exception ex) {
            S3Object s3Object = s3.getObject(new GetObjectRequest(bucketName, ("notsigned.png")));
            lastModifyed = String.valueOf(s3Object.getObjectMetadata().getLastModified());
        }

        return lastModifyed;
    }

    /**
     * Creates a file based on a signature image.
     * @param imageName
     * @param imageData
     * @return
     * @throws IOException
     */
    private static File createFile(String imageName, String imageData) throws IOException {
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(imageData.substring(imageData.indexOf(",") + 1));
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

        File file = new File(imageName + ".png");
        ImageIO.write(bufferedImage, "png", file);

        return file;
    }
}