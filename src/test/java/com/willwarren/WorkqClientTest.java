package com.willwarren;

import com.willwarren.model.ForegroundJob;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by will on 2016-09-16.
 */
public class WorkqClientTest {

    private WorkqClient client;

    @org.junit.Before
    public void setUp() throws Exception {
        this.client = new WorkqClient("localhost", 9922);
    }

    @Test
    public void testForegroundJob() throws Exception {

        ForegroundJob foregroundJob = new ForegroundJob();
        foregroundJob.setName("MYJOB");
        foregroundJob.setId(UUID.randomUUID());
        foregroundJob.setTimeout(60000);
        foregroundJob.setPayload("TEST".getBytes());
        foregroundJob.setPriority(100);
        foregroundJob.setTtr(1000);

        Assert.assertTrue(this.client.run(foregroundJob) > 0);
    }
}
