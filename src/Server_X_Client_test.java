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

    class SharedObject{
        /*public static List<ServerThread> players = Collections.synchronizedList(new ArrayList<ServerThread>());
        public static List<String> response = Collections.synchronizedList(new ArrayList<String>());

        public static List<Integer> Player1 = Collections.synchronizedList(new ArrayList<Integer>());
        public static List<Integer> Player2 = Collections.synchronizedList(new ArrayList<Integer>());
        public static List<Integer> Player3 = Collections.synchronizedList(new ArrayList<Integer>());
        */
        public static List<ServerThread> players = Collections.synchronizedList(new ArrayList<ServerThread>());
        public static List<Integer> playerCards = Collections.synchronizedList(new ArrayList<Integer>());

        public static int currentPlayerId = -1;
        public static int currentRound = 1;
        public static boolean isGameOver = false;
    }
    public static void main(String args[]){


        //Socket s=null;
        ServerSocket ss2=null;
        System.out.println("Server Listening......");
        int counter =0;
        //int responses =3;
        //int[] dealerCards = {4,6,2,5,3,7,1,9,10,13,12,11};

        //this adds a new deck (1-13) and then shuffles for the dealer/server
        List<Integer> deck = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            deck.add(i);
        }
        Collections.shuffle(deck);

        try{
            ss2 = new ServerSocket(4445); // can also use static final PORT_NUM , when defined

        }
        catch(IOException e){
            e.printStackTrace();
            System.out.println("Server error");

        }
       // ArrayList<ServerThread>  players =new ArrayList<>();


        while(counter <3){
            try{

                //s= ss2.accept();
                System.out.println("connection Established");
                //ServerThread st=new ServerThread(s);
                //st.start();
                //synchronized (SharedObject.players) {
                //  SharedObject.players.add(st);
                //}
                Server_X_Client_test server = new Server_X_Client_test();
                ServerThread st1 = server.new ServerThread(ss2.accept(), 1);
                ServerThread st2 = server.new ServerThread(ss2.accept(), 2);
                ServerThread st3 = server.new ServerThread(ss2.accept(), 3);
                

                st1.start();
                st2.start();
                st3.start();

                synchronized (SharedObject.players) {
                    SharedObject.players.add(st1);
                    SharedObject.players.add(st2);
                    SharedObject.players.add(st3);

                counter++;
            }
        }



            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }

       /*while (SharedObject.Player1.size() <responses ) {
           synchronized (SharedObject.players) {
               SharedObject.players.get(0).card(dealerCards[responses-1]);
               SharedObject.players.get(1).card(dealerCards[responses]);
               SharedObject.players.get(2).card(dealerCards[responses+1]);
           }
       }*/
       // players.get(0).card(5);
       // players.get(1).card(5);
      //  players.get(2).card(5);


      while (!SharedObject.isGameOver) {
        System.out.println("Round " + SharedObject.currentRound);
        int dealerCard = deck.remove(0);
        System.out.println("Dealer shows card " + dealerCard);
        SharedObject.playerCards.clear();
        synchronized (SharedObject.players) {
            for (ServerThread player : SharedObject.players) {
                player.cardPrompt();
            }
        }
        int highestCard = -1;
        int highestPlayerId = -1;
        for (int i = 0; i < SharedObject.playerCards.size(); i++) {
            int card = SharedObject.playerCards.get(i);
            if (card > highestCard) {
                highestCard = card;
                highestPlayerId = i;
            }
        }
        synchronized (SharedObject.players) {
            for (int i = 0; i < SharedObject.players.size(); i++) {
                if (i == highestPlayerId) {
                    SharedObject.players.get(i).addPoint();
                }
                SharedObject.players.get(i).clearCard();
            }
        }
        SharedObject.currentRound++;
        if (SharedObject.currentRound > 13) {
            SharedObject.isGameOver = true;
        }
    }
    synchronized (SharedObject.players) {
        for (ServerThread player : SharedObject.players) {
            player.gameOver();
        }
    }
}





