package smile.identity.core;

import org.junit.Test;
import smile.identity.core.keys.SignatureKey;

import static org.junit.Assert.*;

public class SignatureTest {

	@Test
	public void itGeneratesASignatureWhenNoTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey sk = signature.getSignatureKey();
		assertFalse(sk.getSignature().isEmpty());
	}

	@Test
	public void itGeneratesASignatureWhenTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		long timestamp = System.currentTimeMillis();
		SignatureKey sk = signature.getSignatureKey(timestamp);
		assertEquals(sk.getTimestamp(), timestamp);
	}

	@Test
	public void itConfirmsASignature(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey original = signature.getSignatureKey();
		assertTrue(signature.confirmSignature(original.getTimestamp(), original.getSignature()));
	}

	@Test
	public void itConfirmsASignatureWithStringTimestamp() {
		Signature signature = new Signature("partner", "apiKey");
		String timestamp = "2023-02-13T15:07:42.214Z";
		String returnedSignature = "XxThLHZy2ij1WINkqNEPgzVs9RvUDHWlebDYzprqS74=";
		assertTrue(signature.confirmSignature(timestamp, returnedSignature));
	}
}