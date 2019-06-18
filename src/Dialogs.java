import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Dialogs {
	 JFrame frame;
	public Dialogs(JFrame frame){
		this.frame=frame;
	}
	
    public String getValue(String message) {
        return JOptionPane.showInputDialog(
            frame,
            message,
            "input",
            JOptionPane.PLAIN_MESSAGE
        );
    }
    public void errorMessage(String message) {
    	JOptionPane.showMessageDialog(frame,
    		    message,
    		    "Inane error",
    		    JOptionPane.ERROR_MESSAGE);
    }
    public void message(String message) {
    	JOptionPane.showMessageDialog(frame,
    		    message);
    }
    
}
