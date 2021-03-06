/**
 * Copyright (C) 2014 TU Berlin (peel@dima.tu-berlin.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.peelframework.core.results.model

import java.sql.Connection

import com.typesafe.config.ConfigFactory
import org.peelframework.core.results.DB
import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.PropertyChecks

/** Unit test for the [[org.peelframework.core.results.model.System System]] model class.
  *
  */
@RunWith(classOf[JUnitRunner])
class SystemTest extends FunSuite with BeforeAndAfter with PropertyChecks with Matchers {

  implicit var conn: Connection = _

  before {
    conn = DB.getConnection("test")(ConfigFactory.parseString( """ app.db.test.url = "jdbc:h2:mem:" """))
    DB.createSchema(silent = true)
  }

  after {
    DB.dropSchema(silent = true)
  }

  test("one system is persisted properly") {
    val x = System(Symbol("flink-0.9.0"), 'flink, Symbol("0.9.0"))
    System.insert(x)
    System.selectAll() shouldBe Seq(x)
  }

  test("many systems are persisted properly") {
    val xs = for (i <- 0 until 100) yield System(Symbol(s"flink%03${i}d"), 'flink, Symbol("0.9.0"))
    System.insert(xs)
    System.selectAll() shouldBe xs
  }

  test("one system is updated properly") {
    val x = System('sys001, 'flink, Symbol("0.9.0"))
    System.insert(x)
    val y = x.copy(name = 'spark, version = Symbol("0.9.1"))
    System.update(y)
    System.selectAll() shouldBe Seq(y)
  }

  test("many systems are updated properly") {
    val xs = for (i <- 0 until 100) yield System(Symbol(s"sys%03${i}d"), 'flink, Symbol("0.9.0"))
    System.insert(xs)
    val ys = for (x <- xs; version = Symbol("0.9.1")) yield x.copy(name = 'spark, version = version)
    System.update(ys)
    System.selectAll() shouldBe ys
  }

  test("one system is deleted properly") {
    val x = System('sys001, 'flink, Symbol("0.9.0"))
    System.insert(x)
    System.selectAll().size shouldBe 1
    System.delete(x)
    System.selectAll().size shouldBe 0
  }


  test("many systems are deleted properly") {
    val xs = for (i <- 0 until 100) yield System(Symbol(s"sys%03${i}d"), 'flink, Symbol("0.9.0"))
    System.insert(xs)
    System.selectAll().size shouldBe 100
    System.delete(xs)
    System.selectAll().size shouldBe 0
  }
}
