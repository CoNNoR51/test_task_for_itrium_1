import play.api.libs.json._
import scalaj.http._

import java.net.URLEncoder
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

class MultithreadParser {

  def pars_url(start_url: String, number_of_levels: Int = 1): Unit = {

    val start_name = start_url.slice(start_url.lastIndexOf("/") + 1, start_url.length)

    main_pars(start_name, number_of_levels)
  }

  def main_pars(start_name: String, number_of_levels: Int = 1): Unit = {

    var redirecting_links: mutable.Seq[String] = mutable.Seq(name_normalizer(start_name))
    var counter = 1

    for (level <- 1 to number_of_levels) {
      val start_time = System.currentTimeMillis()
      val number_of_links_on_previous_level = counter
      counter = 0

      val value = multi_thread_check_level(redirecting_links, number_of_links_on_previous_level, counter)

      redirecting_links = value._1
      counter = value._2

      println("Number of links on " + level + " level: " + counter +
        "\nTime spent on " + level + " level: " + (System.currentTimeMillis() - start_time) + "ms")
    }
  }

  def multi_thread_check_level(redirecting_links: mutable.Seq[String], number_of_links_on_previous_level: Int, counter: Int): (mutable.Seq[String], Int) = {
    var redirecting_links_var = redirecting_links
    var counter_var = counter
    val step = 30
    var number_of_threads = step - 1

    for (index_of_link_from_previous_level <- (redirecting_links.length - number_of_links_on_previous_level) until redirecting_links.length by step) {
      var list_of_thread: mutable.Seq[Future[(mutable.Seq[String], Int)]] = mutable.Seq()

      if (index_of_link_from_previous_level + step > redirecting_links.length) {
        number_of_threads = redirecting_links.length - 1 - index_of_link_from_previous_level
      } else {
        number_of_threads = step - 1
      }
      println(number_of_threads)

      for (i <- 0 to number_of_threads) {
        list_of_thread :+= multi_thread_request(redirecting_links_var, index_of_link_from_previous_level + i)
      }

      val time = 10.seconds

      for (i <- 0 to number_of_threads) {
        val test = Await.result(list_of_thread(i), time)

        redirecting_links_var :++= test._1
        counter_var += test._2

      }
    }
    (redirecting_links_var.distinct, counter_var-(redirecting_links_var.length - redirecting_links_var.distinct.length))
  }

  def multi_thread_request(redirecting_links: mutable.Seq[String], index_of_link_from_previous_level: Int): Future[(mutable.Seq[String], Int)] = Future {

    val links = get_array_of_links(redirecting_links(index_of_link_from_previous_level))

    var link_buffer: mutable.Seq[String] = mutable.Seq()
    var counter_var = 0

    for (unchecked_link <- links) {
      if ((unchecked_link.ns == 0) && !redirecting_links.contains(name_normalizer(unchecked_link.link)) && !link_buffer.contains(name_normalizer(unchecked_link.link))) {
        link_buffer :+= name_normalizer(unchecked_link.link)
        counter_var += 1
      }
    }
    (link_buffer, counter_var)
  }

  def get_array_of_links(start_name: String): List[LinkMap] = {

    try {
      val response: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="
        + URLEncoder.encode(name_normalizer(start_name), "UTF-8") + "&prop=links&format=json").asString

      val string_response = response.body.replace("\"*\":", "\"link\":")

      val modified_json_from_response: JsValue = Json.parse(string_response)

      (modified_json_from_response \ "parse" \ "links").get.validate[List[LinkMap]].get
    }
    catch {
      case e: java.net.SocketTimeoutException => List(LinkMap(1, start_name, Option("")))
      case r: NoSuchElementException => List(LinkMap(1, start_name, Option("")))
    }
  }

  def name_normalizer(start_name: String): String = {

    if ((start_name(0) == '"') && (start_name(start_name.length - 1) == '"')) {
      start_name.slice(1, start_name.length - 1).replace(" ", "_")
    }
    else {
      start_name.replace(" ", "_")
    }
  }
}
