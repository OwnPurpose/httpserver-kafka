package com.kafka;

import java.util.Properties;

import com.protobuff.AccountBook.Account;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * <p>
 *  This is a Kafka Producer. This will send received Account data to specified Kafka Queue.
 * </p>
 */
public class KafkaProducer {

	public void send(Account account) {
		
		Properties props = new Properties();
		
		props.put("zk.connet", "localhost:2181");
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("metadata.broker.list", "localhost:9092");
		
		ProducerConfig config = new ProducerConfig(props);
	
		Producer<String, String> pro = new Producer<>(config);
		
		pro.send(new KeyedMessage<String, String>("TutorialTopic", account.toString()));
	}
}
