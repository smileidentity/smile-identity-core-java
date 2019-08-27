package smile.identity.core;
import org.json.simple.JSONObject;

import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.MessageDigest;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;

public class Signature {
  private Integer partnerId;
  private String api_key;

  public Signature(String partner_id, String api_key) {
    int partnerId = Integer.parseInt(partner_id);
    this.partnerId = partnerId;
    this.api_key = api_key;
  }

  public String generate_sec_key(Long timestamp) throws Exception {
    String toHash = partnerId + ":" + timestamp;
    String signature = "";
    JSONObject signatureObj = new JSONObject();

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(toHash.getBytes());
      byte[] hashed = md.digest();
      String toEncryptString = bytesToHexStr(hashed);

      PublicKey publicKey = loadPublicKey(api_key);
      byte[] encSignature = encryptString(publicKey, toEncryptString);
      signature = Base64.getEncoder().encodeToString(encSignature) + "|" + toEncryptString;
    } catch(Exception  e) {
      throw e;
    }

    signatureObj.put("sec_key", signature);
    signatureObj.put("timestamp", timestamp);
    return signatureObj.toString();
  }

  // we overload the method for the optional timestamp
  public String generate_sec_key() throws Exception {
    Long timestamp = System.currentTimeMillis();
    return generate_sec_key(timestamp);
  }

  private static PublicKey loadPublicKey(String apiKey) throws GeneralSecurityException, IOException {
    byte[] data = Base64.getDecoder().decode((apiKey.getBytes()));
    X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    KeyFactory factObj = KeyFactory.getInstance("RSA");
    PublicKey lPKey = factObj.generatePublic(spec);
    return lPKey;
  }

  private static byte[] encryptString(PublicKey key, String plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(plaintext.getBytes());
  }

  private static String bytesToHexStr(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
