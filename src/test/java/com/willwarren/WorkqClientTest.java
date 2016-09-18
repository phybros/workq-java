package com.willwarren;

import com.willwarren.exceptions.ResponseException;
import com.willwarren.model.BackgroundJob;
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

//    @Test
//    public void testForegroundJob() throws Exception {
//        ForegroundJob job = new ForegroundJob();
//        job.setName("testForegroundJob");
//        job.setId(UUID.randomUUID());
//        job.setTimeout(60000);
//        job.setPayload("TEST".getBytes());
//        job.setPriority(100);
//        job.setTtr(1000);
//
//        Assert.assertTrue(this.client.run(job) > 0);
//    }

    @Test
    public void testForegroundJobTimeout() throws Exception {
        ForegroundJob job = new ForegroundJob();
        job.setName("testForegroundJobTimeout");
        job.setId(UUID.randomUUID());
        job.setTimeout(1000);
        job.setPayload("TEST".getBytes());
        job.setPriority(100);
        job.setTtr(1000);

        try {
            this.client.run(job);
        } catch (ResponseException re) {
            Assert.assertEquals("TIMED-OUT", re.getResponseErrorCode());
        }
    }

    @Test
    public void testBackgroundJob() throws Exception {
        BackgroundJob job = new BackgroundJob();
        job.setName("testBackgroundJob");
        job.setId(UUID.randomUUID());
        job.setTtl(3600000);
        job.setPayload("Test Payload!".getBytes());
        job.setPriority(100);
        job.setTtr(1000);

        job.setMaxAttempts(2);

        this.client.add(job);
    }
}
