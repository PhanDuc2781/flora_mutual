package com.cmd.flora.data.network

import com.cmd.flora.application.deviceToken
import com.cmd.flora.application.deviceUUID
import com.cmd.flora.data.network.response.FacilityInformation
import com.cmd.flora.data.network.response.Prefecture
import com.cmd.flora.data.network.base.APIRequest
import com.cmd.flora.data.network.base.ApiRouter
import com.cmd.flora.data.network.base.HTTPMethod
import com.cmd.flora.data.network.base.SVGFormatter
import com.cmd.flora.data.network.base.requestResult2
import com.cmd.flora.data.network.request.ContactInformationRequest
import com.cmd.flora.data.network.request.ContactInformationRequestModel
import com.cmd.flora.data.network.request.FacilityPageRequest
import com.cmd.flora.data.network.request.ForgotPassRequestModel
import com.cmd.flora.data.network.request.InformationPageRequest
import com.cmd.flora.data.network.request.LoginRequestModel
import com.cmd.flora.data.network.request.RegisterTokenRequest
import com.cmd.flora.data.network.request.ResetPasswordRequest
import com.cmd.flora.data.network.request.ServiceHistoryPageRequest
import com.cmd.flora.data.network.request.TelPageRequest
import com.cmd.flora.data.network.request.TogglePushNotiResponse
import com.cmd.flora.data.network.request.toRequest
import com.cmd.flora.data.network.response.CallResponse
import com.cmd.flora.data.network.response.ContactGenreResponse
import com.cmd.flora.data.network.response.ContactInformationResponseModel
import com.cmd.flora.data.network.response.ForgotResponseModel
import com.cmd.flora.data.network.response.HomeNotifyResponse
import com.cmd.flora.data.network.response.InformationResponse
import com.cmd.flora.data.network.response.LoginResponseModel
import com.cmd.flora.data.network.response.OrderHistoryResponse
import com.cmd.flora.data.network.response.PaginationResponseModel
import com.cmd.flora.data.network.response.PointResponse
import com.cmd.flora.data.network.response.RegisterTokenResponse
import com.cmd.flora.data.network.response.ResetPasswordResponse
import com.cmd.flora.data.network.response.UserInforResponseModel

suspend fun APIRequest.login(data: LoginRequestModel) = requestResult<LoginResponseModel>(
    ApiRouter(
        APIPath.login(),
        HTTPMethod.POST,
        decodable = data,
        needLogin = false,
        mockFilePath = "api_mock/LoginResponse.json"
    ), shouldCheckError = true
)

suspend fun APIRequest.refresh() = requestResult<LoginResponseModel>(
    ApiRouter(
        APIPath.refreshToken(),
        HTTPMethod.POST,
        needLogin = true,
        mockFilePath = "api_mock/LoginResponse.json"
    ), shouldCheckError = true
)

suspend fun APIRequest.registerToken(data: RegisterTokenRequest) =
    requestResult<RegisterTokenResponse>(
        ApiRouter(
            APIPath.registerToken(),
            HTTPMethod.POST,
            decodable = data,
            needLogin = true,
        ), shouldCheckError = true
    )

suspend fun APIRequest.forgotPass(data: ForgotPassRequestModel) =
    requestResult<ForgotResponseModel>(
        ApiRouter(
            APIPath.forgotPass(),
            HTTPMethod.POST,
            decodable = data,
            needLogin = false,
            mockFilePath = "api_mock/ForgotPassResponse.json"
        ), shouldCheckError = true
    )

suspend fun APIRequest.resetPassword(data: ResetPasswordRequest) =
    requestResult<ResetPasswordResponse>(
        ApiRouter(
            APIPath.resetPass(), HTTPMethod.POST, decodable = data
        )
    )


suspend fun APIRequest.getUserInfor() = requestResult<UserInforResponseModel>(
    ApiRouter(
        APIPath.me(), HTTPMethod.GET, needLogin = true, mockFilePath = "api_mock/MeResponse.json"
    ), shouldCheckError = false
)

suspend fun APIRequest.logout() = requestResult<Boolean>(
    ApiRouter(
        APIPath.logout(),
        HTTPMethod.POST,
        parameters = hashMapOf("device_uuid" to (deviceUUID() ?: "")),
        needLogin = true,
        mockFilePath = "api_mock/Logout.json"
    ), shouldCheckError = true
)

suspend fun APIRequest.withdraw() = requestResult<Boolean>(
    ApiRouter(
        APIPath.withdraw(), HTTPMethod.POST, needLogin = true, mockFilePath = "api_mock/Logout.json"
    ), shouldCheckError = true
)


suspend fun APIRequest.getToast() = requestResult<HomeNotifyResponse>(
    ApiRouter(
        APIPath.homeNotify(), HTTPMethod.GET, mockFilePath = "api_mock/ToastResponse.json"
    ),
)

suspend fun APIRequest.getServiceHistory(currentPage: ServiceHistoryPageRequest) =
    requestResult<PaginationResponseModel<OrderHistoryResponse>>(
        ApiRouter(
            APIPath.serviceHistory(),
            HTTPMethod.GET,
            decodable = currentPage,
            mockFilePath = "api_mock/ServiceHistoryResponse.json"
        )
    )

