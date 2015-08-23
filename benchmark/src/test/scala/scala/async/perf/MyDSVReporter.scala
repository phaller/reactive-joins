package scala.async.tests

import org.scalameter._
import java.io._
import java.util.Date
import java.util.TimeZone
import java.text.SimpleDateFormat
import utils.Tree

/** Produces a DSV file with results that can be used by other visualization tools.
 */
case class MyDsvReporter(delimiter: Char) extends Reporter[Double] {

  val sep = File.separator

  def report(result: CurveData[Double], persistor: Persistor) {
  }

  def report(result: Tree[CurveData[Double]], persistor: Persistor) = {
    val resultdir = currentContext(Key.reports.resultDir)

    new File(s"$resultdir").mkdir()

    def reportCurve(cd: CurveData[Double]) {
      // val filename = s"$resultdir$sep${cd.context.scope}.${cd.context.curve}.dsv"
      val filename = s"${cd.context.scope}.${cd.context.curve}.dsv"
      println(s"writing curve data to file '$filename'...")

      var writer: PrintWriter = null

      try {
        writer = new PrintWriter(new FileWriter(filename, false))
        MyDsvReporter.writeCurveData(cd, persistor, writer, delimiter)
      } finally {
        if (writer != null) writer.close()
      }
    }

    result foreach reportCurve

    true
  }

}

object MyDsvReporter {
  val dateISO: (Date => String) = {
    val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    df.setTimeZone(TimeZone.getTimeZone("UTC"))
    (date) => df.format(date)
  }

  def writeCurveData(cd: CurveData[Double], persistor: Persistor, pw: PrintWriter, delimiter: Char, newline: String = "\n") {
    val history = persistor.load(cd.context)
    import pw._
    import pw.{print => p}

    def header(cd: CurveData[Double]) {
      // p("date")
      // p(delimiter)
      for (paramname <- cd.measurements.head.params.axisData.keys) {
        p("param-" + paramname)
        p(delimiter)
      }
      p("value")
      p(delimiter)
      p("success")
      p(delimiter)
      p("cilo")
      p(delimiter)
      p("cihi")
      p(delimiter)
      p("units")
      // p(delimiter)
      // p("complete")
      print(newline)
    }

    def output(cd: CurveData[Double], date: Date) {
      for (m <- cd.measurements) {
        // p(dateISO(date))
        // p(delimiter)
        for (v <- m.params.axisData.values) {
          p(v)
          p(delimiter)
        }
        p(m.value)
        p(delimiter)
        p(m.success)
        p(delimiter)
        val ci = utils.Statistics.confidenceInterval(m.complete, cd.context.goe(Key.reports.regression.significance, 1e-10))
        p(f"${ci._1}%.3f")
        p(delimiter)
        p(f"${ci._2}%.3f")
        p(delimiter)
        p(m.units)
        // p(delimiter)
        // p("\"" + m.complete.mkString(" ") + "\"")
        print(newline)
      }
    }

    val currentDate = new Date
    val curves = history.curves :+ cd
    val dates = history.dates :+ currentDate
    
    header(cd)
    for ((c, d) <- curves zip dates) output(c.asInstanceOf[CurveData[Double]], d)
  }
}