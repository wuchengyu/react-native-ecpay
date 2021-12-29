import React, {useState, useEffect, useRef} from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
  TouchableOpacity,
  Switch,
} from 'react-native';
import {
  getSDKVersion,
  useReactNativeEcpayTestingToken,
  useTestingReactNativeEcpay,
} from 'react-native-ecpay';
import {Picker} from '@react-native-picker/picker';
import {Colors, Header} from 'react-native/Libraries/NewAppScreen';

const Row = ({children}) => <View style={styles.row}>{children}</View>;
const Title = ({children}) => <Text style={styles.title}>{children}</Text>;

const App = () => {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };
  const didMountRef = useRef(false);
  const [token, setToken] = useState('');
  const [uiPaymentType, setUiPaymentType] = useState(2);
  const [is3D, setIs3D] = useState(false);
  const [result, setResult] = useState('');
  const {getTestingToken} = useReactNativeEcpayTestingToken(1);
  const createPayment = useTestingReactNativeEcpay(res =>
    setResult(JSON.stringify(res)),
  );

  useEffect(() => {
    if (didMountRef.current) {
      setToken('');
      setResult('');
    } else {
      didMountRef.current = true;
    }
  }, [is3D, uiPaymentType]);

  const onTokenClicked = () => {
    getTestingToken(uiPaymentType, is3D).then(t => {
      setToken(t);
    });
  };

  const onPaymentClicked = () => {
    if (!token) return;
    createPayment(token);
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <Header />
      <View
        style={{
          backgroundColor: isDarkMode ? Colors.black : Colors.white,
        }}>
        <Row>
          <View style={{width: 100}}>
            <Title>版本</Title>
          </View>
          <View style={{flex: 1}}>
            <Title>{getSDKVersion()}</Title>
          </View>
        </Row>
        <Row>
          <View style={{width: 100}}>
            <Title>類型</Title>
          </View>
          <View style={{flex: 1}}>
            <Picker
              selectedValue={uiPaymentType}
              onValueChange={(itemValue, itemIndex) =>
                setUiPaymentType(itemValue)
              }>
              <Picker.Item label="定期定額" value={0} />
              <Picker.Item label="國旅卡" value={1} />
              <Picker.Item label="付款選擇頁" value={2} />
              <Picker.Item label="非交易類型" value={3} />
            </Picker>
          </View>
        </Row>
        <Row>
          <View style={{width: 100}}>
            <Title>3D 驗證</Title>
          </View>
          <Switch value={is3D} onValueChange={setIs3D} />
        </Row>
        <Row>
          <TouchableOpacity
            style={{
              backgroundColor: '#B2B2FF',
              borderRadius: 32,
              flex: 1,
              justifyContent: 'center',
              alignItems: 'center',
              paddingVertical: 6,
              marginVertical: 16,
            }}
            onPress={onTokenClicked}>
            <Title>取得 Token</Title>
          </TouchableOpacity>
        </Row>
        <Row>
          <View style={{width: 100, paddingVertical: 16}}>
            <Title>Token</Title>
          </View>
          <View style={{flex: 1}}>
            <Text>{token}</Text>
          </View>
        </Row>
        <Row>
          <TouchableOpacity
            style={{
              backgroundColor: '#B2B2FF',
              borderRadius: 32,
              flex: 1,
              justifyContent: 'center',
              alignItems: 'center',
              paddingVertical: 6,
              marginVertical: 16,
            }}
            onPress={onPaymentClicked}>
            <Title>結帳測試</Title>
          </TouchableOpacity>
        </Row>
        <Text style={{paddingHorizontal: 16}}>回傳</Text>
        <Text style={{paddingHorizontal: 16}}>{result}</Text>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    paddingHorizontal: 16,
    alignItems: 'center',
  },
  title: {
    fontSize: 22,
  },
  content: {
    flex: 1,
  },
});

export default App;
