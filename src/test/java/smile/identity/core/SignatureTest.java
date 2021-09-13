package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;

public class SignatureTest {
	
    private String PARTNER_ID = "<PARTNER_ID>";
    private String API_KEY = "<API_KEY>";
    private Signature mSignature;
	
    @Before
    public void setup() {
    	mSignature = new Signature(PARTNER_ID, API_KEY);
    }

    @Test
    public void testSignature() {
    	JSONObject sigJsonObj = null;
    	Long dateTime = new Date().getTime();
    	
    	try {
        	sigJsonObj = (JSONObject) new JSONParser().parse(mSignature.generate_signature(dateTime));
    	} catch (Exception e) {
    		assertTrue(false);
		}
    	
    	assertNotNull(sigJsonObj);
    	assertTrue(sigJsonObj.containsKey(Signature.TIME_STAMP_KEY));
    	
    	Long dt = (Long) sigJsonObj.get(Signature.TIME_STAMP_KEY);
    	assertEquals(dateTime, dt);
    	
    	assertTrue(sigJsonObj.containsKey(Signature.SIGNATURE_KEY));
    	String signature = (String) sigJsonObj.get(Signature.SIGNATURE_KEY);
    	assertTrue(!signature.isBlank());
    	
    	assertTrue(mSignature.confirm_signature(dateTime, signature));
    }
}