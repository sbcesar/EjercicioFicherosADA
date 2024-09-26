package org.example

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class LectorFicheros(private val fileRoute: Path) {

    //Recibe el fichero cotizacion.csv y devuelve un diccionario con los datos por columnas
    fun transform(fichero: File): Map<String,List<String>> {

        //Comprobar si el fichero existe
        if (Files.notExists(fileRoute)) {
            Files.createDirectory(fileRoute.parent)
            Files.createFile(fileRoute)
        }

        //Leer el fichero
        val datesCollection = mutableMapOf<String,MutableList<String>>()
        var isFistLine = true
        var titles = listOf<String>()

        val br: BufferedReader = Files.newBufferedReader(fileRoute)
        br.use {
            it.forEachLine { line ->
                //Titulo de las columnas
                if (isFistLine) {
                    titles = line.split(";")
                    titles.forEach { title ->
                        datesCollection[title] = mutableListOf()
                    }
                    isFistLine = false
                }
                //Resto de la informacion
                else {
                    val dates = line.split(",")
                    for (i in dates.indices) {
                        datesCollection[titles[i]]?.add(dates[i])
                    }
                }


            }
        }
        return datesCollection
    }

    //Crea un fichero con el minimo, el maximo y la media de cada columna
    fun createFile(dates: Map<String,List<String>>) {

        //Crear archivo si no existe
        if (Files.notExists(fileRoute)) {
            Files.createDirectory(fileRoute.parent)
            Files.createFile(fileRoute)
        }

        //Escribir en el archivo
        val bw: BufferedWriter = Files.newBufferedWriter(fileRoute)
        bw.use { flux ->
            flux.write("Nombre;Minimo;Maximo;Media")
            flux.newLine()

            dates.forEach { (key, values) ->
                if (key != "Nombre") {
                    val numbersDates =
                        values.map { it.replace(",",".") }  //Los cambio por . porque kotlin no detecta valores decimales con ,
                            .map { it.toDouble() }                           //Lista de valores Double

                    if (numbersDates.isNotEmpty()) {
                        val min = numbersDates.minOrNull() ?: 0.0
                        val max = numbersDates.maxOrNull() ?: 0.0
                        val avg = numbersDates.average()

                        flux.write("$key;${"%.2f".format(min)};${"%.2f".format(max)};${"%.2f".format(avg)}")
                        flux.newLine()
                    }
                }
            }
        }
    }

}