package com.checkers.client;

import com.checkers.domain.vo.Field;
import com.checkers.domain.vo.Step;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by Isaiev on 01.10.2015.
 */
public class Client {

    public static void main(String[] args)  {
        Socket clsock = null;
        ObjectInputStream inObject = null;
        ObjectOutputStream outObj = null;
        CheckersBot bot;
        String className = args[0];
        String host = args[1];
        Integer port;
        String teamName = args[3];
        try {
            port = Integer.valueOf(args[2]);
            bot = (CheckersBot) Class.forName(className).newInstance();

            System.out.println("newInstance created");
            clsock = new Socket(host, port);
            System.out.println("Connection established");

            inObject = new ObjectInputStream(clsock.getInputStream());
            outObj = new ObjectOutputStream(clsock.getOutputStream());
            System.out.println("teamName "+teamName);
            ObjectMapper objectMapper = new ObjectMapper();
            System.out.println("objectMapper created");
            outObj.writeObject(teamName);
            Step userStep = null;
            long timer;
            while (true) {
                String field = (String)inObject.readObject();
                Field currentField = objectMapper.readValue(field, Field.class);
                System.out.println(currentField);
                timer = System.currentTimeMillis();
                if(currentField != null){
                    userStep = bot.calculateNextStep(currentField);
                }
                timer = System.currentTimeMillis() - timer;
                userStep.setUsedTime(timer);
                outObj.writeObject(objectMapper.writeValueAsString(userStep));
            }
        } catch (EOFException e) {
            System.out.println("Game Finished");
        } catch (IOException e) {
            System.err.println("Client error! " + e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("Client error! " + e);
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                clsock.close();
                inObject.close();
                outObj.close();
            } catch (IOException e) {
                System.err.println("Client error while closing! " + e);
            }

        }
    }
}
