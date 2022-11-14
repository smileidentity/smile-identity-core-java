package smile.identity.core;

import org.junit.Test;
import smile.identity.core.keys.SignatureKey;

import static org.junit.Assert.*;

public class SignatureTest {

	@Test
	public void itGeneratesASignatureWhenNoTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey sk = signature.generateSignature();
		assertFalse(sk.getSignature().isEmpty());
	}

	@Test
	public void itGeneratesASignatureWhenTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		long timestamp = System.currentTimeMillis();
		SignatureKey sk = signature.generateSignature();
		assertEquals(sk.getTimestamp(), timestamp);
	}

	@Test
	public void itConfirmsASignature(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey original = signature.generateSignature();
		assertTrue(signature.confirmSignature(original.getTimestamp(), original.getSignature()));
	}
}