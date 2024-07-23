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

![Screenshot 2024-07-23 at 9 38 59 AM](https://github.com/user-attachments/assets/0a58ef35-69d4-4b8c-9876-df74e393d04f)


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
