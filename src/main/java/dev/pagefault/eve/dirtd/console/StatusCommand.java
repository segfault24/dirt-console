package dev.pagefault.eve.dirtd.console;

import dev.pagefault.eve.grpc.Common.Empty;
import dev.pagefault.eve.grpc.Common.RequestStatus;
import io.grpc.StatusRuntimeException;

public class StatusCommand extends Command {

	@Override
	public String getCommandString() {
		return "status";
	}

	@Override
	public String getOptionString() {
		return "";
	}

	@Override
	public String getHelpString() {
		return "get the status of the executor";
	}

	@Override
	public void execute(DirtConsole console, String[] args) {
		RequestStatus status = null;
		try {
			status = console.daemonStub.status(Empty.newBuilder().build());
		} catch (StatusRuntimeException e) {
			System.out.println("grpc error: " + e.getLocalizedMessage());
			return;
		}
		if (status != null) {
			System.out.println("success: " + status.getSuccess());
			System.out.println("message: " + status.getMessage());
		} else {
			System.err.println("request status was null!");
		}
	}

}
