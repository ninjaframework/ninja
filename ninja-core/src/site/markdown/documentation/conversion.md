Type Conversion
===============

Introduction
------------

Regardless of the architecture of your project, you may need to convert objects
from one type to another. Ninja Framework allows you to perform these conversions
very easily, you only have to take care of the data transformation.

All you have to do is to add the TypeConverter implementations and inject
<code>Conversion</code> in your application.



Adding the dependency and installing the module
-----------------------------------------------

First - add the dependency to your pom.xml:

<pre class="prettyprint">
&lt;dependency&gt;
    &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
    &lt;artifactId&gt;ninja-conversion&lt;/artifactId&gt;
    &lt;version&gt;NINJA_VERSION_YOU_ARE_USING&lt;/version&gt;
&lt;/dependency&gt;
</pre>


and start the module in your <code>Module.java</code> file of your Ninja
application via:

<pre class="prettyprint">
install(new ConversionModule());
</pre>



Configuration
-------------

The thing you need to configure is to tell the conversion module where the
converters are located in your project. To do this, use the
<code>conversions.converter_package_location</code> configuration key in the
<code>application.conf</code> file.

<pre class="prettyprint">
// Indicate where are located defined converters
// for instance: conversions.converter_package_location=com.package.converters
conversions.converter_package_location=converters
</pre>



Converter definition
--------------------

Creating a converter is very simple, you just have to create a class inheriting
from <code>TypeConverter</code> and implement the <code>convert</code> method.

<pre class="prettyprint">
package converters;

import ninja.conversion.TypeConverter;

/**
 * Convert a {@code String} into an {@code Integer}.
 */
public class StringToIntegerConverter implements TypeConverter&#60;String, Integer&#62; {

    @Override
    public Integer convert(final String source) {
        return Integer.valueOf(source);
    }
}
</pre>



Usage
-----

<pre class="prettyprint">
public class ConversionController {

    @Inject
    Conversion conversion

    public void example() {
        final String numberAsString = "42";
        final Integer number = conversion.convert(numberAsString, Integer.class);
    }

    public void exampleExplicit() {
        final String numberAsString = "42";
        final Integer number = conversion.convert(numberAsString, String.class, Integer.class);
    }

    public void exampleCollection() {
        final List&#60;String&#62; numberAsStringList = Arrays.asList("42", "1337");
        final List&#60;Integer&#62; numberList = conversion.convert(numberAsStringList, Integer.class);
    }

    public void exampleCollectionExplicit() {
        final List&#60;String&#62; numberAsStringList = Arrays.asList("42", "1337");
        final List&#60;Integer&#62; numberList = conversion.convert(
            numberAsStringList,
            String.class,
            Integer.class);
    }
}
</pre>



Null safe
---------

The <code>null</code> value is handled by the <code>Conversion</code> service
and will return <code>null</code> directly without calling any converter.

<pre class="prettyprint">
public class ConversionController {

    @Inject
    Conversion conversion

    public void example() {
        // null value will be returned
        conversion.convert(null, Integer.class);

        // A list containing 42, null and 1337 will be returned
        conversion.convert(Arrays.asList("42", null, "1337"), Integer.class);
    }
}
</pre>
