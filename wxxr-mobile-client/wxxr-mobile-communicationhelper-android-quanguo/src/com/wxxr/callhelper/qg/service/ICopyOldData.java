package com.wxxr.callhelper.qg.service;

public interface ICopyOldData {
    void importolddata();
    void setNeedImport(boolean need);
    boolean  isImportFinish( );
}
