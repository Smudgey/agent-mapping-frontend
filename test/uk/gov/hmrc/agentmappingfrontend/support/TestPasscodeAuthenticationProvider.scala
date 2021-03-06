/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agentmappingfrontend.support

import play.api.mvc.Request
import uk.gov.hmrc.passcode.authentication.{PasscodeAuthenticationProvider, PasscodeVerificationConfig}
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestPasscodeAuthenticationProvider(config: PasscodeVerificationConfig, whitelisted: Boolean)
  extends PasscodeAuthenticationProvider(config: PasscodeVerificationConfig) {

  override def hasValidBearerToken(regime: String)(implicit request: Request[_]): Future[Boolean] =
    Future successful whitelisted

  // same as super.verify() but without the addRedirectUrl(request) because that isn't needed for unit tests and would require a running Play application
  override def verify(regime: String, body: => Future[FailureResult])(implicit request: Request[_], user: AuthContext): Future[FailureResult] = {
    def failed = Future.successful(Redirect(config.loginUrl(request)))

    if (config.enabled) hasValidBearerToken(regime).flatMap(if (_) body else failed)
    else body
  }
}
