package scanner

import Parser.parser
import Scanner.Scanner
import interpreter.interpreter
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess


private fun readLn() = readLine()!! // string line
private fun readInt() = readLn().toInt() // single int
private fun readLong() = readLn().toLong() // single long
private fun readDouble() = readLn().toDouble() // single double
private fun readStrings() = readLn().split(" ") // list of strings
private fun readInts() = readStrings().map { it.toInt() } // list of ints
private fun readLongs() = readStrings().map { it.toLong() } // list of longs
private fun readDoubles() = readStrings().map { it.toDouble() } // list of doubles



var hadError : Boolean = false


fun run(source: String) {
    val scanner = Scanner(source)
    scanner.scanTokens()
//    for (tokens in scanner.tokens){
//        println(tokens)
//    }
    val parserr = parser(scanner.tokens)
    val expression = parserr.parse()
    val inter = interpreter()
    if (expression != null) {
        inter.interpret(expression)
    }
    if (hadError) {
        return
    }
}
fun report(line : Int, where : String, message: String) {
    println("[line $line] error $where : $message")
    hadError = true
}
fun error(line : Int, message: String){
    report(line,"",message)
}

@Throws(IOException::class)
private fun runFile(path: String) {
    val bytes = Files.readAllBytes(Paths.get(path))
    run(String(bytes, Charset.defaultCharset()))
}


@Throws(IOException::class)
private fun runPrompt() {
    while (true) {
        print("> ")
        val line = readLine() ?: break
        run(line)
        hadError = false
    }
}



fun main(args : Array<String>){
    if (args.size > 1){
        println("Usage : Jaya [script]")
        exitProcess(64)
    }
    if (args.size == 1){
        runFile(args[0])
        if (hadError) exitProcess(65)
    }else{
        runPrompt()
    }
}