class ServerThread extends Thread{

    private String line=null;
    private BufferedReader  is = null;
    private PrintWriter os=null;
    private Socket s=null;
    private int playerId;
    private int playerCard;
    private int playerPoints;

    public ServerThread(Socket s, int playerId){
        this.s=s;
        this.playerId = playerId;
        //added playerId
    }

    //adding these functions to play the game
    public void addPoint() {
        playerPoints++;
    }

    public void clearCard() {
        playerCard = -1;
    }

    public void gameOver() {
        os.println("Game over!");
        os.flush();
        os.println("You scored " + playerPoints + " points.");
        os.flush();
    }



    public void card (int card)  {

       //line = "Spades"+ card ;
       // System.out.println(Server_X_Client.SharedObject.response.size());
        try {
            //is= new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=new PrintWriter(s.getOutputStream());
            //os.println("spades "+card);
            os.println("Your turn! Play a card between 1 and 13:");
            os.flush();
            
            while (playerCard == -1) {
                // Wait for player to enter a number
            }
            os.println("You played " + playerCard);
            os.flush();
            synchronized (SharedObject.playerCards) {
                SharedObject.playerCards.add(playerCard);
            }
        } 
        catch (IOException e) {
            throw new RuntimeException(e);
        }

       // System.out.println(line);
    }


    //new function that allows the server to ask a client for a card input
    public void cardPrompt() {
        try {
            os = new PrintWriter(s.getOutputStream());
            os.println("Your turn! Enter a number between 1 and 13:");
            os.flush();
            while (playerCard == -1) {
                String input = is.readLine();
                try {
                    int card = Integer.parseInt(input);
                    if (card >= 1 && card <= 13) {
                        playerCard = card;
                    } else {
                        os.println("Invalid input. Enter a number between 1 and 13:");
                        os.flush();
                    }
                } catch (NumberFormatException e) {
                    os.println("Invalid input. Enter a number between 1 and 13:");
                    os.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    
    public void run() {
        try{
            is= new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=new PrintWriter(s.getOutputStream());
            
            //client validation response
            os.println("You are player " + playerId);
            os.flush();


        }catch(IOException e){
            System.out.println("IO error in server thread");
        }

        try {
            /* if (line == "Spades"){                   //commenting this so that we can go back if needed

        }
            else{
            line=is.readLine();}
            while(line.compareTo("QUIT")!=0){
                Server_X_Client.SharedObject.response.add(line);
                os.println(line);
                os.flush();
                System.out.println("Response to Client  :  "+line);
                line=is.readLine();
            } */


            line = is.readLine();
            while (line != null) {
                if (line.startsWith("Your turn!")) {
                    cardPrompt();
                }
                line = is.readLine();
            }

        } catch (IOException e) {

            line=this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client "+line+" terminated abruptly");
        }
        catch(NullPointerException e){
            line=this.getName(); //reused String line for getting thread name
            System.out.println("Client "+line+" Closed");
        }

        finally{
            try{
                System.out.println("Connection Closing..");
                if (is!=null){
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if(os!=null){
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (s!=null){
                    s.close();
                    System.out.println("Socket Closed");
                }

            }
            catch(IOException ie){
                System.out.println("Socket Close Error");
            }
        }//end finally
    }
}
}



/*
 * Notes for building this
 * we need to implement each client to have a total sum of 91 (1+2+3+..+13)=91
 *          this can be our validation that the cards being used are not reused?
 *          this is kinda my though process but its also a little buggy thinking 
 *          that you can use 13 like 4 times i guess
 *          
 *               The project states that the code doesnt need to validate 
 *   
 * 
 *      need to make a simple score card 
 *      
 *      have the server issue a card first then make the client choose
 * 
 * 
 * 
 * 
 * 
 * 
 */