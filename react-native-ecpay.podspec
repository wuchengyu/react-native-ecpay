# react-native-ecpay.podspec

require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-ecpay"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  react-native-ecpay
                   DESC
  s.homepage     = "https://github.com/wuchengyu/react-native-ecpay"
  # brief license entry:
  s.license      = "MIT"
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  s.authors      = { "Wu Cheng-Yu" => "jvyu59jvyu@icloud.com" }
  s.platforms    = { :ios => "10.0" }
  s.source       = { :git => "https://github.com/wuchengyu/react-native-ecpay.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,c,cc,cpp,m,mm,swift}"
  s.requires_arc = true

  s.static_framework = true

  s.dependency "React"
  # ...
  # s.dependency "..."
  s.dependency 'ECPayPaymentGatewayKit', '~> 1.1.0' 

  s.dependency 'PromiseKit' , '~> 6.8.3'
  s.dependency 'Alamofire', '~> 5.2.1'
  s.dependency 'IQKeyboardManagerSwift'
  s.dependency 'KeychainSwift', '~> 16.0'
  s.dependency 'SwiftyJSON', '~> 4.2.0'
  s.dependency 'SwiftyXMLParser'
  s.dependency 'CryptoSwift', '~> 1.4.1'


end

