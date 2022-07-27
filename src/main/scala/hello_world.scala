import scala.io.StdIn

object hello_world {

  def main (args: Array[String]): Unit = {

    val name = StdIn.readLine("enter ur name: ")

    println(s"Hello, $name!")
  }

}