//suspend fun APIRequest.getListNewsLetter(currentPage: NewLetterPageRequest) =
//    requestResult<PaginationResponseModel<NewsLetter>>(
//        ApiRouter(
//            APIPath.newLetter(),
//            HTTPMethod.GET,
//            decodable = currentPage,
//            mockFilePath = when (currentPage.area) {
//                Area.GIFU -> "api_mock/NewsLetterGifuResponse.json"
//                else -> "api_mock/NewsLetterFukushimaResponse.json"
//            }
//        )
//    )

suspend fun APIRequest.getFacilityInformation(currentPage: FacilityPageRequest) =
    requestResult<PaginationResponseModel<FacilityInformation>>(
        ApiRouter(
            APIPath.facility(),
            HTTPMethod.GET,
            needLogin = false,
            decodable = currentPage,
            mockFilePath = when (currentPage.genre) {
                "wedding" -> "api_mock/WeddingFacilityResponse.json"
                "funeral" -> "api_mock/FuneralFacilityResponse.json"
                else -> "api_mock/ShopFacilityResponse.json"
            }
        )
    )

suspend fun APIRequest.getCall(currentPage: TelPageRequest) =
    requestResult<PaginationResponseModel<CallResponse>>(
        ApiRouter(
            APIPath.tel(),
            HTTPMethod.GET,
            decodable = currentPage,
            mockFilePath = when (currentPage.genre) {
                "wedding" -> "api_mock/WeddingCallResponse.json"
                "funeral" -> "api_mock/FuneralCallResponse.json"
                else -> "api_mock/BenefitCallResponse.json"
            }
        )
    )

suspend fun APIRequest.getInformationResponse(request: InformationPageRequest) =
    requestResult<PaginationResponseModel<InformationResponse>>(
        ApiRouter(
            APIPath.information(),
            HTTPMethod.GET,
            parameters = request.toRequest(),
            needLogin = true,
            mockFilePath = "api_mock/NewResponse.json"
        )
    )

suspend fun APIRequest.getDetailInformation(id: Int, shouldCheckError: Boolean = false) =
    requestResult<InformationResponse>(
        ApiRouter(
            APIPath.detailInformation(id.toString()),
            HTTPMethod.GET,
        ), shouldCheckError = shouldCheckError
    )

suspend fun APIRequest.getEventResponse(currentPage: InformationPageRequest) =
    requestResult<PaginationResponseModel<InformationResponse>>(
        ApiRouter(
            APIPath.event(),
            HTTPMethod.GET,
            decodable = currentPage,
            mockFilePath = "api_mock/NewResponse.json"
        )
    )


suspend fun APIRequest.getDataInquiry() =
    requestResult2<PaginationResponseModel<ContactGenreResponse>, PaginationResponseModel<Prefecture>>(
        ApiRouter(
            APIPath.contactGenre(), HTTPMethod.GET, mockFilePath = "api_mock/contactGenre.json"
        ), ApiRouter(
            APIPath.prefecture(), HTTPMethod.GET, mockFilePath = "api_mock/PrefectureResponse.json"
        )
    )

suspend fun APIRequest.getContactFacilityList(currentPage: ContactInformationRequest) =
    requestResult<PaginationResponseModel<ContactGenreResponse>>(
        ApiRouter(
            APIPath.contactFacilityList(), HTTPMethod.GET, decodable = currentPage
        )
    )

suspend fun APIRequest.sendContact(dataRequest: ContactInformationRequestModel) =
    requestResult<ContactInformationResponseModel>(
        ApiRouter(
            APIPath.sendContact(),
            HTTPMethod.POST,
            decodable = dataRequest,
            needLogin = false,
        )
    )


suspend fun APIRequest.togglePushNotification(isNotify: Boolean) = requestResult<Boolean>(
    ApiRouter(
        APIPath.togglePushNotification(),
        HTTPMethod.PUT,
        decodable = TogglePushNotiResponse(isNotify)
    )
)

suspend fun APIRequest.getHomeNotify() = requestResult<HomeNotifyResponse>(
    ApiRouter(
        APIPath.homeNotify()
    ), shouldCheckError = false
)

suspend fun APIRequest.getPoint(id: Int) = requestResult<PointResponse>(
    ApiRouter(
        APIPath.getPoint(id)
    ), shouldCheckError = true
)

typealias SVGData = String

suspend fun APIRequest.loadSVG(
    url: String, shouldCheckError: Boolean = false
) = requestResult<SVGData>(
    ApiRouter(
        path = url, headers = SVGFormatter
    ), shouldCheckError = shouldCheckError
)

suspend fun APIRequest.addEmailAddress(email: String) = requestResult<Boolean>(
    ApiRouter(
        APIPath.updateEmail(), HTTPMethod.PUT, parameters = hashMapOf("email" to email)
    ), shouldCheckError = true
)

suspend fun APIRequest.unRegisterToken() = requestResult<Boolean>(
    ApiRouter(
        APIPath.unregisterToken(),
        HTTPMethod.POST,
        parameters = hashMapOf("device_token" to (deviceToken() ?: "")),
        needLogin = false
    ), shouldCheckError = false
)
