//package structure
package com.main.server;

//Import statements
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

//driving class

//The main class for a JavaFX application extends the javafx.application.Application class.
// The start () method is the main entry point for all JavaFX applications.
// A JavaFX application defines the user interface container by means of a stage and a scene.
// The JavaFX Stage class is the top-level JavaFX container.
public class Main extends Application {
    //variable declarations
    Stage window;
    int port;

    @Override
    public void start(Stage primaryStage) {

        //port
        port = Integer.parseInt(super.getParameters().getRaw().get(0));

        //Window
        this.window = primaryStage;
        this.window.setTitle("EURUS SERVER");

        //Grid
        /*
                A JavaFX GridPane is a layout component which lays out its child
                components in a grid. The size of the cells in the grid depends on
                the components displayed in the GridPane, but there are some rules.
                All cells in the same row will have the same height, and all cells in
                the same column will have the same width. Different rows can have different
                heights and different columns can have different widths.
         */
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setGridLinesVisible(true);

        //Scene
        //The JavaFX Scene object is the root of the JavaFX Scene graph.
        // In other words, the JavaFX Scene contains all the visual JavaFX GUI components inside it.
        // A JavaFX Scene is represented by the class javafx.scene.Scene.
        // A Scene object has to be set on a JavaFX Stage to be visible.
        Scene scene = new Scene(grid, 700, 470);
        scene.getStylesheets().add("main.css");

        //for server
        new Server(port, grid);

        //launching window
        this.window.setScene(scene);
        this.window.show();
    }


    //Server class
    public static class Server {

        //Socket
        private Socket socket = null;
        private ServerSocket server = null;

        //Network Communication
        public final ArrayList<HandleClient> CLIENTS = new ArrayList<>();

        /*
        BlockingQueue interface supports flow control (in addition to queue)
        by introducing blocking if either BlockingQueue is full or empty.
        A thread trying to enqueue an element in a full queue is blocked
        until some other thread makes space in the queue, either by dequeuing
        one or more elements or clearing the queue completely. Similarly,
        it blocks a thread trying to delete from an empty queue until some other threads insert an item.
        */
        private final LinkedBlockingQueue<String> messages;

        //Declarations for UI
        // A list view is a scrollable list of items from which you can select desired items.
        // You can create a list view component by instantiating the
        // javafx.scene.control.ListView class.
        // You can create either a vertical or a horizontal ListView.
        private final ListView<String> CLIENTS_LIST_VIEW = new ListView<>();
        private final TextArea CHAT = new TextArea();
        ArrayList<String> clientColors = new ArrayList<>();
        //The java.text.SimpleDateFormat class is used to format and parse a string
        // to date and date to string.

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        //Server constructor
        public Server(int port, GridPane grid) {
            //server has only one message attribute, used to send message to the all clients, see implementation below
            this.messages = new LinkedBlockingQueue<>();

            //Customizing CLIENTS_LIST_VIEW
            customizeClientsListView();
            clientColors.add("#FCBA03");
            clientColors.add("#03FCBA");
            clientColors.add("#BA03FC");
            clientColors.add("#FC03C1");
            clientColors.add("#03FC3D");

            //Thread to accept clients
            Thread acceptClients = new Thread() {

                public void run() {
                    try {
                        server = new ServerSocket(port);
                        CHAT.setEditable(false);
                        CHAT.appendText("Status : Eurus Server has started working.\n");
                        CHAT.appendText("Status : Waiting for a user to join.");
                        GridPane.setConstraints(CHAT, 0, 0);
                        CHAT.getStyleClass().add("server-CHAT");

                        GridPane.setConstraints(CLIENTS_LIST_VIEW, 1, 0);
                        CLIENTS_LIST_VIEW.getStyleClass().add("clients_list_view");

                        grid.getChildren().addAll(CHAT, CLIENTS_LIST_VIEW);


                        while (true) {

                            socket = server.accept();

                            //handle multithreading for clients
                            //Each client is associated with the following client object
                            HandleClient client = new HandleClient(socket.getRemoteSocketAddress().toString(), socket.getPort(), socket);
                            //CLIENTS array list gets the newly added client
                            CLIENTS.add(client);
                            new Thread(client).start();
                        }

                    } catch (Exception e) {
                        System.out.println("Error here : " + e.getMessage());
                    }
                }
            };

            acceptClients.start();
            //Thread to use write method of HandleClient class --> Sends message to client (read by client using in.readUTF()
            Thread writeMessages = new Thread() {
                public void run() {
                    while (true) {
                        try {
                            String message = messages.take();

                            for (HandleClient client : CLIENTS) {
                                client.write(message);
                            }

                        } catch (Exception e) {
                            System.out.println("Error here : " + e.getMessage());
                        }
                    }
                }
            };

            writeMessages.start();


        }

