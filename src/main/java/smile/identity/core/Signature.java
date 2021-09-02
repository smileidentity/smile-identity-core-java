package smile.identity.core;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
import java.text.SimpleDateFormat;
import java.util.Base64;

public class Signature {

    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA_256 = "HmacSHA256";
    public static final String SEC_KEY = "sec_key";
    public static final String TIME_STAMP_KEY = "timestamp";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String SIGNATURE_KEY = "signature";
	
    private Integer partnerId;
    private String apiKey;

    public Signature(String partnerId, String apiKey) {
        this.partnerId = Integer.parseInt(partnerId);
        this.apiKey = apiKey;
    }
    
	public String generate_signature() throws Exception {
    	return generate_signature(System.currentTimeMillis());
    }
    
	/***
     *  Will generate a signature for the provided timestamp
     * @param timestamp the timestamp to generate the signature from
     * @return the JSON String containing both the signature and related timestamp
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException
     */
    @SuppressWarnings({ "unchecked" })
	public String generate_signature(Long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
    	JSONObject signatureObj = new JSONObject();
    	signatureObj.put(TIME_STAMP_KEY, timestamp);
    	
    	Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(apiKey.getBytes(), HMAC_SHA_256));
		mac.update(new SimpleDateFormat(DATE_TIME_FORMAT).format(timestamp).getBytes(StandardCharsets.UTF_8));
		mac.update(partnerId.toString().getBytes(StandardCharsets.UTF_8));
		mac.update("sid_request".getBytes(StandardCharsets.UTF_8));
		
		String signature = Base64.getEncoder().encodeToString(mac.doFinal());
		signatureObj.put(SIGNATURE_KEY, signature);
		
		return signatureObj.toString();
    }

    public String getSignature(Long timestamp) throws Exception {
        JSONObject json = (JSONObject) new JSONParser().parse(generate_signature(timestamp));
        return (String) json.get(SIGNATURE_KEY);
    }
    
	/***
     *  Will confirm the signature against a newly generated signature based on the same timestamp
     * @param timestamp the timestamp to generate the signature from
     * @param signature a previously generated signature, to be confirmed
     * @return TRUE or FALSE
     */
    public Boolean confirm_signature(Long timestamp, String signature) {
	    try {
	    	JSONObject json = (JSONObject) new JSONParser().parse(generate_signature(timestamp));
			return signature.equalsIgnoreCase((String) json.get(SIGNATURE_KEY));
		} catch (NoSuchAlgorithmException|InvalidKeyException | ParseException e) {
			return false;
		}
    }

    @SuppressWarnings({ "unchecked" })
	public String generate_sec_key(Long timestamp) throws Exception {
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
        signatureObj.put(TIME_STAMP_KEY, timestamp);
        
        return signatureObj.toString();
    }

    // we overload the method for the optional timestamp
    public String generate_sec_key() throws Exception {
        Long timestamp = System.currentTimeMillis();
        return generate_sec_key(timestamp);
    }
    
    public String getSecKey(Long timestamp) throws Exception {
        JSONObject json = (JSONObject) new JSONParser().parse(generate_sec_key(timestamp));
        return (String) json.get(SEC_KEY);
    }

    public Boolean confirm_sec_key(String timestamp, String secKey) throws Exception {
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
        apiKey = apiKey.replaceAll(" ", "");
        System.out.println("API_KEY: " + apiKey);
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