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

        public static List<ServerThread> players = Collections.synchronizedList(new ArrayList<ServerThread>());
        public static List<Integer> playerCards = Collections.synchronizedList(new ArrayList<Integer>());

        public static List<Integer> responses = Collections.synchronizedList(new ArrayList<Integer>());

        public static int currentPlayerId = -1;
        public static int currentRound = 1;

        public static boolean isGameOver = false;
    }
    public static void main(String args[]){



        ServerSocket ss2=null;
        System.out.println("Server Listening......");
        int counter =0;



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

        Server_X_Client_test server = new Server_X_Client_test();
        while(counter <3){


            try{

                ServerThread st= server.new ServerThread(ss2.accept(), counter+1);
                st.start();
                System.out.println("connection Established");
                synchronized (SharedObject.players) {
                    SharedObject.players.add(st);
                    counter++;
                }
            }

            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }

        }


        while(SharedObject.currentRound<14) {
            synchronized (SharedObject.players) {
                while (SharedObject.players.get(0).response < SharedObject.currentRound || SharedObject.players.get(1).response < SharedObject.currentRound || SharedObject.players.get(2).response < SharedObject.currentRound) {
                   if (SharedObject.players.get(0).response >= SharedObject.currentRound){ SharedObject.players.get(0).wait=true;}
                   if (SharedObject.players.get(1).response >= SharedObject.currentRound){ SharedObject.players.get(1).wait=true;}
                   if (SharedObject.players.get(2).response >= SharedObject.currentRound){ SharedObject.players.get(2).wait=true;}
                }


                System.out.println("round "+SharedObject.currentRound );
                int dealerCard = deck.remove(0);
                System.out.println("Dealer shows card " + dealerCard);


                SharedObject.players.get(0).dealerCard = dealerCard;
                SharedObject.players.get(1).dealerCard = dealerCard;
                SharedObject.players.get(2).dealerCard = dealerCard;

                SharedObject.players.get(0).wait=false;
                SharedObject.players.get(1).wait=false;
                SharedObject.players.get(2).wait=false;







            /*    SharedObject.players.get(0).clearCard();
                SharedObject.players.get(1).clearCard();
                SharedObject.players.get(2).clearCard();

                SharedObject.players.get(0).cardPrompt();
                SharedObject.players.get(1).cardPrompt();
                SharedObject.players.get(2).cardPrompt();
            */

                System.out.println( SharedObject.players.get(0).playerCard);
                System.out.println( SharedObject.players.get(1).playerCard);
                System.out.println( SharedObject.players.get(2).playerCard);

                SharedObject.players.get(0).begin();
                SharedObject.players.get(1).begin();
                SharedObject.players.get(2).begin();

                SharedObject.currentRound++;

                SharedObject.players.get(0).response = SharedObject.currentRound - 1;
                SharedObject.players.get(1).response = SharedObject.currentRound - 1;
                SharedObject.players.get(2).response = SharedObject.currentRound - 1;


            }
        }


      while (!SharedObject.isGameOver) {
        System.out.println("Round " + SharedObject.currentRound);
        int dealerCard = deck.remove(0);
        System.out.println("Dealer shows card " + dealerCard);
        SharedObject.playerCards.clear();


        synchronized (SharedObject.players) {
            for (ServerThread player : SharedObject.players) {
                player.cardPrompt();
                System.out.println("Deal");
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

    private String lastResponse="";
    private BufferedReader reader = null;
    private PrintWriter writer =null;
    private Socket s=null;
    private int playerId;
    private int playerCard;

    private int dealerCard =0;

    private int lastDealerCard =0;
    private int playerPoints;
    private int response=0;
    private boolean wait=false;



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
    public void begin() {
      //  playerCard=0;
        wait=false;
    }

    public void gameOver() {
        writer.println("Game over!");
        writer.flush();
        writer.println("You scored " + playerPoints + " points.");
        writer.flush();
    }


    public void broadcast (String m)  {

        writer.println(m);
        writer.flush();
         //   this.message=m;

    }


    public void card ()  {

        try {

            writer.println("Your turn! Enter a number between 1 and 13:");
            writer.flush();
            line = reader.readLine();
            while (line != null)  {

                try {
                    int card = Integer.parseInt(line);
                    if (card >= 1 && card <= 13) {
                        playerCard = card;
                    } else {
                        writer.println("Invalid input. Enter a number between 1 and 13:");
                        writer.flush();
                    }
                } catch (NumberFormatException e) {
                    writer.println("Invalid input. Enter a number between 1 and 13:");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    //new function that allows the server to ask a client for a card input
    public void cardPrompt() {
        try {
            //writer = new PrintWriter(s.getOutputStream());
            writer.println("Your turn! Enter a number between 1 and 13:");
            writer.flush();
            while (playerCard == -1) {
                String input = reader.readLine();
                try {
                    int card = Integer.parseInt(input);
                    if (card >= 1 && card <= 13) {
                        playerCard = card;
                    } else {
                        writer.println("Invalid input. Enter a number between 1 and 13:");
                        writer.flush();
                    }
                } catch (NumberFormatException e) {
                    writer.println("Invalid input. Enter a number between 1 and 13:");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }





    
    public void run() {

        try {
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            writer = new PrintWriter(s.getOutputStream());

         //  wait=false;
            line = reader.readLine();
            while (line != null) {

                if (this.wait)  {
                    writer.println("We are waiting on other Players");
                   // writer.flush();
                }
                   else if(response==0){
                       writer.println("You are player " + playerId);
                    //   writer.flush();
                       response++;
                   } else if (dealerCard!= lastDealerCard) {
                       writer.println("The Dealer Plays " + dealerCard);
                    //   writer.flush();
                       lastDealerCard=dealerCard;

                   } else if(line!= lastResponse){
                       writer.println("You are played this card " + line);
                    //   writer.flush();
                    lastResponse=line;
                    response++;
                }


                 else{
                       writer.println("Your turn! Enter a number between 1 and 13:");
                    //   writer.flush();
                       try {
                           int card = Integer.parseInt(line);
                           if (card >= 1 && card <= 13) {
                               playerCard = card;
                             //  this.wait= true;
                           } else {
                               System.out.println(line);
                               writer.println("Invalid input. Enter a number between 1 and 13:");
                         //      writer.flush();
                           }
                       } catch (NumberFormatException e) {
                           writer.println("Invalid input. Enter a number between 1 and 13:");

                       }
                   }
                 // line= null;
                writer.flush();
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
                if (reader !=null){
                    reader.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if(writer !=null){
                    writer.close();
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