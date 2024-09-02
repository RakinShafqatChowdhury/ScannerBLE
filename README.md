Build & Run:

At first go to the assignment GitHub URL, click code button and copy HTTPS URL. Paste it to the import project from version control in Android Studio. Then whole project will be synced and build. Connect a device to Android Studio through cable or same WIFI network. Click run to install the app in your device.


Approach:

For the app, I have chosen Kotlin and used MVVM pattern. The app name is ScannerBLE. I have created 2 modules. One is for scanning nearby devices and other one is connect to selected device and fetch it's characteristics.

After opening the app, there will be an empty page where a Search button is displayed. Upon clicking, all the necessary permissions will be taken from the user in runtime. After that user will be prompted to turn on Bluetooth if it is found off. 
After turning on Bluetooth, all the nearby scannable BLE devices will be populated in the page as list along with device's name, address and RSSI signal strength which is real-time. A simple visual representation is also implemented regarding signal strength. Then user needs to click on the certain device which s/he wants to connect with. If the device is connectable, user will be taken to the next screen. If not, then a toast message will be displayed.

On the next screen, the selected device will automatically try to get connected. If the connection establishes, status will be changed to Connected from Connecting. If not, then status will be changed to Disconnected and user will be sent to previous BLE device list screen.

After connecting to the selected device, all the characteristics will be listed down there. There will be read, write button for reading and writing data to the characteristic. Read and write will be visible according to the characteristic's readable and writable capabilities. Upon read button clicking, the value will be displayed in a popup dialog along with data transfer speed. Upon write button clicking, a dialog will be shown where user can input simple string value. Then user needs to click send button to write input data to the characteristic.


Challenges:

1. Acquiring brief knowledge about BLE devices, it's services, characteristics and their properties before starting work.
2. Scanning devices continuously and change UI regarding RSSI strength.
3. Data parsing after reading value from a specific characteristics.
4. Permission related issues in different android OS.
