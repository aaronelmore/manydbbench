package edu.mit.csail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Hello world!
 *
 */
public class App 
{
	static Logger log = Logger.getLogger(App.class.getName());
	private CommandLine cmd;

	public App(CommandLine cmd){
		this.cmd = cmd;
	}
	
	class WorkerThread implements Runnable {
		boolean cont = true;
		private double onChance;
		private double offChance;
		private boolean isOn;
		private int msInterval;
		private String dbName;
		private Random rng;
		private StopWatch watch;
		
		public WorkerThread(double onChance, double offChance, boolean onDefault, int msInterval, String dbName){
			this.onChance = onChance;
			this.offChance = offChance;
			this.isOn = onDefault;
			this.msInterval = msInterval;
			this.dbName = dbName;
			this.rng = new Random();
			this.watch = new Log4JStopWatch(dbName);
		}
		
		public void stop(){
			cont = false;
		}
		
		public List<Double> getStats(){
			throw new NotImplementedException();
		}
		
		@Override
		public void run() {
			if (isOn){
				//Open Connection
			}
			try {
				while (cont){
					//Flip coin
					double coin = rng.nextDouble();
					if(isOn && coin < offChance){
						isOn = !isOn;
						log.info(dbName +" is now off");
						//TODO close conn
					}
					else if(!isOn && coin < onChance){
						isOn = !isOn;
						log.info(dbName +" is now on");
						//TODO open conn
					}
					
					if (isOn){
						//if on issue read
						log.info(dbName +"  should read");
						//TODO read and record stat
						watch.start();
						
						watch.stop();
					}
					//time
					Thread.sleep(this.msInterval);
				}
				log.info("Stopped");
				//save stat
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void run() throws Exception{
		int n = Integer.parseInt(cmd.getOptionValue("n"));
		int timer = Integer.parseInt(cmd.getOptionValue("t")) * 1000;
		List<WorkerThread> workers = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();
		for( int i = 0; i < n; i++){
			WorkerThread w = new WorkerThread(0.02, 0.98, false, 1000, "DB");
			Thread t = new Thread(w);
			t.setDaemon(true);
			workers.add(w);
			t.start();
			threads.add(t);
		}
		Thread.sleep(timer);
		for (WorkerThread t: workers){
			t.stop();
		}
		
		for (Thread t: threads){
			t.join();
		}
		
		for (WorkerThread t: workers){
			//TODO get stats or error
			t.getStats();
		}
		
	}
	
    public static void main( String[] args ) throws Exception
    {
    	Options options = new Options();
    	options.addOption("n", true, "Number of databases");
    	options.addOption("t", true, "How long to run");
    	
    	CommandLineParser parser = new GnuParser();
    	CommandLine cmd = parser.parse( options, args);
    	App app = new App(cmd);
    	app.run();
    }
}
