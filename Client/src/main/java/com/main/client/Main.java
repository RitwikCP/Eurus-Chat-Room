//package structure
package com.main.client;

//import statements
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;

//driving class

//The main class for a JavaFX application extends the javafx.application.Application class.
// The start () method is the main entry point for all JavaFX applications.
// A JavaFX application defines the user interface container by means of a stage and a scene.
// The JavaFX Stage class is the top-level JavaFX container.
public class Main extends Application {

    //variable declarations
    Stage window;

    String address;
    int port;
    public static String name = null;







    @Override
    public void start(Stage primaryStage){
        //Create Stage
        Stage newWindow = new Stage();
        newWindow.setTitle("EURUS");

        //Create view in Java
        Label title = new Label("Enter your username for the current session: ");
        TextArea textArea = new TextArea();

        //button
        final Button button = new Button("DONE");
        button.setStyle("-fx-background-color: yellow; ");

        //Style container
        //Instead of arranging the nodes in horizontal row, Vbox Layout Pane arranges the nodes in a single vertical column.
        // It is represented by javafx.scene.layout.VBox class which provides all the methods to deal with the styling and the distance among the nodes.
        VBox container = new VBox(title, textArea, button);
        container.setSpacing(15);
        container.setPadding(new Insets(25));
        container.setAlignment(Pos.CENTER);

        //Set view in window
        //The JavaFX Scene object is the root of the JavaFX Scene graph.
        // In other words, the JavaFX Scene contains all the visual JavaFX GUI components inside it.
        // A JavaFX Scene is represented by the class javafx.scene.Scene.
        // A Scene object has to be set on a JavaFX Stage to be visible.
        Scene IntroScene = new Scene(container, 500, 130);
        newWindow.setScene(IntroScene);
        IntroScene.getStylesheets().add("IntroScene.css");

        //Launch
        newWindow.show();

        //Handling button press
        //JavaFX’s user interfaces are fuelled by events.
        // In fact, almost every action a user takes in a user interface will trigger an Event,
        // which in turn will fire some executable code.
        // To make a button do something in JavaFX you need to define the executable code usually by using a convenience method like setOnAction().
        //Then, when the button is clicked, JavaFX does the heavy lifting of fetching
        // that pre-defined code, and executing it on the JavaFX Application thread.
        button.setOnAction(event -> {

            name = textArea.getText();

            newWindow.close();

            address = super.getParameters().getRaw().get(0);
            port = Integer.parseInt(super.getParameters().getRaw().get(1));

            //Window
            this.window = primaryStage;
            this.window.setTitle("CLIENT EURUS");

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
            grid.setVgap(10);
            grid.setHgap(10);
            // Insets class stores the inside offsets for the four sides of the rectangular area.
            //To make the object nodes of JavaFX GridPane look smart and organized.
            grid.setPadding(new Insets(10, 10, 10, 10));

            //set up columns
            ColumnConstraints column0 = new ColumnConstraints();
            column0.setPercentWidth(90);
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(20);


            //set up rows
            RowConstraints row0 = new RowConstraints();
            row0.setPercentHeight(75);
            RowConstraints row1 = new RowConstraints();
            row1.setPercentHeight(20);
            RowConstraints row2 = new RowConstraints();
            row2.setPercentHeight(5);


            //add columns and rows to the grid
            grid.getRowConstraints().addAll(row0, row1, row2);
            grid.getColumnConstraints().addAll(column0, column1);

            //Scene
            Scene scene = new Scene(grid, 800, 600);
            scene.getStylesheets().add("main.css");

            //for client
            new Client(address, port, grid);

            //launching window
            this.window.setScene(scene);
            this.window.show();

        });
    }

    //Client class
    public static class Client {

        //Variable declarations for Network Communication
        private Socket socket = null;
        private DataInputStream in = null;
        private DataOutputStream out = null;

        //Blocking Queue - A Queue that additionally supports operations that wait for the queue to
        // become non-empty when retrieving an element, and wait for space to become available in the queue when storing an element.

