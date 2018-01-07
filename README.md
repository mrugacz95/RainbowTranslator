# RainbowTranslator

### Overview
IoT project for translating real colors from detector into english words for Android.

[Here you can find RaspberryPi project](https://github.com/KolorowyAleksander/rainbow-translator)

Application first check if RPi is connected in local network with request to `/raspberrypi/version` and expects JSON response:
`{"name": "reinbowtranslator", "version": "1.0.0"}`
Next it uses socketIO to handle colors requests.

### Used technologies

* Kotlin
* RxKotlin
* OkHttp3
* SocketIO

### Screenshots
<img src="https://user-images.githubusercontent.com/12548284/34651888-13f7573e-f3d7-11e7-9a75-d52162298bcc.png" width="292" height="519" /> <img src="https://user-images.githubusercontent.com/12548284/34651898-467f6b06-f3d7-11e7-8d4d-f2335d94cf80.png" width="292" height="519" />
