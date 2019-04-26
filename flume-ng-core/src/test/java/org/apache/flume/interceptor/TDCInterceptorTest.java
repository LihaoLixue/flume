/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.interceptor;

import com.google.common.base.Charsets;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TDCInterceptorTest {
    @Test
    public void testCustomHeader() throws Exception {
        Interceptor.Builder builder = InterceptorBuilderFactory.newInstance(
                InterceptorType.TDC.toString());
        Context ctx = new Context();
        ctx.put("param", "^\\d{2}\\s+\\w+\\s+\\d{4}.*$");
        builder.configure(ctx);
        Interceptor interceptor = builder.build();

        Event eventBeforeIntercept = EventBuilder.withBody("11 Apr 2019 09:41:06,975 ERROR [lifecycleSupervisor-1-0] (org.apache.flume.source.taildir.ReliableTaildirEventReader.loadPositionFile:146)",
                Charsets.UTF_8);
        Event eventBeforeIntercept1 = EventBuilder.withBody("java.io.EOFException: End of input at line 1 column 1", Charsets.UTF_8);
        Event eventBeforeIntercept2 = EventBuilder.withBody("test event",
                Charsets.UTF_8);
        Event eventBeforeIntercept3 = EventBuilder.withBody("test event",
                Charsets.UTF_8);
        Event eventBeforeIntercept4 = EventBuilder.withBody("test event",
                Charsets.UTF_8);
        Event eventBeforeIntercept5 = EventBuilder.withBody("11 Apr 2019 09:41:06,980 INFO  [lifecycleSupervisor-1-0] (org.",
                Charsets.UTF_8);
        List<Event> events = new ArrayList<>();
        events.add(eventBeforeIntercept);
        events.add(eventBeforeIntercept1);
        events.add(eventBeforeIntercept2);
        events.add(eventBeforeIntercept3);
        events.add(eventBeforeIntercept4);
        events.add(eventBeforeIntercept5);
        interceptor.initialize();
        List<Event> events1 =interceptor.intercept(events);


    }


}