        //An optionally-bounded blocking queue based on linked nodes.
        // This queue orders elements FIFO (first-in-first-out).
        // The head of the queue is that element that has been on the queue the longest time.
        // The tail of the queue is that element that has been on the queue the shortest time.
        // New elements are inserted at the tail of the queue,
        // and the queue retrieval operations obtain elements at the head of the queue.
        // Linked queues typically have higher throughput than array-based queues but less predictable performance in most concurrent applications.


        /*
        BlockingQueue interface supports flow control (in addition to queue)
        by introducing blocking if either BlockingQueue is full or empty.
        A thread trying to enqueue an element in a full queue is blocked
        until some other thread makes space in the queue, either by dequeuing
        one or more elements or clearing the queue completely. Similarly,
        it blocks a thread trying to delete from an empty queue until some other threads insert an item.
        */
        private final LinkedBlockingQueue<String> MESSAGES;
        private final LinkedBlockingQueue<String> MESSAGES_BY_CLIENT;


        //Flowchart for messages:
        //Message in TextArea --On pressing Send button--> Inserted in MESSSAGES Queue --> Sent to server
        //and displayed there --> Read from server and stored in MESSAGES_BY_CLIENT Queue --> Read from this
        //Queue and displayed on Client's side

        //Variables for user interface
        //TextFlow class is a part of JavaFX. TextFlow class is designed to lay out rich text.
        // It can be used to layout several Text nodes in a single text flow. TextFlow class extends Pane class.
        private final TextFlow CHAT = new TextFlow();

        // A JavaFX TextArea control enables users of a JavaFX application
        // to enter text spanning multiple lines, which can then be read by the application.
        // The JavaFX TextArea control is represented by the class javafx.scene.control.TextArea .
        private final TextArea CHAT_INPUT = new TextArea();
        //The byte is one of the primitive data types in Java .
        // This means that the Java byte is the same size as a byte in computer memory:
        // it's 8 bits, and can hold values ranging from -128 to 127
        byte[] emojiByteCode = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81};
        String emoji = new String(emojiByteCode, StandardCharsets.UTF_8);
        private final Button BUTTON_SEND = new Button("SEND");
        private final Button BUTTON_EMOJI= new Button("EMOJIS " + emoji);
        private String username = "";

