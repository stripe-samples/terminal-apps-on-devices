package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentCheckoutBinding
import com.stripe.aod.sampleapp.model.PaymentIntentCreationResponse
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.aod.sampleapp.utils.*
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.callable.PaymentMethodCallback
import com.stripe.stripeterminal.external.models.CollectConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.PaymentMethod
import com.stripe.stripeterminal.external.models.TerminalException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {
	companion object {
		const val TAG = "com.stripe.aod.sampleapp.fragment.CheckoutFragment"

		private const val FORMAT_AMOUNT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.format_amount"
		private const val AMOUNT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.amount"
		private const val CURRENCY = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.currency"
		private const val REQUEST_PAYMENT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.request_payment"
		private const val READ_REUSABLE_CARD = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.read_reusable_card"
		private const val SKIP_TIPPING = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.skip_tipping"
		private const val EXTENDED_AUTH = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.extended_auth"
		private const val INCREMENTAL_AUTH = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.incremental_auth"

		fun requestPayment(
			formatAmount: String,
			amount: Long,
			currency: String,
			skipTipping: Boolean,
			extendedAuth: Boolean,
			incrementalAuth: Boolean
		): CheckoutFragment {
			val fragment = CheckoutFragment()

			val bundle = Bundle()
			bundle.putString(FORMAT_AMOUNT,formatAmount)
			bundle.putLong(AMOUNT, amount)
			bundle.putString(CURRENCY, currency)
			bundle.putBoolean(REQUEST_PAYMENT, true)
			bundle.putBoolean(READ_REUSABLE_CARD, false)
			bundle.putBoolean(SKIP_TIPPING, skipTipping)
			bundle.putBoolean(EXTENDED_AUTH, extendedAuth)
			bundle.putBoolean(INCREMENTAL_AUTH, incrementalAuth)

			fragment.arguments = bundle
			return fragment
		}
	}

	private var collectTask: Cancelable? = null
	private var paymentIntent: PaymentIntent? = null
	private var _viewBinding : FragmentCheckoutBinding? = null
	private val viewBinding get() = _viewBinding!!

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initView(view)
		//hand back press action
		requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity, object: OnBackPressedCallback(true){
			override fun handleOnBackPressed() {
				activity?.backToPrevious()
			}
		})
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_viewBinding = null
	}

	private fun initView(view: View) {
		//get viewBinding instance
		_viewBinding = FragmentCheckoutBinding.bind(view)

		viewBinding.rlBack.setOnClickListener {
			activity?.backToPrevious()
		}

		viewBinding.tvSubmit.setOnClickListener {
			viewBinding.tvSubmit.isEnabled = false
			arguments?.let {
				handlerCreatePaymentIntent(it)
			}
		}

		arguments?.let {
			viewBinding.tvAmount.text = it.getString(FORMAT_AMOUNT)
			viewBinding.rlItem.tvDescription.text = it.getString(FORMAT_AMOUNT)
		}
	}

	private val processPaymentCallback = object : PaymentIntentCallback {
		override fun onSuccess(paymentIntent: PaymentIntent) {
			Log.d(Config.TAG, "processPaymentCallback onSuccess ")
			handleCapturePaymentIntent(paymentIntent)
		}

		override fun onFailure(e: TerminalException) {
			Log.d(Config.TAG, "processPaymentCallback onFailure ")
			MainScope().launch {
				activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
			}
		}
	}

	private val collectPaymentMethodCallback = object : PaymentIntentCallback {
		override fun onSuccess(paymentIntent: PaymentIntent) {
			Log.d(Config.TAG, "collectPaymentMethodCallback onSuccess ")
			handleProcessPayment(paymentIntent)
		}

		override fun onFailure(e: TerminalException) {
			Log.d(Config.TAG, "collectPaymentMethodCallback onFailure ")
		}
	}

	private val createPaymentIntentCallback = object: PaymentIntentCallback {
		override fun onSuccess(paymentIntent: PaymentIntent) {
			Log.d(Config.TAG, "createPaymentIntentCallback onSuccess ")
			handleCollectPaymentIntent(paymentIntent)
		}

		override fun onFailure(e: TerminalException) {
			Log.d(Config.TAG, "createPaymentIntentCallback onFailure ")
		}
	}

	private val reusablePaymentMethodCallback = object: PaymentMethodCallback {
		override fun onSuccess(paymentMethod: PaymentMethod) {
		}

		override fun onFailure(e: TerminalException) {
		}
	}

	/**
	 *  Create Payment Intent
	 */
	private fun handlerCreatePaymentIntent(bundle: Bundle) {
		try {
			ApiClient.createPaymentIntent(
				bundle.getLong(AMOUNT),
				bundle.getString(CURRENCY)?.lowercase(Locale.ENGLISH) ?: "usd",
				bundle.getBoolean(EXTENDED_AUTH),
				bundle.getBoolean(INCREMENTAL_AUTH),
				object : Callback<PaymentIntentCreationResponse> {
					override fun onResponse(
						call: Call<PaymentIntentCreationResponse>,
						response: Response<PaymentIntentCreationResponse>
					) {
						if (response.isSuccessful && response.body() != null) {
							response.body()?.let {
								handleRetrievePaymentIntent(it.secret)
							}
						} else {
							toast(resources.getString(R.string.payment_intent_create_fail))

							MainScope().launch  {
								activity?.clearBackStack()
								activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
							}
						}
					}

					override fun onFailure(
						call: Call<PaymentIntentCreationResponse>,
						t: Throwable
					) {
						toast(resources.getString(R.string.payment_intent_create_fail))

						MainScope().launch  {
							activity?.clearBackStack()
							activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
						}
					}
				}
			)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	/**
	 *  Retrieve Payment Intent
	 */
	private fun handleRetrievePaymentIntent(clientSecret: String) {
		try {
			Terminal.getInstance().retrievePaymentIntent(
				clientSecret,
				createPaymentIntentCallback)
		} catch (e: Exception) {
			toast(resources.getString(R.string.payment_intent_retrieve_fail))
		}
	}

	/**
	 * Collect Payment Intent
	 */
	private fun handleCollectPaymentIntent(paymentIntent: PaymentIntent) {
		try {
			val collectConfig = CollectConfiguration.Builder()
				.skipTipping(false)
				.build()
			this@CheckoutFragment.paymentIntent = paymentIntent
			collectTask = Terminal.getInstance().collectPaymentMethod(
				paymentIntent, collectPaymentMethodCallback, collectConfig
			)
		} catch (e: Exception) {
			toast(resources.getString(R.string.payment_intent_collect_fail))
		}
	}

	/**
	 *  Process Payment Intent
	 */
	private fun handleProcessPayment(paymentIntent: PaymentIntent) {
		try {
			Terminal.getInstance().processPayment(paymentIntent, processPaymentCallback)
		} catch (e: Exception) {
			toast(resources.getString(R.string.payment_intent_process_fail))
		}
	}

	/**
	 *  Capture Payment Intent
	 */
	private fun handleCapturePaymentIntent(paymentIntent: PaymentIntent) {
		try {
			ApiClient.capturePaymentIntent(paymentIntent.id, object: Callback<Void>{
				override fun onResponse(call: Call<Void>, response: Response<Void>) {
					MainScope().launch  {
						activity?.navigateToTarget(ReceiptFragment.TAG,
							ReceiptFragment.requestPayment(
								viewBinding.tvAmount.text.toString()
							), replace = true, addToBackStack = true
						)
					}
				}

				override fun onFailure(call: Call<Void>, t: Throwable) {
					MainScope().launch  {
						activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
					}
				}
			} )
		} catch (e: Exception) {
			toast(resources.getString(R.string.payment_intent_capture_fail))
		}
	}
}
