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
import sun.security.pkcs11.Secmod.DbMode;

/**
 * Hello world!
 *
 */
public class ManyBench 
{
	static Logger log = Logger.getLogger(ManyBench.class.getName());
	private CommandLine cmd;

	public ManyBench(CommandLine cmd){
		this.cmd = cmd;
	}
	
	static class Config {
		int numDbs;
		int timer;
		String db;
		double onChance;
		double offChance;
		int interval;
		String dbNameBase;
		int startAt;
		int startOn;
		
		public Config(CommandLine cmd){
			numDbs = Integer.parseInt(cmd.getOptionValue("n"));
			timer = Integer.parseInt(cmd.getOptionValue("t")) * 1000;
			db = cmd.getOptionValue("db", "postgres");
			onChance = Double.parseDouble(cmd.getOptionValue("on", "0.05"));
			if(onChance > 1){
				onChance/=100;
			}
			offChance = Double.parseDouble(cmd.getOptionValue("off", "0.05"));
			if( offChance > 1) {
				offChance/=100;
			}
			interval = Integer.parseInt(cmd.getOptionValue("interval","1000"));
			startAt = Integer.parseInt(cmd.getOptionValue("startAt","0"));
			startOn = Integer.parseInt(cmd.getOptionValue("startOn","1"));

			dbNameBase = cmd.getOptionValue("dbName", "testdb") + "%s";
		}

		@Override
		public String toString() {
			return "Config [numDbs=" + numDbs + ", timer=" + timer + ", db="
					+ db + ", onChance=" + onChance + ", offChance="
					+ offChance + ", interval=" + interval + ", dbNameBase="
					+ dbNameBase + "]";
		}
		
		
	}
	
	public final void run(Config config) throws Exception{

		log.info(config);
		List<WorkerThreadBase> workers = new ArrayList<>();
		List<Thread> threads = new ArrayList<>();
		boolean on=true;
		int started = 0;
		for( int i = 0; i < config.numDbs; i++){
			WorkerThreadBase w = WorkerFactory.makeWorker(config.db, config.onChance, config.offChance, 
					on, config.interval, String.format(config.dbNameBase, (i+config.startAt)));
			Thread t = new Thread(w);
			if (on){
				started++;
				if (started >= config.startOn){
					on = false;
				}
			}
			t.setDaemon(true);
			workers.add(w);
			t.start();
			threads.add(t);
			on = false; //We want one on at start
		}
		Thread.sleep(config.timer);
		for (WorkerThreadBase t: workers){
			t.stop();
		}
		
		for (Thread t: threads){
			t.join();
		}		
		log.info("Done");
	}
	
    public static void main( String[] args ) throws Exception
    {
    	Options options = new Options();
    	options.addOption("n", true, "Number of databases");
    	options.addOption("s", true, "DB Number to start with");
    	options.addOption("t", true, "How long to run");
    	options.addOption("db", true, "DatabaseType");
    	options.addOption("dbName", true, "DatabaseNameBase");
    	options.addOption("on", true, "On Chance");
    	options.addOption("off", true, "Off Chance");
    	options.addOption("interval", true, "Worker Interval (ms)");
    	options.addOption("startOn", true, "How many DBS to start with on");
    	
    	CommandLineParser parser = new GnuParser();
    	CommandLine cmd = parser.parse( options, args);
    	ManyBench app = new ManyBench(cmd);
    	Config config = new Config(cmd);
    	app.run(config);
    }
}
