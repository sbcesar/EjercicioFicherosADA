package org.example

import java.io.File
import java.nio.file.Path


fun main() {

    val root = Path.of("src")
    val fileRoute = root.resolve("main").resolve("resources").resolve("cotizacion.csv")
    val newFileRoute = root.resolve("main").resolve("resources").resolve("resultado.csv")
    val file = File(fileRoute.toUri())
    val fileReader = LectorFicheros(fileRoute)
    val datesCollection = fileReader.transform(file)
    fileReader.createFile(newFileRoute, datesCollection)

}