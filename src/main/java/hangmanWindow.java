import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class hangmanWindow extends JFrame implements ActionListener, WindowListener {
    private JTextArea scoresArea;
    private JLabel scoresLabel;
    private JTextArea hangmanArea;
    private JLabel hangmanLabel;
    private JTextField userInput;
    private JTextArea serverMsgArea;
    private JLabel serverMsgLabel;
    private JButton sendButton;
    private JLabel commands;
    private JTextArea commandsArea;
    private connectionHandler parent;
    private JLabel wordStateLabel;
    private JTextArea wordStateArea;
    private boolean run;
    public hangmanWindow(connectionHandler parent){
        super("Hangman");
        this.parent = parent;
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.println("Closing socket");
                try {
                    hangmanWindow.this.parent.getSocket().close();
                    dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        setVisible(true);
        setSize(new Dimension(600,600));
        setLocation(600,300);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        run = true;

        c.gridy=0;
        c.weightx=1;
        c.weighty=1;
        c.fill=GridBagConstraints.NONE;
        scoresLabel = new JLabel("Scores: ");
        add(scoresLabel,c);
        commands = new JLabel("Commands: ");
        add(commands,c);

        c.gridy++;
        scoresArea = new JTextArea(3,20);
        scoresArea.setEditable(false);
        add(new JScrollPane(scoresArea),c);

        commandsArea = new JTextArea(3,20);
        commandsArea.append("0 - Ready to play\n");
        commandsArea.append("1 - Exit");
        commandsArea.setEditable(false);
        add(new JScrollPane(commandsArea),c);

        c.gridy++;
        c.gridwidth = 2;
        wordStateLabel = new JLabel("Word state: ");
        add(wordStateLabel,c);
        c.gridy++;
        wordStateArea = new JTextArea(3,20);
        wordStateArea.setEditable(false);
        add(new JScrollPane(wordStateArea),c);

        c.gridy++;
        c.gridwidth=1;
        hangmanLabel = new JLabel("Hangman State: ");
        add(hangmanLabel,c);

        serverMsgLabel = new JLabel("Server Messages: ");
        add(serverMsgLabel,c);

        c.gridy++;
        hangmanArea = new JTextArea(20,20);
        hangmanArea.setEditable(false);
        add(new JScrollPane(hangmanArea),c);

        serverMsgArea = new JTextArea(20,20);
        serverMsgArea.setEditable(false);
        add(new JScrollPane(serverMsgArea),c);

        c.gridy++;
        userInput = new JTextField(20);
        add(userInput,c);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        add(sendButton,c);
        pack();
        Thread readFromServer = new Thread(() -> {
            while(run){
                try {
                    try {
                        ArrayList<String> messages = readFromSocket();
                        if(messages.size() != 0){
                            for(String msg : messages)
                                proccessMessage(msg);
                            }
                        }
                        catch (NullPointerException a){
                            cleanAreas();
                            serverMsgArea.setText("SERVER DOWN");
//                            System.out.println("Server down");
//                            dispose();
                            return;
                        }
                    } catch (IOException e) {
                    System.out.println("Ending program");
                    try {
                        hangmanWindow.this.parent.getSocket().close();
                    } catch (IOException e1) {}
                    dispose();
                    return;
                }
            }
        });
        readFromServer.start();
    }

    private ArrayList<String> readFromSocket() throws IOException {
        ArrayList<String> messages = new ArrayList<String>();
        StringBuilder txt = new StringBuilder();
        String text;
        boolean end = false;
        while (!end) {
            text = parent.getBufferedReader().readLine();
            if(text.equals("$")) {
                end = true;
            }
            else{
                for(int a = 0; a < text.length(); a++){
                    if('$' == text.charAt(a)){
                        end = true;
                    }
                }
                if(end) {
                    String splitTxt[] = text.split("$");
                    for (int i = 0; i < splitTxt.length; i++) {
                        messages.add(splitTxt[i]);
                    }
                }
                else txt.append(text);
                //txt.append(text);
            }
        }
        messages.add(txt.toString());
        return messages;
    }

    private void proccessMessage(String txt) {
        String window[] = splitFirstWord(txt);
        switch (window[0]) {
            case "New":
                cleanAreas();
                break;
            case "Scores":
                printLines(window[1],scoresArea);
                break;
            case "Hangman":
                printLines(window[1],hangmanArea);
                break;
            case "Server":
                printServer(window[1]);
                break;
            case "Word":
                printWord(window[1]);
                break;
        }
    }

    private void cleanAreas(){
        hangmanArea.setText("");
        scoresArea.setText("");
        wordStateArea.setText("");
        serverMsgArea.setText("");
    }
    private void printLines(String txt, JTextArea area){
        area.setText("");
        String lines[] = txt.split("#");
        for(int i=0; i<lines.length; i++)
            area.append(lines[i]+"\n");
    }
    private void printServer(String txt){
        if(serverMsgArea.getLineCount()>5)
            serverMsgArea.setText("");
        serverMsgArea.append(txt+"\n");
    }
    private void printWord(String txt){
        wordStateArea.setText(txt);
    }

    private String[] splitFirstWord(String txt){
        String words[] = txt.split(" ", 2);
        return words;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source==sendButton){
            if(!userInput.getText().equals("")) {
                try {
                    hangmanWindow.this.parent.getBufferedWriter().write(userInput.getText());
                    hangmanWindow.this.parent.getBufferedWriter().flush();
                } catch (IOException e1) {
                    System.out.println("Server error");
                    dispose();
                    return;
                }
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
