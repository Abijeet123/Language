package tool

import java.io.IOException
import java.util.*
import java.io.PrintWriter


fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>")
        System.exit(64)
    }
    val outputDir = args[0]

    defineAst(outputDir, "Expr", Arrays.asList(
        "Binary   ! val left : Expr, val operator : Token, val right : Expr",
        "Grouping ! val expression : Expr",
        "Literal  ! val value : Any?",
        "Unary    ! val operator : Token, val right : Expr",
        "Variable ! val name : Token",
        "Assign ! val name : Token,val value : Expr"
    ));
    defineAst(outputDir, "stmt",Arrays.asList(
        "Block ! val stmt : MutableList<stmt?>",
        "Expression ! val expression : Expr",
        "Print ! val expression : Expr",
        "Var ! val name : Token, val initializer : Expr"
    ))
}

private fun defineVisitor(
    writer: PrintWriter, baseName: String, types: List<String>
) {
    writer.println("  interface Visitor<R> {")
    for (type: String in types) {
        val typeName = type.split("!".toRegex()).toTypedArray()[0].trim { it <= ' ' }
        writer.println(
//            "    fun visit$typeName$baseName(
//                     ${baseName.toLowerCase()} : $baseName) : R"
            "   fun visit$typeName$baseName(${baseName.toLowerCase()} : $typeName) : R?"
        )
    }
    writer.println("  }")
}

fun defineAst(outputDir: String, baseName: String, types : List<String>) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package Parser")
    writer.println()
    writer.println("import java.util.List")
    writer.println("import Scanner.*")
    writer.println()
    writer.println("abstract class $baseName {")

    defineVisitor(writer, baseName, types)

    for (type in types) {
        val className = type.split("!".toRegex()).toTypedArray()[0].trim { it <= ' ' }
        val fields = type.split("!".toRegex()).toTypedArray()[1].trim { it <= ' ' }
        defineType(writer, baseName, className, fields)
    }

    writer.println();
    writer.println("abstract  fun <R> accept(visitor: Visitor<R>) : R?");

    writer.println("}")
    writer.close()
}

private fun defineType(
    writer: PrintWriter, baseName: String,
    className: String, fieldList: String
) {
    writer.println(
        " class $className($fieldList) :${baseName}(){"
    )


    writer.println();
    writer.println("    override fun <R>accept(visitor : Visitor<R>):R? {");
    writer.println("      return visitor.visit$className$baseName(this);");
    writer.println("    }");

    writer.println("  }")
}




