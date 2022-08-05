import play.api.libs.json._
import scalaj.http._
import java.net.URLEncoder
import scala.language.postfixOps

class MultithreadParser {

  def get_array_of_links(start_name: String): Seq[LinkMap] = {

    val response: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="
      + URLEncoder.encode(name_normalizer(start_name), "UTF-8") + "&prop=links&format=json").asString

    val string_response = response.body.replace("\"*\":", "\"link\":")

    val modified_json_from_response: JsValue = Json.parse(string_response)

    (modified_json_from_response \ "parse" \ "links").get.validate[List[LinkMap]].get

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

case class LinkMap(ns: Int, link: String, exists: String)


object LinkMap {
  implicit val linkMapReads: Reads[LinkMap] = Json.reads[LinkMap]
  implicit val linkMapWrites: OWrites[LinkMap] = Json.writes[LinkMap]
  implicit val linkMapFormat: OFormat[LinkMap] = Json.format[LinkMap]
}

