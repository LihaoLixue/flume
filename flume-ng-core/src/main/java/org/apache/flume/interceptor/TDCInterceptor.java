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
import com.google.common.collect.Lists;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Simple Interceptor class that sets the host name or IP on all events
 * that are intercepted.<p>
 * The host header is named <code>host</code> and its format is either the FQDN
 * or IP of the host on which this interceptor is run.
 * <p>
 * <p>
 * Properties:<p>
 * <p>
 * preserveExisting: Whether to preserve an existing value for 'host'
 * (default is false)<p>
 * <p>
 * useIP: Whether to use IP address or fully-qualified hostname for 'host'
 * header value (default is true)<p>
 * <p>
 * hostHeader: Specify the key to be used in the event header map for the
 * host name. (default is "host") <p>
 * <p>
 * Sample config:<p>
 *
 * <code>
 * agent.sources.r1.channels = c1<p>
 * agent.sources.r1.type = SEQ<p>
 * agent.sources.r1.interceptors = i1<p>
 * agent.sources.r1.interceptors.i1.type = host<p>
 * agent.sources.r1.interceptors.i1.preserveExisting = true<p>
 * agent.sources.r1.interceptors.i1.useIP = false<p>
 * agent.sources.r1.interceptors.i1.hostHeader = hostname<p>
 * </code>
 */
public class TDCInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory
            .getLogger(TDCInterceptor.class);
    private static String regex = "";
    Pattern pattern;

    public TDCInterceptor() {
    }

    @Override
    public void initialize() {
        pattern = Pattern.compile(regex);

    }

    @Override
    public Event intercept(Event event) {
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        List<Event> intercepted = Lists.newArrayListWithCapacity(events.size());
        //记录上一个正确匹配的event在队列中的位置,以便下一event有和它连接的需要
        int addnum = 0;
        for (int i = 0; i < events.size(); i++) {
            Event interceptedEvent;
            Matcher matcher = pattern.matcher(
                    new String(events.get(i).getBody(), Charsets.UTF_8));
            if (matcher.find()) {
                interceptedEvent = intercept(events.get(i));
                intercepted.add(interceptedEvent);
                //更新正确匹配的event在队列中的位置
                addnum = i;
            } else {
                if (!intercepted.isEmpty()) {
                    String body = new String(intercepted.get(addnum).getBody(), Charsets.UTF_8) + "$DTC$" + new String(events.get(i).getBody(), Charsets.UTF_8);
                    System.out.println(body);
                    intercepted.get(addnum).setBody(body.getBytes());
                } else {
                    intercepted.clear();
                }
            }
        }
        return intercepted;
    }

    @Override
    public void close() {

    }

    public static class Builder implements Interceptor.Builder {

        @Override
        public Interceptor build() {
            return new TDCInterceptor();
        }

        @Override
        public void configure(Context context) {
            regex = context.getString("param");
        }

    }

}