        public void customizeClientsListView() {
            //Every Cell is associated with a single data item (represented by the
            // item property). The Cell is responsible for rendering that item and,
            // where appropriate, for editing the item.


            //setCellFactory : Sets a new cell factory to use in the ListView. This forces all old
            // ListCell's to be thrown away, and new ListCell's created with the
            // new cell factory.


            //Callback : The Callback interface is designed to allow for a common, reusable interface
            // to exist for defining APIs that requires a call back in certain situations.
            //Callback is defined with two generic parameters: the first
            // parameter specifies the type of the object passed in to the
            // call method, with the second parameter specifying the return type of the method.


            CLIENTS_LIST_VIEW.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {

                @Override
                //ListCell : The Cell type used within ListView instances.
                public ListCell<String> call(ListView<String> param) {
                    return new ListCell<String>() {

                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);

                            if (item == null || empty) {
                                setText(null);
                                setStyle("-fx-background-color: white");
                            } else {
                                setText(item);
                                String color = getRandomColor();
                                setStyle("-fx-background-color: " + color);
                            }
                        }

                    };
                }
            });
        }

        public String getRandomColor() {
            Random rand = new Random();
            return clientColors.get(rand.nextInt(clientColors.size()));
        }




        //Class to handle client is below

        //There are two ways to create a thread:
        //
        //    By extending Thread class
        //    By implementing Runnable interface.

        private class HandleClient implements Runnable {

            private DataInputStream in = null;
            private DataOutputStream out = null;
            public String address;
            public int port;
            public Socket socket;
            private String username;

            public HandleClient(String address, int port, Socket socket) {

                this.address = address;
                this.port = port;
                this.socket = socket;

                try {
                    this.in = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));
                    this.out = new DataOutputStream(socket.getOutputStream());
                } catch (Exception e) {
                    System.out.println("Error here " + e.getMessage());
                }
            }

            @Override
            public void run() {
                try {

                    //get username from client
                    String username = null;



                    while (username == null) {
                        username = in.readUTF();
                        setUsername(username);
                        addClientToUI(username);
                    }

                if (this.username.isBlank()) {
                        out.writeUTF("Eura : Hi! I'm Eura, your personal assistant in Eurus. Your connection has been accepted." + "\nEura : Welcome to Eurus. Good to see you " + username + "." +
                                "\nNOTE: Your are in read mode only as you haven't provided a username." + "\nEura : Enter \"bye\" [Case Ignored] to exit and " +
                                "\"All users\" to get all the users currently   \nconnected to Eurus.");
                    } else {
                        out.writeUTF(
                                "Eura : Hi! I'm Eura, your personal assistant in Eurus." + " Your connection has been accepted." + "\nEura : Welcome to Eurus. Good to see you " + username + "." + "\nEura : Enter \"bye\" [Case Ignored] to exit and " +
                                "\"All users\" to get all the users currently   \nconnected to Eurus.");

                        //Joining message to server
                        String message = "User Joined : " + this.username;
                    try {
                        messages.put(message);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    addMessageToChat(message);
                    }

                    String line = "";
                    while (!line.equals("bye")) {

                        try {
                            line = this.in.readUTF();

                            String line_formatted = this.username + ": " + line;

                            if (line.equals("All users")) {
                                write(CLIENTS.toString());
                                addMessageToChat(line_formatted);

                            } else if (!this.username.isBlank()) {
                                messages.put(line_formatted);
                                //print to server
                                addMessageToChat(line_formatted);
                            }

                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            System.out.println(this.username);
                        }
                    }

                    try {
                        String PartingMessage = "Eura : User " + this.username + " has left the chat.";
                        messages.put(PartingMessage);
                        //String goodbyeMessage = "Eura : Thank you " + this.username + " for using Eurus. Goodbye!";
                        //write(goodbyeMessage);


                        addMessageToChat(PartingMessage);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    removeClientFromUI(this.getUsername());
                    CLIENTS.remove(this);
                    this.in.close();
                    this.out.close();
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error :" + e.getMessage());
                }
            }

            /*
                public static void runLater(Runnable runnable)
                Description :
                Run the specified Runnable on the JavaFX Application Thread at some
                unspecified time in the future. This method, which may be called from any thread,
                will post the Runnable to an event queue and then return immediately to the caller.
                The Runnables are executed in the order they are posted.
                Reason for using it :
                Sometimes you absolutely need to perform some long-running task in a JavaFX application.
                You don't want to leave the GUI unresponsive while the task is running, so you want to run the
                ask in its own thread. However, you would like the running task to update the GUI -
                either along the way, or when the task is completed. The task thread cannot update the GUI
                (the scene graph) directly - but JavaFX has a solution for this problem.

                JavaFX contains the Platform class which has a runLater() method.
                The runLater() method takes a Runnable which is executed by the JavaFX application thread
                when it has time. From inside this Runnable you can modify the JavaFX scene graph.
            */

            //Adds text to the TextArea of Server side

            //addMessageToChat is for putting message on the server side with time

            public void addMessageToChat(String message) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        Date date = new Date(System.currentTimeMillis());
                        String dateFormatted = formatter.format(date);
                        CHAT.appendText("\n" + dateFormatted + " " + message);
                    }
                });
            }
            //Adds new client to the UI (server side)
            public void addClientToUI(String username) {
                Platform.runLater(new Runnable() {
                    public void run() {
                        CLIENTS_LIST_VIEW.getItems().add(username);
                    }
                });
            }
            //Removes exited client from UI (ListView)
            public void removeClientFromUI(String username) {
                String message = "User Left : " + this.username;
                addMessageToChat(message);
                Platform.runLater(new Runnable() {
                    public void run() {
                        CLIENTS_LIST_VIEW.getItems().remove(username);
                    }
                });
            }
            //Sends message to client (read by client using in.readUTF())
            //The message sent using write() is specific to the client and not sent to everyone.
            public void write(String message) {
                try {
                    this.out.writeUTF(message);
                } catch (IOException e) {
                    System.out.println("Error here " + e.getMessage());
                }
            }



            @Override
            public String toString() {

                return getUsername();
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getUsername() {
                return this.username;
            }
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}