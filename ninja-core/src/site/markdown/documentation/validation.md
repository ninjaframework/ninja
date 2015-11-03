Validation
=========

Introduction
------------

Ninja uses Hibernate's implementation of the <code>javax.validation</code> feature. 
This means that all JSR303-defined annotations work in your 
DTO objects when you let Ninja inject parameters into your controller. 
All you have to do is to set the <code>@JSR303Validation</code> 
annotation in front of your method-parameter and catch the result of 
the validation by injecting a <code>Validation</code>-parameter the same way.

An exemplary controller method would look like:

<pre class="prettyprint">
public Result postConfirmedPublication(
    @ProfileId String profileId,
    @JSR303Validation Dto dto, 
    Validation validation) {
        ...
}
</pre>

In this example we want the context to be parsed as <code>profileId</code> 
and a <code>Dto</code>-object. Because of the leading 
<code>@JSR303Validation</code>-annotation all public fields in <code>Dto</code> 
may contain JSR303-annotations like this:

<pre class="prettyprint">
public class Dto {
        @Pattern(regexp = "[a-z]*")
        public String regex;
        @Size(min = 5, max = 10)
        public String length;
        @Min(value = 3)
        @Max(value = 10)
        public int range;
}
</pre>

You can check the result of the validation by evaluating the 
<code>validation</code>-parameter by calling its 
<code>validation.hasBeanViolations()</code>-method in your 
controller which gives you a <code>true</code> or <code>false</code>.
You have the ability to check which field(s) inside your Dto
caused the violations by using the <code>getBeanViolations()</code> 
method which gives you a complete list of all occured violations in your Dto.

Conclusion
----------

JSR303 based validation is a generic approach that allows you to check 
the validity of submitted objects. These objects can be sent to your application
via HTTP form submission, JSON or XML.

The only important part is that your DTO objects contain valid JSR303 validation
annotation which Ninja can evaluate.

If you want to validate nested DTOs or get in deeper detail, visit this great 
site: [JBOSS Validation API](https://docs.jboss.org/hibernate/validator/4.3/reference/en-US/html/validator-usingvalidator.html "JBOSS Validation API")
