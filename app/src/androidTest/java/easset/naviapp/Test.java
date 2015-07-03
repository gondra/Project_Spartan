package easset.naviapp;

import android.test.InstrumentationTestCase;

/**
 * Created by easset-01 on 6/16/2015.
 */
public class Test extends InstrumentationTestCase{
    public void testDatabase(){
        Database database = new Database();
        assertTrue(database.testCreate());
    }
}
