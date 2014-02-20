package com.project.tasks;



public abstract class PersistentTask extends SimpleAbstractTask {

	public PersistentTask() {
		super();
	}
	
	@Override
	public void executeTask() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExecuting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinished() {
		// TODO Auto-generated method stub
		System.out.println("LALALALLA HIT HERE");
	}
}
