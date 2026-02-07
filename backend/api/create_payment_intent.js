const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

module.exports = async (req, res) => {
  if (req.method !== "POST") {
    return res.status(405).json({ error: "Method not allowed" });
  }

  try {
    const { amount, currency, description, line_items } = req.body;

    const params = {
      amount: parseInt(amount, 10),
      currency: currency || "usd",
      payment_method_types: ["card_present"],
      capture_method: "manual",
    };

    if (description) params.description = description;

    if (line_items) {
      params.metadata = { line_items };
    }

    if (req.body["payment_method_options[card_present[request_extended_authorization]]"]) {
      params.payment_method_options = {
        card_present: {
          request_extended_authorization:
            req.body["payment_method_options[card_present[request_extended_authorization]]"] === "true",
          request_incremental_authorization_support:
            req.body["payment_method_options[card_present[request_incremental_authorization_support]]"] === "true",
        },
      };
    }

    const intent = await stripe.paymentIntents.create(params);

    res.json({
      paymentIntentId: intent.id,
      secret: intent.client_secret,
    });
  } catch (err) {
    console.error("Error creating PaymentIntent:", err.message);
    res.status(500).json({ error: err.message });
  }
};
