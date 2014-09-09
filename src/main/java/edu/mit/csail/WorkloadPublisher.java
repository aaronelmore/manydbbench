package edu.mit.csail;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class WorkloadPublisher {

	public WorkloadPublisher(long duration) throws java.io.IOException {
	    ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
	    WorkMsg msg = new WorkMsg("http://hello.com/world", 1, 1000);
	    channel.basicPublish("", QUEUE_NAME, null, SerializationUtils.serialize(msg) );
	    System.out.println(" [x] Sent '" + msg.toString() + "'");
	    
	    channel.close();
	    connection.close();
	}

	private final static String QUEUE_NAME = "task_queue";
	
	public static void main(String[] argv) throws java.io.IOException {
		long duration = 1000;
		if (argv.length == 1){
			duration = Long.parseLong(argv[0]) * 1000;
		}
		WorkloadPublisher wp = new WorkloadPublisher(duration);
	}
	
}
