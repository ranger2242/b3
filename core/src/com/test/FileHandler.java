package com.test;

import com.badlogic.gdx.Gdx;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import static com.test.Player.players;


/**
 * Created by USER on 11/18/2016.
 */
public class FileHandler implements Serializable {

    public static void save() throws IOException {
        JFrame parentFrame = new JFrame();

        //      JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Specify a file to save");

//        int userSelection = fileChooser.showSaveDialog(parentFrame);
        File f;
//        if (userSelection == JFileChooser.APPROVE_OPTION) {
        f = new File(Gdx.files.internal("data.qp").path());
        FileOutputStream out = new FileOutputStream(f.getAbsolutePath());
        ObjectOutputStream ost = new ObjectOutputStream(out);
        for (Player p : players) {
            ost.writeObject(p);
        }

        ost.close();


    }

    public static ArrayList<Player> loadPlayerList() {//trying to upload new load function
        File f;
        ArrayList<Player> temp = new ArrayList<>();
        f = new File(Gdx.files.internal("data.qp").path());
        try {
            FileInputStream in = new FileInputStream(f.getAbsolutePath());
            ObjectInputStream oit = new ObjectInputStream(in);
            try {
                while (true) {
                    temp.add((Player) oit.readObject());
                }
            } catch (EOFException e) {
                oit.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return temp;
    }



    public static void load() {//trying to upload new load function
        File f;
        f =new File(Gdx.files.internal("data.qp").path());
        try {
            FileInputStream in = new FileInputStream(f.getAbsolutePath());
            ObjectInputStream oit = new ObjectInputStream(in);
            try {
                while (true) {
                   players.add((Player)oit.readObject());
                }
            }catch (EOFException e){
                oit.close();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }//

}

