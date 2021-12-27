// ReactNativeEcpayPackage.java

package com.wuchengyu;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;

public class ReactNativeEcpayPackage implements ReactPackage {

    ReactNativeEcpayModule mReactNativeEcpayModule;
    
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        mReactNativeEcpayModule = new ReactNativeEcpayModule(reactContext);
        return Arrays.<NativeModule>asList(mReactNativeEcpayModule);
    }


    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }
}
