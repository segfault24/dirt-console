package dev.pagefault.eve.dirtd.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import dev.pagefault.eve.grpc.DaemonControllerGrpc.DaemonControllerBlockingStub;
import dev.pagefault.eve.grpc.TaskControllerGrpc.TaskControllerBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DirtConsole {

	private final ManagedChannel channel;
	protected final TaskControllerBlockingStub taskStub;
	protected final DaemonControllerBlockingStub daemonStub;
	private Map<String, Command> commands = new HashMap<String, Command>();

	public DirtConsole(ManagedChannel channel) {
		this.channel = channel;
		taskStub = dev.pagefault.eve.grpc.TaskControllerGrpc.newBlockingStub(channel);
		daemonStub = dev.pagefault.eve.grpc.DaemonControllerGrpc.newBlockingStub(channel);

		addCommand(new TaskCommand());
		addCommand(new PoolSizeCommand());
		addCommand(new StatusCommand());
		addCommand(new ForceTypeUpdateCommand());
		addCommand(new LogLevelCommand());
		addCommand(new StructAddCommand());
	}

	public void loop() {
		Scanner in = new Scanner(System.in);

		boolean done = false;
		String lastCommand = "";
		while (!done) {
			System.out.print("> ");
			String line = in.nextLine();

			String[] parts = line.split("\\s+");
			if (parts.length == 0 || (parts.length == 1 && parts[0].isEmpty())) {
				continue;
			}

			String cmd = parts[0].trim().toLowerCase();
			if (cmd.equalsIgnoreCase(".")) {
				cmd = lastCommand;
			} else {
				lastCommand = cmd;
			}

			switch (cmd) {
			case "":
				break;
			case "help":
			case "?":
				help(parts);
				break;
			case "exit":
			case "quit":
			case "q":
				try {
					channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.flush();
				System.exit(0);
				break;
			default:
				Command c = commands.get(cmd);
				if (c != null) {
					c.execute(this, parts);
					continue;
				} else {
					System.err.println("unknown command '" + cmd + "'");
				}
				break;
			}
		}

		in.close();
	}

	private void addCommand(Command c) {
		if (!commands.containsKey(c.getCommandString())) {
			commands.put(c.getCommandString(), c);
		} else {
			System.err.println("Failed to register command '" + c.getCommandString() + "', already exists");
			return;
		}
	}

	private void help(String[] args) {
		if (args.length == 1) {
			// list all commands
			for (Entry<String, Command> c : commands.entrySet()) {
				System.out.println(c.getValue().getCommandString() + " " + c.getValue().getOptionString());
			}
			System.out.println("help <command>");
			System.out.println("exit");
		} else {
			// detail a specific command
			Command c = commands.get(args[1].trim().toLowerCase());
			if (c != null) {
				System.out.println(c.getHelpString());
				return;
			}
		}
	}

	public static void main(String[] args) {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6524").usePlaintext().build();
		DirtConsole con = new DirtConsole(channel);
		con.loop();
	}
}
