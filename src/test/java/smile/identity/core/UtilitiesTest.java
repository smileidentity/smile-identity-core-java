package smile.identity.core;

import org.json.simple.JSONObject;
import org.junit.Test;

public class UtilitiesTest {

    @Test
    public void testUseSignatureDefaultTrue(){
        JSONObject options = new JSONObject();
        Boolean useSignature = Utilities.useSignature(options);
        assert(useSignature).equals(true);
    }

    @Test
    public void testUseSignatureUseOptionFalse(){
        JSONObject options = new JSONObject();
        options.put(Signature.SIGNATURE_KEY, false);
        Boolean useSignature = Utilities.useSignature(options);
        assert(useSignature).equals(false);
    }

    @Test
    public void testUseSignatureUseOptionTrue(){
        JSONObject options = new JSONObject();
        options.put(Signature.SIGNATURE_KEY, true);
        Boolean useSignature = Utilities.useSignature(options);
        assert(useSignature).equals(true);
    }

}
