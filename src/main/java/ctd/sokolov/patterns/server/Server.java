package ctd.sokolov.patterns.server;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ruslan on 12/25/14.
 */
public class Server {
    private static final ConnectionFactory factory = new ConnectionFactory();
    private static volatile boolean IS_RUN = false;
    private static final int THREADS = 16;
    private static final ExecutorService service = Executors.newFixedThreadPool(THREADS);
    private static final String RPC_QUEUE_NAME = "rpc_queue";

    static {
        factory.setHost("localhost");
    }


    public static void run() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection connection = null;

                Channel channel = null;
                try {
                    connection = factory.newConnection();

                    channel = connection.createChannel();

                    channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

                    channel.basicQos(1);

                    QueueingConsumer consumer = new QueueingConsumer(channel);
                    channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

                    if (!IS_RUN) {
                        IS_RUN = true;
                        while (IS_RUN) {
                            final QueueingConsumer.Delivery delivery = consumer.nextDelivery();

                            final BasicProperties props = delivery.getProperties();
                            final BasicProperties replyProps = new BasicProperties.Builder()
                                    .correlationId(props.getCorrelationId())
                                    .build();

                            final String message = new String(delivery.getBody());
                            final Channel finalChannel = channel;
                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    String response = String.valueOf(ServerBackEnd.getResponse(message));
                                    try {
                                        finalChannel.basicPublish("", props.getReplyTo(), replyProps, response.getBytes());
                                        finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    close(connection);
                }
            }
        });
        t.start();
    }

    private static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            }
            catch (Exception ignore) {}
        }
    }

    public static void stop() {
        IS_RUN = false;
    }
}
