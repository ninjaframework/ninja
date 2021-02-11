/**
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.utils;

import com.google.common.collect.ImmutableMap;
import javax.inject.Named;

import org.junit.After;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import static org.assertj.core.api.Assertions.assertThat;


public class NinjaPropertiesImplTest {

    @After
    public void tearDown() {
        // make sure the external conf property is removed after the test.
        System.clearProperty(NinjaProperties.NINJA_EXTERNAL_CONF);
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);
    }

    @Test
    public void testSkippingThroughModesWorks() {

        // check that mode tests works:
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.test).build();
        assertThat(ninjaPropertiesImpl.get("testproperty")).isEqualTo("test_testproperty");

        // check that mode dev works:
        ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();
        assertThat(ninjaPropertiesImpl.get("testproperty")).isEqualTo("dev_testproperty");
        assertThat(ninjaPropertiesImpl.get(NinjaConstant.applicationSecret)).isEqualTo("secret");

        // and in a completely different mode with no "%"-prefixed key the
        // default value use used
        ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.prod).build();
        assertThat(ninjaPropertiesImpl.get("testproperty")).isEqualTo("testproperty_without_prefix");
        assertThat(ninjaPropertiesImpl.get(NinjaConstant.applicationSecret)).isEqualTo("secret");

        // tear down
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);

    }

    @Test(expected = RuntimeException.class)
    public void testGetOrDie() {

        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        assertThat(ninjaPropertiesImpl.getOrDie("testproperty")).isEqualTo("dev_testproperty");

        ninjaPropertiesImpl.getOrDie("a_propert_that_is_not_in_the_file");
    }

    @Test
    public void testGetBooleanParsing() {

        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();
        assertThat(ninjaPropertiesImpl.getBoolean("booleanTestTrue")).isTrue();

        assertThat(ninjaPropertiesImpl.getBoolean("booleanTestFalse")).isFalse();

        assertThat(ninjaPropertiesImpl.getBoolean("booleanNotValid")).isNull();

    }

    @Test(expected = RuntimeException.class)
    public void testGetBooleanOrDie() {

        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        assertThat(ninjaPropertiesImpl.getBooleanOrDie("booleanTestTrue")).isTrue();

        ninjaPropertiesImpl.getBooleanOrDie("booleanNotValid");

    }

    @Test
    public void testGetIntegerParsing() {

        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        assertThat(ninjaPropertiesImpl.getInteger("integerTest")).isEqualTo(new Integer(123456789));

        assertThat(ninjaPropertiesImpl.getInteger("integerNotValid")).isNull();

    }

    @Test(expected = RuntimeException.class)
    public void testGetIntegerDie() {

        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        assertThat(ninjaPropertiesImpl.getIntegerOrDie("integerTest")).isEqualTo(new Integer(123456789));

        ninjaPropertiesImpl.getIntegerOrDie("integerNotValid");

    }

    @Test
    public void testPropertiesBoundInGuice() {
        final NinjaPropertiesImpl props = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();
        MockService service = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                props.bindProperties(binder());
            }
        }).getInstance(MockService.class);
        assertThat(service.applicationSecret).isNotNull();
        assertThat(service.applicationSecret).isEqualTo("secret");
    }

    public static class MockService {
        @Inject
        @Named(NinjaConstant.applicationSecret)
        public String applicationSecret;
    }

    @Test
    public void testReferenciningOfPropertiesWorks() {

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf: (fullServerName=${serverName}:${serverPort})
        assertThat(ninjaProperties.get("fullServerName")).isEqualTo("http://myserver.com:80");

    }

    @Test
    public void testLoadingOfExternalConfFile() {

        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF,
                "conf/heroku.conf");

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // we expect that the original application secret gets overwritten:
        assertThat(ninjaProperties.get(NinjaConstant.applicationSecret)).isEqualTo("secretForHeroku");

        // and make sure other properties of heroku.conf get loaded as well:
        assertThat(ninjaProperties.get("heroku.special.property")).isEqualTo("some special parameter");

        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        assertThat(ninjaProperties.get("fullServerName")).isEqualTo("http://myapp.herokuapp.com:80");

    }
    
    @Test
    public void testLoadingOfExternalConfFileOverridesSystemProperty() {

        // we can set an external conf file by setting the following system property
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF,
                "conf/filedoesnotexist.conf");

        // instantiate the properties, but provide a different one
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withExternalConfiguration( "conf/heroku.conf")
                .build();

        // we expect that the original application secret gets overwritten:
        assertThat(ninjaProperties.get(NinjaConstant.applicationSecret)).isEqualTo("secretForHeroku");

        // and make sure other properties of heroku.conf get loaded as well:
        assertThat(ninjaProperties.get("heroku.special.property")).isEqualTo("some special parameter");

        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        assertThat(ninjaProperties.get("fullServerName")).isEqualTo("http://myapp.herokuapp.com:80");

    }

    @Test
    public void testLoadingOfExternalConfigurationFileWorksAndPrefixedConfigurationsSetRead() {

        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF,
                "conf/heroku.conf");

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.test).build();

        // this is testing if referencing of properties works with external
        // configurations
        // and application.conf (fullServerName=${serverName}:${serverPort})
        // It also will be different as we are in "test" mode:
        // "myapp-test" is the important thing here.
        assertThat(ninjaProperties.get("fullServerName")).isEqualTo("http://myapp-test.herokuapp.com:80");

    }

    @Test
    public void testUft8Works() {

        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();
        // We test this: utf8Test=this is utf8: öäü
        assertThat(ninjaProperties.get("utf8Test")).isEqualTo("this is utf8: öäü");

    }

    @Test(expected = RuntimeException.class)
    public void testExernalConfigLoadingBreaksWhenFileDoesNotExist() {

        // we can set an external conf file by setting the following system
        // property:
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/non_existing.conf");

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // now a runtime exception must be thrown.
    }

    @Test
    public void testGetWithDefault() {

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // test default works when property not there:
        assertThat(ninjaProperties.getWithDefault("non_existsing_property_to_check_defaults", "default")).isEqualTo("default");

        // test default works when property is there: => we are int dev mode...
        assertThat(ninjaProperties.getWithDefault("testproperty", "default")).isEqualTo("dev_testproperty");

    }

    @Test
    public void testGetIntegerWithDefault() {

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // test default works when property not there:
        assertThat(ninjaProperties.getIntegerWithDefault("non_existsing_property_to_check_defaults", 1)).isEqualTo(Integer.valueOf(1));

        // test default works when property is there:
        assertThat(ninjaProperties.getIntegerWithDefault("integerTest", 1)).isEqualTo(Integer.valueOf(123456789));

    }

    @Test
    public void testGetBooleanWithDefault() {

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // test default works when property not there:
        assertThat(ninjaProperties.getBooleanWithDefault("non_existsing_property_to_check_defaults", true)).isTrue();

        // test default works when property is there:
        assertThat(ninjaProperties.getBooleanWithDefault("booleanTestTrue", false)).isTrue();

    }

    @Test
    public void testGetStringArrayWorks() {

        // instantiate the properties:
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();

        // test default works when property not there:
        assertThat(ninjaProperties.get("getOneElementStringArray")).isEqualTo("one");
        assertThat(ninjaProperties.getStringArray("getOneElementStringArray")[0]).isEqualTo("one");
        assertThat(ninjaProperties.get("getMultipleElementStringArrayWithSpaces")).isEqualTo("one , me");
        assertThat(ninjaProperties.getStringArray("getMultipleElementStringArrayWithSpaces")[0]).isEqualTo("one");      
        assertThat(ninjaProperties.getStringArray("getMultipleElementStringArrayWithSpaces")[1]).isEqualTo( "me");
        assertThat(ninjaProperties.get("getMultipleElementStringArrayWithoutSpaces")).isEqualTo("one,me");
        assertThat(ninjaProperties.getStringArray("getMultipleElementStringArrayWithoutSpaces")[0]).isEqualTo("one");
        assertThat(ninjaProperties.getStringArray("getMultipleElementStringArrayWithoutSpaces")[1]).isEqualTo("me");

    }
    
    @Test
    public void systemProperties() {
        // verify property in external conf is set
        NinjaProperties ninjaProperties = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withExternalConfiguration("conf/system_property.conf")
                .build();
        
        assertThat(ninjaProperties.get("unit.test.123")).isEqualTo("123-value-via-external-conf");
        
        // verify system property overrides it
        System.setProperty("unit.test.123", "123-value-via-system-property");
        try {
            ninjaProperties = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withExternalConfiguration("conf/system_property.conf")
                .build();
            assertThat(ninjaProperties.get("unit.test.123")).isEqualTo("123-value-via-system-property");
        } finally {
            System.clearProperty("unit.test.123");
        }
        
        // verify prefixed system property overrides both
        System.setProperty("unit.test.123", "123-value-via-system-property");
        System.setProperty("%dev.unit.test.123", "123-value-via-prefixed-system-property");
        try {
            ninjaProperties = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withExternalConfiguration( "conf/system_property.conf")
                .build();
            assertThat(ninjaProperties.get("unit.test.123")).isEqualTo("123-value-via-prefixed-system-property");
        } finally {
            System.clearProperty("unit.test.123");
        }
    }
    
    @Test
    public void programmaticallyOverridingProperties_works() {
        // given initially serverPort=80...
        NinjaProperties originalNinjaProperties = new NinjaPropertiesImpl.Builder().withMode(NinjaMode.dev).build();
        assertThat(originalNinjaProperties.getInteger("serverPort")).isEqualTo(80);
        
        // when
        NinjaProperties actualProperties = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withProperties(ImmutableMap.of("serverPort", "1001"))
                .build();
        
        // then
        assertThat(actualProperties.getInteger("serverPort")).isEqualTo(1001);
    
    }

}
