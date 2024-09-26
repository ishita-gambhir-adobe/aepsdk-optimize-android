/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.optimize
import com.adobe.marketing.mobile.AdobeError

/**
 * AEPOptimizeError class is used to create AEPOptimizeError from error details received from Experience Edge.
 * @param type The type of error that occurred.
 * @param status The HTTP status code of the error.
 * @param title The title of the error.
 * @param detail The details of the error.
 * @param report The report of the error.
 * @param adobeError The corresponding AdobeError.
 */
public class AEPOptimizeError(val type: String? = "", val status: Int? = 0, val title: String? = "", val detail: String? = "", var report: Map<String, Any>?, var adobeError: AdobeError?) {

    val serverErrors = listOf(
        OptimizeConstants.HTTPResponseCodes.tooManyRequests,
        OptimizeConstants.HTTPResponseCodes.internalServerError,
        OptimizeConstants.HTTPResponseCodes.serviceUnavailable
    )

    val networkErrors = listOf(
        OptimizeConstants.HTTPResponseCodes.badGateway,
        OptimizeConstants.HTTPResponseCodes.gatewayTimeout
    )

    companion object {
        public fun getTimeoutError(): AEPOptimizeError {
            return AEPOptimizeError(
                null,
                OptimizeConstants.ErrorData.Timeout.STATUS,
                OptimizeConstants.ErrorData.Timeout.TITLE,
                OptimizeConstants.ErrorData.Timeout.DETAIL,
                null,
                AdobeError.CALLBACK_TIMEOUT
            )
        }

        public fun getUnexpectedError(): AEPOptimizeError {
            return AEPOptimizeError(
                null,
                null,
                OptimizeConstants.ErrorData.Unexpected.TITLE,
                OptimizeConstants.ErrorData.Unexpected.DETAIL,
                null,
                AdobeError.UNEXPECTED_ERROR
            )
        }
    }

    init {
        if (adobeError == null) {
            adobeError = when {
                status == OptimizeConstants.HTTPResponseCodes.clientTimeout -> AdobeError.CALLBACK_TIMEOUT
                serverErrors.contains(status) -> AdobeError.SERVER_ERROR
                networkErrors.contains(status) -> AdobeError.NETWORK_ERROR
                status in 400..499 -> AdobeError.INVALID_REQUEST
                else -> AdobeError.UNEXPECTED_ERROR
            }
        }
    }
}
