import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {

    val wiki = new WikiParser
    var start_number = -110

    println("Wiki parser menu:\n" +
      "1 - Search by name of page for one level only\n" +
      "2 - Search by url of page for one level only\n" +
      "3 - Search by name of page for the specified number of levels\n" +
      "4 - Search by url of page for the specified number of levels\n" +
      "5 - Help\n" +
      "Any other number - Exit\n")

    while (start_number == -110) {
      print("Enter a number: ")

      try {
        start_number = StdIn.readInt()
      } catch {
        case e: NumberFormatException => println("Wrong input! Input a number")
      }
    }

    start_number match {
      case 1 =>
        val start_name = StdIn.readLine("\nEnter name of page on wiki: ")

        wiki.main_pars(start_name)
        println("\nJob is done! Exiting program...")

      case 2 =>
        val start_url = StdIn.readLine("\nEnter url of page on wiki: ")

        wiki.pars_url(start_url)
        println("\nJob is done! Exiting program...")

      case 3 =>
        val start_name = StdIn.readLine("\nEnter name of page on wiki: ")
        var number_of_levels = -1

        while (number_of_levels <= 1) {
          print("Enter a number of levels (1 to 20): ")

          try {
            number_of_levels = StdIn.readInt()
          } catch {
            case e: NumberFormatException => println("Wrong input! Input a number (1 to 20)")
          }
        }

        wiki.main_pars(start_name, number_of_levels)
        println("\nJob is done! Exiting program...")

      case 4 =>
        val start_url = StdIn.readLine("\nEnter url of page on wiki: ")
        var number_of_levels = -1

        while (number_of_levels <= 1) {
          print("Enter a number of levels (1 to 20): ")

          try {
            number_of_levels = StdIn.readInt()
          } catch {
            case e: NumberFormatException => println("Wrong input! Input a number (1 to 20)")
          }
        }

        wiki.pars_url(start_url)
        println("\nJob is done! Exiting program...")

      case 5 =>
        println("\nThe program is designed to get the number of all links from a Wikipedia article leading to other articles\n" +
          "Example of name of page: pet door\n" +
          "Example of url of page: https://en.wikipedia.org/wiki/Pet_door")

      case 0 =>
        wiki.main_pars("pet door", 3)

      case _ => println("\nExiting from program...")
    }


  }

}
