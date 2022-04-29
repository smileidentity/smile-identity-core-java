package smile.identity.core;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class UtilitiesTest {
	
	private static final int PORT = 3200; //Random port number, arbitrarily chosen
	private static final String TEST_BASE_URL = "http://localhost:" + PORT;
	private String POST_REQUEST = "POST";
	private String API_KEY = "<API_KEY>";
	private String PARTNER_ID = "<PARTNER_ID>";
	private Utilities mUtils = null;
	private MockWebServer mMockServer = null;
	private MockResponse mMockResponse = null;
	
	@Before
	public void setup() throws IOException {
		mMockServer = new MockWebServer();
		mMockServer.start(PORT);
		mMockResponse = new MockResponse();
		mUtils = new Utilities(PARTNER_ID, API_KEY, TEST_BASE_URL);
	}
	
	@Test
	public void testJobStatus() throws InvalidKeyException, IllegalArgumentException, UnsupportedOperationException, NoSuchAlgorithmException, ParseException, RuntimeException, java.text.ParseException, IOException, InterruptedException {
		mMockResponse.setResponseCode(200);
		mMockResponse.setBody("");
		mMockServer.enqueue(mMockResponse);
		
		String endpoint = "/job_status";
		String userId = "";
		String jobId = "";

		mUtils.getJobStatus(userId, jobId, "{}");
		
		RecordedRequest request = mMockServer.takeRequest();
		assertEquals(POST_REQUEST, request.getMethod());
		assertEquals(endpoint, request.getPath());
		assertEquals((TEST_BASE_URL + endpoint), request.getRequestUrl().toString());
	}
	
	@After
	public void reset() throws IOException {
		mMockServer.shutdown();
		mMockServer.close();
		mMockResponse = null;
		mUtils = null;
	}
}