import play.api.libs.json.JsValue.jsValueToJsLookup
import play.api.libs.json._
import scalaj.http._
import scala.language.postfixOps


class WikiParser {

  def pars_url(start_url: String, number_of_levels: Int = 1) = {

    val start_name = start_url.slice(start_url.lastIndexOf("/") + 1, start_url.length)

    main_pars(start_name, number_of_levels)
  }

  def main_pars(start_name: String, number_of_levels: Int = 1) = {

    val response_for_page_id: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="
      + name_normalizer(start_name) + "&prop=links&format=json").asString

    val json_id: JsValue = Json.parse(response_for_page_id.body)


    val test = (json_id \ "parse" \ "links").get
    println((json_id \ "parse" \ "links").get.getClass)

    //for (i <- 0 until (json_id \ "parse" \ "links")){

    //}

    //var next_level_page_name = (json_id \ "parse" \ "links" \ 5 \ "*").get.toString

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
