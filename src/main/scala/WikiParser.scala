import play.api.libs.json._
import scalaj.http._
//import spray.json._

class WikiParser {

  def pars_url(start_url: String, number_of_layers: Int) = {

  }

  def main_pars(start_name: String, number_of_layers: Int) = {

    val response_for_page_id: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=parse&page="+start_name+"&prop=images&format=json").asString

    val json_id: JsValue = Json.parse(response_for_page_id.body)

    var page_id = json_id.\("parse").\("pageid")

//    page_id = page_id.

    println(page_id)

    val response: HttpResponse[String] = Http("https://en.wikipedia.org/w/api.php?action=query&prop=links&titles="+start_name+"&pllimit=max&plnamespace=0&format=json").asString

    println(response.body.getClass)

//    var json = response.body.parseJson

//    json // fifth - number of name

    val json_play: JsValue = Json.parse(response.body)

    println("ff")

    println(json_play.\("query").\("pages").\(s"$page_id").\("links").\(0).\("title"))

    println(json_play.\("query").\("pages").\(s"$page_id").\("links").\(0).\("title").get.getClass)

    //println(json_play.\("query").\("pages")



  }

}
