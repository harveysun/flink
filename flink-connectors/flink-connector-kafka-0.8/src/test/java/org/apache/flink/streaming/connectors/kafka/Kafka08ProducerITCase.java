/*
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

package org.apache.flink.streaming.connectors.kafka;

/**
 * IT cases for the {@link FlinkKafkaProducer08}.
 */
@SuppressWarnings("serial")
public class Kafka08ProducerITCase extends KafkaProducerTestBase {

	@Override
	public void testExactlyOnceRegularSink() throws Exception {
		// Kafka08 does not support exactly once semantic
	}

	@Override
	public void testExactlyOnceCustomOperator() throws Exception {
		// Kafka08 does not support exactly once semantic
	}

	@Override
	public void testOneToOneAtLeastOnceRegularSink() throws Exception {
		// TODO: enable this for Kafka 0.8 - now it hangs indefinitely
	}

	@Override
	public void testOneToOneAtLeastOnceCustomOperator() throws Exception {
		// Disable this test since FlinkKafka08Producer doesn't support custom operator mode
	}
}
