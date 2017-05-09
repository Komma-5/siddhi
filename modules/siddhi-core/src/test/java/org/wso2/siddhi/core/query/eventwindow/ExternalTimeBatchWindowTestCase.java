/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.siddhi.core.query.eventwindow;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;

import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;

public class ExternalTimeBatchWindowTestCase {


    private static final Logger log = Logger.getLogger(ExternalTimeBatchWindowTestCase.class);
    private int inEventCount;
    private int removeEventCount;
    private long sum;
    private boolean eventArrived;

    @Before
    public void init() {
        inEventCount = 0;
        removeEventCount = 0;
        eventArrived = false;
    }

    @Test
    public void testExternalTimeBatchWindow1() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test1");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 6 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804342l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335814341l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335814345l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335824341l, "192.10.1.7"});

        Thread.sleep(1000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 2, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testExternalTimeBatchWindow2() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test2");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804342l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805340l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335814341l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335814345l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335824341l, "192.10.1.7"});

        Thread.sleep(1000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 2, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();


    }


    @Test
    public void testExternalTimeBatchWindow3() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test3");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804342l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805341l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335814341l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335814345l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335824341l, "192.10.1.7"});

        Thread.sleep(1000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 3, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();


    }


    @Test
    public void testExternalTimeBatchWindow4() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test4");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 6 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804999l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805000l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805999l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335806000l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335806001l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335824341l, "192.10.1.7"});

        Thread.sleep(1000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 3, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();


    }

    @Test
    public void testExternalTimeBatchWindow5() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test5");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 3 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});

        Thread.sleep(5000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 1, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();

    }

    @Test
    public void testExternalTimeBatchWindow6() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test6");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 3 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";


        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335805599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335805607l, "192.10.1.6"});

        Thread.sleep(5000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 2, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();


    }

    @Test
    public void testExternalTimeBatchWindow7() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test7");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 2 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (inEvents != null) {
                    inEventCount = inEventCount + inEvents.length;
                }
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335805599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335805607l, "192.10.1.6"});
        Thread.sleep(3000);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.7"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.8"});
        Thread.sleep(3000);
        inputHandler.send(new Object[]{1366335806606l, "192.10.1.9"});
        inputHandler.send(new Object[]{1366335806690l, "192.10.1.10"});
        Thread.sleep(3000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 4, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();


    }

    @Test
    public void testExternalTimeBatchWindow8() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test8");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 2 sec) output all events; ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert all events into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                for (Event event : inEvents) {
                    inEventCount++;
                    if (inEventCount == 1) {
                        Assert.assertEquals(4l, event.getData(2));
                    } else if (inEventCount == 2) {
                        Assert.assertEquals(3l, event.getData(2));
                    } else if (inEventCount == 3) {
                        Assert.assertEquals(5l, event.getData(2));
                    } else if (inEventCount == 4) {
                        Assert.assertEquals(7l, event.getData(2));
                    } else if (inEventCount == 5) {
                        Assert.assertEquals(2l, event.getData(2));
                    }
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335805599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335805607l, "192.10.1.6"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.7"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.8"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.91"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.92"});
        inputHandler.send(new Object[]{1366335806606l, "192.10.1.9"});
        inputHandler.send(new Object[]{1366335806690l, "192.10.1.10"});
        Thread.sleep(3000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 5, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testExternalTimeBatchWindow9() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test9");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 2 sec); ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "group by ip " +
                "insert into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("uniqueIps", new StreamCallback() {
            @Override
            public synchronized void receive(Event[] events) {
                if (events != null) {
                    inEventCount = inEventCount + events.length;
                    for (Event event : events) {
                        sum = sum + (Long) event.getData(2);
                    }
                }
                eventArrived = true;
            }
        });

        final InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                int i = 0;
                long time = 1366335804341l;
                while (i < 10000) {

                    try {
                        inputHandler.send(new Object[]{time, "192.10.1." + Thread.currentThread().getId()});
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time += 1000;
                    i++;
                }
            }
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();
        new Thread(runnable).start();


        Thread.sleep(10000);
        Assert.assertEquals(10 * 10000, sum);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testExternalTimeBatchWindow10() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test10");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0, 2 sec); ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                for (Event event : inEvents) {
                    inEventCount++;
                    if (inEventCount == 1) {
                        Assert.assertEquals(4l, event.getData(2));
                    } else if (inEventCount == 2) {
                        Assert.assertEquals(3l, event.getData(2));
                    } else if (inEventCount == 3) {
                        Assert.assertEquals(5l, event.getData(2));
                    } else if (inEventCount == 4) {
                        Assert.assertEquals(7l, event.getData(2));
                    } else if (inEventCount == 5) {
                        Assert.assertEquals(2l, event.getData(2));
                    }
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335805599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335805607l, "192.10.1.6"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.7"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.8"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.91"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.92"});
        inputHandler.send(new Object[]{1366335806606l, "192.10.1.9"});
        inputHandler.send(new Object[]{1366335806690l, "192.10.1.10"});
        Thread.sleep(3000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 5, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testExternalTimeBatchWindow11() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test11");

        SiddhiManager siddhiManager = new SiddhiManager();

        String cseEventStream = "" +
                "define stream LoginEvents (timestamp long, ip string); " +
                "define window LoginWindow (timestamp long, ip string) externalTimeBatch(timestamp, 1 sec, 0); ";
        String query = "" +
                "@info(name = 'query0') " +
                "from LoginEvents " +
                "insert into LoginWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from LoginWindow " +
                "select timestamp, ip, count() as total  " +
                "insert into uniqueIps ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(cseEventStream + query);

        executionPlanRuntime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (removeEvents != null) {
                    removeEventCount = removeEventCount + removeEvents.length;
                }
                for (Event event : inEvents) {
                    inEventCount++;
                    if (inEventCount == 1) {
                        Assert.assertEquals(4l, event.getData(2));
                    } else if (inEventCount == 2) {
                        Assert.assertEquals(7l, event.getData(2));
                    }
                }
                eventArrived = true;
            }

        });


        InputHandler inputHandler = executionPlanRuntime.getInputHandler("LoginEvents");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{1366335804341l, "192.10.1.3"});
        inputHandler.send(new Object[]{1366335804599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335804600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335804607l, "192.10.1.6"});
        inputHandler.send(new Object[]{1366335805599l, "192.10.1.4"});
        inputHandler.send(new Object[]{1366335805600l, "192.10.1.5"});
        inputHandler.send(new Object[]{1366335805607l, "192.10.1.6"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.7"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.8"});
        Thread.sleep(2100);
        inputHandler.send(new Object[]{1366335805606l, "192.10.1.91"});
        inputHandler.send(new Object[]{1366335805605l, "192.10.1.92"});
        inputHandler.send(new Object[]{1366335806606l, "192.10.1.9"});
        inputHandler.send(new Object[]{1366335806690l, "192.10.1.10"});
        Thread.sleep(3000);

        junit.framework.Assert.assertEquals("Event arrived", true, eventArrived);
        junit.framework.Assert.assertEquals("In Events ", 2, inEventCount);
        junit.framework.Assert.assertEquals("Remove Events ", 0, removeEventCount);
        executionPlanRuntime.shutdown();
    }

    @Test
    public void testExternalTimeBatchWindow12() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test12");

        SiddhiManager siddhiManager = new SiddhiManager();

        String streams = "" +
                "define stream cseEventStream (timestamp long, symbol string, price float, volume int); " +
                "define stream twitterStream (timestamp long, user string, tweet string, company string); " +
                "define window cseEventWindow (timestamp long, symbol string, price float, volume int) externalTimeBatch(timestamp, 1 sec, 0); " +
                "define window twitterWindow (timestamp long, user string, tweet string, company string) externalTimeBatch(timestamp, 1 sec, 0); ";
        String query = "" +
                "@info(name = 'query0') " +
                "from cseEventStream " +
                "insert into cseEventWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from twitterStream " +
                "insert into twitterWindow; " +
                "" +
                "@info(name = 'query2') " +
                "from cseEventWindow join twitterWindow " +
                "on cseEventWindow.symbol== twitterWindow.company " +
                "select cseEventWindow.symbol as symbol, twitterWindow.tweet, cseEventWindow.price " +
                "insert into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
        try {
            executionPlanRuntime.addCallback("query2", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    EventPrinter.print(timeStamp, inEvents, removeEvents);
                    if (inEvents != null) {
                        inEventCount += (inEvents.length);
                    }
                    if (removeEvents != null) {
                        removeEventCount += (removeEvents.length);
                    }
                    eventArrived = true;
                }
            });
            InputHandler cseEventStreamHandler = executionPlanRuntime.getInputHandler("cseEventStream");
            InputHandler twitterStreamHandler = executionPlanRuntime.getInputHandler("twitterStream");
            executionPlanRuntime.start();
            cseEventStreamHandler.send(new Object[]{1366335804341l, "WSO2", 55.6f, 100});
            twitterStreamHandler.send(new Object[]{1366335804341l, "User1", "Hello World", "WSO2"});
            twitterStreamHandler.send(new Object[]{1366335805301l, "User2", "Hello World2", "WSO2"});
            cseEventStreamHandler.send(new Object[]{1366335805341l, "WSO2", 75.6f, 100});
            cseEventStreamHandler.send(new Object[]{1366335806541l, "WSO2", 57.6f, 100});
            Thread.sleep(1000);
            junit.framework.Assert.assertEquals(2, inEventCount);
            junit.framework.Assert.assertEquals(0, removeEventCount);
            junit.framework.Assert.assertTrue(eventArrived);
        } finally {
            executionPlanRuntime.shutdown();
        }
    }

    @Test
    public void testExternalTimeBatchWindow13() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test13");

        SiddhiManager siddhiManager = new SiddhiManager();

        String streams = "" +
                "define stream cseEventStream (timestamp long, symbol string, price float, volume int); " +
                "define stream twitterStream (timestamp long, user string, tweet string, company string); " +
                "define window cseEventWindow (timestamp long, symbol string, price float, volume int) externalTimeBatch(timestamp, 1 sec, 0); " +
                "define window twitterWindow (timestamp long, user string, tweet string, company string) externalTimeBatch(timestamp, 1 sec, 0); ";
        String query = "" +
                "@info(name = 'query0') " +
                "from cseEventStream " +
                "insert into cseEventWindow; " +
                "" +
                "@info(name = 'query1') " +
                "from twitterStream " +
                "insert into twitterWindow; " +
                "" +
                "@info(name = 'query2') " +
                "from cseEventWindow join twitterWindow " +
                "on cseEventWindow.symbol== twitterWindow.company " +
                "select cseEventWindow.symbol as symbol, twitterWindow.tweet, cseEventWindow.price " +
                "insert all events into outputStream ;";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);
        try {
            executionPlanRuntime.addCallback("query2", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                    EventPrinter.print(timeStamp, inEvents, removeEvents);
                    if (inEvents != null) {
                        inEventCount += (inEvents.length);
                    }
                    if (removeEvents != null) {
                        removeEventCount += (removeEvents.length);
                    }
                    eventArrived = true;
                }
            });
            InputHandler cseEventStreamHandler = executionPlanRuntime.getInputHandler("cseEventStream");
            InputHandler twitterStreamHandler = executionPlanRuntime.getInputHandler("twitterStream");
            executionPlanRuntime.start();
            cseEventStreamHandler.send(new Object[]{1366335804341l, "WSO2", 55.6f, 100});
            twitterStreamHandler.send(new Object[]{1366335804341l, "User1", "Hello World", "WSO2"});
            twitterStreamHandler.send(new Object[]{1366335805301l, "User2", "Hello World2", "WSO2"});
            cseEventStreamHandler.send(new Object[]{1366335805341l, "WSO2", 75.6f, 100});
            cseEventStreamHandler.send(new Object[]{1366335806541l, "WSO2", 57.6f, 100});
            Thread.sleep(1000);
            junit.framework.Assert.assertEquals(2, inEventCount);
            junit.framework.Assert.assertEquals(1, removeEventCount);
            junit.framework.Assert.assertTrue(eventArrived);
        } finally {
            executionPlanRuntime.shutdown();
        }
    }

    @Test
    public void testExternalTimeBatchWindow14() throws Exception {
        log.info("ExternalTimeBatchWindow test14");

        SiddhiManager siddhiManager = new SiddhiManager();

        String query = "define stream jmxMetric(cpu int, timestamp long); " +
                "define window jmxMetricWindow(cpu int, timestamp long) externalTimeBatch(timestamp, 10 sec);" +
                "@info(name = 'query0') " +
                "from jmxMetric " +
                "insert into jmxMetricWindow; " +
                "" +
                "@info(name='query1') " +
                "from jmxMetricWindow " +
                "select avg(cpu) as avgCpu, count(1) as count insert into tmp;";

        ExecutionPlanRuntime runtime = siddhiManager.createExecutionPlanRuntime(query);

        final AtomicInteger recCount = new AtomicInteger(0);
        runtime.addCallback("query1", new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                Assert.assertEquals(1, inEvents.length);
                recCount.incrementAndGet();
                double avgCpu = (Double) inEvents[0].getData()[0];
                if (recCount.get() == 1) {
                    Assert.assertEquals(15, avgCpu, 0);
                } else if (recCount.get() == 2) {
                    Assert.assertEquals(85, avgCpu, 0);
                }
                long count = (Long) inEvents[0].getData()[1];
                Assert.assertEquals(3, count);
            }
        });

        InputHandler input = runtime.getInputHandler("jmxMetric");
        runtime.start();
        // external events' time stamp less than the window, should not have event recieved in call back.
        long now = 0;
        int length = 3;
        for (int i = 0; i < length; i++) {
            input.send(new Object[]{15, now + i * 10});
        }

        // second round
        // if the trigger event mix with the last window, we should see the avgValue is not expected
        for (int i = 0; i < length; i++) {
            input.send(new Object[]{85, now + 10000 + i * 10}); // the first entity of the second round
        }
        // to trigger second round
        input.send(new Object[]{10000, now + 10 * 10000});

        Thread.sleep(1000);

        Assert.assertEquals(2, recCount.get());
    }

    @Test
    public void testExternalTimeBatchWindow15() throws Exception {
        log.info("ExternalTimeBatchWindow test15");

        SiddhiManager siddhiManager = new SiddhiManager();

        String stream = "define stream jmxMetric(cpu int, memory int, bytesIn long, bytesOut long, timestamp long); " +
                "define window jmxMetricWindow(cpu int, memory int, bytesIn long, bytesOut long, timestamp long) externalTimeBatch(timestamp, 10 sec) output current events; ";

        String query = "" +
                "@info(name = 'query0') " +
                "from jmxMetric " +
                "insert into jmxMetricWindow; " +
                "" +
                "@info(name = 'downSample') "
                + "from jmxMetricWindow "
                + "select avg(cpu) as avgCpu, max(cpu) as maxCpu, min(cpu) as minCpu, "
                + " '|' as s, "
                + " avg(memory) as avgMem, max(memory) as maxMem, min(memory) as minMem, "
                + " '|' as s1, "
                + " avg(bytesIn) as avgBytesIn, max(bytesIn) as maxBytesIn, min(bytesIn) as minBytesIn, "
                + " '|' as s2, "
                + " avg(bytesOut) as avgBytesOut, max(bytesOut) as maxBytesOut, min(bytesOut) as minBytesOut, "
                + " '|' as s3, "
                + " timestamp as timeWindowEnds, "
                + " '|' as s4, "
                + " count(1) as metric_count "
                + " INSERT INTO tmp;";

        SiddhiManager sm = new SiddhiManager();
        ExecutionPlanRuntime plan = sm.createExecutionPlanRuntime(stream + query);

        InputHandler input = plan.getInputHandler("jmxMetric");

        // stream call back doesn't follow the counter
        final AtomicInteger counter = new AtomicInteger();
        {
            // stream callback
            plan.addCallback("jmxMetric", new StreamCallback() {
                @Override
                public void receive(Event[] arg0) {
                    counter.addAndGet(arg0.length);
                }
            });
        }
        final AtomicInteger queryWideCounter = new AtomicInteger();
        {
            plan.addCallback("downSample", new QueryCallback() {
                @Override
                public void receive(long timeStamp, Event[] inevents, Event[] removevents) {
                    int currentCount = queryWideCounter.addAndGet(inevents.length);
                    log.info(MessageFormat.format("Round {0} ====", currentCount));
                    log.info(" events count " + inevents.length);

                    EventPrinter.print(inevents);
                }

            });
        }

        plan.start();

        int round = 4;
        int eventsPerRound = 0;
        long externalTs = System.currentTimeMillis();
        for (int i = 0; i < round; i++) {
            eventsPerRound = sendEvent(input, i, externalTs);
            Thread.sleep(3000);
        }
        // trigger next round
        sendEvent(input, round, externalTs);

        plan.shutdown();
        Thread.sleep(1000);
        Assert.assertEquals(round * eventsPerRound + eventsPerRound, counter.get());
        Assert.assertEquals(round, queryWideCounter.get());
    }

    // one round of sending events
    private int sendEvent(InputHandler input, int ite, long externalTs) throws Exception {
        int len = 3;
        Event[] events = new Event[len];
        for (int i = 0; i < len; i++) {
            // cpu int, memory int, bytesIn long, bytesOut long, timestamp long
            events[i] = new Event(externalTs,
                    new Object[]{15 + 10 * i * ite, 1500 + 10 * i * ite, 1000L, 2000L, externalTs + ite * 10000 + i * 50});
        }

        input.send(events);
        return len;
    }

    @Test
    public void testExternalTimeBatchWindow16() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test16");

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream inputStream(currentTime long,value int); " +
                "define window inputWindow(currentTime long,value int) externalTimeBatch(currentTime,5 sec); ";
        String query = " " +
                "@info(name = 'query0') " +
                "from inputStream " +
                "insert into inputWindow; " +
                "" +
                "@info(name='query') " +
                "from inputWindow " +
                "select value " +
                "insert into outputStream; ";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
        executionPlanRuntime.addCallback("query", new QueryCallback() {
            int count = 0;

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (count == 0) {
                    Assert.assertEquals(1, inEvents[0].getData(0));
                } else if (count == 1) {
                    Assert.assertEquals(6, inEvents[0].getData(0));
                } else if (count == 2) {
                    Assert.assertEquals(13, inEvents[0].getData(0));
                }
                count += 1;
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{10000L, 1});
        Thread.sleep(100);
        inputHandler.send(new Object[]{11000L, 2});
        Thread.sleep(100);
        inputHandler.send(new Object[]{12000L, 3});
        Thread.sleep(100);
        inputHandler.send(new Object[]{13000L, 4});
        Thread.sleep(100);
        inputHandler.send(new Object[]{14000L, 5});
        Thread.sleep(100);
        inputHandler.send(new Object[]{15000L, 6});
        Thread.sleep(100);
        inputHandler.send(new Object[]{16500L, 7});
        Thread.sleep(100);
        inputHandler.send(new Object[]{17000L, 8});
        Thread.sleep(100);
        inputHandler.send(new Object[]{18000L, 9});
        Thread.sleep(100);
        inputHandler.send(new Object[]{19000L, 10});
        Thread.sleep(100);
        inputHandler.send(new Object[]{20000L, 11});
        Thread.sleep(100);
        inputHandler.send(new Object[]{20500L, 12});
        Thread.sleep(100);
        inputHandler.send(new Object[]{22000L, 13});
        Thread.sleep(100);
        inputHandler.send(new Object[]{23000L, 14});
        Thread.sleep(100);
    }

    @Test
    public void testExternalTimeBatchWindow17() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test17");

        SiddhiManager siddhiManager = new SiddhiManager();

        String inputStream = "define stream inputStream(currentTime long,value int); " +
                "define window inputWindow(currentTime long,value int) externalTimeBatch(currentTime,5 sec,1200); ";
        String query = " " +
                "@info(name = 'query0') " +
                "from inputStream " +
                "insert into inputWindow; " +
                "" +
                "@info(name='query') " +
                "from inputWindow " +
                "select value " +
                "insert into outputStream; ";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
        executionPlanRuntime.addCallback("query", new QueryCallback() {
            int count = 0;

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                if (count == 0) {
                    Assert.assertEquals(0L, inEvents[0].getData(0));
                    Assert.assertEquals(11L, inEvents[inEvents.length - 1].getData(0));
                }
                if (count == 1) {
                    Assert.assertEquals(12L, inEvents[0].getData(0));
                }
                count += 1;

            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        for (long i = 0; i < 10000; i += 100) {
            inputHandler.send(new Object[]{i + 10000, i / 100});
            Thread.sleep(200);
        }

    }

    @Test
    public void testExternalTimeBatchWindow18() throws InterruptedException {
        log.info("ExternalTimeBatchWindow test18");

        SiddhiManager siddhiManager = new SiddhiManager();
        String inputStream = "define stream inputStream(currentTime long,value int); " +
                "define window inputWindow(currentTime long,value int) externalTimeBatch(currentTime,5 sec, 0, 6 sec) output current events; ";
        String query = " " +
                "@info(name = 'query0') " +
                "from inputStream " +
                "insert into inputWindow; " +
                "" +
                "@info(name='query') " +
                "from inputWindow " +
                "select value, currentTime " +
                "insert into outputStream; ";

        ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(inputStream + query);
        executionPlanRuntime.addCallback("query", new QueryCallback() {
            int count = 0;

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                if (count == 0) {
                    Assert.assertEquals(1, inEvents[0].getData(0));
                } else if (count == 1) {
                    Assert.assertEquals(6, inEvents[0].getData(0));
                } else if (count == 2) {
                    Assert.assertEquals(11, inEvents[0].getData(0));
                } else if (count == 3) {
                    Assert.assertEquals(14, inEvents[0].getData(0));
                } else if (count == 4) {
                    Assert.assertEquals(15, inEvents[0].getData(0));
                }
                count += 1;
            }
        });

        InputHandler inputHandler = executionPlanRuntime.getInputHandler("inputStream");
        executionPlanRuntime.start();

        inputHandler.send(new Object[]{10000L, 1});
        Thread.sleep(100);
        inputHandler.send(new Object[]{11000L, 2});
        Thread.sleep(100);
        inputHandler.send(new Object[]{12000L, 3});
        Thread.sleep(100);
        inputHandler.send(new Object[]{13000L, 4});
        Thread.sleep(100);
        inputHandler.send(new Object[]{14000L, 5});
        Thread.sleep(100);
        inputHandler.send(new Object[]{15000L, 6});
        Thread.sleep(100);
        inputHandler.send(new Object[]{16500L, 7});
        Thread.sleep(100);
        inputHandler.send(new Object[]{17000L, 8});
        Thread.sleep(100);
        inputHandler.send(new Object[]{18000L, 9});
        Thread.sleep(100);
        inputHandler.send(new Object[]{19000L, 10});
        Thread.sleep(100);
        inputHandler.send(new Object[]{20100L, 11});
        Thread.sleep(100);
        inputHandler.send(new Object[]{20500L, 12});
        Thread.sleep(100);
        inputHandler.send(new Object[]{22000L, 13});
        Thread.sleep(100);
        inputHandler.send(new Object[]{25000L, 14});
        Thread.sleep(100);
        inputHandler.send(new Object[]{32000L, 15});
        Thread.sleep(100);
        inputHandler.send(new Object[]{33000L, 16});
        Thread.sleep(6000);

    }

}