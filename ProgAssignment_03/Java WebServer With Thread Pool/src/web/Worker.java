package web;

import java.io.*;
import java.net.*;
import java.util.*;
// may have to remove java.nio.charset.StandardCharsets . Allows us to convert
// byte array to a string
import java.nio.charset.StandardCharsets;

class Worker extends Thread implements HttpConstants {

    final static int BUF_SIZE = 2048;
    static final byte[] EOL = {(byte) '\r', (byte) '\n'};

    /* buffer to use for requests */
    byte[] buffer;
    /* Socket to client we're handling */
    private Socket socket;
    /* WebServer the worker works for */
    WebServer webServer;

    Worker(WebServer webServer, String name) {
        super(name);
        this.webServer = webServer;
        buffer = new byte[BUF_SIZE];
        this.start();
    }

    synchronized void setSocket(Socket socket) {
        this.socket = socket;
        notify();
    }

    public synchronized void run() {
        // System.out.println("in run"); // REMOVE
        webServer.workerHasStarted();

        while (true) {
            /* nothing to do */
            try {
                wait();
            } catch (InterruptedException e) {
                /* should not happen */
                continue;
            }

            try {
                handleClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /* go back in wait queue if there's fewer
             * than numHandler connections.
             */
            Vector pool = webServer.threads;
            synchronized (pool) {
                if (pool.size() >= webServer.workers) {
                    /* too many threads, exit this one */
                    return;
                } else {
                    pool.addElement(this);
                }
            }
        }
    }

    void handleClient() throws IOException {
        // initialize variables
        int int_steps = 0;
        int int_num = 0;
        String str_int = null;
        boolean valid_int = true;
        byte[] byte_steps;
        
        // DR.OTTES CODE START
        InputStream is = new BufferedInputStream(socket.getInputStream());
        PrintStream ps = new PrintStream(socket.getOutputStream());
        
                                       
        /* we will only block in read for this many milliseconds
         * before we fail with java.io.InterruptedIOException,
         * at which point we will abandon the connection.
         */
        /*
        socket.setSoTimeout(webServer.timeout);
        socket.setTcpNoDelay(true);
        */
        
        /* zero out the buffer from last time */
        for (int i = 0; i < BUF_SIZE; i++) {
            buffer[i] = 0;
        }
        try {
            /* We only support HTTP GET/HEAD, and don't
             * support any fancy HTTP options,
             * so we're only interested really in
             * the first line.
             */
            int nread = 0, r = 0;

                
                r = is.read(buffer, nread, BUF_SIZE - nread);
                                
                if (r == -1) {
                System.out.println("r == -1");

                    /* EOF */
                    return;
                }
        // DR.OTTES CODE END
            
            // convert byte array to string for ease of use
                // method: byteToString(buffer)
            str_int = (String)(byteToString(buffer)).trim();
            System.out.println("Bytes as String:");
            System.out.println(str_int);
                        
            // verify string is int
                // method: isValidInt(str_int)
            valid_int = isValidInt(str_int);
            
            // check if string is a valid int
            if(valid_int)
            {
                System.out.println("String was an integer!");
                // convert stringToInt, save result in int_num 
                    // method: stringToInt(String str_int)
                int_num = stringToInt(str_int);
                System.out.println("String as integer:");
                System.out.println(int_num);
            
                // call collatz conjecture return number of steps it took to get 1
                    // method: collatzSteps(int_num)
                int_steps = collatzSteps(int_num);
                    
                // convert int to bytes.
                /* NOTICE not sure if we can just return a byte array from 
                intToBytes to byte_steps... Java may handle it but we may 
                have to manually copy the array. */
                    // method: intToBytes(int_steps)
                byte_steps = intToBytes(int_steps);
                
                // send client message about steps
                    // method: sendClientStepMsg(byte_steps)
                //sendClientStepMsg(int_steps.byteValue());
                ps.write(Integer.valueOf(int_steps).byteValue());

            }
            // otherwise string is not valid
            else
            {
                // send client message that the string isnt a int
                    // sendClientErrMsg()
                System.out.println("String was not an integer");
                sendClientErrMsg();
                ps.print("an error has occured");
                
            }
        // close the connection regardless of result    
        } finally {
            
            System.out.println("closed socket");
            // close socket
            socket.close();
        }
    }


    
    /*
    desc: should be able to convert a byte array into a string array. Will have to 
    convert Ascii from byte buffer to char for string.
    
    args: buffer which contains user input message.
    
    returns: string version of byte buffer. the string should be the char/string
    represention of the byte buffer.
    */
    public String byteToString(byte[] buffer)
    {
        String s = "";
        
        for (int i = 0; i < buffer.length; i += 1)
        {
            s += (char)buffer[i];
        }
        
        // return string version of buffer array
        return s;
    }
    
    /*
    desc: takes in a int and returns how many steps it took to get to 1. Two rules 
    Rule 1: If the number is even divide by 2 (X/2)
    Rules 2: if the number is odd mutiple by three and add 1 (3X + 1)
    Go until X is 1. Then return how many steps it took to get there.
    
    args: int_num - this is the requested int number from the user to start the
    collatz conjecture at
    
    returns: number of steps it took to get X = 1
    */
    public int collatzSteps(int int_num)
    {
        // initialize variables
        int steps = 0;
        
        // loop while number is not 1
        while (int_num != 1)
        {
            // check if int_num is odd
            if (int_num % 2 != 0)
            {
                // set int_num equal to (3 * int_num) + 1
                int_num = (3 * int_num) + 1;
            }
            // otherwise it's even
            else
            {
                // set int_num equal to int_num/2
                int_num /= 2;
            }   
            // increment steps 
            steps += 1;
        }
        
        // return steps
        return steps;
    }
    
    /*
    desc: Converts int to bytes. This will take the number of steps returned from
    the collatzSteps method and transform it into bytes before sending it off
    to the client
    
    args: int_steps - number of steps it took to get to 1
    
    returns: byte array represtion of the integer int_steps
    */
    public byte[] intToBytes(int int_steps)
    {
        // initialize variables
        
        // calculate the number of bytes needed
        
        // loop though the byte array 
        
            // fill each index with the needed data
        
        // return bytes
        return new byte[1];
    }
    
    /*
    desc: checks if the user gave us a valid int
    
    args: str_int - string represtation of the client input
    
    returns: if the string is a integer
    */
    public boolean isValidInt(String str_int)
    {
        // loop through string
        for (int i = 0; i < str_int.length(); i += 1)
        {
            // check if current character is a digit
                // method isDigit()
            if (('A' <= str_int.charAt(i) && str_int.charAt(i) <= 'Z')
                || ('a' <= str_int.charAt(i) && str_int.charAt(i) <= 'z')
                )
            {
                return false;
            }
        }
        
        // return true since we have looped through the whole string without 
        // hitting a non number char
        return true;
    }
    
    /*
    desc: sends the client an error message if they entered anything that isn't 
    an int
    
    args: none
    
    returns: nothing
    */
    public void sendClientErrMsg()
    {
        // initialize variables

        
        // print that the client fucked up        
    }

    /*
    desc: sends the client how many steps it took for the collatz conjecture
    
    args: byte_steps - number of steps it took to reach 1, in byte format
    
    returns: Nothing
    */
    public void sendClientStepMsg(byte[] byte_steps)
    {
        // initialize variables
    }
    
    /*
    desc: Converts String to int to be used by collatzSteps()
    
    args: str_int - this is the clients input. Should also be verfied as an int
    at this point
    
    returns: integer represention of the client message
    */
    public int stringToInt(String str_int)
    {
        // initialize variables
        int number = 0;
        int base = 1;
        
        for (int i = str_int.length() - 1; i >= 0; i -= 1)
        {
            number += (str_int.charAt(i) - '0') * base;
            base *= 10;
        }
                
        // stub return
        return number;
    }
}
