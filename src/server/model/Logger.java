package server.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class Logger extends Observable {
	
	private static final String PATH = "data/serverlog.txt";
	
	private static Logger logger;

	private List<Log> logs;
	
	private Logger() {
		logs = new ArrayList<Log>();
	}
	
	public static Logger getInstance() {
		if (logger == null)
			logger = new Logger();
		return logger;
	}
	
	public void info(String text) {
		if (text == null) return;
		report();
		if (logs.size() >= 50)
			logs.clear();
		logs.add(new Log(Log.INFO, text));
	}
	
	private void report() {
		setChanged();
		notifyObservers(new Integer(0));
	}
	
	public void warning(String text) {
		if (text == null) return;
		report();
		logs.add(new Log(Log.WARNING, text));
	}
	
	public void system(String text) {
		if (text == null) return;
		report();
		logs.add(new Log(Log.SYSTEM, text));
	}
	
	public void save() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd a");
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------\r\n");
		sb.append("            " + format.format(new Date(System.currentTimeMillis())) + "\r\n");
		sb.append("------------------------------------\r\n");
		for (Log log : logs)
			sb.append(log + "\r\n");
		sb.append("\r\n\r\n\r\n");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, true));
			writer.write(sb.toString());
			writer.close();
		} catch (Exception e) {}
	}
	
	public List<Log> getLogs() {
		return logs;
	}
	
	public Log getLogAt(int index) {
		return logs.get(index);
	}
	
	public int size() {
		return logs.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Log log : logs)
			sb.append(log + "\r\n");
		return sb.toString();
	}
}
