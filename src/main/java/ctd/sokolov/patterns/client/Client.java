package ctd.sokolov.patterns.client;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.io.IOException;

/**
 * Created by ruslan on 12/26/14.
 */
public class Client {

    private static Connection connection;
    private static Channel channel;
    private static String requestQueueName = "rpc_queue";
    private static String replyQueueName;
    private static QueueingConsumer consumer;

    static {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();

            replyQueueName = channel.queueDeclare().getQueue();
            consumer = new QueueingConsumer(channel);
            channel.basicConsume(replyQueueName, true, consumer);
        } catch (IOException e) {
            System.out.println("Error while creating client");
            e.printStackTrace();
        }
    }

    public static int call(String message) throws Exception {
        String response;
        String corrId = java.util.UUID.randomUUID().toString();

        BasicProperties props = new BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueueName)
                .build();

        channel.basicPublish("", requestQueueName, props, message.getBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
                response = new String(delivery.getBody());
                break;
            }
        }

        return Integer.parseInt(response);
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (IOException e) {
            System.out.println("Error occurred while close client");
        }
    }

    @Override
    public void finalize() {
        close();
    }
}
