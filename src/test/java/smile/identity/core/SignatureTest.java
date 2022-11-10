package smile.identity.core;

import org.junit.Assert;
import org.junit.Test;
import smile.identity.core.keys.SignatureKey;

public class SignatureTest {

	@Test
	public void itGeneratesASignatureWhenNoTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey sk = signature.generateSignature();
		Assert.assertFalse(sk.getSignature().isEmpty());
	}

	@Test
	public void itGeneratesASignatureWhenTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		long timestamp = System.currentTimeMillis();
		SignatureKey sk = signature.generateSignature();
		Assert.assertEquals(sk.getTimestamp(), timestamp);
	}

	@Test
	public void itConfirmsASignature(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey original = signature.generateSignature();
		Assert.assertTrue(signature.confirmSignature(original.getTimestamp(), original.getSignature()));
	}
}