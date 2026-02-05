require("dotenv").config();
const express = require("express");
const cors = require("cors");

const app = express();
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

// ─── Connection Token ────────────────────────────────────────────────────────
app.post("/connection_token", async (_req, res) => {
  try {
    const token = await stripe.terminal.connectionTokens.create();
    res.json({ secret: token.secret });
  } catch (err) {
    console.error("Error creating connection token:", err.message);
    res.status(500).json({ error: err.message });
  }
});

// ─── Products ────────────────────────────────────────────────────────────────
// Returns all active products that have metadata.terminal === "true",
// with their default price expanded.
app.get("/products", async (_req, res) => {
  try {
    const allProducts = [];
    let hasMore = true;
    let startingAfter = undefined;

    while (hasMore) {
      const params = {
        active: true,
        expand: ["data.default_price"],
        limit: 100,
      };
      if (startingAfter) params.starting_after = startingAfter;

      const page = await stripe.products.list(params);

      const terminalProducts = page.data.filter(
        (p) => p.metadata && p.metadata.terminal === "true"
      );
      allProducts.push(...terminalProducts);

      hasMore = page.has_more;
      if (page.data.length > 0) {
        startingAfter = page.data[page.data.length - 1].id;
      }
    }

    const products = allProducts.map((p) => ({
      id: p.id,
      name: p.name,
      description: p.description || "",
      images: p.images || [],
      unit_amount: p.default_price ? p.default_price.unit_amount : 0,
      currency: p.default_price ? p.default_price.currency : "usd",
      price_id: p.default_price ? p.default_price.id : null,
    }));

    res.json({ products });
  } catch (err) {
    console.error("Error fetching products:", err.message);
    res.status(500).json({ error: err.message });
  }
});

// ─── Create PaymentIntent ────────────────────────────────────────────────────
app.post("/create_payment_intent", async (req, res) => {
  try {
    const {
      amount,
      currency,
      description,
      line_items,
    } = req.body;

    const params = {
      amount: parseInt(amount, 10),
      currency: currency || "usd",
      payment_method_types: ["card_present"],
      capture_method: "manual",
    };

    if (description) params.description = description;

    // Store line items in metadata so we have a record
    if (line_items) {
      params.metadata = { line_items };
    }

    // Forward any card_present options
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
});

// ─── Update PaymentIntent ────────────────────────────────────────────────────
app.post("/update_payment_intent", async (req, res) => {
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
});

// ─── Capture PaymentIntent ───────────────────────────────────────────────────
app.post("/capture_payment_intent", async (req, res) => {
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
});

const PORT = process.env.PORT || 4242;
app.listen(PORT, "0.0.0.0", () => {
  console.log(`Server running on port ${PORT}`);
});
