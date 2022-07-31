import play.api.libs.json._
import scalaj.http._

import java.net.URLEncoder
import scala.language.postfixOps


class WikiParser {

  def pars_url(start_url: String, number_of_levels: Int = 1): Unit = {

    val start_name = start_url.slice(start_url.lastIndexOf("/") + 1, start_url.length)

    main_pars(start_name, number_of_levels)
  }

  def main_pars(start_name: String, number_of_levels: Int = 1) = {

    var redirecting_links: Seq[String] = Seq(name_normalizer(start_name))
    var previous_length_of_arr_with_links = 0

    for (level <- 1 to number_of_levels) {
      val start_time = System.currentTimeMillis()

      for (i <- previous_length_of_arr_with_links until redirecting_links.length) {
        previous_length_of_arr_with_links = redirecting_links.length
        val links = get_array_of_links(redirecting_links(i))

        for (unchecked_link <- links) {
          if ((unchecked_link("ns") == "0") && !redirecting_links.contains(name_normalizer(unchecked_link("link")))) {
            redirecting_links :+= name_normalizer(unchecked_link("link"))
          }
        }
      }
      println("Number of links on " + level + " level: " + (redirecting_links.length - previous_length_of_arr_with_links) +
      "\nTime spent on " + level + " level: " + (System.currentTimeMillis() - start_time) + "ms")
    }


  }

  def get_array_of_links(start_name: String): List[Map[String, String]] = {

    val response: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="
      + URLEncoder.encode(name_normalizer(start_name), "UTF-8") + "&prop=links&format=json").asString

    val list_of_ns = List(-1, -2, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 100, 101, 118, 119, 710, 711, 828, 829, 2300, 2301, 2302, 2303)

    var string_response = response.body.replace("*", "link")

    for (index <- list_of_ns){
      string_response = string_response.replace("\"ns\":" + index.toString + ",", "\"ns\":\"" + index.toString + "\",")
    }

    val modified_json_from_response: JsValue = Json.parse(string_response)

    (modified_json_from_response \ "parse" \ "links").get.validate[List[Map[String, String]]].get
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