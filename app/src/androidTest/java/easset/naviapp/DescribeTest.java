package easset.naviapp;

import android.app.Application;
import android.test.ApplicationTestCase;

import Describe.PrepareForGeneratingObject;

public class DescribeTest extends ApplicationTestCase<Application> {
    public DescribeTest() {
        super(Application.class);
    }

    public void testName() throws Exception {
        PrepareForGeneratingObject p = new PrepareForGeneratingObject();
        p.prepare();
    }
}
