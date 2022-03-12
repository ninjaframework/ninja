package ninja.conversion;

import ninja.conversion.converter.StringToIntegerConverter;
import ninja.conversion.exception.ConverterDoesNotExistException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConversionTestImplicitSourceType {

    private Conversion conversion;

    @Before
    public void initializeConversion() {
        final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap = new HashMap<>();
        converterMap.put(new ConverterCompositeKey(String.class, Integer.class),
                new StringToIntegerConverter());

        conversion = new ConversionImpl(converterMap);
    }

    @Test
    public void testCanConvertByValueTrue() {
        final boolean actual = conversion.canConvert("42", Integer.class);

        Assert.assertTrue(actual);
    }

    @Test
    public void testCanConvertByValueFalse() {
        final boolean actual = conversion.canConvert("42", BigDecimal.class);

        Assert.assertFalse(actual);
    }

    @Test
    public void testCanConvertNull() {
        final boolean actual = conversion.canConvert((String) null, Integer.class);

        Assert.assertTrue(actual);
    }

    @Test
    public void testIterable() {
        final Iterable<String> valueIterable = Arrays.asList("1", "-42", "1337");
        final List<Integer> actualList = conversion.convert(valueIterable, Integer.class);

        Assert.assertNotNull(actualList);
        Assert.assertEquals(3, actualList.size());
        Assert.assertEquals(Integer.valueOf(1), actualList.get(0));
        Assert.assertEquals(Integer.valueOf(-42), actualList.get(1));
        Assert.assertEquals(Integer.valueOf(1337), actualList.get(2));
    }

    @Test
    public void testIterableNull() {
        final Iterable<String> valueList = null;
        final List<Integer> actualSet = conversion.convert(valueList, Integer.class);

        Assert.assertNull(actualSet);
    }

    @Test
    public void testList() {
        final List<String> valueList = Arrays.asList("1", "-42", "1337");
        final List<Integer> actualList = conversion.convert(valueList, Integer.class);

        Assert.assertNotNull(actualList);
        Assert.assertEquals(3, actualList.size());
        Assert.assertEquals(Integer.valueOf(1), actualList.get(0));
        Assert.assertEquals(Integer.valueOf(-42), actualList.get(1));
        Assert.assertEquals(Integer.valueOf(1337), actualList.get(2));
    }

    @Test
    public void testListEmpty() {
        final List<String> valueList = Collections.emptyList();
        final List<Integer> actualList = conversion.convert(valueList, Integer.class);

        Assert.assertNotNull(actualList);
        Assert.assertTrue(actualList.isEmpty());
    }

    @Test
    public void testListNull() {
        final List<String> valueList = null;
        final List<Integer> actualList = conversion.convert(valueList, Integer.class);

        Assert.assertNull(actualList);
    }

    @Test(expected = ConverterDoesNotExistException.class)
    public void testListUnknownConverter() {
        final List<String> valueList = Arrays.asList("1", "-42", "1337");
        conversion.convert(valueList, BigDecimal.class);
    }

    @Test
    public void testListWithNullValue() {
        final List<String> valueList = Arrays.asList("1", null, "1337");
        final List<Integer> actualList = conversion.convert(valueList, Integer.class);

        Assert.assertNotNull(actualList);
        Assert.assertEquals(3, actualList.size());
        Assert.assertEquals(Integer.valueOf(1), actualList.get(0));
        Assert.assertNull(actualList.get(1));
        Assert.assertEquals(Integer.valueOf(1337), actualList.get(2));
    }

    @Test
    public void testSet() {
        final Set<String> valueSet = new HashSet<>(Arrays.asList("1", "-42", "1337"));
        final Set<Integer> actualSet = conversion.convert(valueSet, Integer.class);

        Assert.assertNotNull(actualSet);
        Assert.assertEquals(3, actualSet.size());

        final List<Integer> actualList = actualSet.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(Integer.valueOf(-42), actualList.get(0));
        Assert.assertEquals(Integer.valueOf(1), actualList.get(1));
        Assert.assertEquals(Integer.valueOf(1337), actualList.get(2));
    }

    @Test
    public void testSetEmpty() {
        final Set<String> valueSet = Collections.emptySet();
        final Set<Integer> actualSet = conversion.convert(valueSet, Integer.class);

        Assert.assertNotNull(actualSet);
        Assert.assertTrue(actualSet.isEmpty());
    }

    @Test
    public void testSetNull() {
        final Set<String> valueList = null;
        final Set<Integer> actualSet = conversion.convert(valueList, Integer.class);

        Assert.assertNull(actualSet);
    }

    @Test
    public void testSetWithNullValue() {
        final Set<String> valueSet = new HashSet<>(Arrays.asList("1", null, "1337"));
        final Set<Integer> actualSet = conversion.convert(valueSet, Integer.class);

        Assert.assertNotNull(actualSet);
        Assert.assertEquals(3, actualSet.size());

        final List<Integer> actualList = actualSet
                .stream()
                .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                .collect(Collectors.toList());
        Assert.assertEquals(Integer.valueOf(1), actualList.get(0));
        Assert.assertEquals(Integer.valueOf(1337), actualList.get(1));
        Assert.assertNull(actualList.get(2));
    }

    @Test
    public void testSimpleValue() {
        final Integer actual = conversion.convert("42", Integer.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(Integer.valueOf(42), actual);
    }

    @Test
    public void testSimpleValueNull() {
        final Integer actual = conversion.convert((String) null, Integer.class);

        Assert.assertNull(actual);
    }

    @Test(expected = ConverterDoesNotExistException.class)
    public void testUnknownConverter() {
        conversion.convert("42", BigDecimal.class);
    }

    @Test
    public void testUnknownConverterWithNullValue() {
        // Null can always be converter, so we don't looking for any existing converter
        conversion.convert((String) null, BigDecimal.class);
    }

}
