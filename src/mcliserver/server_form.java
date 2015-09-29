/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcliserver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.UIManager;

/**
 *
 * @author Andromeda
 */
public class server_form extends javax.swing.JFrame {

    ArrayList<PrintWriter> clientOutputStream;
    ArrayList<String> users;

    int port = 2610;
    String[] tempusr = {"new user!"};
    String fname;
    
    Thread receiveThread;
    DefaultListModel listModel;
//     PrintWriter client;

    public class ClientHandler implements Runnable {

        BufferedReader reader;
        Socket socket;
        PrintWriter client;

        public ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                socket = clientSocket;
                InputStreamReader inputReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(inputReader);
            } catch (Exception e) {
                e.printStackTrace();
                serverStatus.append("Error in clientHandler!!");
            }
        }

        @Override
        public void run() {
            String inputMessage;
            String[] messageParts;

            try {
                while (!(inputMessage = reader.readLine()).isEmpty()) {
                    serverStatus.append("IN: "+inputMessage+"\n");
                    messageParts = inputMessage.split(":");

                    for (String messagePart : messageParts) {
                        serverStatus.append(messagePart + "\n");
                    }
                    
                    if (messageParts[2].equals("Connect")) {
                        broadcast(messageParts[0] + ":" + messageParts[1] + ":" + "Chat");
                        addUser(messageParts[0], client);
                    } else if (messageParts[2].equals("Disconnect")) {
                        broadcast(messageParts[0] + ": has disconnected." + ":" + "Chat");
                        removeUser(messageParts[0]);
                    } else if (messageParts[2].equals("Chat")) {
                        broadcast(inputMessage);
                    } else if (messageParts[2].equals("Chat2")) {
                        int indx = users.indexOf(messageParts[3]);
                        String message = messageParts[0] + ":" + messageParts[1]+":Chat";
                        personalChat(message, indx);
                    }else if(messageParts[2].equals("Got")){
                        serverStatus.append("sender is : "+messageParts[1]+"\n");
                        int indx = users.indexOf(messageParts[1]);
                        serverStatus.append("Go 1\n");
                        String message = messageParts[0]+":"+messageParts[1]+":Got";
                        personalChat(message, indx);
                        serverStatus.append("Go 2\n");
                    }else if(messageParts[2].equals("Got-Voice")){
                        serverStatus.append("sender is : "+messageParts[1]+"\n");
                        int indx = users.indexOf(messageParts[1]);
                        serverStatus.append("Go 1\n");
                        String message = messageParts[0]+":"+messageParts[1]+":Got-Voice";
                        personalChat(message, indx);
                        serverStatus.append("Go 2\n");
                    }
                    else if (messageParts[2].equals("Send")) {
                       int indx = users.indexOf(messageParts[1]);
                       String message = inputMessage;
                        personalChat(message, indx);
                    }else if(messageParts[2].equals("Voice")){
                       int indx = users.indexOf(messageParts[1]);
                       String message = inputMessage;
                        personalChat(message, indx);
                        serverStatus.append(messageParts[0]+" is going to send a voice sms to "+messageParts[1]+"\n");
                    }
                    else if(messageParts[2].equals("Fname")){
                        int indx = users.indexOf(messageParts[3]);
                        String message = messageParts[0]+":"+messageParts[1]+":"+messageParts[2];
                        personalChat(message, indx);
                    }
                    else if (messageParts[3].equals("Confd")) {
                        int indx = users.indexOf(messageParts[2]);
                        String message = messageParts[0]+":"+messageParts[1]+":File";
                        personalChat(message, indx);
                    }
                    else if (messageParts[3].equals("con-Voice")) {
                        int indx = users.indexOf(messageParts[2]);
                        String message = messageParts[0]+":"+messageParts[1]+":con-Voice";
                        personalChat(message, indx);
                    }
                    
                    else if(messageParts[2].equals("Voice-sms")){
                        int indx = users.indexOf(messageParts[3]);
                        String message = messageParts[0]+":"+messageParts[1]+":"+messageParts[2];
                        personalChat(message, indx);
                    }else {
                        serverStatus.append("Chat condition not held!!\n");
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                serverStatus.append("Lost Connection with " + users.get(clientOutputStream.indexOf(client)));
                clientOutputStream.remove(client);
            }
        }
    }

    public server_form() {
        listModel = new DefaultListModel();
        listModel.addElement("ALL");
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        serverStatus = new javax.swing.JTextArea();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        portTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Server GUI");

        serverStatus.setBackground(new java.awt.Color(50, 48, 48));
        serverStatus.setColumns(20);
        serverStatus.setFont(new java.awt.Font("Segoe UI Light", 0, 12)); // NOI18N
        serverStatus.setLineWrap(true);
        serverStatus.setRows(5);
        serverStatus.setToolTipText("status window");
        serverStatus.setWrapStyleWord(true);
        serverStatus.setDisabledTextColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(serverStatus);

        startButton.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        startButton.setText("START");
        startButton.setToolTipText("start the server");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        stopButton.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        stopButton.setText("STOP");
        stopButton.setToolTipText("close the server");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        clearButton.setFont(new java.awt.Font("Segoe UI Light", 1, 18)); // NOI18N
        clearButton.setText("CLEAR");
        clearButton.setToolTipText("clear status window ");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });

        userList.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        userList.setModel(listModel);
        userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        userList.setToolTipText("User List");
        jScrollPane2.setViewportView(userList);

        portTextField.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        portTextField.setText("2610");

        jLabel1.setFont(new java.awt.Font("Segoe UI Light", 0, 18)); // NOI18N
        jLabel1.setText("Port :");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(startButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(stopButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(clearButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(startButton)
                            .addComponent(stopButton)
                            .addComponent(clearButton)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        Thread starterThread = new Thread(new StartServer());
        starterThread.start();

        serverStatus.append("Server started....\n");
    }//GEN-LAST:event_startButtonActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        broadcast("Server:is stopping\n:Chat");

        serverStatus.append("Server is stopping services....\n");
        serverStatus.setText("");

    }//GEN-LAST:event_stopButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed

        serverStatus.setText("");
    }//GEN-LAST:event_clearButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(server_form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(server_form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(server_form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(server_form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new server_form().setVisible(true);
            }
        });
    }

    public class StartServer implements Runnable {

        @Override
        public void run() {
            clientOutputStream = new ArrayList();
            users = new ArrayList();
            //get port from textf
            port = Integer.parseInt(portTextField.getText());
            try {
                ServerSocket serverSocket = new ServerSocket(port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream());
                    clientOutputStream.add(clientWriter);

                    Thread listenThread = new Thread(new ClientHandler(clientSocket, clientWriter));
                    listenThread.start();
                    serverStatus.append("Connection established on port : "+port+"\n");
                }
            } catch (Exception e) {
                //e.printStackTrace();
                serverStatus.append("Error establishing a connection!!");
            }
        }
    }

    public void addUser(String name, PrintWriter client) {
        String message;
        users.add(name);
        listModel.addElement(name);
        serverStatus.append(name + " added! \n");
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String temp : tempList) {
            message = temp + ": :Connect";
            broadcast(message);
        }
        broadcast("Server: :Done");
    }

    public void removeUser(String name) {
        String message;
        users.remove(name);
        listModel.removeElement(name);
        message = name + ": :Disconnect";
        broadcast(message);
        broadcast("Server: :Done");

    }

    private void personalChat(String message, int index) {
        
        PrintWriter writer = (PrintWriter) clientOutputStream.get(index);
        serverStatus.append("Got writer!!\n");
        writer.println(message);
        serverStatus.append("Sending : " + message + "\n");
        writer.flush();
        serverStatus.setCaretPosition(serverStatus.getDocument().getLength());

    }

    public void broadcast(String message) {
        Iterator iterate = clientOutputStream.iterator();

        while (iterate.hasNext()) {
            try {
                PrintWriter writer = (PrintWriter) iterate.next();
                writer.println(message);
                serverStatus.append("Sending : " + message + "\n");
                writer.flush();
                serverStatus.setCaretPosition(serverStatus.getDocument().getLength());
            } catch (Exception e) {
                e.printStackTrace();
                serverStatus.append("Broadcasting error!!\n");
            }
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField portTextField;
    private javax.swing.JTextArea serverStatus;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    private javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
