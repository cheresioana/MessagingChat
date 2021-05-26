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

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://mendpuhm:Ny0KzCPeb7NaQTn3GXsJxp8S8PmSuz7A@hawk.rmq.cloudamqp.com/mendpuhm");
        try {
            int id = Integer.parseInt(argv[0]);
            System.out.println("Started client with id: [ " + id + " ]");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_MESSAGE, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                CustomData data = mapper.readValue(message, CustomData.class);
                if (data.getDestinatar().contains("" + id)){
                    System.out.println(" [x] Received '" + message + "'");
                    data.setDestinatar(data.getDestinatar().replace("" + id, ""));
                    if (!data.getDestinatar().trim().isEmpty()){
                        System.out.println("Inca mai sunt destinatari care nu au primit mesajul: " + data.getDestinatar());
                        channel.basicPublish("", QUEUE_MESSAGE, null, data.toString().getBytes());
                    }
                }
                else{
                    channel.basicPublish("", QUEUE_MESSAGE, null, message.getBytes());
                    if (data.getDestinatar().contains("-1")){
                        System.out.println(" [x] Received '" + message + "'");
                    }
                }

            };
            channel.basicConsume(QUEUE_MESSAGE, true, deliverCallback, consumerTag -> { });

            Scanner scan= new Scanner(System.in);
            while(true) {
                System.out.println("Dati id-ul celor carora vreti sa le transmiteti mesajul. Pentru broadcast scrieti -1");
                String text = scan.nextLine();
                CustomData data = CustomData.createInstance(id,text, "You have been messaged by " + id);
                channel.basicPublish("", QUEUE_MESSAGE, null, data.toString().getBytes());
            }
            /*while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
                String[] split = data.split("\t\t");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime start = LocalDateTime.parse(split[0], formatter);
                LocalDateTime end = LocalDateTime.parse(split[1], formatter);


                channel.basicPublish("", QUEUE_NAME, null, message.toString().getBytes());
                System.out.println(" [x] Sent '" + message + "'");
                Thread.sleep(1000);
            }*/

        }
        catch (Exception e){
            System.out.println("Exception");
        }
    }
}
