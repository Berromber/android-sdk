package com.twincoders.twinpush.sdk.communications;

import android.content.Context;

public interface TwinRequestLauncher {

	/**
	 * Obtains current context
	 * @return Context
	 */
	Context getContext();
	
	/**
	 * Establish the request timeout time, in seconds
	 * @param timeOutSeconds
	 */
	void setTimeOutSeconds(int timeOutSeconds);
	
	/**
	 * Adds a request to execution queue
	 */
	void launchRequest(TwinRequest request);

	/** 
	 * Cancels the execution of a requests, removing it from execution queue or canceling its connection if already launched
	 */
	void cancelRequest(TwinRequest request);
}
