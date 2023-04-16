// echo server
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Server_X_Client_test {

    class SharedObject {

        public static List<ServerThread> players = Collections.synchronizedList(new ArrayList<ServerThread>());
        //   public static List<Integer> playerCards = Collections.synchronizedList(new ArrayList<Integer>());

        //   public static List<Integer> responses = Collections.synchronizedList(new ArrayList<Integer>());

        public static int playerOnePoints = 0;
        public static int playerTwoPoints = 0;
        public static int playerThreePoints = 0;
        public static int currentRound = 1;

        //     public static boolean isGameOver = false;
    }

    public static void main(String args[]) {


        ServerSocket ss2 = null;
        System.out.println("Server Listening......");
        int counter = 0;


        List<Integer> deck = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            deck.add(i);
        }
        Collections.shuffle(deck);

        try {
            ss2 = new ServerSocket(4445); // can also use static final PORT_NUM , when defined

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server error");

        }


        Server_X_Client_test server = new Server_X_Client_test();
        while (counter < 3) {


            try {

                ServerThread st = server.new ServerThread(ss2.accept(), counter + 1);
                st.start();
                System.out.println("connection Established");
                synchronized (SharedObject.players) {
                    SharedObject.players.add(st);
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Connection Error");

            }

        }
        synchronized (SharedObject.players) {

            System.out.println("round 1");
            int dealerCard = deck.remove(0);
            System.out.println("Dealer shows card " + dealerCard);


            SharedObject.players.get(0).message = "You are Player 1 and the Dealers card is " + dealerCard;
            SharedObject.players.get(1).message = "You are Player 2 and the Dealers card is " + dealerCard;
            SharedObject.players.get(2).message = "You are Player 3 and the Dealers card is " + dealerCard;

            SharedObject.currentRound++;


            while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                    SharedObject.players.get(0).message = "Waiting on other Players";
                }
                if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                    SharedObject.players.get(1).message = "Waiting on other Players";
                }
                if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                    SharedObject.players.get(2).message = "Waiting on other Players";
                }
            }

            SharedObject.currentRound++;


            while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                String winner= "Everyone Lost";
                int res =Results(SharedObject.players.get(0).playerCard, SharedObject.players.get(1).playerCard, SharedObject.players.get(2).playerCard, dealerCard);
                if( res!=0){
                    winner= "The Winning Number is "+res;
                }
                SharedObject.players.get(0).message = winner;
                SharedObject.players.get(1).message = winner;
                SharedObject.players.get(2).message = winner;

                if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                    SharedObject.players.get(0).message = "Waiting on other Players";
                }
                if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                    SharedObject.players.get(1).message = "Waiting on other Players";
                }
                if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                    SharedObject.players.get(2).message = "Waiting on other Players";
                }

            }

        }
        System.out.println("Players Here");


        while (SharedObject.currentRound < 14) {
            synchronized (SharedObject.players) {
                String winner;
                int res=0;

                System.out.println("round " + SharedObject.currentRound);
                int dealerCard = deck.remove(0);
                System.out.println("Dealer shows card " + dealerCard);

                SharedObject.currentRound++;
                SharedObject.players.get(0).message = "The Dealers card is " + dealerCard;
                SharedObject.players.get(1).message = "The Dealers card is " + dealerCard;
                SharedObject.players.get(2).message = "The Dealers card is " + dealerCard;

                SharedObject.currentRound++;
                while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {

                    if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                        SharedObject.players.get(0).message = "Waiting on other Players";
                    }
                    if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                        SharedObject.players.get(1).message = "Waiting on other Players";
                    }
                    if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                        SharedObject.players.get(2).message = "Waiting on other Players";
                    }
                }

                SharedObject.currentRound++;
                while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                   winner= "Everyone Lost";
                    res =Results(SharedObject.players.get(0).playerCard, SharedObject.players.get(1).playerCard, SharedObject.players.get(2).playerCard, dealerCard);
                           if( res!=0){
                               winner= "The Winning Number is "+res;
                           }

                    SharedObject.players.get(0).message = winner;
                    SharedObject.players.get(1).message = winner;
                    SharedObject.players.get(2).message = winner;

                    if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                        SharedObject.players.get(0).message = "Waiting on other Players";
                    }
                    if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                        SharedObject.players.get(1).message = "Waiting on other Players";
                    }
                    if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                        SharedObject.players.get(2).message = "Waiting on other Players";
                    }

                }
                if(SharedObject.players.get(0).playerCard==res){SharedObject.playerOnePoints++;}
                if(SharedObject.players.get(1).playerCard==res){SharedObject.playerTwoPoints++;}
                if(SharedObject.players.get(2).playerCard==res){SharedObject.playerThreePoints++;}

                System.out.println("Player 1 Points "+SharedObject.playerOnePoints);
                System.out.println("Player 2 Points "+SharedObject.playerTwoPoints);
                System.out.println("Player 3 Points "+SharedObject.playerThreePoints);




            }
        }
        synchronized (SharedObject.players) {
            System.out.println("Player 1 Points "+SharedObject.playerOnePoints);
            System.out.println("Player 2 Points "+SharedObject.playerTwoPoints);
            System.out.println("Player 3 Points "+SharedObject.playerThreePoints);

        }

    }

    public static int Results(int playerOne, int playerTwo, int playerThree, int dealerCard) {


        int high = 0;
        if (playerOne > high) {
            high = playerOne;
        }
        if (playerTwo > high) {
            high = playerTwo;
        }
        if (playerThree > high) {
            high = playerThree;
        }

        if (high < dealerCard) {
            return 0;
        }

        return high;
    }


    class ServerThread extends Thread {

        private String line = null;

        private String message = "";
        private BufferedReader reader = null;
        private PrintWriter writer = null;
        private Socket s = null;
        private int playerId;
        private int playerCard;
        private int response = 0;
        private ArrayList<Integer> taken = new ArrayList<Integer>();


        public ServerThread(Socket s, int playerId) {
            this.s = s;
            this.playerId = playerId;

        }


        public void run() {

            try {
                reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
                writer = new PrintWriter(s.getOutputStream());

                line = reader.readLine();
                while (line != null) {

                    if (message != "") {
                        writer.println(message);
                        message = "";
                        response++;
                    } else
                        try {
                            int card = Integer.parseInt(line);
                            if (taken.contains(card)) {
                                writer.println("Invalid input. you already used that card:");
                            } else if (card >= 1 && card <= 13) {
                                writer.println("You choose " + card);
                                playerCard = card;
                                taken.add(card);
                                response++;
                            } else {
                                System.out.println(line);
                                writer.println("Invalid input. Enter a number between 1 and 13:");

                            }
                        } catch (NumberFormatException e) {
                            writer.println("Invalid input. Enter a number between 1 and 13:");
                        }
                    writer.flush();
                    line = reader.readLine();
                }

            } catch (IOException e) {

                line = this.getName(); //reused String line for getting thread name
                System.out.println("IO Error/ Client " + line + " terminated abruptly");
            } catch (NullPointerException e) {
                line = this.getName(); //reused String line for getting thread name
                System.out.println("Client " + line + " Closed");
            } finally {
                try {
                    System.out.println("Connection Closing..");
                    if (reader != null) {
                        reader.close();
                        System.out.println(" Socket Input Stream Closed");
                    }

                    if (writer != null) {
                        writer.close();
                        System.out.println("Socket Out Closed");
                    }
                    if (s != null) {
                        s.close();
                        System.out.println("Socket Closed");
                    }

                } catch (IOException ie) {
                    System.out.println("Socket Close Error");
                }
            }
        }
    }
}

