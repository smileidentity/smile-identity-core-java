package smile.identity.core;

import org.json.simple.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Signature {

    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    public static final String SEC_KEY = "sec_key";
    public static final String TIME_STAMP_KEY = "timestamp";
    public static final String SIGNATURE_KEY = "signature";
	
    private Integer partnerId;
    private String apiKey;

    public Signature(String partner_id, String apiKey) {
        int partnerId = Integer.parseInt(partner_id);
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }
    
	public JSONObject generateSignature() throws Exception {
    	return generateSignature(System.currentTimeMillis());
    }
    
	/***
     *  Will generate a signature for the provided timestamp
     * @param timestamp the timestamp to generate the signature from
     * @return the JSON Object containing both the signature and related timestamp
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked" })
	public JSONObject generateSignature(Long timestamp) throws Exception {
        JSONObject signatureObj = new JSONObject();
    	signatureObj.put(TIME_STAMP_KEY, timestamp);
    	
    	SecretKeySpec secretKeySpec = new SecretKeySpec(apiKey.getBytes(), HMAC_SHA_256);
    	
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(secretKeySpec);
		mac.update(timestamp.toString().getBytes(StandardCharsets.UTF_8));
		mac.update(partnerId.toString().getBytes(StandardCharsets.UTF_8));
		mac.update("sid_request".getBytes(StandardCharsets.UTF_8));
		
		String signature = Base64.getEncoder().encodeToString(mac.doFinal());
		signatureObj.put(SIGNATURE_KEY, signature);
		
		return signatureObj;
    }
    
	/***
     *  Will confirm the signature against a newly generated signature based on the same timestamp
     * @param timestamp the timestamp to generate the signature from
     * @param signature a previously signature, to be confirmed
     * @return TRUE or FALSE
     */
    public Boolean confirmSignature(Long timestamp, String signature) {
	    try {
			return signature.equalsIgnoreCase((String) generateSignature(timestamp).get(SIGNATURE_KEY));
		} catch (Exception e) {
			return false;
		}
    }

    @SuppressWarnings("unchecked")
	public String generateSecKey(Long timestamp) throws Exception {
        String toHash = partnerId + ":" + timestamp;
        String signature = "";
        JSONObject signatureObj = new JSONObject();

        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(toHash.getBytes());
            byte[] hashed = md.digest();
            String hashSignature = bytesToHexStr(hashed);

            PublicKey publicKey = loadPublicKey(apiKey);
            byte[] encSignature = encryptString(publicKey, hashSignature);
            signature = Base64.getEncoder().encodeToString(encSignature) + "|" + hashSignature;
        } catch (Exception e) {
            throw e;
        }

        signatureObj.put(SEC_KEY, signature);
        signatureObj.put(SIGNATURE_KEY, timestamp);
        return signatureObj.toString();
    }

    // we overload the method for the optional timestamp
    public String generateSecKey() throws Exception {
        Long timestamp = System.currentTimeMillis();
        return generateSecKey(timestamp);
    }

    public Boolean confirmSecKey(String timestamp, String secKey) throws Exception {
        String toHash = partnerId + ":" + timestamp;
        
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(toHash.getBytes());
            byte[] hashed = md.digest();
            String hashSignature = bytesToHexStr(hashed);

            String[] arrOfStr = secKey.split("\\|");
            String encrypted = arrOfStr[0];

            PublicKey publicKey = loadPublicKey(apiKey);

            byte[] decodedBytes = Base64.getDecoder().decode(encrypted);
            String decrypted = decryptString(publicKey, decodedBytes);

            return decrypted.equals(hashSignature);
        } catch (Exception e) {
            throw e;
        }
    }

    private static PublicKey loadPublicKey(String apiKey) throws GeneralSecurityException, IOException {
        apiKey = apiKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "").replace("\r", "").trim();
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

    private static String decryptString(PublicKey key, byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encrypted));
    }

    private static String bytesToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
}