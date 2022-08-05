import play.api.libs.json._
import scalaj.http._
import java.net.URLEncoder
import scala.collection.mutable
import scala.language.postfixOps


class WikiParser {

  def pars_url(start_url: String, number_of_levels: Int = 1): Unit = {

    val start_name = start_url.slice(start_url.lastIndexOf("/") + 1, start_url.length)

    main_pars(start_name, number_of_levels)
  }


  def main_pars(start_name: String, number_of_levels: Int = 1): Unit = {

    var redirecting_links: mutable.Seq[String] = mutable.Seq(name_normalizer(start_name))
    var counter = 0

    for (level <- 1 to number_of_levels) {
      val start_time = System.currentTimeMillis()
      val number_of_links_on_previous_level = counter
      counter = 0

      for (index_of_link_from_previous_level <- (redirecting_links.length - number_of_links_on_previous_level - 1) until redirecting_links.length) {
        val links = get_array_of_links(redirecting_links(index_of_link_from_previous_level))

        for (unchecked_link <- links) {
          if ((unchecked_link("ns") == "0") && !redirecting_links.contains(name_normalizer(unchecked_link("link")))) {
            redirecting_links :+= name_normalizer(unchecked_link("link"))
            counter += 1
          }
        }
      }
      println("Number of links on " + level + " level: " + counter +
        "\nTime spent on " + level + " level: " + (System.currentTimeMillis() - start_time) + "ms")
    }
  }

  def get_array_of_links(start_name: String): List[Map[String, String]] = {

    try {
      val response: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="
        + URLEncoder.encode(name_normalizer(start_name), "UTF-8") + "&prop=links&format=json").asString
      val list_of_ns = List(-1, -2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100, 101, 118, 119, 710, 711, 828, 829, 2300, 2301, 2302, 2303)

      var string_response = response.body.replace("\"*\"", "\"link\"")

      for (index <- list_of_ns) {
        string_response = string_response.replace("\"ns\":" + index.toString + ",", "\"ns\":\"" + index.toString + "\",")
      }

      val modified_json_from_response: JsValue = Json.parse(string_response)

      (modified_json_from_response \ "parse" \ "links").get.validate[List[Map[String, String]]].get

    } catch {
      case e: java.net.SocketTimeoutException => List(Map("ns" -> "0", "link" -> start_name))
      case r: NoSuchElementException => List(Map("ns" -> "0", "link" -> start_name))
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