package smile.identity.core;

import org.junit.Test;
import smile.identity.core.keys.SignatureKey;

public class SignatureTest {

	@Test
	public void itGeneratesASignatureWhenNoTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey sk = signature.generateSignature();
		assert!(sk.getSignature()).isEmpty();
	}

	@Test
	public void itGeneratesASignatureWhenTimestampProvided(){
		Signature signature = new Signature("partner", "apiKey");
		Long timestamp = System.currentTimeMillis();
		SignatureKey sk = signature.generateSignature();
		assert(sk.getTimestamp().equals(timestamp));
	}

	@Test
	public void itConfirmsASignature(){
		Signature signature = new Signature("partner", "apiKey");
		SignatureKey original = signature.generateSignature();
		assert(signature.validateSignature(original.getTimestamp(), original.getSignature()));
	}
}