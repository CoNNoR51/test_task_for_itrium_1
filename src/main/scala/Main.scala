

object Main {
  def main(args: Array[String]): Unit = {

    //val start_url = StdIn.readLine("enter a start url: ")

    var r = new WikiParser
    val start_name = "pet door"

    r.main_pars(start_name)

    //r.pars_url("https://en.wikipedia.org/wiki/Pet_door")

  }

}
