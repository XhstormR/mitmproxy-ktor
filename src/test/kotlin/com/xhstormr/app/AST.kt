package com.xhstormr.app

import com.google.javascript.jscomp.Compiler
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.JsAst
import com.google.javascript.jscomp.NodeTraversal
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.rhino.Node
import com.oracle.truffle.js.parser.GraalJSParserHelper
import com.oracle.truffle.js.runtime.JSParserOptions
import org.jsoup.Jsoup
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mozilla.javascript.Parser

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AST {

    private val HTML_CODE =
        """
            <!DOCTYPE html>
            <html dir="ltr" lang="zh"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
            <meta name="referrer" content="0xb9d8c"><meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=yes;">
            <meta name="cache-control" content="0xb9d8c">
            <title>0xb9d8c</title>
            </html>
        """.trimIndent()

    private val JS_CODE =
        """
            console.log('Hello World');console.log('Hello World');
            // Statements can be terminated by ;
            // Single-line comments start with two slashes.
            /* Multiline comments start with slash-star,
               and end with star-slash */
        """.trimIndent()

    @BeforeAll
    fun beforeAll() {
        println("BeforeAll")
    }

    @Test
    fun html_jsoup() {
        val document = Jsoup.parse(HTML_CODE)

        val elements = document.allElements

        elements.filter { it.ownText().contains("0xb9d8c") }.forEach {
            println("ownText: $it")
            println("it.tag(): " + it.tag())
            println("----")
        }

        elements.filter { it.attributes().any { it.value.contains("0xb9d8c") } }.forEach {
            println("attributes: $it")
            println("it.tag(): " + it.tag())
            println("----")
        }
    }

    @Test
    fun js_graalvm() {
        println(GraalJSParserHelper.parseToJSON(JS_CODE, "ast", false, JSParserOptions()))
    }

    @Test
    fun js_closureccompiler() {
        val compiler = Compiler().apply {
            initOptions(CompilerOptions())
        }
        val ast = JsAst(SourceFile.fromCode("123.js", JS_CODE))
        val node = ast.getAstRoot(compiler)
        println(node.toStringTree())
        NodeTraversal.traverse(compiler, node, AAA())
    }

    @Test
    fun js_rhino() {
        val parser = Parser()
        val astRoot = parser.parse(JS_CODE, null, 0)
        println(astRoot)
        println(astRoot.debugPrint())
    }
}

class AAA : NodeTraversal.AbstractPreOrderCallback() {
    override fun shouldTraverse(t: NodeTraversal, n: Node, parent: Node?): Boolean {
        if (n.isClass) {
            println("n.isClass")
            println(n.firstChild.string)
        }
        if (n.isMemberFunctionDef || n.isGetterDef || n.isSetterDef) {
            println("n.isMemberFunctionDef || n.isGetterDef || n.isSetterDef")
            println(n.string)
        }
        println(n.token)
        return true
    }
}
