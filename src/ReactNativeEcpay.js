import { NativeModules, NativeEventEmitter, Platform } from "react-native";
import {
  getTestingTradeTokenRequestData,
  getTestingUserTokenRequestData,
  testing3DMerchantData,
  testingMerchantData,
} from "./testData";

const NativeModuleEcpay = NativeModules.ReactNativeEcpay;

let instance = null;

export default class ReactNativeEcpay {
  language = "ZH-TW";

  eventEmitter = new NativeEventEmitter(NativeModuleEcpay);
  nativeModuleEcpay = NativeModuleEcpay;

  constructor(serverType, _language) {
    if (_language) {
      this.language = _language;
    }

    if (!instance) {
      instance = this;
    }

    this.nativeModuleEcpay.initialize(serverType);

    return instance;
  }

  createPayment(token, merchantId, storeName = "") {
    this.nativeModuleEcpay.createPayment(
      token,
      merchantId,
      storeName,
      this.language
    );
  }

  addResultListener(listener) {
    instance.eventEmitter.addListener("onResult", listener);
  }

  removeResultListener() {
    instance.eventEmitter.removeAllListeners("onResult");
  }

  createTestingPayment(token, is3D) {
    const merchantData = !!is3D ? testing3DMerchantData : testingMerchantData;
    instance.createPayment(
      token,
      merchantData.merchantID,
      merchantData.merchantName
    );
  }

  static getSDKVersion() {
    return NativeModuleEcpay.getSDKVersion()
  }

  /**
   * @param paymentUIType 0:定期定額, 1:國旅卡, 2:付款選擇清單頁, 3:用於非交易類型
   * @param is3D 是否開啟 3D 驗證
   * @returns 付款 Token
   */
  getTestingToken(paymentUIType, is3D) {
    const merchantData = is3D ? testing3DMerchantData : testingMerchantData;
    console.log("nativeModuleEcpay", paymentUIType, is3D, instance);
    if (paymentUIType < 3) {
      if (Platform.OS === "ios") {
        return instance.nativeModuleEcpay.getTestingTradeToken(
          paymentUIType,
          merchantData.merchantID,
          !!is3D,
          merchantData.aesKey,
          merchantData.aesIV,
          getTestingTradeTokenRequestData(
            paymentUIType,
            merchantData.merchantID,
            !!is3D
          )
        );
      }
      if (Platform.OS === "android") {
        return instance.nativeModuleEcpay?.getTestingTradeToken(
          merchantData.merchantID,
          !!is3D,
          merchantData.aesKey,
          merchantData.aesIV,
          JSON.stringify(
            getTestingTradeTokenRequestData(
              paymentUIType,
              merchantData.merchantID,
              !!is3D
            )
          )
        );
      }
    } else {
      if (Platform.OS === "ios") {
        return instance.nativeModuleEcpay.getTestingTradeToken(
          merchantData.merchantID,
          !!is3D,
          merchantData.aesKey,
          merchantData.aesIV,
          getTestingUserTokenRequestData(merchantData.merchantID)
        );
      }
      if (Platform.OS === "android") {
        return instance.nativeModuleEcpay?.getTestingUserToken(
          merchantData.merchantID,
          !!is3D,
          merchantData.aesKey,
          merchantData.aesIV,
          JSON.stringify(
            getTestingUserTokenRequestData(merchantData.merchantID)
          )
        );
      }
    }
  }
}
