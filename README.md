# Apps on Devices integration

This repository contains a sample app that demonstrates best practices for writing an [Apps on Devices](https://stripe.com/docs/terminal/features/apps-on-devices/overview) integration.

The app demonstrates the following
- How to discover and connect the handoff reader
- How to collect and confirm a payment
- How to deep link to the device's admin settings

<img src="https://raw.githubusercontent.com/stripe-samples/terminal-apps-on-devices/master/demo.gif" width="400" height="711" />

## Prerequisites
Before proceeding with the integration, ensure you have the following
- Stripe S700 DevKit smart reader
- [Android Studio Flamingo](https://developer.android.com/studio/releases) or greater

## Setup

### Clone the repo

Clone this repo and open it in [Android Studio](https://developer.android.com/studio).

### Deploy the Example Terminal Backend
The Apps on Devices example app depends on the [Example Terminal Backend](https://github.com/stripe/example-terminal-backend).

We recommend deploying the backend to Render.com.

1. Set up a free [Render account](https://dashboard.render.com/register) if you haven't created one previously.
2. Click the button below to deploy the backend. You'll be prompted to enter a name for the Render service group as well as your Stripe API test mode secret key.
3. Go to the [next steps](#next-steps) in this README for how to use this app

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/stripe/example-terminal-backend/)

### Point the example app to your backend

Edit `local.properties` and add an entry for `BACKEND_URL`. For example if your instance is available at `https://my-backend-123.onrender.com`, you'll add the following.

```
BACKEND_URL=https://my-backend-123.onrender.com
```

## Run

Run the example app on a Stripe S700 DevKit smart reader.

![Screenshot 2024-07-23 at 9 38 59 AM](https://github.com/user-attachments/assets/0a58ef35-69d4-4b8c-9876-df74e393d04f)

## Building an APK

To generate an APK you can upload to the S700:

### Debug APK

From the project root, run:

```bash
./gradlew assembleDebug
```

The APK will be at:

```
app/build/outputs/apk/debug/app-debug.apk
```

> **Note:** Debug APKs have `android:debuggable` enabled and cannot be used with Stripe Terminal asset versions. Use a release APK instead.

### Release APK (required for Apps on Devices)

Stripe Terminal Apps on Devices requires an APK with `android:debuggable` disabled. The project includes a signing config so `assembleRelease` produces a signed APK out of the box.

1. Generate the release keystore (if it doesn't already exist in the project root):

```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release -storepass android -keypass android
```

2. Build the release APK:

```bash
./gradlew assembleRelease
```

The signed APK will be at:

```
app/build/outputs/apk/release/app-release.apk
```

### Using Android Studio

1. Open the project in Android Studio
2. Go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**
3. When the build finishes, click **locate** in the notification to find the APK

### Uploading to S700

Once you have the APK, follow the [Deploy the sample app](https://stripe.com/docs/terminal/features/apps-on-devices/deploy) guide to upload it via the Stripe Dashboard under **Terminal > Devices > Apps**.

## Next steps

- [Deploy the sample app](https://stripe.com/docs/terminal/features/apps-on-devices/deploy) to learn how to upload and deploy your app
- Read [troubleshooting apps on devices](https://stripe.com/docs/terminal/features/apps-on-devices/troubleshooting) for resolutions to common issues

## Get support
If you found a bug or want to suggest a new [feature/use case/sample], please [file an issue](../../issues).

If you have questions, comments, or need help with code, we're here to help:
- on [Discord](https://stripe.com/go/developer-chat)
- on Twitter at [@StripeDev](https://twitter.com/StripeDev)
- on Stack Overflow at the [stripe-payments](https://stackoverflow.com/tags/stripe-payments/info) tag
- by [email](mailto:support+github@stripe.com)

Sign up to [stay updated with developer news](https://go.stripe.global/dev-digest).

## Author(s)
- [ericlin-bbpos](https://github.com/ericlin-bbpos)
- [ianlin-bbpos](https://github.com/ianlin-bbpos)
- [mshafrir-stripe](https://github.com/mshafrir-stripe)
