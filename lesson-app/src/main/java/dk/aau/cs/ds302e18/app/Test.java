package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.domain.SignatureCanvas;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Test
{
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException
    {
        SignatureCanvas signatureCanvas = new SignatureCanvas();

        System.out.println(signatureCanvas.getSignatureDate("p3-project", "fgdsfgsdfg"));

    }
}
