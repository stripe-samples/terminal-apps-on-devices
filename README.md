# Apps on Devices example

This repository will contain example app(s) for Terminal's custom 3rd party apps on devices feature.

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

Run the example app on a Stripe WisePOS E or S700 DevKit device.

<img width="435" alt="Screenshot 2023-06-13 at 11 00 32 AM" src="https://github.com/stripe/terminal-apps-on-devices-example/assets/45020849/9c0472f4-2355-4094-a2fd-ce3498409d07">

