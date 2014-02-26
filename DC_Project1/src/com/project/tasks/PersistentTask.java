package com.project.tasks;

public abstract class PersistentTask extends SimpleAbstractTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9128306171904510455L;

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
	public void onFinished() {
		// TODO Auto-generated method stub
		System.out.println("LALALALLA HIT HERE");
	}

	@Override
	public void onProgressUpdate() {
		// TODO Auto-generated method stub

	}
}
