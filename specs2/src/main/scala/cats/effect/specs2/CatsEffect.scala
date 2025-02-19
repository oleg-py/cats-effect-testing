/*
 * Copyright 2019 Daniel Spiewak
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

package cats.effect.specs2

import cats.effect.{ContextShift, IO, Timer}

import org.specs2.execute.{AsResult, Failure, Result}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait CatsEffect {

  private val executionContext: ExecutionContext = ExecutionContext.global

  implicit val ioContextShift: ContextShift[IO] = IO.contextShift(executionContext)
  implicit val ioTimer: Timer[IO] = IO.timer(executionContext)

  protected val Timeout = 10.seconds

  implicit def ioAsResult[R](implicit R: AsResult[R]): AsResult[IO[R]] = new AsResult[IO[R]] {
    def asResult(t: => IO[R]): Result =
      t.unsafeRunTimed(Timeout).map(R.asResult(_)).getOrElse(Failure(s"expectation timed out after $Timeout"))
  }
}
