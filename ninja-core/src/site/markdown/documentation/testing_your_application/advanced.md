Advanced
========

NinjaRunner
-----------

NinjaRunner makes it easy to test DAOs and service objects.
Service objects are generally the place where business logic executed.
Each service object may be injected a lot of DAOs.

Suppose you have some service objects ,
without NinjaRunner , the only way to test service objects is to <code>getInstance()</code>
of these DAOs
from Guice's injector and manually constructor-injecting to the service object
in NinjaTest's <code>@Before</code> method.

With NinjaRunner, you just add <code>@RunWith(NinjaRunner.class)</code> to your test class ,
and declare <code>@Inject private ServiceObj serviceObj</code> and you'll get an
injected serviceObj. No more <code>@Before</code> methods.

Code Example:
 
<pre class="prettyprint">
&#064;RunWith(NinjaRunner.class)
public class DataServiceTest  {
    &#064;Inject private ServiceObj serviceObj;

    &#064;Test
    public void testDataService() {
        assert (serviceObj != null);
    }
}
</pre>



NinjaDaoTestBase
----------------

Sometimes you need to test a DAO method directly on a real Database, 
just extend NinjaDaoTestBase and instantiate the DAO class calling 
the <code>getInstance(...)</code> method from the super 
class and start using it in your test methods. 

This helper starts the Persistence Service using the parameters of the 
application.conf file. You can pass the NinjaMode (test, dev, prod) or 
set it via command line. If no NinjaMode is passed NinjaDaoTestBase 
assumes NinjaMode.test as default.

Check an example:

<pre class="prettyprint">
import ninja.NinjaDaoTestBase;
import org.junit.*;

public class AbstractDaoTest extends NinjaDaoTestBase {

    private TestDao testDao;

    @Before
    public void setup(){
        //Instanting DAO using super method
        testDao = getInstance(TestDao.class);
    }


    @Test
    public void testSave() {
        MyEntity myEntity = new MyEntity();
        myEntity.setEmail("asdf@asdf.co");
        assertNull(myEntity.getId());

        //Use the DAO with a real database
        myEntity = testDao.save(myEntity);

        assertNotNull(entity.getId());
    }
}
</pre>
