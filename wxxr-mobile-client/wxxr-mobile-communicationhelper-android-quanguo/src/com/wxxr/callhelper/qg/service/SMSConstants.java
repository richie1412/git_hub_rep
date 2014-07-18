/**
 * 
 */
package com.wxxr.callhelper.qg.service;

/**
 * @author neillin
 *
 */
public interface SMSConstants {
	 	int TYPE_ALL    = 0;
	 	int TYPE_INBOX  = 1;
	 	int TYPE_SENT   = 2;
	 	int TYPE_DRAFT  = 3;
	 	int TYPE_OUTBOX = 4;
	 	int TYPE_FAILED = 5;
	 	int TYPE_QUEUED = 6; 
	 	
	 	int STATUS_COMPLETED = 0;
	 	int STATUS_RECEIVED = 1;
	 	int STATUS_PEDING = 64;
	 	int STATUS_FAILED = 128;
	 	
	 	int READ_READ = 1;
	 	int READ_UNREAD = 0;
	 	
	 	int PROTOCOL_SMS = 0;
	 	int PROTOCOL_MMS = 1;
}
