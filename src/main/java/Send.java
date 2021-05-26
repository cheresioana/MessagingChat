import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;

public class Send {

    private final static String QUEUE_MESSAGE = "messages";

    public static void main(String[] argv) throws Exception {

        // Connecting to cloud using url
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://mendpuhm:Ny0KzCPeb7NaQTn3GXsJxp8S8PmSuz7A@hawk.rmq.cloudamqp.com/mendpuhm");

        try {
            //attribute id to client
            int id = Integer.parseInt(argv[0]);
            System.out.println("Started client with id: [ " + id + " ]");

            //connect to messaging queue
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_MESSAGE, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                CustomData data = mapper.readValue(message, CustomData.class);
                //check if the current process should read the message (check if it's id is in destinatari)
                if (data.getDestinatar().contains("" + id)){
                    System.out.println(" [x] Received '" + message + "'");
                    data.setDestinatar(data.getDestinatar().replace("" + id, ""));
                    if (!data.getDestinatar().trim().isEmpty()){
                        //if there are still processes that didn't receive the message re-add it to the queue
                        System.out.println("Inca mai sunt destinatari care nu au primit mesajul: " + data.getDestinatar());
                        channel.basicPublish("", QUEUE_MESSAGE, null, data.toString().getBytes());
                    }
                }
                else{
                    //if the process should not read the message or if the message if for broadcast re-add it into the queue
                    channel.basicPublish("", QUEUE_MESSAGE, null, message.getBytes());
                    //check if the message is broadcast
                    if (data.getDestinatar().contains("-1")){
                        System.out.println(" [x] Received '" + message + "'");
                    }
                }

            };
            channel.basicConsume(QUEUE_MESSAGE, true, deliverCallback, consumerTag -> { });

            //read from keyboard the processes to whom to send the message
            Scanner scan= new Scanner(System.in);
            while(true) {
                System.out.println("Dati id-ul celor carora vreti sa le transmiteti mesajul. Pentru broadcast scrieti -1");
                String text = scan.nextLine();
                //create the message
                CustomData data = CustomData.createInstance(id,text, "You have been messaged by " + id);
                //publish the message
                channel.basicPublish("", QUEUE_MESSAGE, null, data.toString().getBytes());
            }

        }
        catch (Exception e){
            System.out.println("Exception encountered");

        }
    }
}
