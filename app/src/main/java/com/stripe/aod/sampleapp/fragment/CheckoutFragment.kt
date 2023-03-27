package com.stripe.aod.sampleapp.fragment;

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.Config
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentCheckoutBinding
import com.stripe.aod.sampleapp.model.PaymentIntentCreationResponse
import com.stripe.aod.sampleapp.network.ApiClient
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.clearBackStack
import com.stripe.aod.sampleapp.utils.navigateToTarget
import com.stripe.aod.sampleapp.utils.replaceFragmentInActivity
import com.stripe.stripeterminal.Terminal
import com.stripe.stripeterminal.external.callable.Cancelable
import com.stripe.stripeterminal.external.callable.PaymentIntentCallback
import com.stripe.stripeterminal.external.callable.PaymentMethodCallback
import com.stripe.stripeterminal.external.models.CollectConfiguration
import com.stripe.stripeterminal.external.models.PaymentIntent
import com.stripe.stripeterminal.external.models.PaymentMethod
import com.stripe.stripeterminal.external.models.TerminalException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CheckoutFragment : Fragment(R.layout.fragment_checkout), View.OnClickListener {
	companion object {
		const val TAG = "com.stripe.aod.sampleapp.fragment.CheckoutFragment"

		private const val AMOUNT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.amount"
		private const val CURRENCY = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.currency"
		private const val REQUEST_PAYMENT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.request_payment"
		private const val READ_REUSABLE_CARD = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.read_reusable_card"
		private const val SKIP_TIPPING = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.skip_tipping"
		private const val EXTENDED_AUTH = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.extended_auth"
		private const val INCREMENTAL_AUTH = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.incremental_auth"

		fun requestPayment(
			amount: Long,
			currency: String,
			skipTipping: Boolean,
			extendedAuth: Boolean,
			incrementalAuth: Boolean
		): CheckoutFragment {
			val fragment = CheckoutFragment()

			val bundle = Bundle()
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

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_viewBinding = FragmentCheckoutBinding.inflate(inflater, container, false)
		return viewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initView()
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

	private fun initView() {
		viewBinding.rlBack.setOnClickListener(this)
		viewBinding.tvSubmit.setOnClickListener(this)

		val fragment = activity?.supportFragmentManager?.findFragmentByTag(InputFragment.TAG)
		if (fragment != null) {
			val view: TextView? = fragment.view?.findViewById(R.id.tv_amount)
			viewBinding.tvAmount.text = view?.text
			viewBinding.rlItem.tvDescription.text = view?.text
		}
	}

	override fun onClick(v: View) {
		var id = v.id
		if (id == R.id.rl_back) {
			activity?.backToPrevious()
		} else if (id == R.id.tv_submit) {
			viewBinding.tvSubmit.isEnabled = false
			try {
				arguments?.let {
					ApiClient.createPaymentIntent(
						it.getLong(AMOUNT),
						it.getString(CURRENCY)?.lowercase(Locale.ENGLISH) ?: "usd",
						it.getBoolean(EXTENDED_AUTH),
						it.getBoolean(INCREMENTAL_AUTH),
						object : Callback<PaymentIntentCreationResponse> {
							override fun onResponse(
								call: Call<PaymentIntentCreationResponse>,
								response: Response<PaymentIntentCreationResponse>
							) {
								if (response.isSuccessful && response.body() != null)
									Terminal.getInstance().retrievePaymentIntent(
										response.body()?.secret!!,
										createPaymentIntentCallback
									)
								else {
									Toast.makeText(
										activity,
										resources.getString(R.string.payment_intent_create_fail),
										Toast.LENGTH_LONG
									).show()

									Handler(Looper.getMainLooper()).post {
										activity?.clearBackStack()
										activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
									}
								}
							}

							override fun onFailure(
								call: Call<PaymentIntentCreationResponse>,
								t: Throwable
							) {
								Toast.makeText(
									activity,
									resources.getString(R.string.payment_intent_create_fail),
									Toast.LENGTH_LONG
								).show()

								Handler(Looper.getMainLooper()).post {
									activity?.clearBackStack()
									activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
								}
							}
						}
					)
				}
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	private val processPaymentCallback by lazy {
		object : PaymentIntentCallback {
			override fun onSuccess(paymentIntent: PaymentIntent) {
				Log.d(Config.TAG, "processPaymentCallback onSuccess ")

				try {
					ApiClient.capturePaymentIntent(paymentIntent.id, object: Callback<Void>{
						override fun onResponse(call: Call<Void>, response: Response<Void>) {
							Handler(Looper.getMainLooper()).post {
								activity?.navigateToTarget(ReceiptFragment.TAG,
									ReceiptFragment.requestPayment(
										viewBinding.tvAmount.text.toString()
									), true, true)
							}
						}

						override fun onFailure(call: Call<Void>, t: Throwable) {
							Handler(Looper.getMainLooper()).post {
//								activity?.clearBackStack()
								activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
							}
						}
					} )
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}

			override fun onFailure(e: TerminalException) {
				Log.d(Config.TAG, "processPaymentCallback onFailure ")

				Handler(Looper.getMainLooper()).post {
//					activity?.clearBackStack()
					activity?.replaceFragmentInActivity(HomeFragment(), R.id.container)
				}
			}
		}
	}

	private val cancelPaymentIntentCallback by lazy {
		object : PaymentIntentCallback {
			override fun onSuccess(paymentIntent: PaymentIntent) {
				Log.d(Config.TAG, "cancelPaymentIntentCallback onSuccess ")

			}

			override fun onFailure(e: TerminalException) {
			}
		}
	}

	private val collectPaymentMethodCallback by lazy {
		object : PaymentIntentCallback {
			override fun onSuccess(paymentIntent: PaymentIntent) {
				Log.d(Config.TAG, "collectPaymentMethodCallback onSuccess ")
				try {
					Terminal.getInstance().processPayment(paymentIntent, processPaymentCallback)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}

			override fun onFailure(e: TerminalException) {
			}
		}
	}

	private val createPaymentIntentCallback by lazy {
		object : PaymentIntentCallback {
			override fun onSuccess(paymentIntent: PaymentIntent) {
				Log.d(Config.TAG, "createPaymentIntentCallback onSuccess ")

				try {
					val collectConfig = CollectConfiguration.Builder()
						.skipTipping(false)
						.build()
					this@CheckoutFragment.paymentIntent = paymentIntent
					collectTask = Terminal.getInstance().collectPaymentMethod(
						paymentIntent, collectPaymentMethodCallback, collectConfig
					)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}

			override fun onFailure(e: TerminalException) {

			}
		}
	}

	private val reusablePaymentMethodCallback by lazy {
		object : PaymentMethodCallback {
			override fun onSuccess(paymentMethod: PaymentMethod) {
			}

			override fun onFailure(e: TerminalException) {
			}
		}
	}
}
