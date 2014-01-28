Advanced
========

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