        //Client constructor
        public Client(String address, int port, GridPane grid) {

            this.MESSAGES_BY_CLIENT = new LinkedBlockingQueue<>();
            this.MESSAGES = new LinkedBlockingQueue<>();

            try {

                socket = new Socket(address, port);

                in = new DataInputStream(
                        new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(socket.getOutputStream());


                username = Main.name;

                setUsername(username);
                out.writeUTF(username);

                //connection accepted
                String serverMessage = in.readUTF();
                Text text = new Text(serverMessage);
                CHAT.getChildren().add(text);

                //setting up chat with UI
                CHAT.getStyleClass().add("server-chat");
                CHAT.setPadding(new Insets(5, 5, 5, 5));
                GridPane.setHgrow(CHAT, Priority.ALWAYS);
                GridPane.setVgrow(CHAT, Priority.ALWAYS);
                ScrollPane sp = new ScrollPane();
                sp.setContent(CHAT);
                GridPane.setConstraints(sp, 0, 0);

                //Client Status (Circle - Online)
                Circle statusCircle = new Circle(0, 0, 10);
                statusCircle.getStyleClass().add("status-circle");
                GridPane.setValignment(statusCircle, VPos.TOP);
                GridPane.setHgrow(statusCircle, Priority.ALWAYS);
                GridPane.setVgrow(statusCircle, Priority.ALWAYS);
                GridPane.setConstraints(statusCircle, 1, 0);

                //Client Status (Label)
                Label statusLabel = new Label("Online");
                statusLabel.getStyleClass().add("status-label");
                GridPane.setValignment(statusLabel, VPos.TOP);
                GridPane.setHalignment(statusLabel, HPos.CENTER);
                GridPane.setHgrow(statusLabel, Priority.ALWAYS);
                GridPane.setVgrow(statusLabel, Priority.ALWAYS);
                GridPane.setConstraints(statusLabel, 1, 0);

                //UserName
                Label usernameLabel = new Label("User: " + username);
                usernameLabel.getStyleClass().add("status-username");
                GridPane.setValignment(usernameLabel, VPos.TOP);
                GridPane.setHalignment(usernameLabel, HPos.LEFT);
                GridPane.setHgrow(usernameLabel, Priority.ALWAYS);
                GridPane.setVgrow(usernameLabel, Priority.ALWAYS);
                usernameLabel.setPadding(new Insets(30, 0, 0, 0));
                GridPane.setConstraints(usernameLabel, 1, 0);

                //set up user input area to UI
                CHAT_INPUT.getStyleClass().add("chat-input");
                CHAT_INPUT.setWrapText(true);
                CHAT_INPUT.setPromptText("Enter Message : ");
                GridPane.setHgrow(CHAT_INPUT, Priority.ALWAYS);
                GridPane.setVgrow(CHAT_INPUT, Priority.ALWAYS);
                GridPane.setConstraints(CHAT_INPUT, 0, 1);

                //set button to send message to UI
                BUTTON_SEND.getStyleClass().add("button-send");
                GridPane.setHgrow(BUTTON_SEND, Priority.ALWAYS);
                GridPane.setVgrow(BUTTON_SEND, Priority.ALWAYS);
                GridPane.setHalignment(BUTTON_SEND, HPos.RIGHT);
                GridPane.setValignment(BUTTON_SEND, VPos.TOP);
                GridPane.setConstraints(BUTTON_SEND, 0, 2);

                //Emojis
                BUTTON_EMOJI.getStyleClass().add("button-emojis");
                GridPane.setHgrow(BUTTON_EMOJI ,Priority.ALWAYS);
                GridPane.setVgrow(BUTTON_EMOJI , Priority.ALWAYS);
                GridPane.setHalignment(BUTTON_EMOJI , HPos.LEFT);
                GridPane.setValignment(BUTTON_EMOJI , VPos.TOP);
                GridPane.setConstraints(BUTTON_EMOJI , 0, 2);
                grid.getChildren().addAll(sp, CHAT_INPUT, BUTTON_SEND, BUTTON_EMOJI, statusCircle, statusLabel, usernameLabel);

                //set up BUTTON to send message to UI
                setUpButtonSend();
                setUpButtonEmojis();

            } catch (Exception e) {
                System.out.println("Error : " + e.getMessage());
            }

            //First Thread is defined below (note that when a Java program starts up,
            // one thread begins running immediately. This is usually
            // called the main thread of our program because it is
            // the one that is executed when our program begins. )

            //There are two ways to create a thread:
            //
            //    By extending Thread class
            //    By implementing Runnable interface.
            Thread userInput = new Thread() {
                //Checks if user wants to exit Eurus
                public void run() {

                    try {
                        String message = "";
                        while (!message.equals("bye")) {
                            try {
                                //MESSAGES_BY_CLIENT is for displaying the message by client on server side
                                message = MESSAGES_BY_CLIENT.take();

                                out.writeUTF(message);
                            } catch (IOException | InterruptedException e) {

                                e.printStackTrace();


                            }
                        }

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        in.close();
                        out.close();
                        socket.close();
                        System.exit(0);



                    } catch (IOException e) {
                        System.out.println("Error : " + e.getMessage());
                    }
                }
            };
            /*
                The start() method of thread class is used to begin the execution of thread.
                The result of this method is two threads that are running concurrently:
                the current thread (which returns from the call to the start method) and
                the other thread (which executes its run method).
                The start() method internally calls the run() method of Runnable interface to execute the code
                specified in the run() method in a separate thread.

                The start thread performs the following tasks:

                   --> It stats a new thread
                   --> The thread moves from New State to Runnable state.
                   --> When the thread gets a chance to execute, its target run() method will run.

             */
            userInput.start();
            //Second thread
            Thread readMessagesToClient = new Thread() {

                public void run() {
                    String message = "";
                    while (true) {
                        try {   //MESSAGES is for displaying messages in Client side (Reads messages from server side)

                            message = MESSAGES.take();

                            //Puts message in the client side after reading from server(see below
                            //for that implementation)
                            addMessageToChat(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            readMessagesToClient.start();
            //To read messages from Server
            //Class defined below
            ReadMessagesFromServer server = new ReadMessagesFromServer(socket);
            new Thread(server).start();
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void addMessageToChat(String message) {

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
            Platform.runLater(new Runnable() {
                public void run() {
                    Text text = new Text("\n" + message);

                    if (message.contains(username + ": ") && (!username.equals(""))) {
                        text.setStyle("-fx-fill:#004AFF;-fx-font-weight:bold");
                    }
                    CHAT.getChildren().add(text);
                }
            });
        }

        private class ReadMessagesFromServer implements Runnable {


            Socket socket;

            ReadMessagesFromServer(Socket socket) {
                this.socket = socket;
            }

            public void run() {
                try {
                    DataInputStream inp = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));


                    String line = "";
                    while (true) {

                        try {
                            if(line.equals("Eura : User " + username + " has left the chat.")){
                                break;
                            }


                            line = inp.readUTF(); //Reads from server side

                            //This put method puts the line string at the end of the queue
                            MESSAGES.put(line);




                        } catch (IOException  | InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    inp.close();
                    socket.close();
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }



            }
        }

        //Setting up Send button
        public void setUpButtonSend() {
            BUTTON_SEND.setOnAction(e -> {
                String message = CHAT_INPUT.getText();
                if(message.equalsIgnoreCase("bye")){
                    message = "bye";
                }

                //Puts the entered text in the MESSAGES_BY_CLIENT queue
                MESSAGES_BY_CLIENT.add(message);
                CHAT_INPUT.setText("");
            });
        }

        //Setting up Emoji button
        public void setUpButtonEmojis() {

            BUTTON_EMOJI.setOnAction(e -> {
                Stage EWindow = new Stage();
                EWindow.setTitle("Choose your emoji :)");


                //Grid for Emoji
                GridPane EmojiGrid = new GridPane();

                EmojiGrid.setVgap(2);
                EmojiGrid.setHgap(2);
                EmojiGrid.setPadding(new Insets(10, 10, 10, 10));

                //set up columns
                ColumnConstraints column0 = new ColumnConstraints();
                column0.setPercentWidth(20);
                ColumnConstraints column1 = new ColumnConstraints();
                column1.setPercentWidth(20);
                ColumnConstraints column2 = new ColumnConstraints();
                column2.setPercentWidth(20);
                ColumnConstraints column3 = new ColumnConstraints();
                column3.setPercentWidth(20);
                ColumnConstraints column4 = new ColumnConstraints();
                column4.setPercentWidth(20);




                //set up rows
                RowConstraints row0 = new RowConstraints();
                row0.setPercentHeight(20);
                RowConstraints row1 = new RowConstraints();
                row1.setPercentHeight(20);
                RowConstraints row2 = new RowConstraints();
                row2.setPercentHeight(20);
                RowConstraints row3 = new RowConstraints();
                row3.setPercentHeight(20);
                RowConstraints row4 = new RowConstraints();
                row4.setPercentHeight(20);



                //add columns and rows to the grid
                EmojiGrid.getRowConstraints().addAll(row0, row1, row2, row3, row4);
                EmojiGrid.getColumnConstraints().addAll(column0, column1, column2, column3, column4);

                //Scene
                Scene EmoScene = new Scene(EmojiGrid, 400, 250);

                //Setting up emojis
                byte[] emojiByteCode1 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x80};
                String emoji1 = new String(emojiByteCode1, StandardCharsets.UTF_8);
                byte[] emojiByteCode2 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81};
                String emoji2 = new String(emojiByteCode2, StandardCharsets.UTF_8);
                byte[] emojiByteCode3 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x82};
                String emoji3 = new String(emojiByteCode3, StandardCharsets.UTF_8);
                byte[] emojiByteCode4 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x83};
                String emoji4 = new String(emojiByteCode4, StandardCharsets.UTF_8);
                byte[] emojiByteCode5 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x84};
                String emoji5 = new String(emojiByteCode5, StandardCharsets.UTF_8);
                byte[] emojiByteCode6 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x85};
                String emoji6 = new String(emojiByteCode6, StandardCharsets.UTF_8);
                byte[] emojiByteCode7 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x86};
                String emoji7 = new String(emojiByteCode7, StandardCharsets.UTF_8);
                byte[] emojiByteCode8 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x87};
                String emoji8 = new String(emojiByteCode8, StandardCharsets.UTF_8);
                byte[] emojiByteCode9 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0xa1};
                String emoji9 = new String(emojiByteCode9, StandardCharsets.UTF_8);
                byte[] emojiByteCode10 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x89};
                String emoji10 = new String(emojiByteCode10, StandardCharsets.UTF_8);
                byte[] emojiByteCode11 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8a};
                String emoji11 = new String(emojiByteCode11, StandardCharsets.UTF_8);
                byte[] emojiByteCode12 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8b};
                String emoji12 = new String(emojiByteCode12, StandardCharsets.UTF_8);
                byte[] emojiByteCode13 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8c};
                String emoji13 = new String(emojiByteCode13, StandardCharsets.UTF_8);
                byte[] emojiByteCode14 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8d};
                String emoji14 = new String(emojiByteCode14, StandardCharsets.UTF_8);
                byte[] emojiByteCode15 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8e};
                String emoji15 = new String(emojiByteCode15, StandardCharsets.UTF_8);
                byte[] emojiByteCode16 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x8f};
                String emoji16 = new String(emojiByteCode16, StandardCharsets.UTF_8);
                byte[] emojiByteCode17 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x90};
                String emoji17 = new String(emojiByteCode17, StandardCharsets.UTF_8);
                byte[] emojiByteCode18 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x91};
                String emoji18 = new String(emojiByteCode18, StandardCharsets.UTF_8);
                byte[] emojiByteCode19 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x92};
                String emoji19 = new String(emojiByteCode19, StandardCharsets.UTF_8);
                byte[] emojiByteCode20 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x93};
                String emoji20 = new String(emojiByteCode20, StandardCharsets.UTF_8);
                byte[] emojiByteCode21 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x94};
                String emoji21 = new String(emojiByteCode21, StandardCharsets.UTF_8);
                byte[] emojiByteCode22 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x95};
                String emoji22 = new String(emojiByteCode22, StandardCharsets.UTF_8);
                byte[] emojiByteCode23 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x96};
                String emoji23 = new String(emojiByteCode23, StandardCharsets.UTF_8);
                byte[] emojiByteCode24 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x97};
                String emoji24 = new String(emojiByteCode24, StandardCharsets.UTF_8);
                byte[] emojiByteCode25 = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x98};
                String emoji25 = new String(emojiByteCode25, StandardCharsets.UTF_8);



                //Button Handling
                Button E1 = new Button(emoji1);
                E1.getStyleClass().add("my-special-button");

                Button E2 = new Button(emoji2);
                E2.getStyleClass().add("my-special-button");

                Button E3 = new Button(emoji3);
                E3.getStyleClass().add("my-special-button");

                Button E4 = new Button(emoji4);
                E4.getStyleClass().add("my-special-button");

                Button E5 = new Button(emoji5);
                E5.getStyleClass().add("my-special-button");

                Button E6 = new Button(emoji6);
                E6.getStyleClass().add("my-special-button");

                Button E7 = new Button(emoji7);
                E7.getStyleClass().add("my-special-button");

                Button E8 = new Button(emoji8);
                E8.getStyleClass().add("my-special-button");

                Button E9 = new Button(emoji9);
                E9.getStyleClass().add("my-special-button");

                Button E10 = new Button(emoji10);
                E10.getStyleClass().add("my-special-button");

                Button E11 = new Button(emoji11);
                E11.getStyleClass().add("my-special-button");

                Button E12 = new Button(emoji12);
                E12.getStyleClass().add("my-special-button");

                Button E13 = new Button(emoji13);
                E13.getStyleClass().add("my-special-button");

                Button E14 = new Button(emoji14);
                E14.getStyleClass().add("my-special-button");

                Button E15 = new Button(emoji15);
                E15.getStyleClass().add("my-special-button");

                Button E16 = new Button(emoji16);
                E16.getStyleClass().add("my-special-button");

                Button E17 = new Button(emoji17);
                E17.getStyleClass().add("my-special-button");

                Button E18 = new Button(emoji18);
                E18.getStyleClass().add("my-special-button");

                Button E19 = new Button(emoji19);
                E19.getStyleClass().add("my-special-button");

                Button E20 = new Button(emoji20);
                E20.getStyleClass().add("my-special-button");

                Button E21 = new Button(emoji21);
                E21.getStyleClass().add("my-special-button");

                Button E22 = new Button(emoji22);
                E22.getStyleClass().add("my-special-button");

                Button E23 = new Button(emoji23);
                E23.getStyleClass().add("my-special-button");

                Button E24 = new Button(emoji24);
                E24.getStyleClass().add("my-special-button");

                Button E25 = new Button(emoji25);
                E25.getStyleClass().add("my-special-button");

                E1.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E1, VPos.CENTER);
                GridPane.setHalignment(E1, HPos.CENTER);
                GridPane.setHgrow(E1, Priority.ALWAYS);
                GridPane.setVgrow(E1, Priority.ALWAYS);
                GridPane.setConstraints(E1, 0, 0);

                E2.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E2, VPos.CENTER);
                GridPane.setHalignment(E2, HPos.CENTER);
                GridPane.setHgrow(E2, Priority.ALWAYS);
                GridPane.setVgrow(E2, Priority.ALWAYS);
                GridPane.setConstraints(E2, 0, 1);

                E3.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E3, VPos.CENTER);
                GridPane.setHalignment(E3, HPos.CENTER);
                GridPane.setHgrow(E3, Priority.ALWAYS);
                GridPane.setVgrow(E3, Priority.ALWAYS);
                GridPane.setConstraints(E3, 0, 2);

                E4.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E4, VPos.CENTER);
                GridPane.setHalignment(E4, HPos.CENTER);
                GridPane.setHgrow(E4, Priority.ALWAYS);
                GridPane.setVgrow(E4, Priority.ALWAYS);
                GridPane.setConstraints(E4, 0, 3);

                E5.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E5, VPos.CENTER);
                GridPane.setHalignment(E5, HPos.CENTER);
                GridPane.setHgrow(E5, Priority.ALWAYS);
                GridPane.setVgrow(E5, Priority.ALWAYS);
                GridPane.setConstraints(E5, 0, 4);

                E6.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E6, VPos.CENTER);
                GridPane.setHalignment(E6, HPos.CENTER);
                GridPane.setHgrow(E6, Priority.ALWAYS);
                GridPane.setVgrow(E6, Priority.ALWAYS);
                GridPane.setConstraints(E6, 1, 0);

                E7.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E7, VPos.CENTER);
                GridPane.setHalignment(E7, HPos.CENTER);
                GridPane.setHgrow(E7, Priority.ALWAYS);
                GridPane.setVgrow(E7, Priority.ALWAYS);
                GridPane.setConstraints(E7, 1,1 );

                E8.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E8, VPos.CENTER);
                GridPane.setHalignment(E8, HPos.CENTER);
                GridPane.setHgrow(E8, Priority.ALWAYS);
                GridPane.setVgrow(E8, Priority.ALWAYS);
                GridPane.setConstraints(E8, 1, 2);

                E9.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E9, VPos.CENTER);
                GridPane.setHalignment(E9, HPos.CENTER);
                GridPane.setHgrow(E9, Priority.ALWAYS);
                GridPane.setVgrow(E9, Priority.ALWAYS);
                GridPane.setConstraints(E9, 1, 4);

                E10.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E10, VPos.CENTER);
                GridPane.setHalignment(E10, HPos.CENTER);
                GridPane.setHgrow(E10, Priority.ALWAYS);
                GridPane.setVgrow(E10, Priority.ALWAYS);
                GridPane.setConstraints(E10, 2, 0);

                E11.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E11, VPos.CENTER);
                GridPane.setHalignment(E11, HPos.CENTER);
                GridPane.setHgrow(E11, Priority.ALWAYS);
                GridPane.setVgrow(E11, Priority.ALWAYS);
                GridPane.setConstraints(E11, 2, 1);

                E12.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E12, VPos.CENTER);
                GridPane.setHalignment(E12, HPos.CENTER);
                GridPane.setHgrow(E12, Priority.ALWAYS);
                GridPane.setVgrow(E12, Priority.ALWAYS);
                GridPane.setConstraints(E12, 2, 2);

                E13.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E13, VPos.CENTER);
                GridPane.setHalignment(E13, HPos.CENTER);
                GridPane.setHgrow(E13, Priority.ALWAYS);
                GridPane.setVgrow(E13, Priority.ALWAYS);
                GridPane.setConstraints(E13, 2, 3);

                E14.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E14, VPos.CENTER);
                GridPane.setHalignment(E14, HPos.CENTER);
                GridPane.setHgrow(E14, Priority.ALWAYS);
                GridPane.setVgrow(E14, Priority.ALWAYS);
                GridPane.setConstraints(E14, 2, 4);

                E15.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E15, VPos.CENTER);
                GridPane.setHalignment(E15, HPos.CENTER);
                GridPane.setHgrow(E15, Priority.ALWAYS);
                GridPane.setVgrow(E15, Priority.ALWAYS);
                GridPane.setConstraints(E15, 3, 0);

                E16.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E16, VPos.CENTER);
                GridPane.setHalignment(E16, HPos.CENTER);
                GridPane.setHgrow(E16, Priority.ALWAYS);
                GridPane.setVgrow(E16, Priority.ALWAYS);
                GridPane.setConstraints(E16, 3, 1);

                E17.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E17, VPos.CENTER);
                GridPane.setHalignment(E17, HPos.CENTER);
                GridPane.setHgrow(E17, Priority.ALWAYS);
                GridPane.setVgrow(E17, Priority.ALWAYS);
                GridPane.setConstraints(E17, 3, 2);

                E18.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E18, VPos.CENTER);
                GridPane.setHalignment(E18, HPos.CENTER);
                GridPane.setHgrow(E18, Priority.ALWAYS);
                GridPane.setVgrow(E18, Priority.ALWAYS);
                GridPane.setConstraints(E18, 3, 3);

                E19.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E19, VPos.CENTER);
                GridPane.setHalignment(E19, HPos.CENTER);
                GridPane.setHgrow(E19, Priority.ALWAYS);
                GridPane.setVgrow(E19, Priority.ALWAYS);
                GridPane.setConstraints(E19, 3, 4);

                E20.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E20, VPos.CENTER);
                GridPane.setHalignment(E20, HPos.CENTER);
                GridPane.setHgrow(E20, Priority.ALWAYS);
                GridPane.setVgrow(E20, Priority.ALWAYS);
                GridPane.setConstraints(E20, 4, 0);

                E21.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E21, VPos.CENTER);
                GridPane.setHalignment(E21, HPos.CENTER);
                GridPane.setHgrow(E21, Priority.ALWAYS);
                GridPane.setVgrow(E21, Priority.ALWAYS);
                GridPane.setConstraints(E21, 4, 1);

                E22.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E22, VPos.CENTER);
                GridPane.setHalignment(E22, HPos.CENTER);
                GridPane.setHgrow(E22, Priority.ALWAYS);
                GridPane.setVgrow(E22, Priority.ALWAYS);
                GridPane.setConstraints(E22, 4, 2);


                E23.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E23, VPos.CENTER);
                GridPane.setHalignment(E23, HPos.CENTER);
                GridPane.setHgrow(E23, Priority.ALWAYS);
                GridPane.setVgrow(E23, Priority.ALWAYS);
                GridPane.setConstraints(E23, 4, 3);


                E24.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E24, VPos.CENTER);
                GridPane.setHalignment(E24, HPos.CENTER);
                GridPane.setHgrow(E24, Priority.ALWAYS);
                GridPane.setVgrow(E24, Priority.ALWAYS);
                GridPane.setConstraints(E24, 4, 4);

                E25.setStyle("-fx-background-color: Yellow;");
                GridPane.setValignment(E25, VPos.CENTER);
                GridPane.setHalignment(E25, HPos.CENTER);
                GridPane.setHgrow(E25, Priority.ALWAYS);
                GridPane.setVgrow(E25, Priority.ALWAYS);
                GridPane.setConstraints(E25, 1, 3);


                E1.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji1 + " " );
                    EWindow.close();

                });
                E2.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji2 + " " );
                    EWindow.close();

                });
                E3.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji3 + " " );
                    EWindow.close();

                });
                E4.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji4 + " " );
                    EWindow.close();

                });
                E5.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji5 + " " );
                    EWindow.close();

                });
                E6.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji6 + " " );
                    EWindow.close();

                });
                E7.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji7 + " " );
                    EWindow.close();

                });
                E8.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji8 + " " );
                    EWindow.close();

                });
                E9.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji9 + " " );
                    EWindow.close();

                });
                E10.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji10 + " " );
                    EWindow.close();

                });
                E11.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji11 + " " );
                    EWindow.close();

                });
                E12.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji12 + " " );
                    EWindow.close();

                });
                E13.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji13 + " " );
                    EWindow.close();

                });
                E14.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji14 + " " );
                    EWindow.close();

                });
                E15.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji15 + " " );
                    EWindow.close();

                });
                E16.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji16 + " " );
                    EWindow.close();

                });
                E17.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji17 + " " );
                    EWindow.close();

                });
                E18.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji18 + " " );
                    EWindow.close();

                });
                E19.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji19 + " " );
                    EWindow.close();

                });
                E20.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji21 + " " );
                    EWindow.close();

                });
                E21.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji21 + " " );
                    EWindow.close();

                });
                E22.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji22 + " " );
                    EWindow.close();

                });
                E23.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji23 + " " );
                    EWindow.close();

                });
                E24.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji24 + " " );
                    EWindow.close();

                });
                E25.setOnAction(event -> {
                    CHAT_INPUT.setText( CHAT_INPUT.getText() + " " + emoji25 + " " );
                    EWindow.close();

                });



                EmojiGrid.getChildren().addAll(E1, E2, E3, E4, E5,E6, E7, E8, E9, E10,E11, E12, E13, E14, E15,E16, E17, E18, E19, E20,E21, E22, E23, E24, E25);

                EmoScene.getStylesheets().add("main.css");
                EWindow.setScene(EmoScene);

                EWindow.show();
            });
        }

    }

    //main method
    public static void main(String[] args) {
        //It will just pass the command line arguments (args) to the javafx.application.Application.launch
        // which will open the JavaFX as expected.
        launch(args);
    }

}