package dk.aau.cs.ds302e18.app;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Test
{
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException
    {
        String Url = "peter@servon.eu";
        System.out.println(DigestUtils.md5Hex(Url));
    }
}
