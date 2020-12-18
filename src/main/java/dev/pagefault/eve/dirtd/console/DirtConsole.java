package dev.pagefault.eve.dirtd.console;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import dev.pagefault.eve.grpc.Task.TaskRequest;
import dev.pagefault.eve.grpc.Task.TaskRequestStatus;
import dev.pagefault.eve.grpc.TaskControllerGrpc.TaskControllerBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class DirtConsole {

	private final ManagedChannel channel;
	private final TaskControllerBlockingStub blockingStub;

	public DirtConsole(ManagedChannel channel) {
		this.channel = channel;
		blockingStub = dev.pagefault.eve.grpc.TaskControllerGrpc.newBlockingStub(channel);
	}

	public void loop() {
		Scanner in = new Scanner(System.in);

		boolean done = false;
		String lastCommand = "";
		while (!done) {
			System.out.print("> ");
			String line = in.nextLine();

			String[] parts = line.split("\\s+");
			if (parts.length == 0) {
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
				System.out.println("here there be dragons");
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
			case "test":
				try {
					TaskRequest req = TaskRequest.newBuilder().setTaskname("test-task-name").build();
					TaskRequestStatus status = blockingStub.enableTask(req);
					System.out.println("success: " + status.getSuccess());
					System.out.println("message: " + status.getMessage());
				} catch (StatusRuntimeException e) {
					System.out.println("grpc error: " + e.getLocalizedMessage());
				}
				break;
			default:
				System.out.println("unknown command");
				break;
			}
		}

		in.close();
	}

	public static void main(String[] args) {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:6524").usePlaintext().build();
		DirtConsole con = new DirtConsole(channel);
		con.loop();
	}
}
