import { useState, useEffect } from "react";

import ReactNativeEcpay from "./ReactNativeEcpay";

export default ReactNativeEcpay;

export function getSDKVersion() {
  return ReactNativeEcpay.getSDKVersion();
}

export function useReactNativeEcpay(listener, serverType = 1) {
  const RNEcpay = new ReactNativeEcpay(serverType);

  useEffect(() => {
    RNEcpay.addResultListener(listener);
    return () => {
      RNEcpay.removeResultListener();
    };
  }, [listener]);

  return RNEcpay.createPayment;
}

export function useTestingReactNativeEcpay(listener, serverType = 1) {
  const RNEcpay = new ReactNativeEcpay(serverType);

  useEffect(() => {
    RNEcpay.addResultListener(listener);
    return () => {
      RNEcpay.removeResultListener();
    };
  }, [listener]);

  return RNEcpay.createTestingPayment;
}

export function useReactNativeEcpayTestingToken(serverType = 1) {
  const RNEcpay = new ReactNativeEcpay(serverType)
  // const [testingToken, setTestingToken] = useState();

  const getTestingToken = RNEcpay.getTestingToken;

  // useEffect(() => {
  //   getTestingToken();
  // }, []);

  return {
    // testingToken,
    getTestingToken,
  };
}
