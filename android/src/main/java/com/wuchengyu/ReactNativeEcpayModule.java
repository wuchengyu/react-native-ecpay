// ReactNativeEcpayModule.java

package com.wuchengyu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.net.URLDecoder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tw.com.ecpay.paymentgatewaykit.manager.*;


public class ReactNativeEcpayModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;

    private String revision = "1.0.0";

    public ReactNativeEcpayModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @NonNull
    @Override
    public String getName() {
        return "ReactNativeEcpay";
    }

    @ReactMethod
    public void initialize(Integer type, Promise promise) {
        switch (type) {
            case 0: {
                PaymentkitManager.initialize(reactContext.getCurrentActivity(), ServerType.Beta);
                promise.resolve("Beta");
                break;
            }
            case 1: {
                PaymentkitManager.initialize(reactContext.getCurrentActivity(), ServerType.Stage);
                promise.resolve("Stage");
                break;
            }
            default: {
                Resources res = reactContext.getResources();
                String env = res.getString(res.getIdentifier("ECPayPaymentGatewayKitEnv", "string", reactContext.getPackageName()));

                switch (env.toLowerCase()) {
                    case "beta": {
                        PaymentkitManager.initialize(reactContext.getCurrentActivity(), ServerType.Beta);
                        promise.resolve("Beta");
                        break;
                    }
                    case "stage": {
                        PaymentkitManager.initialize(reactContext.getCurrentActivity(), ServerType.Stage);
                        promise.resolve("Stage");
                        break;
                    }
                    default: {
                        PaymentkitManager.initialize(reactContext.getCurrentActivity(), ServerType.Prod);
                        promise.resolve("Prod");
                        break;
                    }
                }
            }
        }
    }

    @ReactMethod
    public void createPayment(String token, String merchantID, String appStoreName, String language) {
        LanguageCode languageCode = LanguageCode.zhTW;

        switch (language.toLowerCase()) {
            case "zh-tw":
                languageCode = LanguageCode.zhTW;
                break;
            case "en-us":
                languageCode = LanguageCode.enUS;
                break;
        }

        PaymentkitManager.createPayment(reactContext.getCurrentActivity(), token, merchantID, languageCode, false, appStoreName, PaymentkitManager.RequestCode_CreatePayment);
    }

    public class DecData {
        public int RtnCode;
        public String RtnMsg;
        public String Token;
        public String TokenExpireDate;
    }

    @ReactMethod
    public void getTestingTradeToken(String merchantId, boolean is3D, final String aesKey, final String aesIV, String params, final Promise promise) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(),
                    "AES"), new IvParameterSpec(aesIV.getBytes()));
            byte[] encryptedBytes = cipher.doFinal(params.getBytes());
            String base64Data = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

            GetTokenByTradeInfo getTokenByTradeInfo = new GetTokenByTradeInfo();
            getTokenByTradeInfo.setRqID(String.valueOf(System.currentTimeMillis()));
            getTokenByTradeInfo.setRevision(revision);
            getTokenByTradeInfo.setMerchantID(merchantId);
            getTokenByTradeInfo.setData(base64Data);

            PaymentkitManager.testGetTokenByTrade(reactContext.getCurrentActivity(),
                    getTokenByTradeInfo, new CallbackFunction<GetTokenByTradeInfoCallbackData>() {
                        @Override
                        public void callback(GetTokenByTradeInfoCallbackData callbackData) {
                            try {
                                Log.e("testGetToken.callback", String.valueOf(callbackData.getCallbackStatus()));
                                if (callbackData.getCallbackStatus() ==
                                        CallbackStatus.Success) {
                                    Log.e("testGetToken.callback", String.valueOf(callbackData.getRtnCode()));
                                    if (callbackData.getRtnCode() == 1) {
                                        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                                        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getBytes(),
                                                "AES"), new IvParameterSpec(aesIV.getBytes()));
                                        byte[] decBytes = cipher.doFinal(Base64.decode(callbackData.getData(), Base64.NO_WRAP));
                                        String resStr = new String(decBytes);
                                        String resJson = URLDecoder.decode(resStr);
                                        DecData decData = new Gson().fromJson(resJson, DecData.class);
                                        promise.resolve(decData.Token);
                                    }
                                } else {
                                    promise.reject("-1", "");
                                }
                            } catch (Exception ex) {
                                promise.reject(ex);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    @ReactMethod
    public void getTestingUserToken(String merchantId, boolean is3D, final String aesKey, final String aesIV, String params, final Promise promise) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesKey.getBytes(),
                    "AES"), new IvParameterSpec(aesIV.getBytes()));
            byte[] encryptedBytes = cipher.doFinal(params.getBytes());
            String base64Data = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

            GetTokenByUserInfo getTokenByUserInfo = new GetTokenByUserInfo();
            getTokenByUserInfo.setRqID(String.valueOf(System.currentTimeMillis()));
            getTokenByUserInfo.setRevision(revision);
            getTokenByUserInfo.setMerchantID(merchantId);
            getTokenByUserInfo.setData(base64Data);


            PaymentkitManager.testGetTokenByUser(reactContext.getCurrentActivity(), getTokenByUserInfo, new CallbackFunction<GetTokenByUserInfoCallbackData>() {
                @Override
                public void callback(GetTokenByUserInfoCallbackData callbackData) {

                    try {
                        if (callbackData.getCallbackStatus() ==
                                CallbackStatus.Success) {
                            if (callbackData.getRtnCode() == 1) {
                                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey.getBytes(),
                                        "AES"), new IvParameterSpec(aesIV.getBytes()));
                                byte[] decBytes = cipher.doFinal(Base64.decode(callbackData.getData(), Base64.NO_WRAP));
                                String resJson = URLDecoder.decode(new String(decBytes));

                                DecData decData = new Gson().fromJson(resJson, DecData.class);
                                promise.resolve(decData.Token);
                            } else {
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getPaymentTypeName(PaymentType paymentType) {
        switch (paymentType) {
            case CreditCard:
                return "信用卡";
            case CreditInstallment:
                return "信用卡分期";
            case ATM:
                return "ATM虛擬帳號";
            case CVS:
                return "超商代碼";
            case Barcode:
                return "超商條碼";
            case PeriodicFixedAmount:
                return "信用卡定期定額";
            case NationalTravelCard:
                return "國旅卡";
            default:
                return "";
        }
    }

    private ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == PaymentkitManager.RequestCode_CreatePayment) {
                PaymentkitManager.createPaymentResult(activity, resultCode, data, new CallbackFunction<CreatePaymentCallbackData>() {
                    @Override
                    public void callback(CreatePaymentCallbackData callbackData) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        WritableMap jsObject = Arguments.createMap();
                        jsObject.putInt("RtnCode", callbackData.getRtnCode());
                        jsObject.putString("RtnMsg", callbackData.getRtnMsg());
                        switch (callbackData.getCallbackStatus()) {
                            case Success: {
                                jsObject.putString("Status", "Success");

                                jsObject.putInt("PaymentType", callbackData.getPaymentType().getType());
                                jsObject.putString("PaymentTypeName", getPaymentTypeName(callbackData.getPaymentType()));

                                /* Order info */
                                WritableMap orderInfo = Arguments.createMap();
                                orderInfo.putString("merchantTradeNo", callbackData.getOrderInfo().getMerchantTradeNo());

                                try {
                                    Date convertedDate = dateFormat.parse(callbackData.getOrderInfo().getTradeDate());
                                    orderInfo.putInt("TradeDate", (int) (convertedDate.getTime() / 1000));
                                } catch (ParseException e) {
                                    Log.e("OrderInfo", e.getMessage());
                                    orderInfo.putInt("TradeDate", (int) (new Date().getTime() / 1000));
                                }

                                orderInfo.putString("tradeNo", callbackData.getOrderInfo().getTradeNo());
                                jsObject.putMap("OrderInfo", orderInfo);


                                if (callbackData.getPaymentType() == PaymentType.CreditCard || callbackData.getPaymentType() == PaymentType.CreditInstallment || callbackData.getPaymentType() == PaymentType.PeriodicFixedAmount || callbackData.getPaymentType() == PaymentType.NationalTravelCard) {
                                    WritableMap cardInfo = Arguments.createMap();

                                    cardInfo.putString("AuthCode", callbackData.getCardInfo().getAuthCode());
                                    cardInfo.putString("Gwsr", callbackData.getCardInfo().getGwsr());

                                    try {
                                        Date convertedDate = dateFormat.parse(callbackData.getOrderInfo().getTradeDate());
                                        cardInfo.putInt("ProcessDate", (int) (convertedDate.getTime() / 1000));
                                    } catch (ParseException e) {
                                        Log.e("CardInfo", e.getMessage());
                                        cardInfo.putInt("ProcessDate", (int) (new Date().getTime() / 1000));
                                    }

                                    cardInfo.putInt("Amount", callbackData.getCardInfo().getAmount());
                                    cardInfo.putInt("Eci", callbackData.getCardInfo().getEci());
                                    cardInfo.putString("Card6No", callbackData.getCardInfo().getCard6No());
                                    cardInfo.putString("Card4No", callbackData.getCardInfo().getCard4No());

                                    if (callbackData.getPaymentType() == PaymentType.CreditCard) {
                                        cardInfo.putInt("RedDan", callbackData.getCardInfo().getRedDan());
                                        cardInfo.putInt("RedDeAmt", callbackData.getCardInfo().getRedDeAmt());
                                        cardInfo.putInt("RedOkAmt", callbackData.getCardInfo().getRedOkAmt());
                                        cardInfo.putInt("RedYet", callbackData.getCardInfo().getRedYet());
                                    }

                                    if (callbackData.getPaymentType() == PaymentType.CreditInstallment) {
                                        cardInfo.putInt("Stage", callbackData.getCardInfo().getStage());
                                        cardInfo.putInt("Stast", callbackData.getCardInfo().getStast());
                                        cardInfo.putInt("Staed", callbackData.getCardInfo().getStaed());
                                    }
                                    jsObject.putMap("CardInfo", cardInfo);
                                } else {
                                    jsObject.putNull("CardInfo");
                                }


                                if (callbackData.getPaymentType() == PaymentType.ATM) {
                                    WritableMap ATMInfo = Arguments.createMap();
                                    ATMInfo.putString("BankCode", callbackData.getAtmInfo().getBankCode());
                                    ATMInfo.putString("VAccount", callbackData.getAtmInfo().getvAccount());
                                    ATMInfo.putString("ExpireDate", callbackData.getAtmInfo().getExpireDate());
                                    jsObject.putMap("ATMInfo", ATMInfo);
                                } else {
                                    jsObject.putNull("ATMInfo");
                                }

                                if (callbackData.getPaymentType() == PaymentType.CVS) {
                                    WritableMap CVSInfo = Arguments.createMap();
                                    CVSInfo.putString("PaymentNo", callbackData.getCvsInfo().getPaymentNo());
                                    CVSInfo.putString("ExpireDate", callbackData.getCvsInfo().getExpireDate());
                                    CVSInfo.putString("PaymentURL", callbackData.getCvsInfo().getPaymentURL());
                                    jsObject.putMap("CVSInfo", CVSInfo);
                                } else {
                                    jsObject.putNull("CVSInfo");
                                }

                                if (callbackData.getPaymentType() == PaymentType.Barcode) {
                                    WritableMap barcodeInfo = Arguments.createMap();
                                    barcodeInfo.putString("ExpireDate", callbackData.getBarcodeInfo().getExpireDate());
                                    barcodeInfo.putString("Barcode1", callbackData.getBarcodeInfo().getBarcode1());
                                    barcodeInfo.putString("Barcode2", callbackData.getBarcodeInfo().getBarcode2());
                                    barcodeInfo.putString("Barcode3", callbackData.getBarcodeInfo().getBarcode3());
                                    jsObject.putMap("BarcodeInfo", barcodeInfo);
                                } else {
                                    jsObject.putNull("BarcodeInfo");
                                }

                                break;
                            }
                            case Fail: {
                                jsObject.putString("Status", "Fail");
                                break;
                            }
                            case Cancel: {
                                jsObject.putString("Status", "Cancel");
                                break;
                            }
                        }
                        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onResult", jsObject);
                    }
                });
            }
        }
    };
}
