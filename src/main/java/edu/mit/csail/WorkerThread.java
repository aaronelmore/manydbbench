package edu.mit.csail;

import org.apache.commons.lang.SerializationUtils;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class WorkerThread {

	public WorkerThread() {
		// TODO Auto-generated constructor stub
	}

	private static void doWork(WorkMsg msg) throws InterruptedException {
	
		Thread.sleep(msg.durationMS);
	}
	
	private final static String QUEUE_NAME = "task_queue";
    static boolean autoAck = false;

	public static void main(String[] argv) throws java.io.IOException,
	             java.lang.InterruptedException {
	
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	
	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

	    int prefetchCount = 1;
	    channel.basicQos(prefetchCount);
	    
	    QueueingConsumer consumer = new QueueingConsumer(channel);

	    channel.basicConsume(QUEUE_NAME, autoAck, consumer);

	    while (true) {
	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	      WorkMsg msg = (WorkMsg)SerializationUtils.deserialize(delivery.getBody());
	      System.out.println(" [x] Received '" + msg.toString() + "'");
	      doWork(msg);
	      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	    }
	    
	}
}
