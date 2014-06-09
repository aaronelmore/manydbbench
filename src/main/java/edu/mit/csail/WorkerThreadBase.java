package edu.mit.csail;

import java.sql.Connection;
import java.util.Random;

import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;

public abstract class WorkerThreadBase implements Runnable {
	static Logger log = Logger.getLogger(WorkerThreadBase.class.getSimpleName());
	private boolean cont = true;
	private double onChance;
	private double offChance;
	protected boolean isOn;
	private int msInterval;
	protected String dbName;
	private Random rng;
	private StopWatch watch;

	
	protected Connection conn;
	protected String host;
	protected String user;
	protected String password;
	protected String port;
	
	
	public WorkerThreadBase(double onChance, double offChance, boolean onDefault, int msInterval, String dbName){
		this.onChance = onChance;
		this.offChance = offChance;
		this.isOn = onDefault;
		this.msInterval = msInterval;
		this.dbName = dbName;
		this.rng = new Random();
		this.watch = new Log4JStopWatch(dbName,Logger.getLogger("instrument.org.perf4j.TimingLogger"));
		this.host = "localhost";
		this.user = "dbv";
		this.password = "XXX";
	}
	
	public void stop(){
		cont = false;
	}

	
	public abstract void openCon();
	public abstract void closeCon();
	public abstract void doWork() throws Exception;
	
	public void stopWithFailure(String failure) {
		log.error(String.format("(%s) Thread stopping with failure: %s", dbName, failure));
		cont = false;
	}
	
	@Override
	public void run() {
		log.debug(String.format("(%s) Starting db thread", dbName));
		try {
			//Jitter		
			Thread.sleep(rng.nextInt(123));
			if (isOn){
				//Open Connection
				log.debug("Opening connection");
				openCon();
			}

			while (cont){
				//Flip coin
				double coin = rng.nextDouble();
				log.debug(String.format("coin %.2f off:%.2f on:%.2f",coin,offChance,onChance));
				if(isOn && coin < offChance){
					isOn = !isOn;
					log.debug(String.format("(%s) is now off", dbName));
					closeCon();
				}
				else if(!isOn && coin < onChance){
					isOn = !isOn;
					log.debug(String.format("(%s) is now on", dbName));
					openCon();
				}
				
				if (isOn){
					//if on issue read
					log.debug(dbName +"  should read");
					watch.start();
					try {
						doWork();
					} catch (Exception e){
						log.error("Exception on do work",e);
						watch.stop();
					}
					watch.stop();
				}
				//time
				Thread.sleep(this.msInterval);
			}
			if(isOn){
				closeCon();
			}
			log.info(String.format("(%s) Done",dbName));
			//save stat
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
