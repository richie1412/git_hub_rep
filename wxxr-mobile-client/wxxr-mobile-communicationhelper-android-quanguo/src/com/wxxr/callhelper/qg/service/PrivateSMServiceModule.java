/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wxxr.callhelper.qg.ClosableIteratable;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.INotificationService;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.adapter.IListDataProvider;
import com.wxxr.callhelper.qg.bean.PrivateSMSetting;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.db.dao.PrivateSMDAO;
import com.wxxr.callhelper.qg.event.NewPrivateSMReceivedEvent;
import com.wxxr.callhelper.qg.event.NumberAddedEvent;
import com.wxxr.callhelper.qg.event.NumberRemovedEvent;
import com.wxxr.callhelper.qg.event.PrivateSMDeletedEvent;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.preference.DictionaryUtils;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.preference.api.IPreferenceManager;

/**
 * @author neillin
 *
 */
public class PrivateSMServiceModule extends AbstractModule<ComHelperAppContext> implements
		IPrivateSMService {
	private static final Trace log = Trace.register(PrivateSMServiceModule.class);
	
	private PrivateSMSetting currentSetting;
	private IPreferenceManager prefManager;
	private Map<String, LinkedList<TextMessageBean>> messages;
	private PrivateSMDAO dao;
	private Map<String, String> nameCache = new HashMap<String, String>();
	private Map<String, PrivateSMSummary> summaries = new HashMap<String, PrivateSMSummary>();
	private boolean initialized = false;
	private boolean viewOnShow;
	private LinkedList<String> numberIdx = new LinkedList<String>();
	private IListDataProvider<PrivateSMSummary> sumDataProvider;
	private ConcurrentHashMap<String, IListDataProvider<TextMessageBean>> itemDataProviders = new ConcurrentHashMap<String, IListDataProvider<TextMessageBean>>();
	private Comparator<TextMessageBean> comparator = new Comparator<TextMessageBean>() {

		@Override
		public int compare(TextMessageBean lhs, TextMessageBean rhs) {
			long val = rhs.getTimestamp() - lhs.getTimestamp();
			if(val == 0L){
				return 0;
			}
			return val < 0 ? 1: -1;
		}
	};
	
	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#isPasswordSetup()
	 */
	@Override
	public boolean isPasswordSetup() {
		makesureInitialized();
		return currentSetting.getPassword() != null;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#setupPassword(java.lang.String)
	 */
	@Override
	public void setupPassword(String passwd) {
		makesureInitialized();
		currentSetting.setPassword(passwd);
		updateLocalPreference(currentSetting);
	}
	
	@Override
	public boolean verifyPassword(String password){
		makesureInitialized();
		String passwd = currentSetting.getPassword();
		if(passwd == null){
			return false;
		}
		return passwd.equals(password);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#addPrivateNumber(java.lang.String)
	 */
	@Override
	public boolean addPrivateNumber(String number) {
		makesureInitialized();
		boolean isHas=currentSetting.addPrivateNumber(number);
		if(isHas){
			updateLocalPreference(currentSetting);
			initPrivateMessages(number);
			NumberAddedEvent evt = new NumberAddedEvent();
			evt.setNumber(number);
			getService(IEventRouter.class).routeEvent(evt);
		}
		return isHas;
	}

	public void onSMReceived(TextMessageBean msg){
		makesureInitialized();
		getDAO().insert(msg);
		addMessage(msg);
		NewPrivateSMReceivedEvent evt = new NewPrivateSMReceivedEvent();
		evt.setMessage(msg);
		context.getService(IEventRouter.class).routeEvent(evt);
		if(currentSetting.isRingBellWhenReceiving()){
			ringBell();
		}
	}
		
	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#removePrivateNumber(java.lang.String)
	 */
	@Override
	public boolean removePrivateNumber(String number) {
		makesureInitialized();
		if(currentSetting.removePrivateNumber(number)){
			updateLocalPreference(currentSetting);
			summaries.remove(number);
			messages.remove(number);
			NumberRemovedEvent evt = new NumberRemovedEvent();
			evt.setNumber(number);
			getService(IEventRouter.class).routeEvent(evt);
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#getAllPrivateNumber()
	 */
	@Override
	public List<String> getAllPrivateNumber() {
		makesureInitialized();
		return currentSetting.getPrivateNumbers();
	}
	
	public void setRingBellWhenReceiving(boolean bool){
		makesureInitialized();
		if(currentSetting.isRingBellWhenReceiving() != bool){
			currentSetting.setRingBellWhenReceiving(bool);
			updateLocalPreference(currentSetting);
			if(bool){
				getService(INotificationService.class).registerNotificationEvent(NewPrivateSMReceivedEvent.class, 1);
			}else{
				getService(INotificationService.class).unregisterNotificationEvent(NewPrivateSMReceivedEvent.class);
			}
		}
	}
	
	public boolean isRingBellWhenReceiving(){
		makesureInitialized();
		return currentSetting.isRingBellWhenReceiving();
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IPrivateSMService#getContactName(java.lang.String)
	 */
	@Override
	public String getContactName(String number) {
		String name = null;
		synchronized(this.nameCache){
			name = this.nameCache.get(number);
			if(name == null){
				name = getNameFromContacts(number);
				if(name != null){
					this.nameCache.put(number, name);
				}
			}
		}
		return name;
	}

	protected void ringBell() {
		
	}
	
	/**
	 * @param number
	 * @return
	 */
	protected String getNameFromContacts(String number) {
		String name = null;
		try {
//			String whereName = ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + " = ?";
//			String[] whereNameParams = new String[] { ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, number };
//			Cursor nameCur = context.getApplication().getAndroidApplication().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, whereName, whereNameParams, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
//			try {
//			if(nameCur.moveToNext()) {
//			    String given = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
//			    String family = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
//			    String display = nameCur.getString(nameCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
//			    if(display != null){
//			    	name = display;
//			    }else if((given != null)||(family != null)){
//			    	name = new StringBuffer((family != null) ? family : "").append(given != null ? given : "").toString();
//			    }
//			}
//			}finally {
//				nameCur.close();	
//			}
			Tools.getContactsName(context.getApplication().getAndroidApplication(), number);
		}catch(Throwable t){
			log.warn("Caught exception when query name from contacts :"+number, t);
		}
		return name;
	}
	
	public List<TextMessageBean> getAllMessageOf(String phoneNumber){
		makesureInitialized();
		LinkedList<TextMessageBean> list=getGroup(phoneNumber, false);
		if(list!=null){
			return Collections.unmodifiableList(list);
		}else{
			return null;
		}
		
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IPreferenceManager.class);		
		addRequiredService(INotificationService.class);		
	}

	@Override
	protected void startService() {
		context.registerService(IPrivateSMService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IPrivateSMService.class, this);
		if(this.dao != null){
			this.dao.close();
			this.dao = null;
		}
		if(this.messages != null){
			this.messages.clear();
			this.messages = null;
		}
		if(getService(INotificationService.class) != null){
			getService(INotificationService.class).unregisterNotificationEvent(NewPrivateSMReceivedEvent.class);
		}
	}
	
	private void doInit(){
		loadLocalPreference();
		initPrivateMessages();
		if(currentSetting.isRingBellWhenReceiving()){
			getService(INotificationService.class).registerNotificationEvent(NewPrivateSMReceivedEvent.class, 1);
		}
	}
	
	@Override
	public boolean isAPrivateNumber(String number){
		makesureInitialized();
		return isInPrivateNumberList(number);
	}

	/**
	 * @param number
	 * @return
	 */
	private boolean isInPrivateNumberList(String number) {
		return currentSetting.getPrivateNumbers().contains(number);
	}
	
	private void initPrivateMessages(String number) {
		PrivateSMDAO db = getDAO();
		if(messages == null){
			messages = new HashMap<String, LinkedList<TextMessageBean>>();
		}
		ClosableIteratable<TextMessageBean> result = db.findByNumber(number);
		if(result != null){
			try {
				for (TextMessageBean bean : result) {
					addMessage(bean);
				}
			}finally{
				result.close();
			}
		}
	}

	/**
	 * 
	 */
	protected PrivateSMDAO getDAO() {
		if(dao == null){
			dao = new PrivateSMDAO(context.getApplication().getAndroidApplication());
			this.dao.openDB();
		}
		return dao;
	}
	
	private void initPrivateMessages() {
		PrivateSMDAO db = getDAO();
		if(this.messages != null){
			this.messages.clear();
		}
		messages = new HashMap<String, LinkedList<TextMessageBean>>();
		ClosableIteratable<TextMessageBean> result = db.findAll();
		if(result != null){
			try {
				for (TextMessageBean bean : result) {
					addMessage(bean);
				}
			}finally{
				result.close();
			}
		}
	}

	/**
	 * @param bean
	 */
	protected void addMessage(TextMessageBean bean) {
		String number = bean.getNumber();
		if(isInPrivateNumberList(number)){
			LinkedList<TextMessageBean> set = getGroup(number, true);
			set.add(bean);
			Collections.sort(set, comparator);
		}
	}
	

	private LinkedList<TextMessageBean> getGroup(String mo, boolean createFlag) {
		synchronized(this.messages){
			LinkedList<TextMessageBean> set = this.messages.get(mo);
			if(createFlag && (set == null)){
				set = new LinkedList<TextMessageBean>();
				this.messages.put(mo, set);
				if(!this.numberIdx.contains(mo)){
					this.numberIdx.add(mo);
				}
			}
			return set;
		}
	}
	
	private synchronized void makesureInitialized() {
		if(!initialized){
			doInit();
			initialized = true;
		}
	}
	
	protected void updateLocalPreference(PrivateSMSetting setting){
		if(setting == null){
			return;
		}
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
		if(d == null){
			d = new Hashtable<String, String>();
			mgr.newPreference(getPrefrencesName(), d);
		}else{
			d = DictionaryUtils.clone(d);
		}
		setting.updateTo(d);
		mgr.putPreference(getPrefrencesName(), d);
	}

	protected void loadLocalPreference() {
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
		PrivateSMSetting setting = new PrivateSMSetting();
		setting.setRingBellWhenReceiving(true);//缺省打开响铃
		if(d != null){
			setting.updateFrom(d);
		}
		currentSetting = setting;
	}

	
	private IPreferenceManager getPrefManager() {
		if(this.prefManager == null){
			this.prefManager = context.getService(IPreferenceManager.class);
		}
		return this.prefManager;
	}
	
	protected String getPrefrencesName()  {
		return getModuleName();
	}
	
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "PrivateSMServiceModule";
	}

	
	@Override
	public List<PrivateSMSummary> getSummarys() {
		makesureInitialized();
		List<String> numbers = currentSetting.getPrivateNumbers();
		if(numbers.isEmpty()){
			return null;
		}
		LinkedList<PrivateSMSummary> sums = new LinkedList<PrivateSMSummary>();
		for (String number : numbers) {
			PrivateSMSummary sum = getSummary(number, true);
			if(sum != null){
				sums.add(sum);
			}
		}
		return sums;
	}

	/**
	 * @param sums
	 * @param number
	 */
	protected PrivateSMSummary getSummary(final String num, boolean createFlag) {
		PrivateSMSummary sum = this.summaries.get(num);
		if(createFlag &&(sum == null)){
			sum = new PrivateSMSummary() {
				private final String number = num;
				@Override
				public String getPhoneNumber() {
					return number;
				}
				
				@Override
				public String getName() {
					return getContactName(number);
				}
				
				@Override
				public int getMessageCount() {
					List<TextMessageBean> msgs = getGroup(number, false);
					return msgs != null ? msgs.size() : 0;
				}
				
				@Override
				public TextMessageBean getLatestMessage() {
					List<TextMessageBean> msgs = getGroup(number, false);
					return (msgs != null)&&(msgs.size() > 0) ? msgs.get(msgs.size()-1) : null;
				}
			};
			this.summaries.put(num, sum);
		}
		return sum;
	}

	@Override
	public void deleteMessage(Integer msgId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the viewOnShow
	 */
	public boolean isViewOnShow() {
		return viewOnShow;
	}

	/**
	 * @param viewOnShow the viewOnShow to set
	 */
	public void setViewOnShow(boolean viewOnShow) {
		this.viewOnShow = viewOnShow;
	}

	@Override
	public synchronized IListDataProvider<PrivateSMSummary> getDataProvider() {
		if(this.sumDataProvider == null){
			this.sumDataProvider = new IListDataProvider<PrivateSMSummary>() {
				
				@Override
				public int getItemCounts() {
					return numberIdx.size();
				}
				
				@Override
				public PrivateSMSummary getItem(int i) {
					String num = numberIdx.get(i);
					return  getSummary(num, true);
				}
			};
		}
		return this.sumDataProvider;
	}

	@Override
	public IListDataProvider<TextMessageBean> getMessageDataProvider(
			final String num) {
		makesureInitialized();
		IListDataProvider<TextMessageBean> p = this.itemDataProviders.get(num);
		if(p == null){
			IListDataProvider<TextMessageBean> newp = new IListDataProvider<TextMessageBean>() {
				private final String number = num;
				@Override
				public int getItemCounts() {
					List<TextMessageBean> msgs = getGroup(number, false);
					return msgs != null ? msgs.size() : 0;
				}
				
				@Override
				public TextMessageBean getItem(int i) {
					List<TextMessageBean> msgs = getGroup(number, false);
					if((msgs == null)||(i >= msgs.size())||(i < 0)){
						throw new ArrayIndexOutOfBoundsException();
					}
					return  msgs.get(i);
				}
			};
			p = this.itemDataProviders.putIfAbsent(num, newp);
			if(p == null){
				p = newp;
			}
		}
		return p;
	}

	@Override
	public void deleteMessage(TextMessageBean msg) {
		if(msg == null){
			return;
		}
		makesureInitialized();
		String number = msg.getNumber();
		LinkedList<TextMessageBean> msgs = getGroup(number, false);
		if(msgs != null){
			msgs.remove(msg);
		}
		getDAO().delete(msg.getId());
		PrivateSMDeletedEvent evt = new PrivateSMDeletedEvent();
		evt.setMessage(msg);
		getService(IEventRouter.class).routeEvent(evt);
	}
	
	@Override
	public void addPrivateNumberMore(List<String> numbers) {
		makesureInitialized();
		for (String number : numbers) {
			addPrivateNumber(number);
		}
	}


	@Override
	public Boolean getMoveOutgoing2PrivateOutbox() {
		makesureInitialized();
		return this.currentSetting.getMoveOutgingIn();
	}

	@Override
	public void setMoveOutgoing2PrivateOutbox(boolean bool) {
		makesureInitialized();
		this.currentSetting.setMoveOutgingIn(bool);
		updateLocalPreference(this.currentSetting);
	}

	@Override
	public void addOutgoingMessage(TextMessageBean msg) {
		makesureInitialized();
		getDAO().insert(msg);
		addMessage(msg);
	}

	@Override
	public void deleteMessage(String number,boolean needrefresh) {
		
		makesureInitialized();
		getDAO().deleteByNumber(number);	
		if(needrefresh){
		initPrivateMessages();
		}
	}

	@Override
	public void onSMReceived(TextMessageBean msg, boolean notifyother,boolean updatecache) {
		makesureInitialized();	
		getDAO().insert(msg);
		if(updatecache){
			addMessage(msg);	
		}
		
		if(notifyother){
		NewPrivateSMReceivedEvent evt = new NewPrivateSMReceivedEvent();
		evt.setMessage(msg);		
		context.getService(IEventRouter.class).routeEvent(evt);
		if(currentSetting.isRingBellWhenReceiving()){
			ringBell();
		}
		}
	}

	
	@Override
	public String getBindedEmail() {
		// TODO Auto-generated method stub
		makesureInitialized();
        return  currentSetting.getBindemail();
	}

	@Override
	public void setBindedEmail(String email) {
		makesureInitialized();
		currentSetting.setBindemail(email);
		updateLocalPreference(currentSetting);
	}

	@Override
	public int getAllUnreadSize() {		
		return getDAO().findUnreadOfAll();
	}

	@Override
	public int getUnreadSizeOfPhoneNumber(String num) {
		// TODO Auto-generated method stub
		 return getDAO().findUnreadOfPhonenumber(num);
	}
	@Override
	public void setReadMsgOfPhoneNumber(String phonenum){
		  getDAO().setReadOfPhonenumber(phonenum);
	}

}
