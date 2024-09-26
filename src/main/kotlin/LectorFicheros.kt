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
                } else {
                    val dates = line.split(";")
                    for (i in dates.indices) {
                        datesCollection[titles[i]]?.add(dates[i])
                    }
                }
            }
        }
        return datesCollection
    }

    //Crea un fichero con el minimo, el maximo y la media de cada columna
    fun createFile(newFileRoute: Path, dates: Map<String,List<String>>) {


        //Crear archivo si no existe
        if (Files.notExists(newFileRoute)) {
            Files.createDirectories(newFileRoute.parent)
            Files.createFile(newFileRoute)
        }

        val dataList = mutableListOf<String>()

        dates.forEach { (nombre, numeros) ->
            if (nombre != "Nombre") {
                val numbersDates = numeros
                    .map { it.replace(",",".") }  //Los cambio por . porque kotlin no detecta valores decimales con ,
                    .filter { it.toDoubleOrNull() != null }        //Lo que no sea numero lo excluye
                    .map { it.toDouble() }                         //Lista de valores Double

                if (numbersDates.isNotEmpty()) {
                    val min = numbersDates.minOrNull() ?: 0.0
                    val max = numbersDates.maxOrNull() ?: 0.0
                    val avg = numbersDates.average()
                    dataList.add("$nombre;${"%.2f".format(min)};${"%.2f".format(max)};${"%.2f".format(avg)}")
                }
            }
        }

        //Escribir en el archivo
        val bw: BufferedWriter = Files.newBufferedWriter(newFileRoute)
        bw.use { bufferedWriter ->
            dataList.forEach { line ->
                bufferedWriter.write(line)
                bufferedWriter.newLine()
            }
        }
    }

}