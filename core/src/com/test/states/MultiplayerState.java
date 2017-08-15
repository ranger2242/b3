package com.test.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.test.Button;
import com.test.Game;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

import static com.test.Game.HEIGHT;
import static com.test.Game.WIDTH;
import static com.test.Main.outc;
import static com.test.states.GameState.*;

/**
 * Created by Chris Cavazos on 8/14/2017.
 */
public class MultiplayerState extends State {
    Button hostB;
    Button joinB;
    public static boolean hosting=false;
    public static boolean joining=false;
    boolean connectMode=true;
    boolean trip=true;
    static String ip="";
    static DataInputStream is;
    static DataOutputStream os;
    static Socket socket;
    static ServerSocket listener;
    static int port=22422;

    MultiplayerState(GameStateManager gsm) {
        super(gsm);

        Texture t = new Texture("button.png");
        hostB = new Button(t, t.getWidth(), t.getHeight());
        joinB = new Button(t, t.getWidth(), t.getHeight());
        hostB.setType(7);
        joinB.setType(8);
        output.clear();


        initCamera();
    }

    public static void setIP(String IP) {
        ip = IP;
        joining=true;
        outc("IP set to "+ip);
        try {
            connectionHandler();
        } catch (IOException e) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void connectionHandler() throws Exception {
        outc("Opening port for connection.");
        if (hosting) {
            listener = new ServerSocket(port);
            outc("Server Socket opened on port " + port);
            socket = listener.accept();
            is =new DataInputStream(socket.getInputStream());
            os =new DataOutputStream(socket.getOutputStream());
            outc("Server created successfully");
            outc(is.readUTF());
            outc(is.readUTF());


        }
        if(joining){
            socket = new Socket(ip, port);
            outc("Socket opened on port "+port);

            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            if(socket.isConnected())
                outc("Client connected successfully");
            os.writeUTF("Client connected");
            os.writeUTF("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
            outc(is.readUTF());

        }


    }
    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return InetAddress.getLocalHost().getHostAddress();
            //return ip;
            //return "127.0.0.1";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void viewIP(){
        try {
            outc("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  // often returns "127.0.0.1"
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Enumeration<NetworkInterface> n = null;
        try {
            n = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (; n.hasMoreElements();)
        {
            NetworkInterface e = n.nextElement();

            Enumeration<InetAddress> a = e.getInetAddresses();
            for (; a.hasMoreElements();)
            {
                InetAddress addr = a.nextElement();
                outc("  " + addr.getHostAddress());
            }
        }
    }
    public static boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    void initCamera(){
        cam.setToOrtho(false, WIDTH, HEIGHT);
        cam.position.set( WIDTH/2,HEIGHT/2,0);
    }

    @Override
    protected void handleInput() {
        updateMousePos();

    }
    @Override
    public void update(float dt) {
        handleInput();
        hostB.update(mpos.x, mpos.y);
        joinB.update(mpos.x, mpos.y);
    }


    @Override
    public void render(SpriteBatch sb) {
        Gdx.gl.glClearColor(0,0,0,1);
        sb.begin();
        sbdrawStringOutput(sb);
        sbdrawButtons(sb);
        sb.end();

    }
    private void sbdrawButtons(SpriteBatch sb) {
        Game.getFont().setColor(Color.BLACK);
        ArrayList<Button> buttons = new ArrayList<>();
        ArrayList<String> bnames = new ArrayList<>();

        buttons.add(hostB);
        buttons.add(joinB);
        bnames.add("HOST");
        bnames.add("JOIN");

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setPos(new Vector2(30,  50 + (i * 40)));
            buttons.get(i).render(sb);
        }
        int c = 0;
        for (String s : bnames) {
            Game.getFont().draw(sb, s, buttons.get(c).getSkin().getX() + 20, buttons.get(c).getSkin().getY() + 20);
            c++;
        }
    }
    private void sbdrawStringOutput(SpriteBatch sb){
       /* for(HoverText t:HoverText.texts){
            t.draw(sb);
        }*/
        for (int i = 0; i < output.size() && i<10; i++) {
            try {
                Color c;
                if(i==output.size()-1){
                    c=Color.WHITE;
                }
                else
                    c=Color.GRAY;
                Game.getFont().setColor(c);
                Game.getFont().draw(sb, output.get(i), (2*WIDTH/3) - 30, HEIGHT - (i * 20)-30);
            } catch (IndexOutOfBoundsException|NullPointerException ignored) {
            }
        }
    }

    @Override
    public void dispose() {

    }
}
