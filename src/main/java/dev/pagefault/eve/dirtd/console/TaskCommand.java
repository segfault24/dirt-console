package dev.pagefault.eve.dirtd.console;

import dev.pagefault.eve.grpc.Common.RequestStatus;
import dev.pagefault.eve.grpc.Task.TaskRequest;
import io.grpc.StatusRuntimeException;

public class TaskCommand extends Command {

	@Override
	public String getCommandString() {
		return "task";
	}

	@Override
	public String getOptionString() {
		return "<start|stop|enable|disable|status> <taskname>";
	}

	@Override
	public String getHelpString() {
		return "start, stop, enable, disable a task or get its status";
	}

	@Override
	public void execute(DirtConsole console, String[] args) {
		if (args.length != 3) {
			System.err.println("invalid number of arguments (try 'help " + getCommandString() + "')");
			return;
		}
		TaskRequest req = TaskRequest.newBuilder().setTaskname(args[2]).build();
		RequestStatus status = null;
		try {
			switch (args[1].toLowerCase()) {
			case "start":
				status = console.taskStub.startTask(req);
				break;
			case "stop":
				status = console.taskStub.stopTask(req);
				break;
			case "enable":
				status = console.taskStub.enableTask(req);
				break;
			case "disable":
				status = console.taskStub.disableTask(req);
				break;
			case "status":
				status = console.taskStub.statusTask(req);
				break;
			default:
				System.err.println("invalid subcommand '" + args[1] + "'");
				return;
			}
		} catch (StatusRuntimeException e) {
			System.out.println("grpc error: " + e.getLocalizedMessage());
			return;
		}
		if (status != null) {
			System.out.println("success: " + status.getSuccess());
			System.out.println("message: " + status.getMessage());
		} else {
			System.err.println("task request status was null!");
		}
	}

}
