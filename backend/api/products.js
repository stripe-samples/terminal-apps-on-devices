const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

module.exports = async (req, res) => {
  if (req.method !== "GET") {
    return res.status(405).json({ error: "Method not allowed" });
  }

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
};
