import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class ClientS {

	String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Directory");
    private String user="";
	private Socket socket;
	private Dialogs dialogs= new Dialogs(frame);
	private JTextField commands;
	private JButton btnLogIn = new JButton("Log In");
	private JButton btnNewUser = new JButton("Create User");
	private JTextArea textArea = new JTextArea();
    public ClientS(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    private void run() throws IOException {
    	frame.getContentPane().setBackground(Color.WHITE);
    	frame.getContentPane().setLayout(null);
    	socket = new Socket(serverAddress, 59001);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
    	btnLogIn.setBounds(65, 125, 311, 39);
    	frame.getContentPane().add(btnLogIn);
    	btnNewUser.setBounds(62, 39, 314, 39);
    	frame.getContentPane().add(btnNewUser);
    	commands = new JTextField();
    	commands.setEnabled(false);
    	commands.setBounds(10, 181, 412, 31);
    	frame.getContentPane().add(commands);
    	commands.setColumns(10);
    	textArea.setEnabled(false);
    	textArea.setVisible(false);
    	commands.setVisible(false);
    	textArea.setEditable(false);
    	textArea.setBounds(10, 11, 412, 162);
    	frame.getContentPane().add(textArea);
    	
    	btnLogIn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			out.println("LOGIN");
    			while (in.hasNextLine()) {
    				String line = in.nextLine();
                    if (line.equals("USER")) {
                    	user=dialogs.getValue("Username:");
                        out.println(user);
                    } else if (line.equals("PASSWORD")) {
                    	out.println(dialogs.getValue("Password:"));
                    } else if (line.equals("WRONGUSER")) {
                    	dialogs.errorMessage("User not found");
                    	break;
                    }
                    else if (line.equals("USERLOGGED")) {
                    	dialogs.message("user logged");
                    	switchInterface(false);
                    	out.println("cd-"+user);
                    	setTextArea();
                    	break;
                    }
                }
    		}
    	});
    	
    	btnNewUser.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			out.println("NEWUSER");
    			while (in.hasNextLine()) {
    				String line = in.nextLine();
                    if (line.equals("USER")) {
                    	user=dialogs.getValue("Username:");
                        out.println(user);
                    } else if (line.equals("PASSWORD")) {
                    	out.println(dialogs.getValue("Password:"));
                    } else if (line.equals("SIZE")) {
                    	out.println(dialogs.getValue("Size in bytes:"));
                    }else if (line.equals("USERTAKEN")) {
                    	dialogs.errorMessage("User already taken");
                    	break;
                    }
                    else if (line.equals("USERLOGGED")) {
                    	dialogs.message("user created");
                    	switchInterface(false);
                    	out.println("cd-"+user);
                    	setTextArea();
                    	break;
                    }
                }
    		}
    	});
    	commands.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	textArea.setText("");
            	char[] command =  commands.getText().toCharArray();
            	int i=-1;
            	String result="";
            	boolean flag=true;
            	while(i<command.length-1) {
            		i++;
            		if(command[i]==' ' && flag) {
            			result+='-';
            			continue;
            		}
            		if(command[i]=='"') {
            			flag=!flag;
            			continue;
            		}
            		result+=command[i];
            	}
            	System.out.println(result);
                out.println(result);
                commands.setText("");
                setTextArea();
            }
        });
    	
    	
    	
    	
    }

    public static void main(String[] args) throws Exception {
        ClientS client = new ClientS("localhost");
        client.frame.setSize(448, 262);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
    
    @SuppressWarnings("deprecation")
	private void switchInterface(boolean switchV) {
    	btnLogIn.setVisible(switchV);
    	btnNewUser.setVisible(switchV);
    	btnLogIn.enable(switchV);
    	btnNewUser.enable(switchV);
    	textArea.enable(!switchV);
    	textArea.setVisible(!switchV);
    	commands.setVisible(!switchV);
    	commands.setEnabled(!switchV);
    }
    
    private void setTextArea() {
    	String result="";
    	while (in.hasNextLine()) {
    		String input=in.nextLine();
    		if (input.equals("DIRNOTFOUND")) {
    			dialogs.errorMessage("Directory not found");
    			return;
    		}
    		if (input.equals("INVALIDCOMMAND")) {
    			dialogs.errorMessage("Invalid Command");
    			return;
    		}
    		if (input.equals("INVALIDFILE")) {
    			dialogs.errorMessage("File not found or invalid");
    			return;
    		}
    		if (input.equals("INVALIDDIR")) {
    			dialogs.errorMessage("Directory not found or invalid");
    			return;
    		}
    		if (input.equals("SUCCESS")) {
    			dialogs.errorMessage("Successful operation");
    			return;
    		}
    		if (input.equals("CONFIRM")) {
    			String answer;
    			answer=dialogs.getValue("Do you want to overwrite this file? (Y/N)");
                out.println(answer);
    		}
    		if (input.equals("$")) {
    			break;
    		}
    		result+=input;
    	}
    	result=result.replace("$","\n");
    	textArea.setText(result);
    }
}
