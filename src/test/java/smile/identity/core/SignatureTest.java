package smile.identity.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class SignatureTest {
	
    private Integer PARTNER_ID = 212;
    private String API_KEY = "gNra9o0gy3pRFg0xc3NXZwAYtAPj1L9iOIQjX3yK0Ui2QG1lZ68VcNNfs34QW1h5Wg+PTe2UCZUjLpK5VNjwmU2I94Ihdjb9HplUODatu5QuThDmg+w7kd5PZrkXXI79xRRD6d9b7oqefA07rj3GoMFZ2YCDNN50EMPIgSdWSnw=";
    private Signature mSignature;
	
    @Before
    public void setup() {
    	mSignature = new Signature(PARTNER_ID.toString(), API_KEY);
    }

    @Test
    public void testSignature() {
    	JSONObject sigJsonObj = null;
    	Long dateTime = new Date().getTime();
    	
    	try {
        	sigJsonObj = mSignature.generateSignature(dateTime);
    	} catch (Exception e) {
    		System.out.println("EXCEPTION: " + e.getMessage());
    		assertTrue(false);
		}
    	
    	assertNotNull(sigJsonObj);
    	assertTrue(sigJsonObj.containsKey(Signature.TIME_STAMP_KEY));
    	
    	Long dt = (Long) sigJsonObj.get(Signature.TIME_STAMP_KEY);
    	assertEquals(dateTime, dt);
    	
    	assertTrue(sigJsonObj.containsKey(Signature.SIGNATURE_KEY));
    	String signature = (String) sigJsonObj.get(Signature.SIGNATURE_KEY);
    	assertTrue(!signature.isBlank());
    	
    	assertTrue(mSignature.confirmSignature(dateTime, signature));
    }
}