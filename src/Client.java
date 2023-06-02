
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
public class Client extends  JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading=new JLabel("Client");
    private TextArea messageArea=new TextArea();
    private  TextField messageInput=new TextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);


    public Client()
    {
        try {
            System.out.println("sending request to the server");
            socket=new Socket("127.0.0.1",7777);
            System.out.println("connection done");
            br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

            createGUI();
            hangleEvents();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    private void hangleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println("key is released"+e.getKeyCode());
                if(e.getKeyCode() == 10)
                {
                    /*System.out.println("you have pressed a enter button");*/
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me:"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();

                }

            }
        });
    }

    private void createGUI()
    {
        this.setTitle("Client Messenger");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("icon/b.png"));

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        //messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setEditable(false);


        this.setLayout(new BorderLayout());
        this.add(heading,BorderLayout.NORTH);
        /*JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);*/
        this.add(messageArea,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        this.setVisible(true);

    }
    private void startReading()
    {
        Runnable r1=()->{
            System.out.println("reader started");
            try {
            while(true)
            {
                String msg=br.readLine();
                if(msg.equals("exit"))
                {
                    System.out.println("server terminated the chat");
                    JOptionPane.showMessageDialog(this,"server terminated the chat");
                    messageInput.setEnabled(false);
                    socket.close();
                    break;
                    }
                messageArea.append("Server:"+msg+"\n");
                    //System.out.println("Server:"+msg);
                }
                }catch (Exception e) {
                System.out.println("connection closed");
            }

        };
        new Thread(r1).start();

    }
    private void startWriting()
    {
        Runnable r2 = () -> {
            System.out.println("writing is started....");
            try{
            while(true && !socket.isClosed())
            {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                if (content.equals("exit"))
                {
                    socket.close();
                    break;
                }

                }
                System.out.println("connection closed");
                }catch (Exception e) {
                e.printStackTrace();
            }

        };
        new Thread(r2).start();
    }
    public static void main(String args[])
    {

        new Client();
    }
}
