import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class CmdLameTest {

	public static void main(String[] args) {
	    Command command = new Command();
	    command.setCommand("lame");
	    command.addParameter("--preset");
	    command.addParameter("standard");
	    //command.addParameter("--silent"); //keine Ausgabe
	    //command.addParameter("--quiet"); //keine Ausgabe
	    command.addParameter("--nohist");
	    command.addParameter("--disptime");
	    command.addParameter("20");
	    command.addParameter("/home/andreas/Musik/Genesis - Selling England By The Pound/03 - Firth Of Fifth.wav");
	    command.addParameter("/home/andreas/Musik/Genesis - Selling England By The Pound/mp3/03 - Firth Of Fifth.mp3");
	    
	    String commandString = command.toString();
	    System.out.println(commandString);

	    CommandExecutor cmd = new CommandExecutor();
	    Result result = cmd.execute(command);
	    
	    for(String line : result.asStringList()) {
	    	System.out.println(line);
	    }
	}

}
