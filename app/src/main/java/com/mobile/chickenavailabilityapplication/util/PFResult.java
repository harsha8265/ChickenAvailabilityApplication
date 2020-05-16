package com.mobile.chickenavailabilityapplication.util;

public class PFResult<T> {

    private PFSecurityError mError = null;
    private T mResult = null;

    public PFResult(PFSecurityError mError) {
        this.mError = mError;
    }

    public PFResult(T result) {
        mResult = result;
    }

    public PFSecurityError getError() {
        return mError;
    }

    public T getResult() {
        return mResult;
    }
}
