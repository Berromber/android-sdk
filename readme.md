TwinPush SDK Library
==================

Native Android SDK for TwinPush platform.

For detailed information see TwinPush [oficial documentation](http://developers.twinpush.com/quickstart?platform=android).

## Register For Google Cloud Messaging

TwinPush uses Google Cloud Messaging to deliver Push Notifications to Android devices.

To use this service, it is necessary to access to the [Google Developers Console](https://console.developers.google.com) and perform the following steps:

 1. Create a Project (or select an existing one)
 2. Enable Google Cloud Messaging service
 3. Create a Public API access Key for Server in the Credentials section
 
Write down the **Public API Key** and the **Project Number** (GCM sender ID) as it will be used later.
 
You can create and manage projects in the [Google Developers Console](https://console.developers.google.com). For instructions, see the [Developers Console documentation](https://developers.google.com/console/help/new/).

## Register your application in TwinPush

The next step is to setup the TwinPush application. This can be done through the [TwinPush console](https://app.twinpush.com):

1. Access to TwinPush website and login with your account
2. From the control panel of your application, select Application Settings
3. Open _Google Cloud Messaging (GCM)_ section
4. Enter the Server API Key obtained during Google Cloud Messaging registration
5. Enter the Android Application package

![](http://developers.twinpush.com/assets/android_apikey-7d67473c7ca735ac5ff674dbcc7841bf.png)

## Building the application

### Gradle Dependency
Use this dependency in your build.gradle file to reference this library in your project

```groovy
compile 'com.twincoders.twinpush:android-sdk:1.1.0'
```

### Non-Gradle Library import

You have to add the following libraries to the project by dragging them to the libs folder:

* [twinpush-sdk.jar](https://github.com/TwinPush/android-sdk/raw/master/sdk/bin/twinpush-sdk.jar) - TwinPush Library. Provides native access to the TwinPush API and includes convenience methods.
* [gcm.jar](http://code.google.com/p/gcm/source/browse/gcm-client/dist/?r=af0f427f11ec05c252d8424fffb9ff5521b59495) - Google Cloud Messaging Library. Required for device registration in GCM and receiving notifications.
* android-support-v4 - Android Compatibility Library. Allows the use of methods and classes of an API Level on devices with lower versions.

The libraries must be checked to be exported with the project.

### Configuring Android manifest

To use GCM is needed Android 2.2 or higher. Therefore, the application must have a minimum API Level of 8:

```xml
<uses-sdk android:minSdkVersion="8" android:targetSdkVersion="xx"/>
```
    
Add the following permissions:

```xml
<!-- [START twinpush_permission] -->
<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" />
<!-- GCM connects to Internet Services. -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- Keeps the processor from sleeping when a message is received. -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
<!-- Permission to vibrate -->
<uses-permission android:name="android.permission.VIBRATE" />
<!-- GCM requires a Google account (for Android version lower than 4.0.4) -->
<uses-permission android:name="android.permission.GET_ACCOUNTS" />
<!-- Custom permission so only this application can receive GCM messages 
(not required for applications targeted to minSdkVersion 16 and higher) -->
<permission android:name="my_app_package.permission.C2D_MESSAGE" android:protectionLevel="signature" />
<uses-permission android:name="my_app_package.permission.C2D_MESSAGE" />
<!-- [END twinpush_permission] -->
```

Inside the _application_ node include the GCM receiver:

```xml
<!-- [START gcm_receiver] -->
<receiver
    android:name="com.google.android.gms.gcm.GcmReceiver"
    android:exported="true"
    android:permission="com.google.android.c2dm.permission.SEND">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    
        <category android:name="my_app_package" />
    </intent-filter>
</receiver>
<!-- [END gcm_receiver] -->
```
And include also the TwinPush services to register device and receive notifications:

```xml
<!-- [START twinpush_services] -->
<service
    android:name="com.twincoders.twinpush.sdk.services.NotificationIntentService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</service>
    
<service
    android:name="com.twincoders.twinpush.sdk.services.RegistrationIntentService"
    android:exported="false">
</service>
<!-- [END twinpush_services] -->
```
    
Where `my_app_package` must be replaced by the package name of your application.

### Starting TwinPush SDK

To Setup TwinPush SDK you will need the following information:

* **TwinPush App ID**: Application ID obtained from Settings section of TwinPush platform
* **TwinPush API Key**: TwinPush Application API Key displayed in Settings section
* **Google Project Number**: Project number (formerly Sender ID) obtained in the Google APIs Console
* **Subdomain**: Server subdomain where the application is deployed. Can be obtained in the Settings section of the TwinPush platform.
* **Notification icon**: An image resource that will be displayed on action bar when a Push notification is received
  
![](http://developers.twinpush.com/assets/android_icon-2f9119a5e58e5ac4854d3f637699c18b.png)

To initialize the SDK you will ussually override the `onCreate` method of main activity and call `setup` method from the TwinPush SDK, that accepts a `TwinPushOptions` object as  parameter that will hold the required information.

```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Setup TwinPush SDK
    TwinPushOptions options = new TwinPushOptions();                // Initialize options
    options.twinPushAppId =     "7687xxxxxxxxxxxx";                 // - APP ID
    options.twinPushApiKey =    "c5caxxxxxxxxxxxxxxxxxxxxxxxx1592"; // - API Key
    options.gcmProjectNumber =  "8xxxxxxxxxxx";                     // - GCM Project Number
    options.subdomain =         "mycompany";                        // - Application subdomain
    options.notificationIcon =  R.drawable.ic_notification;         // - Notification icon
    TwinPushSDK.getInstance(this).setup(options);                   // Call setup
    /* Your code goes here... */
}
```

The `setup` method will return **false** if any of the required parameter is missing.

As seen in the previous example, to access to the shared instance of TwinPush SDK, it is possible to invoque `TwinPushSDK.getInstance` class method that takes the context as parameter.


## Basic TwinPush integration

### Registering device

Once setup the TwinPush SDK, the device must register to receive notifications. This is made through the `register` method of the `TwinPushSDK` object.

In the following sample code you can see different ways to register the device.

```java
// Obtain TwinPushSDK instance
TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
// Register without alias
twinPush.register();
// Register with alias
twinPush.register("email@company.com");
// Register with alias and listener
twinPush.register("email@company.com", new TwinPushSDK.OnRegistrationListener() {
    @Override
    public void onRegistrationSuccess(String currentAlias) {
        // Registration Successful!
    }
    
    @Override
    public void onRegistrationError(Exception exception) {
        // Error during registration
    }
});
```

###Receiving notifications

When your application receives a Push notification, it will be shown in the status bar. If the user interacts with the notification, it will send an Intent to the main activity of your application with the information received.

This intent contains the following data:

* Action: `NotificationIntentService.ON_NOTIFICATION_RECEIVED_ACTION`
* Extras:
  * `NotificationIntentService.EXTRA_NOTIFICATION`: Serialized object of class PushNotification that contains the information of the received notification.

To obtain the information from this Intent, depending on the execution mode and the current status of the activity, you should take care of the following methods:

* `onCreate`: When the activity is not running, `onCreate` method will be called. To access to the Intent, use the activity `getIntent()` method.
* `onNewIntent`: This method will be called when the application is already running. The new intent will be set as parameter.

####Example

As an example, the following code shows an activity with a WebView when it receives a rich notification (containing HTML).

First, we include calls to `checkPushNotification` in the entry points previously mentioned.

```java
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Your initialization code goes here
    // (...)
    // Check push notification
    checkPushNotification(getIntent());
}

@Override
protected void onNewIntent(Intent intent) {
    checkPushNotification(intent);
    super.onNewIntent(intent);
}
```

Later, the `checkPushNotification` method is implemented, which examines the Intent received to see if it is a push notification and display `RichNotificationActivity` activity (or custom) in the case of a rich notification.

```java
// Checks if the intent contains a Push notification and displays rich content when appropriated
void checkPushNotification(Intent intent) {
    if (intent != null && intent.getAction() != null && intent.getAction().equals(NotificationIntentService.ON_NOTIFICATION_OPENED_ACTION)) {
        PushNotification notification = (PushNotification) intent.getSerializableExtra(    NotificationIntentService.EXTRA_NOTIFICATION);
        TwinPushSDK.getInstance(this).onNotificationOpen(notification);
    
        if (notification != null && notification.isRichNotification()) {
            Intent richIntent = new Intent(this, RichNotificationActivity.class);
            richIntent.putExtra(    NotificationIntentService.EXTRA_NOTIFICATION, notification);
            startActivity(richIntent);
        }
    }
}
```
Remember to declare in the Manifest file the activity that you will use to display rich content notifications.

In case of the default Rich Activity:

```xml
<activity
    android:name="com.twincoders.twinpush.sdk.activities.RichNotificationActivity"
    android:theme="@style/AppTheme">
</activity>
```

###Displaying notifications inbox

Through TwinPush SDK you can obtain all the rich notifications received by the device in order to display a messages inbox.

This requires performing a method call getNotifications of TwinPushSDK:

```java
TwinPushSDK.getInstance(this).getNotifications(currentPage, maxPages,new GetNotificationsRequest.Listener() {
    @Override
    public void onError(Exception exception) {
        // Error occurred on request
    }

    @Override
    public void onSuccess(List<PushNotification> notifications, int totalPages) {
        // Request successful
    }
});
```

To make it easier to display the results in a list, there is an abstract implementation for an Adapter that can be extended.

In the following code sample we configure an Adapter to be used in an activity to display notifications on a ListView. You can insert this code in the onCreate method of your activity:

```java
/* Setup list adapter */
NotifListAdapter adapter = new NotifListAdapter(this) {
    @Override
    public NotificationListItemView getViewInstance(Context context) {
        NotificationListItemView view = new MyCustomView(context);
        // Your class MyCustomView should implement NotificationListItemView interface
        return view;
    }
};

adapter.setListener(new NotifListAdapter.Listener() {
    @Override
    public void onNotificationSelected(PushNotification notification) {
        // Notification selected
    }
});
```

The Demo Application contains an Inbox Activity that uses the new method of implementing lists through [ReciclerView](http://developer.android.com/intl/es/reference/android/support/v7/widget/RecyclerView.html).

###Sending user information

Through TwinPush SDK it is possible to send information of the application users.

To do this, you have to make a call to `setProperty` method of `TwinPushSDK`.

```java
TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
twinPush.setProperty("age", getAge());
twinPush.setProperty("gender", getGender());
```

This method takes to parameters:

* Name to be assigned to this property, which will be used to identify it in the statistics
* Value to be assigned to the device. If sending null, it will delete previously submitted information for this attribute.

The system automatically recognizes the type of data to be sent.

You can also delete all information sent by a device performing a call to `clearProperties`:

```java
TwinPushSDK.getInstance(this).clearProperties();
```

###Sending usage statistics

Using TwinPush is possible to record the how long a device uses the application, as well as the number of times it is opened. To do this, you just have to add a call to `activityStart` and `activityStop` methods of TwinPush SDK in onStart and onStop methods of application activities.

It is recommended to export common functionality to an abstract parent activity that will be extended by the rest of application activities.

```java
@Override
protected void onStart() {
    TwinPushSDK.getInstance(this).activityStart(this);
    super.onStart();
};

@Override
protected void onStop() {
    TwinPushSDK.getInstance(this).activityStop(this);
    super.onStop();
}
```

Through these calls TwinPush can determine the periods of user activity with the application.

###Sending location

There are two ways to notify the user location to TwinPush:

* **Automatically**: you only have to define the type of monitoring, and the SDK automatically sends the changes of the user's position, even when the application is closed.
* **Explicitly**: the user location is sent through a manual call every time you want to update it.

To access the location using either of the two methods, it is necessary to include the following in the manifest node of the AndroidManifest.xml file of the application:

```xml
<!-- Permission to access to GPS Location -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```

####Automatic sending of location

TwinPush automatically sends the position using a service that is running in the background and is notified of changes in the user's location.

This service does not perform any consulting to location services, but feeds on the changes reported by other sources (also known as passive provider), so battery consumption is not affected.

For the configuration of the services, you must include the following lines in the _AndroidManifest.xml_ file:

Inside the manifest node, the following pemission:

```xml
<!-- Permission to start service on Boot completed -->
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

Inside application node:

```xml
<!-- Passive Location tracking service -->
<service android:name="com.twincoders.twinpush.sdk.services.LocationService"/>
<!-- Restart location tracking service when the device is rebooted -->
<receiver android:name="com.twincoders.twinpush.sdk.services.BootReceiver" android:exported="false">
    <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED"/>
    </intent-filter>
</receiver>
```

Once set up the service, you just have to include a call to the SDK `startMonitoringLocationChanges` method to start tracking:

```java
TwinPushSDK.getInstance(this).startMonitoringLocationChanges();
```

Through this call, you begin passive monitoring the user's location, even when the app is closed or is in the background.

To stop monitoring the location, just do a call to `stopMonitoringLocationChanges` method.

```java
TwinPushSDK.getInstance(this).stopMonitoringLocationChanges();
```

####Explicitly sending the location

To explicitly update the user's location you can make a call to any of the following methods:

* `setLocation(double latitude, double longitude)`: it sends the user coordinates
* `updateLocation(LocationPrecision precision)`: obtains and sends the current location of the user based on the stated accuracy. This level of accuracy will determine the origin, time to obtain and the location accuracy collected, which will result in battery consumption.

Examples of both use cases:

```java
TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
// Send coordinates
twinPush.setLocation(40.383, -3.717);
// Update location
twinPush.updateLocation(LocationPrecision.HIGH);
```

## Customize behavior

### Custom notification layouts

As described in the [official documentation](http://developer.android.com/design/patterns/notifications.html), Android offers a variety of ways to display notifications to the user.

![](http://developer.android.com/design/media/notifications_pattern_expandable.png)
> _Example of default Android expanded and contracted layouts (source: [Android Developers](http://developer.android.com/))_

By default, TwinPush will display the notification message in both contracted and expanded layouts, and will show the application icon for the notifications. By overriding the default TwinPush behavior, you can stack notifications, change the icon displayed on each and broadly, improve and customize the way in which messages are displayed to the user.

It is possible to modify the way in which notifications are shown through TwinPush by following the steps below:

* Create a class that extends [NotificationIntentService](https://github.com/TwinPush/android-sdk/blob/master/sdk/src/com/twincoders/twinpush/sdk/services/NotificationIntentService.java) and override the `displayNotification` method to display the notification in the desired way:

```java
public class MyIntentService extends NotificationIntentService {
    @Override
    protected void displayNotification(Context context, PushNotification notification) {
        // Use your custom layout to display notification
    }
}
```

* Replace the `NotificationIntentService` declaration with your own implementation in the Manifest file:

```xml
<service
    android:name="my_app_package.services.MyIntentService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.android.c2dm.intent.RECEIVE" />
    </intent-filter>
</service>
```

### Custom Domain

For Enterprise solutions, TwinPush offers the possibility of deploying the platform in a dedicated server. To address the requests made from the application to this new server, it is needed to specify its custom URL or domain

It is very important to specify the custom host or subdomain before any interaction with the TwinPush SDK

To do so, the TwinPush SDK for Android counts with two methods:

#### Set subdomain

Using the `setSubdomain` method of the SDK you can set custom subdomain inside twinpush.com domain.

```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
    /* Set subdomain to https://mycompany.twinpush.com before TwinPush setup*/
    twinPush.setSubdomain("mycompany");
    /* TwinPush setup */
    twinPush.setup(TWINPUSH_APP_ID, TWINPUSH_TOKEN, GOOGLE_PROJECT_NUMBER);
    /* ... */
}
```
#### Set server host

Using the setServerHost method of the TwinPushSDK you can set a full custom host for the API server.

```java
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TwinPushSDK twinPush = TwinPushSDK.getInstance(this);
    /* Set host to https://push.mycompany.com/api before TwinPush setup*/
    twinPush.setServerHost("https://push.mycompany.com/api");
    /* TwinPush setup */
    twinPush.setup(TWINPUSH_APP_ID, TWINPUSH_TOKEN, GOOGLE_PROJECT_NUMBER);
    /* ... */
}
```

## License

    The MIT License (MIT)
    
    Copyright (c) 2015 TwinCoders S.L.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.