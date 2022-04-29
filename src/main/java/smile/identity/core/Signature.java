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
	
    private String partnerId;
    private String apiKey;

    /**
     * Creates a Signature object.
     *
     * @param partner_id the provided partner ID string
     * 
     * @param api_key the partner-provided API key 
     */
    public Signature(String partnerId, String apiKey) {
        this.partnerId = partnerId;
        this.apiKey = apiKey;
    }
    
    /***
     * Generates a signature for the current timestamp
     * 
     * @return the JSON String containing both the signature and related timestamp
     * 
	 * @throws NoSuchAlgorithmException 
	 * 
	 * @throws InvalidKeyException
     */
    public String generate_signature() throws NoSuchAlgorithmException, InvalidKeyException {
    	return generate_signature(System.currentTimeMillis());
    }
    
	/***
     * Generates a signature for the provided timestamp
     * 
     * @param timestamp the timestamp to generate the signature from
     * 
     * @return the JSON String containing both the signature and related timestamp
     * 
	 * @throws NoSuchAlgorithmException
	 *  
	 * @throws InvalidKeyException
     */
    @SuppressWarnings({ "unchecked", "serial" })
	public String generate_signature(Long timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
    	Mac mac = Mac.getInstance(HMAC_SHA_256);
        mac.init(new SecretKeySpec(apiKey.getBytes(), HMAC_SHA_256));
		mac.update(new SimpleDateFormat(DATE_TIME_FORMAT).format(timestamp).getBytes(StandardCharsets.UTF_8));
		mac.update(partnerId.getBytes(StandardCharsets.UTF_8));
		mac.update("sid_request".getBytes(StandardCharsets.UTF_8));
		
		return new JSONObject() {
			{
				put(TIME_STAMP_KEY, timestamp);
				put(SIGNATURE_KEY, Base64.getEncoder().encodeToString(mac.doFinal()));
			}
		}.toString();
    }

    /***
     * Fetches the generated signature for a given timestamp
     * 
     * @param timestamp the timestamp to generate the signature from
     * 
     * @return the string-formatted signature
     * 
     * @throws ParseException
     * 
	 * @throws NoSuchAlgorithmException
	 *  
	 * @throws InvalidKeyException
     */
    public String getSignature(Long timestamp) throws ParseException, InvalidKeyException, NoSuchAlgorithmException {
        JSONObject json = (JSONObject) new JSONParser().parse(generate_signature(timestamp));
        return (String) json.get(SIGNATURE_KEY);
    }
    
	/***
     * Confirms the signature against a newly generated signature based on the same timestamp
     * 
     * @param timestamp the timestamp to generate the signature from
     * 
     * @param signature a previously generated signature, to be confirmed
     * 
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

    /***
     * Generates a sec_key for the provided timestamp
     * 
     * @param timestamp the timestamp to generate the signature from
     * 
     * @return the JSON String containing both the sec_key and related timestamp
     * 
	 * @throws IOException
	 *  
	 * @throws GeneralSecurityException
     */
    @SuppressWarnings({ "unchecked", "serial" })
    @Deprecated
	public String generate_sec_key(Long timestamp) throws GeneralSecurityException, IOException {
        String toHash = Integer.parseInt(partnerId) + ":" + timestamp;

        MessageDigest md = MessageDigest.getInstance(SHA_256);
        md.update(toHash.getBytes());
        byte[] hashed = md.digest();
        String hashSignature = bytesToHexStr(hashed);

        PublicKey publicKey = loadPublicKey(apiKey);
        byte[] encSignature = encryptString(publicKey, hashSignature);
        final String signature = Base64.getEncoder().encodeToString(encSignature) + "|" + hashSignature;

        return new JSONObject() {
        	{
        		put(SEC_KEY, signature);
        		put(TIME_STAMP_KEY, timestamp);
        	}
        }.toString();
    }

    /***
     * Generates a sec_key for the current timestamp
     * 
     * @return the JSON String containing both the sec_key and related timestamp
     * 
	 * @throws IOException 
	 * 
	 * @throws GeneralSecurityException
     */
    @Deprecated
    public String generate_sec_key() throws IOException, GeneralSecurityException {
        Long timestamp = System.currentTimeMillis();
        return generate_sec_key(timestamp);
    }

    @Deprecated
    public String getSecKey(Long timestamp) throws ParseException, IOException, GeneralSecurityException {
        JSONObject json = (JSONObject) new JSONParser().parse(generate_sec_key(timestamp));
        return (String) json.get(SEC_KEY);
    }

    /***
     * Confirms the sec-key against a newly generated sec-key based on the same timestamp
     * 
     * @param timestamp the timestamp to generate the signature from
     * 
     * @param sec-key a previously generated sec-key, to be confirmed
     * 
     * @return TRUE or FALSE
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IOException
     */
    @Deprecated
    public Boolean confirm_sec_key(String timestamp, String secKey) throws GeneralSecurityException, IOException {
        String toHash = Integer.parseInt(partnerId) + ":" + timestamp;
        
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
        } catch (GeneralSecurityException | IOException e) {
            throw e;
        }
    }

    /***
     * Generates public key from API key
     * 
     * @param apiKey the specified API key
     * 
     * @return the generated public key
     * 
     * @throws GeneralSecurityException
     * 
     * @throws IOException
     */
    @Deprecated
    private static PublicKey loadPublicKey(String apiKey) throws GeneralSecurityException, IOException {
        apiKey = apiKey.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "").replace("\r", "").trim();
        apiKey = apiKey.replaceAll(" ", "");
        byte[] data = Base64.getDecoder().decode((apiKey.getBytes()));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /***
     * Encrypts a plain string using a supplied key
     * 
     * @param key the supplied key
     * 
     * @param plaintext the string to encrypt
     * 
     * @return the resulting, encrypted string as a byte array
     * 
     * @throws NoSuchAlgorithmException
     * 
     * @throws NoSuchPaddingException
     * 
     * @throws InvalidKeyException
     * 
     * @throws IllegalBlockSizeException
     * 
     * @throws BadPaddingException
     */
    @Deprecated
    private static byte[] encryptString(PublicKey key, String plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext.getBytes());
    }

    /***
     * Decrypts an encrypted phrase [byte array] using the provided key
     * 
     * @param key the supplied key
     * 
     * @param encrypted the encrypted phrase to be decrypted
     * 
     * @return the corresponding, decrypted string
     * 
     * @throws NoSuchAlgorithmException
     * 
     * @throws NoSuchPaddingException
     * 
     * @throws InvalidKeyException
     * 
     * @throws IllegalBlockSizeException
     * 
     * @throws BadPaddingException
     */
    @Deprecated
    private static String decryptString(PublicKey key, byte[] encrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(encrypted));
    }

    /***
     * Converts a given bytes array to a hexadecimal string
     * 
     * @param bytes the provided byte array
     * 
     * @return the corresponding hexadecimal string
     */
    @Deprecated
    private static String bytesToHexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        
        return sb.toString();
    }
}