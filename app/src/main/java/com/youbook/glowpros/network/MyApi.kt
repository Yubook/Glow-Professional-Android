package com.youbook.glowpros.network

import com.youbook.glowpros.ui.barber_terms_policy.BarberProfileResponse
import com.youbook.glowpros.ui.booking_details.OrderReviewResponse
import com.youbook.glowpros.ui.fragment.booking_list.BarberBookingResponse
import com.youbook.glowpros.ui.fragment.booking_list.CancelReasonListResponse
import com.youbook.glowpros.ui.fragment.dashboard.HomeOfferResponse
import com.youbook.glowpros.ui.insight.GraphResponseData
import com.youbook.glowpros.ui.insight.MostBookedServicesResponse
import com.youbook.glowpros.ui.login.CountryCodeResponseModel
import com.youbook.glowpros.ui.notification.NotificationListResponse
import com.youbook.glowpros.ui.payment_history.BarberPaymentHistoryResponse
import com.youbook.glowpros.ui.profile.CityResponse
import com.youbook.glowpros.ui.profile.ProfileResponseNew
import com.youbook.glowpros.ui.profile.StateResponse
import com.youbook.glowpros.ui.review.ReviewResponse
import com.youbook.glowpros.ui.select_services.ServiceResponseModel
import com.youbook.glowpros.ui.show_case.BarberPortfolioResponse
import com.youbook.glowpros.ui.terms_privacy.TermsPrivacyResponse
import com.youbook.glowpros.ui.your_availability.TimeSlotsResponse
import com.youbook.glowpros.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

interface MyApi {

    @FormUrlEncoded
    @POST("addDeviceIdAndToken")
    suspend fun getOffer(
        @FieldMap params: Map<String, String>
    ): HomeOfferResponse

    @FormUrlEncoded
    @POST("cancelOrder")
    suspend fun orderCancelByDriver(
        @FieldMap params: Map<String, String>
    ): CommonResponse

    @FormUrlEncoded
    @POST("orderComplete")
    suspend fun orderComplete(
        @FieldMap params: Map<String, String>
    ): CommonResponse

    @FormUrlEncoded
    @POST("driverSelectRadius")
    suspend fun addDriverRadius(
        @FieldMap params: Map<String, String>
    ): CommonResponse

    @FormUrlEncoded
    @POST("addBarberService")
    suspend fun addBarberService(
        @Field("service_id[]") serviceIdList : List<Int>,
        @Field("price[]") priceList : List<Double>,
    ) : CommonResponse


    @FormUrlEncoded
    @POST("barberOnOff")
    suspend fun barberOnOff(
        @Field("status") status : Int,
    ) : CommonResponse

    @FormUrlEncoded
    @POST("updateTermsPolicy")
    suspend fun updateTermsPolicy(
        @FieldMap params: Map<String, String>
    ) : CommonResponse

    @GET("getReview")
    suspend fun getReview(): ReviewResponse

    @GET("getStates")
    suspend fun getStates(): StateResponse

    @GET("getCities")
    suspend fun getCities(): CityResponse

    @GET("getBarberService")
    suspend fun getBarberService(): ServiceResponseModel

    @GET("revenueFilterMap")
    suspend fun getRevenueData(
        @Query("barber_id") driver_id: String?,
        @Query("search") search: String?
    ): GraphResponseData

    @GET("revenueFilterMap")
    suspend fun getRevenueDataWithoutSearch(
        @Query("barber_id") driver_id: String?
    ): GraphResponseData


    @GET("getDriverProfile")
    suspend fun getDriverProfile(
        @Query("barber_id") driver_id: String?
    ): BarberProfileResponse

    @GET("getOrderReview")
    suspend fun getOrderReview(
        @Query("order_id") order_id: String?,
        @Query("from_id") from_id: String?,
        @Query("to_id") to_id: String?
    ): OrderReviewResponse

    @GET("getPortfolio")
    suspend fun getBarberPortfolio() : BarberPortfolioResponse

    @FormUrlEncoded
    @POST("removePortfolio")
    suspend fun removePortfolio(
        @Field("portfolio_id") portfolioId : String,
    ): CommonResponse

    @GET("getServices")
    suspend fun getService(): ServiceResponseModel

    @GET("getCities")
    suspend fun getCities(@Query("country_id") countryId: String?): CityResponse

    @GET("getTimes")
    suspend fun getTimesSlots(): TimeSlotsResponse

    @GET("getCountries")
    suspend fun getCountryCode(): CountryCodeResponseModel

    @Multipart
    @POST("addPortfolioes")
    suspend fun addPortfolioImage(
        @Part files: List<MultipartBody.Part>,
    ): BarberPortfolioResponse

    @FormUrlEncoded
    @POST("addBarberTime")
    suspend fun addBarberTime(
        @Field("date[]") dateList : ArrayList<String>,
        @Field("time_id[]") slotTimeList : ArrayList<String>
    ): CommonResponse

    @FormUrlEncoded
    @POST("deleteBarberService")
    suspend fun removeBarberService(
        @Field("id") service_id : String,
    ): CommonResponse

    @GET("totalRevenueOfBarber")
    suspend fun getMostBookedServices(
    ): MostBookedServicesResponse

    @GET("getNotification")
    suspend fun getNotification(): NotificationListResponse

    @GET("read_notification")
    suspend fun readNotification(): CommonResponse

    @POST("logout")
    suspend fun logout(): CommonResponse

    @GET("getBarberBookings")
    suspend fun getDriverOrders(
    ): BarberBookingResponse

    @GET("cancelReasons")
    suspend fun getCancelReasons(
    ): CancelReasonListResponse

    @GET("getBarberPaymentHistory")
    suspend fun getPaymentHistory(
    ): BarberPaymentHistoryResponse

    @GET("getTermsPolicy")
    suspend fun getTermsPolicy(
        @Query("for") forUser: String
    ): TermsPrivacyResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("mobile") mobile: String,
        @Field("phone_code") phone_code: String
    ): ProfileResponseNew

    @Multipart
    @POST("addBarberProfile")
    suspend fun addProfile(
        @Part files: List<MultipartBody.Part>,
        @QueryMap params: Map<String, String>
    ): ProfileResponseNew

    @Multipart
    @POST("updateBarberProfile")
    suspend fun updateProfile(
        @Part files: List<MultipartBody.Part>,
        @QueryMap params: Map<String, String>
    ): ProfileResponseNew

    @FormUrlEncoded
    @POST("updateBarberProfile")
    suspend fun updateProfileWithoutPhoto(
        @FieldMap params: Map<String, String>
    ): ProfileResponseNew


    /*@GET("createPaymentToken")
    suspend fun getPaymentToken(): PaymentKeyResponse

    @GET("getService")
    suspend fun getService(): ServicesResponse*/

    companion object {

        var myApi: MyApi? = null


        fun getInstanceToken(authToken: String): MyApi {
            val client = OkHttpClient.Builder().apply {
                addInterceptor(AuthenticationInterceptor(authToken!!))
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }.build()
// na
            val gson = GsonBuilder()
                .setLenient()
                .create()


            val retrofit = Retrofit.Builder()
            retrofit.baseUrl(Constants.BASE_URL)
            retrofit.client(client)
            retrofit.addConverterFactory(GsonConverterFactory.create(gson))
            retrofit.addConverterFactory(ScalarsConverterFactory.create())
            myApi = retrofit.build().create(MyApi::class.java)

            return myApi!!
        }

        fun getInstance(): MyApi {

            val client = OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }.build()

            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (myApi == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()
                myApi = retrofit.create(MyApi::class.java)
            }
            return myApi!!
        }
    }

}