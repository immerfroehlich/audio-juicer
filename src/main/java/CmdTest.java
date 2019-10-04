import de.immerfroehlich.command.Command;
import de.immerfroehlich.command.CommandExecutor;
import de.immerfroehlich.command.Result;

public class CmdTest {

	public static void main(String[] args) {
		
		CommandExecutor cmd = new CommandExecutor();
		Command command = new Command();
		command.setCommand("ls");
		command.addParameter("-la");
		
		String cmdstring = command.toString();
		System.out.println(cmdstring);
		
		Result result = cmd.execute(command);
		for(String line : result.asStringList()) {
			System.out.println(line);
		}
		
		command = new Command();
		command.setCommand("ls");
		command.addParameter("-76");
		
		result = cmd.execute(command);
		if (result.hasErrors()) {
			for(String line : result.getStdErr()) {
				System.out.println(line);
			}
		}

	}

}
