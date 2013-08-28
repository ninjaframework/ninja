package ninja.jpa;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.persist.PersistService;

@RunWith(MockitoJUnitRunner.class)
public class JpaInitializerTest {

    @Mock
    PersistService persistService;
    
    @Test
    public void testStart() throws Exception {
        
        JpaInitializer jpaInitializer = new JpaInitializer(persistService);
        jpaInitializer.start();
        
        verify(persistService).start();
        
        // we also verify that the annotation is ok
        Method method = JpaInitializer.class.getMethod("start");
        Start start = method.getAnnotation(Start.class);
    
        assertEquals(10, start.order());
    }
    
    @Test
    public void testStop() throws Exception {
        
        JpaInitializer jpaInitializer = new JpaInitializer(persistService);
        jpaInitializer.stop();
        
        verify(persistService).stop();
        
        // we also verify that the annotation is ok
        Method method = JpaInitializer.class.getMethod("stop");
        Dispose dispose = method.getAnnotation(Dispose.class);
    
        assertEquals(10, dispose.order());
    }

}
