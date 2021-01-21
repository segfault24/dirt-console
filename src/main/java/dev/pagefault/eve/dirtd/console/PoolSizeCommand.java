package dev.pagefault.eve.dirtd.console;

import dev.pagefault.eve.grpc.Daemon.Property;
import dev.pagefault.eve.grpc.Daemon.PropertyStatus;
import io.grpc.StatusRuntimeException;

public class PoolSizeCommand extends Command {

	@Override
	public String getCommandString() {
		return "pool-size";
	}

	@Override
	public String getOptionString() {
		return "<numthreads>";
	}

	@Override
	public String getHelpString() {
		return "get/set the number of worker threads";
	}

	@Override
	public void execute(DirtConsole console, String[] args) {
		if (args.length > 2) {
			System.err.println("invalid number of arguments (try 'help " + getCommandString() + "')");
			return;
		}
		PropertyStatus status = null;

		// get
		if (args.length == 1) {
			Property req = Property.newBuilder().setPropertyName("threads").build();
			try {
				status = console.daemonStub.getProperty(req);
				System.out.println("success: " + status.getStatus().getSuccess());
				System.out.println("message: " + status.getStatus().getMessage());
				System.out.println("value: " + status.getProperty().getPropertyValue());
			} catch (StatusRuntimeException e) {
				System.err.println("grpc error: " + e.getLocalizedMessage());
			}
		}

		// set
		if (args.length == 2) {
			Property req = Property.newBuilder().setPropertyName("threads").setPropertyValue(args[1]).build();
			try {
				status = console.daemonStub.setProperty(req);
				System.out.println("success: " + status.getStatus().getSuccess());
				System.out.println("message: " + status.getStatus().getMessage());
				System.out.println("value: " + status.getProperty().getPropertyValue());
			} catch (StatusRuntimeException e) {
				System.err.println("grpc error: " + e.getLocalizedMessage());
			}
		}
	}

}