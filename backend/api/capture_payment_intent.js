const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

module.exports = async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).json({ error: "Method not allowed" });
  }

  try {
    const { payment_intent_id } = req.body;
    const intent = await stripe.paymentIntents.capture(payment_intent_id);

    res.json({
      paymentIntentId: intent.id,
      secret: intent.client_secret,
    });
  } catch (err) {
    console.error("Error capturing PaymentIntent:", err.message);
    res.status(500).json({ error: err.message });
  }
};
