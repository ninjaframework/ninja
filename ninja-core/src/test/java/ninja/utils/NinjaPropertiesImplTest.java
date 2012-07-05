package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class NinjaPropertiesImplTest {

	@Mock
	Logger logger;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSkippingThroughModesWorks() {

		// check that mode tests works:
		System.setProperty("mode", "test");
		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);
		assertEquals("test_testproperty",
		        ninjaPropertiesImpl.get("testproperty"));

		// check that mode dev works:
		System.setProperty("mode", "dev");
		ninjaPropertiesImpl = new NinjaPropertiesImpl(logger);
		assertEquals("dev_testproperty",
		        ninjaPropertiesImpl.get("testproperty"));
		assertEquals("secret", ninjaPropertiesImpl.get("applicationSecret"));

		// remove property => we expect that the dev property is used as default
		// value
		System.clearProperty("mode");
		ninjaPropertiesImpl = new NinjaPropertiesImpl(logger);
		assertEquals("dev_testproperty",
		        ninjaPropertiesImpl.get("testproperty"));
		assertEquals("secret", ninjaPropertiesImpl.get("applicationSecret"));

		// and in a completely different mode with no "%"-prefixed key the
		// default value use used
		System.setProperty("mode", "prod");
		ninjaPropertiesImpl = new NinjaPropertiesImpl(logger);
		assertEquals("testproperty_without_prefix",
		        ninjaPropertiesImpl.get("testproperty"));
		assertEquals("secret", ninjaPropertiesImpl.get("applicationSecret"));

		// tear down
		System.clearProperty("mode");

	}
	
	@Test(expected = RuntimeException.class)
	public void testGetOrDie() {

		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);

		assertEquals("dev_testproperty",
		        ninjaPropertiesImpl.getOrDie("testproperty"));

		ninjaPropertiesImpl.getOrDie("a_propert_that_is_not_in_the_file");

	}

	@Test
	public void testGetBooleanParsing() {

		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);
		assertEquals(true, ninjaPropertiesImpl.getBoolean("booleanTestTrue"));

		assertEquals(false, ninjaPropertiesImpl.getBoolean("booleanTestFalse"));

		assertEquals(null, ninjaPropertiesImpl.getBoolean("booleanNotValid"));

	}
	

	@Test(expected = RuntimeException.class)
	public void testGetBooleanOrDie() {

		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);

		assertEquals(true,
		        ninjaPropertiesImpl.getBooleanOrDie("booleanTestTrue"));

		ninjaPropertiesImpl.getBooleanOrDie("booleanNotValid");

	}

	@Test
	public void testGetIntegerParsing() {

		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);

		assertEquals(new Integer(123456789),
		        ninjaPropertiesImpl.getInteger("integerTest"));

		assertEquals(null, ninjaPropertiesImpl.getInteger("integerNotValid"));

	}

	@Test(expected = RuntimeException.class)
	public void testGetIntegerDie() {

		NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl(
		        logger);

		assertEquals(new Integer(123456789),
		        ninjaPropertiesImpl.getIntegerOrDie("integerTest"));

		ninjaPropertiesImpl.getIntegerOrDie("integerNotValid");

	}




}
