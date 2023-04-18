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
        public static int playerOnePoints = 0;
        public static int playerTwoPoints = 0;
        public static int playerThreePoints = 0;
        public static int currentRound = 1;

        public static int Round = 1;

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

            System.out.println("Round 1");
            int dealerCard = deck.remove(0);
            System.out.println("Dealer shows card " + dealerCard+ " of Spades");
            int res=0;


            SharedObject.players.get(0).message = "You are Player 1 and your Suite is Hearts and the Dealers card is " + dealerCard+ " of Spades";
            SharedObject.players.get(1).message = "You are Player 2 and your Suite is Diamonds and the Dealers card is " + dealerCard+ " of Spades";
            SharedObject.players.get(2).message = "You are Player 3 and your Suite is Clubs and the Dealers card is " + dealerCard+ " of Spades";

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

            SharedObject.players.get(0).response=SharedObject.currentRound-1;
            SharedObject.players.get(1).response=SharedObject.currentRound-1;
            SharedObject.players.get(2).response=SharedObject.currentRound-1;


            while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                String winner= "Everyone Lost";
                 res =Results(SharedObject.players.get(0).playerCard, SharedObject.players.get(1).playerCard, SharedObject.players.get(2).playerCard, dealerCard);
                if( res!=0){
                    String a ="";
                    String b ="";
                    String c= "";
                    Integer d=0;
                    Integer e=0;
                    if(SharedObject.players.get(0).playerCard==res){
                        a= "Client 1 ";
                        d++;
                    }
                    if(SharedObject.players.get(1).playerCard==res){
                        if (d>0) {
                            b = "and Client 2" ;
                        }
                        else {
                            b = "Client 2 ";
                        }
                        e++;
                    }
                    if(SharedObject.players.get(2).playerCard==res){

                        if (d>0||e>0) {
                            c = " and Client 3" ;
                        }
                        else {
                            c = "Client 3";
                        }

                    }
                    winner= "Winner of the round: "+a+b+c+" by "+res;
                }
                if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                    SharedObject.players.get(0).message = "Waiting on other Players";
                }else{
                    SharedObject.players.get(0).message = winner;
                }
                if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                    SharedObject.players.get(1).message = "Waiting on other Players";
                }
                else{
                    SharedObject.players.get(1).message = winner;
                }
                if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                    SharedObject.players.get(2).message = "Waiting on other Players";
                }
                else{
                    SharedObject.players.get(2).message = winner;
                }

            }
            if(SharedObject.players.get(0).playerCard==res){SharedObject.playerOnePoints+=dealerCard;}
            if(SharedObject.players.get(1).playerCard==res){SharedObject.playerTwoPoints+=dealerCard;}
            if(SharedObject.players.get(2).playerCard==res){SharedObject.playerThreePoints+=dealerCard;}



        }



        while (SharedObject.Round < 13) {

            synchronized (SharedObject.players) {
                String winner;
                int res=0;

                System.out.println("Round " + (SharedObject.Round+1));
                int dealerCard = deck.remove(0);
                System.out.println("Dealer shows card " + dealerCard + " of Spades");

                SharedObject.currentRound++;

                SharedObject.players.get(0).response=SharedObject.currentRound-1;
                SharedObject.players.get(1).response=SharedObject.currentRound-1;
                SharedObject.players.get(2).response=SharedObject.currentRound-1;

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

                SharedObject.players.get(0).response=SharedObject.currentRound-1;
                SharedObject.players.get(1).response=SharedObject.currentRound-1;
                SharedObject.players.get(2).response=SharedObject.currentRound-1;

                while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                   winner= "Everyone Lost";
                    res =Results(SharedObject.players.get(0).playerCard, SharedObject.players.get(1).playerCard, SharedObject.players.get(2).playerCard, dealerCard);
                    if( res!=0) {
                        String a = "";
                        String b = "";
                        String c = "";
                        Integer d = 0;
                        Integer e = 0;
                        if (SharedObject.players.get(0).playerCard == res) {
                            a = "Client 1 ";
                            d++;
                        }
                        if (SharedObject.players.get(1).playerCard == res) {
                            if (d > 0) {
                                b = "and Client 2";
                            } else {
                                b = "Client 2 ";
                            }
                            e++;
                        }
                        if (SharedObject.players.get(2).playerCard == res) {

                            if (d > 0 || e > 0) {
                                c = " and Client 3";
                            } else {
                                c = "Client 3";
                            }

                        }
                        winner = "Winner of the round: " + a + b + c + " by " + res;
                    }


                    if (SharedObject.players.get(0).response >= SharedObject.currentRound) {
                        SharedObject.players.get(0).message = "Waiting on other Players";
                    }else{
                        SharedObject.players.get(0).message = winner;
                    }
                    if (SharedObject.players.get(1).response >= SharedObject.currentRound) {
                        SharedObject.players.get(1).message = "Waiting on other Players";
                    }
                    else{
                        SharedObject.players.get(1).message = winner;
                    }
                    if (SharedObject.players.get(2).response >= SharedObject.currentRound) {
                        SharedObject.players.get(2).message = "Waiting on other Players";
                    }
                    else{
                        SharedObject.players.get(2).message = winner;
                    }

                }
                if(SharedObject.players.get(0).playerCard==res){SharedObject.playerOnePoints+=dealerCard;}
                if(SharedObject.players.get(1).playerCard==res){SharedObject.playerTwoPoints+=dealerCard;}
                if(SharedObject.players.get(2).playerCard==res){SharedObject.playerThreePoints+=dealerCard;}


            }
            SharedObject.Round++;
        }
        synchronized (SharedObject.players) {

            int high=0;
            if(SharedObject.playerOnePoints>high){
                high=SharedObject.playerOnePoints;
            }
            if(SharedObject.playerTwoPoints>high){
                high=SharedObject.playerTwoPoints;
            }
            if(SharedObject.playerThreePoints>high){
                high=SharedObject.playerThreePoints;
            }

            String winners;
            String a = "";
            String b = "";
            String c = "";
            Integer d = 0;
            Integer e = 0;
            if (SharedObject.playerOnePoints == high) {
                a = "Client 1 ";
                d++;
            }
            if (SharedObject.playerTwoPoints == high) {
                if (d > 0) {
                    b = "and Client 2";
                } else {
                    b = "Client 2 ";
                }
                e++;
            }
            if (SharedObject.playerThreePoints == high) {

                if (d > 0 || e > 0) {
                    c = " and Client 3";
                } else {
                    c = "Client 3";
                }

            }
            winners = "Winner of the Game is: " + a + b + c + " with " + high+" Points!";
            System.out.println("Player 1 Points "+SharedObject.playerOnePoints);
            System.out.println("Player 2 Points "+SharedObject.playerTwoPoints);
            System.out.println("Player 3 Points "+SharedObject.playerThreePoints);
            System.out.println(winners);

            SharedObject.players.get(0).message = "Player 1 Points: "+SharedObject.playerOnePoints+ " Player 2 Points: "+SharedObject.playerTwoPoints+" Player 3 Points: "+SharedObject.playerThreePoints +" "+winners;
            SharedObject.players.get(1).message = "Player 1 Points: "+SharedObject.playerOnePoints+ " Player 2 Points: "+SharedObject.playerTwoPoints+" Player 3 Points: "+SharedObject.playerThreePoints +" "+winners;
            SharedObject.players.get(2).message = "Player 1 Points: "+SharedObject.playerOnePoints+ " Player 2 Points: "+SharedObject.playerTwoPoints+" Player 3 Points: "+SharedObject.playerThreePoints +" "+winners;



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

        private String suite;
        private int playerCard;
        private int response = 0;
        private ArrayList<Integer> taken = new ArrayList<Integer>();


        public ServerThread(Socket s, int playerId) {
            this.s = s;
            this.playerId = playerId;

            if(playerId==1){
                suite="Hearts";
            }
            else if(playerId==2){
                suite="Diamonds";
            }
            else {
                suite= "Clubs";
            }
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
                        if(message.contains("Points")){
                            reader.close();
                            writer.close();
                            s.close();
                        }
                    } else
                        try {
                            int card = Integer.parseInt(line);
                            if (taken.contains(card)) {
                                writer.println("Invalid input. you already used that card:");
                            } else if (card >= 1 && card <= 13) {
                                writer.println("You choose " + card+ " of "+suite);
                                playerCard = card;
                                taken.add(card);
                                response++;
                            } else {

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

