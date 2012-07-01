package ninja.utils;

import static org.junit.Assert.assertEquals;

import ninja.utils.Crypto;

import org.junit.Before;
import org.junit.Test;

public class CryptoTest {
	
	private Crypto crypto;
	
	@Before
	public void setup() {
		
		crypto = new Crypto("Fxu6U5BTGIJZ06c8bD1xkhHc3Ct5JZXlst8tJ1K5uJJPaLdceDo6CUz0iWpjjQUY");
	}
	
	@Test
	public void testSigning() {
		
		assertEquals("f0f591a35650937c9559ee8f98cc29dac46c3fcb", crypto.signHmacSha1("Sentence to sign"));
		assertEquals("ba864c24a2a80a639d4f76bb44fd71650dcd4904", crypto.signHmacSha1("Another sentence to sign"));
		assertEquals("4ad5fb0895dbc0c7172f9fc85d59f74b69f99b8b", crypto.signHmacSha1("Yet another sentence to sign"));
		
	}

}
