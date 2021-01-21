package dev.pagefault.eve.dirtd.console;

public abstract class Command {

	public abstract String getCommandString();

	public abstract String getOptionString();

	public abstract String getHelpString();

	public abstract void execute(DirtConsole console, String[] args);

}
