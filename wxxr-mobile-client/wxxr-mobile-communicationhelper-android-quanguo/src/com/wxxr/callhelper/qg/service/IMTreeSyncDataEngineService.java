package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.sync.IDataConsumer;

public interface IMTreeSyncDataEngineService {
  void startSync();
  void stopSync();
  void registerConsumer(String key, IDataConsumer consumer);
  boolean unregisterConsumer(String key, IDataConsumer consumer);
}
