function getMMDDYYYY() {
  return new Date()
    .toISOString()
    .replace(/^(\d+)-(\d+)-(\d+)T(\d+):(\d+):(.+)Z/, "$3$2$1");
}
function getCurrentDateString() {
  return new Date()
    .toISOString()
    .replace(/^(\d+)-(\d+)-(\d+)T(\d+):(\d+):(.+)Z/, "$1/$2/$3 $4:$5");
}


export const testingMerchantData = {
  merchantID: "2000132",
  merchantName: "測試商店-2000132",
  aesKey: "5294y06JbISpM5x9",
  aesIV: "v77hoKGq4kWxNNIS",
};

export const testing3DMerchantData = {
  merchantID: "3002607",
  merchantName: "測試商店-3002607",
  aesKey: "pwFHCqoQZGmho4w6",
  aesIV: "EkRm7iFT261dpevs",
};

export function getTestingTradeTokenRequestData(
  paymentType,
  merchantId,
  is3D,
) {
  const periodType = "M";
  const frequency = 12; //至少要大於等於 1次以上。
  //當 PeriodType 設為 D 時，最多可設 365次。
  //當 PeriodType 設為 M 時，最多可設 12 次。
  //當 PeriodType 設為 Y 時，最多可設 1 次。

  const execTimes = 99; //至少要大於 1 次以上。
  //當 PeriodType 設為 D 時，最多可設 999次。
  //當 PeriodType 設為 M 時，最多可設 99 次。
  //當 PeriodType 設為 Y 時，最多可設 9 次。
  const paymentListType = is3D ? 1 : 0; //currentTestMode == TestMode.is3D ? "1" : "0"

  const decryptedDictionary = {
    MerchantID: merchantId,
    RememberCard: 1,
    PaymentUIType: paymentType,
    ChoosePaymentList: paymentListType, //0:全部，1:單純信用卡一次繳清
    OrderInfo: {
      //"MerchantTradeNo": "4200000515202003205168406290",
      MerchantTradeNo: Date.now(),
      MerchantTradeDate: getCurrentDateString(), //"2018/09/03 18:35:20",
      TotalAmount: 200,
      ReturnURL: "https://tw.yahoo.com/",
      TradeDesc: "測試交易",
      ItemName: "測試商品",
    },
    CardInfo: {
      Redeem: "0",
      PeriodAmount: paymentType == 0 ? 200 : 0, //當PaymentUIType為0時，此欄位必填 (必須等於TotalAmount)
      PeriodType: periodType,
      Frequency: frequency,
      ExecTimes: execTimes,
      OrderResultURL: "https://www.microsoft.com/",
      PeriodReturnURL: "https://www.ecpay.com.tw/",
      CreditInstallment: "3,12,24",
      TravelStartDate: getMMDDYYYY(),
      TravelEndDate: getMMDDYYYY(),
      TravelCounty: "001",
    },
    ATMInfo: {
      ExpireDate: 5,
    },
    CVSInfo: {
      StoreExpireDate: "10080",
      Desc_1: "條碼一",
      Desc_2: "條碼二",
      Desc_3: "條碼三",
      Desc_4: "條碼四",
    },
    BarcodeInfo: {
      StoreExpireDate: 5,
    },
    ConsumerInfo: {
      MerchantMemberID: "1234567",
      Email: "test@gmail.com",
      Phone: "0910000222",
      Name: "黃小鴨",
      CountryCode: "002",
      Address: "台北市南港區三重路19-2號 6號棟樓之2, D",
    },
    CardList: [
      {
        PayToken: "123456789",
        Card6No: "123456",
        Card4No: "1234",
        IsValid: 1,
        BankName: "玉山銀行",
        Code: "002",
      },
      {
        PayToken: "987456123",
        Card6No: "654123",
        Card4No: "1111",
        IsValid: 1,
        BankName: "台新銀行",
        Code: "003",
      },
    ],
    UnionPayInfo: {
      OrderResultURL: "https://www.ecpay.com.tw/",
    },
  };

  return decryptedDictionary;
}

export function getTestingUserTokenRequestData(merchantId) {
  return {
    PlatformID: merchantId,
    MerchantID: merchantId,
    ConsumerInfo: {
      MerchantMemberID: "1234567",
      Email: "test@gmail.com",
      Phone: "0910000222",
      Name: "黃小鴨",
      CountryCode: "002",
      Address: "",
    },
  };
}
