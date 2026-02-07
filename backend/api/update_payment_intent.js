const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

module.exports = async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).json({ error: "Method not allowed" });
  }

  try {
    const { payment_intent_id, receipt_email } = req.body;
    const params = {};
    if (receipt_email) params.receipt_email = receipt_email;

    const intent = await stripe.paymentIntents.update(payment_intent_id, params);

    res.json({
      paymentIntentId: intent.id,
      secret: intent.client_secret,
    });
  } catch (err) {
    console.error("Error updating PaymentIntent:", err.message);
    res.status(500).json({ error: err.message });
  }
};
