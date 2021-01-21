package dev.pagefault.eve.dirtd.console;

import dev.pagefault.eve.grpc.Common.RequestStatus;
import dev.pagefault.eve.grpc.Daemon.Struct;
import io.grpc.StatusRuntimeException;

public class StructAddCommand extends Command {

	@Override
	public String getCommandString() {
		return "structadd";
	}

	@Override
	public String getOptionString() {
		return "<structid>";
	}

	@Override
	public String getHelpString() {
		return "add a market structure to the database, using the default esi key";
	}

	@Override
	public void execute(DirtConsole console, String[] args) {
		if (args.length != 2) {
			System.err.println("invalid number of arguments (try 'help " + getCommandString() + "')");
			return;
		}

		try {
			long structId = Long.parseLong(args[1]);
			Struct req = Struct.newBuilder().setStructid(structId).build();
			try {
				RequestStatus status = console.daemonStub.addStruct(req);
				System.out.println("success: " + status.getSuccess());
				System.out.println("message: " + status.getMessage());
			} catch (StatusRuntimeException e) {
				System.err.println("grpc error: " + e.getLocalizedMessage());
			}
		} catch (NumberFormatException e) {
			System.err.println("structure id was not a number");
		}
	}

}
