package edu.mit.csail;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class WorkerFactory {
	static enum WorkerType {
		postgres, vertica, mock;
	}
	public static WorkerThreadBase makeWorker(String worker, double onChance, double offChance, boolean onDefault, int msInterval, String dbName){
		WorkerType w = WorkerType.valueOf(worker);
		switch(w) {
			case postgres:
				return new PostgresWorker(onChance, offChance, onDefault, msInterval, dbName);
			case mock:
				return new MockWorker(onChance, offChance, onDefault, msInterval, dbName);
			case vertica:
				throw new NotImplementedException();
			default:
				throw new NotImplementedException();							
		}
	}
}
