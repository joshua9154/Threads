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


public class Server_X_Client {

    class SharedObject{
        public static List<ServerThread> players = Collections.synchronizedList(new ArrayList<ServerThread>());
        public static List<String> response = Collections.synchronizedList(new ArrayList<String>());

        public static List<Integer> Player1 = Collections.synchronizedList(new ArrayList<Integer>());
        public static List<Integer> Player2 = Collections.synchronizedList(new ArrayList<Integer>());
        public static List<Integer> Player3 = Collections.synchronizedList(new ArrayList<Integer>());
    }
    public static void main(String args[]){


        Socket s=null;
        ServerSocket ss2=null;
        System.out.println("Server Listening......");
        int counter =0;
        int responses =1;
        int[] dealerCards = {4,6,2,5,3,7,1,9,10,13,12,11};
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

                s= ss2.accept();
                System.out.println("connection Established");
                ServerThread st=new ServerThread(s);
                st.start();
                synchronized (SharedObject.players) {
                    SharedObject.players.add(st);
                }



                counter++;

            }

            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }

       while (SharedObject.Player1.size() <responses ) {
           synchronized (SharedObject.players) {
               SharedObject.players.get(0).card(dealerCards[responses]);
               SharedObject.players.get(1).card(dealerCards[responses]);
               SharedObject.players.get(2).card(dealerCards[responses]);
           }
       }
       // players.get(0).card(5);
       // players.get(1).card(5);
      //  players.get(2).card(5);

    }

}

class ServerThread extends Thread{

    String line=null;
    BufferedReader  is = null;
    PrintWriter os=null;
    Socket s=null;

    public ServerThread(Socket s){
        this.s=s;
    }

    public void card (int card)  {

       //line = "Spades"+ card ;
       // System.out.println(Server_X_Client.SharedObject.response.size());
        try {
            is= new BufferedReader(new InputStreamReader(s.getInputStream()));

                line=is.readLine();
                System.out.println(line);
            os=new PrintWriter(s.getOutputStream());
            os.println("spades "+card);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       // System.out.println(line);
    }

    public void run() {
        try{
            is= new BufferedReader(new InputStreamReader(s.getInputStream()));
            os=new PrintWriter(s.getOutputStream());


        }catch(IOException e){
            System.out.println("IO error in server thread");
        }

        try {
            if (line == "Spades"){

            }
            else{
            line=is.readLine();}
            while(line.compareTo("QUIT")!=0){
                Server_X_Client.SharedObject.response.add(line);
                os.println(line);
                os.flush();
                System.out.println("Response to Client  :  "+line);
                line=is.readLine();
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